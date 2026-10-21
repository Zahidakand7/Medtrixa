package com.medtrixa.app.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.medtrixa.app.ui.components.PremiumBackButton

private fun nihssTone(total: Int, calm: Color, danger: Color): Color = when {
    total <= 4 -> calm
    total <= 15 -> Color(0xFFE9A23B)
    total <= 20 -> Color(0xFFE5733F)
    else -> danger
}

@Composable
fun NihssScreen(onBackClick: () -> Unit) {
    val scheme = MaterialTheme.colorScheme
    val haptic = LocalHapticFeedback.current
    val clipboard = LocalClipboardManager.current

    // 15 entries, -1 = not scored, -2 = untestable
    var raw by rememberSaveable { mutableStateOf("") }
    val values: List<Int> = remember(raw) {
        val parts = raw.split(",")
        List(15) { i -> parts.getOrNull(i)?.toIntOrNull() ?: -1 }
    }
    val coma = values[0] == 3
    val total = (0..14).fold(0) { acc, i ->
        acc + when {
            i == 11 && coma -> 2
            i == 12 && coma -> 3
            values[i] >= 0 -> values[i]
            else -> 0
        }
    }
    val answered = (0..14).count { i -> ((i == 11 || i == 12) && coma) || values[i] != -1 }
    val untestable = values.count { it == -2 }
    val band = nihssBand(total)

    val copyText = "NIHSS " + total + " (" + band + ")" +
        (if (answered < 15) ", incomplete: $answered of 15 items entered" else "") + "\n" +
        nihssEntries.indices.joinToString(", ") { i ->
            nihssEntries[i].code + "=" + when {
                i == 11 && coma -> "2*"
                i == 12 && coma -> "3*"
                values[i] == -1 -> "-"
                values[i] == -2 -> "UN"
                else -> values[i].toString()
            }
        }

    val setValue: (Int, Int) -> Unit = { i, v ->
        val l = values.toMutableList()
        l[i] = v
        raw = l.joinToString(",")
    }

    val shownTotal by animateIntAsState(total, tween(400), label = "total")
    val progress by animateFloatAsState(total / 42f, tween(400), label = "progress")
    val accent by animateColorAsState(
        nihssTone(total, scheme.tertiary, scheme.error), tween(400), label = "accent"
    )

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        scheme.primary.copy(alpha = 0.06f).compositeOver(scheme.background),
                        scheme.background
                    )
                )
            ),
        color = Color.Transparent
    ) {
        Column(Modifier.fillMaxSize()) {

            Row(
                Modifier
                    .statusBarsPadding()
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PremiumBackButton(onClick = onBackClick)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("NIHSS", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(
                        "NIH Stroke Scale",
                        style = MaterialTheme.typography.bodySmall,
                        color = scheme.onSurfaceVariant
                    )
                }
            }

            Column(
                Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(scheme.surface)
                    .border(0.5.dp, accent.copy(alpha = 0.4f), RoundedCornerShape(22.dp))
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Total score",
                            style = MaterialTheme.typography.labelMedium,
                            color = scheme.onSurfaceVariant
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                "$shownTotal",
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "  / 42",
                                style = MaterialTheme.typography.titleMedium,
                                color = scheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }
                    }
                    Text(
                        band.removeSuffix(" stroke"),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = accent,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(accent.copy(alpha = 0.15f))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
                Spacer(Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape),
                    color = accent,
                    trackColor = accent.copy(alpha = 0.15f)
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "$answered of 15 answered" +
                        (if (untestable > 0) " · $untestable untestable, not counted" else ""),
                    style = MaterialTheme.typography.bodySmall,
                    color = scheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledTonalButton(
                        onClick = { clipboard.setText(AnnotatedString(copyText)) },
                        enabled = answered > 0
                    ) { Text("Copy score") }
                    TextButton(onClick = { raw = "" }, enabled = answered > 0) { Text("Clear") }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(nihssEntries) { index, entry ->
                    NihssItemCard(
                        entry = entry,
                        selected = values[index],
                        autoScore = if (coma && index == 11) 2 else if (coma && index == 12) 3 else null,
                        onSelect = { v ->
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            setValue(index, v)
                        }
                    )
                }
                item {
                    Text(
                        "Scoring aid based on the NINDS NIH Stroke Scale. The prompts are condensed. " +
                            "Items 9 and 10 need the official picture, naming and sentence sheets, " +
                            "and the exam should follow the NINDS form and certified training.",
                        style = MaterialTheme.typography.bodySmall,
                        color = scheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun NihssItemCard(
    entry: NihssEntry,
    selected: Int,
    autoScore: Int?,
    onSelect: (Int) -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(20.dp)
    val title = entry.title.substringAfter(". ")
    val points = when {
        autoScore != null -> "+$autoScore auto"
        selected >= 0 -> "+$selected"
        selected == -2 -> "UN"
        else -> ""
    }

    Column(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(scheme.surface)
            .border(BorderStroke(0.5.dp, scheme.outline.copy(alpha = 0.18f)), shape)
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                entry.code,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = scheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .widthIn(min = 36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(scheme.primary.copy(alpha = 0.12f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
            Spacer(Modifier.width(10.dp))
            Text(
                title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            if (points.isNotEmpty()) {
                Text(
                    points,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = scheme.primary
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            entry.hint,
            style = MaterialTheme.typography.bodySmall,
            color = scheme.onSurfaceVariant
        )
        if (autoScore != null) {
            Spacer(Modifier.height(10.dp))
            Text(
                "Coma (item 1a = 3): scored automatically as $autoScore.",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(scheme.primary.copy(alpha = 0.10f))
                    .padding(12.dp)
            )
        } else {
            entry.options.forEach { (value, label) ->
                Spacer(Modifier.height(6.dp))
                NihssOptionRow(value, label, selected == value) {
                    onSelect(if (selected == value) -1 else value)
                }
            }
        }
    }
}

@Composable
private fun NihssOptionRow(value: Int, text: String, selected: Boolean, onClick: () -> Unit) {
    val scheme = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(14.dp)
    val bg by animateColorAsState(
        if (selected) scheme.primary.copy(alpha = 0.12f) else Color.Transparent,
        tween(200), label = "optBg"
    )
    val stroke by animateColorAsState(
        if (selected) scheme.primary else scheme.outline.copy(alpha = 0.2f),
        tween(200), label = "optStroke"
    )

    Row(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(bg)
            .border(1.dp, stroke, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .defaultMinSize(minWidth = 26.dp, minHeight = 26.dp)
                .clip(CircleShape)
                .background(if (selected) scheme.primary else scheme.onSurface.copy(alpha = 0.08f))
                .padding(horizontal = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                if (value == -2) "UN" else "$value",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = if (selected) scheme.onPrimary else scheme.onSurfaceVariant
            )
        }
        Spacer(Modifier.width(12.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        if (selected) {
            Icon(
                Icons.Filled.Check,
                contentDescription = null,
                tint = scheme.primary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
