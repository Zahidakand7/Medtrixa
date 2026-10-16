package com.medtrixa.app.ui.screens

import android.content.Context
import com.medtrixa.app.ui.components.PremiumBackButton
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medtrixa.app.data.model.AntibioticDosingGuideline
import com.medtrixa.app.data.repository.AntibioticDosingRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AntibioticDosingGuidelinesScreen(onBackClick: () -> Unit) {
    BackHandler { onBackClick() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        AntibioticDosingRepository.load(context)
    }

    var query by rememberSaveable { mutableStateOf("") }
    val results = remember(query) {
        AntibioticDosingRepository.search(query)
            .sortedBy { it.sourceStatus.contains("ARCHIVED", ignoreCase = true) }
    }

    Scaffold(
        modifier = Modifier.statusBarsPadding(),
        topBar = {
            TopAppBar(
                title = { Text("Antibiotic Dosing Guidelines", fontSize = 20.sp, lineHeight = 26.sp, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    PremiumBackButton(onClick = onBackClick, edgeInset = 12.dp)
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                placeholder = {
                    AutoSizeText(
                        text = "Search Indication, Organism or Antibiotic",
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                maxLines = 1
            )

            if (results.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No matching guidelines found.")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    itemsIndexed(results) { index, guideline ->
                        GuidelineCard(guideline, index)
                    }
                }
            }
        }
    }
}

@Composable
private fun AutoSizeText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier
) {
    var scaledFontSize by remember(text) { mutableStateOf(style.fontSize) }
    var readyToDraw by remember(text) { mutableStateOf(false) }

    Text(
        text = text,
        style = style.copy(fontSize = scaledFontSize),
        maxLines = 1,
        softWrap = false,
        modifier = modifier.drawWithContent { if (readyToDraw) drawContent() },
        onTextLayout = { result ->
            if (result.didOverflowWidth && scaledFontSize.value > 8f) {
                scaledFontSize = androidx.compose.ui.unit.TextUnit(
                    scaledFontSize.value * 0.94f,
                    androidx.compose.ui.unit.TextUnitType.Sp
                )
            } else {
                readyToDraw = true
            }
        }
    )
}

private val cardPalette = listOf(
    Color(0xFFE3F2FD),
    Color(0xFFE8F5E9),
    Color(0xFFFFF3E0),
    Color(0xFFF3E5F5),
    Color(0xFFFCE4EC)
)

@Composable
private fun GuidelineCard(guideline: AntibioticDosingGuideline, index: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardPalette[index % cardPalette.size]),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            AutoSizeText(
                text = capitalizeWords(guideline.indication),
                style = MaterialTheme.typography.titleLarge.copy(
                    color = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = capitalizeRegimenName(guideline.antibiotic),
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 12.sp),
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))
            DetailRow("Setting", guideline.setting)
            DetailRow("Organisms", capitalizeDrugName(guideline.organisms))
            DetailRow("Dose", guideline.dose)
            DetailRow("Route", guideline.route)
            DetailRow("Frequency", guideline.frequency)
            DetailRow("Duration", guideline.duration)
            DetailRow("Patient Group", guideline.patientGroup)
            DetailRow("Renal Adjustment", guideline.renalAdjustment, justify = true)
            DetailRow("Hepatic Adjustment", guideline.hepaticAdjustment, justify = true)
            DetailRow("Alternatives", capitalizeDrugName(guideline.alternatives), justify = true)
            DetailRow("Notes", guideline.notes, justify = true)

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Source: ${guideline.source}",
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Status: ${guideline.sourceStatus} \u00b7 Reviewed: ${guideline.reviewDate}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String, justify: Boolean = false) {
    if (value.isBlank()) return
    // Track whether this value actually wraps to more than one line once laid
    // out at the card's width. Justification is only ever applied when it does;
    // a single-line value is always rendered left-aligned.
    var isMultiLine by remember(value) { mutableStateOf(false) }

    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text("$label:", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.labelMedium)
        Text(
            value,
            style = if (justify && isMultiLine) {
                // Multi-line descriptive paragraphs: justify interior lines with
                // natural word wrapping + hyphenation; Compose/Android's justifier
                // (JUSTIFICATION_MODE_INTER_WORD, applied via LineBreak.Paragraph)
                // already leaves the final line of the paragraph left-aligned and
                // falls back to normal spacing when a line can't stretch cleanly,
                // so word spacing never becomes sporadic or oversized.
                MaterialTheme.typography.bodySmall.copy(
                    textAlign = TextAlign.Justify,
                    hyphens = Hyphens.Auto,
                    lineBreak = LineBreak.Paragraph
                )
            } else {
                MaterialTheme.typography.bodySmall.copy(textAlign = TextAlign.Start)
            },
            onTextLayout = { result ->
                if (justify) {
                    val nowMultiLine = result.lineCount > 1
                    if (nowMultiLine != isMultiLine) isMultiLine = nowMultiLine
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Capitalizes the first letter after each word boundary (space, slash, hyphen)
 * so drug names read correctly regardless of how the source CSV cased them.
 */
private fun capitalizeDrugName(text: String): String {
    if (text.isBlank()) return text
    val boundary = charArrayOf(' ', '/', '-', ';', ',', '(')
    val sb = StringBuilder(text.length)
    var capitalizeNext = true
    for (c in text) {
        if (capitalizeNext && c.isLetter()) {
            sb.append(c.uppercaseChar())
            capitalizeNext = false
        } else {
            sb.append(c)
        }
        if (c in boundary) capitalizeNext = true
    }
    return sb.toString()
}

private val regimenNameMinorWords = setOf(
    "a", "an", "and", "as", "at", "but", "by", "e.g", "for", "from",
    "i.e", "in", "nor", "of", "on", "or", "the", "to", "vs", "with"
)

/**
 * Headline-style capitalization for regimen/antibiotic names, e.g. turning
 * "mechanism-directed therapy (e.g., ceftazidime-avibactam for KPC-producing
 * CRE)" into "Mechanism-directed Therapy (e.g., Ceftazidime-Avibactam for
 * KPC-producing CRE)". Only the first letter of each space-separated word is
 * ever touched; everything after it (hyphenated suffixes, acronym casing
 * such as "KPC", internal punctuation) is left exactly as the source data
 * has it. Small connector words (for, e.g., of, ...) stay lowercase unless
 * they are the first word of the string.
 */
private fun capitalizeRegimenName(text: String): String {
    if (text.isBlank()) return text
    val withoutAbbrevComma = text.replace(Regex("(?i)\\b(e\\.g|i\\.e)\\.,"), "$1.")
    val words = withoutAbbrevComma.split(" ")
    val formatted = words.mapIndexed { index, word ->
        val letterIdx = word.indexOfFirst { it.isLetter() }
        if (letterIdx == -1) return@mapIndexed word
        val prefix = word.substring(0, letterIdx)
        val core = word.substring(letterIdx)
        val strippedForCheck = core.trimEnd(',', '.', ')', ';').lowercase()
        val isMinor = index != 0 && strippedForCheck in regimenNameMinorWords
        val firstChar = if (isMinor) core[0].lowercaseChar() else core[0].uppercaseChar()
        prefix + firstChar + core.substring(1)
    }.joinToString(" ")
    // Force the parenthetical clause onto its own line rather than leaving the
    // break to natural word-wrap, which shifts with screen width/font size.
    return formatted.replaceFirst(" (", "\n(")
}

/**
 * Capitalizes the first letter of every word so indication titles read as
 * Title Case regardless of how the source CSV cased them.
 */
private fun capitalizeWords(text: String): String {
    if (text.isBlank()) return text
    return text.split(" ").joinToString(" ") { word ->
        if (word.isEmpty()) word
        else word.replaceFirstChar { it.uppercaseChar() }
    }
}

fun preloadAntibioticGuidelines(context: Context) {
    AntibioticDosingRepository.load(context)
}
