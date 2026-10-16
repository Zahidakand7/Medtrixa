package com.medtrixa.app.ui.screens.drugtools

import androidx.activity.compose.BackHandler
import androidx.compose.ui.unit.sp
import com.medtrixa.app.ui.components.PremiumBackButton
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.medtrixa.app.data.db.AppDatabase
import com.medtrixa.app.data.db.SearchHit

@Composable
fun TherapeuticAlternativesScreen(
    onBackClick: () -> Unit,
    onDrugClick: (SearchHit) -> Unit
) {
    BackHandler { onBackClick() }
    val context = LocalContext.current
    val dao = remember { AppDatabase.getInstance(context).drugDao() }
    var query by remember { mutableStateOf("") }
    var suggestions by remember { mutableStateOf<List<SearchHit>>(emptyList()) }
    var selected by remember { mutableStateOf<SearchHit?>(null) }
    var alternatives by remember { mutableStateOf<List<SearchHit>?>(null) }

    LaunchedEffect(query) {
        val q = query.trim()
        suggestions = if (q.length < 2) emptyList() else dao.search(q)
    }
    LaunchedEffect(selected) {
        val cls = selected?.drugClass
        alternatives = when {
            selected == null -> null
            cls.isNullOrBlank() -> emptyList()
            else -> dao.getSameClass(cls, selected!!.rxcui)
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 22.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PremiumBackButton(onClick = onBackClick)
                    Column {
                        Text("Therapeutic Alternatives", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 26.sp)
                        Text(
                            "Other drugs in the same class",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            item {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { query = "" }) {
                                Icon(Icons.Outlined.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    placeholder = { Text("Search a drug") }
                )
            }
            items(suggestions.take(6), key = { "s" + it.rxcui }) { hit ->
                AltRow(hit.name, hit.drugClass, showArrow = false, onClick = {
                    selected = hit
                    query = ""
                })
            }
            val sel = selected
            if (sel != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                "Selected drug",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                sel.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                sel.drugClass ?: "Class not available",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
                val alts = alternatives
                if (alts == null) {
                    item { Text("Loading...", style = MaterialTheme.typography.bodyMedium) }
                } else if (alts.isEmpty()) {
                    item {
                        Text(
                            if (sel.drugClass.isNullOrBlank()) "No class data is available for this drug."
                            else "No other drugs found in this class.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    item {
                        Text(
                            "${alts.size} alternative(s) in this class",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    items(alts, key = { "a" + it.rxcui }) { hit ->
                        AltRow(hit.name, hit.drugClass, showArrow = true, onClick = { onDrugClick(hit) })
                    }
                    item {
                        Text(
                            "Same class does not mean interchangeable. Check indications, dosing and contraindications before switching.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else if (suggestions.isEmpty()) {
                item {
                    Text(
                        "Search for a drug to see others in its class.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun AltRow(name: String, drugClass: String?, showArrow: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(Modifier.padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(
                    drugClass ?: "Class not available",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (showArrow) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
