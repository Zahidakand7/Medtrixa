package com.medtrixa.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medtrixa.app.ui.components.AutoSizeText
import com.medtrixa.app.ui.components.PremiumBackButton
import kotlin.math.roundToInt

private val InsIndigo = Color(0xFF3949AB)
private val InsTeal = Color(0xFF00897B)
private val InsBlue = Color(0xFF1565C0)
private val InsAmber = Color(0xFFEF6C00)
private val InsRed = Color(0xFFC62828)
private val InsPurple = Color(0xFF6A1B9A)

private fun insUnits(v: Double): String = v.roundToInt().toString()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsulinDosing2Screen(onBackClick: () -> Unit) {
    val primary = MaterialTheme.colorScheme.primary
    val background = MaterialTheme.colorScheme.background

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(primary.copy(alpha = 0.06f).compositeOver(background), background)
                )
            )
    ) {
        Scaffold(
            modifier = Modifier.statusBarsPadding(),
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Insulin Dosing 2", fontSize = 20.sp, lineHeight = 26.sp, fontWeight = FontWeight.SemiBold) },
                    navigationIcon = { PremiumBackButton(onClick = onBackClick, edgeInset = 12.dp) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 32.dp)
            ) {
                item { InsHero() }
                item {
                    InsNote(
                        "Avoid therapeutic inertia: reassess and modify treatment regularly (every 3-6 months) and do not delay changes when goals are not met. Reinforce weight management and physical activity, and provide diabetes self-management education and support (DSMES) throughout.",
                        InsAmber
                    )
                }
                item { InsStartInsulinFirst() }
                item { InsConnector("Injectable needed to reach goals") }
                item { InsStepGlp1() }
                item { InsConnector("Above goal, or GLP-1 RA not suitable") }
                item { InsStepBasal() }
                item { InsConnector("Every visit") }
                item { InsStepAssess() }
                item { InsConnector("Glycemia above goal") }
                item { InsStepPrandial() }
                item { InsConnector("Bedtime NPH users") }
                item { InsStepNphConversion() }
                item { InsConnector("Still above goal: choose a plan") }
                item { InsRegimenStepwise() }
                item { InsRegimenSelfMixed() }
                item { InsRegimenPremix() }
                item { InsSafetyCard() }
                item { InsDoseCalculator() }
                item { InsReferences() }
            }
        }
    }
}

@Composable
private fun InsHero() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF1A237E), InsIndigo, InsTeal)))
            .padding(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            AutoSizeText(
                text = "ADA STANDARDS OF CARE 2026 - FIGURE 9.5",
                style = TextStyle(color = Color.White.copy(alpha = 0.85f), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp),
                minFontSize = 8.sp
            )
            AutoSizeText(
                text = "Insulin Intensification",
                style = TextStyle(color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            )
            Text(
                "Stepwise injectable therapy for adults with type 2 diabetes: from GLP-1 RA or dual GIP/GLP-1 RA to basal, prandial and premix plans, with a built-in dose calculator.",
                color = Color.White.copy(alpha = 0.92f),
                fontSize = 13.sp,
                lineHeight = 19.sp,
                textAlign = TextAlign.Justify
            )
            Spacer(Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.20f))
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
                Text("5 steps - 3 regimens", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            }
        }
    }
}

@Composable
private fun InsNote(text: String, color: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(14.dp)
    ) {
        Text(text, color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp, lineHeight = 19.sp, textAlign = TextAlign.Justify)
    }
}

@Composable
private fun InsConnector(label: String) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(InsBlue.copy(alpha = 0.12f))
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text(label, color = InsBlue, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
        }
        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun InsStepCard(
    number: String,
    title: String,
    color: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(6.dp)
                .background(Brush.horizontalGradient(listOf(color, color.copy(alpha = 0.35f))))
        )
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(32.dp).clip(CircleShape).background(color),
                    contentAlignment = Alignment.Center
                ) {
                    Text(number, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    AutoSizeText(
                        text = title,
                        style = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    )
                }
            }
            content()
        }
    }
}

@Composable
private fun InsBody(text: String) {
    Text(
        text,
        color = MaterialTheme.colorScheme.onSurface,
        fontSize = 14.sp,
        lineHeight = 21.sp,
        textAlign = TextAlign.Justify
    )
}

