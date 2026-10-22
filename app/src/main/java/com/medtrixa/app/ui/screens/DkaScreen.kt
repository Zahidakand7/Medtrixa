package com.medtrixa.app.ui.screens

import com.medtrixa.app.ui.components.BulletText
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medtrixa.app.ui.components.PremiumBackButton
import java.util.Locale

private fun String.toNumD(): Double? = trim().replace(',', '.').toDoubleOrNull()
private fun cleanD(s: String): String = s.filter { it.isDigit() || it == '.' || it == ',' }
private fun f1(x: Double): String = String.format(Locale.US, "%.1f", x)
private fun f0(x: Double): String = String.format(Locale.US, "%.0f", x)
private fun f2(x: Double): String = String.format(Locale.US, "%.2f", x).trimEnd('0').trimEnd('.')
private fun fU(x: Double): String = if (Math.abs(x - Math.rint(x)) < 0.05) f0(x) else f1(x)

private val DkaRed = Color(0xFFC62828)
private val DkaOrange = Color(0xFFEF6C00)
private val DkaYellow = Color(0xFFFFC107)
private val DkaGreen = Color(0xFF2E7D32)
private val DkaAmber = Color(0xFFB26A00)
private val DkaSlate = Color(0xFF607D8B)

/** 1 mild, 2 moderate, 3 severe */
private fun sevLabel(l: Int): String = when (l) {
    1 -> "Mild"
    2 -> "Moderate"
    else -> "Severe"
}

private fun sevColor(l: Int): Color = when (l) {
    1 -> DkaYellow
    2 -> DkaOrange
    else -> DkaRed
}

