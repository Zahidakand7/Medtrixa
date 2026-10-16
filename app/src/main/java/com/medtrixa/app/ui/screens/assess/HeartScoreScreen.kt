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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

private data class HeartOption(val label: String, val detail: String, val points: Int)

private val heartHistoryOptions = listOf(
    HeartOption(
        "Slightly suspicious",
        "Predominantly non-specific features; non-anginal pattern",
        0
    ),
    HeartOption(
        "Moderately suspicious",
        "Mixture of specific (anginal) and non-specific features",
        1
    ),
    HeartOption(
        "Highly suspicious",
        "Predominantly specific features: retrosternal pressure or tightness, radiation to jaw, shoulder or arm, exertional onset, diaphoresis, nausea or dyspnoea, relief with nitrates",
        2
    )
)

private val heartEcgOptions = listOf(
    HeartOption("Normal", "No repolarisation abnormality", 0),
    HeartOption(
        "Non-specific repolarisation disturbance",
        "LBBB, LVH, paced rhythm, digoxin effect, or repolarisation changes without significant ST deviation",
        1
    ),
    HeartOption(
        "Significant ST-segment deviation",
        "ST deviation not attributable to LBBB, LVH or digoxin",
        2
    )
)

private val heartTroponinOptions = listOf(
    HeartOption("\u2264 1\u00D7 URL", "At or below the 99th percentile upper reference limit", 0),
    HeartOption("> 1\u20133\u00D7 URL", "Above the URL, up to three times the URL", 1),
    HeartOption("> 3\u00D7 URL", "More than three times the URL", 2)
)

