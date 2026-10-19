package com.medtrixa.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medtrixa.app.data.repository.LabReference
import com.medtrixa.app.data.repository.LabReferenceRepository
import com.medtrixa.app.ui.components.PremiumBackButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

private const val NT_PROBNP = "NT-proBNP (heart failure)"
private const val UP = "\u25B2"
private const val DOWN = "\u25BC"

private const val NARROW_NOTE =
    "This test is narrowly regulated: even a small deviation can matter, so no mild or moderate grade is given."
private const val RULE_HIGH =
    "Grading: up to 25% above the limit is mild, 25% to 100% above is moderate, more than double the limit is very high."
private const val RULE_LOW =
    "Grading: up to 25% below the limit is mild, 25% to 50% below is moderate, less than half the limit is very low."

private val narrowTests = setOf(
    "Sodium", "Potassium", "Chloride", "Bicarbonate", "Anion gap",
    "Calcium, total", "Calcium, ionized", "Magnesium", "Phosphorus",
    "Osmolality, serum", "Glucose, plasma (fasting)"
)

private fun fmtNum(x: Double): String {
    return if (x == Math.floor(x) && Math.abs(x) < 1.0e9) String.format(Locale.US, "%.0f", x)
    else String.format(Locale.US, "%.2f", x).trimEnd('0').trimEnd('.')
}

private fun fmtPct(x: Double): String =
    if (x < 10.0) String.format(Locale.US, "%.1f", x) else String.format(Locale.US, "%.0f", x)

private fun cleanNum(s: String): String = s.filter { it.isDigit() || it == '.' || it == ',' }

private fun String.toNumL(): Double? = trim().replace(',', '.').toDoubleOrNull()

/** level: 0 within, 1 mild, 2 moderate, 3 very, 9 information only */
private data class LabVerdict(
    val label: String,
    val detail: String,
    val level: Int,
    val arrow: String = "",
    val rule: String = ""
)

private fun rangeText(low: Double?, high: Double?, unit: String): String = when {
    low != null && high != null -> fmtNum(low) + " to " + fmtNum(high) + " " + unit
    high != null -> "Up to " + fmtNum(high) + " " + unit
    low != null -> "At least " + fmtNum(low) + " " + unit
    else -> ""
}

private fun judge(v: Double, low: Double?, high: Double?, unit: String, narrow: Boolean): LabVerdict {
    if (high != null && high > 0.0 && v > high) {
        val pct = (v - high) / high * 100.0
        val detail = fmtNum(v) + " " + unit + " is " + fmtPct(pct) + "% above the upper limit (" + fmtNum(high) + ")."
        if (narrow) return LabVerdict("HIGH", detail + " " + NARROW_NOTE, 2, UP)
        return when {
            pct < 25.0 -> LabVerdict("MILDLY HIGH", detail, 1, UP, RULE_HIGH)
            pct < 100.0 -> LabVerdict("MODERATELY HIGH", detail, 2, UP, RULE_HIGH)
            else -> LabVerdict("VERY HIGH", detail + " That is more than double the limit.", 3, UP, RULE_HIGH)
        }
    }
    if (low != null && low > 0.0 && v < low) {
        val pct = (low - v) / low * 100.0
        val detail = fmtNum(v) + " " + unit + " is " + fmtPct(pct) + "% below the lower limit (" + fmtNum(low) + ")."
        if (narrow) return LabVerdict("LOW", detail + " " + NARROW_NOTE, 2, DOWN)
        return when {
            pct < 25.0 -> LabVerdict("MILDLY LOW", detail, 1, DOWN, RULE_LOW)
            pct < 50.0 -> LabVerdict("MODERATELY LOW", detail, 2, DOWN, RULE_LOW)
            else -> LabVerdict("VERY LOW", detail + " That is less than half the limit.", 3, DOWN, RULE_LOW)
        }
    }
    return LabVerdict("WITHIN RANGE", "", 0)
}

private fun judgeBands(v: Double, bands: String, unit: String): LabVerdict {
    var prev = 0.0
    bands.split(";").forEach { part ->
        val bits = part.split("|")
        if (bits.size == 2) {
            val up = if (bits[0] == "INF") Double.POSITIVE_INFINITY else bits[0].toDoubleOrNull()
            if (up != null) {
                if (v < up) {
                    val rng = when {
                        prev == 0.0 -> "below " + fmtNum(up)
                        up.isInfinite() -> fmtNum(prev) + " or more"
                        else -> fmtNum(prev) + " to " + fmtNum(up - 1.0)
                    }
                    val level = when (bits[1]) {
                        "Optimal", "Desirable", "Normal", "Near-optimal" -> 0
                        "Borderline high" -> 1
                        "High" -> 2
                        "Very high" -> 3
                        else -> 9
                    }
                    val arrow = if (level > 0) UP else ""
                    return LabVerdict(
                        bits[1].uppercase(),
                        bits[1] + ": " + rng + " " + unit + " (ABIM category).",
                        level,
                        arrow
                    )
                }
                prev = up
            }
        }
    }
    return LabVerdict("", "", 9)
}

