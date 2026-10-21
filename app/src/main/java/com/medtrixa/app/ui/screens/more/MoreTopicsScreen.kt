package com.medtrixa.app.ui.screens.more

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MoreTopicsScreen(
    onHomeClick: () -> Unit,
    onToolsClick: () -> Unit,
    onTopicClick: (String) -> Unit
) {
    BackHandler { onHomeClick() }
    val grouped = MoreTopicRegistry.all.groupBy { it.category }.toSortedMap()
    val count = MoreTopicRegistry.all.size

    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(Modifier.fillMaxSize()) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PremiumBackButton(onClick = onHomeClick)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Topics", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text(
                        count.toString() + (if (count == 1) " quick reference" else " quick references"),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                grouped.forEach { (category, list) ->
                    item(key = "h_" + category) {
                        Text(
                            category,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    items(list, key = { it.id }) { t ->
                        val c = topicAccentColor(t.accent)
                        Card(
                            Modifier.fillMaxWidth().clickable { onTopicClick(t.id) },
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    Modifier.size(44.dp).clip(RoundedCornerShape(12.dp))
                                        .background(Brush.linearGradient(listOf(c, c.copy(alpha = 0.65f)))),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        if (t.badge.isEmpty()) t.title.take(2) else t.badge,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(t.title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                                    Text(
                                        t.subtitle,
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Icon(
                                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
