package com.medtrixa.app.ui.screens.assess

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun Cha2ds2VascScreen(
    onBackClick: () -> Unit
) {
    BackHandler { onBackClick() }

    var heartFailure by remember { mutableStateOf<Boolean?>(null) }
    var hypertension by remember { mutableStateOf<Boolean?>(null) }
    var diabetes by remember { mutableStateOf<Boolean?>(null) }
    var previousStroke by remember { mutableStateOf<Boolean?>(null) }
    var vascularDisease by remember { mutableStateOf<Boolean?>(null) }
    var female by remember { mutableStateOf<Boolean?>(null) }
    var ageText by remember { mutableStateOf("") }
    var calculated by remember { mutableStateOf(false) }

    val age = ageText.toIntOrNull()
    val ageValid = age != null && age in 18..120
    val ready = ageValid && heartFailure != null && hypertension != null && diabetes != null &&
        previousStroke != null && vascularDisease != null && female != null

    val agePoints = when {
        age == null -> 0
        age >= 75 -> 2
        age >= 65 -> 1
        else -> 0
    }
    val hfP = if (heartFailure == true) 1 else 0
    val htnP = if (hypertension == true) 1 else 0
    val dmP = if (diabetes == true) 1 else 0
    val strokeP = if (previousStroke == true) 2 else 0
    val vascP = if (vascularDisease == true) 1 else 0
    val sexP = if (female == true) 1 else 0
    val vaScore = hfP + htnP + agePoints + dmP + strokeP + vascP
    val score = vaScore + sexP

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            RiskHeader("CHA₂DS₂-VASc", "Stroke Risk in Atrial Fibrillation", onBackClick)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = ageText,
                onValueChange = { v ->
                    if (v.length <= 3 && v.all { it.isDigit() }) {
                        ageText = v
                        calculated = false
                    }
                },
                label = { Text("Age (years)") },
                supportingText = {
                    Text(
                        when {
                            ageText.isEmpty() -> "Under 65: 0 · 65–74: 1 · 75 or older: 2 points"
                            !ageValid -> "Enter an age between 18 and 120"
                            else -> "Age points: $agePoints"
                        }
                    )
                },
                isError = ageText.isNotEmpty() && !ageValid,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            YesNoRow(
                title = "Congestive heart failure / LV dysfunction",
                subtitle = "1 point · signs or symptoms of HF, or reduced LVEF",
                value = heartFailure,
                onChange = { heartFailure = it; calculated = false }
            )
            Spacer(modifier = Modifier.height(10.dp))
            YesNoRow(
                title = "Hypertension",
                subtitle = "1 point · history of hypertension or on treatment",
                value = hypertension,
                onChange = { hypertension = it; calculated = false }
            )
            Spacer(modifier = Modifier.height(10.dp))
            YesNoRow(
                title = "Diabetes mellitus",
                subtitle = "1 point · diagnosed diabetes or on treatment",
                value = diabetes,
                onChange = { diabetes = it; calculated = false }
            )
            Spacer(modifier = Modifier.height(10.dp))
            YesNoRow(
                title = "Previous stroke / TIA / thromboembolism",
                subtitle = "2 points",
                value = previousStroke,
                onChange = { previousStroke = it; calculated = false }
            )
            Spacer(modifier = Modifier.height(10.dp))
            YesNoRow(
                title = "Vascular disease",
                subtitle = "1 point · prior MI, peripheral artery disease or aortic plaque",
                value = vascularDisease,
                onChange = { vascularDisease = it; calculated = false }
            )
            Spacer(modifier = Modifier.height(10.dp))
            YesNoRow(
                title = "Sex",
                subtitle = "Female: 1 point",
                value = female,
                onChange = { female = it; calculated = false },
                yesLabel = "Female",
                noLabel = "Male"
            )

            Spacer(modifier = Modifier.height(20.dp))
            RiskCalculateButton(enabled = ready) { calculated = true }

            if (calculated && ready) {
                Spacer(modifier = Modifier.height(16.dp))

                val isFemale = female == true
                val tier = if (isFemale) {
                    when {
                        score >= 3 -> 2
                        score == 2 -> 1
                        else -> 0
                    }
                } else {
                    when {
                        score >= 2 -> 2
                        score == 1 -> 1
                        else -> 0
                    }
                }
                val bandLabel = listOf("Low risk", "Intermediate risk", "High risk")[tier]
                val bandColor = listOf(Color(0xFF1F9D6B), Color(0xFFC27A00), Color(0xFFC0392B))[tier]
                val guidance = when (tier) {
                    2 -> "Oral anticoagulation is recommended. A DOAC is preferred over warfarin, except with a mechanical heart valve or moderate-to-severe mitral stenosis."
                    1 -> "Oral anticoagulation is reasonable to consider, after weighing bleeding risk and patient preference."
                    else -> "Anticoagulation is generally not indicated on this score alone. Antiplatelet therapy is not recommended for stroke prevention in AF."
                }
                val annualRisk = listOf("0.2", "0.6", "2.2", "3.2", "4.8", "7.2", "9.7", "11.2", "10.8", "12.2")[score.coerceIn(0, 9)]

                RiskResultCard(
                    heading = "CHA₂DS₂-VASc Score",
                    scoreText = "$score / 9",
                    bandLabel = bandLabel,
                    bandColor = bandColor
                ) {
                    Text(guidance, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
                    RiskNote(
                        "Annual ischaemic stroke risk without anticoagulation",
                        "About $annualRisk% per year at a score of $score (Swedish AF cohort). Absolute estimates vary between cohorts."
                    )
                    RiskNote(
                        "Components",
                        "Heart failure $hfP · Hypertension $htnP · Age $agePoints · Diabetes $dmP · Stroke/TIA $strokeP · Vascular $vascP · Sex $sexP"
                    )
                    RiskNote(
                        "CHA₂DS₂-VA (2024 ESC)",
                        "Score without the sex category: $vaScore. The 2024 ESC guideline uses this version: anticoagulation is recommended at 2 or more and should be considered at 1."
                    )
                    RiskNote(
                        "Interpret with caution",
                        "Validated for non-valvular AF. Anticoagulation is indicated regardless of score with a mechanical heart valve, moderate-to-severe mitral stenosis, or hypertrophic cardiomyopathy. Assess bleeding risk (for example HAS-BLED) and correct modifiable factors such as uncontrolled hypertension, alcohol and interacting drugs. Bleeding risk alone is not a reason to withhold anticoagulation. Reassess at least annually, as risk rises with age and new comorbidities."
                    )
                    RiskReference("Lip GYH et al. Chest 2010;137:263-272. Friberg L et al. Eur Heart J 2012;33:1500-1510. Joglar JA et al. Circulation 2024;149:e1-e156. Van Gelder IC et al. Eur Heart J 2024;45:3314-3414. Use with clinical judgement.")
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
