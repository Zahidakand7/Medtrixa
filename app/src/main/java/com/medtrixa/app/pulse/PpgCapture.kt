package com.medtrixa.app.pulse

import android.content.Context
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.Executors
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
    private var provider: ProcessCameraProvider? = null
    private var camera: Camera? = null

    fun start(owner: LifecycleOwner) {
        val future = ProcessCameraProvider.getInstance(context)
        future.addListener({
            val p = future.get()
            provider = p
            val analysis = ImageAnalysis.Builder()
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
            analysis.setAnalyzer(executor) { analyze(it) }
            p.unbindAll()
            camera = p.bindToLifecycle(owner, CameraSelector.DEFAULT_BACK_CAMERA, analysis)
            camera?.cameraControl?.enableTorch(true)
        }, ContextCompat.getMainExecutor(context))
    }

    fun stop() {
        camera?.cameraControl?.enableTorch(false)
        provider?.unbindAll()
        camera = null
        synchronized(lock) { reds.clear(); times.clear(); lastProcessNs = 0L }
        _progress.value = 0f
        _result.value = PpgResult(PpgStatus.TOO_SHORT)
    }

    private fun analyze(image: ImageProxy) {
        try {
            val plane = image.planes[0]
            val buf = plane.buffer
            val rowStride = plane.rowStride
            val pixelStride = plane.pixelStride
            val w = image.width
            val h = image.height
            var sum = 0L
            var count = 0
            var y = (h * 0.3f).toInt()
            while (y < (h * 0.7f).toInt()) {
                var x = (w * 0.3f).toInt()
                while (x < (w * 0.7f).toInt()) {
                    sum += buf.get(y * rowStride + x * pixelStride).toInt() and 0xFF
                    count++
                    x += 4
                }
                y += 4
            }
            if (count == 0) return
            val mean = sum.toFloat() / count
            val t = image.imageInfo.timestamp

            if (mean < 90f || mean > 250f) {
                synchronized(lock) { reds.clear(); times.clear() }
                _progress.value = 0f
                _result.value = PpgResult(
                    if (mean < 90f) PpgStatus.NO_FINGER else PpgStatus.SATURATED
                )
                return
            }

            synchronized(lock) {
                reds.add(mean)
                times.add(t)
                while (times.size > 1 && t - times.first() > 12_000_000_000L) {
                    reds.removeAt(0)
                    times.removeAt(0)
                }
                val spanNs = t - times.first()
                _progress.value = minOf(1f, spanNs / (PpgProcessor.MIN_SECONDS * 1e9f))
                if (spanNs >= (PpgProcessor.MIN_SECONDS * 1e9).toLong() &&
                    t - lastProcessNs >= 1_000_000_000L
                ) {
                    lastProcessNs = t
                    val fps = (times.size - 1) * 1e9f / spanNs
                    _result.value = PpgProcessor.process(reds.toFloatArray(), fps)
                }
            }
        } finally {
            image.close()
        }
    }
}