@Composable
private fun InsBullet(text: String) {
    Row {
        Text("•", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp, modifier = Modifier.width(16.dp))
        Text(
            text,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp,
            lineHeight = 21.sp,
            textAlign = TextAlign.Justify,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun InsBlock(label: String, color: Color, items: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp, maxLines = 1)
        items.forEach { InsBullet(it) }
    }
}

@Composable
private fun InsStartInsulinFirst() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = InsRed.copy(alpha = 0.08f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = InsRed)
                Spacer(Modifier.width(10.dp))
                Box(Modifier.weight(1f)) {
                    AutoSizeText(
                        text = "Consider insulin first when",
                        style = TextStyle(color = InsRed, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    )
                }
            }
            InsBullet("Symptoms of hyperglycemia are present (for example polyuria or polydipsia)")
            InsBullet("Evidence of catabolism (unexpected weight loss)")
            InsBullet("A1C is very high (above 10%, above 86 mmol/mol)")
            InsBullet("Blood glucose is very high (300 mg/dL or more, 16.7 mmol/L or more)")
            InsBullet("Type 1 diabetes is a possibility")
            Text(
                "As glucose toxicity resolves, the plan can often be simplified or changed to noninsulin agents.",
                color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp, lineHeight = 17.sp, textAlign = TextAlign.Justify
            )
        }
    }
}

@Composable
private fun InsStepGlp1() {
    InsStepCard("1", "GLP-1 RA or GIP/GLP-1 RA", InsTeal) {
        InsBody("Consider a GLP-1 RA or dual GIP and GLP-1 RA in most individuals before insulin. Without severe hyperglycemia or a hyperglycemic crisis, GLP-1-based therapy is preferred to insulin for initial or add-on therapy.")
        InsBlock("INITIATION", InsTeal, listOf("Start the appropriate dose for the agent selected (varies within class)."))
        InsBlock("TITRATION", InsTeal, listOf("Titrate to the maintenance dose (varies within class)."))
        InsBlock(
            "CHOOSING THE AGENT", InsTeal, listOf(
                "Consider individual preference, glycemic lowering, weight-lowering effect and injection frequency.",
                "With cardiovascular disease, choose a GLP-1 RA with proven CVD benefit.",
                "Oral or injectable GLP-1 RAs are both appropriate.",
                "Do not combine with a DPP-4 inhibitor: it adds no extra glucose lowering."
            )
        )
        InsNote("Already on a GLP-1 RA or dual GIP/GLP-1 RA, not appropriate for the person, or insulin preferred: go to basal insulin.", InsTeal)
    }
}

@Composable
private fun InsStepBasal() {
    InsStepCard("2", "Add basal insulin", InsBlue) {
        InsBody("Basal analog or bedtime NPH. Choose based on person-specific considerations, including cost (see Table 9.4 in the Standards).")
        InsBlock("INITIATION", InsBlue, listOf("Start 10 units a day, OR 0.1-0.2 units/kg a day."))
        InsBlock(
            "TITRATION", InsBlue, listOf(
                "Set a fasting plasma glucose (FPG) goal (see Section 6: Glycemic Goals).",
                "Use an evidence-based titration algorithm, for example increase by 2 units every 3 days until the FPG goal is reached without hypoglycemia.",
                "For hypoglycemia, determine the cause. If there is no clear reason, lower the dose by 10-20%."
            )
        )
        InsBlock(
            "WHEN STARTING", InsBlue, listOf(
                "Consider prescribing glucagon for emergent hypoglycemia.",
                "If not on CGM, consider adding CGM.",
                "Continue metformin, SGLT2 inhibitor and GLP-1 RA unless contraindicated or not tolerated, and reassess sulfonylurea or meglitinide doses."
            )
        )
        InsBlock(
            "PRACTICE POINTS", InsBlue, listOf(
                "On a GLP-1 RA plus basal insulin, consider a fixed-ratio combination product (IDegLira or iGlarLixi).",
                "Switching basal insulins: often unit for unit. Consider a 10-20% lower start in very tight control or high hypoglycemia risk; this is typically needed when switching from NPH or U-300 glargine.",
                "Consider switching evening NPH to a basal analog if hypoglycemia develops, or evening doses are often forgotten and a morning long-acting dose would suit better.",
                "Consider dosing NPH in the morning for steroid-induced hyperglycemia."
            )
        )
    }
}

