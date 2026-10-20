package com.medtrixa.app.pulse

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

enum class PpgStatus { TOO_SHORT, NO_FINGER, SATURATED, LOW_QUALITY, OK }

data class PpgResult(
    val status: PpgStatus,
    val bpm: Int? = null,
    val quality: Float = 0f,
    val filtered: FloatArray = FloatArray(0),
    val peakIndices: List<Int> = emptyList(),
    val ibiMs: List<Double> = emptyList(),
    val sdnnMs: Double? = null,
    val rmssdMs: Double? = null
)

/**
 * Camera PPG processing. Input: mean red channel (0..255) per frame.
 * Output is a pulse-rate ESTIMATE only. Nothing here relates to blood pressure.
 */
object PpgProcessor {
    const val MIN_SECONDS = 8f
    const val MIN_BPM = 42
    const val MAX_BPM = 210
    const val QUALITY_THRESHOLD = 0.55f
    private const val FINGER_MIN_RED = 90f
    private const val SATURATED_RED = 250f

    fun process(red: FloatArray, fps: Float): PpgResult {
        if (fps <= 0f || red.isEmpty()) {
            return PpgResult(PpgStatus.TOO_SHORT)
        }

        val mean = red.average().toFloat()
        if (mean < FINGER_MIN_RED) return PpgResult(PpgStatus.NO_FINGER)
        if (mean > SATURATED_RED) return PpgResult(PpgStatus.SATURATED)

        // Show the waveform before the full measurement window is available.
        val enoughForTrace = red.size >= max(12, (2f * fps).toInt())
        val filtered = if (enoughForTrace) bandpass(red, fps) else FloatArray(0)

        if (red.size < (MIN_SECONDS * fps).toInt()) {
            return PpgResult(
                status = PpgStatus.TOO_SHORT,
                filtered = filtered
            )
        }

        val peaks = detectPeaks(filtered, fps)
        val allIbi = intervalsMs(peaks, fps)
        val ibi = cleanIntervals(allIbi)
        if (ibi.size < 5) {
            return PpgResult(PpgStatus.LOW_QUALITY, quality = 0f, filtered = filtered, peakIndices = peaks)
        }

        val medianIbi = median(ibi)
        val bpmPeaks = 60000.0 / medianIbi
        val (bpmSpec, concentration) = spectralRate(filtered, fps)

        val spec = min(1f, (concentration / 0.7).toFloat())
        val cv = stdDev(ibi) / ibi.average()
        val regularity = (1.0 - min(1.0, cv / 0.25)).toFloat()
        val agree = if (abs(bpmPeaks - bpmSpec) <= 6.0) 1f else 0.4f
        val quality = (0.45f * spec + 0.40f * regularity + 0.15f * agree).coerceIn(0f, 1f)

        val ok = quality >= QUALITY_THRESHOLD
        return PpgResult(
            status = if (ok) PpgStatus.OK else PpgStatus.LOW_QUALITY,
            bpm = if (ok) bpmPeaks.roundToInt() else null,
            quality = quality,
            filtered = filtered,
            peakIndices = peaks,
            ibiMs = ibi,
            sdnnMs = if (ok) stdDev(ibi) else null,
            rmssdMs = if (ok) rmssd(ibi) else null
        )
    }

    private fun movingAverage(x: FloatArray, window: Int): FloatArray {
        val n = x.size
        val prefix = DoubleArray(n + 1)
        for (i in 0 until n) prefix[i + 1] = prefix[i] + x[i]
        val half = max(1, window / 2)
        return FloatArray(n) { i ->
            val a = max(0, i - half)
            val b = min(n - 1, i + half)
            ((prefix[b + 1] - prefix[a]) / (b - a + 1)).toFloat()
        }
    }

    /** About 0.7-3.5 Hz. Output is inverted: red drops as blood volume rises. */
    private fun bandpass(x: FloatArray, fps: Float): FloatArray {
        val trend = movingAverage(x, (1.5f * fps).roundToInt())
        val detrended = FloatArray(x.size) { trend[it] - x[it] }
        return movingAverage(detrended, max(1, (0.1f * fps).roundToInt()))
    }

    private fun detectPeaks(f: FloatArray, fps: Float): List<Int> {
        val minDist = (fps * 60f / MAX_BPM).toInt()
        val threshold = 0.25f * stdDev(f.map { it.toDouble() }).toFloat()
        val peaks = ArrayList<Int>()
        for (i in 1 until f.size - 1) {
            if (f[i] > f[i - 1] && f[i] >= f[i + 1] && f[i] > threshold) {
                val last = peaks.lastOrNull()
                if (last == null || i - last >= minDist) peaks.add(i)
                else if (f[i] > f[last]) peaks[peaks.size - 1] = i
            }
        }
        return peaks
    }

    private fun intervalsMs(peaks: List<Int>, fps: Float): List<Double> =
        peaks.zipWithNext { a, b -> (b - a) * 1000.0 / fps }

    /** Drops physiologically impossible gaps and beats far from the median. */
    private fun cleanIntervals(ibi: List<Double>): List<Double> {
        val plausible = ibi.filter { it in (60000.0 / MAX_BPM)..(60000.0 / MIN_BPM) }
        if (plausible.isEmpty()) return plausible
        val med = median(plausible)
        return plausible.filter { abs(it - med) <= 0.30 * med }
    }

    /** Dominant frequency in the heart-rate band, plus how concentrated the power is there. */
    private fun spectralRate(f: FloatArray, fps: Float): Pair<Double, Double> {
        fun power(freq: Double): Double {
            var re = 0.0
            var im = 0.0
            val w = 2.0 * PI * freq / fps
            for (i in f.indices) {
                re += f[i] * cos(w * i)
                im += f[i] * sin(w * i)
            }
            return re * re + im * im
        }
        var bestF = 0.0
        var bestP = -1.0
        var f0 = MIN_BPM / 60.0
        while (f0 <= MAX_BPM / 60.0) {
            val p = power(f0)
            if (p > bestP) { bestP = p; bestF = f0 }
            f0 += 0.02
        }
        var total = 0.0
        var near = 0.0
        var fr = 0.3
        while (fr <= 6.0) {
            val p = power(fr)
            total += p
            if (abs(fr - bestF) <= 0.12 || abs(fr - 2 * bestF) <= 0.12) near += p
            fr += 0.02
        }
        return Pair(bestF * 60.0, if (total > 0) near / total else 0.0)
    }

    private fun median(v: List<Double>): Double {
        val s = v.sorted()
        return if (s.size % 2 == 1) s[s.size / 2] else (s[s.size / 2 - 1] + s[s.size / 2]) / 2.0
    }

    private fun stdDev(v: List<Double>): Double {
        if (v.size < 2) return 0.0
        val m = v.average()
        return sqrt(v.sumOf { (it - m) * (it - m) } / (v.size - 1))
    }

    private fun stdDev(f: FloatArray): Double = stdDev(f.map { it.toDouble() })

    private fun rmssd(ibi: List<Double>): Double {
        if (ibi.size < 2) return 0.0
        val d = ibi.zipWithNext { a, b -> (b - a) * (b - a) }
        return sqrt(d.average())
    }
}
