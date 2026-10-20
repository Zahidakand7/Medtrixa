package com.medtrixa.app.pulse

import androidx.camera.core.ImageProxy
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

enum class FingerState { NO_FINGER, PRESENT }

enum class SignalQuality { NONE, POOR, FAIR, GOOD }

data class PulseUiState(
    val finger: FingerState = FingerState.NO_FINGER,
    val quality: SignalQuality = SignalQuality.NONE,
    val progress: Float = 0f,
    val bpm: Int? = null,
    val finished: Boolean = false,
    val timedOut: Boolean = false,
    val elapsedSec: Int = 0,
    val message: String = "Place a fingertip over the camera lens and the flash.",
    val waveform: List<Float> = emptyList()
)

/**
 * Camera PPG engine (research prototype, not a medical device).
 * Feed it the mean red, green and blue of each camera frame (torch on).
 * - No finger: the buffer is cleared and no waveform is returned.
 * - A heart rate is returned only after the signal has been good and stable.
 * - After 60 seconds without a reliable reading it reports a time-out.
 */
class PulseEngine {

    private companion object {
        const val FS = 30.0
        const val TIMEOUT_MS = 60_000L
        const val GOOD_PURITY = 0.50
        const val FAIR_PURITY = 0.30
        const val NEEDED_GOOD = 8
        const val MIN_STABLE_SECONDS = 15.0
    }

    private val times = ArrayList<Long>()
    private val vals = ArrayList<Double>()

    private var startMs = -1L
    private var presentSince = -1L
    private var absentSince = -1L
    private var noFingerSince = -1L
    private var poorSince = -1L
    private var fingerPresent = false
    private var lastEval = 0L
    private var lastWave = 0L
    private var wave: List<Float> = emptyList()
    private val good = ArrayList<Double>()
    private var poorRun = 0
    private var quality = SignalQuality.NONE
    private var bpm: Int? = null
    private var finished = false
    private var timedOut = false

    fun reset() {
        times.clear(); vals.clear(); good.clear()
        startMs = -1L; presentSince = -1L; absentSince = -1L; noFingerSince = -1L; poorSince = -1L
        fingerPresent = false; lastEval = 0L; lastWave = 0L; wave = emptyList()
        poorRun = 0; quality = SignalQuality.NONE; bpm = null; finished = false; timedOut = false
    }

    fun addFrame(tMs: Long, r: Double, g: Double, b: Double): PulseUiState {
        if (startMs < 0) {
            startMs = tMs
            noFingerSince = tMs
        }
        if (!finished && !timedOut) {
            if (tMs - startMs >= TIMEOUT_MS) timedOut = true
            else process(tMs, r, g, b)
        }
        return snapshot(tMs)
    }

    private fun fingerOk(r: Double, g: Double, b: Double): Boolean =
        r >= 100.0 && r / (g + 1.0) >= 1.8 && r / (b + 1.0) >= 2.2

    private fun process(t: Long, r: Double, g: Double, b: Double) {
        if (fingerOk(r, g, b)) {
            absentSince = -1L
            if (presentSince < 0) presentSince = t
            if (t - presentSince >= 1000L) {
                if (!fingerPresent) fingerPresent = true
            }
            times.add(t)
            vals.add(-r) // blood volume up -> less red light -> signal up
            while (times.isNotEmpty() && t - times[0] > 20_000L) {
                times.removeAt(0)
                vals.removeAt(0)
            }
        } else {
            presentSince = -1L
            if (absentSince < 0) absentSince = t
            if (t - absentSince >= 500L) {
                if (fingerPresent || times.isNotEmpty()) noFingerSince = t
                fingerPresent = false
                times.clear(); vals.clear(); good.clear()
                poorRun = 0; poorSince = -1L
                quality = SignalQuality.NONE
                wave = emptyList()
            }
        }
        if (!fingerPresent || times.size < 2) return

        val dur = (times[times.size - 1] - times[0]) / 1000.0

        if (t - lastWave >= 100L && dur >= 3.0) {
            lastWave = t
            wave = buildWave()
        }

        if (dur >= 8.0 && t - lastEval >= 1000L) {
            lastEval = t
            val win = min(dur, 12.0)
            val tEnd = times[times.size - 1].toDouble()
            val x = resample(tEnd - win * 1000.0, tEnd)
            val y = bandpass(x, true)
            if (y.size < 60) return
            val (hs, purity) = spectrum(y)
            val (hp, _) = peaksHr(y, hs)
            val agree = hp != null && abs(hp - hs) <= 4.0
            quality = when {
                agree && purity >= GOOD_PURITY -> SignalQuality.GOOD
                purity >= FAIR_PURITY -> SignalQuality.FAIR
                else -> SignalQuality.POOR
            }
            if (quality == SignalQuality.GOOD && hp != null) {
                good.add((hp + hs) / 2.0)
                while (good.size > NEEDED_GOOD) good.removeAt(0)
                poorRun = 0
                poorSince = -1L
            } else {
                poorRun++
                if (quality == SignalQuality.POOR) {
                    if (poorSince < 0) poorSince = t
                } else poorSince = -1L
                if (poorRun >= 3) good.clear()
            }
            if (good.size >= NEEDED_GOOD && ((good.maxOrNull() ?: 0.0) - (good.minOrNull() ?: 0.0)) <= 4.0 && dur >= MIN_STABLE_SECONDS) {
                bpm = Math.round(median(good)).toInt()
                finished = true
            }
        }
    }

