package com.medtrixa.app.ui.screens.stewardship

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.exp

class VisualBlock(val content: @Composable () -> Unit) : SBlock()

object SvColor {
    val bg0 = Color(0xFF0A111F)
    val bg1 = Color(0xFF16233A)
    val text = Color(0xFFF1F5F9)
    val sub = Color(0xFF9FB0C7)
    val green = Color(0xFF34D399)
    val amber = Color(0xFFFBBF24)
    val red = Color(0xFFF87171)
    val blue = Color(0xFF60A5FA)
    val violet = Color(0xFFA78BFA)
    val teal = Color(0xFF2DD4BF)
    val rose = Color(0xFFFB7185)
}

@Composable
private fun SvText(
    text: String,
    size: Int = 13,
    color: Color = SvColor.text,
    weight: FontWeight = FontWeight.Normal,
    align: TextAlign = TextAlign.Start,
    modifier: Modifier = Modifier
) {
    BasicText(
        text = text,
        modifier = if (align == TextAlign.Justify) modifier.fillMaxWidth() else modifier,
        style = TextStyle(
            color = color,
            fontSize = size.sp,
            fontWeight = weight,
            textAlign = align,
            lineHeight = (size + 5).sp
        )
    )
}

@Composable
private fun SvPanel(title: String, subtitle: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.verticalGradient(listOf(SvColor.bg1, SvColor.bg0)))
            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(24.dp))
            .padding(18.dp)
    ) {
        Box(
            Modifier
                .width(38.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Brush.horizontalGradient(listOf(SvColor.teal, SvColor.blue)))
        )
        Spacer(Modifier.height(10.dp))
        FitSvText(title, 17, SvColor.text, FontWeight.Bold, 10)
        Spacer(Modifier.height(2.dp))
        SvText(subtitle, 12, SvColor.sub, align = TextAlign.Justify)
        Spacer(Modifier.height(14.dp))
        content()
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(10.dp).clip(CircleShape).background(color))
        Spacer(Modifier.width(6.dp))
        SvText(label, 11, SvColor.sub)
    }
}

class SvStat(val value: String, val label: String, val color: Color)

@Composable
private fun StatGrid(stats: List<SvStat>, valueSize: Int) {
    stats.chunked(2).forEach { row ->
        Row(
            Modifier.fillMaxWidth().height(IntrinsicSize.Min).padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            row.forEach { s ->
                Column(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0x14FFFFFF))
                        .padding(14.dp)
                ) {
                    SvText(s.value, valueSize, s.color, FontWeight.ExtraBold)
                    Spacer(Modifier.height(3.dp))
                    SvText(s.label, 11, SvColor.sub)
                }
            }
            if (row.size == 1) Spacer(Modifier.weight(1f))
        }
    }
}

fun statsVisual(title: String, subtitle: String, stats: List<SvStat>, foot: String = ""): VisualBlock =
    VisualBlock {
        SvPanel(title, subtitle) {
            StatGrid(stats, 22)
            if (foot.isNotEmpty()) SvText(foot, 10, SvColor.sub, align = TextAlign.Justify)
        }
    }

class SvRow(val name: String, val cells: List<Int>)