private fun judgeNtProBnp(v: Double, age: Double?, lowEgfr: Boolean): LabVerdict {
    if (lowEgfr) {
        return if (v >= 1200.0) LabVerdict("HIGH PROBABILITY", "High probability of heart failure (1200 pg/mL or more, eGFR below 60).", 3, UP)
        else LabVerdict("", "Below the 1200 pg/mL high-probability threshold for eGFR below 60. ABIM gives no separate rule-out value for this group.", 9)
    }
    if (age == null) return LabVerdict("", "Enter the age to interpret.", 9)
    if (age < 18.0) return LabVerdict("", "The ABIM thresholds apply to adults (18 years and older).", 9)
    val high = when {
        age < 50.0 -> 450.0
        age <= 75.0 -> 900.0
        else -> 1800.0
    }
    return when {
        v <= 300.0 -> LabVerdict("HF UNLIKELY", "Heart failure unlikely (300 pg/mL or less).", 0)
        v >= high -> LabVerdict("HIGH PROBABILITY", "High probability of heart failure (${fmtNum(high)} pg/mL or more for this age).", 3, UP)
        else -> LabVerdict("INDETERMINATE", "Between the thresholds. Use clinical assessment.", 1)
    }
}

private fun levelFill(level: Int): Color = when (level) {
    0 -> Color(0xFF2E7D32)
    1 -> Color(0xFFFFC107)
    2 -> Color(0xFFEF6C00)
    3 -> Color(0xFFC62828)
    else -> Color(0xFF607D8B)
}

