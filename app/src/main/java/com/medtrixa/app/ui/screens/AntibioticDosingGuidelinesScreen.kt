package com.medtrixa.app.ui.screens

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medtrixa.app.data.model.AntibioticDosingGuideline
import com.medtrixa.app.data.repository.AntibioticDosingRepository
import com.medtrixa.app.ui.components.PremiumBackButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val tierFilters = listOf("All", "Preferred", "Alternative", "Step-down", "Reserve", "Avoid")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AntibioticDosingGuidelinesScreen(onBackClick: () -> Unit) {
    BackHandler { onBackClick() }
    val context = LocalContext.current

    var loaded by remember { mutableStateOf(AntibioticDosingRepository.isLoaded()) }
    LaunchedEffect(Unit) {
        if (!loaded) {
            withContext(Dispatchers.IO) { AntibioticDosingRepository.load(context) }
            loaded = true
        }
    }

    var selectedTab by rememberSaveable { mutableStateOf(0) }
    var query by rememberSaveable { mutableStateOf("") }
    var selectedGroup by rememberSaveable { mutableStateOf("All") }
    var selectedTier by rememberSaveable { mutableStateOf("All") }

    val groups = remember(loaded) {
        listOf("All") + AntibioticDosingRepository.getAll().map { it.group }.filter { it.isNotBlank() }.distinct()
    }

    val results = remember(query, selectedGroup, selectedTier, loaded) {
        AntibioticDosingRepository.search(query)
            .filter { selectedGroup == "All" || it.group == selectedGroup }
            .filter { selectedTier == "All" || it.tier == selectedTier }
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
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 },
                    text = { Text("Dosing", fontWeight = FontWeight.SemiBold) })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 },
                    text = { Text("Breakpoints", fontWeight = FontWeight.SemiBold) })
            }
            if (selectedTab == 1) {
                ClsiBreakpointsPanel()
                return@Column
            }
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(18.dp),
                placeholder = {
                    AutoSizeText(
                        text = "Search Indication, Organism or Antibiotic",
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear search")
                        }
                    }
                },
                singleLine = true,
                maxLines = 1
            )

            FilterRow(
                options = groups,
                selected = selectedGroup,
                colorFor = { if (it == "All") MaterialTheme.colorScheme.primary else groupColor(it) },
                onSelect = { selectedGroup = it }
            )
            FilterRow(
                options = tierFilters,
                selected = selectedTier,
                colorFor = { if (it == "All") MaterialTheme.colorScheme.primary else tierColor(it) },
                onSelect = { selectedTier = it }
            )

            if (results.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(if (loaded) "No matching guidelines found." else "Loading guidelines\u2026")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 28.dp)
                ) {
                    item { HeroBanner(count = results.size) }
                    items(results) { guideline ->
                        GuidelineCard(guideline)
                    }
                    item { DisclaimerFooter() }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterRow(
    options: List<String>,
    selected: String,
    colorFor: @Composable (String) -> Color,
    onSelect: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(bottom = 4.dp)
    ) {
        items(options) { option ->
            val accent = colorFor(option)
            FilterChip(
                selected = option == selected,
                onClick = { onSelect(option) },
                label = { Text(option, fontSize = 13.sp, fontWeight = FontWeight.Medium) },
                shape = RoundedCornerShape(50),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = accent.copy(alpha = 0.18f),
                    selectedLabelColor = accent
                )
            )
        }
    }
}

@Composable
private fun HeroBanner(count: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(listOf(Color(0xFF0D47A1), Color(0xFF1565C0), Color(0xFF00897B)))
            )
            .padding(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                "IDSA 2026 AMR GUIDANCE",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.4.sp
            )
            Text(
                "Resistant Gram-negative dosing",
                color = Color.White,
                fontSize = 22.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Adult doses for normal renal and hepatic function. Current as of 1 March 2026.",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
            Spacer(Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.20f))
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
                Text(
                    "$count regimens",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
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

private fun groupColor(group: String): Color = when (group) {
    "ESBL-E" -> Color(0xFF1565C0)
    "AmpC-E" -> Color(0xFF00796B)
    "CRE" -> Color(0xFFC62828)
    "DTR P. aeruginosa" -> Color(0xFF6A1B9A)
    "CRAB" -> Color(0xFFE65100)
    "S. maltophilia" -> Color(0xFF2E7D32)
    else -> Color(0xFF455A64)
}

private fun tierColor(tier: String): Color = when (tier) {
    "Preferred" -> Color(0xFF2E7D32)
    "Alternative" -> Color(0xFF1565C0)
    "Step-down" -> Color(0xFF00838F)
    "Reserve" -> Color(0xFFEF6C00)
    "Avoid" -> Color(0xFFC62828)
    else -> Color(0xFF546E7A)
}

@Composable
private fun TierBadge(tier: String) {
    if (tier.isBlank()) return
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Color.White)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            tier.uppercase(),
            color = tierColor(tier),
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.8.sp
        )
    }
}

