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
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.medtrixa.app.data.model.ContraindicationResult
import com.medtrixa.app.data.repository.ContraindicationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.TimeoutCancellationException

@Composable
fun ContraindicationCheckerScreen(onBackClick: () -> Unit) {
    BackHandler { onBackClick() }

    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }
    var loadError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            withTimeout(15_000L) {
                withContext(Dispatchers.IO) {
                    ContraindicationRepository.load(context.applicationContext)
                }
            }
        } catch (e: TimeoutCancellationException) {
            loadError = "Timed out loading contraindication data (over 15s). The dataset file may be too large or corrupted."
        } catch (e: Exception) {
            loadError = e.message ?: "Couldn't load contraindication data"
        } finally {
            isLoading = false
        }
    }

    var drugInput by remember { mutableStateOf(TextFieldValue("")) }
    var showSuggestions by remember { mutableStateOf(false) }
    var checked by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<ContraindicationResult?>(null) }
    var closeMatches by remember { mutableStateOf<List<String>>(emptyList()) }

    fun combinedSuggestions(query: String): List<String> {
        return ContraindicationRepository.searchNames(query, limit = 6)
    }

    val suggestions = remember(drugInput.text, showSuggestions) {
        if (showSuggestions) {
            combinedSuggestions(drugInput.text).filter {
                !it.equals(drugInput.text.trim(), ignoreCase = true)
            }
        } else emptyList()
    }

    fun runCheck() {
        checked = true
        val trimmed = drugInput.text.trim()
        result = ContraindicationRepository.getContraindications(trimmed)
        closeMatches = if (result == null) combinedSuggestions(trimmed).take(3) else emptyList()
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
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "Contraindication Checker",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (isLoading) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Loading contraindication data…", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                return@Column
            }

            if (loadError != null) {
                Text(
                    "Couldn't load the contraindication database: $loadError",
                    color = MaterialTheme.colorScheme.error
                )
                return@Column
            }

            ContraindicationDrugField(
                label = "Drug name",
                value = drugInput,
                onValueChange = {
                    val textChanged = it.text != drugInput.text
                    drugInput = it
                    if (textChanged) {
                        showSuggestions = true
                        checked = false
                    }
                },
                suggestions = suggestions,
                onSuggestionSelected = { name ->
                    drugInput = TextFieldValue(text = name, selection = TextRange(name.length))
                    showSuggestions = false
                }
            )

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = {
                    showSuggestions = false
                    runCheck()
                },
                enabled = drugInput.text.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Check contraindications")
            }

            Spacer(modifier = Modifier.height(18.dp))

            if (checked) {
                if (result != null) {
                    ContraindicationResultCard(result!!)
                } else {
                    CompactNoContraindicationCard(drugInput.text.trim())
                    if (closeMatches.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        ContraindicationSuggestionsCard(drugInput.text.trim(), closeMatches)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                "Reference only, based on openFDA drug label data. Not a substitute for clinical judgment or a full prescribing reference.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ContraindicationDrugField(
    label: String,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
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

@Composable
private fun ContraindicationResultCard(result: ContraindicationResult) {
    val text = result.contraindications.trim()
    val isNoneOnFile = text.isEmpty() || text.equals("none", ignoreCase = true) || text.equals("none.", ignoreCase = true)

    val cardContainerColor = if (isNoneOnFile) MaterialTheme.colorScheme.surfaceVariant else Color(0xFFFCEBEB)
    val contentColor = if (isNoneOnFile) MaterialTheme.colorScheme.onSurfaceVariant else Color(0xFF791F1F)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = cardContainerColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxWidth()) {
                Icon(
                    imageVector = Icons.Outlined.WarningAmber,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(result.genericName, fontWeight = FontWeight.Bold, color = contentColor)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                if (isNoneOnFile) "None found." else text,
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Justify
            )
            if (result.status.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Status: ${result.status}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun CompactNoContraindicationCard(drugName: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "No contraindication record found for \"$drugName\" in this dataset.",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "That means it isn't in the dataset, not that no contraindications exist.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ContraindicationSuggestionsCard(drugName: String, closeMatches: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Did you mean (for \"$drugName\"): ${closeMatches.joinToString(", ")}?",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
