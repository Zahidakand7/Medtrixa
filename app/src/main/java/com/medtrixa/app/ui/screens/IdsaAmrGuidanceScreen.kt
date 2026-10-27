package com.medtrixa.app.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medtrixa.app.data.model.AntibioticDosingGuideline
import com.medtrixa.app.data.repository.AntibioticDosingRepository
import com.medtrixa.app.ui.components.PremiumBackButton
import com.medtrixa.app.ui.theme.auraAccent
import com.medtrixa.app.ui.theme.medtrixaAuraBrush
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val idsaTiers = listOf("Preferred", "Alternative", "Step-down", "Reserve", "Avoid")

@Composable
fun IdsaAmrGuidanceScreen(onBackClick: () -> Unit) {
    BackHandler { onBackClick() }
    val context = LocalContext.current
    val scheme = MaterialTheme.colorScheme

    var loaded by remember { mutableStateOf(AntibioticDosingRepository.isLoaded()) }
    LaunchedEffect(Unit) {
        if (!loaded) {
            withContext(Dispatchers.IO) { AntibioticDosingRepository.load(context) }
            loaded = true
        }
    }

    var query by rememberSaveable { mutableStateOf("") }
    var group by rememberSaveable { mutableStateOf("All") }
    var tier by rememberSaveable { mutableStateOf("All") }

    val groups = remember(loaded) {
        listOf("All") + AntibioticDosingRepository.getAll().map { it.group }.filter { it.isNotBlank() }.distinct()
    }
    val results = remember(query, group, tier, loaded) {
        AntibioticDosingRepository.search(query)
            .filter { group == "All" || it.group == group }
            .filter { tier == "All" || it.tier == tier }
            .sortedBy { it.sourceStatus.contains("ARCHIVED", ignoreCase = true) }
    }

    Surface(
        modifier = Modifier.fillMaxSize().background(medtrixaAuraBrush()),
        color = Color.Transparent,
        contentColor = scheme.onBackground
    ) {
        Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()) {
            IdsaHeader(count = results.size, onBackClick = onBackClick)

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(20.dp),
                placeholder = {
                    Text(
                        "Search indication, organism or antibiotic",
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = scheme.primary) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear search")
                        }
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = scheme.surface,
                    unfocusedContainerColor = scheme.surface,
                    focusedBorderColor = scheme.primary,
                    unfocusedBorderColor = scheme.outline.copy(alpha = 0.3f)
                )
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 6.dp)
            ) {
                items(groups) { g ->
                    IdsaChip(
                        label = g,
                        selected = g == group,
                        accent = if (g == "All") scheme.primary else idsaGroupColor(g)
                    ) { group = if (g == group) "All" else g }
                }
            }
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                items(idsaTiers) { t ->
                    IdsaChip(label = t, selected = t == tier, accent = idsaTierColor(t)) {
                        tier = if (t == tier) "All" else t
                    }
                }
            }

            if (!loaded || results.isEmpty()) {
                IdsaEmpty(loaded = loaded, modifier = Modifier.weight(1f))
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(results) { guideline -> IdsaRegimenCard(guideline) }
                    item { IdsaDisclaimer() }
                }
            }
        }
    }
}

