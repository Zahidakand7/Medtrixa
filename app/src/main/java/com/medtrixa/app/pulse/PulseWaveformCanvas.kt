package com.medtrixa.app.pulse

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val SlateBg = Color(0xFF0F172A)
private val GridColor = Color(0xFF334155)
private val CyanStart = Color(0xFF38BDF8)
private val CyanEnd = Color(0xFF0284C7)
private val Amber = Color(0xFFF59E0B)
private val SlateLine = Color(0xFF64748B)

/** Dark premium waveform card. Draws nothing but a flat dashed line while there is no finger. */
@Composable
fun PulseWaveformCanvas(
    waveform: List<Float>,
    finger: FingerState,
    quality: SignalQuality,
    modifier: Modifier = Modifier.fillMaxWidth().height(160.dp)
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background(SlateBg)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val w = size.width
            val h = size.height
            val gridStroke = 1.dp.toPx()
            for (i in 1..3) {
                val y = h * i / 4f
                drawLine(GridColor.copy(alpha = 0.7f), Offset(0f, y), Offset(w, y), gridStroke)
            }
            for (i in 1..5) {
                val x = w * i / 6f
                drawLine(GridColor.copy(alpha = 0.45f), Offset(x, 0f), Offset(x, h), gridStroke)
            }

            val mid = h / 2f
            if (finger != FingerState.PRESENT || waveform.size < 2) {
                drawLine(
                    color = Color(0xFF475569),
                    start = Offset(16.dp.toPx(), mid),
                    end = Offset(w - 16.dp.toPx(), mid),
                    strokeWidth = 2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 12f))
                )
                return@Canvas
            }

            val amp = h * 0.38f
            val stepX = w / (waveform.size - 1)
            val path = Path()
            for (i in waveform.indices) {
                val x = i * stepX
                val y = mid - waveform[i] * amp
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }

            val brush: Brush = when (quality) {
                SignalQuality.GOOD -> Brush.horizontalGradient(listOf(CyanStart, CyanEnd), 0f, w)
                SignalQuality.FAIR -> Brush.horizontalGradient(listOf(Amber, Amber), 0f, w)
                else -> Brush.horizontalGradient(listOf(SlateLine, SlateLine), 0f, w)
            }
            val glow: Color = when (quality) {
                SignalQuality.GOOD -> CyanStart
                SignalQuality.FAIR -> Amber
                else -> SlateLine
            }

            drawPath(
                path,
                color = glow.copy(alpha = 0.10f),
                style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
            drawPath(
                path,
                color = glow.copy(alpha = 0.22f),
                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
            drawPath(
                path,
                brush = brush,
                style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }

        if (finger != FingerState.PRESENT) {
            Text(
                "No finger detected",
                color = Color(0xFF94A3B8),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

/** Small status chip: a coloured dot and "Finger detected" or "No finger". */
@Composable
fun FingerStatusChip(finger: FingerState, quality: SignalQuality) {
    val present = finger == FingerState.PRESENT
    val dot = when {
        !present -> Color(0xFFEF4444)
        quality == SignalQuality.GOOD -> Color(0xFF22C55E)
        quality == SignalQuality.FAIR -> Color(0xFFF59E0B)
        else -> Color(0xFF38BDF8)
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(10.dp).background(dot, CircleShape))
        Spacer(Modifier.width(8.dp))
        Text(
            if (present) "Finger detected" else "No finger",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
