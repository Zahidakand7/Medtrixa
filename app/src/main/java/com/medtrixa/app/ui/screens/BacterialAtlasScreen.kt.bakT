package com.medtrixa.app.ui.screens

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import com.medtrixa.app.ui.theme.auraAccent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medtrixa.app.ui.components.AutoSizeText
import com.medtrixa.app.ui.components.PremiumBackButton
import com.medtrixa.app.ui.theme.atlasHeroBrush
import org.json.JSONArray
import org.json.JSONObject

private val Ink = Color(0xFF1B2733)
private val Muted = Color(0xFF5B6773)
private val atlasGroups = listOf("All", "Gram-positive", "Gram-negative", "Atypical", "Anaerobe", "Acid-fast")

// Same outer spacing on list and detail screens
private val ScreenSide = 16.dp
private val ScreenVertical = 16.dp

private data class AtlasSection(val title: String, val text: String)

private data class Organism(
    val id: String,
    val name: String,
    val group: String,
    val morphology: String,
    val tagline: String,
    val diseases: List<String>,
    val sections: List<AtlasSection>,
    val pearls: List<String>,
    val references: List<String>
)

private fun JSONArray.toStrings(): List<String> = List(length()) { getString(it) }

private fun loadOrganisms(context: Context): List<Organism> = try {
    val text = context.assets.open("bacterial_atlas.json").bufferedReader().use { it.readText() }
    val arr = JSONObject(text).getJSONArray("organisms")
    List(arr.length()) { i ->
        val o = arr.getJSONObject(i)
        val secs = o.getJSONArray("sections")
        Organism(
            id = o.getString("id"),
            name = o.getString("name"),
            group = o.getString("group"),
            morphology = o.getString("morphology"),
            tagline = o.getString("tagline"),
            diseases = o.optJSONArray("diseases")?.toStrings() ?: emptyList(),
            sections = List(secs.length()) { j ->
                val s = secs.getJSONObject(j)
                AtlasSection(s.getString("title"), s.getString("text"))
            },
            pearls = o.optJSONArray("pearls")?.toStrings() ?: emptyList(),
            references = o.optJSONArray("references")?.toStrings() ?: emptyList()
        )
    }
} catch (e: Exception) {
    emptyList()
}

@Composable
private fun groupColor(g: String): Color = when (g) {
    "Gram-positive" -> auraAccent(0, true)
    "Gram-negative" -> auraAccent(1, true)
    "Atypical" -> auraAccent(2, true)
    "Anaerobe" -> auraAccent(3, true)
    "Acid-fast" -> auraAccent(4, true)
    else -> auraAccent(5, true)
}

// "Pseudomonas aeruginosa" -> "P. aeruginosa"
private fun shortName(name: String): String {
    val parts = name.trim().split(" ").filter { it.isNotBlank() }
    return if (parts.size >= 2) "${parts[0].first()}. ${parts.drop(1).joinToString(" ")}" else name
}

@Composable
fun BacterialAtlasScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val organisms = remember { loadOrganisms(context) }
    var selectedId by rememberSaveable { mutableStateOf<String?>(null) }
    val selected = organisms.firstOrNull { it.id == selectedId }

    BackHandler { if (selected != null) selectedId = null else onBackClick() }

    if (selected != null) {
        OrganismDetail(selected) { selectedId = null }
    } else {
        AtlasList(organisms, onBackClick) { selectedId = it.id }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AtlasList(organisms: List<Organism>, onBackClick: () -> Unit, onOpen: (Organism) -> Unit) {
    var query by rememberSaveable { mutableStateOf("") }
    var group by rememberSaveable { mutableStateOf("All") }

    // Only show chips for groups that actually have organisms
    val availableGroups = remember(organisms) {
        listOf("All") + atlasGroups.drop(1).filter { g -> organisms.any { it.group == g } }
    }
    val activeGroup = if (group in availableGroups) group else "All"

    val filtered = organisms.filter { o ->
        (activeGroup == "All" || o.group == activeGroup) &&
            (query.isBlank() ||
                o.name.contains(query, true) ||
                o.morphology.contains(query, true) ||
                o.diseases.any { it.contains(query, true) })
    }

    val side = Modifier.padding(horizontal = ScreenSide)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bacterial Atlas", fontSize = 20.sp, lineHeight = 26.sp, fontWeight = FontWeight.SemiBold) },
                navigationIcon = { PremiumBackButton(onClick = onBackClick, edgeInset = 12.dp) }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(vertical = ScreenVertical),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Box(side) { AtlasHero(total = organisms.size, shown = filtered.size) } }
            item {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    singleLine = true,
                    modifier = side.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    placeholder = { Text("Search organism, disease or shape", maxLines = 1) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
                )
            }
            item {
                // Padding lives inside the row so chips scroll edge to edge and are never clipped
                LazyRow(
                    contentPadding = PaddingValues(horizontal = ScreenSide),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(availableGroups) { g ->
                        val n = if (g == "All") organisms.size else organisms.count { it.group == g }
                        FilterChip(
                            selected = activeGroup == g,
                            onClick = { group = g },
                            label = { Text("$g ($n)", maxLines = 1) }
                        )
                    }
                }
            }
            items(filtered, key = { it.id }) { o -> Box(side) { OrganismCard(o) { onOpen(o) } } }
            if (filtered.isEmpty()) {
                item {
                    Column(
                        modifier = side.fillMaxWidth().padding(top = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = Muted.copy(alpha = 0.6f),
                            modifier = Modifier.size(40.dp)
                        )
                        Text("No matching organisms", color = Muted, fontSize = 15.sp)
                        TextButton(onClick = { query = ""; group = "All" }) { Text("Clear filters") }
                    }
                }
            }
        }
    }
}

