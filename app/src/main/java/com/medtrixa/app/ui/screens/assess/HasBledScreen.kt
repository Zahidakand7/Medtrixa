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
fun HasBledScreen(
    onBackClick: () -> Unit
) {
    BackHandler { onBackClick() }

    var hypertension by remember { mutableStateOf(false) }
    var abnormalRenal by remember { mutableStateOf(false) }
    var abnormalLiver by remember { mutableStateOf(false) }
    var strokeHistory by remember { mutableStateOf(false) }
    var bleedingHistory by remember { mutableStateOf(false) }
    var labileInr by remember { mutableStateOf(false) }
    var elderly by remember { mutableStateOf(false) }
    var drugUse by remember { mutableStateOf(false) }
    var alcoholUse by remember { mutableStateOf(false) }
    var calculated by remember { mutableStateOf(false) }

    val score = listOf(
        hypertension, abnormalRenal, abnormalLiver, strokeHistory,
        bleedingHistory, labileInr, elderly, drugUse, alcoholUse
    ).count { it }

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
                    Text("HAS-BLED", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("Bleeding Risk in Anticoagulation", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("Risk Factors", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))

            AssessSwitchRow("Hypertension", "Uncontrolled, SBP > 160 mmHg", hypertension, { hypertension = it; calculated = false })
            AssessSwitchRow("Abnormal Renal Function", "Dialysis, transplant, Cr > 2.26 mg/dL", abnormalRenal, { abnormalRenal = it; calculated = false })
            AssessSwitchRow("Abnormal Liver Function", "Cirrhosis, bilirubin > 2× normal, AST/ALT > 3× normal", abnormalLiver, { abnormalLiver = it; calculated = false })
            AssessSwitchRow("Stroke History", "Prior ischemic or hemorrhagic stroke", strokeHistory, { strokeHistory = it; calculated = false })
            AssessSwitchRow("Bleeding History/Predisposition", "Prior major bleed or bleeding diathesis", bleedingHistory, { bleedingHistory = it; calculated = false })
            AssessSwitchRow("Labile INR", "Unstable/high INRs, time in therapeutic range < 60%", labileInr, { labileInr = it; calculated = false })
            AssessSwitchRow("Elderly", "Age > 65 years", elderly, { elderly = it; calculated = false })
            AssessSwitchRow("Drug Use", "Antiplatelets, NSAIDs", drugUse, { drugUse = it; calculated = false })
            AssessSwitchRow("Alcohol Use", "≥ 8 drinks/week", alcoholUse, { alcoholUse = it; calculated = false })

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { calculated = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Outlined.Assessment, contentDescription = null)
                Spacer(modifier = Modifier.padding(start = 4.dp))
                Text("Calculate HAS-BLED")
            }

            if (calculated) {
                Spacer(modifier = Modifier.height(18.dp))

                val risk = when {
                    score <= 2 -> "Low bleeding risk"
                    else -> "High bleeding risk — caution with anticoagulation, consider closer monitoring"
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("HAS-BLED Score", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("$score / 9", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(risk, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