private fun levelOn(level: Int): Color = if (level == 1) Color(0xFF1B1B1B) else Color.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabInterpreterScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    var loaded by remember { mutableStateOf(LabReferenceRepository.isLoaded()) }
    LaunchedEffect(Unit) {
        if (!loaded) {
            withContext(Dispatchers.IO) { LabReferenceRepository.load(context) }
            loaded = true
        }
    }

    var query by rememberSaveable { mutableStateOf("") }
    var sex by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("All") }
    var valuesStr by rememberSaveable { mutableStateOf("") }
    var ntAge by rememberSaveable { mutableStateOf("") }
    var ntLowEgfr by rememberSaveable { mutableStateOf(false) }

    val primary = MaterialTheme.colorScheme.primary
    val background = MaterialTheme.colorScheme.background

    val all = remember(loaded) { LabReferenceRepository.getAll() }
    val categories = remember(loaded) { listOf("All") + all.map { it.category }.distinct() }
    val shown = remember(loaded, query, category) {
        all.withIndex().filter { (_, t) ->
            (category == "All" || t.category == category) &&
                (query.isBlank() || t.name.contains(query.trim(), ignoreCase = true))
        }
    }

    val values: Map<Int, String> = remember(valuesStr) {
        val m = mutableMapOf<Int, String>()
        valuesStr.split(";").forEach { part ->
            val bits = part.split("=")
            if (bits.size == 2) {
                val k = bits[0].toIntOrNull()
                if (k != null) m[k] = bits[1]
            }
        }
        m
    }

    fun setValue(id: Int, text: String) {
        val m = values.toMutableMap()
        if (text.isEmpty()) m.remove(id) else m[id] = text
        valuesStr = m.entries.joinToString(";") { it.key.toString() + "=" + it.value }
    }

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
                        Text("Lab Interpreter", fontSize = 20.sp, lineHeight = 26.sp, fontWeight = FontWeight.SemiBold)
                    },
                    navigationIcon = { PremiumBackButton(onClick = onBackClick, edgeInset = 12.dp) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).fillMaxSize()) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(18.dp),
                    placeholder = { Text("Search a test") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { query = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear search")
                            }
                        }
                    },
                    singleLine = true
                )

                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Sex", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    FilterChip(
                        selected = sex == "F",
                        onClick = { sex = if (sex == "F") "" else "F" },
                        label = { Text("Female") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = primary.copy(alpha = 0.15f),
                            selectedLabelColor = primary
                        )
                    )
                    FilterChip(
                        selected = sex == "M",
                        onClick = { sex = if (sex == "M") "" else "M" },
                        label = { Text("Male") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = primary.copy(alpha = 0.15f),
                            selectedLabelColor = primary
                        )
                    )
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { c ->
                        FilterChip(
                            selected = category == c,
                            onClick = { category = c },
                            label = { Text(c, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = primary.copy(alpha = 0.15f),
                                selectedLabelColor = primary
                            )
                        )
                    }
                }

                if (shown.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(if (loaded) "No matching tests." else "Loading\u2026")
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 28.dp)
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
                                Text("Check results against reference ranges", fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "Adult ranges from the ABIM, January 2026. Type a value under any test.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        item { LegendBlock() }

                        items(shown, key = { it.index }) { (idx, t) ->
                            LabCard(
                                t = t,
                                sex = sex,
                                value = values[idx] ?: "",
                                onValue = { setValue(idx, cleanNum(it)) },
                                ntAge = ntAge,
                                onNtAge = { ntAge = cleanNum(it) },
                                ntLowEgfr = ntLowEgfr,
                                onNtLowEgfr = { ntLowEgfr = it }
                            )
                        }

                        item {
                            Text(
                                "Source: ABIM Laboratory Test Reference Ranges, revised January 2026. Reference ranges vary by " +
                                    "laboratory, method, age and sex, so always compare with the range printed on the report. " +
                                    "A result outside a reference range is not a diagnosis, and a result inside one does not " +
                                    "exclude disease. These are not patient-specific targets or critical-value limits. " +
                                    "Decision support only.",
                                fontSize = 11.sp,
                                lineHeight = 16.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendBlock() {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf(
                0 to "Within range",
                1 to "Mild",
                2 to "Moderate",
                3 to "Very high / very low"
            ).forEach { (lvl, label) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(12.dp).background(levelFill(lvl), CircleShape))
                    Spacer(Modifier.width(6.dp))
                    Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        Text(
            "Arrows show direction. Severity is how far a result lies outside the reference range: mild is up to 25%, " +
                "moderate is 25% to 100% above (25% to 50% below), very high is more than double (very low is less than half). " +
                "This measures distance from the range, not clinical urgency. Narrowly regulated tests such as sodium, " +
                "potassium and calcium are not graded, because small changes can matter.",
            fontSize = 11.sp,
            lineHeight = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun LabCard(
    t: LabReference,
    sex: String,
    value: String,
    onValue: (String) -> Unit,
    ntAge: String,
    onNtAge: (String) -> Unit,
    ntLowEgfr: Boolean,
    onNtLowEgfr: (Boolean) -> Unit
) {
    val isNt = t.name == NT_PROBNP
    val hasBands = t.bands.isNotEmpty()
    val narrow = narrowTests.contains(t.name)

    val needSex = t.sexSpecific && sex.isEmpty()
    val low = if (sex == "M") t.lowM else t.lowF
    val high = if (sex == "M") t.highM else t.highF

    val rangeLine: String = when {
        isNt -> "Age and kidney-function dependent thresholds"
        hasBands -> {
            val first = t.bands.split(";").firstOrNull()?.split("|")
            if (first != null && first.size == 2)
                first[1] + ": below " + fmtNum(first[0].toDoubleOrNull() ?: 0.0) + " " + t.unit
            else ""
        }
        t.sexSpecific && sex.isEmpty() ->
            "Women: " + rangeText(t.lowF, t.highF, t.unit) + ". Men: " + rangeText(t.lowM, t.highM, t.unit)
        else -> rangeText(low, high, t.unit)
    }

    val v = value.toNumL()
    val verdict: LabVerdict? = when {
        v == null -> null
        isNt -> judgeNtProBnp(v, ntAge.toNumL(), ntLowEgfr)
        hasBands -> judgeBands(v, t.bands, t.unit)
        needSex -> LabVerdict("", "Select sex above to interpret this test.", 9)
        else -> judge(v, low, high, t.unit, narrow)
    }

    val graded = verdict != null && verdict.label.isNotEmpty() && verdict.level != 9

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = if (graded && verdict != null)
            BorderStroke(if (verdict.level == 0) 1.5.dp else 2.5.dp, levelFill(verdict.level))
        else null
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(t.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(
                rangeLine,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            OutlinedTextField(
                value = value,
                onValueChange = onValue,
                label = { Text("Result (" + t.unit + ")", fontSize = 12.sp, maxLines = 1) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            if (isNt) {
                OutlinedTextField(
                    value = ntAge,
                    onValueChange = onNtAge,
                    label = { Text("Age (years)", fontSize = 12.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(checked = ntLowEgfr, onCheckedChange = onNtLowEgfr)
                    Spacer(Modifier.width(10.dp))
                    Text("eGFR below 60 mL/min/1.73 m\u00B2", fontSize = 13.sp)
                }
            }
            if (verdict != null) {
                if (graded) {
                    Box(
                        modifier = Modifier
                            .background(levelFill(verdict.level), RoundedCornerShape(50))
                            .padding(horizontal = 16.dp, vertical = 7.dp)
                    ) {
                        Text(
                            (verdict.arrow + " " + verdict.label).trim(),
                            color = levelOn(verdict.level),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                    if (verdict.detail.isNotEmpty()) {
                        Text(verdict.detail, fontSize = 14.sp, lineHeight = 20.sp)
                    }
                    if (verdict.rule.isNotEmpty()) {
                        Text(
                            verdict.rule,
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else if (verdict.detail.isNotEmpty()) {
                    Text(
                        verdict.detail,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (t.note.isNotEmpty() && t.note != "special") {
                Text(
                    t.note,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
