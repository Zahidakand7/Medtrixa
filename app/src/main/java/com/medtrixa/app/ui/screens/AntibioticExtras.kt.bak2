package com.medtrixa.app.ui.screens

import android.content.Context
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

private val RxInk = Color(0xFF1B2733)
private val RxMuted = Color(0xFF5B6773)

private data class RxSection(val title: String, val text: String)

private data class RxItem(
    val id: String,
    val title: String,
    val category: String,
    val subtitle: String,
    val summary: String,
    val sections: List<RxSection>,
    val source: String
)

private fun loadRx(context: Context, file: String): List<RxItem> = try {
    val text = context.assets.open(file).bufferedReader().use { it.readText() }
    val arr = JSONObject(text).getJSONArray("items")
    List(arr.length()) { i ->
        val o = arr.getJSONObject(i)
        val secs = o.optJSONArray("sections") ?: JSONArray()
        RxItem(
            id = o.optString("id", i.toString()),
            title = o.getString("title"),
            category = o.optString("category", ""),
            subtitle = o.optString("subtitle", ""),
            summary = o.optString("summary", ""),
            sections = List(secs.length()) { j ->
                val s = secs.getJSONObject(j)
                RxSection(s.getString("title"), s.getString("text"))
            },
            source = o.optString("source", "")
        )
    }
} catch (e: Exception) {
    emptyList()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AntibioticExtrasPanel(
    file: String,
    eyebrow: String,
    title: String,
    blurb: String,
    searchHint: String,
    c1: Color,
    c2: Color,
    c3: Color,
    accent: Color,
    tint: Color
) {
    val context = LocalContext.current
    val loaded by produceState<List<RxItem>?>(initialValue = null, key1 = file) {
        value = withContext(Dispatchers.IO) { loadRx(context, file) }
    }
    var query by rememberSaveable(file) { mutableStateOf("") }
    var category by rememberSaveable(file) { mutableStateOf("All") }

    val all = loaded ?: emptyList()
    val categories = remember(all) {
        listOf("All") + all.map { it.category }.filter { it.isNotBlank() }.distinct()
    }
    val shown = remember(all, query, category) {
        val q = query.trim().lowercase()
        all.filter { it0 ->
            (category == "All" || it0.category == category) &&
                (q.isEmpty() ||
                    it0.title.lowercase().contains(q) ||
                    it0.subtitle.lowercase().contains(q) ||
                    it0.summary.lowercase().contains(q) ||
                    it0.category.lowercase().contains(q) ||
                    it0.sections.any { s -> s.text.lowercase().contains(q) })
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 12.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Box(Modifier.padding(horizontal = 16.dp)) {
                val countText = if (shown.size == all.size) "${all.size} entries" else "${shown.size} of ${all.size} entries"
                RxHero(eyebrow, title, blurb, countText, c1, c2, c3, accent)
            }
        }
        item {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(28.dp),
                placeholder = { Text(searchHint, maxLines = 1) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
            )
        }
        if (categories.size > 2) {
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { c ->
                        FilterChip(
                            selected = category == c,
                            onClick = { category = c },
                            label = { Text(c, fontSize = 13.sp, fontWeight = FontWeight.Medium, maxLines = 1) },
                            shape = RoundedCornerShape(50),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = tint.copy(alpha = 0.18f),
                                selectedLabelColor = tint
                            )
                        )
                    }
                }
            }
        }
        if (loaded == null) {
            item { Text("Loading\u2026", color = RxMuted, modifier = Modifier.padding(horizontal = 16.dp)) }
        } else if (shown.isEmpty()) {
            item {
                val msg = if (all.isEmpty()) "Entries for this section are being added." else "No matching entries."
                Text(msg, color = RxMuted, modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp))
            }
        }
        items(shown, key = { it.id }) { item -> RxCard(item, tint) }
        item {
            Text(
                "Educational reference. Verify doses against the current product label, local antimicrobial guidelines and the patient's renal and hepatic function before use.",
                color = RxMuted,
                fontSize = 11.sp,
                lineHeight = 16.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
private fun RxHero(
    eyebrow: String,
    title: String,
    blurb: String,
    count: String,
    c1: Color,
    c2: Color,
    c3: Color,
    accent: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Brush.linearGradient(listOf(c1, c2, c3)))
            .padding(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(eyebrow, color = accent, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.4.sp)
            Text(title, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(blurb, color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp, lineHeight = 18.sp)
            Spacer(Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(accent.copy(alpha = 0.20f))
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
                Text(count, color = accent, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            }
        }
    }
}

@Composable
private fun RxCard(item: RxItem, tint: Color) {
    var expanded by rememberSaveable(item.id) { mutableStateOf(false) }
    Card(
        modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth().animateContentSize(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, tint.copy(alpha = 0.30f))
    ) {
        Column(Modifier.clickable { expanded = !expanded }.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (item.category.isNotBlank()) RxPill(item.category, tint)
                    Text(item.title, color = RxInk, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    if (item.subtitle.isNotBlank()) {
                        Text(item.subtitle, color = tint, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
                Icon(
                    if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = RxMuted
                )
            }
            if (item.summary.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    item.summary,
                    color = RxMuted,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    maxLines = if (expanded) Int.MAX_VALUE else 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (expanded) {
                item.sections.forEach { s ->
                    Spacer(Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.width(4.dp).height(16.dp).clip(RoundedCornerShape(2.dp)).background(tint))
                        Spacer(Modifier.width(10.dp))
                        Text(s.title, color = RxInk, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(Modifier.height(10.dp))
                    RxText(s.text, tint)
                }
                if (item.source.isNotBlank()) {
                    Spacer(Modifier.height(14.dp))
                    Text("Source: " + item.source, color = RxMuted, fontSize = 11.sp, lineHeight = 16.sp)
                }
            }
        }
    }
}

@Composable
private fun RxPill(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
    }
}

@Composable
private fun RxText(text: String, tint: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        text.split("\n").filter { it.isNotBlank() }.forEach { line ->
            if (line.startsWith("\u2022 ")) {
                Row {
                    Box(Modifier.width(20.dp).padding(top = 7.dp)) {
                        Box(Modifier.size(8.dp).clip(CircleShape).background(tint))
                    }
                    Text(
                        line.removePrefix("\u2022 "),
                        color = RxInk,
                        fontSize = 14.sp,
                        lineHeight = 21.sp,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                Text(line, color = RxInk, fontSize = 14.sp, lineHeight = 21.sp, textAlign = TextAlign.Justify)
            }
        }
    }
}
