package com.medtrixa.app.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.LocalPharmacy
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.outlined.Sell
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medtrixa.app.data.RecentDrugStore
import com.medtrixa.app.data.db.AppDatabase
import com.medtrixa.app.data.db.DrugEntity
import com.medtrixa.app.data.db.DrugLabelEntity
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private fun cleanLabelText(raw: String?): String {
    if (raw.isNullOrBlank()) return "No information available for this section."
    return raw
        .replace("•", "")
        .lines()
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .joinToString(" ")
        .replace(Regex("\\s{2,}"), " ")
        .trim()
}

private fun formatMarketingDate(raw: String?): String? = try {
    val d = LocalDate.parse(raw)
    "${d.dayOfMonth} ${d.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)}, ${d.year}"
} catch (e: Exception) { null }

private val CARD_ACCENT_COLORS = listOf(
    Color(0xFF6C5CE7), // purple
    Color(0xFF0984E3), // blue
    Color(0xFF00B894), // teal/green
    Color(0xFFE17055), // orange
    Color(0xFFE84393), // pink
    Color(0xFFFDA13B)  // amber
)

private fun formatApproval(raw: String?): String? = try {
    val d = LocalDate.parse(raw)
    "${d.dayOfMonth} ${d.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)}, ${d.year}"
} catch (e: Exception) { raw }

private val FORMULATION_ORDER = listOf(
    "tablet", "capsule", "syrup", "suspension", "powder for suspension", "powder", "cream", "ointment"
)

private fun formulationRank(formulation: String?): Int {
    val f = formulation?.trim()?.lowercase() ?: return FORMULATION_ORDER.size
    val idx = FORMULATION_ORDER.indexOfFirst { f.contains(it) }
    return if (idx >= 0) idx else FORMULATION_ORDER.size
}

private fun strengthValue(strength: String?): Double {
    if (strength.isNullOrBlank()) return Double.MAX_VALUE
    val match = Regex("\\d+(\\.\\d+)?").find(strength)
    return match?.value?.toDoubleOrNull() ?: Double.MAX_VALUE
}

