package com.medtrixa.app.ui.screens.assess

import androidx.activity.compose.BackHandler
import androidx.compose.ui.unit.sp
import com.medtrixa.app.ui.components.PremiumBackButton
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
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign

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
                PremiumBackButton(onClick = onBackClick)
                Column() {
                    Text("APGAR Score", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 26.sp)
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

            val (bandLabel, bandColor, guidance) = when {
                total >= 7 -> Triple("Reassuring", Color(0xFF1F9D6B), "Routine newborn care and skin-to-skin contact. The score alone does not indicate resuscitation.")
                total >= 4 -> Triple("Moderately abnormal", Color(0xFFC27A00), "Dry, warm, stimulate and support breathing (oxygen or ventilation as needed). Monitor closely and repeat the score at 5 minutes.")
                else -> Triple("Low score", Color(0xFFC0392B), "Immediate resuscitation per NRP. HR below 100: positive-pressure ventilation. HR below 60 after 30 s of effective ventilation: start chest compressions.")
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Total APGAR Score", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    if (allSelected) {
                        Text("$total / 10", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = bandLabel,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            modifier = Modifier
                                .background(bandColor, RoundedCornerShape(20.dp))
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(guidance, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
                        ApgarNote(
                            "Components",
                            "Appearance ${appearanceScore ?: 0} · Pulse ${pulseScore ?: 0} · Grimace ${grimaceScore ?: 0} · Activity ${activityScore ?: 0} · Respiration ${respirationScore ?: 0}"
                        )
                        ApgarNote(
                            "Timing",
                            "Score at 1 and 5 minutes. If the 5-minute score is below 7, repeat every 5 minutes up to 20 minutes. Never delay resuscitation to assign the 1-minute score."
                        )
                        ApgarNote(
                            "Interpret with caution",
                            "The score alone does not diagnose asphyxia or predict neurological outcome. Asphyxia needs supporting evidence, such as cord pH below 7.0 or base deficit of 12 mmol/L or more, neonatal encephalopathy and multiorgan dysfunction. Scores can be lower without hypoxia in preterm infants, with maternal sedation, congenital anomalies and neuromuscular disease."
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Apgar V. Anesth Analg 1953;32:260-267. AAP/ACOG, The Apgar Score. Pediatrics 2015;136(4):819-822. Use with clinical judgement.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
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

@Composable
private fun ApgarNote(title: String, body: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
        Text(title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(2.dp))
        Text(body, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Start)
    }
}
