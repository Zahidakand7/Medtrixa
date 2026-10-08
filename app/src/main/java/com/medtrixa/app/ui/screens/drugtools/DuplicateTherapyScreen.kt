package com.medtrixa.app.ui.screens.drugtools

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.medtrixa.app.data.db.AppDatabase
import com.medtrixa.app.data.db.SearchHit

@Composable
fun DuplicateTherapyScreen(onBackClick: () -> Unit) {
    BackHandler { onBackClick() }
    val context = LocalContext.current
    val dao = remember { AppDatabase.getInstance(context).drugDao() }
    var query by remember { mutableStateOf("") }
    var suggestions by remember { mutableStateOf<List<SearchHit>>(emptyList()) }
    val selected = remember { mutableStateListOf<SearchHit>() }

    LaunchedEffect(query) {
        val q = query.trim()
        suggestions = if (q.length < 2) emptyList() else dao.search(q)
    }

    val classified = selected.filter { !it.drugClass.isNullOrBlank() }
    val duplicates = classified.groupBy { it.drugClass!!.trim().lowercase() }.values.filter { it.size > 1 }
    val unclassified = selected.size - classified.size
    val warnBg = Color(0xFFFFF1D6)
    val warnFg = Color(0xFF8A5A00)
    val warnText = Color(0xFF3D2A00)

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 22.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                    Spacer(Modifier.width(4.dp))
                    Column {
                        Text("Duplicate Therapy", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text(
                            "Find overlapping drug classes",
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
                    placeholder = { Text("Add a medication") }
                )
            }
            items(suggestions.take(6), key = { "s" + it.rxcui }) { hit ->
                DupDrugRow(hit.name, hit.drugClass, onClick = {
                    if (selected.none { it.rxcui == hit.rxcui }) selected.add(hit)
                    query = ""
                })
            }
            if (selected.isNotEmpty()) {
                item {
                    Text(
                        "Medication list (${selected.size})",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                items(selected.toList(), key = { "m" + it.rxcui }) { hit ->
                    DupDrugRow(hit.name, hit.drugClass, onClick = null) {
                        IconButton(onClick = { selected.remove(hit) }) {
                            Icon(Icons.Outlined.Close, contentDescription = "Remove")
                        }
                    }
                }
            }
            if (selected.size < 2) {
                item {
                    Text(
                        "Add at least two medications to check for duplicate classes.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                if (duplicates.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text("No duplicate classes found", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                                Text(
                                    "Based on drug class only. Interactions are not checked here.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    items(duplicates, key = { "d" + it.first().rxcui }) { group ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = warnBg)
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text("Possible duplicate therapy", style = MaterialTheme.typography.labelLarge, color = warnFg, fontWeight = FontWeight.SemiBold)
                                Spacer(Modifier.height(4.dp))
                                Text(group.first().drugClass ?: "", style = MaterialTheme.typography.titleMedium, color = warnText, fontWeight = FontWeight.SemiBold)
                                Spacer(Modifier.height(4.dp))
                                Text(group.joinToString(", ") { it.name }, style = MaterialTheme.typography.bodyMedium, color = warnText)
                            }
                        }
                    }
                }
                if (unclassified > 0) {
                    item {
                        Text(
                            "$unclassified selected drug(s) have no class data and were not compared.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                item {
                    Text(
                        "Matching is by drug class. Some same-class combinations are intentional, so use clinical judgement.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun DupDrugRow(
    name: String,
    drugClass: String?,
    onClick: (() -> Unit)?,
    trailing: @Composable () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth().let { if (onClick != null) it.clickable { onClick() } else it },
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
            trailing()
        }
    }
}