fun spectrumVisual(
    title: String,
    subtitle: String,
    cols: List<String>,
    rows: List<SvRow>,
    foot: String = ""
): VisualBlock = VisualBlock {
    SvPanel(title, subtitle) {
        Column(Modifier.horizontalScroll(rememberScrollState())) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(Modifier.width(112.dp))
                cols.forEach { c ->
                    Box(Modifier.width(52.dp), contentAlignment = Alignment.Center) {
                        SvText(c, 10, SvColor.sub, FontWeight.SemiBold, TextAlign.Center)
                    }
                }
            }
            Spacer(Modifier.height(6.dp))
            rows.forEach { r ->
                Row(Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.width(112.dp)) {
                        SvText(r.name, 12, SvColor.text, FontWeight.Medium)
                    }
                    r.cells.forEach { v ->
                        Box(Modifier.width(52.dp), contentAlignment = Alignment.Center) {
                            Box(
                                Modifier
                                    .size(if (v == 0) 8.dp else 18.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (v) {
                                            2 -> SvColor.green
                                            1 -> SvColor.amber
                                            else -> Color(0x33FFFFFF)
                                        }
                                    )
                            )
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            LegendDot(SvColor.green, "Reliable")
            LegendDot(SvColor.amber, "Variable")
            LegendDot(Color(0x55FFFFFF), "None")
        }
        if (foot.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            SvText(foot, 10, SvColor.sub, align = TextAlign.Justify)
        }
    }
}

class SvDur(val label: String, val old: Int, val now: Int, val nowLabel: String, val note: String)

fun durationVisual(
    title: String,
    subtitle: String,
    items: List<SvDur>,
    maxDays: Int = 14,
    foot: String = ""
): VisualBlock = VisualBlock {
    SvPanel(title, subtitle) {
        items.forEach { d ->
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SvText(d.label, 13, SvColor.text, FontWeight.SemiBold, modifier = Modifier.weight(1f))
                SvText(d.nowLabel, 13, SvColor.green, FontWeight.Bold)
            }
            Spacer(Modifier.height(6.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(9.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color(0x1FFFFFFF))
            ) {
                Box(
                    Modifier
                        .fillMaxWidth((d.old.toFloat() / maxDays).coerceIn(0.05f, 1f))
                        .height(9.dp)
                        .background(Color(0x55F87171))
                )
                Box(
                    Modifier
                        .fillMaxWidth((d.now.toFloat() / maxDays).coerceIn(0.05f, 1f))
                        .height(9.dp)
                        .background(Brush.horizontalGradient(listOf(SvColor.teal, SvColor.green)))
                )
            }
            Spacer(Modifier.height(3.dp))
            SvText("Traditional about " + d.old + " d. " + d.note, 11, SvColor.sub)
            Spacer(Modifier.height(14.dp))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            LegendDot(SvColor.green, "Evidence-based course")
            LegendDot(Color(0x88F87171), "Traditional course")
        }
        if (foot.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            SvText(foot, 10, SvColor.sub, align = TextAlign.Justify)
        }
    }
}

class SvStep(val title: String, val detail: String, val color: Color)

fun flowVisual(title: String, subtitle: String, steps: List<SvStep>): VisualBlock = VisualBlock {
    SvPanel(title, subtitle) {
        steps.forEachIndexed { i, s ->
            Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                Column(Modifier.fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        Modifier.size(30.dp).clip(CircleShape).background(s.color),
                        contentAlignment = Alignment.Center
                    ) {
                        SvText((i + 1).toString(), 14, SvColor.bg0, FontWeight.ExtraBold)
                    }
                    if (i < steps.size - 1) {
                        Box(
                            Modifier
                                .width(2.dp)
                                .weight(1f)
                                .background(s.color.copy(alpha = 0.45f))
                        )
                    }
                }
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f).padding(bottom = 16.dp)) {
                    SvText(s.title, 14, SvColor.text, FontWeight.Bold)
                    Spacer(Modifier.height(2.dp))
                    SvText(s.detail, 12, SvColor.sub, align = TextAlign.Justify)
                }
            }
        }
    }
}

class SvSeg(val label: String, val pct: Float, val pctLabel: String, val color: Color, val examples: String)

fun awareVisual(
    title: String,
    subtitle: String,
    segs: List<SvSeg>,
    targetPct: Float,
    targetLabel: String,
    foot: String = ""
): VisualBlock = VisualBlock {
    SvPanel(title, subtitle) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(42.dp)
                .clip(RoundedCornerShape(12.dp))
        ) {
            Row(Modifier.fillMaxSize()) {
                segs.forEach { s ->
                    Box(
                        Modifier.weight(s.pct).fillMaxHeight().background(s.color),
                        contentAlignment = Alignment.Center
                    ) {
                        if (s.pct >= 10f) SvText(s.pctLabel, 14, SvColor.bg0, FontWeight.ExtraBold)
                    }
                }
            }
            Row(Modifier.fillMaxSize()) {
                Spacer(Modifier.weight(targetPct))
                Box(Modifier.width(3.dp).fillMaxHeight().background(Color.White))
                Spacer(Modifier.weight(100f - targetPct))
            }
        }
        Spacer(Modifier.height(8.dp))
        SvText(targetLabel, 11, SvColor.sub, align = TextAlign.Justify)
        Spacer(Modifier.height(14.dp))
        segs.forEach { s ->
            Row(verticalAlignment = Alignment.Top) {
                Box(Modifier.padding(top = 4.dp).size(10.dp).clip(CircleShape).background(s.color))
                Spacer(Modifier.width(10.dp))
                Column {
                    SvText(s.label + "  " + s.pctLabel, 13, SvColor.text, FontWeight.Bold)
                    SvText(s.examples, 11, SvColor.sub)
                }
            }
            Spacer(Modifier.height(10.dp))
        }
        if (foot.isNotEmpty()) SvText(foot, 10, SvColor.sub, align = TextAlign.Justify)
    }
}

