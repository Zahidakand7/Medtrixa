package com.medtrixa.app.pulse

import android.content.Context
import android.os.SystemClock
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PpgCapture(private val context: Context) {
    private val _result = MutableStateFlow(PpgResult(PpgStatus.TOO_SHORT))
    val result: StateFlow<PpgResult> = _result

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress

    private val lock = Any()
    private val reds = ArrayList<Float>()
    private val times = ArrayList<Long>()

    private var lastProcessNs = 0L
    private val executor = Executors.newSingleThreadExecutor()
    private val watchdog: ScheduledExecutorService =
        Executors.newSingleThreadScheduledExecutor()
    private var watchdogTask: ScheduledFuture<*>? = null

    private var provider: ProcessCameraProvider? = null
    private var camera: Camera? = null
    private var lifecycleOwner: LifecycleOwner? = null

    // Rear lenses that have a flash unit. Phones with several rear cameras may
    // expose more than one; the user can cycle through them with nextLens().
    private val _lensCount = MutableStateFlow(1)
    val lensCount: StateFlow<Int> = _lensCount
    private val _lensIndex = MutableStateFlow(0)
    val lensIndex: StateFlow<Int> = _lensIndex

    // Frame counters used by the finger gate (touched only on the analyzer thread)
    private var missFrames = 0

    @Volatile private var active = false
    @Volatile private var lastFrameElapsedMs = 0L
    private val generation = AtomicInteger(0)

    fun start(owner: LifecycleOwner) {
        if (active) return

        active = true
        lifecycleOwner = owner
        missFrames = 0
        val token = generation.incrementAndGet()
        lastFrameElapsedMs = SystemClock.elapsedRealtime()
        clearSamples(PpgStatus.TOO_SHORT)

        watchdogTask?.cancel(false)
        watchdogTask = watchdog.scheduleAtFixedRate({
            if (active && generation.get() == token) {
                val now = SystemClock.elapsedRealtime()
                if (now - lastFrameElapsedMs > 1800L) {
                    lastFrameElapsedMs = now
                    clearSamples(PpgStatus.TOO_SHORT)
                }
            }
        }, 500L, 500L, TimeUnit.MILLISECONDS)

        val future = ProcessCameraProvider.getInstance(context)
        future.addListener({
            if (!active || generation.get() != token) return@addListener

            try {
                val p = future.get()
                if (!active || generation.get() != token) return@addListener

                provider = p
                val analysis = ImageAnalysis.Builder()
                    .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                analysis.setAnalyzer(executor) { image ->
                    analyze(image, token)
                }

                if (!active || generation.get() != token) {
                    analysis.clearAnalyzer()
                    return@addListener
                }

                val flashLenses = CameraSelector.DEFAULT_BACK_CAMERA
                    .filter(p.availableCameraInfos)
                    .filter { it.hasFlashUnit() }
                val count = maxOf(1, flashLenses.size)
                _lensCount.value = count
                val idx = _lensIndex.value % count
                _lensIndex.value = idx

                val selector = if (flashLenses.size > 1) {
                    CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .addCameraFilter { infos ->
                            val withFlash = infos.filter { it.hasFlashUnit() }
                            if (withFlash.isEmpty()) infos
                            else listOf(withFlash[idx % withFlash.size])
                        }
                        .build()
                } else {
                    CameraSelector.DEFAULT_BACK_CAMERA
                }

                p.unbindAll()
                camera = p.bindToLifecycle(
                    owner,
                    selector,
                    analysis
                )

                if (active && generation.get() == token) {
                    camera?.cameraControl?.enableTorch(true)
                } else {
                    camera?.cameraControl?.enableTorch(false)
                    p.unbindAll()
                    camera = null
                }
            } catch (_: Exception) {
                if (active && generation.get() == token) {
                    clearSamples(PpgStatus.TOO_SHORT)
                }
            }
        }, ContextCompat.getMainExecutor(context))
    }

    /** Switch to the next rear lens that has a flash, then restart capture. */
    fun nextLens() {
        val owner = lifecycleOwner ?: return
        if (!active) return
        stop()
        _lensIndex.value = _lensIndex.value + 1
        start(owner)
    }

    fun stop() {
        active = false
        generation.incrementAndGet()

        watchdogTask?.cancel(false)
        watchdogTask = null

        try {
            camera?.cameraControl?.enableTorch(false)
            provider?.unbindAll()
        } catch (_: Exception) {
            // Camera may already have been released by the lifecycle.
        }

        camera = null
        provider = null
        lastFrameElapsedMs = 0L
        clearSamples(PpgStatus.TOO_SHORT)
    }

    private fun clearSamples(status: PpgStatus) {
        synchronized(lock) {
            reds.clear()
            times.clear()
            lastProcessNs = 0L
        }
        _progress.value = 0f
        _result.value = PpgResult(status)
    }

    private fun analyze(image: ImageProxy, token: Int) {
        try {
            if (!active || generation.get() != token) return

            val plane = image.planes[0]
            val buf = plane.buffer
            val rowStride = plane.rowStride
            val pixelStride = plane.pixelStride
            val w = image.width
            val h = image.height

            var sumR = 0L
            var sumG = 0L
            var sumB = 0L
            var count = 0
            var y = (h * 0.3f).toInt()
            while (y < (h * 0.7f).toInt()) {
                var x = (w * 0.3f).toInt()
                while (x < (w * 0.7f).toInt()) {
                    val offset = y * rowStride + x * pixelStride
                    sumR += buf.get(offset).toInt() and 0xFF
                    sumG += buf.get(offset + 1).toInt() and 0xFF
                    sumB += buf.get(offset + 2).toInt() and 0xFF
                    count++
                    x += 4
                }
                y += 4
            }
            if (count == 0) return

            val meanR = sumR.toFloat() / count
            val meanG = sumG.toFloat() / count
            val meanB = sumB.toFloat() / count
            val timestampNs = image.imageInfo.timestamp
            lastFrameElapsedMs = SystemClock.elapsedRealtime()

            // A fingertip lit by the torch is strongly red: red far above green and
            // blue. Loose ratios let a warm room, a palm or a pale surface pass as
            // a finger, so the trace appeared with nothing on the lens.
            val redOverGreen = meanR / (meanG + 1f)
            val redOverBlue = meanR / (meanB + 1f)
            val fingerLike =
                meanR >= 100f &&
                redOverGreen >= 1.8f &&
                redOverBlue >= 2.2f

            if (!fingerLike) {
                // Tolerate a few bad frames, then clear once (not on every frame)
                missFrames++
                if (missFrames >= 6 &&
                    active && generation.get() == token &&
                    _result.value.status != PpgStatus.NO_FINGER
                ) {
                    clearSamples(PpgStatus.NO_FINGER)
                }
                return
            }
            missFrames = 0

            if (meanR >= 250f) {
                if (active && generation.get() == token &&
                    _result.value.status != PpgStatus.SATURATED
                ) {
                    clearSamples(PpgStatus.SATURATED)
                }
                return
            }

            synchronized(lock) {
                if (!active || generation.get() != token) return

                reds.add(meanR)
                times.add(timestampNs)

                while (
                    times.size > 1 &&
                    timestampNs - times.first() > 12_000_000_000L
                ) {
                    reds.removeAt(0)
                    times.removeAt(0)
                }

                if (times.size < 2) return

                val spanNs = timestampNs - times.first()
                if (spanNs <= 0L) return

                _progress.value = minOf(
                    1f,
                    spanNs / (PpgProcessor.MIN_SECONDS * 1e9f)
                )

                // Refresh waveform approximately twice per second.
                if (timestampNs - lastProcessNs >= 500_000_000L) {
                    lastProcessNs = timestampNs
                    val fps = (times.size - 1) * 1e9f / spanNs
                    _result.value = PpgProcessor.process(
                        reds.toFloatArray(),
                        fps
                    )
                }
            }
        } finally {
            image.close()
        }
    }
}
