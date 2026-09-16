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
fun WellsDvtScreen(
    onBackClick: () -> Unit
) {
    BackHandler { onBackClick() }

    var activeCancer by remember { mutableStateOf(false) }
    var paralysis by remember { mutableStateOf(false) }
    var bedridden by remember { mutableStateOf(false) }
    var localizedTenderness by remember { mutableStateOf(false) }
    var entireLegSwollen by remember { mutableStateOf(false) }
    var calfSwelling by remember { mutableStateOf(false) }
    var pittingEdema by remember { mutableStateOf(false) }
    var collateralVeins by remember { mutableStateOf(false) }
    var previousDvt by remember { mutableStateOf(false) }
    var alternativeDiagnosis by remember { mutableStateOf(false) }
    var calculated by remember { mutableStateOf(false) }

    val positiveCriteria = listOf(
        activeCancer, paralysis, bedridden, localizedTenderness, entireLegSwollen,
        calfSwelling, pittingEdema, collateralVeins, previousDvt
    ).count { it }

    val score = positiveCriteria - (if (alternativeDiagnosis) 2 else 0)

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
                    Text("Wells Score — DVT", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("Pretest Probability for Deep Vein Thrombosis", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("Clinical Criteria", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))

            AssessSwitchRow("Active Cancer", "Treatment ongoing or within 6 months, or palliative", activeCancer, { activeCancer = it; calculated = false })
            AssessSwitchRow("Paralysis/Immobilization", "Recent leg paresis, paralysis, or plaster immobilization", paralysis, { paralysis = it; calculated = false })
            AssessSwitchRow("Recently Bedridden", "≥3 days, or major surgery within 12 weeks", bedridden, { bedridden = it; calculated = false })
            AssessSwitchRow("Localized Tenderness", "Along the deep venous system", localizedTenderness, { localizedTenderness = it; calculated = false })
            AssessSwitchRow("Entire Leg Swollen", "", entireLegSwollen, { entireLegSwollen = it; calculated = false })
            AssessSwitchRow("Calf Swelling", "> 3 cm compared to asymptomatic leg", calfSwelling, { calfSwelling = it; calculated = false })
            AssessSwitchRow("Pitting Edema", "Confined to the symptomatic leg", pittingEdema, { pittingEdema = it; calculated = false })
            AssessSwitchRow("Collateral Superficial Veins", "Non-varicose", collateralVeins, { collateralVeins = it; calculated = false })
            AssessSwitchRow("Previous DVT", "Previously documented", previousDvt, { previousDvt = it; calculated = false })
            AssessSwitchRow("Alternative Diagnosis Likely", "As likely or more likely than DVT (−2 points)", alternativeDiagnosis, { alternativeDiagnosis = it; calculated = false })

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
                    score <= 0 -> "Low probability of DVT"
                    score <= 2 -> "Moderate probability of DVT"
                    else -> "High probability of DVT"
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Wells Score", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(score.toString(), style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(risk, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