fun pkpdVisual(title: String, subtitle: String, caption: String, chips: List<SvStat>): VisualBlock =
    VisualBlock {
        SvPanel(title, subtitle) {
            Canvas(Modifier.fillMaxWidth().height(160.dp)) {
                val w = size.width
                val h = size.height
                val hours = 24f
                val tau = 6f
                val k = 0.4f
                val dose = 5f
                val steps = 288
                val pts = FloatArray(steps + 1)
                var maxC = 0f
                for (i in 0..steps) {
                    val t = hours * i / steps
                    var c = 0f
                    var d = 0f
                    while (d <= t) {
                        c += dose * exp(-k * (t - d))
                        d += tau
                    }
                    pts[i] = c
                    if (c > maxC) maxC = c
                }
                val top = maxC * 1.12f
                val dx = w / steps
                fun py(c: Float): Float = h - (c / top) * h
                for (i in 0 until steps) {
                    if (pts[i] > 1f) {
                        drawRect(
                            color = SvColor.green.copy(alpha = 0.22f),
                            topLeft = Offset(i * dx, py(pts[i])),
                            size = Size(dx + 1f, py(1f) - py(pts[i]))
                        )
                    }
                }
                drawLine(
                    color = SvColor.amber,
                    start = Offset(0f, py(1f)),
                    end = Offset(w, py(1f)),
                    strokeWidth = 2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f), 0f)
                )
                val path = Path()
                for (i in 0..steps) {
                    val x = i * dx
                    val y = py(pts[i])
                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                drawPath(
                    path = path,
                    color = SvColor.teal,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                SvText("0 h", 10, SvColor.sub)
                SvText("24 h, doses every 6 h", 10, SvColor.sub)
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                LegendDot(SvColor.teal, "Free drug level")
                LegendDot(SvColor.amber, "MIC")
                LegendDot(SvColor.green, "Time above MIC")
            }
            Spacer(Modifier.height(8.dp))
            SvText(caption, 11, SvColor.sub, align = TextAlign.Justify)
            Spacer(Modifier.height(14.dp))
            StatGrid(chips, 16)
        }
    }

class SvScore(val score: String, val risk: String, val action: String, val color: Color)

fun scoreVisual(title: String, subtitle: String, items: List<SvScore>, foot: String = ""): VisualBlock =
    VisualBlock {
        SvPanel(title, subtitle) {
            items.chunked(2).forEach { row ->
                Row(
                    Modifier.fillMaxWidth().height(IntrinsicSize.Min).padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    row.forEach { s ->
                        Column(
                            Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(16.dp))
                                .background(s.color.copy(alpha = 0.14f))
                                .border(1.dp, s.color.copy(alpha = 0.55f), RoundedCornerShape(16.dp))
                                .padding(14.dp)
                        ) {
                            SvText("Score " + s.score, 12, SvColor.sub, FontWeight.SemiBold)
                            SvText(s.risk, 22, s.color, FontWeight.ExtraBold)
                            Spacer(Modifier.height(4.dp))
                            SvText(s.action, 12, SvColor.text)
                        }
                    }
                }
            }
            if (foot.isNotEmpty()) SvText(foot, 10, SvColor.sub, align = TextAlign.Justify)
        }
    }

@Composable
private fun FitSvText(
    text: String,
    size: Int,
    color: Color,
    weight: FontWeight,
    minSize: Int,
    modifier: Modifier = Modifier
) {
    var sp by remember(text) { mutableStateOf(size.toFloat()) }
    BasicText(
        text = text,
        modifier = modifier,
        maxLines = 1,
        softWrap = false,
        overflow = TextOverflow.Clip,
        onTextLayout = { r ->
            if (r.didOverflowWidth && sp > minSize) {
                sp = (sp - 0.5f).coerceAtLeast(minSize.toFloat())
            }
        },
        style = TextStyle(
            color = color,
            fontSize = sp.sp,
            fontWeight = weight,
            lineHeight = (sp + 5f).sp
        )
    )
}
