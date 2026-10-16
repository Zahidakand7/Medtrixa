package com.medtrixa.app.ui.screens.assess

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun HasBledScreen(
    onBackClick: () -> Unit
) {
    BackHandler { onBackClick() }

    val titles = listOf(
        "Hypertension",
        "Abnormal renal function",
        "Abnormal liver function",
        "Stroke history",
        "Bleeding history or predisposition",
        "Labile INR",
        "Elderly",
        "Drugs",
        "Alcohol"
    )
    val subtitles = listOf(
        "Uncontrolled, systolic BP above 160 mmHg",
        "Dialysis, transplant, or creatinine above 2.26 mg/dL (200 µmol/L)",
        "Cirrhosis, or bilirubin above 2× normal with AST/ALT/ALP above 3× normal",
        "Prior stroke",
        "Prior major bleed, anaemia or bleeding diathesis",
        "Unstable or high INRs, or time in therapeutic range below 60%. Applies only on warfarin: answer No otherwise",
        "Age above 65 years",
        "Antiplatelets or NSAIDs",
        "8 or more drinks per week"
    )
    val answers = remember { mutableStateListOf<Boolean?>(null, null, null, null, null, null, null, null, null) }
    var calculated by remember { mutableStateOf(false) }

    val ready = answers.all { it != null }
    val score = answers.count { it == true }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            RiskHeader("HAS-BLED", "Bleeding Risk in Anticoagulation", onBackClick)
            Spacer(modifier = Modifier.height(16.dp))

            titles.forEachIndexed { i, title ->
                YesNoRow(
                    title = title,
                    subtitle = "1 point · " + subtitles[i],
                    value = answers[i],
                    onChange = { answers[i] = it; calculated = false }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(10.dp))
            RiskCalculateButton(enabled = ready) { calculated = true }

            if (calculated && ready) {
                Spacer(modifier = Modifier.height(16.dp))

                val tier = when {
                    score >= 3 -> 2
                    score == 2 -> 1
                    else -> 0
                }
                val bandLabel = listOf("Low bleeding risk", "Moderate bleeding risk", "High bleeding risk")[tier]
                val bandColor = listOf(Color(0xFF1F9D6B), Color(0xFFC27A00), Color(0xFFC0392B))[tier]
                val guidance = when (tier) {
                    2 -> "High bleeding risk. Correct modifiable factors, arrange closer follow-up and review more often. A high score is not by itself a reason to withhold anticoagulation."
                    1 -> "Moderate bleeding risk. Address modifiable factors and review regularly."
                    else -> "Low bleeding risk. Anticoagulation, where indicated, can proceed with routine follow-up."
                }
                val rates = listOf("1.13", "1.02", "1.88", "3.74", "8.70", "12.50")
                val rateText = if (score <= 5) {
                    "About ${rates[score]} major bleeds per 100 patient-years at a score of $score (Euro Heart Survey derivation cohort). Rates differ between cohorts."
                } else {
                    "Scores of 6 or more were too uncommon in the derivation cohort for a reliable rate. Risk is very high."
                }
                val modifiable = listOf(0 to "Uncontrolled hypertension", 5 to "Labile INR", 7 to "Antiplatelet or NSAID use", 8 to "Alcohol excess")
                    .filter { answers[it.first] == true }
                    .map { it.second }

                RiskResultCard(
                    heading = "HAS-BLED Score",
                    scoreText = "$score / 9",
                    bandLabel = bandLabel,
                    bandColor = bandColor
                ) {
                    Text(guidance, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
                    RiskNote("Bleeding risk", rateText)
                    RiskNote(
                        "Modifiable risk factors present",
                        if (modifiable.isEmpty()) "None identified from the answers given."
                        else modifiable.joinToString(", ") + ". Addressing these lowers bleeding risk."
                    )
                    RiskNote(
                        "Interpret with caution",
                        "HAS-BLED identifies factors to correct and patients to review more often. Current guidelines advise against using it alone to withhold oral anticoagulation, because the stroke benefit usually outweighs the bleeding risk. Weigh it together with the stroke risk (CHA₂DS₂-VASc). Predictive accuracy for major bleeding is modest."
                    )
                    RiskReference("Pisters R et al. Chest 2010;138:1093-1100. Lip GYH et al. J Am Coll Cardiol 2011;57:173-180. Joglar JA et al. Circulation 2024;149:e1-e156. Van Gelder IC et al. Eur Heart J 2024;45:3314-3414. Use with clinical judgement.")
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
