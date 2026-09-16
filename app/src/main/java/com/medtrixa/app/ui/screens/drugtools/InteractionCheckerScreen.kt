package com.medtrixa.app.ui.screens.drugtools

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.medtrixa.app.data.model.DrugInteractionResult
import com.medtrixa.app.data.repository.DrugInteractionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun InteractionCheckerScreen(onBackClick: () -> Unit) {
    BackHandler { onBackClick() }

    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }
    var loadError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            withContext(Dispatchers.IO) {
                DrugInteractionRepository.load(context.applicationContext)
            }
        } catch (e: Exception) {
            loadError = e.message ?: "Couldn't load interaction data"
        } finally {
            isLoading = false
        }
    }

    var drugAInput by remember { mutableStateOf("") }
    var drugBInput by remember { mutableStateOf("") }
    var showSuggestionsA by remember { mutableStateOf(false) }
    var showSuggestionsB by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<DrugInteractionResult?>(null) }
    var checked by remember { mutableStateOf(false) }
    var closeMatchesA by remember { mutableStateOf<List<String>>(emptyList()) }
    var closeMatchesB by remember { mutableStateOf<List<String>>(emptyList()) }

    val suggestionsA = remember(drugAInput, showSuggestionsA) {
        if (showSuggestionsA) DrugInteractionRepository.searchNames(drugAInput, limit = 6) else emptyList()
    }
    val suggestionsB = remember(drugBInput, showSuggestionsB) {
        if (showSuggestionsB) DrugInteractionRepository.searchNames(drugBInput, limit = 6) else emptyList()
    }

    fun runCheck() {
        checked = true
        val exactA = DrugInteractionRepository.isKnownDrug(drugAInput)
        val exactB = DrugInteractionRepository.isKnownDrug(drugBInput)
        result = DrugInteractionRepository.checkInteraction(drugAInput, drugBInput)
        closeMatchesA = if (!exactA) DrugInteractionRepository.searchNames(drugAInput, limit = 3) else emptyList()
        closeMatchesB = if (!exactB) DrugInteractionRepository.searchNames(drugBInput, limit = 3) else emptyList()
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "Drug Interaction Checker",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (isLoading) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Loading interaction data…", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                return@Column
            }

            if (loadError != null) {
                Text(
                    "Couldn't load the interaction database: $loadError",
                    color = MaterialTheme.colorScheme.error
                )
                return@Column
            }

            DrugField(
                label = "First drug",
                value = drugAInput,
                onValueChange = {
                    drugAInput = it
                    showSuggestionsA = true
                    checked = false
                },
                suggestions = suggestionsA,
                onSuggestionSelected = {
                    drugAInput = it
                    showSuggestionsA = false
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            DrugField(
                label = "Second drug",
                value = drugBInput,
                onValueChange = {
                    drugBInput = it
                    showSuggestionsB = true
                    checked = false
                },
                suggestions = suggestionsB,
                onSuggestionSelected = {
                    drugBInput = it
                    showSuggestionsB = false
                }
            )

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = {
                    showSuggestionsA = false
                    showSuggestionsB = false
                    runCheck()
                },
                enabled = drugAInput.isNotBlank() && drugBInput.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Check interaction")
            }

            Spacer(modifier = Modifier.height(18.dp))

            if (checked) {
                val currentResult = result
                if (currentResult != null) {
                    InteractionResultCard(currentResult)
                } else {
                    NoInteractionCard(
                        drugA = drugAInput,
                        drugB = drugBInput,
                        closeMatchesA = closeMatchesA,
                        closeMatchesB = closeMatchesB
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                "Reference only, based on the DDInter interaction dataset. Not a substitute for clinical judgment or a full prescribing reference.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DrugField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    suggestions: List<String>,
    onSuggestionSelected: (String) -> Unit
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        if (suggestions.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column {
                    suggestions.forEach { name ->
                        Text(
                            text = name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSuggestionSelected(name) }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

private data class SeverityStyle(
    val containerColor: Color,
    val contentColor: Color,
    val label: String
)

private fun severityStyle(level: String): SeverityStyle = when (level.trim().lowercase()) {
    "major" -> SeverityStyle(Color(0xFFFCEBEB), Color(0xFF791F1F), "Major interaction")
    "moderate" -> SeverityStyle(Color(0xFFFAEEDA), Color(0xFF854F0B), "Moderate interaction")
    "minor" -> SeverityStyle(Color(0xFFEAF3DE), Color(0xFF3B6D11), "Minor interaction")
    else -> SeverityStyle(Color(0xFFF1EFE8), Color(0xFF444441), "Unknown severity")
}

private fun severityAdvice(level: String): String = when (level.trim().lowercase()) {
    "major" -> "Use caution. Monitor closely or consult a full reference for management."
    "moderate" -> "Monitor for effect. Consider dose or timing adjustment where appropriate."
    "minor" -> "Generally low clinical significance, but be aware of the interaction."
    else -> "Severity not classified in this dataset."
}

@Composable
private fun InteractionResultCard(result: DrugInteractionResult) {
    val style = severityStyle(result.level)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = style.containerColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.WarningAmber,
                    contentDescription = null,
                    tint = style.contentColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(style.label, fontWeight = FontWeight.Bold, color = style.contentColor)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "${result.drugA} + ${result.drugB}",
                style = MaterialTheme.typography.bodyMedium,
                color = style.contentColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                severityAdvice(result.level),
                style = MaterialTheme.typography.bodySmall,
                color = style.contentColor
            )
        }
    }
}

@Composable
private fun NoInteractionCard(
    drugA: String,
    drugB: String,
    closeMatchesA: List<String>,
    closeMatchesB: List<String>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "No recorded interaction between \"$drugA\" and \"$drugB\" in this dataset.",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "That means it isn't in the dataset, not that no interaction exists.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (closeMatchesA.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Did you mean (for \"$drugA\"): ${closeMatchesA.joinToString(", ")}?",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            if (closeMatchesB.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Did you mean (for \"$drugB\"): ${closeMatchesB.joinToString(", ")}?",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
