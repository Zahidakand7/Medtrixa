package com.medtrixa.app.ui.screens.assess

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

@Composable
fun WellsPeScreen(
    onBackClick: () -> Unit
) {
    BackHandler { onBackClick() }

    var dvtSigns by remember { mutableStateOf(false) }
    var peIsTopDiagnosis by remember { mutableStateOf(false) }
    var heartRateOver100 by remember { mutableStateOf(false) }
    var immobilizationOrSurgery by remember { mutableStateOf(false) }
    var previousDvtOrPe by remember { mutableStateOf(false) }
    var hemoptysis by remember { mutableStateOf(false) }
    var malignancy by remember { mutableStateOf(false) }
    var calculated by remember { mutableStateOf(false) }

    val score = (if (dvtSigns) 3.0 else 0.0) +
        (if (peIsTopDiagnosis) 3.0 else 0.0) +
        (if (heartRateOver100) 1.5 else 0.0) +
        (if (immobilizationOrSurgery) 1.5 else 0.0) +
        (if (previousDvtOrPe) 1.5 else 0.0) +
        (if (hemoptysis) 1.0 else 0.0) +
        (if (malignancy) 1.0 else 0.0)

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
                    Text("Wells Score — PE", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("Pretest Probability for Pulmonary Embolism", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("Clinical Criteria", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))

            AssessSwitchRow("Clinical Signs of DVT", "Leg swelling and pain with palpation of deep veins (+3 points)", dvtSigns, { dvtSigns = it; calculated = false })
            AssessSwitchRow("PE Is #1 Diagnosis", "Or equally likely as an alternative diagnosis (+3 points)", peIsTopDiagnosis, { peIsTopDiagnosis = it; calculated = false })
            AssessSwitchRow("Heart Rate > 100 bpm", "(+1.5 points)", heartRateOver100, { heartRateOver100 = it; calculated = false })
            AssessSwitchRow("Immobilization or Surgery", "≥3 days bedridden, or surgery in previous 4 weeks (+1.5 points)", immobilizationOrSurgery, { immobilizationOrSurgery = it; calculated = false })
            AssessSwitchRow("Previous DVT or PE", "Objectively diagnosed (+1.5 points)", previousDvtOrPe, { previousDvtOrPe = it; calculated = false })
            AssessSwitchRow("Hemoptysis", "(+1 point)", hemoptysis, { hemoptysis = it; calculated = false })
            AssessSwitchRow("Malignancy", "Treatment within 6 months, or palliative (+1 point)", malignancy, { malignancy = it; calculated = false })

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { calculated = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Outlined.Assessment, contentDescription = null)
                Spacer(modifier = Modifier.padding(start = 4.dp))
                Text("Calculate Wells Score")
            }

            if (calculated) {
                Spacer(modifier = Modifier.height(18.dp))

                val risk = when {
                    score < 2.0 -> "Low probability of PE"
                    score <= 6.0 -> "Moderate probability of PE"
                    else -> "High probability of PE"
                }

                val dichotomized = if (score > 4.0) "PE Likely" else "PE Unlikely"

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Wells Score", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            if (score == score.toInt().toDouble()) score.toInt().toString() else score.toString(),
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(risk, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(dichotomized, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
