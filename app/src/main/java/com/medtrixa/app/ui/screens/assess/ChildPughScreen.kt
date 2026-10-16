package com.medtrixa.app.ui.screens.assess

import androidx.activity.compose.BackHandler
import androidx.compose.ui.unit.sp
import com.medtrixa.app.ui.components.PremiumBackButton
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

private data class CpOption(val label: String, val detail: String, val points: Int)

private val cpAscitesOptions = listOf(
    CpOption("Absent", "No ascites on clinical or ultrasound assessment", 1),
    CpOption("Mild or diuretic-responsive", "Slight ascites, detectable on imaging or controlled with diuretic therapy", 2),
    CpOption("Moderate\u2013severe or refractory", "Clinically evident ascites despite diuretic therapy", 3)
)

private val cpEncephalopathyOptions = listOf(
    CpOption("Absent", "No hepatic encephalopathy", 1),
    CpOption("Grade 1\u20132 (West Haven)", "Mild\u2013moderate, or controlled with medical therapy", 2),
    CpOption("Grade 3\u20134 (West Haven)", "Severe, or refractory to medical therapy", 3)
)

private fun cpFormat(x: Double): String =
    if (x % 1.0 == 0.0) x.toInt().toString() else x.toString()

private fun cpError(text: String, v: Double?, min: Double, max: Double, unit: String): String? =
    if (text.isNotEmpty() && (v == null || v < min || v > max))
        "Plausible range: " + cpFormat(min) + "-" + cpFormat(max) + " " + unit
    else null

private fun cpDecimal(new: String, old: String): String {
    val f = new.filter { it.isDigit() || it == '.' }
    return if (f.count { it == '.' } > 1) old else f
}

