package com.medtrixa.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medtrixa.app.ui.components.PremiumBackButton
import java.util.Locale
import kotlin.math.abs
import kotlin.math.pow

private const val KPA_TO_MMHG = 7.50062

private data class AbgResult(
    val summary: String,
    val findings: List<Pair<String, String>>,
    val warnings: List<String>
)

private fun f(x: Double, d: Int = 1): String = String.format(Locale.US, "%." + d + "f", x)

private fun String.toNum(): Double? = trim().replace(',', '.').toDoubleOrNull()

private fun interpretAbg(
    ph: Double,
    paco2: Double,
    hco3In: Double?,
    na: Double?,
    cl: Double?,
    albumin: Double?,
    pao2: Double?,
    fio2Pct: Double?,
    age: Double?,
    kpa: Boolean
): AbgResult {
    val findings = mutableListOf<Pair<String, String>>()
    val warnings = mutableListOf<String>()

    fun g(x: Double, d: Int = 1): String =
        if (kpa) f(x / KPA_TO_MMHG, 1) + " kPa" else f(x, d) + " mmHg"

    val hPlus = 10.0.pow(9.0 - ph)
    val hco3Calc = 24.0 * paco2 / hPlus
    val hco3 = hco3In ?: hco3Calc
    if (hco3In == null) {
        findings.add("HCO\u2083\u207B" to "${f(hco3Calc)} mEq/L (calculated from pH and PaCO\u2082)")
    } else if (abs(hco3In - hco3Calc) > 2.0) {
        warnings.add(
            "pH, PaCO\u2082 and HCO\u2083\u207B do not agree with each other (calculated HCO\u2083\u207B \u2248 ${f(hco3Calc)} mEq/L). " +
                "Check the entered values and the sample (for example a venous sample, air bubble or delayed analysis)."
        )
    }

    val acidemia = ph < 7.35
    val alkalemia = ph > 7.45
    val respAcid = paco2 > 45.0
    val respAlk = paco2 < 35.0
    val metAcid = hco3 < 22.0
    val metAlk = hco3 > 26.0

    var summary: String
    var extra = ""

    if (acidemia) {
        summary = when {
            respAcid && metAcid -> "Mixed respiratory and metabolic acidosis"
            respAcid -> "Respiratory acidosis"
            metAcid -> "Metabolic acidosis"
            else -> "Acidemia not explained by these values"
        }
    } else if (alkalemia) {
        summary = when {
            respAlk && metAlk -> "Mixed respiratory and metabolic alkalosis"
            respAlk -> "Respiratory alkalosis"
            metAlk -> "Metabolic alkalosis"
            else -> "Alkalemia not explained by these values"
        }
    } else {
        summary = when {
            respAcid && metAlk -> "Normal pH with high PaCO\u2082 and high HCO\u2083\u207B"
            respAlk && metAcid -> "Normal pH with low PaCO\u2082 and low HCO\u2083\u207B"
            !respAcid && !respAlk && !metAcid && !metAlk -> "No acid\u2013base disturbance on these values"
            else -> "Normal pH with a pattern that is not a simple disorder"
        }
    }

    if (summary.startsWith("Acidemia not") || summary.startsWith("Alkalemia not")) {
        warnings.add("The pH is abnormal but PaCO\u2082 and HCO\u2083\u207B are not. Re-check the entered values.")
    }
    if (summary.startsWith("Normal pH with high") || summary.startsWith("Normal pH with low")) {
        warnings.add(
            "A normal pH with both PaCO\u2082 and HCO\u2083\u207B abnormal means full compensation or a mixed disorder. " +
                "The values alone cannot separate these. Use the history and the anion gap."
        )
    }
    if (summary.startsWith("Normal pH with a pattern")) {
        warnings.add("This pattern is not a simple disorder. Check the entered values and consider a mixed disorder.")
    }

    when (summary) {
        "Metabolic acidosis" -> {
            val e = 1.5 * hco3 + 8.0
            findings.add("Expected PaCO\u2082 (Winter's formula)" to "${g(e)} \u00B1 ${if (kpa) "0.3 kPa" else "2 mmHg"}  (1.5 \u00D7 HCO\u2083\u207B + 8)")
            findings.add("Measured PaCO\u2082" to g(paco2))
            extra = when {
                paco2 > e + 2.0 -> " with concomitant respiratory acidosis"
                paco2 < e - 2.0 -> " with concomitant respiratory alkalosis"
                else -> " with appropriate respiratory compensation"
            }
        }
        "Metabolic alkalosis" -> {
            val e = 0.7 * (hco3 - 24.0) + 40.0
            findings.add("Expected PaCO\u2082" to "${g(e)} \u00B1 ${if (kpa) "0.7 kPa" else "5 mmHg"}  (0.7 \u00D7 (HCO\u2083\u207B \u2212 24) + 40)")
            findings.add("Measured PaCO\u2082" to g(paco2))
            findings.add("Note" to "Respiratory compensation for metabolic alkalosis is variable. Interpret with caution.")
            extra = when {
                paco2 > e + 5.0 -> " with concomitant respiratory acidosis"
                paco2 < e - 5.0 -> " with concomitant respiratory alkalosis"
                else -> " with appropriate respiratory compensation"
            }
        }
        "Respiratory acidosis" -> {
            val d = (paco2 - 40.0) / 10.0
            val acute = 24.0 + 1.0 * d
            val chLow = 24.0 + 3.0 * d
            val chHigh = 24.0 + 4.0 * d
            findings.add("Expected HCO\u2083\u207B" to "Acute: ${f(acute)} mEq/L. Chronic: ${f(chLow)} to ${f(chHigh)} mEq/L")
            findings.add("Measured HCO\u2083\u207B" to "${f(hco3)} mEq/L")
            extra = when {
                hco3 < acute - 2.0 -> " with concomitant metabolic acidosis"
                hco3 > chHigh + 2.0 -> " with concomitant metabolic alkalosis"
                hco3 <= acute + 2.0 -> ", acute (compensation not yet developed)"
                hco3 >= chLow - 2.0 -> ", chronic (renal compensation present)"
                else -> ", partly compensated (subacute)"
            }
        }
        "Respiratory alkalosis" -> {
            val d = (40.0 - paco2) / 10.0
            val acute = 24.0 - 2.0 * d
            val chLow = 24.0 - 5.0 * d
            val chHigh = 24.0 - 4.0 * d
            findings.add("Expected HCO\u2083\u207B" to "Acute: ${f(acute)} mEq/L. Chronic: ${f(chLow)} to ${f(chHigh)} mEq/L")
            findings.add("Measured HCO\u2083\u207B" to "${f(hco3)} mEq/L")
            extra = when {
                hco3 > acute + 2.0 -> " with concomitant metabolic alkalosis"
                hco3 < chLow - 2.0 -> " with concomitant metabolic acidosis"
                hco3 >= acute - 2.0 -> ", acute (compensation not yet developed)"
                hco3 <= chHigh + 2.0 -> ", chronic (renal compensation present)"
                else -> ", partly compensated (subacute)"
            }
        }
        else -> {
            if (summary.startsWith("Mixed")) {
                findings.add("Compensation" to "Not assessed: two primary disorders are present.")
            }
        }
    }
    summary += extra

    // Anion gap
    if (na != null && cl != null) {
        var ag = na - (cl + hco3)
        findings.add("Anion gap" to "${f(ag)} mEq/L  (Na \u2212 (Cl + HCO\u2083\u207B))")
        if (albumin != null) {
            ag += 2.5 * (4.0 - albumin)
            findings.add("Albumin-corrected anion gap" to "${f(ag)} mEq/L  (AG + 2.5 \u00D7 (4.0 \u2212 albumin g/dL))")
        }
        if (ag > 12.0) {
            if (hco3 < 24.0) {
                val ratio = (ag - 12.0) / (24.0 - hco3)
                val meaning = when {
                    ratio < 0.4 -> "suggests a mainly normal-anion-gap (hyperchloremic) acidosis"
                    ratio < 0.8 -> "suggests a combined high- and normal-anion-gap acidosis"
                    ratio <= 2.0 -> "consistent with a pure high-anion-gap acidosis"
                    else -> "suggests a concurrent metabolic alkalosis (or pre-existing high HCO\u2083\u207B)"
                }
                findings.add("Delta ratio" to "${f(ratio, 2)}: $meaning")
            } else {
                findings.add("Delta ratio" to "HCO\u2083\u207B is not reduced despite a high anion gap: suggests a concurrent metabolic alkalosis.")
            }
            findings.add("Anion gap reading" to "Elevated (above 12 mEq/L). Reference ranges are lab-dependent, commonly 8 to 12.")
        } else if (metAcid) {
            findings.add("Anion gap reading" to "Not elevated: a normal-anion-gap (hyperchloremic) metabolic acidosis.")
        } else {
            findings.add("Anion gap reading" to "Not elevated. Reference ranges are lab-dependent, commonly 8 to 12.")
        }
    }

    // Oxygenation
    if (pao2 != null) {
        findings.add("PaO\u2082" to (g(pao2, 0) + if (pao2 < 60.0) "  (below 60 mmHg: hypoxemia)" else ""))
        if (fio2Pct != null) {
            val fio2 = fio2Pct / 100.0
            findings.add("PaO\u2082/FiO\u2082 ratio" to "${f(pao2 / fio2, 0)} mmHg")
            val alveolar = fio2 * (760.0 - 47.0) - paco2 / 0.8
            val aa = alveolar - pao2
            if (age != null && fio2 <= 0.21) {
                val expected = age / 4.0 + 4.0
                findings.add(
                    "A\u2013a gradient" to
                        "${g(aa)}  (expected for age \u2248 ${g(expected)}; ${if (aa > expected) "elevated" else "within the expected range"})"
                )
            } else {
                findings.add(
                    "A\u2013a gradient" to
                        "${g(aa)}  (the expected value rises with FiO\u2082, so it is not graded here)"
                )
            }
        }
    }

    return AbgResult(summary, findings, warnings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AbgInterpreterScreen(onBackClick: () -> Unit) {
    var kpa by rememberSaveable { mutableStateOf(false) }
    var ph by rememberSaveable { mutableStateOf("") }
    var pco2 by rememberSaveable { mutableStateOf("") }
    var hco3 by rememberSaveable { mutableStateOf("") }
    var na by rememberSaveable { mutableStateOf("") }
    var cl by rememberSaveable { mutableStateOf("") }
    var alb by rememberSaveable { mutableStateOf("") }
    var po2 by rememberSaveable { mutableStateOf("") }
    var fio2 by rememberSaveable { mutableStateOf("") }
    var age by rememberSaveable { mutableStateOf("") }

    val primary = MaterialTheme.colorScheme.primary
    val background = MaterialTheme.colorScheme.background

    val phV = ph.toNum()
    val cV = pco2.toNum()
    val hV = hco3.toNum()
    val naV = na.toNum()
    val clV = cl.toNum()
    val albV = alb.toNum()
    val oV = po2.toNum()
    val fV = fio2.toNum()
    val ageV = age.toNum()

    val cMm = cV?.let { if (kpa) it * KPA_TO_MMHG else it }
    val oMm = oV?.let { if (kpa) it * KPA_TO_MMHG else it }

    var error: String? = null
    if (phV != null && (phV < 6.5 || phV > 8.0)) error = "pH is outside the plausible range (6.5 to 8.0)."
    if (cMm != null && (cMm < 5.0 || cMm > 200.0)) error = "PaCO\u2082 is outside the plausible range."
    if (hV != null && (hV < 1.0 || hV > 80.0)) error = "HCO\u2083\u207B is outside the plausible range (1 to 80 mEq/L)."
    if (fV != null && (fV < 21.0 || fV > 100.0)) error = "FiO\u2082 must be between 21 and 100 percent."

    val errorMsg: String? = error

    val result: AbgResult? =
        if (phV != null && cMm != null && errorMsg == null) {
            interpretAbg(phV, cMm, hV, naV, clV, albV, oMm, fV, ageV, kpa)
        } else null

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
                        Text("ABG Interpreter", fontSize = 20.sp, lineHeight = 26.sp, fontWeight = FontWeight.SemiBold)
                    },
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
                        Text("Acid\u2013base analysis", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Enter pH and PaCO\u2082. HCO\u2083\u207B is calculated if left blank.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(selected = !kpa, onClick = { kpa = false }, label = { Text("mmHg") })
                        FilterChip(selected = kpa, onClick = { kpa = true }, label = { Text("kPa") })
                    }
                }

                item {
                    SectionCard("Required") {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            NumField("pH", ph, { ph = it }, Modifier.weight(1f))
                            NumField(if (kpa) "PaCO\u2082 (kPa)" else "PaCO\u2082 (mmHg)", pco2, { pco2 = it }, Modifier.weight(1f))
                        }
                        NumField("HCO\u2083\u207B (mEq/L), optional", hco3, { hco3 = it }, Modifier.fillMaxWidth())
                    }
                }

                item {
                    SectionCard("Anion gap (optional)") {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            NumField("Na\u207A (mEq/L)", na, { na = it }, Modifier.weight(1f))
                            NumField("Cl\u207B (mEq/L)", cl, { cl = it }, Modifier.weight(1f))
                        }
                        NumField("Albumin (g/dL), optional", alb, { alb = it }, Modifier.fillMaxWidth())
                    }
                }

                item {
                    SectionCard("Oxygenation (optional)") {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            NumField(if (kpa) "PaO\u2082 (kPa)" else "PaO\u2082 (mmHg)", po2, { po2 = it }, Modifier.weight(1f))
                            NumField("FiO\u2082 (%)", fio2, { fio2 = it }, Modifier.weight(1f))
                        }
                        NumField("Age (years), for A\u2013a gradient", age, { age = it }, Modifier.fillMaxWidth())
                    }
                }

                if (errorMsg != null) {
                    item { WarningCard(listOf(errorMsg)) }
                }

                if (result != null) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(24.dp))
                                .background(primary.copy(alpha = 0.12f))
                                .padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("Interpretation", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(result.summary, fontSize = 19.sp, lineHeight = 25.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    if (result.warnings.isNotEmpty()) {
                        item { WarningCard(result.warnings) }
                    }
                    if (result.findings.isNotEmpty()) {
                        item {
                            SectionCard("Details") {
                                result.findings.forEach { (label, text) ->
                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = primary)
                                        Text(text, fontSize = 13.sp, lineHeight = 18.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Text(
                        "Method: physiological approach to acid\u2013base disorders (Berend et al., N Engl J Med 2014). " +
                            "Winter's formula (Albert et al., Ann Intern Med 1967). Compensation ranges are approximations and a " +
                            "normal-looking result does not exclude a mixed disorder. Anion gap reference range, albumin correction " +
                            "and delta ratio cut-offs vary by laboratory and source. Decision support only: interpret with the clinical picture.",
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

@Composable
private fun NumField(label: String, value: String, onChange: (String) -> Unit, modifier: Modifier) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        modifier = modifier,
        label = { Text(label, fontSize = 12.sp, maxLines = 1) },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
    )
}

@Composable
private fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
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
private fun WarningCard(lines: List<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.error.copy(alpha = 0.10f))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        lines.forEach { Text(it, fontSize = 13.sp, lineHeight = 18.sp, color = MaterialTheme.colorScheme.onSurface) }
    }
}