private fun sevOn(l: Int): Color = if (l == 1) Color(0xFF1B1B1B) else Color.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DkaScreen(onBackClick: () -> Unit) {
    var mmol by rememberSaveable { mutableStateOf(false) }
    var glucose by rememberSaveable { mutableStateOf("") }
    var knownDm by rememberSaveable { mutableStateOf(false) }
    var bhb by rememberSaveable { mutableStateOf("") }
    var urine2 by rememberSaveable { mutableStateOf(false) }
    var ph by rememberSaveable { mutableStateOf("") }
    var hco3 by rememberSaveable { mutableStateOf("") }
    var mental by rememberSaveable { mutableStateOf(0) }
    var weight by rememberSaveable { mutableStateOf("") }
    var frail by rememberSaveable { mutableStateOf(false) }
    var kVal by rememberSaveable { mutableStateOf("") }
    var curGlu by rememberSaveable { mutableStateOf("") }
    var rBhb by rememberSaveable { mutableStateOf("") }
    var rPh by rememberSaveable { mutableStateOf("") }
    var rHco3 by rememberSaveable { mutableStateOf("") }

    val primary = MaterialTheme.colorScheme.primary
    val background = MaterialTheme.colorScheme.background
    val unitG = if (mmol) "mmol/L" else "mg/dL"
    fun toMg(v: Double): Double = if (mmol) v * 18.0 else v
    fun gl(mg: Double): String = if (mmol) f1(mg / 18.0) + " mmol/L" else f0(mg) + " mg/dL"

    val gMg = glucose.toNumD()?.let { toMg(it) }
    val bhbV = bhb.toNumD()
    val phV = ph.toNumD()
    val hco3V = hco3.toNumD()

    // Diagnosis (2024 consensus): D, K and A criteria
    val dOk: Boolean? = if ((gMg != null && gMg >= 200.0) || knownDm) true else if (gMg == null) null else false
    val kOk: Boolean? = if ((bhbV != null && bhbV >= 3.0) || urine2) true else if (bhbV != null) false else null
    val aOk: Boolean? =
        if ((phV != null && phV < 7.3) || (hco3V != null && hco3V < 18.0)) true
        else if (phV != null || hco3V != null) false else null
    val verdict: String = when {
        dOk == true && kOk == true && aOk == true -> "MEETS DKA CRITERIA"
        dOk == false || kOk == false || aOk == false -> "DOES NOT MEET ALL DKA CRITERIA"
        else -> "ENTER VALUES TO CHECK CRITERIA"
    }
    val verdictColor = when (verdict) {
        "MEETS DKA CRITERIA" -> DkaRed
        "DOES NOT MEET ALL DKA CRITERIA" -> DkaSlate
        else -> DkaAmber
    }

    // Severity: highest category among the measures entered
    val sevParts = mutableListOf<Pair<String, Int>>()
    if (phV != null) sevParts.add("pH " + f2(phV) to (if (phV > 7.25) 1 else if (phV >= 7.0) 2 else 3))
    if (hco3V != null) sevParts.add("Bicarbonate " + fU(hco3V) to (if (hco3V >= 15.0) 1 else if (hco3V >= 10.0) 2 else 3))
    if (bhbV != null) sevParts.add("Beta-hydroxybutyrate " + fU(bhbV) to (if (bhbV > 6.0) 3 else 1))
    sevParts.add("Mental status" to (mental + 1))
    val sevLevel = sevParts.maxOf { it.second }
    val showSeverity = verdict == "MEETS DKA CRITERIA"

    val w = weight.toNumD()?.takeIf { it > 0.0 }
    val kV = kVal.toNumD()
    val curMg = curGlu.toNumD()?.let { toMg(it) }
    val rB = rBhb.toNumD()
    val rP = rPh.toNumD()
    val rH = rHco3.toNumD()

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
                    title = {
                        Text("DKA", fontSize = 20.sp, lineHeight = 26.sp, fontWeight = FontWeight.SemiBold)
                    },
                    navigationIcon = { PremiumBackButton(onClick = onBackClick, edgeInset = 12.dp) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize().imePadding(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 32.dp)
            ) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    listOf(primary.copy(alpha = 0.14f), primary.copy(alpha = 0.03f))
                                ),
                                RoundedCornerShape(24.dp)
                            )
                            .padding(horizontal = 20.dp, vertical = 16.dp)
                    ) {
                        Text("Diabetic ketoacidosis in adults", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(4.dp))
                        var dkaSubSize by remember { mutableStateOf(12.sp) }
                        Text(
                            "ADA, EASD, JBDS, AACE and DTS consensus report, 2024.",
                            fontSize = dkaSubSize,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            softWrap = false,
                            onTextLayout = { r ->
                                if (r.didOverflowWidth && dkaSubSize > 8.sp) dkaSubSize = dkaSubSize * 0.92f
                            }
                        )
                    }
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("Glucose units", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        FilterChip(
                            selected = !mmol, onClick = { mmol = false }, label = { Text("mg/dL") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = primary.copy(alpha = 0.15f), selectedLabelColor = primary
                            )
                        )
                        FilterChip(
                            selected = mmol, onClick = { mmol = true }, label = { Text("mmol/L") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = primary.copy(alpha = 0.15f), selectedLabelColor = primary
                            )
                        )
                    }
                }

                // 1. Diagnose and grade
                item {
                    DkaCard("1. Diagnose and grade") {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            DkaField("Glucose ($unitG)", glucose, { glucose = cleanD(it) }, Modifier.weight(1f))
                            DkaField("Beta-hydroxybutyrate (mmol/L)", bhb, { bhb = cleanD(it) }, Modifier.weight(1f))
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            DkaField("Venous pH", ph, { ph = cleanD(it) }, Modifier.weight(1f))
                            DkaField("Bicarbonate (mmol/L)", hco3, { hco3 = cleanD(it) }, Modifier.weight(1f))
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Switch(checked = knownDm, onCheckedChange = { knownDm = it })
                            Spacer(Modifier.width(10.dp))
                            Text("History of diabetes (any glucose)", fontSize = 13.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Switch(checked = urine2, onCheckedChange = { urine2 = it })
                            Spacer(Modifier.width(10.dp))
                            Text("Urine ketones 2+ or higher (if no BHB)", fontSize = 13.sp)
                        }
                        Text("Mental status", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Normal", "Drowsy", "Stupor or coma").forEachIndexed { i, label ->
                                FilterChip(
                                    selected = mental == i,
                                    onClick = { mental = i },
                                    label = { Text(label, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = primary.copy(alpha = 0.15f), selectedLabelColor = primary
                                    )
                                )
                            }
                        }

                        CriterionRow("D: glucose " + gl(200.0) + " or higher, or history of diabetes", dOk)
                        CriterionRow("K: beta-hydroxybutyrate 3.0 mmol/L or higher, or urine ketones 2+", kOk)
                        CriterionRow("A: pH below 7.3 and/or bicarbonate below 18 mmol/L", aOk)

                        Pill(verdict, verdictColor, Color.White)

                        if (showSeverity) {
                            Pill(sevLabel(sevLevel).uppercase() + " DKA", sevColor(sevLevel), sevOn(sevLevel))
                            sevParts.forEach { (label, lv) ->
                                Text(label + ": " + sevLabel(lv).lowercase(), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text(
                                when (sevLevel) {
                                    1 -> "Suggested setting: a regular or observation nursing unit."
                                    2 -> "Suggested setting: a step-down or intermediate care unit."
                                    else -> "Suggested setting: an intensive care unit."
                                },
                                fontSize = 14.sp, fontWeight = FontWeight.SemiBold, lineHeight = 20.sp,
                                textAlign = TextAlign.Justify
                            )
                            Text(
                                "Severity bands: mild is pH above 7.25, bicarbonate 15 or higher, normal mental status. Moderate is pH 7.0 to 7.25, " +
                                    "bicarbonate 10 to under 15, normal or drowsy. Severe is beta-hydroxybutyrate above 6, pH below 7.0, bicarbonate below 10, " +
                                    "stupor or coma. Not all criteria must be met. Clinical judgment and resources decide. " +
                                    "A concomitant critical illness or HHS also calls for intensive care.",
                                fontSize = 11.sp, lineHeight = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Justify
                            )
                        }
                        Text(
                            "Euglycemic DKA is covered by the history-of-diabetes option, and is more common with SGLT2 inhibitors. " +
                                "Anion gap is no longer a diagnostic or severity criterion.",
                            fontSize = 11.sp, lineHeight = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Justify
                        )
                    }
                }

                // 2. Potassium first
                item {
                    DkaCard("2. Potassium before insulin") {
                        DkaField("Serum potassium (mmol/L)", kVal, { kVal = cleanD(it) }, Modifier.fillMaxWidth())
                        if (kV != null) {
                            when {
                                kV < 3.5 -> {
                                    Pill("HOLD INSULIN", DkaRed, Color.White)
                                    Text("Potassium below 3.5: give 10 to 20 mmol/hour and postpone insulin until potassium is above 3.5 mmol/L.", fontSize = 14.sp, lineHeight = 20.sp,
                                        textAlign = TextAlign.Justify)
                                }
                                kV <= 5.0 -> {
                                    Pill("REPLACE AS PART OF FLUIDS", DkaGreen, Color.White)
                                    Text("Potassium 3.5 to 5.0: give 10 to 20 mmol in each liter of IV fluid to keep potassium at 4 to 5 mmol/L.", fontSize = 14.sp, lineHeight = 20.sp,
                                        textAlign = TextAlign.Justify)
                                }
                                else -> {
                                    Pill("NO POTASSIUM YET", DkaOrange, Color.White)
                                    Text("Potassium above 5.0: do not give potassium. Check potassium every 2 hours.", fontSize = 14.sp, lineHeight = 20.sp,
                                        textAlign = TextAlign.Justify)
                                }
                            }
                        }
                        Text(
                            "Measure potassium at baseline, 2 hours after starting insulin, then every 4 hours until DKA resolves.",
                            fontSize = 12.sp, lineHeight = 17.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Justify
                        )
                    }
                }

                // 3. Dosing
                item {
                    DkaCard("3. Fluids and insulin for this patient") {
                        DkaField("Weight (kg)", weight, { weight = cleanD(it) }, Modifier.fillMaxWidth())
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Switch(checked = frail, onCheckedChange = { frail = it })
                            Spacer(Modifier.width(10.dp))
                            Text("Older adult, or heart or kidney failure", fontSize = 13.sp)
                        }
                        Text("Fluids", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = primary)
                        Text(
                            if (frail) "Isotonic saline or a balanced crystalloid in smaller boluses (for example 250 mL), guided by hemodynamics and fluid balance."
                            else "Isotonic saline or a balanced crystalloid at 500 to 1,000 mL per hour for the first 2 to 4 hours, then adjust to hemodynamics, fluid balance and sodium.",
                            fontSize = 14.sp, lineHeight = 20.sp,
                            textAlign = TextAlign.Justify
                        )
                        Text(
                            "Replace the estimated fluid deficit over 24 to 48 hours, about half of it in the first 8 to 12 hours.",
                            fontSize = 12.sp, lineHeight = 17.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Justify
                        )
                        Text("Insulin", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = primary)
                        if (w != null) {
                            Text("Moderate or severe DKA: fixed-rate IV infusion 0.1 units/kg/hour = ${fU(0.1 * w)} units/hour (or a nurse-driven variable-rate protocol).", fontSize = 14.sp, lineHeight = 20.sp,
                                textAlign = TextAlign.Justify)
                            Text("After dextrose is added: reduce to 0.05 units/kg/hour = ${fU(0.05 * w)} units/hour until resolution.", fontSize = 14.sp, lineHeight = 20.sp,
                                textAlign = TextAlign.Justify)
                            Text("Mild or uncomplicated moderate DKA (non-critical setting, close nursing supervision): subcutaneous rapid-acting analogue 0.1 units/kg every hour = ${fU(0.1 * w)} units, or 0.2 units/kg every 2 hours = ${fU(0.2 * w)} units.", fontSize = 14.sp, lineHeight = 20.sp,
                                textAlign = TextAlign.Justify)
                            Text("To transition to subcutaneous insulin: estimate a total daily dose of 0.3 to 0.6 units/kg/day = ${fU(0.3 * w)} to ${fU(0.6 * w)} units/day, or use the pre-admission dose.", fontSize = 14.sp, lineHeight = 20.sp,
                                textAlign = TextAlign.Justify)
                        } else {
                            Text("Enter the weight to see the doses.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(
                            "Start insulin as soon as possible once potassium is above 3.5 mmol/L. Consider a 0.1 units/kg IV bolus only if IV access is delayed. " +
                                "If the patient takes basal insulin, it can be continued at the usual dose alongside the infusion. " +
                                "Start basal insulin at least 1 to 2 hours before stopping the infusion.",
                            fontSize = 12.sp, lineHeight = 17.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Justify
                        )
                    }
                }

                // 4. Dextrose trigger
                item {
                    DkaCard("4. Dextrose and glucose goal") {
                        DkaField("Current glucose ($unitG)", curGlu, { curGlu = cleanD(it) }, Modifier.fillMaxWidth())
                        if (curMg != null) {
                            if (curMg < 250.0) {
                                Pill("ADD DEXTROSE", DkaOrange, Color.White)
                                Text(
                                    "Glucose is below " + gl(250.0) + ": add dextrose 5% to 10% to the IV fluids and keep insulin running until ketonemia resolves" +
                                        (if (w != null) " (fixed rate " + fU(0.05 * w) + " units/hour)." else " (fixed rate 0.05 units/kg/hour)."),
                                    fontSize = 14.sp, lineHeight = 20.sp,
                                    textAlign = TextAlign.Justify
                                )
                            } else {
                                Pill("NO DEXTROSE YET", DkaGreen, Color.White)
                                Text("Glucose is " + gl(250.0) + " or higher: continue fluids and insulin as above.", fontSize = 14.sp, lineHeight = 20.sp,
                                    textAlign = TextAlign.Justify)
                            }
                        }
                        Text(
                            "Glucose goal until resolution: " + gl(150.0) + " to " + gl(200.0) + ". Check capillary glucose every 1 to 2 hours.",
                            fontSize = 12.sp, lineHeight = 17.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Justify
                        )
                    }
                }

                // 5. Resolution
                item {
                    DkaCard("5. Has DKA resolved?") {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            DkaField("Beta-hydroxybutyrate (mmol/L)", rBhb, { rBhb = cleanD(it) }, Modifier.weight(1f))
                            DkaField("Venous pH", rPh, { rPh = cleanD(it) }, Modifier.weight(1f))
                        }
                        DkaField("Bicarbonate (mmol/L)", rHco3, { rHco3 = cleanD(it) }, Modifier.fillMaxWidth())
                        if (rB != null && (rP != null || rH != null)) {
                            val ketoneOk = rB < 0.6
                            val acidOk = (rP != null && rP >= 7.3) || (rH != null && rH >= 18.0)
                            if (ketoneOk && acidOk) {
                                Pill("RESOLUTION CRITERIA MET", DkaGreen, Color.White)
                                Text("Beta-hydroxybutyrate is below 0.6 mmol/L and pH is 7.3 or higher, or bicarbonate is 18 mmol/L or higher.", fontSize = 14.sp, lineHeight = 20.sp,
                                    textAlign = TextAlign.Justify)
                            } else {
                                Pill("NOT YET RESOLVED", DkaOrange, Color.White)
                                Text(
                                    (if (!ketoneOk) "Beta-hydroxybutyrate is not yet below 0.6 mmol/L. " else "") +
                                        (if (!acidOk) "Neither venous pH 7.3 or higher nor bicarbonate 18 mmol/L or higher is reached." else ""),
                                    fontSize = 14.sp, lineHeight = 20.sp,
                                    textAlign = TextAlign.Justify
                                )
                            }
                        }
                        Text(
                            "Both parts are needed: beta-hydroxybutyrate below 0.6 mmol/L AND (venous pH 7.3 or higher OR bicarbonate 18 mmol/L or higher). " +
                                "Where beta-hydroxybutyrate cannot be measured, a normal anion gap is a reasonable substitute.",
                            fontSize = 12.sp, lineHeight = 17.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Justify
                        )
                    }
                }

                item {
                    DkaInfo(
                        "Monitoring",
                        listOf(
                            "Capillary glucose every 1 to 2 hours.",
                            "Electrolytes, phosphate, creatinine, beta-hydroxybutyrate and venous pH every 4 hours until resolution.",
                            "Venous pH is enough: arterial blood gases are not needed for acid\u2013base assessment.",
                            "Find and treat the precipitating cause."
                        )
                    )
                }

                item {
                    DkaInfo(
                        "Bicarbonate and phosphate",
                        listOf(
                            "Routine bicarbonate is not recommended. Consider it only for severe acidosis (pH below 7.0).",
                            "Routine phosphate is not recommended. Consider replacement if phosphate is below 1.0 mmol/L, particularly with muscle weakness or cardiac or respiratory impairment."
                        )
                    )
                }

                item {
                    Text(
                        "Source: Umpierrez GE, Davis GM, ElSayed NA, et al. Hyperglycemic crises in adults with diabetes: a consensus report " +
                            "(ADA, EASD, JBDS, AACE, DTS). Diabetes Care 2024;47(8):1257-1275, as summarized by Rodriguez Alvarez et al., " +
                            "Cleveland Clinic J Med 2025;92(3):152-158. This is adult guidance and does not apply to children. HHS and mixed " +
                            "features with significant ketonemia are managed as DKA. Decision support only; follow your local protocol.",
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 4.dp),
                        textAlign = TextAlign.Justify
                    )
                }
            }
        }
    }
}

