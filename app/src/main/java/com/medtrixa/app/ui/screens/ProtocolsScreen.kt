package com.medtrixa.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.ShowChart
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material.icons.outlined.Info
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medtrixa.app.ui.components.PremiumBackButton

private val comingNext = listOf(
    "SBAR Handover",
    "Anaphylaxis", "Stroke", "DKA", "Status epilepticus"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProtocolsScreen(
    onBackClick: () -> Unit,
    onCardiacArrestClick: () -> Unit,
    onPulseClick: () -> Unit,
    onAbgClick: () -> Unit,
    onSepsisClick: () -> Unit,
    onLabClick: () -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary
    val background = MaterialTheme.colorScheme.background

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(primary.copy(alpha = 0.06f).compositeOver(background), background)
                )
            )
    ) {
        Scaffold(
            modifier = Modifier.statusBarsPadding(),
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text("Protocols", fontSize = 20.sp, lineHeight = 26.sp, fontWeight = FontWeight.SemiBold)
                    },
                    navigationIcon = { PremiumBackButton(onClick = onBackClick, edgeInset = 12.dp) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 28.dp)
            ) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    listOf(primary.copy(alpha = 0.14f), primary.copy(alpha = 0.03f))
                                ),
                                RoundedCornerShape(24.dp)
                            )
                            .padding(horizontal = 20.dp, vertical = 18.dp)
                    ) {
                        Text("Act fast. Follow current guidelines.", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Every protocol shows its guideline source and review date.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                item {
                    Text("Core tools", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { onCardiacArrestClick() },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .background(primary.copy(alpha = 0.10f), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Outlined.Favorite, contentDescription = null, tint = primary)
                            }
                            Spacer(Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Cardiac Arrest", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text(
                                    "ACLS timer, drug prompts, event log",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    "AHA 2025",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = primary
                                )
                            }
                        }
                    }
                }

                item {
                    ProtocolRow(
                        title = "Pulse Assessment",
                        subtitle = "Camera-based pulse estimate and waveform",
                        source = "RESEARCH USE ONLY",
                        icon = Icons.Outlined.ShowChart,
                        onClick = onPulseClick
                    )
                }

                item {
                    ProtocolRow(
                        title = "ABG Interpreter",
                        subtitle = "Primary disorder, compensation, anion gap",
                        source = "Physiological approach",
                        icon = Icons.Outlined.Science,
                        onClick = onAbgClick
                    )
                }

                item {
                    ProtocolRow(
                        title = "Sepsis",
                        subtitle = "Time-zero clock, checklist, shock support",
                        source = "SSC 2026",
                        icon = Icons.Outlined.Warning,
                        onClick = onSepsisClick
                    )
                }

                item {
                    ProtocolRow(
                        title = "Lab Interpreter",
                        subtitle = "Check results against reference ranges",
                        source = "ABIM 2026",
                        icon = Icons.Outlined.Info,
                        onClick = onLabClick
                    )
                }

                item {
                    Text("Coming next", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }

                items(comingNext.chunked(2)) { pair ->
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        pair.forEach { name ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f))
                                    .padding(vertical = 14.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    name,
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        if (pair.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun ProtocolRow(
    title: String,
    subtitle: String,
    source: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(primary.copy(alpha = 0.10f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = primary)
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(source, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = primary)
            }
        }
    }
}
