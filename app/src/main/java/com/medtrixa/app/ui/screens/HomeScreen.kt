package com.medtrixa.app.ui.screens

import androidx.compose.material.icons.outlined.VerifiedUser
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Lightbulb
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocalPharmacy
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.medtrixa.app.data.db.AppDatabase
import com.medtrixa.app.data.db.SearchHit
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Feedback
import androidx.compose.material.icons.outlined.Lock

@Composable
fun HomeScreen(
    onHomeClick: () -> Unit = {},
    onToolsClick: () -> Unit = {},
    onMoreClick: () -> Unit = {},
    onCalculateClick: () -> Unit = {},
    onAssessClick: () -> Unit = {},
    onDrugToolsClick: () -> Unit = {},
    onReferenceClick: () -> Unit = {},
    onAiInsightsClick: () -> Unit = {},
    onThemesClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onPremiumClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onCheckUpdatesClick: () -> Unit = {},
    onHelpGuideClick: () -> Unit = {},
    onClinicalSourcesClick: () -> Unit = {},
    onFeedbackClick: () -> Unit = {},
    onAboutClick: () -> Unit = {},
    onPrivacyClick: () -> Unit = {},
    onDrugClick: (String) -> Unit = {}
) {
    var searchField by remember { mutableStateOf(TextFieldValue("")) }
    val searchQuery = searchField.text
    val context = LocalContext.current
    val voiceLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spoken = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
            if (!spoken.isNullOrBlank()) searchField = TextFieldValue(spoken, TextRange(spoken.length))
        }
    }
    var hits by remember { mutableStateOf<List<SearchHit>>(emptyList()) }
    val q = searchQuery.trim()

    LaunchedEffect(q) {
        hits = if (q.isNotEmpty()) {
            val found = AppDatabase.getInstance(context).drugDao().search(q)
            if (q.length == 1) {
                // Single letter: only names starting with it, in alphabetical order
                found
                    .filter { it.name.startsWith(q, ignoreCase = true) }
                    .sortedBy { it.name.lowercase() }
            } else {
                found
            }
        } else {
            emptyList()
        }
    }
    var overflowExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.06f)
                            .compositeOver(MaterialTheme.colorScheme.background),
                        MaterialTheme.colorScheme.background
                    )
                )
            ),
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    top = 22.dp,
                    bottom = 24.dp
                ),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.03f)
                                    )
                                ),
                                RoundedCornerShape(24.dp)
                            )
                            .padding(horizontal = 20.dp, vertical = 18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text(
                                text = "Medtrixa",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "A Companion for Healthcare Professionals",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Box {
                            IconButton(
                                onClick = { overflowExpanded = true },
                                modifier = Modifier.offset(y = (-12).dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.MoreHoriz,
                                    contentDescription = "More options",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            DropdownMenu(
                                expanded = overflowExpanded,
                                onDismissRequest = { overflowExpanded = false },
                                containerColor = MaterialTheme.colorScheme.surface
                            ) {
                                Text(
                                    "Personalize",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                                )
                                DropdownMenuItem(
                                    text = { Text("Themes") },
                                    leadingIcon = { Icon(Icons.Outlined.Palette, contentDescription = null) },
                                    onClick = { overflowExpanded = false; onThemesClick() }
                                )
                                DropdownMenuItem(
                                    text = { Text("Notifications") },
                                    leadingIcon = { Icon(Icons.Outlined.Notifications, contentDescription = null) },
                                    onClick = { overflowExpanded = false; onNotificationsClick() }
                                )
                                HorizontalDivider()
                                Text(
                                    "Medtrixa",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                                )
                                DropdownMenuItem(
                                    text = { Text("Upgrade to Premium") },
                                    leadingIcon = { Icon(Icons.Outlined.WorkspacePremium, contentDescription = null) },
                                    onClick = { overflowExpanded = false; onPremiumClick() }
                                )
                                DropdownMenuItem(
                                    text = { Text("Settings") },
                                    leadingIcon = { Icon(Icons.Outlined.Settings, contentDescription = null) },
                                    onClick = { overflowExpanded = false; onSettingsClick() }
                                )
                                DropdownMenuItem(
                                    text = { Text("Check for Updates") },
                                    leadingIcon = { Icon(Icons.Outlined.Refresh, contentDescription = null) },
                                    onClick = { overflowExpanded = false; onCheckUpdatesClick() }
                                )
                                HorizontalDivider()
                                Text(
                                    "Support and Information",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                                )
                                DropdownMenuItem(
                                    text = { Text("Help and Guide") },
                                    leadingIcon = { Icon(Icons.Outlined.HelpOutline, contentDescription = null) },
                                    onClick = { overflowExpanded = false; onHelpGuideClick() }
                                )
                                DropdownMenuItem(
                                    text = { Text("Clinical Sources") },
                                    leadingIcon = { Icon(Icons.Outlined.MenuBook, contentDescription = null) },
                                    onClick = { overflowExpanded = false; onClinicalSourcesClick() }
                                )
                                DropdownMenuItem(
                                    text = { Text("Feedback and Report an Issue") },
                                    leadingIcon = { Icon(Icons.Outlined.Feedback, contentDescription = null) },
                                    onClick = { overflowExpanded = false; onFeedbackClick() }
                                )
                                DropdownMenuItem(
                                    text = { Text("About Medtrixa") },
                                    leadingIcon = { Icon(Icons.Outlined.Info, contentDescription = null) },
                                    onClick = { overflowExpanded = false; onAboutClick() }
                                )
                                DropdownMenuItem(
                                    text = { Text("Privacy and Disclaimer") },
                                    leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                                    onClick = { overflowExpanded = false; onPrivacyClick() }
                                )
                            }
                        }
                    }
                }

                item {
                    Column {
                        OutlinedTextField(
                            value = searchField,
                            onValueChange = { searchField = it },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Search,
                                    contentDescription = "Search"
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = {
                                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                        putExtra(
                                            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                                        )
                                    }
                                    try {
                                        voiceLauncher.launch(intent)
                                    } catch (e: ActivityNotFoundException) {
                                        Toast.makeText(context, "Voice input is not available on this device", Toast.LENGTH_SHORT).show()
                                    }
                                }) {
                                    Icon(
                                        imageVector = Icons.Outlined.Mic,
                                        contentDescription = "Voice search"
                                    )
                                }
                            },
                            placeholder = {
                                Text("Search Generic Name")
                            }
                        )
                    }
                }

                if (q.isNotEmpty()) {
                    if (hits.isEmpty()) {
                        item {
                            Text(
                                text = "No matches. Try a generic or brand name.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        items(hits, key = { it.rxcui }) { hit ->
                            DrugResultCard(hit, q) { onDrugClick(hit.name) }
                        }
                    }
                }

                item {
                    Text(
                        text = "Quick Access",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        QuickAccessCard(
                            modifier = Modifier.weight(1f),
                            title = "Calculate",
                            subtitle = "Medical Calculators",
                            icon = Icons.Outlined.Calculate,
                            onClick = onCalculateClick
                        )

                        QuickAccessCard(
                            modifier = Modifier.weight(1f),
                            title = "Assess",
                            subtitle = "Clinical Scores",
                            icon = Icons.Outlined.Assessment,
                            onClick = onAssessClick
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        QuickAccessCard(
                            modifier = Modifier.weight(1f),
                            title = "Drug Tools",
                            subtitle = "Dosing & More",
                            icon = Icons.Outlined.LocalPharmacy,
                            onClick = onDrugToolsClick
                        )

                        QuickAccessCard(
                            modifier = Modifier.weight(1f),
                            title = "Reference",
                            subtitle = "Guidelines & Sources",
                            icon = Icons.Outlined.MenuBook,
                            onClick = onReferenceClick
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        QuickAccessCard(
                            modifier = Modifier.weight(1f),
                            title = "AI Insights",
                            subtitle = "Ask Anything",
                            icon = Icons.Outlined.AutoAwesome,
                            onClick = onAiInsightsClick
                        )

                        QuickAccessCard(
                            modifier = Modifier.weight(1f),
                            title = "Healthcare Insights",
                            subtitle = "Facts & Pearls",
                            icon = Icons.Outlined.Lightbulb
                        )
                    }
                }

                item {
                    SectionHeader("Recently Used")

                    Spacer(modifier = Modifier.height(10.dp))

                    EmptyStateCard(
                        text = "Your recent tools will appear here"
                    )
                }

                item {
                    SectionHeader("Favorites")

                    Spacer(modifier = Modifier.height(10.dp))

                    EmptyStateCard(
                        text = "Save your frequently used tools here"
                    )
                }
            }

            HomeBottomNavigation(
                onHomeClick = onHomeClick,
                onToolsClick = onToolsClick,
                onMoreClick = onMoreClick
            )
        }
    }
}

@Composable
private fun QuickAccessCard(
    modifier: Modifier,
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .height(132.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun EmptyStateCard(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.FavoriteBorder,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun HomeBottomNavigation(
    onHomeClick: () -> Unit,
    onToolsClick: () -> Unit,
    onMoreClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Home",
                modifier = Modifier.clickable { onHomeClick() },
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Tools",
                modifier = Modifier.clickable { onToolsClick() },
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "More",
                modifier = Modifier.clickable { onMoreClick() },
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