@Composable
fun HeartScoreScreen(
    onBackClick: () -> Unit
) {
    BackHandler { onBackClick() }

    var history by remember { mutableStateOf<Int?>(null) }
    var ecg by remember { mutableStateOf<Int?>(null) }
    var troponin by remember { mutableStateOf<Int?>(null) }
    var age by remember { mutableStateOf("") }
    var hypertension by remember { mutableStateOf(false) }
    var dyslipidaemia by remember { mutableStateOf(false) }
    var diabetes by remember { mutableStateOf(false) }
    var obesity by remember { mutableStateOf(false) }
    var smoking by remember { mutableStateOf(false) }
    var familyHistory by remember { mutableStateOf(false) }
    var atherosclerosis by remember { mutableStateOf(false) }
    var calculated by remember { mutableStateOf(false) }

    val patientAge = age.toIntOrNull()
    val ageError =
        if (age.isNotEmpty() && (patientAge == null || patientAge < 18 || patientAge > 120))
            "Plausible range: 18-120 years"
        else null

    val agePoints = when {
        patientAge == null -> 0
        patientAge >= 65 -> 2
        patientAge >= 45 -> 1
        else -> 0
    }

    val riskFactorCount = listOf(
        hypertension, dyslipidaemia, diabetes, obesity, smoking, familyHistory
    ).count { it }
    val riskPoints = when {
        atherosclerosis || riskFactorCount >= 3 -> 2
        riskFactorCount >= 1 -> 1
        else -> 0
    }

    val ready = history != null && ecg != null && troponin != null &&
        patientAge != null && ageError == null

    val total = (history ?: 0) + (ecg ?: 0) + agePoints + riskPoints + (troponin ?: 0)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PremiumBackButton(onClick = onBackClick)

                Column() {
                    Text(
                        text = "Heart Score",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 26.sp
                    )
                    Text(
                        text = "Acute Chest Pain Risk Assessment",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Clinical Parameters",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            HeartOptionGroup(
                title = "History (H)",
                helper = "Clinician's assessment of the chest pain history",
                options = heartHistoryOptions,
                selected = history,
                onSelect = { history = it; calculated = false }
            )

            HeartOptionGroup(
                title = "ECG (E)",
                helper = "Initial 12-lead electrocardiogram",
                options = heartEcgOptions,
                selected = ecg,
                onSelect = { ecg = it; calculated = false }
            )

            Text(
                text = "Age (A)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = age,
                onValueChange = {
                    age = it.filter(Char::isDigit)
                    calculated = false
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Age") },
                suffix = { Text("years") },
                isError = ageError != null,
                supportingText = if (ageError != null) {
                    { Text(ageError) }
                } else {
                    { Text("< 45 = 0   |   45\u201364 = 1   |   \u2265 65 = 2") }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Risk Factors (R)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Select all that apply",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            HeartCheckRow("Hypertension", hypertension) { hypertension = it; calculated = false }
            HeartCheckRow("Hypercholesterolaemia / dyslipidaemia", dyslipidaemia) { dyslipidaemia = it; calculated = false }
            HeartCheckRow("Diabetes mellitus", diabetes) { diabetes = it; calculated = false }
            HeartCheckRow("Obesity (BMI > 30 kg/m\u00B2)", obesity) { obesity = it; calculated = false }
            HeartCheckRow("Current smoker or cessation \u2264 3 months", smoking) { smoking = it; calculated = false }
            HeartCheckRow("Family history of premature CVD (first-degree relative, < 65 years)", familyHistory) { familyHistory = it; calculated = false }
            HeartCheckRow("Known atherosclerotic disease (prior MI, PCI/CABG, CVA/TIA, PAD)", atherosclerosis) { atherosclerosis = it; calculated = false }

            Spacer(modifier = Modifier.height(14.dp))

            HeartOptionGroup(
                title = "Troponin (T)",
                helper = "Initial cardiac troponin relative to the assay's 99th percentile upper reference limit (URL)",
                options = heartTroponinOptions,
                selected = troponin,
                onSelect = { troponin = it; calculated = false }
            )

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = { calculated = true },
                modifier = Modifier.fillMaxWidth(),
                enabled = ready
            ) {
                Icon(
                    imageVector = Icons.Outlined.Assessment,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.padding(start = 4.dp))
                Text("Calculate")
            }

            if (calculated && ready) {
                Spacer(modifier = Modifier.height(18.dp))

                val category = when {
                    total <= 3 -> "Low Risk (0\u20133)"
                    total <= 6 -> "Moderate Risk (4\u20136)"
                    else -> "High Risk (7\u201310)"
                }
                val maceRisk = when {
                    total <= 3 -> "1.7%"
                    total <= 6 -> "16.6%"
                    else -> "50.1%"
                }
                val interpretation = when {
                    total <= 3 ->
                        "Low short-term risk of major adverse cardiac events (MACE). Early discharge may be considered where serial high-sensitivity troponin testing is negative and an alternative life-threatening diagnosis is not suspected, with appropriate outpatient follow-up."
                    total <= 6 ->
                        "Intermediate short-term risk of MACE. Admission or extended observation, serial troponin measurement and further non-invasive evaluation (stress testing or coronary CT angiography) are suggested, with cardiology input as required."
                    else ->
                        "High short-term risk of MACE. An early invasive strategy and urgent cardiology review are indicated in accordance with acute coronary syndrome management guidelines."
                }

                // Result card: center-aligned
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Heart Score",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = total.toString(),
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "out of 10 points",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = category,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Estimated 6-week MACE risk: $maceRisk",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Narratives: left-aligned only
                HeartSection(
                    title = "Component Scores",
                    body = "\u2022 History: ${history ?: 0}\n" +
                        "\u2022 ECG: ${ecg ?: 0}\n" +
                        "\u2022 Age: $agePoints\n" +
                        "\u2022 Risk factors: $riskPoints" +
                        (if (atherosclerosis) " (known atherosclerotic disease)" else " ($riskFactorCount identified)") + "\n" +
                        "\u2022 Troponin: ${troponin ?: 0}"
                )
                HeartSection(title = "Interpretation", body = interpretation)
                HeartSection(
                    title = "Clinical Caveat",
                    body = "The Heart score was derived and validated in emergency department patients with undifferentiated chest pain. It is not applicable to ST-elevation myocardial infarction or haemodynamic instability. Troponin should be interpreted using assay-specific, high-sensitivity protocols with serial sampling. The score supports, and does not replace, clinical judgement."
                )
                HeartSection(
                    title = "Definition",
                    body = "MACE: acute myocardial infarction, percutaneous coronary intervention, coronary artery bypass grafting, or all-cause death within 6 weeks of presentation."
                )
                HeartSection(
                    title = "References",
                    body = "Six AJ et al. Neth Heart J 2008;16:191-196.\nBackus BE et al. Int J Cardiol 2013;168:2153-2158.\nGulati M et al. 2021 AHA/ACC/ASE/CHEST/SAEM/SCCT/SCMR Guideline for the Evaluation and Diagnosis of Chest Pain. Circulation 2021;144:e368-e454."
                )

                Spacer(modifier = Modifier.height(24.dp))
            } else {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun HeartSection(title: String, body: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun HeartOptionGroup(
    title: String,
    helper: String,
    options: List<HeartOption>,
    selected: Int?,
    onSelect: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 14.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = helper,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        options.forEach { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = selected == option.points,
                        onClick = { onSelect(option.points) },
                        role = Role.RadioButton
                    )
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.Top
            ) {
                RadioButton(
                    selected = selected == option.points,
                    onClick = null
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = option.label + "  (" + option.points + ")",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = option.detail,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun HeartCheckRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(
                value = checked,
                onValueChange = onCheckedChange,
                role = Role.Checkbox
            )
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Checkbox(checked = checked, onCheckedChange = null)
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
    }
}