@Composable
private fun IdsaHeader(count: Int, onBackClick: () -> Unit) {
    val scheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        scheme.primary.copy(alpha = 0.14f),
                        scheme.primary.copy(alpha = 0.03f)
                    )
                )
            )
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PremiumBackButton(onClick = onBackClick, edgeInset = 0.dp)
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(
                "IDSA AMR Guidance",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = scheme.onBackground
            )
            Text(
                "IDSA 2026 \u00b7 $count regimens",
                fontSize = 12.sp,
                color = scheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun IdsaChip(label: String, selected: Boolean, accent: Color, onClick: () -> Unit) {
    val scheme = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(50)
    Box(
        modifier = Modifier
            .clip(shape)
            .background(if (selected) accent.copy(alpha = 0.16f) else scheme.surface)
            .border(1.dp, if (selected) accent else scheme.outline.copy(alpha = 0.25f), shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            color = if (selected) accent else scheme.onSurfaceVariant,
            maxLines = 1
        )
    }
}

@Composable
private fun IdsaPill(text: String, color: Color, strong: Boolean = false) {
    Text(
        text,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.6.sp,
        color = color,
        maxLines = 1,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = if (strong) 0.20f else 0.12f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

@Composable
private fun idsaGroupColor(group: String): Color = when (group) {
    "ESBL-E" -> auraAccent(0)
    "AmpC-E" -> auraAccent(1)
    "CRE" -> auraAccent(2)
    "DTR P. aeruginosa" -> auraAccent(3)
    "CRAB" -> auraAccent(4)
    "S. maltophilia" -> auraAccent(5)
    else -> MaterialTheme.colorScheme.secondary
}

@Composable
private fun idsaTierColor(tier: String): Color {
    val dark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    return when (tier) {
        "Preferred" -> if (dark) Color(0xFF66BB6A) else Color(0xFF2E7D32)
        "Avoid" -> if (dark) Color(0xFFEF5350) else Color(0xFFC62828)
        "Alternative" -> auraAccent(2)
        "Step-down" -> auraAccent(1)
        "Reserve" -> auraAccent(3)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IdsaRegimenCard(g: AntibioticDosingGuideline) {
    var expanded by rememberSaveable(g.group, g.indication, g.antibiotic, g.tier) {
        mutableStateOf(false)
    }
    val scheme = MaterialTheme.colorScheme
    val accent = idsaGroupColor(g.group)
    val tierColor = idsaTierColor(g.tier)
    val isAvoid = g.tier == "Avoid"
    val hasDose = g.dose.isNotBlank() && g.dose != "Avoid" && g.dose != "Preserve"
    val archived = g.sourceStatus.contains("ARCHIVED", ignoreCase = true)

    Card(
        onClick = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth().animateContentSize(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = scheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(0.5.dp, accent.copy(alpha = 0.30f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IdsaPill(g.group.ifBlank { "Guideline" }, accent)
                if (archived) IdsaPill("ARCHIVED", scheme.onSurfaceVariant)
                Spacer(Modifier.weight(1f))
                if (g.tier.isNotBlank()) IdsaPill(g.tier.uppercase(), tierColor, strong = true)
            }

            Text(
                g.antibiotic,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                lineHeight = 22.sp
            )
            Text(
                g.indication,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = accent,
                lineHeight = 18.sp
            )

            if (hasDose) {
                IdsaDoseBlock(g, accent)
            } else if (g.notes.isNotBlank()) {
                val c = if (isAvoid) idsaTierColor("Avoid") else accent
                Text(
                    g.notes,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(c.copy(alpha = 0.09f))
                        .padding(14.dp)
                )
            }

            if (expanded) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    IdsaDetail("Organisms", g.organisms)
                    IdsaDetail("Setting", g.setting)
                    IdsaDetail("When to use", g.patientGroup)
                    IdsaDetail("Duration", g.duration)
                    IdsaDetail("Renal adjustment", g.renalAdjustment)
                    IdsaDetail("Hepatic adjustment", g.hepaticAdjustment)
                    IdsaDetail("Alternatives", g.alternatives)
                    if (hasDose) IdsaDetail("Notes", g.notes)
                    Text(
                        "Source: ${g.source}",
                        fontSize = 11.sp,
                        color = scheme.onSurfaceVariant
                    )
                    Text(
                        "Status: ${g.sourceStatus} \u00b7 Reviewed: ${g.reviewDate}",
                        fontSize = 11.sp,
                        color = scheme.onSurfaceVariant
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    if (expanded) "Hide details" else "Tap for details",
                    fontSize = 11.sp,
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

@Composable
private fun IdsaDoseBlock(g: AntibioticDosingGuideline, accent: Color) {
    val lines = g.dose.split(" | ").map { it.trim() }.filter { it.isNotEmpty() }
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
            if (g.route.isNotBlank() && g.route != "-") IdsaPill(g.route, accent, strong = true)
        }
        lines.forEach { line ->
            Text(line, fontSize = 16.sp, lineHeight = 22.sp, fontWeight = FontWeight.Bold)
        }
        if (g.frequency.isNotBlank() && g.frequency != "-") {
            Text(
                g.frequency,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun IdsaDetail(label: String, value: String) {
    if (value.isBlank() || value == "-") return
    Column {
        Text(
            label.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(value, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun IdsaEmpty(loaded: Boolean, modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    Column(
        modifier = modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (!loaded) {
            CircularProgressIndicator(strokeWidth = 3.dp, modifier = Modifier.size(32.dp))
            Spacer(Modifier.height(12.dp))
            Text("Loading regimens\u2026", color = scheme.onSurfaceVariant)
        } else {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                tint = scheme.primary,
                modifier = Modifier.size(36.dp)
            )
            Spacer(Modifier.height(10.dp))
            Text("No matching regimens", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(
                "Try another organism, indication or antibiotic.",
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                color = scheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun IdsaDisclaimer() {
    Text(
        "Adult doses for normal renal and hepatic function, current as of 1 March 2026. " +
            "Doses follow IDSA Table 1 and may differ from FDA labeling. Susceptibility must be confirmed. " +
            "This is clinical decision support, not a substitute for clinical judgment, local antibiograms " +
            "or infectious diseases consultation.",
        fontSize = 11.sp,
        lineHeight = 16.sp,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp)
    )
}
