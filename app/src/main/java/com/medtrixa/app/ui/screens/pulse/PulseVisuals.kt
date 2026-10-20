package com.medtrixa.app.ui.screens.pulse

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.medtrixa.app.pulse.PpgStatus

private val WaveBg = Color(0xFF0F172A)
private val WaveGrid = Color(0xFF334155)
private val WaveLow = Color(0xFF0284C7)
private val WaveHigh = Color(0xFF38BDF8)
private val WaveIdleText = Color(0xFF94A3B8)

/**
 * Pulse trace on a dark slate panel: faint grid, layered glow, fading tail, peak markers.
 * When [signal] is empty (no finger / no contact) only the grid and [idleMessage] are shown.
 */
@Composable
fun PulseWaveform(
    signal: FloatArray,
    peaks: List<Int>,
    modifier: Modifier = Modifier,
    idleMessage: String? = null
) {
    Box(
        modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(WaveBg),
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            val gridColor = WaveGrid.copy(alpha = 0.55f)
            for (i in 1..3) {
                val gy = h * i / 4f
                drawLine(gridColor, Offset(0f, gy), Offset(w, gy), strokeWidth = 1f)
            }
            for (i in 1..7) {
                val gx = w * i / 8f
                drawLine(gridColor, Offset(gx, 0f), Offset(gx, h), strokeWidth = 1f)
            }
            if (signal.size < 2) return@Canvas

            var lo = Float.MAX_VALUE
            var hi = -Float.MAX_VALUE
            for (v in signal) { lo = minOf(lo, v); hi = maxOf(hi, v) }
            val range = (hi - lo).coerceAtLeast(1e-3f)
            val padY = h * 0.14f
            fun px(i: Int) = w * i / (signal.size - 1)
            fun py(v: Float) = h - padY - (v - lo) / range * (h - 2 * padY)

            val path = Path()
            path.moveTo(px(0), py(signal[0]))
            for (i in 1 until signal.size) {
                val x0 = px(i - 1); val y0 = py(signal[i - 1])
                val x1 = px(i); val y1 = py(signal[i])
                val cx = (x0 + x1) / 2f
                path.cubicTo(cx, y0, cx, y1, x1, y1)
            }

            val line = Brush.horizontalGradient(
                0f to WaveLow.copy(alpha = 0.20f),
                0.55f to WaveLow,
                1f to WaveHigh
            )
            // Glow underlay: wide, faint strokes beneath the core line
            drawPath(path, line, alpha = 0.07f,
                style = Stroke(16.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
            drawPath(path, line, alpha = 0.14f,
                style = Stroke(9.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
            drawPath(path, line,
                style = Stroke(2.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))

            for (p in peaks) {
                if (p !in signal.indices) continue
                val c = Offset(px(p), py(signal[p]))
                drawCircle(WaveHigh.copy(alpha = 0.20f), 8.dp.toPx(), c)
                drawCircle(WaveHigh, 3.dp.toPx(), c)
            }
        }
        if (idleMessage != null && signal.size < 2) {
            Text(
                idleMessage,
                modifier = Modifier.padding(horizontal = 24.dp),
                style = MaterialTheme.typography.bodySmall,
                color = WaveIdleText,
                textAlign = TextAlign.Center
            )
        }
    }
}

/** Circular signal-quality gauge, 0-100. Green at or above the reporting threshold. */
@Composable
fun SignalQualityRing(quality: Float, modifier: Modifier = Modifier) {
    val q = quality.coerceIn(0f, 1f)
    val color = when {
        q >= 0.55f -> Color(0xFF2EB67D)
        q >= 0.35f -> Color(0xFFE8A33D)
        else -> Color(0xFFD9534F)
    }
    val track = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
    Box(modifier.size(72.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val sw = 6.dp.toPx()
            val topLeft = Offset(sw / 2, sw / 2)
            val arc = Size(size.width - sw, size.height - sw)
            drawArc(track, 0f, 360f, false, topLeft, arc, style = Stroke(sw))
            drawArc(
                color, -90f, 360f * q, false, topLeft, arc,
                style = Stroke(sw, cap = StrokeCap.Round)
            )
        }
        Text("${(q * 100).toInt()}", style = MaterialTheme.typography.titleMedium)
    }
}

fun PpgStatus.userMessage(): String = when (this) {
    PpgStatus.TOO_SHORT -> "Keep your finger still. Measuring…"
    PpgStatus.NO_FINGER -> "Cover the flash and the camera beside it with your fingertip(s)"
    PpgStatus.SATURATED -> "Press more lightly on the lens"
    PpgStatus.LOW_QUALITY -> "Signal too weak. Hold still and try again"
    PpgStatus.OK -> "Pulse estimate ready"
}
