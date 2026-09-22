package com.medtrixa.app.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.medtrixa.app.data.RecentDrugStore
import com.medtrixa.app.data.db.AppDatabase
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.size

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DrugSearchScreen(onBackClick: () -> Unit, onDrugClick: (String) -> Unit) {
    val context = LocalContext.current
    var query by remember { mutableStateOf("") }
    var results by remember { mutableStateOf<List<String>>(emptyList()) }
    var recents by remember { mutableStateOf<List<String>>(emptyList()) }
    var longPressedItem by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        recents = RecentDrugStore.getRecents(context)
    }

    LaunchedEffect(query) {
        results = if (query.isNotEmpty()) {
            AppDatabase.getInstance(context).drugDao().search(query).map { it.genericName }
        } else {
            emptyList()
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Drug Search",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.padding(top = 12.dp))

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                placeholder = { Text("Search Generic Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.padding(top = 20.dp))

            if (query.isEmpty()) {
                if (recents.isNotEmpty()) {
                    Text(
                        text = "RECENTLY VIEWED",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        items(recents, key = { it }) { name ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .combinedClickable(
                                        onClick = { onDrugClick(name) },
                                        onLongClick = { longPressedItem = name }
                                    )
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Filled.History,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(end = 10.dp)
                                )
                                Text(name, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                                if (longPressedItem == name) {
                                    Icon(
                                        Icons.Filled.Close,
                                        contentDescription = "Remove",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .background(Color(0xFFE5484D), CircleShape)
                                            .clickable {
                                                RecentDrugStore.removeRecent(context, name)
                                                recents = RecentDrugStore.getRecents(context)
                                                longPressedItem = null
                                            }
                                            .padding(3.dp)
                                            .size(15.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(results) { genericName ->
                        Card(modifier = Modifier.fillMaxWidth().clickable { onDrugClick(genericName) }) {
                            Text(
                                text = genericName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
