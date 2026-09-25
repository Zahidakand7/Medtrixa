package com.medtrixa.app.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

// ───────────────────────── Brand colors ─────────────────────────

private val PubMedBlue = Color(0xFF2A6EBB)
private val PubMedBlueLight = Color(0xFFEAF2FB)

// A small, recognizable palette for the most common PubMed publication types;
// anything not in this map just falls back to the brand blue.
private val pubTypeColors = mapOf(
    "Randomized Controlled Trial" to Color(0xFF2E7D32),
    "Meta-Analysis" to Color(0xFF6A1B9A),
    "Systematic Review" to Color(0xFF00838F),
    "Review" to Color(0xFF00838F),
    "Clinical Trial" to Color(0xFF2E7D32),
    "Guideline" to Color(0xFFAD6800),
    "Case Reports" to Color(0xFF8D6E63)
)

private fun colorForPubType(type: String): Color = pubTypeColors[type] ?: PubMedBlue

// ───────────────────────── Data + network ─────────────────────────

private data class PubMedArticle(
    val pmid: String,
    val title: String,
    val journal: String,
    val pubDate: String,
    val authors: List<String> = emptyList(),
    val pubTypes: List<String> = emptyList()
)

private fun searchPubMed(query: String, sortMode: String): List<PubMedArticle> {
    val encoded = URLEncoder.encode(query, "UTF-8")
    val sortParam = if (sortMode == "Newest") "pub_date" else "relevance"
    val searchUrl = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&term=$encoded&retmax=8&sort=$sortParam&retmode=json"

    val searchConn = URL(searchUrl).openConnection() as HttpURLConnection
    searchConn.connectTimeout = 10000
    searchConn.readTimeout = 10000
    val searchJson = JSONObject(searchConn.inputStream.bufferedReader().readText())
    searchConn.disconnect()

    val idArray = searchJson.getJSONObject("esearchresult").getJSONArray("idlist")
    if (idArray.length() == 0) return emptyList()

    val ids = (0 until idArray.length()).joinToString(",") { idArray.getString(it) }
    val summaryUrl = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db=pubmed&id=$ids&retmode=json"

    val summaryConn = URL(summaryUrl).openConnection() as HttpURLConnection
    summaryConn.connectTimeout = 10000
    summaryConn.readTimeout = 10000
    val summaryJson = JSONObject(summaryConn.inputStream.bufferedReader().readText())
    summaryConn.disconnect()

    val resultObj = summaryJson.getJSONObject("result")
    val results = mutableListOf<PubMedArticle>()
    for (i in 0 until idArray.length()) {
        val pmid = idArray.getString(i)
        val item = resultObj.optJSONObject(pmid) ?: continue

        val authorsArr = item.optJSONArray("authors")
        val authors = mutableListOf<String>()
        if (authorsArr != null) {
            for (a in 0 until authorsArr.length()) {
                authorsArr.optJSONObject(a)?.optString("name", "")?.let { name ->
                    if (name.isNotBlank()) authors.add(name)
                }
            }
        }

        val pubTypeArr = item.optJSONArray("pubtype")
        val pubTypes = mutableListOf<String>()
        if (pubTypeArr != null) {
            for (t in 0 until pubTypeArr.length()) {
                val type = pubTypeArr.optString(t, "")
                if (type.isNotBlank() && type != "Journal Article") pubTypes.add(type)
            }
        }

        results.add(
            PubMedArticle(
                pmid = pmid,
                title = item.optString("title", ""),
                journal = item.optString("fulljournalname", ""),
                pubDate = item.optString("pubdate", ""),
                authors = authors,
                pubTypes = pubTypes
            )
        )
    }
    return results
}

private val pubMedSuggestions = listOf(
    "SGLT2 inhibitors in heart failure",
    "Sepsis fluid resuscitation",
    "Anticoagulation in atrial fibrillation",
    "GLP-1 agonists cardiovascular outcomes",
    "Early mobilization in ICU",
    "Statin therapy in primary prevention"
)

