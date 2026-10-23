package com.medtrixa.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusEvent
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.roundToInt

// Sources: ADA Standards of Care in Diabetes-2026, sections 16 and 6 (Diabetes Care 2026;49(Suppl 1));
// RABBIT 2 (Umpierrez et al., Diabetes Care 2007;30:2181-2186) and RABBIT 2 Surgery (Diabetes Care 2011;34:256-261);
// US FDA glucagon prescribing information. Wording is our own. Not affiliated with or endorsed by the ADA.
private const val INSULIN_SOURCE = "ADA 2026 · RABBIT 2 · FDA labels"
private const val INSULIN_CHECKED = "checked Oct 2026"

private fun insGl(mg: Double): String =
    String.format(Locale.US, "%.0f mg/dL (%.1f mmol/L)", mg, mg / 18.0)

@Composable
private fun InsHeroBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF0D47A1), Color(0xFF1565C0), Color(0xFF00897B))))
            .padding(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            AutoSizeText(
                text = "ADA STANDARDS OF CARE 2026 - INPATIENT",
                style = TextStyle(color = Color.White.copy(alpha = 0.85f), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp),
                minFontSize = 8.sp
            )
            AutoSizeText(
                text = "Inpatient Insulin Dosing",
                style = TextStyle(color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            )
            Text(
                "Weight-based starting estimates, a hypoglycemia check and treatment steps for hospitalized adults with type 2 diabetes.",
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
                AutoSizeText(
                    text = "$INSULIN_SOURCE · $INSULIN_CHECKED",
                    style = TextStyle(color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold),
                    minFontSize = 8.sp
                )
            }
        }
    }
}

@Composable
private fun InsAlertCard(danger: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = danger.copy(alpha = 0.08f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = danger)
                Spacer(Modifier.width(10.dp))
                Box(Modifier.weight(1f)) {
                    AutoSizeText(
                        text = "High-alert medication",
                        style = TextStyle(color = danger, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    )
                }
            }
            Text(
                "Insulin errors cause serious harm. These are starting estimates, not orders. " +
                    "A prescriber and pharmacist confirm every dose against the patient and your local protocol.",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                textAlign = TextAlign.Justify
            )
        }
    }
}

