package com.medtrixa.app.ui.screens.pulse

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.medtrixa.app.pulse.PpgStatus

/** Smooth pulse trace: faint grid, glow underlay, fading tail, peak markers. */
@Composable
fun PulseWaveform(
    signal: FloatArray,
    peaks: List<Int>,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.primary
) {
    val grid = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)
    Canvas(modifier.fillMaxWidth().height(140.dp)) {
        val w = size.width
        val h = size.height
        for (i in 1..3) {
            val gy = h * i / 4f
            drawLine(grid, Offset(0f, gy), Offset(w, gy), strokeWidth = 1f)
        }
        if (signal.size < 2) return@Canvas

        var lo = Float.MAX_VALUE
        var hi = -Float.MAX_VALUE
        for (v in signal) { lo = minOf(lo, v); hi = maxOf(hi, v) }
        val range = (hi - lo).coerceAtLeast(1e-3f)
        val padY = h * 0.12f
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

        val fade = Brush.horizontalGradient(
            0f to lineColor.copy(alpha = 0.15f),
            0.6f to lineColor.copy(alpha = 0.8f),
            1f to lineColor
        )
        drawPath(
            path, fade, alpha = 0.12f,
            style = Stroke(10.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
        drawPath(
            path, fade,
            style = Stroke(2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        for (p in peaks) {
            if (p !in signal.indices) continue
            val c = Offset(px(p), py(signal[p]))
            drawCircle(lineColor.copy(alpha = 0.18f), 7.dp.toPx(), c)
            drawCircle(lineColor, 3.dp.toPx(), c)
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
    PpgStatus.NO_FINGER -> "Cover the camera and flash with your fingertip"
    PpgStatus.SATURATED -> "Press more lightly on the lens"
    PpgStatus.LOW_QUALITY -> "Signal too weak. Hold still and try again"
    PpgStatus.OK -> "Pulse estimate ready"
}