// ───────────────────────── Screen ─────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PubMedScreen(onBackClick: () -> Unit) {
    var query by remember { mutableStateOf("") }
    var sortMode by remember { mutableStateOf("Relevance") }
    var results by remember { mutableStateOf<List<PubMedArticle>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var hasSearched by remember { mutableStateOf(false) }
    var lastQuery by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current
    val suggestions = remember { pubMedSuggestions.shuffled().take(4) }

    fun runSearch(text: String) {
        if (text.isBlank()) return
        query = text
        lastQuery = text
        isLoading = true
        error = null
        hasSearched = true
        scope.launch {
            try {
                val r = withContext(Dispatchers.IO) { searchPubMed(text, sortMode) }
                results = r
            } catch (e: Exception) {
                error = "Couldn't reach PubMed: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("PubMed", fontWeight = FontWeight.Bold)
                        Text(
                            "NCBI biomedical literature",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PubMedBlueLight
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = PubMedBlue) },
                placeholder = { Text("Search Medical Literature") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { runSearch(query) })
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = sortMode == "Relevance",
                    onClick = {
                        sortMode = "Relevance"
                        if (hasSearched) runSearch(lastQuery)
                    },
                    label = { Text("Relevance") }
                )
                FilterChip(
                    selected = sortMode == "Newest",
                    onClick = {
                        sortMode = "Newest"
                        if (hasSearched) runSearch(lastQuery)
                    },
                    label = { Text("Newest") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PubMedBlue)
                    }
                }

                error != null -> {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.errorContainer
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                error ?: "",
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            TextButton(
                                onClick = { runSearch(lastQuery) }
                            ) { Text("Retry", fontWeight = FontWeight.SemiBold) }
                        }
                    }
                }

                !hasSearched -> {
                    Column {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            color = PubMedBlueLight
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    "What do you need to know?",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = PubMedBlue
                                )
                                Text(
                                    "Search millions of peer-reviewed articles from PubMed/MEDLINE, straight from NCBI. No API key needed.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            "Try asking",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            suggestions.forEach { suggestion ->
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable { runSearch(suggestion) },
                                    shape = RoundedCornerShape(14.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Icon(
                                            Icons.Filled.Search,
                                            contentDescription = null,
                                            tint = PubMedBlue,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(suggestion, style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                            }
                        }
                    }
                }

                results.isEmpty() -> {
                    Text(
                        "No results found for \"$lastQuery\". Try a different search.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                else -> {
                    Text(
                        "${results.size} results · sorted by ${sortMode.lowercase()}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = PubMedBlue
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        itemsIndexed(results) { index, r ->
                            PubMedResultCard(index + 1, r) {
                                uriHandler.openUri("https://pubmed.ncbi.nlm.nih.gov/${r.pmid}/")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PubMedResultCard(index: Int, r: PubMedArticle, onOpen: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .clickable(onClick = onOpen),
        shape = RoundedCornerShape(22.dp),
        color = PubMedBlueLight
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier.size(28.dp).background(PubMedBlue.copy(alpha = 0.18f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "$index",
                        style = MaterialTheme.typography.labelMedium,
                        color = PubMedBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    r.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
            }

            if (r.pubTypes.isNotEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    r.pubTypes.take(2).forEach { type ->
                        val color = colorForPubType(type)
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = color.copy(alpha = 0.14f)
                        ) {
                            Text(
                                type,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = color,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            if (r.authors.isNotEmpty()) {
                val authorLine = if (r.authors.size > 3) {
                    r.authors.take(3).joinToString(", ") + ", et al."
                } else {
                    r.authors.joinToString(", ")
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(
                        Icons.Outlined.Groups,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        authorLine,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (r.journal.isNotBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(
                        Icons.AutoMirrored.Outlined.MenuBook,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        r.journal,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (r.pubDate.isNotBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(
                        Icons.Outlined.CalendarToday,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        r.pubDate,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(
                    Icons.AutoMirrored.Outlined.OpenInNew,
                    contentDescription = null,
                    tint = PubMedBlue,
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    "Open in PubMed · PMID ${r.pmid}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = PubMedBlue
                )
            }
        }
    }
}
