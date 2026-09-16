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

private data class ApgarOption(val points: Int, val label: String)

@Composable
fun ApgarScreen(
    onBackClick: () -> Unit
) {
    BackHandler { onBackClick() }

    val appearanceOptions = listOf(
        ApgarOption(2, "Pink all over"),
        ApgarOption(1, "Body pink, extremities blue"),
        ApgarOption(0, "Blue/pale all over")
    )
    val pulseOptions = listOf(
        ApgarOption(2, "≥ 100 bpm"),
        ApgarOption(1, "< 100 bpm"),
        ApgarOption(0, "Absent")
    )
    val grimaceOptions = listOf(
        ApgarOption(2, "Cries, coughs, or pulls away"),
        ApgarOption(1, "Grimace / weak cry with stimulation"),
        ApgarOption(0, "No response")
    )
    val activityOptions = listOf(
        ApgarOption(2, "Active motion"),
        ApgarOption(1, "Some flexion of limbs"),
        ApgarOption(0, "Limp / flaccid")
    )
    val respirationOptions = listOf(
        ApgarOption(2, "Good, strong cry"),
        ApgarOption(1, "Slow / irregular"),
        ApgarOption(0, "Absent")
    )

    var appearanceScore by remember { mutableStateOf<Int?>(null) }
    var pulseScore by remember { mutableStateOf<Int?>(null) }
    var grimaceScore by remember { mutableStateOf<Int?>(null) }
    var activityScore by remember { mutableStateOf<Int?>(null) }
    var respirationScore by remember { mutableStateOf<Int?>(null) }

    val allSelected = appearanceScore != null && pulseScore != null &&
        grimaceScore != null && activityScore != null && respirationScore != null

    val total = (appearanceScore ?: 0) + (pulseScore ?: 0) + (grimaceScore ?: 0) +
        (activityScore ?: 0) + (respirationScore ?: 0)

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
                    Text("APGAR Score", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("Newborn Condition Assessment", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            ApgarSection("Appearance (Skin Color)", appearanceOptions, appearanceScore) { appearanceScore = it }
            Spacer(modifier = Modifier.height(16.dp))
            ApgarSection("Pulse (Heart Rate)", pulseOptions, pulseScore) { pulseScore = it }
            Spacer(modifier = Modifier.height(16.dp))
            ApgarSection("Grimace (Reflex Irritability)", grimaceOptions, grimaceScore) { grimaceScore = it }
            Spacer(modifier = Modifier.height(16.dp))
            ApgarSection("Activity (Muscle Tone)", activityOptions, activityScore) { activityScore = it }
            Spacer(modifier = Modifier.height(16.dp))
            ApgarSection("Respiration", respirationOptions, respirationScore) { respirationScore = it }

            Spacer(modifier = Modifier.height(20.dp))

            val interpretation = when {
                total >= 7 -> "Normal"
                total >= 4 -> "Moderately abnormal"
                else -> "Low score — needs resuscitation"
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Total APGAR Score", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    if (allSelected) {
                        Text("$total / 10", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(interpretation, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    } else {
                        Text("— / 10", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Select all five categories",
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
private fun ApgarSection(
    title: String,
    options: List<ApgarOption>,
    selected: Int?,
    onSelect: (Int) -> Unit
) {
    Column {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            options.forEach { option ->
                FilterChip(
                    selected = selected == option.points,
                    onClick = { onSelect(option.points) },
                    label = { Text("${option.points} — ${option.label}") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