@Composable
fun ChildPughScreen(
    onBackClick: () -> Unit
) {
    BackHandler { onBackClick() }

    var si by remember { mutableStateOf(false) }
    var unitChosen by remember { mutableStateOf(false) }
    var bilirubin by remember { mutableStateOf("") }
    var albumin by remember { mutableStateOf("") }
    var inr by remember { mutableStateOf("") }
    var ascites by remember { mutableStateOf<Int?>(null) }
    var encephalopathy by remember { mutableStateOf<Int?>(null) }
    var calculated by remember { mutableStateOf(false) }

    val bilUnit = if (si) "\u00B5mol/L" else "mg/dL"
    val albUnit = if (si) "g/L" else "g/dL"

    val bil = bilirubin.toDoubleOrNull()
    val alb = albumin.toDoubleOrNull()
    val inrValue = inr.toDoubleOrNull()

    val bilError = cpError(bilirubin, bil, if (si) 2.0 else 0.1, if (si) 855.0 else 50.0, bilUnit)
    val albError = cpError(albumin, alb, if (si) 5.0 else 0.5, if (si) 60.0 else 6.0, albUnit)
    val inrError = cpError(inr, inrValue, 0.5, 10.0, "")

    val ready = bil != null && alb != null && inrValue != null &&
        ascites != null && encephalopathy != null &&
        bilError == null && albError == null && inrError == null

    val bilPts = if (bil == null) 0 else if (si) {
        when { bil < 34.0 -> 1; bil <= 50.0 -> 2; else -> 3 }
    } else {
        when { bil < 2.0 -> 1; bil <= 3.0 -> 2; else -> 3 }
    }
    val albPts = if (alb == null) 0 else if (si) {
        when { alb > 35.0 -> 1; alb >= 28.0 -> 2; else -> 3 }
    } else {
        when { alb > 3.5 -> 1; alb >= 2.8 -> 2; else -> 3 }
    }
    val inrPts = if (inrValue == null) 0 else when {
        inrValue < 1.7 -> 1
        inrValue <= 2.3 -> 2
        else -> 3
    }
    val ascPts = ascites ?: 0
    val hePts = encephalopathy ?: 0
    val total = bilPts + albPts + inrPts + ascPts + hePts

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
                        text = "Child-Pugh Score",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 26.sp
                    )
                    Text(
                        text = "Liver Disease Severity Assessment",
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

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Units",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = bilirubin,
                onValueChange = { bilirubin = cpDecimal(it, bilirubin); calculated = false },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Total Bilirubin") },
                suffix = { Text(bilUnit) },
                isError = bilError != null,
                supportingText = {
                    Text(
                        bilError ?: if (si) "< 34 = 1   |   34\u201350 = 2   |   > 50 = 3"
                        else "< 2 = 1   |   2\u20133 = 2   |   > 3 = 3"
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = albumin,
                onValueChange = { albumin = cpDecimal(it, albumin); calculated = false },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Serum Albumin") },
                suffix = { Text(albUnit) },
                isError = albError != null,
                supportingText = {
                    Text(
                        albError ?: if (si) "> 35 = 1   |   28\u201335 = 2   |   < 28 = 3"
                        else "> 3.5 = 1   |   2.8\u20133.5 = 2   |   < 2.8 = 3"
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = inr,
                onValueChange = { inr = cpDecimal(it, inr); calculated = false },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("INR") },
                isError = inrError != null,
                supportingText = {
                    Text(inrError ?: "< 1.7 = 1   |   1.7\u20132.3 = 2   |   > 2.3 = 3")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            Spacer(modifier = Modifier.height(14.dp))

            CpOptionGroup(
                title = "Ascites",
                helper = "Clinical and imaging assessment",
                options = cpAscitesOptions,
                selected = ascites,
                onSelect = { ascites = it; calculated = false }
            )

            CpOptionGroup(
                title = "Hepatic Encephalopathy",
                helper = "West Haven grading",
                options = cpEncephalopathyOptions,
                selected = encephalopathy,
                onSelect = { encephalopathy = it; calculated = false }
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

                val cls = when {
                    total <= 6 -> "A"
                    total <= 9 -> "B"
                    else -> "C"
                }
                val classTitle = when (cls) {
                    "A" -> "Class A (5\u20136): Well-compensated"
                    "B" -> "Class B (7\u20139): Significant functional compromise"
                    else -> "Class C (10\u201315): Decompensated"
                }
                val oneYear = when (cls) { "A" -> "~100%"; "B" -> "~80%"; else -> "~45%" }
                val twoYear = when (cls) { "A" -> "~85%"; "B" -> "~60%"; else -> "~35%" }
                val periop = when (cls) { "A" -> "~10%"; "B" -> "~30%"; else -> "~82%" }

                val interpretation = when (cls) {
                    "A" -> "Compensated cirrhosis with preserved hepatic synthetic function and minimal clinical manifestations of portal hypertension."
                    "B" -> "Significant hepatic functional compromise. Cirrhosis is decompensated or at high risk of decompensation."
                    else -> "Severe hepatic dysfunction consistent with decompensated cirrhosis and advanced synthetic failure."
                }
                val management = when (cls) {
                    "A" -> "Treat the underlying aetiology and precipitating factors. Continue hepatocellular carcinoma surveillance (ultrasound with or without AFP at 6-monthly intervals) and varices screening according to portal hypertension guidance. Elective surgery is generally tolerated with preoperative optimisation."
                    "B" -> "Hepatology review is advised, with optimisation of ascites, encephalopathy, nutrition and varices prophylaxis. Consider referral for liver transplant evaluation. Elective surgery carries substantially increased risk and warrants multidisciplinary assessment."
                    else -> "Urgent hepatology referral and assessment for liver transplantation are advised (MELD 3.0 for allocation priority). Where transplantation is not feasible, early palliative care input is appropriate. Elective surgery is generally contraindicated."
                }
                val drugs = when (cls) {
                    "A" -> "Standard dosing is generally acceptable. Exercise caution with hepatically cleared agents and avoid hepatotoxins."
                    "B" -> "Dose reduction or avoidance of extensively hepatically metabolised drugs is generally required per product labelling. Avoid NSAIDs and sedatives."
                    else -> "Avoid or substantially reduce the dose of hepatically cleared drugs; several agents are contraindicated per product labelling. Avoid NSAIDs, benzodiazepines and other sedatives, and nephrotoxins."
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
                            text = "Child-Pugh Score",
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
                            text = "out of 15 points",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = classTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Estimated survival: $oneYear at 1 year, $twoYear at 2 years",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Narratives: left-aligned only
                CpSection(
                    title = "Component Scores",
                    body = "\u2022 Total bilirubin (" + cpFormat(bil ?: 0.0) + " " + bilUnit + "): " + bilPts + "\n" +
                        "\u2022 Serum albumin (" + cpFormat(alb ?: 0.0) + " " + albUnit + "): " + albPts + "\n" +
                        "\u2022 INR (" + cpFormat(inrValue ?: 0.0) + "): " + inrPts + "\n" +
                        "\u2022 Ascites: " + ascPts + "\n" +
                        "\u2022 Hepatic encephalopathy: " + hePts
                )
                CpSection(title = "Interpretation", body = interpretation)
                CpSection(
                    title = "Prognostic Impact",
                    body = "\u2022 Estimated 1-year survival: $oneYear\n" +
                        "\u2022 Estimated 2-year survival: $twoYear\n" +
                        "\u2022 Estimated perioperative mortality (abdominal surgery): $periop"
                )
                CpSection(title = "Management Implications", body = management)
                CpSection(title = "Pharmacological Implications", body = drugs)
                CpSection(
                    title = "Clinical Caveat",
                    body = "Ascites and encephalopathy grading are subjective, and INR varies between laboratories and reagents. In primary biliary cholangitis and primary sclerosing cholangitis, modified bilirubin thresholds (< 4, 4\u201310, > 10 mg/dL) are used. Survival and perioperative figures derive from historical cohorts and may not reflect contemporary care. Interpret alongside MELD 3.0 and the overall clinical picture."
                )
                CpSection(
                    title = "References",
                    body = "Child CG, Turcotte JG. Surgery and portal hypertension. In: The Liver and Portal Hypertension. 1964.\nPugh RNH et al. Br J Surg 1973;60:646-649.\nEASL Clinical Practice Guidelines for the management of patients with decompensated cirrhosis. J Hepatol 2018;69:406-460."
                )

                Spacer(modifier = Modifier.height(24.dp))
            } else {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun CpUnitChoice(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .selectable(selected = selected, onClick = onClick, role = Role.RadioButton)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = null)
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun CpSection(title: String, body: String) {
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
private fun CpOptionGroup(
    title: String,
    helper: String,
    options: List<CpOption>,
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
