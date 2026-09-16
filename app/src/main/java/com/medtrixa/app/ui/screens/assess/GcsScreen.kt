package com.medtrixa.app.ui.screens.assess

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private data class GcsOption(val points: Int, val label: String)

@Composable
fun GcsScreen(
    onBackClick: () -> Unit
) {
    BackHandler { onBackClick() }

    val eyeOptions = listOf(
        GcsOption(4, "Spontaneous"),
        GcsOption(3, "To speech"),
        GcsOption(2, "To pain"),
        GcsOption(1, "None")
    )
    val verbalOptions = listOf(
        GcsOption(5, "Oriented"),
        GcsOption(4, "Confused"),
        GcsOption(3, "Inappropriate words"),
        GcsOption(2, "Incomprehensible sounds"),
        GcsOption(1, "None")
    )
    val motorOptions = listOf(
        GcsOption(6, "Obeys commands"),
        GcsOption(5, "Localizes pain"),
        GcsOption(4, "Withdraws from pain"),
        GcsOption(3, "Abnormal flexion"),
        GcsOption(2, "Abnormal extension"),
        GcsOption(1, "None")
    )

    var eyeScore by remember { mutableStateOf<Int?>(null) }
    var verbalScore by remember { mutableStateOf<Int?>(null) }
    var motorScore by remember { mutableStateOf<Int?>(null) }

    val allSelected = eyeScore != null && verbalScore != null && motorScore != null
    val total = (eyeScore ?: 0) + (verbalScore ?: 0) + (motorScore ?: 0)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Outlined.ArrowBack, contentDescription = "Back")
                }
                Column(modifier = Modifier.padding(start = 4.dp)) {
                    Text("Glasgow Coma Scale", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("Level of Consciousness Assessment", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            GcsSection("Eye Opening (E)", eyeOptions, eyeScore) { eyeScore = it }
            Spacer(modifier = Modifier.height(16.dp))
            GcsSection("Verbal Response (V)", verbalOptions, verbalScore) { verbalScore = it }
            Spacer(modifier = Modifier.height(16.dp))
            GcsSection("Motor Response (M)", motorOptions, motorScore) { motorScore = it }

            Spacer(modifier = Modifier.height(20.dp))

            val severity = when {
                total <= 8 -> "Severe brain injury"
                total <= 12 -> "Moderate brain injury"
                else -> "Mild brain injury"
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Total GCS", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    if (allSelected) {
                        Text("$total / 15", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("E$eyeScore V$verbalScore M$motorScore", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(severity, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    } else {
                        Text("— / 15", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "E${eyeScore ?: "-"} V${verbalScore ?: "-"} M${motorScore ?: "-"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Select all three categories",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun GcsSection(
    title: String,
    options: List<GcsOption>,
    selected: Int?,
    onSelect: (Int) -> Unit
) {
    Column {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            options.chunked(2).forEach { rowOptions ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    rowOptions.forEach { option ->
                        FilterChip(
                            selected = selected == option.points,
                            onClick = { onSelect(option.points) },
                            label = { Text("${option.points} — ${option.label}") }
                        )
                    }
                }
            }
        }
    }
}