    private fun snapshot(t: Long): PulseUiState {
        val elapsed = if (startMs >= 0) ((t - startMs) / 1000L).toInt() else 0
        val message = when {
            finished -> "Done. Estimated heart rate shown."
            timedOut -> "Couldn't get a reliable reading. Check finger placement, keep still, and try again."
            !fingerPresent ->
                if (noFingerSince >= 0 && t - noFingerSince >= 8_000L)
                    "Move your finger until \"Finger detected\" appears. With several cameras, try the second lens, or put one finger on the lens and another on the flash."
                else "Place a fingertip over the camera lens and the flash."
            quality == SignalQuality.NONE -> "Finger detected. Hold still..."
            quality == SignalQuality.GOOD -> "Good signal. Hold steady..."
            quality == SignalQuality.FAIR -> "Almost there. Keep still."
            poorSince >= 0 && t - poorSince >= 10_000L ->
                "Still weak. Shift your finger slightly, or use one finger on the lens and another on the flash."
            else -> "Weak signal. Press lightly and keep still."
        }
        val progress = if (finished) 1f else min(1f, good.size.toFloat() / NEEDED_GOOD)
        return PulseUiState(
            finger = if (fingerPresent) FingerState.PRESENT else FingerState.NO_FINGER,
            quality = if (fingerPresent) quality else SignalQuality.NONE,
            progress = progress,
            bpm = bpm,
            finished = finished,
            timedOut = timedOut,
            elapsedSec = elapsed,
            message = message,
            waveform = if (fingerPresent) wave else emptyList()
        )
    }

    // ---------- signal processing ----------

    private fun resample(t0: Double, t1: Double): DoubleArray {
        val n = ((t1 - t0) / 1000.0 * FS).toInt()
        val out = DoubleArray(max(n, 0))
        var j = 0
        for (i in 0 until n) {
            val t = t0 + i * 1000.0 / FS
            while (j + 1 < times.size && times[j + 1] < t) j++
            if (j + 1 >= times.size) {
                out[i] = vals[vals.size - 1]
                continue
            }
            val ta = times[j].toDouble()
            val tb = times[j + 1].toDouble()
            val va = vals[j]
            val vb = vals[j + 1]
            out[i] = if (tb == ta) va else {
                var w = (t - ta) / (tb - ta)
                if (w < 0.0) w = 0.0
                if (w > 1.0) w = 1.0
                va + (vb - va) * w
            }
        }
        return out
    }

    private fun movAvg(x: DoubleArray, k: Int): DoubleArray {
        val n = x.size
        val half = k / 2
        val pre = DoubleArray(n + 1)
        for (i in 0 until n) pre[i + 1] = pre[i] + x[i]
        val out = DoubleArray(n)
        for (i in 0 until n) {
            val a = max(0, i - half)
            val b = min(n, i + half + 1)
            out[i] = (pre[b] - pre[a]) / (b - a)
        }
        return out
    }

    private fun bandpass(x: DoubleArray, trim: Boolean): DoubleArray {
        if (x.isEmpty()) return x
        val s = movAvg(x, 3)
        val l = movAvg(x, 45)
        val y = DoubleArray(x.size) { s[it] - l[it] }
        val cut = FS.toInt()
        return if (trim && y.size > 2 * cut + 10) y.copyOfRange(cut, y.size - cut) else y
    }

    private fun stdev(x: DoubleArray): Double {
        if (x.isEmpty()) return 0.0
        val m = x.average()
        var s = 0.0
        for (v in x) s += (v - m) * (v - m)
        return sqrt(s / x.size)
    }

    private fun median(x: List<Double>): Double {
        val s = x.sorted()
        val n = s.size
        return if (n % 2 == 1) s[n / 2] else (s[n / 2 - 1] + s[n / 2]) / 2.0
    }

