package com.medtrixa.app.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medtrixa.app.ui.components.PremiumBackButton
import com.medtrixa.app.ui.screens.stewardship.SAccent
import com.medtrixa.app.ui.screens.stewardship.SBullets
import com.medtrixa.app.ui.screens.stewardship.SCallout
import com.medtrixa.app.ui.screens.stewardship.SRow
import com.medtrixa.app.ui.screens.stewardship.SRows
import com.medtrixa.app.ui.screens.stewardship.SSection
import com.medtrixa.app.ui.screens.stewardship.SText
import com.medtrixa.app.ui.screens.stewardship.VisualBlock
import com.medtrixa.app.ui.screens.stewardship.stewSectionsA
import com.medtrixa.app.ui.screens.stewardship.stewSectionsB
import com.medtrixa.app.ui.screens.stewardship.stewSectionsC

private val stewSources = listOf(
    "CDC. Core Elements of Hospital Antibiotic Stewardship Programs, 2019; and The Core Elements of Outpatient Antibiotic Stewardship Programs, 2025.",
    "CDC. Antibiotic Use and Stewardship in the United States, 2024 Update: Progress and Opportunities.",
    "WHO. Global Antibiotic Resistance Surveillance Report 2025 (GLASS). Geneva; 2025. WHO Antimicrobial resistance fact sheet, July 2026.",
    "GBD 2021 Antimicrobial Resistance Collaborators. Global burden of bacterial antimicrobial resistance 1990-2021. Lancet. 2024;404:1199-1226.",
    "2025 ATS Clinical Practice Guideline Updates for the Diagnosis and Management of Community-Acquired Pneumonia. Am J Respir Crit Care Med. 2025;212(1).",
    "IDSA 2025 Guideline on Management and Treatment of Complicated Urinary Tract Infections: Duration of Antibiotics. Clin Infect Dis. 2025.",
    "BALANCE Investigators. Antibiotic treatment for 7 versus 14 days in patients with bloodstream infections. N Engl J Med. 2025;392:1065-1078.",
    "Tamma PD, et al. IDSA 2024 Guidance on the Treatment of Antimicrobial-Resistant Gram-Negative Infections. Clin Infect Dis. 2024.",
    "Surviving Sepsis Campaign: International Guidelines for Management of Sepsis and Septic Shock 2026. Intensive Care Med and Crit Care Med. 2026.",
    "IDSA/ESCMID. Staphylococcus aureus Bacteremia: Risk Stratification, Diagnostic Evaluation, and Management of Adults and Children. 2026.",
    "Trubiano JA, et al. PEN-FAST. JAMA Intern Med. 2020;180:745-752. Copaescu AM, et al. PALACE trial. JAMA Intern Med. 2023;183:944-952.",
    "Johnson S, et al. 2021 IDSA/SHEA Focused Update on Clostridioides difficile Infection in Adults. Clin Infect Dis. 2021;73:e1029-e1044.",
    "Rybak MJ, et al. Therapeutic monitoring of vancomycin for serious MRSA infections: 2020 consensus guideline. Am J Health Syst Pharm. 2020;77:835-864."
)

private fun accent1(a: SAccent): Color = when (a) {
    SAccent.BLUE -> Color(0xFF2563EB)
    SAccent.ROSE -> Color(0xFFE11D48)
    SAccent.AMBER -> Color(0xFFD97706)
    SAccent.RED -> Color(0xFFDC2626)
    SAccent.VIOLET -> Color(0xFF7C3AED)
    SAccent.GREEN -> Color(0xFF059669)
    SAccent.TEAL -> Color(0xFF0D9488)
}

private fun accent2(a: SAccent): Color = when (a) {
    SAccent.BLUE -> Color(0xFF60A5FA)
    SAccent.ROSE -> Color(0xFFFB7185)
    SAccent.AMBER -> Color(0xFFFBBF24)
    SAccent.RED -> Color(0xFFF87171)
    SAccent.VIOLET -> Color(0xFFA78BFA)
    SAccent.GREEN -> Color(0xFF34D399)
    SAccent.TEAL -> Color(0xFF2DD4BF)
}

@Composable
private fun FitText(
    text: String,
    maxSize: Float,
    minSize: Float,
    color: Color,
    fontWeight: FontWeight,
    modifier: Modifier = Modifier
) {
    var sizeSp by remember(text) { mutableStateOf(maxSize) }
    Text(
        text = text,
        modifier = modifier,
        color = color,
        fontSize = sizeSp.sp,
        lineHeight = (sizeSp + 6f).sp,
        fontWeight = fontWeight,
        maxLines = 1,
        softWrap = false,
        overflow = TextOverflow.Clip,
        onTextLayout = { r ->
            if (r.didOverflowWidth && sizeSp > minSize) {
                sizeSp = (sizeSp - 0.5f).coerceAtLeast(minSize)
            }
        }
    )
}