@Composable
private fun InsStepAssess() {
    InsStepCard("3", "Assess basal dose at every visit", InsAmber) {
        InsBody("Look for clinical signals of overbasalization (a basal dose higher than needed, often masking insufficient mealtime insulin) and the need for adjunctive therapy:")
        InsBullet("Elevated bedtime-to-morning and/or postprandial-to-preprandial glucose differential (for example a bedtime-to-morning rise of 50 mg/dL or more, 2.8 mmol/L or more)")
        InsBullet("Hypoglycemia, aware or unaware")
        InsBullet("High glucose variability")
        InsNote("Basal titrated to an acceptable fasting glucose but glycemia still above goal, significant postprandial hyperglycemia, or signs of overbasalization: advance to combination injectable therapy.", InsAmber)
        InsNote("Not yet on a GLP-1 RA or dual GIP/GLP-1 RA: consider adding one, alone or as a fixed-ratio product, before prandial insulin. It addresses prandial control with less hypoglycemia and weight gain. If glycemia stays above goal, add prandial insulin.", InsTeal)
    }
}

@Composable
private fun InsStepPrandial() {
    InsStepCard("4", "Add prandial insulin", InsPurple) {
        InsBody("Usually one dose with the largest meal, or the meal with the greatest postprandial glucose excursion. Prandial insulin can be dosed on its own or mixed with NPH.")
        InsBlock(
            "INITIATION", InsPurple, listOf(
                "Start 4 units a day, or 10% of the basal insulin dose.",
                "If A1C is below 8% (below 64 mmol/mol), consider lowering the basal dose by 4 units a day or 10%."
            )
        )
        InsBlock(
            "TITRATION", InsPurple, listOf(
                "Increase the dose by 1-2 units or 10-15%, twice weekly.",
                "For hypoglycemia, determine the cause. If there is no clear reason, lower the corresponding dose by 10-20%."
            )
        )
        InsBlock(
            "PRACTICE POINTS", InsPurple, listOf(
                "Options: rapid- or ultra-rapid-acting analogs, short-acting human insulin, or inhaled human insulin (contraindicated in chronic lung disease such as asthma or COPD).",
                "With significant prandial additions, especially at the evening meal, consider decreasing basal insulin.",
                "Keep metformin, SGLT2 inhibitor and GLP-1 RA. Limit or stop sulfonylureas, meglitinides and DPP-4 inhibitors.",
                "If adding prandial insulin to NPH, consider a self-mixed or premixed plan to reduce the number of injections."
            )
        )
    }
}

@Composable
private fun InsStepNphConversion() {
    InsStepCard("5", "Bedtime NPH to twice-daily NPH", InsIndigo) {
        InsBody("One possible approach, based on individual needs and current glycemic management:")
        InsBlock(
            "INITIATION", InsIndigo, listOf(
                "Total dose = 80% of the current bedtime NPH dose.",
                "2/3 in the morning.",
                "1/3 at bedtime."
            )
        )
        InsBlock("TITRATION", InsIndigo, listOf("Titrate based on individualized needs."))
    }
}

@Composable
private fun InsRegimenStepwise() {
    InsStepCard("6a", "Stepwise prandial injections", InsPurple) {
        InsBullet("Add prandial injections in steps: two, then three additional injections.")
        InsBullet("Proceed to a full basal-bolus plan: basal insulin plus prandial insulin with each meal.")
        InsBody("Basal-prandial plans suit people with irregular eating times or variable meal content.")
    }
}

@Composable
private fun InsRegimenSelfMixed() {
    InsStepCard("6b", "Self-mixed / split plan", InsBlue) {
        InsBody("NPH and short or rapid-acting insulin can be adjusted separately.")
        InsBlock(
            "INITIATION", InsBlue, listOf(
                "Total NPH dose = 80% of the current NPH dose.",
                "2/3 before breakfast, 1/3 before dinner.",
                "Add 4 units of short or rapid-acting insulin to each injection, or 10% of the reduced NPH dose."
            )
        )
        InsBlock("TITRATION", InsBlue, listOf("Titrate each component of the plan based on individualized needs."))
    }
}

@Composable
private fun InsRegimenPremix() {
    InsStepCard("6c", "Twice-daily premixed plan", InsTeal) {
        InsBlock(
            "INITIATION", InsTeal, listOf(
                "Usually unit for unit, at the same total insulin dose. Adjustment to individual needs may be required."
            )
        )
        InsBlock("TITRATION", InsTeal, listOf("Titrate based on individualized needs."))
        InsBody("A simple option when injecting before one or more meals is not feasible. Human premixed insulins (for example NPH/regular 70/30) are often lower cost, with a higher hypoglycemia risk than analogs.")
    }
}

