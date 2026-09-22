package com.medtrixa.app.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.foundation.clickable
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import androidx.compose.ui.platform.LocalUriHandler

private data class PubMedResult(
    val pmid: String,
    val title: String,
    val journal: String,
    val pubDate: String
)

private fun searchPubMed(query: String, sortMode: String): List<PubMedResult> {
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
    val results = mutableListOf<PubMedResult>()
    for (i in 0 until idArray.length()) {
        val pmid = idArray.getString(i)
        val item = resultObj.optJSONObject(pmid) ?: continue
        results.add(
            PubMedResult(
                pmid = pmid,
                title = item.optString("title", ""),
                journal = item.optString("fulljournalname", ""),
                pubDate = item.optString("pubdate", "")
            )
        )
    }
    return results
}

@Composable
@OptIn(ExperimentalFoundationApi::class, androidx.compose.material3.ExperimentalMaterial3Api::class)
fun AiInsightsScreen(onBackClick: () -> Unit) {
    val scope = rememberCoroutineScope()

    var query by remember { mutableStateOf("") }
    var provider by remember { mutableStateOf("Groq") }
    var providerExpanded by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var results by remember { mutableStateOf<List<PubMedResult>>(emptyList()) }
    var sortMode by remember { mutableStateOf("Relevance") }

    val providers = listOf("Groq", "Gemini", "PubMed")

    fun runSearch() {
        if (query.isBlank()) return
        error = null
        results = emptyList()
        if (provider != "PubMed") {
            error = "$provider needs your own API key, set up in Settings (coming soon). Try PubMed for now — it needs no key."
            return
        }
        loading = true
        scope.launch {
            try {
                val r = withContext(Dispatchers.IO) { searchPubMed(query, sortMode) }
                results = r
                if (r.isEmpty()) error = "No results found for that query."
            } catch (e: Exception) {
                error = "Couldn't reach PubMed: ${e.message}"
            } finally {
                loading = false
            }
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 12.dp)) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text("AI Insights", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            }

            Text(
                "SOURCE",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            ExposedDropdownMenuBox(
                expanded = providerExpanded,
                onExpandedChange = { providerExpanded = it }
            ) {
                OutlinedTextField(
                    value = provider,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = providerExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = providerExpanded,
                    onDismissRequest = { providerExpanded = false }
                ) {
                    providers.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                provider = option
                                providerExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.padding(top = 16.dp))

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                placeholder = { Text(if (provider == "PubMed") "Search for Citations" else "Ask a clinical question") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.padding(top = 10.dp))

            if (provider == "PubMed") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 10.dp)) {
                    listOf("Relevance", "Newest").forEach { mode ->
                        androidx.compose.material3.FilterChip(
                            selected = sortMode == mode,
                            onClick = { sortMode = mode },
                            label = { Text(mode) }
                        )
                    }
                }
            }

            androidx.compose.material3.Button(
                onClick = { runSearch() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (provider == "PubMed") "Search" else "Ask")
            }

            Spacer(modifier = Modifier.padding(top = 16.dp))

            if (loading) {
                CircularProgressIndicator()
            }

            error?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }

            val uriHandler = LocalUriHandler.current
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(results) { r ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(r.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.padding(top = 4.dp))
                            Text(
                                "${r.journal} — ${r.pubDate}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "https://pubmed.ncbi.nlm.nih.gov/${r.pmid}/",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.clickable {
                                    uriHandler.openUri("https://pubmed.ncbi.nlm.nih.gov/${r.pmid}/")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
