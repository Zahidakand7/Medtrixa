package com.medtrixa.app.ui.screens.drugtools

import com.medtrixa.app.ui.components.PremiumBackButton
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                PremiumBackButton(onClick = onBackClick)
                Text(
                    "Contraindication Checker",
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 20.sp,
                    lineHeight = 26.sp,
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

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
            Column(modifier = Modifier.padding(18.dp)) {
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
            }
            }

            Spacer(modifier = Modifier.height(22.dp))

            val canCheck = drugInput.text.isNotBlank()
            val primaryColor = MaterialTheme.colorScheme.primary
            Button(
                onClick = {
                    showSuggestions = false
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
                        "Check contraindications",
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
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                "Reference only, based on openFDA drug label data. Not a substitute for clinical judgment or a full prescribing reference.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
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

// ---- Premium white card: coloured header band (red = contraindications, green = none on label, blue-grey = not in dataset) ----
@Composable
private fun ResultPanel(
    headerColor: Color,
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    content: (@Composable ColumnScope.() -> Unit)? = null
) {
    val shape = RoundedCornerShape(20.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, Color(0xFFDDE3E8), shape)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(headerColor)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
            }
            if (subtitle != null) {
                // 38dp icon chip + 12dp gap, so this line starts exactly under the drug name
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    modifier = Modifier.padding(start = 50.dp, top = 2.dp)
                )
            }
        }
        if (content != null) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp), content = content)
        }
    }
}

@Composable
private fun ContraindicationResultCard(result: ContraindicationResult) {
    val text = cleanLabelRefs(result.contraindications.trim())
    val isNoneOnFile = text.isEmpty() || text.equals("none", ignoreCase = true) || text.equals("none.", ignoreCase = true)
    val headerColor = if (isNoneOnFile) Color(0xFF2E7D4F) else Color(0xFFC53D3D)

    ResultPanel(
        headerColor = headerColor,
        icon = if (isNoneOnFile) Icons.Outlined.CheckCircle else Icons.Outlined.WarningAmber,
        title = result.genericName,
        subtitle = if (isNoneOnFile) "No contraindications found." else null,
        content = if (isNoneOnFile && result.status.isBlank()) null else ({
        if (!isNoneOnFile) {
            Text(
                text,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 24.sp,
                color = Color(0xFF1F2933),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Justify
            )
        }
        if (result.status.isNotBlank()) {
            if (!isNoneOnFile) Spacer(modifier = Modifier.height(14.dp))
            Surface(
                shape = RoundedCornerShape(50),
                color = headerColor.copy(alpha = 0.10f)
            ) {
                Text(
                    "Status: ${result.status}",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = headerColor
                )
            }
        }
        })
    )
}

@Composable
private fun CompactNoContraindicationCard(drugName: String) {
    ResultPanel(
        headerColor = Color(0xFF5B7683),
        icon = Icons.Outlined.Info,
        title = drugName
    ) {
        Text(
            "No contraindication record found for \"$drugName\" in this dataset.",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1F2933)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            "That means it isn't in the dataset, not that no contraindications exist.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ContraindicationSuggestionsCard(drugName: String, closeMatches: List<String>) {
    val accent = MaterialTheme.colorScheme.primary
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = accent.copy(alpha = 0.08f))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            Icon(Icons.Outlined.Search, contentDescription = null, tint = accent, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                "Did you mean (for \"$drugName\"): ${closeMatches.joinToString(", ")}?",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

private fun cleanLabelRefs(raw: String): String {
    var t = raw
    t = Regex("""\[\s*see\b[^\]]*\]""", RegexOption.IGNORE_CASE).replace(t, "")
    t = Regex("""\(\s*\d{1,2}\.\d{1,2}(?:\s*,\s*\d{1,2}(?:\.\d{1,2})?)*\s*\)""").replace(t, "")
    t = Regex("""\(\s*\d{1,2}(?:\s*,\s*\d{1,2}(?:\.\d{1,2})?)+\s*\)""").replace(t, "")
    t = Regex("""\(\s+\d{1,2}\s+\)""").replace(t, "")
    t = Regex(""" {2,}""").replace(t, " ")
    t = Regex(""" +([.,;:])""").replace(t, "$1")
    return t.trim()
}