@Composable
fun DrugDetailScreen(genericName: String, onBackClick: () -> Unit) {
    val context = LocalContext.current
    var strengths by remember { mutableStateOf<List<DrugEntity>>(emptyList()) }
    var label by remember { mutableStateOf<DrugLabelEntity?>(null) }
    var loaded by remember { mutableStateOf(false) }
    var expandedSection by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(genericName) {
        RecentDrugStore.addRecent(context, genericName)
        val dao = AppDatabase.getInstance(context).drugDao()
        strengths = dao.getStrengths(genericName).sortedWith(
            compareBy({ formulationRank(it.formulation) }, { strengthValue(it.strength) })
        )
        label = dao.getLabel(genericName)
        loaded = true
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 12.dp)) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text("Search Result", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            }

            if (loaded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    CARD_ACCENT_COLORS[0].copy(alpha = 0.12f),
                                    CARD_ACCENT_COLORS[2].copy(alpha = 0.08f)
                                )
                            ),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(16.dp)
                ) {
                    val primary = strengths.firstOrNull()
                    val distinctStrengths = strengths.mapNotNull { it.strength }.distinct()

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(CARD_ACCENT_COLORS[0].copy(alpha = 0.16f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Medication,
                                contentDescription = null,
                                tint = CARD_ACCENT_COLORS[0],
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(genericName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            primary?.pharmacologicClass?.let {
                                Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    if (distinctStrengths.isNotEmpty()) {
                        Spacer(modifier = Modifier.padding(top = 10.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(distinctStrengths.size) { idx ->
                                Tag(distinctStrengths[idx], CARD_ACCENT_COLORS[idx % CARD_ACCENT_COLORS.size])
                            }
                        }
                        Spacer(modifier = Modifier.padding(top = 10.dp))
                    }

                    primary?.let { p ->
                        if (!p.formulation.isNullOrBlank()) {
                            IconInfoRow(
                                icon = Icons.Outlined.LocalPharmacy,
                                iconColor = CARD_ACCENT_COLORS[1],
                                text = buildString {
                                    append("Formulation: ${p.formulation}")
                                    if (!p.route.isNullOrBlank()) append(", Route - ${p.route}")
                                }
                            )
                        }
                    }

                    StylishDivider()

                    primary?.let {
                        IconInfoRow(
                            icon = Icons.Outlined.Sell,
                            iconColor = CARD_ACCENT_COLORS[3],
                            text = "Brand Name: ${it.brandName ?: "N/A"}"
                        )
                        if (!it.company.isNullOrBlank()) {
                            IconInfoRow(
                                icon = Icons.Outlined.Business,
                                iconColor = CARD_ACCENT_COLORS[2],
                                text = "Company: ${it.company}"
                            )
                        }
                        formatMarketingDate(it.marketingDate)?.let { d ->
                            IconInfoRow(
                                icon = Icons.Outlined.CalendarToday,
                                iconColor = CARD_ACCENT_COLORS[4],
                                text = "Marketing Date: $d"
                            )
                        }
                        val approval = formatApproval(it.initialApproval) ?: it.initialApproval
                        if (approval != null || it.rxcui != null) {
                            IconInfoRow(
                                icon = Icons.Outlined.VerifiedUser,
                                iconColor = CARD_ACCENT_COLORS[5],
                                text = buildString {
                                    approval?.let { a -> append("FDA Approval: $a") }
                                    if (approval != null && it.rxcui != null) append(", ")
                                    it.rxcui?.let { r -> append("RxCUI: $r") }
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.padding(top = 16.dp))

                if (label != null) {
                    val l = label!!
                    val sections = listOf(
                        "Indications" to cleanLabelText(l.indications),
                        "Adult dose" to cleanLabelText(l.adultDose),
                        "Child dose" to cleanLabelText(l.childDose),
                        "Renal / hepatic dose" to cleanLabelText(l.renalHepaticDose),
                        "Administration" to cleanLabelText(l.adultAdministration),
                        "Contraindications" to cleanLabelText(l.contraindications),
                        "Warnings" to cleanLabelText(l.warnings),
                        "Adverse reactions" to cleanLabelText(l.adverseReactions),
                        "Pregnancy" to cleanLabelText(l.pregnancy),
                        "Mechanism of action" to cleanLabelText(l.mechanism)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                    ) {
                        sections.forEachIndexed { index, (title, content) ->
                            AccordionRow(
                                title = title,
                                content = content,
                                expanded = expandedSection == title,
                                onToggle = {
                                    expandedSection = if (expandedSection == title) null else title
                                },
                                showDivider = index != sections.lastIndex
                            )
                        }
                    }
                } else {
                    Text(
                        "Clinical details aren't available for this drug yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.padding(bottom = 24.dp))
        }
    }
}

@Composable
private fun Eyebrow(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun StylishDivider() {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .padding(vertical = 16.dp)
            .background(MaterialTheme.colorScheme.outlineVariant)
    )
}

@Composable
private fun IconInfoRow(icon: ImageVector, iconColor: Color, text: String) {
    if (text.isBlank()) return
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .background(iconColor.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(14.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun InfoLine(label1: String, value1: String?, label2: String?, value2: String?) {
    if (value1.isNullOrBlank()) return
    Text(
        text = buildString {
            append("$label1: $value1")
            if (label2 != null && !value2.isNullOrBlank()) append(", $label2 $value2")
        },
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(bottom = 3.dp)
    )
}

@Composable
private fun Tag(text: String, color: Color = MaterialTheme.colorScheme.primary) {
    if (text.isBlank()) return
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = color,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 3.dp)
    )
}

@Composable
private fun AccordionRow(
    title: String,
    content: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    showDivider: Boolean
) {
    Column {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggle() }
                .animateContentSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (expanded) FontWeight.Medium else FontWeight.Normal
                )
                Icon(
                    imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (expanded) {
                Spacer(modifier = Modifier.padding(top = 8.dp))
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Justify
                )
            }
        }
        if (showDivider) {
            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
        }
    }
}