@Composable
private fun DkaCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
            content()
        }
    }
}

@Composable
private fun DkaInfo(title: String, lines: List<String>) {
    DkaCard(title) {
        lines.forEach {
            BulletText(it)
        }
    }
}

@Composable
private fun DkaField(label: String, value: String, onChange: (String) -> Unit, modifier: Modifier) {
    val unitMatch = Regex("""(.*?)\s*\(([^()]*)\)""").matchEntire(label.trim())
    val fieldName = unitMatch?.groupValues?.get(1) ?: label
    val fieldUnit = unitMatch?.groupValues?.get(2)
    val focus = LocalFocusManager.current
    Column(modifier = modifier) {
        Text(
            fieldName,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 5.dp)
        )
        OutlinedTextField(
        value = value,
        onValueChange = onChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { focus.clearFocus() }),
        placeholder = { Text("Enter value") },
        trailingIcon = fieldUnit?.let { u -> { DkaUnitChip(u) } }
    )
    }
}

@Composable
private fun Pill(text: String, fill: Color, on: Color) {
    Box(
        modifier = Modifier
            .background(fill, RoundedCornerShape(50))
            .padding(horizontal = 16.dp, vertical = 7.dp)
    ) {
        Text(text, color = on, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, letterSpacing = 0.5.sp)
    }
}

@Composable
private fun CriterionRow(text: String, ok: Boolean?) {
    val mark = when (ok) {
        true -> "\u2714 Met"
        false -> "\u2716 Not met"
        null -> "? Enter value"
    }
    val color = when (ok) {
        true -> DkaGreen
        false -> DkaSlate
        null -> DkaAmber
    }
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(mark, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = color)
        Text(text, fontSize = 13.sp, lineHeight = 18.sp)
    }
}

@Composable
private fun DkaUnitChip(text: String) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.padding(end = 8.dp)
    ) {
        Text(
            text,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}
