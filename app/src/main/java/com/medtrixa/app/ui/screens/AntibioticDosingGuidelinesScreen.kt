package com.medtrixa.app.ui.screens
import com.medtrixa.app.ui.components.AutoSizeText

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextOverflow
import android.content.Context
import androidx.compose.material3.Button
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import androidx.compose.ui.text.style.TextAlign

private data class AntibioticRegimen(
    val indication: String,
    val setting: String,
    val organisms: String,
    val antibiotic: String,
    val dose: String,
    val route: String,
    val frequency: String,
    val duration: String,
    val patientGroup: String,
    val renalAdjustment: String,
    val alternatives: String,
    val notes: String,
    val source: String,
    val sourceStatus: String,
    val reviewDate: String
)

private sealed interface GuidelinesState {
    object Loading : GuidelinesState
    data class Ready(val items: List<AntibioticRegimen>) : GuidelinesState
    data class Failed(val message: String) : GuidelinesState
}

private object AntibioticGuidelines {
    private const val FILE_NAME = "antibiotic_dosing_guidelines.csv"
    private val mutableState = MutableStateFlow<GuidelinesState>(GuidelinesState.Loading)
    val state: StateFlow<GuidelinesState> = mutableState

    @Synchronized
    fun ensureLoaded(context: Context) {
        if (mutableState.value is GuidelinesState.Ready) return
        mutableState.value = GuidelinesState.Loading
        mutableState.value = try {
            val text = context.applicationContext.assets.open(FILE_NAME)
                .bufferedReader(Charsets.UTF_8).use { it.readText() }
            val items = parse(text)
            if (items.isEmpty()) GuidelinesState.Failed("The guidelines file is empty.")
            else GuidelinesState.Ready(items)
        } catch (e: Exception) {
            GuidelinesState.Failed("Could not load guidelines: ${e.message ?: "unknown error"}")
        }
    }

    private fun parse(raw: String): List<AntibioticRegimen> {
        val rows = parseCsv(raw.removePrefix("\uFEFF"))
        if (rows.size < 2) return emptyList()
        val header = rows.first().map { it.trim() }
        fun col(name: String): Int {
            val index = header.indexOf(name)
            require(index >= 0) { "Missing column: $name" }
            return index
        }
        val names = listOf(
            "indication", "setting", "organisms", "antibiotic", "dose", "route",
            "frequency", "duration", "patientGroup", "renalAdjustment",
            "alternatives", "notes", "source", "sourceStatus", "reviewDate"
        )
        val idx = names.associateWith { col(it) }
        return rows.drop(1)
            .filter { r -> r.any { it.isNotBlank() } }
            .map { r ->
                fun v(name: String) = r.getOrElse(idx.getValue(name)) { "" }.trim()
                AntibioticRegimen(
                    indication = v("indication"),
                    setting = v("setting"),
                    organisms = v("organisms"),
                    antibiotic = v("antibiotic"),
                    dose = v("dose"),
                    route = v("route"),
                    frequency = v("frequency"),
                    duration = v("duration"),
                    patientGroup = v("patientGroup"),
                    renalAdjustment = v("renalAdjustment"),
                    alternatives = v("alternatives"),
                    notes = v("notes"),
                    source = v("source"),
                    sourceStatus = v("sourceStatus"),
                    reviewDate = v("reviewDate")
                )
            }
    }

    private fun parseCsv(text: String): List<List<String>> {
        val rows = mutableListOf<List<String>>()
        var row = mutableListOf<String>()
        val field = StringBuilder()
        var inQuotes = false
        var i = 0
        while (i < text.length) {
            val c = text[i]
            if (inQuotes) {
                if (c == '"') {
                    if (i + 1 < text.length && text[i + 1] == '"') {
                        field.append('"')
                        i++
                    } else {
                        inQuotes = false
                    }
                } else {
                    field.append(c)
                }
            } else {
                when (c) {
                    '"' -> inQuotes = true
                    ',' -> {
                        row.add(field.toString())
                        field.setLength(0)
                    }
                    '\r' -> {}
                    '\n' -> {
                        row.add(field.toString())
                        field.setLength(0)
                        rows.add(row)
                        row = mutableListOf()
                    }
                    else -> field.append(c)
                }
            }
            i++
        }
        if (field.isNotEmpty() || row.isNotEmpty()) {
            row.add(field.toString())
            rows.add(row)
        }
        return rows
    }
}