    private fun peaksHr(y: DoubleArray, hs: Double): Pair<Double?, Double> {
        val sd = stdev(y)
        if (sd == 0.0) return Pair(null, 0.0)
        val cand = ArrayList<Int>()
        for (i in 2 until y.size - 2) {
            var mx = y[i - 2]
            for (k in i - 1..i + 2) if (y[k] > mx) mx = y[k]
            if (y[i] > 0.25 * sd && y[i] >= mx) cand.add(i)
        }
        val kept = ArrayList<Int>()
        val minD = (max(0.3, 0.6 * 60.0 / hs) * FS).toInt()
        for (i in cand) {
            if (kept.isNotEmpty() && i - kept[kept.size - 1] < minD) {
                if (y[i] > y[kept[kept.size - 1]]) kept[kept.size - 1] = i
            } else kept.add(i)
        }
        val ibis = ArrayList<Double>()
        for (k in 0 until kept.size - 1) {
            val v = (kept[k + 1] - kept[k]) / FS
            if (v >= 0.33 && v <= 1.5) ibis.add(v)
        }
        if (ibis.size < 4) return Pair(null, 0.0)
        val med = median(ibis)
        val mean = ibis.average()
        var s = 0.0
        for (v in ibis) s += (v - mean) * (v - mean)
        val cv = sqrt(s / ibis.size) / mean
        return Pair(60.0 / med, cv)
    }

    private fun spectrum(y: DoubleArray): Pair<Double, Double> {
        val n = y.size
        val m = y.average()
        val w = DoubleArray(n) { 0.5 - 0.5 * cos(2.0 * PI * it / (n - 1)) }
        val yz = DoubleArray(n) { (y[it] - m) * w[it] }
        val bins = ((4.0 - 0.5) / 0.02).toInt() + 1
        val freqs = DoubleArray(bins) { 0.5 + 0.02 * it }
        val p = DoubleArray(bins)
        for (k in 0 until bins) {
            val ang = 2.0 * PI * freqs[k] / FS
            var re = 0.0
            var im = 0.0
            for (i in 0 until n) {
                re += yz[i] * cos(ang * i)
                im -= yz[i] * sin(ang * i)
            }
            p[k] = re * re + im * im
        }
        var total = 0.0
        for (v in p) total += v
        var best = -1
        for (k in 0 until bins) {
            if (freqs[k] >= 0.7 && freqs[k] <= 3.5 && (best < 0 || p[k] > p[best])) best = k
        }
        val f0 = freqs[best]
        var pur = 0.0
        for (k in 0 until bins) {
            if (abs(freqs[k] - f0) <= 0.12) pur += p[k]
            if (abs(freqs[k] - 2.0 * f0) <= 0.15) pur += p[k]
        }
        return Pair(60.0 * f0, if (total > 0.0) pur / total else 0.0)
    }

    private fun buildWave(): List<Float> {
        val tEnd = times[times.size - 1].toDouble()
        val win = min((tEnd - times[0]) / 1000.0, 6.0)
        val x = resample(tEnd - win * 1000.0, tEnd)
        if (x.size < 30) return emptyList()
        val y = bandpass(x, false)
        val sd = stdev(y)
        if (sd == 0.0) return emptyList()
        val step = 2
        val out = ArrayList<Float>()
        var i = 0
        while (i < y.size) {
            val v = (y[i] / (3.0 * sd)).coerceIn(-1.0, 1.0)
            out.add(v.toFloat())
            i += step
        }
        return out
    }
}

object PulseFrameStats {
    /** Mean R, G, B (0..255) of the central half of a YUV_420_888 frame. */
    fun meanRgb(image: ImageProxy): DoubleArray {
        val w = image.width
        val h = image.height
        val yP = image.planes[0]
        val uP = image.planes[1]
        val vP = image.planes[2]
        val yB = yP.buffer
        val uB = uP.buffer
        val vB = vP.buffer
        var sr = 0.0
        var sg = 0.0
        var sb = 0.0
        var n = 0
        var y = h / 4
        while (y < h * 3 / 4) {
            var x = w / 4
            while (x < w * 3 / 4) {
                val yi = y * yP.rowStride + x * yP.pixelStride
                val ci = (y / 2) * uP.rowStride + (x / 2) * uP.pixelStride
                val cj = (y / 2) * vP.rowStride + (x / 2) * vP.pixelStride
                if (yi < yB.limit() && ci < uB.limit() && cj < vB.limit()) {
                    val yv = (yB.get(yi).toInt() and 0xFF).toDouble()
                    val u = (uB.get(ci).toInt() and 0xFF) - 128.0
                    val v = (vB.get(cj).toInt() and 0xFF) - 128.0
                    sr += (yv + 1.402 * v).coerceIn(0.0, 255.0)
                    sg += (yv - 0.344136 * u - 0.714136 * v).coerceIn(0.0, 255.0)
                    sb += (yv + 1.772 * u).coerceIn(0.0, 255.0)
                    n++
                }
                x += 4
            }
            y += 4
        }
        if (n == 0) return doubleArrayOf(0.0, 0.0, 0.0)
        return doubleArrayOf(sr / n, sg / n, sb / n)
    }
}