@Composable
private fun InsSafetyCard() {
    InsStepCard("!", "Safety and monitoring", InsRed) {
        InsBullet("Prescribe glucagon to everyone taking insulin or at high risk of hypoglycemia. Products that need no reconstitution are preferred. Teach family and caregivers how to use it.")
        InsBullet("CGM is recommended for adults on insulin therapy.")
        InsBullet("Automated insulin delivery systems should be offered to adults with type 1 or type 2 diabetes on insulin, based on their needs and preferences.")
        InsBullet("Insulin needs fall as eGFR falls, and hypoglycemia risk and duration of action rise with kidney impairment: titrate per clinical response.")
        InsBullet("Adding or escalating a GLP-1 RA or dual GIP/GLP-1 RA: reassess the insulin dose.")
        InsBullet("Review the plan every 3-6 months and simplify or deintensify when treatment burden or hypoglycemia risk is high.")
    }
}

@Composable
private fun InsResult(label: String, value: String, color: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(color.copy(alpha = 0.10f))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(label, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.0.sp)
        Text(value, color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp, lineHeight = 20.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun InsDoseCalculator() {
    var weightText by rememberSaveable { mutableStateOf("") }
    var doseText by rememberSaveable { mutableStateOf("") }
    val weight = weightText.replace(',', '.').toDoubleOrNull()?.takeIf { it > 0 }
    val dose = doseText.replace(',', '.').toDoubleOrNull()?.takeIf { it > 0 }

    InsStepCard("Rx", "Dose calculator", InsIndigo) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
                value = weightText,
                onValueChange = { weightText = it },
                modifier = Modifier.weight(1f),
                singleLine = true,
                label = { Text("Weight (kg)", maxLines = 1) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            OutlinedTextField(
                value = doseText,
                onValueChange = { doseText = it },
                modifier = Modifier.weight(1f),
                singleLine = true,
                label = { Text("Daily dose (units)", maxLines = 1) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
        }
        Text(
            "Daily dose = current basal or NPH dose. For a premix conversion, enter the total daily insulin dose.",
            color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp, lineHeight = 17.sp
        )
        if (weight == null && dose == null) {
            Text("Enter a weight and/or a current daily dose to see starting doses.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
        }
        if (weight != null) {
            InsResult(
                "BASAL START",
                "10 units a day, or ${insUnits(0.1 * weight)}-${insUnits(0.2 * weight)} units a day (0.1-0.2 units/kg)",
                InsBlue
            )
        }
        if (dose != null) {
            val reduced = 0.8 * dose
            InsResult(
                "SWITCHING BASAL INSULIN",
                "Unit for unit: ${insUnits(dose)} units. With a 10-20% reduction (from NPH or U-300 glargine, or high hypoglycemia risk): ${insUnits(0.8 * dose)}-${insUnits(0.9 * dose)} units.",
                InsBlue
            )
            InsResult(
                "PRANDIAL START",
                "4 units, or ${insUnits(0.1 * dose)} units (10% of basal). If A1C is below 8%, consider lowering basal to ${insUnits(dose - 4)} units (4 fewer) or ${insUnits(0.9 * dose)} units (10% less).",
                InsPurple
            )
            InsResult(
                "BEDTIME NPH TO TWICE DAILY",
                "Total ${insUnits(reduced)} units: ${insUnits(reduced * 2 / 3)} units in the morning, ${insUnits(reduced / 3)} units at bedtime.",
                InsIndigo
            )
            InsResult(
                "SELF-MIXED / SPLIT",
                "NPH ${insUnits(reduced * 2 / 3)} units before breakfast and ${insUnits(reduced / 3)} units before dinner. Add 4 units of rapid-acting insulin to each injection, or ${insUnits(0.1 * reduced)} units (10% of the reduced NPH dose).",
                InsBlue
            )
            InsResult(
                "TWICE-DAILY PREMIX",
                "Same total dose, unit for unit: ${insUnits(dose)} units a day, then adjust to individual needs.",
                InsTeal
            )
        }
        Text(
            "Doses are rounded to whole units. Starting doses and titration need clinical judgment.",
            color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp, lineHeight = 17.sp
        )
    }
}

@Composable
private fun InsReferences() {
    InsStepCard("Ref", "Source", InsAmber) {
        InsBullet("American Diabetes Association Professional Practice Committee. 9. Pharmacologic Approaches to Glycemic Treatment: Standards of Care in Diabetes - 2026. Diabetes Care 2026;49(Suppl 1):S183-S215. Figure 9.5 (page S198) and recommendations 9.17-9.28.")
        Text(
            "Educational decision support. Individualize therapy and check against the current Standards of Care before clinical use.",
            color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp, lineHeight = 17.sp, textAlign = TextAlign.Justify
        )
    }
}