fun preloadAntibioticGuidelines(context: Context) {
    val appContext = context.applicationContext
    Thread { AntibioticGuidelines.ensureLoaded(appContext) }.start()
}

private fun AntibioticRegimen.matches(query: String): Boolean {
    if (query.isBlank()) return true

    val q = query.trim().lowercase()

    val searchableText = listOf(
        indication,
        setting,
        organisms,
        antibiotic,
        dose,
        route,
        frequency,
        duration,
        patientGroup,
        renalAdjustment,
        alternatives,
        notes,
        source
    ).joinToString(" ").lowercase()

    return q.split(Regex("\\s+"))
        .filter { it.isNotBlank() }
        .all { searchableText.contains(it) }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AntibioticDosingGuidelinesScreen(
    onBackClick: () -> Unit
) {
    var query by remember { mutableStateOf("") }

    val context = LocalContext.current
    var retryCount by remember { mutableStateOf(0) }
    val loadState by AntibioticGuidelines.state.collectAsState()

    LaunchedEffect(retryCount) {
        withContext(Dispatchers.IO) { AntibioticGuidelines.ensureLoaded(context) }
    }

    val regimens = (loadState as? GuidelinesState.Ready)?.items.orEmpty()
    val results = remember(query, regimens) {
        regimens.filter { it.matches(query) }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBack,
                        contentDescription = "Back"
                    )
                }

                Column(modifier = Modifier.padding(start = 4.dp)) {
                    AutoSizeText(
                        text = "Antibiotic Dosing Guidelines",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Renowned empiric antibiotic selection and dosing by indication",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    top = 8.dp,
                    bottom = 24.dp
                ),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
            item {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = null
                        )
                    },
                    textStyle = MaterialTheme.typography.bodyMedium,
                    placeholder = {
                        Text(
                            "Search indication, organism or antibiotic",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                )
            }

            item {
                Text(
                    text = if (loadState !is GuidelinesState.Ready) {
                        ""
                    } else if (query.isBlank()) {
                        "${results.size} verified reference entries"
                    } else {
                        "${results.size} result${if (results.size == 1) "" else "s"}"
                    },
                    style = MaterialTheme.typography.titleMedium
                )
            }

            if (loadState is GuidelinesState.Loading) {
                item {
                    Text(
                        text = "Loading guidelines…",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else if (loadState is GuidelinesState.Failed) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "Guidelines could not be loaded",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = (loadState as GuidelinesState.Failed).message,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(onClick = { retryCount++ }) {
                                Text("Try again")
                            }
                        }
                    }
                }
            } else if (results.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp)
                        ) {
                            Text(
                                text = "No matching regimen",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Try an indication, organism, or antibiotic name.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            } else {
                items(
                    items = results,
                    key = {
                        "${it.indication}|${it.setting}|${it.antibiotic}|${it.dose}"
                    }
                ) { regimen ->

                    AntibioticRegimenCard(regimen)
                }
            }


item {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Clinical reference notice",
                    style = MaterialTheme.typography.titleSmall
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "This guide is a reference aid, not a substitute for patient-specific clinical judgment, local antibiograms, culture results, allergy assessment, renal/hepatic function, drug interactions, or institutional protocols.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            }
        }
    }

    BackHandler(onBack = onBackClick)
}


@Composable
private fun AntibioticRegimenCard(
    regimen: AntibioticRegimen
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {

            Text(
                text = regimen.antibiotic,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = regimen.indication,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = regimen.setting,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            DetailRow("Likely organisms", regimen.organisms)
            DetailRow("Dose", regimen.dose)
            DetailRow("Route", regimen.route)
            DetailRow("Frequency", regimen.frequency)
            DetailRow("Duration", regimen.duration)
            DetailRow("Patient group", regimen.patientGroup)
            DetailRow("Renal adjustment", regimen.renalAdjustment)
            DetailRow("Alternatives", regimen.alternatives)
            DetailRow("Notes", regimen.notes)

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Source: ${regimen.source}",
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = "Status: ${regimen.sourceStatus} • Reviewed ${regimen.reviewDate}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Justify,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
