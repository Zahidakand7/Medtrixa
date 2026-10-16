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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

private fun rangeError(text: String, v: Int?, min: Int, max: Int, unit: String): String? =
    if (text.isNotEmpty() && (v == null || v < min || v > max))
        "Plausible range: $min-$max $unit"
    else null

@Composable
fun Curb65Screen(
    onBackClick: () -> Unit
) {
    BackHandler { onBackClick() }

    var confusion by remember { mutableStateOf(false) }
    var ureaHigh by remember { mutableStateOf(false) }
    var respiratoryRate by remember { mutableStateOf("") }
    var systolicBp by remember { mutableStateOf("") }
    var diastolicBp by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var calculated by remember { mutableStateOf(false) }

    fun resetResult() {
        calculated = false
    }

    val rr = respiratoryRate.toIntOrNull()
    val sbp = systolicBp.toIntOrNull()
    val dbp = diastolicBp.toIntOrNull()
    val patientAge = age.toIntOrNull()

    val rrError = rangeError(respiratoryRate, rr, 4, 60, "/min")
    val sbpError = rangeError(systolicBp, sbp, 50, 300, "mmHg")
    val dbpError = rangeError(diastolicBp, dbp, 20, 200, "mmHg")
        ?: if (sbp != null && dbp != null && dbp >= sbp) "Must be lower than systolic BP" else null
    val ageError = rangeError(age, patientAge, 18, 120, "years")

    val inputsValid = rr != null && sbp != null && dbp != null && patientAge != null &&
        rrError == null && sbpError == null && dbpError == null && ageError == null

    val tachypnoea = rr != null && rr >= 30
    val hypotension = (sbp != null && sbp < 90) || (dbp != null && dbp <= 60)
    val elderly = patientAge != null && patientAge >= 65

    val score = (if (confusion) 1 else 0) +
        (if (ureaHigh) 1 else 0) +
        (if (tachypnoea) 1 else 0) +
        (if (hypotension) 1 else 0) +
        (if (elderly) 1 else 0)

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
                        text = "CURB-65",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 26.sp
                    )
                    Text(
                        text = "Pneumonia Severity Assessment",
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

            Spacer(modifier = Modifier.height(10.dp))

            Curb65SwitchRow(
                title = "Confusion",
                description = "New-onset disorientation to person, place or time (AMTS \u2264 8)",
                checked = confusion,
                onCheckedChange = {
                    confusion = it
                    resetResult()
                }
            )

            Curb65SwitchRow(
                title = "Serum urea > 7 mmol/L",
                description = "Equivalent to BUN > 19 mg/dL",
                checked = ureaHigh,
                onCheckedChange = {
                    ureaHigh = it
                    resetResult()
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Curb65NumberField(
                value = respiratoryRate,
                onValueChange = {
                    respiratoryRate = it.filter(Char::isDigit)
                    resetResult()
                },
                label = "Respiratory Rate",
                suffix = "/min",
                error = rrError,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Curb65NumberField(
                    value = systolicBp,
                    onValueChange = {
                        systolicBp = it.filter(Char::isDigit)
                        resetResult()
                    },
                    label = "Systolic BP",
                    suffix = "mmHg",
                    error = sbpError,
                    modifier = Modifier.weight(1f)
                )

                Curb65NumberField(
                    value = diastolicBp,
                    onValueChange = {
                        diastolicBp = it.filter(Char::isDigit)
                        resetResult()
                    },
                    label = "Diastolic BP",
                    suffix = "mmHg",
                    error = dbpError,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Hypotension criterion: SBP < 90 mmHg or DBP \u2264 60 mmHg.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            Curb65NumberField(
                value = age,
                onValueChange = {
                    age = it.filter(Char::isDigit)
                    resetResult()
                },
                label = "Age",
                suffix = "years",
                error = ageError,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { calculated = true },
                modifier = Modifier.fillMaxWidth(),
                enabled = inputsValid
            ) {
                Icon(
                    imageVector = Icons.Outlined.Assessment,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.padding(start = 4.dp))
                Text("Calculate")
            }

            if (calculated && inputsValid) {
                Spacer(modifier = Modifier.height(18.dp))

                val severity = when {
                    score <= 1 -> "Low Severity (Group 1)"
                    score == 2 -> "Intermediate Severity (Group 2)"
                    else -> "High Severity (Group 3)"
                }

                val mortality = when (score) {
                    0 -> "0.7%"
                    1 -> "2.1%"
                    2 -> "9.2%"
                    3 -> "14.5%"
                    4 -> "40%"
                    else -> "57%"
                }

                val disposition = when {
                    score <= 1 ->
                        "Low 30-day mortality risk. Likely suitable for community-based management with oral antimicrobial therapy, subject to clinical judgement."
                    score == 2 ->
                        "Intermediate 30-day mortality risk. Consider short-stay inpatient or hospital-supervised outpatient management."
                    score == 3 ->
                        "High-severity community-acquired pneumonia. Manage as severe CAP with inpatient care and early senior review."
                    else ->
                        "High-severity community-acquired pneumonia with markedly elevated mortality. Urgent assessment for critical care / ICU admission is advised."
                }

                val criteriaMet = buildList {
                    if (confusion) add("\u2022 Confusion (new-onset)")
                    if (ureaHigh) add("\u2022 Serum urea > 7 mmol/L")
                    if (tachypnoea) add("\u2022 Tachypnoea (RR \u2265 30/min)")
                    if (hypotension) add("\u2022 Hypotension (SBP < 90 or DBP \u2264 60 mmHg)")
                    if (elderly) add("\u2022 Age \u2265 65 years")
                }
                val criteriaNotMet = buildList {
                    if (!confusion) add("\u2022 Confusion (new-onset)")
                    if (!ureaHigh) add("\u2022 Serum urea > 7 mmol/L")
                    if (!tachypnoea) add("\u2022 Tachypnoea (RR \u2265 30/min)")
                    if (!hypotension) add("\u2022 Hypotension (SBP < 90 or DBP \u2264 60 mmHg)")
                    if (!elderly) add("\u2022 Age \u2265 65 years")
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
                            text = "CURB-65 Score",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = score.toString(),
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "out of 5 points",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = severity,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Estimated 30-day mortality: $mortality",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Narratives: left-aligned, justified
                Curb65Section(
                    title = "Criteria Met (" + score + "/5)",
                    body = if (criteriaMet.isEmpty()) "None" else criteriaMet.joinToString("\n")
                )
                Curb65Section(
                    title = "Criteria Not Met",
                    body = if (criteriaNotMet.isEmpty()) "None" else criteriaNotMet.joinToString("\n")
                )
                Curb65Section(
                    title = "Interpretation and Disposition",
                    body = disposition
                )
                Curb65Section(
                    title = "Clinical Caveat",
                    body = "CURB-65 does not incorporate hypoxaemia, comorbidity, multilobar radiographic involvement, immunosuppression or social factors. It is an adjunct to, and not a substitute for, clinical assessment."
                )
                Curb65Section(
                    title = "References",
                    body = "Lim WS et al. Thorax 2003;58:377-382. British Thoracic Society guidelines for the management of community-acquired pneumonia in adults. Mortality estimates are derived from the original derivation and validation cohorts."
                )

                Spacer(modifier = Modifier.height(24.dp))
            } else {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun Curb65Section(title: String, body: String) {
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
private fun Curb65NumberField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    suffix: String,
    error: String?,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        singleLine = true,
        label = { Text(label) },
        suffix = { Text(suffix) },
        isError = error != null,
        supportingText = if (error != null) {
            { Text(error) }
        } else null,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}

@Composable
private fun Curb65SwitchRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
