package com.medtrixa.app.ui.screens.drugtools

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medtrixa.app.data.model.DrugInteractionResult
import com.medtrixa.app.data.repository.DrugInteractionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.TimeoutCancellationException

private data class PairCheckResult(
    val ingredientA: String,
    val ingredientB: String,
    val interaction: DrugInteractionResult?
)

@Composable
fun InteractionCheckerScreen(onBackClick: () -> Unit) {
    BackHandler { onBackClick() }

    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }
    var loadError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            withTimeout(15_000L) {
                withContext(Dispatchers.IO) {
                    DrugInteractionRepository.load(context.applicationContext)
                }
            }
        } catch (e: TimeoutCancellationException) {
            loadError = "Timed out loading interaction data (over 15s). The dataset file may be too large or corrupted."
        } catch (e: Exception) {
            loadError = e.message ?: "Couldn't load interaction data"
        } finally {
            isLoading = false
        }
    }

    var drugAInput by remember { mutableStateOf(TextFieldValue("")) }
    var drugBInput by remember { mutableStateOf(TextFieldValue("")) }
    var showSuggestionsA by remember { mutableStateOf(false) }
    var showSuggestionsB by remember { mutableStateOf(false) }
    var checked by remember { mutableStateOf(false) }
    var pairResults by remember { mutableStateOf<List<PairCheckResult>>(emptyList()) }
    var resolvedALabel by remember { mutableStateOf<String?>(null) }
    var resolvedBLabel by remember { mutableStateOf<String?>(null) }
    var closeMatchesA by remember { mutableStateOf<List<String>>(emptyList()) }
    var closeMatchesB by remember { mutableStateOf<List<String>>(emptyList()) }

    fun combinedSuggestions(query: String): List<String> {
        return DrugInteractionRepository.searchNames(query, limit = 6)
    }

    val suggestionsA = remember(drugAInput.text, showSuggestionsA) {
        if (showSuggestionsA) {
            combinedSuggestions(drugAInput.text).filter {
                !it.equals(drugAInput.text.trim(), ignoreCase = true)
            }
        } else emptyList()
    }
    val suggestionsB = remember(drugBInput.text, showSuggestionsB) {
        if (showSuggestionsB) {
            combinedSuggestions(drugBInput.text).filter {
                !it.equals(drugBInput.text.trim(), ignoreCase = true)
            }
        } else emptyList()
    }

    fun resolveToIngredients(input: String): List<String> {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return emptyList()
        if (DrugInteractionRepository.isKnownDrug(trimmed)) return listOf(trimmed)
        return listOf(trimmed)
    }

    fun runCheck() {
        checked = true
        val trimmedA = drugAInput.text.trim()
        val trimmedB = drugBInput.text.trim()

        val ingredientsA = resolveToIngredients(trimmedA)
        val ingredientsB = resolveToIngredients(trimmedB)

        resolvedALabel = if (ingredientsA.size > 1 || (ingredientsA.isNotEmpty() && !ingredientsA[0].equals(trimmedA, ignoreCase = true))) {
            "\"$trimmedA\" resolved to: ${ingredientsA.joinToString(", ")}"
        } else null

        resolvedBLabel = if (ingredientsB.size > 1 || (ingredientsB.isNotEmpty() && !ingredientsB[0].equals(trimmedB, ignoreCase = true))) {
            "\"$trimmedB\" resolved to: ${ingredientsB.joinToString(", ")}"
        } else null

        val results = mutableListOf<PairCheckResult>()
        for (a in ingredientsA) {
            for (b in ingredientsB) {
                results.add(PairCheckResult(a, b, DrugInteractionRepository.checkInteraction(a, b)))
            }
        }
        pairResults = results

        val aUnresolved = ingredientsA.size == 1 && ingredientsA[0].equals(trimmedA, ignoreCase = true) &&
            !DrugInteractionRepository.isKnownDrug(trimmedA)
        val bUnresolved = ingredientsB.size == 1 && ingredientsB[0].equals(trimmedB, ignoreCase = true) &&
            !DrugInteractionRepository.isKnownDrug(trimmedB)

        closeMatchesA = if (aUnresolved) combinedSuggestions(trimmedA).take(3) else emptyList()
        closeMatchesB = if (bUnresolved) combinedSuggestions(trimmedB).take(3) else emptyList()
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                            MaterialTheme.colorScheme.background
                        ),
                        endY = 1100f
                    )
                )
        ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 3.dp,
                    modifier = Modifier.size(48.dp)
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                }
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    "Drug Interaction Checker",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
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

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
            Column(modifier = Modifier.padding(18.dp)) {
            DrugField(
                label = "First drug",
                value = drugAInput,
                onValueChange = {
                    val textChanged = it.text != drugAInput.text
                    drugAInput = it
                    if (textChanged) {
                        showSuggestionsA = true
                        checked = false
                    }
                },
                suggestions = suggestionsA,
                onSuggestionSelected = { name ->
                    drugAInput = TextFieldValue(text = name, selection = TextRange(name.length))
                    showSuggestionsA = false
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            DrugField(
                label = "Second drug",
                value = drugBInput,
                onValueChange = {
                    val textChanged = it.text != drugBInput.text
                    drugBInput = it
                    if (textChanged) {
                        showSuggestionsB = true
                        checked = false
                    }
                },
                suggestions = suggestionsB,
                onSuggestionSelected = { name ->
                    drugBInput = TextFieldValue(text = name, selection = TextRange(name.length))
                    showSuggestionsB = false
                }
            )
            }
            }

            Spacer(modifier = Modifier.height(22.dp))

            val canCheck = drugAInput.text.isNotBlank() && drugBInput.text.isNotBlank()
            val primaryColor = MaterialTheme.colorScheme.primary
            Button(
                onClick = {
                    showSuggestionsA = false
                    showSuggestionsB = false
                    runCheck()
                },
                enabled = canCheck,
                shape = RoundedCornerShape(30.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp, disabledElevation = 0.dp),
                modifier = Modifier.fillMaxWidth().height(60.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            if (canCheck) Brush.horizontalGradient(
                                listOf(primaryColor, lerp(primaryColor, Color(0xFF0B2F3F), 0.55f))
                            ) else SolidColor(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)),
                            RoundedCornerShape(30.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Check interaction",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (canCheck) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            AnimatedVisibility(
                visible = checked,
                enter = fadeIn(tween(350)) + slideInVertically(tween(350)) { it / 10 }
            ) {
            Column {
                if (resolvedALabel != null) {
                    Text(
                        resolvedALabel!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
                if (resolvedBLabel != null) {
                    Text(
                        resolvedBLabel!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
                if (resolvedALabel != null || resolvedBLabel != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                val multiplePairs = pairResults.size > 1
                pairResults.forEachIndexed { index, pr ->
                    if (pr.interaction != null) {
                        if (multiplePairs) {
                            Text(
                                "Result ${index + 1} of ${pairResults.size}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        InteractionResultCard(pr.interaction)
                    } else {
                        CompactNoInteractionCard(pr.ingredientA, pr.ingredientB)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                if (closeMatchesA.isNotEmpty() || closeMatchesB.isNotEmpty()) {
                    SuggestionsCard(
                        drugA = drugAInput.text,
                        drugB = drugBInput.text,
                        closeMatchesA = closeMatchesA,
                        closeMatchesB = closeMatchesB
                    )
                }
            }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                "Reference only, based on the DDInter interaction dataset. Not a substitute for clinical judgment or a full prescribing reference.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
        }
    }
}

@Composable
private fun DrugField(
    label: String,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    suggestions: List<String>,
    onSuggestionSelected: (String) -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = primary) },
            textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedLabelColor = primary,
                focusedContainerColor = primary.copy(alpha = 0.05f),
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
            ),
            modifier = Modifier.fillMaxWidth()
        )
        if (suggestions.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column {
                    suggestions.forEachIndexed { index, name ->
                        if (index > 0) {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                            )
                        }
                        Text(
                            text = name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSuggestionSelected(name) }
                                .padding(horizontal = 16.dp, vertical = 13.dp),
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
    "major" -> "If taken together, this combination can be life-threatening or cause severe harm. " +
        "Medical intervention is typically required \u2014 this may mean choosing a different drug, adjusting the dose, or close monitoring."
    "moderate" -> "If taken together, this combination can worsen the patient's condition or reduce treatment effectiveness. " +
        "It usually calls for monitoring, and a dose or timing adjustment may be needed."
    "minor" -> "If taken together, this combination has limited clinical impact. " +
        "It may slightly increase the frequency or severity of side effects, but a change in treatment usually isn't required."
    else -> "DDInter does not have enough mechanism data to classify this pair's severity. " +
        "This is a gap in the dataset, not a finding of safety \u2014 verify with another reference before deciding."
}

private fun severityIcon(level: String): ImageVector = when (level.trim().lowercase()) {
    "major" -> Icons.Outlined.Error
    "minor" -> Icons.Outlined.Info
    else -> Icons.Outlined.WarningAmber
}

private fun severityRank(level: String): Int = when (level.trim().lowercase()) {
    "minor" -> 1
    "moderate" -> 2
    "major" -> 3
    else -> 0
}

@Composable
private fun InteractionResultCard(result: DrugInteractionResult) {
    val style = severityStyle(result.level)
    val rank = severityRank(result.level)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = style.containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .width(7.dp)
                    .fillMaxHeight()
                    .background(style.contentColor.copy(alpha = 0.85f))
            )
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(style.contentColor.copy(alpha = 0.12f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = severityIcon(result.level),
                            contentDescription = null,
                            tint = style.contentColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        style.label,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 19.sp,
                        color = style.contentColor
                    )
                }
                if (rank > 0) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        repeat(3) { i ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(6.dp)
                                    .background(
                                        if (i < rank) style.contentColor else style.contentColor.copy(alpha = 0.15f),
                                        RoundedCornerShape(3.dp)
                                    )
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.6f)
                ) {
                    Text(
                        "${result.drugA} + ${result.drugB}",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = style.contentColor
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    severityAdvice(result.level),
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 22.sp,
                    color = style.contentColor,
                    textAlign = TextAlign.Justify
                )
            }
        }
    }
}

@Composable
private fun CompactNoInteractionCard(ingredientA: String, ingredientB: String) {
    val accent = MaterialTheme.colorScheme.primary
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .width(7.dp)
                    .fillMaxHeight()
                    .background(accent.copy(alpha = 0.55f))
            )
            Row(
                modifier = Modifier.padding(18.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(accent.copy(alpha = 0.10f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Info, contentDescription = null, tint = accent, modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        "No recorded interaction between \"$ingredientA\" and \"$ingredientB\" in this dataset.",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "That means it isn't in the dataset, not that no interaction exists.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun SuggestionsCard(
    drugA: String,
    drugB: String,
    closeMatchesA: List<String>,
    closeMatchesB: List<String>
) {
    val accent = MaterialTheme.colorScheme.primary
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = accent.copy(alpha = 0.08f))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            Icon(Icons.Outlined.Search, contentDescription = null, tint = accent, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                if (closeMatchesA.isNotEmpty()) {
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
}