@Composable
private fun GuidelineCard(guideline: AntibioticDosingGuideline) {
    var expanded by rememberSaveable(guideline.group, guideline.indication, guideline.antibiotic, guideline.tier) {
        mutableStateOf(false)
    }
    val accent = groupColor(guideline.group)
    val isAvoid = guideline.tier == "Avoid"
    val hasDose = guideline.dose.isNotBlank() && guideline.dose != "Avoid" && guideline.dose != "Preserve"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.30f))
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(listOf(accent, accent.copy(alpha = 0.72f))))
                    .padding(horizontal = 18.dp, vertical = 11.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        guideline.group.ifBlank { "Guideline" },
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    TierBadge(guideline.tier)
                }
            }

            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    guideline.antibiotic,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 22.sp
                )
                Text(
                    guideline.indication,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = accent,
                    lineHeight = 18.sp
                )

                if (hasDose) {
                    DoseBlock(guideline, accent)
                } else if (guideline.notes.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background((if (isAvoid) tierColor("Avoid") else accent).copy(alpha = 0.09f))
                            .padding(14.dp)
                    ) {
                        Text(
                            guideline.notes,
                            fontSize = 13.sp,
                            lineHeight = 19.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                if (expanded) {
                    Spacer(Modifier.height(2.dp))
                    DetailRow("Organisms", guideline.organisms)
                    DetailRow("Setting", guideline.setting)
                    DetailRow("When to use", guideline.patientGroup, justify = true)
                    DetailRow("Duration", guideline.duration, justify = true)
                    DetailRow("Renal Adjustment", guideline.renalAdjustment, justify = true)
                    DetailRow("Hepatic Adjustment", guideline.hepaticAdjustment, justify = true)
                    DetailRow("Alternatives", guideline.alternatives, justify = true)
                    if (hasDose) DetailRow("Notes", guideline.notes, justify = true)
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "Source: ${guideline.source}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Status: ${guideline.sourceStatus} \u00b7 Reviewed: ${guideline.reviewDate}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        if (expanded) "Hide details" else "Tap for details",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = accent
                    )
                    Icon(
                        if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DoseBlock(guideline: AntibioticDosingGuideline, accent: Color) {
    val doseLines = guideline.dose.split(" | ").map { it.trim() }.filter { it.isNotEmpty() }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(accent.copy(alpha = 0.09f))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "DOSE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                color = accent,
                modifier = Modifier.weight(1f)
            )
            if (guideline.route.isNotBlank() && guideline.route != "-") {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(accent.copy(alpha = 0.18f))
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(guideline.route, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = accent)
                }
            }
        }
        doseLines.forEach { line ->
            Text(
                line,
                fontSize = 16.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        if (guideline.frequency.isNotBlank() && guideline.frequency != "-") {
            Text(
                guideline.frequency,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String, justify: Boolean = false) {
    if (value.isBlank() || value == "-") return
    // Justification is only applied when the value actually wraps to more than one line;
    // single-line values stay left-aligned.
    var isMultiLine by remember(value) { mutableStateOf(false) }

    Column(modifier = Modifier.padding(vertical = 3.dp)) {
        Text(
            label.uppercase(),
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            letterSpacing = 1.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value,
            style = if (justify && isMultiLine) {
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

@Composable
private fun DisclaimerFooter() {
    Text(
        "Doses follow IDSA Table 1 and may differ from FDA labeling. Susceptibility must be confirmed. " +
            "This is clinical decision support, not a substitute for clinical judgment, local antibiograms or " +
            "infectious diseases consultation.",
        fontSize = 11.sp,
        lineHeight = 16.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

fun preloadAntibioticGuidelines(context: Context) {
    AntibioticDosingRepository.load(context)
}