@Composable
fun AntibioticStewardshipReferenceScreen(
    onBackClick: () -> Unit
) {
    val sections = remember { stewSectionsA + stewSectionsB + stewSectionsC }
    var selected by rememberSaveable { mutableStateOf(0) }
    val sourcesIndex = sections.size
    val listState = rememberLazyListState()

    LaunchedEffect(selected) { listState.scrollToItem(0) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PremiumBackButton(onClick = onBackClick)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Antibiotic Stewardship",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        fontSize = 20.sp,
                        lineHeight = 26.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    FitText(
                        text = "Evidence, spectrum, duration and resistance, 2026",
                        maxSize = 14f,
                        minSize = 10f,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Normal
                    )
                }
            }

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(count = sections.size + 1) { i ->
                    if (i < sections.size) {
                        SectionChip(sections[i].title, sections[i].accent, selected == i) { selected = i }
                    } else {
                        SectionChip("Sources", SAccent.VIOLET, selected == i) { selected = i }
                    }
                }
            }

            if (selected < sourcesIndex) {
                SectionContent(sections[selected], listState)
            } else {
                SourcesContent(listState)
            }
        }
    }

    BackHandler(onBack = onBackClick)
}

@Composable
private fun SectionChip(label: String, accent: SAccent, selected: Boolean, onClick: () -> Unit) {
    val base = accent1(accent)
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (selected) base else base.copy(alpha = 0.14f))
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 12.dp)
    ) {
        Text(
            text = label,
            color = if (selected) Color.White else base,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            maxLines = 1
        )
    }
}

@Composable
private fun SectionContent(section: SSection, listState: LazyListState) {
    val accent = accent1(section.accent)
    val entries: List<Any> = remember(section) {
        val out = mutableListOf<Any>()
        section.blocks.forEach { b ->
            if (b is SRows) out.addAll(b.rows) else out.add(b)
        }
        out
    }
    val count = remember(section) {
        section.blocks.filterIsInstance<SRows>().fold(0) { acc, r -> acc + r.rows.size }
    }

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Banner(section.title, count, section.accent) }
        items(count = entries.size) { i ->
            when (val e = entries[i]) {
                is SText -> Text(
                    text = e.text,
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 14.sp, lineHeight = 21.sp, textAlign = TextAlign.Justify, hyphens = Hyphens.Auto, lineBreak = LineBreak.Paragraph),
                    color = MaterialTheme.colorScheme.onSurface
                )
                is SBullets -> BulletCard(e.items, accent)
                is SCallout -> CalloutCard(e.title, e.body, accent)
                is SRow -> RowCard(e, accent)
                is VisualBlock -> e.content()
                else -> {}
            }
        }
    }
}

@Composable
private fun SourcesContent(listState: LazyListState) {
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Banner("Sources", 0, SAccent.VIOLET) }
        items(count = stewSources.size) { i ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                )
            ) {
                Text(
                    text = stewSources[i],
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp, lineHeight = 19.sp, textAlign = TextAlign.Justify, hyphens = Hyphens.Auto, lineBreak = LineBreak.Paragraph),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun Banner(title: String, count: Int, accent: SAccent) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.horizontalGradient(listOf(accent1(accent), accent2(accent))))
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            FitText(
                text = title,
                maxSize = 20f,
                minSize = 12f,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            if (count > 0) {
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color.White.copy(alpha = 0.22f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = count.toString() + " entries",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun BulletCard(items: List<String>, accent: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items.forEach { line ->
                Row(verticalAlignment = Alignment.Top) {
                    Box(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(accent)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = line,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp, lineHeight = 19.sp, textAlign = TextAlign.Justify, hyphens = Hyphens.Auto, lineBreak = LineBreak.Paragraph),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun CalloutCard(title: String, body: String, accent: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = accent.copy(alpha = 0.12f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                tint = accent
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                FitText(
                    text = title,
                    maxSize = 15f,
                    minSize = 11f,
                    color = accent,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp, lineHeight = 19.sp, textAlign = TextAlign.Justify, hyphens = Hyphens.Auto, lineBreak = LineBreak.Paragraph),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun RowCard(row: SRow, accent: Color) {
    val muted = MaterialTheme.colorScheme.onSurfaceVariant
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
        )
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxHeight()
                    .background(accent)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FitText(
                    text = row.term,
                    maxSize = 16f,
                    minSize = 11f,
                    color = accent,
                    fontWeight = FontWeight.Bold
                )
                FitText(
                    text = row.full,
                    maxSize = 14f,
                    minSize = 10f,
                    color = muted,
                    fontWeight = FontWeight.Medium
                )
                RichBody(row.body, accent)
                val note = row.note
                if (note != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(accent.copy(alpha = 0.10f))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = note,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 17.sp, textAlign = TextAlign.Justify, hyphens = Hyphens.Auto, lineBreak = LineBreak.Paragraph),
                            color = muted
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RichBody(body: String, accent: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        body.split("\n").forEach { line ->
            if (line.startsWith("> ")) {
                Row(
                    modifier = Modifier.padding(start = 6.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(accent)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = line.removePrefix("> "),
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp, lineHeight = 19.sp, textAlign = TextAlign.Justify, hyphens = Hyphens.Auto, lineBreak = LineBreak.Paragraph),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            } else {
                Text(
                    text = line,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp, lineHeight = 19.sp, textAlign = TextAlign.Justify, hyphens = Hyphens.Auto, lineBreak = LineBreak.Paragraph),
                    fontWeight = if (line.endsWith(":")) FontWeight.SemiBold else FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
