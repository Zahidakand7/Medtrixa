package com.medtrixa.app.ui.screens.assess

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun PercScreen(onBackClick: () -> Unit) {
    BackHandler { onBackClick() }
    val titles = listOf("Age", "Heart rate", "Oxygen saturation", "Unilateral leg swelling", "Hemoptysis", "Recent surgery or trauma", "Prior DVT or PE", "Hormone use")
    val subtitles = listOf("Age 50 years or older", "Heart rate 100 bpm or higher", "SpO2 < 95% on room air", "Present on clinical examination", "Coughing up blood", "Surgery or trauma requiring hospitalization within the past 4 weeks", "History of deep vein thrombosis or pulmonary embolism", "Exogenous estrogen use (oral contraceptives, HRT, or hormone therapy)")
    val answers = remember { mutableStateListOf<Boolean?>(null, null, null, null, null, null, null, null) }
    var calculated by remember { mutableStateOf(false) }
    val ready = answers.all { it != null }
    val positiveCount = answers.count { it == true }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().verticalScroll(rememberScrollState()).padding(16.dp)
        ) {
            RiskHeader("PERC Rule", "Pulmonary Embolism Rule-out Criteria", onBackClick)
            Spacer(modifier = Modifier.height(16.dp))
            RiskNote("Clinical Indication", "Use only in low pre-test probability patients (e.g., Wells PE < 2) to safely rule out PE without D-dimer testing.")
            Spacer(modifier = Modifier.height(16.dp))
            titles.forEachIndexed { i, title ->
                YesNoRow(title = title, subtitle = subtitles[i], value = answers[i], onChange = { answers[i] = it; calculated = false })
                Spacer(modifier = Modifier.height(10.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            RiskCalculateButton(enabled = ready) { calculated = true }
            if (calculated && ready) {
                Spacer(modifier = Modifier.height(16.dp))
                val isPercNegative = positiveCount == 0
                val bandLabel = if (isPercNegative) "PERC Negative (PE Ruled Out)" else "PERC Positive (Cannot Rule Out PE)"
                val bandColor = if (isPercNegative) Color(0xFF1F9D6B) else Color(0xFFC0392B)
                val guidance = if (isPercNegative) "Pulmonary embolism is clinically ruled out (risk < 1.5%). D-dimer testing or imaging is not required." else "PERC criteria not met ($positiveCount positive findings). Cannot rule out PE with clinical criteria alone."
                RiskResultCard(heading = "PERC Result", scoreText = "$positiveCount / 8 positive", bandLabel = bandLabel, bandColor = bandColor) {
                    Text(guidance, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
                    RiskNote("Clinical Interpretation", if (isPercNegative) "All 8 variables are negative. Post-test probability of PE is low enough to safely forego further workup." else "One or more criteria are positive. Proceed with diagnostic workup such as high-sensitivity D-dimer or CTPA.")
                    RiskReference("Kline JA et al. J Thromb Haemost. 2004;2(8):1247-1255.")
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