@Composable
private fun InsCard(title: String, accent: Color, number: String = "", content: @Composable ColumnScope.() -> Unit) {
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
                .background(Brush.horizontalGradient(listOf(accent, accent.copy(alpha = 0.35f))))
        )
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (number.isNotEmpty()) {
                    Box(
                        modifier = Modifier.size(30.dp).clip(CircleShape).background(accent),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(number, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Spacer(Modifier.width(12.dp))
                }
                Column(Modifier.weight(1f)) {
                    AutoSizeText(
                        text = title,
                        style = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
private fun InsBullet(text: String) {
    Row(Modifier.padding(vertical = 3.dp)) {
        Text("•", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(16.dp))
        Text(
            text,
            fontSize = 13.sp,
            lineHeight = 19.sp,
            textAlign = TextAlign.Justify,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun InsSmall(text: String, color: Color, bold: Boolean = false) {
    Text(
        text,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        textAlign = TextAlign.Justify,
        fontWeight = if (bold) FontWeight.SemiBold else FontWeight.Normal,
        color = color
    )
}

@Composable
private fun InsLabel(text: String, color: Color) {
    Text(text, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp, maxLines = 1)
}

@Composable
private fun InsStat(label: String, value: String, sub: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.10f))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        InsLabel(label.uppercase(Locale.US), color)
        AutoSizeText(
            text = value,
            style = TextStyle(color = color, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        )
        Text(sub, fontSize = 11.sp, lineHeight = 15.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun InsField(value: String, onValueChange: (String) -> Unit, label: String) {
    val requester = remember { BringIntoViewRequester() }
    val scope = rememberCoroutineScope()
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, maxLines = 1) },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = Modifier
            .fillMaxWidth()
            .bringIntoViewRequester(requester)
            .onFocusEvent { st ->
                if (st.isFocused) scope.launch {
                    delay(350)
                    requester.bringIntoView()
                }
            }
    )
}

@Composable
private fun GlucoseStrip(mg: Double?) {
    val segs = listOf(
        14f to Color(0xFFD64545),
        16f to Color(0xFFF08A24),
        30f to Color(0xFF9AA5B1),
        80f to Color(0xFF2E9E6B),
        220f to Color(0xFFE5B300),
        20f to Color(0xFFD64545)
    )
    BoxWithConstraints(Modifier.fillMaxWidth().height(34.dp)) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(12.dp)
                .align(Alignment.BottomStart)
                .clip(RoundedCornerShape(6.dp))
        ) {
            segs.forEach { (w, c) -> Box(Modifier.weight(w).fillMaxHeight().background(c)) }
        }
        if (mg != null) {
            val f = ((mg.coerceIn(40.0, 420.0) - 40.0) / 380.0).toFloat()
            Box(
                Modifier
                    .align(Alignment.TopStart)
                    .offset(x = maxWidth * f - 8.dp)
                    .size(16.dp)
                    .background(MaterialTheme.colorScheme.surface, CircleShape)
                    .border(3.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsulinDosingScreen(onBackClick: () -> Unit) {
    var useMmol by rememberSaveable { mutableStateOf(false) }
    var situation by rememberSaveable { mutableStateOf(0) }
    var eating by rememberSaveable { mutableStateOf(true) }
    var age70 by rememberSaveable { mutableStateOf(false) }
    var kidney by rememberSaveable { mutableStateOf(false) }
    var otherRisk by rememberSaveable { mutableStateOf(false) }
    var weightText by rememberSaveable { mutableStateOf("") }
    var glucoseText by rememberSaveable { mutableStateOf("") }
    var currentText by rememberSaveable { mutableStateOf("") }

    val primary = MaterialTheme.colorScheme.primary
    val bg = MaterialTheme.colorScheme.background
    val danger = MaterialTheme.colorScheme.error
    val caution = MaterialTheme.colorScheme.tertiary
    val muted = MaterialTheme.colorScheme.onSurfaceVariant
    val orange = Color(0xFFF08A24)
    val unit = if (useMmol) "mmol/L" else "mg/dL"

    val toMg: (String) -> Double? = { s ->
        s.trim().replace(',', '.').toDoubleOrNull()
            ?.let { v -> if (useMmol) v * 18.0 else v }
            ?.takeIf { it > 0.0 }
    }
    val weightRaw = weightText.trim().replace(',', '.').toDoubleOrNull()
    val wKg: Double? = if (weightRaw != null && weightRaw >= 30.0 && weightRaw <= 250.0) weightRaw else null
    val gMg = toMg(glucoseText)
    val curMg = toMg(currentText)
    val higherRisk = age70 || kidney || otherRisk
    val factor: Double = when {
        higherRisk -> 0.3
        gMg != null && gMg < 201.0 -> 0.4
        else -> 0.5
    }
    val gState = when {
        gMg == null -> 0
        gMg < 140.0 -> 1
        gMg < 180.0 -> 2
        gMg <= 400.0 -> 3
        else -> 4
    }
    val tddD: Double? =
        if (situation == 0 && wKg != null && (gState == 2 || gState == 3)) wKg * factor else null

    val (hypoTitle, hypoBody, hypoColor) = when {
        curMg == null -> Triple("", "Enter a glucose to classify it.", muted)
        curMg < 54.0 -> Triple("Level 2 hypoglycaemia", "Below 54 mg/dL (3.0 mmol/L). Treat now.", danger)
        curMg < 70.0 -> Triple("Level 1 hypoglycaemia", "54 to 69 mg/dL (3.0 to 3.8 mmol/L). Treat now.", orange)
        curMg < 100.0 -> Triple(
            "Not hypoglycaemic",
            "A fasting value below 100 mg/dL can predict hypoglycaemia in the next 24 hours. Consider reviewing the insulin plan.",
            primary
        )
        curMg <= 180.0 -> Triple("Within the 100 to 180 mg/dL inpatient target", "", primary)
        else -> Triple("Above the inpatient target", "Above 180 mg/dL (10.0 mmol/L).", caution)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(primary.copy(alpha = 0.06f).compositeOver(bg), bg)))
    ) {
        Scaffold(
            modifier = Modifier.statusBarsPadding(),
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Insulin Dosing", fontSize = 20.sp, lineHeight = 26.sp, fontWeight = FontWeight.SemiBold) },
                    navigationIcon = { PremiumBackButton(onClick = onBackClick, edgeInset = 12.dp) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier.padding(padding).consumeWindowInsets(padding).imePadding().fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { InsHeroBanner() }
                item { InsAlertCard(danger) }

                item {
                    InsCard("Who is this for?", primary, "1") {
                        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Ward patient, type 2", "ICU, DKA or HHS", "On insulin at home", "Type 1 or unsure")
                                .forEachIndexed { i, label ->
                                    FilterChip(selected = situation == i, onClick = { situation = i }, label = { Text(label) })
                                }
                        }
                        Spacer(Modifier.height(10.dp))
                        InsLabel("GLUCOSE UNITS", muted)
                        Spacer(Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(selected = !useMmol, onClick = { useMmol = false }, label = { Text("mg/dL") })
                            FilterChip(selected = useMmol, onClick = { useMmol = true }, label = { Text("mmol/L") })
                        }
                        Spacer(Modifier.height(8.dp))
                        InsSmall(
                            "Not covered: children, pregnancy, insulin pumps, steroid-induced hyperglycaemia, tube or parenteral feeding, and perioperative plans.",
                            muted
                        )
                    }
                }

                if (situation == 1) {
                    item {
                        InsCard("Critically ill, DKA or HHS", danger, "!") {
                            InsBullet("Continuous IV insulin infusion is recommended for critically ill patients (ADA 2026, 16.8a). Follow your hospital's infusion protocol.")
                            InsBullet("Glucose goal for most ICU patients: 140 to 180 mg/dL (7.8 to 10.0 mmol/L).")
                            InsBullet("For DKA or HHS, use the DKA protocol under Emergency Protocols.")
                            InsBullet("No subcutaneous estimate is shown for these patients.")
                        }
                    }
                } else if (situation == 2) {
                    item {
                        InsCard("Already on insulin at home", danger, "!") {
                            InsBullet("Do not use a weight-based estimate. Reconcile the home regimen with the prescriber or pharmacist.")
                            InsBullet("Adjust for current intake, kidney function, steroids and illness.")
                            InsBullet("This tool estimates doses only for patients who were not on insulin before admission.")
                        }
                    }
                } else if (situation == 3) {
                    item {
                        InsCard("Type 1 diabetes or type unknown", danger, "!") {
                            InsBullet("The estimate here comes from trials in type 2 diabetes.")
                            InsBullet("Type 1 diabetes, or an unclear type, needs specialist advice. No estimate is shown.")
                        }
                    }
                } else {
                    item {
                        InsCard("Patient factors", primary, "2") {
                            InsLabel("ORAL INTAKE", muted)
                            Spacer(Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FilterChip(selected = eating, onClick = { eating = true }, label = { Text("Eating meals") })
                                FilterChip(selected = !eating, onClick = { eating = false }, label = { Text("Poor or no intake") })
                            }
                            Spacer(Modifier.height(10.dp))
                            InsLabel("HIGHER HYPOGLYCAEMIA RISK", muted)
                            Spacer(Modifier.height(2.dp))
                            InsSmall("Any one lowers the starting dose.", muted)
                            Spacer(Modifier.height(4.dp))
                            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FilterChip(selected = age70, onClick = { age70 = !age70 }, label = { Text("Age 70 or older") })
                                FilterChip(selected = kidney, onClick = { kidney = !kidney }, label = { Text("Kidney impairment") })
                                FilterChip(selected = otherRisk, onClick = { otherRisk = !otherRisk }, label = { Text("Other high risk") })
                            }
                            Spacer(Modifier.height(8.dp))
                            InsSmall(
                                "Kidney impairment in the trials: eGFR below 60, or creatinine 2.0 mg/dL or higher. " +
                                    "Other high risk, such as frailty or erratic intake, is clinician judgement.",
                                muted
                            )
                        }
                    }

                    item {
                        InsCard("Starting estimate", primary, "3") {
                            InsField(weightText, { weightText = it }, "Weight (kg)")
                            Spacer(Modifier.height(8.dp))
                            InsField(glucoseText, { glucoseText = it }, "Admission or persistent glucose ($unit)")
                            Spacer(Modifier.height(12.dp))

                            if (tddD != null) {
                                val tddR = tddD.roundToInt()
                                val basalU = (tddR / 2.0).roundToInt()
                                val perMeal = maxOf(1, ((tddR - basalU) / 3.0).roundToInt())
                                val tier = when {
                                    tddR < 40 -> "Low-dose scale (under 40 units/day)"
                                    tddR <= 80 -> "Medium-dose scale (40 to 80 units/day)"
                                    else -> "High-dose scale (over 80 units/day)"
                                }
                                val basis = when {
                                    higherRisk -> "higher hypoglycaemia risk"
                                    factor < 0.45 -> "glucose 140 to 200 mg/dL"
                                    else -> "glucose 201 to 400 mg/dL"
                                }
                                if (gState == 2) {
                                    Text(
                                        "Below ADA's threshold to start insulin (persistent glucose of 180 mg/dL or higher, confirmed twice within 24 hours). Shown for reference only.",
                                        fontSize = 12.sp,
                                        lineHeight = 17.sp,
                                        textAlign = TextAlign.Justify,
                                        fontWeight = FontWeight.SemiBold,
                                        color = caution
                                    )
                                    Spacer(Modifier.height(10.dp))
                                }
                                Column(
                                    Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(18.dp))
                                        .background(Brush.linearGradient(listOf(primary.copy(alpha = 0.16f), primary.copy(alpha = 0.05f))))
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    InsLabel("TOTAL DAILY DOSE", primary)
                                    AutoSizeText(
                                        text = "$tddR units/day",
                                        style = TextStyle(color = primary, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        weightText.trim() + " kg × " + String.format(Locale.US, "%.1f", factor) + " units/kg (" + basis + ")",
                                        fontSize = 12.sp,
                                        color = muted
                                    )
                                }
                                Spacer(Modifier.height(10.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    InsStat("Basal", "$basalU units", "once daily, long-acting", primary, Modifier.weight(1f))
                                    if (eating) {
                                        InsStat("Mealtime", "$perMeal units", "before each of 3 meals, rapid-acting", primary, Modifier.weight(1f))
                                    } else {
                                        InsStat("Mealtime", "Held", "poor or no intake: basal plus correction only", danger, Modifier.weight(1f))
                                    }
                                }
                                Spacer(Modifier.height(12.dp))
                                Text("Correction tier: $tier", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                                Spacer(Modifier.height(2.dp))
                                InsSmall(
                                    "Correction doses usually start at 140 or 150 mg/dL. Use your hospital's approved correction scale for this tier; scales differ between hospitals, so none is shown here.",
                                    muted
                                )
                                if (tddR >= 100) {
                                    Spacer(Modifier.height(8.dp))
                                    InsSmall(
                                        "Very large starting dose. Re-check the weight, units and glucose, and ask a senior clinician or the diabetes team.",
                                        danger,
                                        bold = true
                                    )
                                }
                                Spacer(Modifier.height(8.dp))
                                InsSmall(
                                    "Whole units, rounded: three mealtime doses can differ from half the total by 1 to 2 units. Round to the device in use. " +
                                        "RABBIT 2 used glargine for basal and glulisine for mealtime doses. Hold the mealtime dose if the patient does not eat.",
                                    muted
                                )
                            } else {
                                when {
                                    weightText.isNotBlank() && wKg == null ->
                                        Text("Enter a weight between 30 and 250 kg.", fontSize = 13.sp, color = danger)
                                    glucoseText.isNotBlank() && gMg == null ->
                                        Text("Enter a valid glucose.", fontSize = 13.sp, color = danger)
                                    gState == 1 ->
                                        Text(
                                            "Below the hospital hyperglycaemia threshold of " + insGl(140.0) + ". No starting dose is shown.",
                                            fontSize = 13.sp,
                                            color = muted
                                        )
                                    gState == 4 ->
                                        Text(
                                            "Above " + insGl(400.0) + " is outside the range of the trials this estimate is based on. " +
                                                "Check for DKA or HHS and ask a senior clinician. No estimate is shown.",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = danger
                                        )
                                    else ->
                                        Text("Enter the weight and glucose to calculate.", fontSize = 13.sp, color = muted)
                                }
                            }
                        }
                    }
                }

                item {
                    InsCard("After starting insulin", primary, "4") {
                        InsBullet("Glucose goal for patients outside the ICU: 100 to 180 mg/dL (5.6 to 10.0 mmol/L), if reached without significant hypoglycaemia (ADA 2026).")
                        InsBullet("Review the regimen any time glucose falls below 70 mg/dL (3.9 mmol/L). It often predicts a later severe low (ADA 2026).")
                        InsBullet("In RABBIT 2 the basal dose was reduced by 20% after a glucose below 70 mg/dL.")
                        InsBullet("In the surgical trial, basal-bolus insulin controlled glucose better than sliding scale alone but caused more hypoglycaemia (23.1% versus 4.7%).")
                        InsBullet("Trial protocols checked glucose before meals and at bedtime, or about every 6 hours when not eating.")
                        InsBullet("Reassess daily: needs change with intake, illness, kidney function and steroids.")
                    }
                }

                item {
                    InsCard("Hypoglycaemia check", orange, "5") {
                        InsField(currentText, { currentText = it }, "Current glucose ($unit)")
                        Spacer(Modifier.height(10.dp))
                        GlucoseStrip(curMg)
                        Spacer(Modifier.height(8.dp))
                        if (curMg != null) {
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(hypoColor.copy(alpha = 0.10f))
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                if (hypoTitle.isNotEmpty()) {
                                    Text(hypoTitle, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = hypoColor)
                                }
                                if (hypoBody.isNotEmpty()) {
                                    Text(hypoBody, fontSize = 13.sp, lineHeight = 19.sp, textAlign = TextAlign.Justify, color = hypoColor)
                                }
                            }
                        } else {
                            Text(hypoBody, fontSize = 13.sp, color = muted)
                        }
                        Spacer(Modifier.height(8.dp))
                        InsSmall(
                            "Green is the 100 to 180 mg/dL inpatient target. Orange is level 1 (54 to 69 mg/dL) and red is level 2 (below 54 mg/dL). " +
                                "Level 3 is a severe event with altered mental or physical status needing assistance, at any glucose.",
                            muted
                        )
                        Spacer(Modifier.height(12.dp))
                        InsLabel("TREATMENT", orange)
                        Spacer(Modifier.height(4.dp))
                        InsBullet("Alert and able to swallow, not NPO: 15 g of fast-acting carbohydrate, glucose preferred. Avoid high-fat or high-protein foods for initial treatment.")
                        InsBullet("Recheck glucose every 15 minutes and repeat treatment until it is stable above 70 mg/dL (3.9 mmol/L).")
                        InsBullet("Unable to swallow, NPO or unresponsive: IV glucose or glucagon. The IV dextrose dose and concentration follow your hospital protocol.")
                        InsBullet("Glucagon, adults and children 25 kg or more (US label): 1 mg subcutaneously, intramuscularly or intravenously. If there is no response after 15 minutes, a second 1 mg dose can be given from a new kit while help arrives. Give IV glucose if the patient does not respond.")
                        InsBullet("Other glucagon forms exist (nasal 3 mg, ready-to-inject 0.5 or 1 mg). Follow the product label.")
                        InsBullet("After any glucose below 70 mg/dL, review the insulin plan.")
                    }
                }

                item {
                    InsSmall(
                        "Decision support only. Estimates follow the ADA Standards of Care 2026 (hospital and hypoglycaemia sections), the RABBIT 2 trials and US glucagon labels. " +
                            "Always follow your local protocol. Wording is our own. Not affiliated with or endorsed by the ADA.",
                        muted
                    )
                }
            }
        }
    }
}