@Composable
private fun AtlasHero(total: Int, shown: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(atlasHeroBrush())
            .padding(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                "CLINICAL MICROBIOLOGY",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.4.sp
            )
            AutoSizeText(
                text = "Pathogen-First Reference",
                style = TextStyle(color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            )
            Text(
                "Morphology, virulence, syndromes, diagnosis and evidence-based treatment for each organism.",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 13.sp,
                lineHeight = 18.sp,
                textAlign = TextAlign.Start
            )
            Spacer(Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.20f))
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
                val label = if (shown == total) "$total organisms" else "$shown of $total organisms"
                Text(label, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            }
        }
    }
}

@Composable
private fun Pill(text: String, color: Color, filled: Boolean = false) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (filled) Color.White else color.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OrganismCard(o: Organism, onClick: () -> Unit) {
    val c = groupColor(o.group)
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(6.dp)
                .background(Brush.horizontalGradient(listOf(c, c.copy(alpha = 0.35f))))
        )
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Pill(o.group, c)
            AutoSizeText(
                text = o.name,
                style = TextStyle(color = Ink, fontSize = 19.sp, fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic)
            )
            Text(o.morphology, color = c, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
            Text(
                o.diseases.take(3).joinToString("  |  "),
                color = Muted,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                maxLines = 2
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OrganismDetail(o: Organism, onBack: () -> Unit) {
    val c = groupColor(o.group)
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        shortName(o.name),
                        fontSize = 20.sp,
                        lineHeight = 26.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontStyle = FontStyle.Italic,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = { PremiumBackButton(onClick = onBack, edgeInset = 12.dp) }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(horizontal = ScreenSide, vertical = ScreenVertical),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { DetailHeader(o, c) }
            if (o.diseases.isNotEmpty()) {
                item { SectionCard("Key Diseases", c) { RichText(o.diseases.joinToString("\n") { "• $it" }, c) } }
            }
            items(o.sections) { s -> SectionCard(s.title, c) { RichText(s.text, c) } }
            if (o.pearls.isNotEmpty()) item { PearlsCard(o.pearls) }
            if (o.references.isNotEmpty()) {
                item { SectionCard("References", c) { RichText(o.references.joinToString("\n") { "• $it" }, c) } }
            }
            item {
                Text(
                    "Educational reference. Confirm regimens and doses against current guidelines and local susceptibility data before clinical use.",
                    color = Muted,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}

@Composable
private fun DetailHeader(o: Organism, c: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Brush.linearGradient(listOf(lerp(c, Color.Black, 0.45f), c)))
            .padding(20.dp)
    ) {
        Column {
            Pill(o.group, c, filled = true)
            Spacer(Modifier.height(14.dp))
            AutoSizeText(
                text = o.name,
                style = TextStyle(color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                o.morphology,
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
            Spacer(Modifier.height(14.dp))
            Text(
                o.tagline,
                color = Color.White.copy(alpha = 0.92f),
                fontSize = 14.sp,
                lineHeight = 21.sp,
                textAlign = TextAlign.Start
            )
        }
    }
}

@Composable
private fun SectionCard(title: String, color: Color, content: @Composable () -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.width(4.dp).height(18.dp).clip(RoundedCornerShape(2.dp)).background(color))
                Spacer(Modifier.width(10.dp))
                AutoSizeText(
                    text = title,
                    style = TextStyle(color = Ink, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                )
            }
            content()
        }
    }
}

@Composable
private fun PearlsCard(pearls: List<String>) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF4D6)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("Clinical Pearls", color = Color(0xFFB45F06), fontSize = 17.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
            RichText(pearls.joinToString("\n") { "• $it" }, Color(0xFFB45F06))
        }
    }
}

@Composable
private fun RichText(text: String, dotColor: Color = Ink) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        text.split("\n").filter { it.isNotBlank() }.forEach { line ->
            if (line.startsWith("• ")) {
                // Bullets: always left-aligned, never justified
                Row {
                    Box(Modifier.width(22.dp).padding(top = 7.dp)) {
                        Box(Modifier.size(8.dp).clip(CircleShape).background(dotColor))
                    }
                    Text(
                        line.removePrefix("• "),
                        color = Ink,
                        fontSize = 14.sp,
                        lineHeight = 21.sp,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                // Paragraph style: justified
                Text(line, color = Ink, fontSize = 14.sp, lineHeight = 21.sp, textAlign = TextAlign.Justify)
            }
        }
    }
}
