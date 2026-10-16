package com.medtrixa.app.ui.screens.assess

import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.medtrixa.app.ui.components.PremiumBackButton
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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

@Composable
fun QsofaScreen(
    onBackClick: () -> Unit
) {
    BackHandler { onBackClick() }

    var alteredMentation by remember { mutableStateOf(false) }
    var respiratoryRate by remember { mutableStateOf("") }
    var systolicBp by remember { mutableStateOf("") }
    var calculated by remember { mutableStateOf(false) }

    fun resetResult() { calculated = false }

    val rr = respiratoryRate.toIntOrNull()
    val sbp = systolicBp.toIntOrNull()

    val score = (if (alteredMentation) 1 else 0) +
        (if (rr != null && rr >= 22) 1 else 0) +
        (if (sbp != null && sbp <= 100) 1 else 0)

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
                    Text("qSOFA", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 26.sp)
                    Text("Quick Sequential Organ Failure Assessment", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("Clinical Criteria", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))

            AssessSwitchRow(
                title = "Altered Mentation",
                description = "GCS < 15",
                checked = alteredMentation,
                onCheckedChange = { alteredMentation = it; resetResult() }
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = respiratoryRate,
                onValueChange = { respiratoryRate = it.filter(Char::isDigit); resetResult() },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Respiratory Rate") },
                suffix = { Text("/min") }
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = systolicBp,
                onValueChange = { systolicBp = it.filter(Char::isDigit); resetResult() },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Systolic BP") },
                suffix = { Text("mmHg") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { calculated = true },
                modifier = Modifier.fillMaxWidth(),
                enabled = rr != null && sbp != null
            ) {
                Icon(Icons.Outlined.Assessment, contentDescription = null)
                Spacer(modifier = Modifier.padding(start = 4.dp))
                Text("Calculate")
            }

            if (calculated) {
                Spacer(modifier = Modifier.height(18.dp))

                val positive = score >= 2
                val risk = if (positive) "qSOFA positive (≥2)" else "Low risk of poor outcome"
                val riskColor = if (positive) Color(0xFFC0392B) else Color(0xFF1F9D6B)
                val impact = if (positive) "In patients with suspected infection, associated with higher in-hospital mortality and prolonged ICU stay. Assess for organ dysfunction (SOFA), obtain lactate and blood cultures, and consider escalation per your sepsis pathway." else "qSOFA <2. Does not exclude sepsis. Continue clinical assessment and monitoring."

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("qSOFA Score", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("$score / 3", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = risk,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            modifier = Modifier
                                .background(riskColor, RoundedCornerShape(20.dp))
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                        val met = buildList {
                            if ((respiratoryRate.toDoubleOrNull() ?: 0.0) >= 22) add("RR ≥22/min")
                            val sbp = systolicBp.toDoubleOrNull()
                            if (sbp != null && sbp <= 100) add("SBP ≤100 mmHg")
                            if (alteredMentation) add("Altered mentation (GCS <15)")
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (met.isEmpty()) "No qSOFA criteria met" else "Criteria met: " + met.joinToString(", "),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = impact,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Bedside prompt, not a diagnostic criterion; limited sensitivity. Criteria: RR ≥22/min, SBP ≤100 mmHg, altered mentation (GCS <15). Seymour et al., JAMA 2016.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
internal fun AssessSwitchRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
