package com.medtrixa.app.ui.screens.drugtools

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medtrixa.app.data.db.SearchHit
import com.medtrixa.app.data.db.AppDatabase
import com.medtrixa.app.data.model.LactMedResult
import com.medtrixa.app.data.repository.LactMedRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private val PlRed = Color(0xFFC0392B)
private val PlAmber = Color(0xFFC27A00)
private val PlGreen = Color(0xFF2E7D32)
private val PlBlue = Color(0xFF2F6F9F)
private val PlGrey = Color(0xFF5F6B7A)
private val PlPink = Color(0xFFD6456B)
private val PlInk = Color(0xFF111827)

private data class Signal(val text: String, val level: Int)
private data class ExSys(val name: String, val from: Int, val to: Int)
private data class Factor(val text: String, val weight: Int)
private data class LmpInfo(val gaDays: Long, val edd: String, val conception: String)

private val exSystems = listOf(
    ExSys("CNS", 5, 12), ExSys("Heart", 5, 10), ExSys("Limbs", 6, 10), ExSys("Eyes", 6, 10),
    ExSys("Ears", 6, 11), ExSys("Palate", 8, 12), ExSys("Genitalia", 9, 14)
)

private val infantRiskFactors = listOf(
    Factor("Infant younger than 2 months, or preterm / low birth weight (immature clearance)", 1),
    Factor("Infant has renal or hepatic impairment, or a metabolic disorder (e.g. G6PD deficiency)", 1),
    Factor("Long half-life (over 24 h) or active metabolites", 1),
    Factor("CNS-active drug (sedative, opioid, antiepileptic, psychotropic)", 1),
    Factor("High maternal dose or prolonged course", 1),
    Factor("Small, lipophilic, weakly protein-bound molecule (higher milk transfer)", 1),
    Factor("More than one sedating or interacting drug", 1)
)
private val infantCritical = Factor("Label or reports describe infant adverse effects, or advise against breastfeeding", 3)
private val infantProtective = listOf(
    "Poor oral bioavailability in the infant (e.g. aminoglycosides, heparins)",
    "Short half-life (under 6 h)",
    "Highly protein-bound (over 90%)",
    "Long record of safe use in breastfed infants"
)

private fun plNum(x: Double): String = "%.2f".format(x).trimEnd('0').trimEnd('.')

private fun exHours(text: String?): Double? {
    if (text.isNullOrBlank()) return null
    val m = Regex(
        "(\\d+(?:\\.\\d+)?)\\s*(?:(?:-|–|to)\\s*(\\d+(?:\\.\\d+)?))?\\s*(hours?|hrs?|h|days?|d|minutes?|mins?|weeks?)\\b",
        RegexOption.IGNORE_CASE
    ).find(text) ?: return null
    val lo = m.groupValues[1].toDouble()
    val hi = m.groupValues[2].toDoubleOrNull()
    val v = if (hi != null) (lo + hi) / 2 else lo
    val u = m.groupValues[3].lowercase()
    return when {
        u.startsWith("min") -> v / 60
        u.startsWith("d") -> v * 24
        u.startsWith("w") -> v * 168
        else -> v
    }
}

private fun exDur(h: Double): String = when {
    h < 1 -> "${plNum(h * 60)} min"
    h < 48 -> "${plNum(h)} h"
    else -> "${plNum(h / 24)} days"
}

private fun exGa(days: Long): String = "${days / 7} w ${days % 7} d"

private fun lmpInfo(d: Int, m: Int, y: Int): LmpInfo? {
    if (d !in 1..31 || m !in 1..12 || y !in 2000..2100) return null
    return try {
        val cal = Calendar.getInstance()
        cal.isLenient = false
        cal.clear()
        cal.set(y, m - 1, d, 12, 0, 0)
        val lmp = cal.timeInMillis
        val days = (System.currentTimeMillis() - lmp) / 86400000L
        if (days < 0 || days > 300) return null
        val fmt = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val c2 = Calendar.getInstance()
        c2.timeInMillis = lmp
        c2.add(Calendar.DAY_OF_YEAR, 280)
        val edd = fmt.format(c2.time)
        c2.timeInMillis = lmp
        c2.add(Calendar.DAY_OF_YEAR, 14)
        LmpInfo(days, edd, fmt.format(c2.time))
    } catch (e: Exception) {
        null
    }
}

private fun exPhase(w: Int): String = when {
    w <= 2 -> "Pre-conception (before fertilisation)"
    w <= 4 -> "All-or-none period (first 2 weeks after conception)"
    w <= 10 -> "Organogenesis (peak susceptibility to structural malformations)"
    w <= 13 -> "Late first trimester (palate and genitalia still forming)"
    w <= 27 -> "Second trimester (growth and functional maturation)"
    else -> "Third trimester / peripartum (neonatal adaptation)"
}

private fun triOf(weeks: Int): String = when {
    weeks <= 13 -> "First trimester"
    weeks <= 27 -> "Second trimester"
    else -> "Third trimester"
}

private fun tidyLabel(s: String): String {
    var t = s.replace(Regex("\\s+"), " ").trim()
    listOf(
        "Risk Summary", "Clinical Considerations", "Human Data", "Animal Data", "Lactation",
        "Fetal/Neonatal Adverse Reactions", "Labor or Delivery", "Females and Males of Reproductive Potential"
    ).forEach { h ->
        t = t.replace(Regex("\\s+(?=" + Regex.escape(h) + ")"), "\n\n")
    }
    return t
}

private fun splitLactation(t: String): Pair<String?, String?> {
    val m = Regex("(?:8\\.2\\s*)?Lactation\\s+Risk Summary").find(t) ?: return Pair(t, null)
    return Pair(t.substring(0, m.range.first).trim(), t.substring(m.range.first).trim())
}

private fun detectPregSignals(t: String): List<Signal> {
    val out = mutableListOf<Signal>()
    fun has(p: String) = Regex(p, RegexOption.IGNORE_CASE).containsMatchIn(t)
    Regex("pregnancy category\\s+([ABCDX])\\b", RegexOption.IGNORE_CASE).find(t)?.let {
        val c = it.groupValues[1].uppercase()
        out += Signal("Legacy FDA Pregnancy Category $c", when (c) { "X" -> 2; "D", "C" -> 1; else -> 0 })
    }
    if (has("contraindicated (in|during) pregnan|contraindicated in women who (are|may)"))
        out += Signal("Contraindicated in pregnancy", 2)
    if (has("embryo-?fetal toxicity|fetal toxicity|(can|may) cause fetal harm"))
        out += Signal("Fetal harm / embryo-fetal toxicity wording", 2)
    if (has("teratogen|congenital (malformation|anomal)|birth defect|major malformation"))
        out += Signal("Teratogenicity / congenital malformation wording", 1)
    if (has("neonatal (withdrawal|respiratory depression|hypoglyc|adverse)|persistent pulmonary hypertension|premature closure of the (fetal )?ductus"))
        out += Signal("Late-pregnancy / neonatal adverse-effect wording", 1)
    if (has("oligohydramnios"))
        out += Signal("Oligohydramnios (fetal renal effect) wording", 1)
    if (has("no adequate and well-controlled|insufficient data|limited data|not sufficient to"))
        out += Signal("Human data limited or insufficient", 0)
    if (has("animal reproduction studies"))
        out += Signal("Animal reproduction data referenced", 0)
    return out
}

private fun detectLactSignals(t: String): List<Signal> {
    val out = mutableListOf<Signal>()
    fun has(p: String) = Regex(p, RegexOption.IGNORE_CASE).containsMatchIn(t)
    if (has("breastfeeding is not recommended|not recommended during (breastfeeding|lactation)|not to breastfeed|discontinue (breastfeeding|nursing)|should not breastfeed"))
        out += Signal("Breastfeeding not recommended / avoid wording", 2)
    if (has("serious adverse reactions in (the )?breastfed|adverse (effects?|reactions?) (in|on) (the )?breastfed|infant (sedation|drowsiness)|monitor (the )?(breastfed )?infant"))
        out += Signal("Infant adverse-effect / monitoring wording", 1)
    if (has("reduce[sd]? milk (production|supply)|suppress(es)? lactation|effects? on milk production"))
        out += Signal("May affect milk production", 1)
    if (has("present in human milk|excreted in human milk|detected in human milk|is excreted in"))
        out += Signal("Present in human milk", 0)
    if (has("no data (are )?available|no data on the presence|no information (is )?available"))
        out += Signal("Limited or no human milk data", 0)
    return out
}

private fun levelOf(hasText: Boolean, strong: Int): Pair<String, Color> = when {
    !hasText -> "No label data" to PlGrey
    strong == 2 -> "Higher concern" to PlRed
    strong == 1 -> "Caution" to PlAmber
    else -> "No high-risk wording" to PlBlue
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, textAlign = TextAlign.Start)
    Spacer(Modifier.height(10.dp))
}

@Composable
private fun EmptyState(msg: String) {
    Box(Modifier.fillMaxWidth().padding(top = 48.dp), contentAlignment = Alignment.Center) {
        Text(msg, fontSize = 15.sp, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun BannerCard(title: String, note: String, color: Color) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f))) {
        Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color, textAlign = TextAlign.Center)
            Spacer(Modifier.height(6.dp))
            Text(note, fontSize = 13.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun InfoCard(title: String, value: String, note: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, fontSize = 14.sp, textAlign = TextAlign.Center)
            Spacer(Modifier.height(6.dp))
            Text(value, fontSize = 26.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Spacer(Modifier.height(4.dp))
            Text(note, fontSize = 12.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun GlanceCard(modifier: Modifier, title: String, label: String, color: Color) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f))) {
        Column(Modifier.fillMaxWidth().padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, fontSize = 13.sp, textAlign = TextAlign.Center)
            Spacer(Modifier.height(6.dp))
            Text(label, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = color, textAlign = TextAlign.Center)
            Spacer(Modifier.height(4.dp))
            Text("from label wording", fontSize = 11.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun SignalList(signals: List<Signal>) {
    if (signals.isEmpty()) return
    SectionTitle("Signals found in label text")
    signals.sortedByDescending { it.level }.forEach { s ->
        val col = when (s.level) { 2 -> PlRed; 1 -> PlAmber; else -> PlGrey }
        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(10.dp).clip(CircleShape).background(col))
            Spacer(Modifier.width(10.dp))
            Text(s.text, fontSize = 14.sp, textAlign = TextAlign.Start)
        }
    }
    Spacer(Modifier.height(16.dp))
}

@Composable
private fun LabelTextCard(title: String, text: String, accent: Color) {
    var expanded by remember(text) { mutableStateOf(false) }
    val long = text.length > 700
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = accent)
            Spacer(Modifier.height(8.dp))
            Text(
                if (expanded || !long) text else text.take(700).trimEnd() + "…",
                fontSize = 14.sp, lineHeight = 21.sp, textAlign = TextAlign.Start
            )
            if (long) {
                TextButton(onClick = { expanded = !expanded }) { Text(if (expanded) "Show less" else "Show full text") }
            }
        }
    }
}

@Composable
private fun LactMedCards(lm: LactMedResult) {
    if (lm.summary.isNotBlank()) {
        Spacer(Modifier.height(12.dp))
        LabelTextCard("LactMed: summary of use during lactation", lm.summary, PlBlue)
    }
    if (lm.infant.isNotBlank()) {
        Spacer(Modifier.height(12.dp))
        LabelTextCard("LactMed: effects in breastfed infants", lm.infant, PlBlue)
    }
    Spacer(Modifier.height(8.dp))
    Text(
        "Source: LactMed (US National Library of Medicine)" + if (lm.revised.isNotBlank()) ", revised ${lm.revised}" else "",
        fontSize = 12.sp, textAlign = TextAlign.Start, color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun BulletCard(title: String, items: List<String>, accent: Color) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = accent, textAlign = TextAlign.Start)
            Spacer(Modifier.height(8.dp))
            items.forEach {
                Text("•  $it", fontSize = 14.sp, lineHeight = 21.sp, textAlign = TextAlign.Start, modifier = Modifier.padding(vertical = 2.dp))
            }
        }
    }
}

@Composable
private fun Disclaimer(text: String) {
    Spacer(Modifier.height(20.dp))
    Text(text, fontSize = 12.sp, textAlign = TextAlign.Justify, color = MaterialTheme.colorScheme.onSurfaceVariant)
}

@Composable
private fun NumField(value: String, onChange: (String) -> Unit, label: String, modifier: Modifier = Modifier.fillMaxWidth(), decimal: Boolean = true) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = if (decimal) KeyboardType.Decimal else KeyboardType.Number),
        modifier = modifier
    )
}

@Composable
private fun TwoWayToggle(a: String, b: String, selected: Int, onSelect: (Int) -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(a, b).forEachIndexed { i, t ->
            if (selected == i) Button(onClick = { onSelect(i) }, modifier = Modifier.weight(1f)) { Text(t, maxLines = 1) }
            else OutlinedButton(onClick = { onSelect(i) }, modifier = Modifier.weight(1f)) { Text(t, maxLines = 1) }
        }
    }
}

@Composable
private fun CheckRow(text: String, checked: Boolean, onToggle: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable { onToggle() }.padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = checked, onCheckedChange = { onToggle() })
        Text(text, fontSize = 14.sp, lineHeight = 20.sp, textAlign = TextAlign.Start, modifier = Modifier.weight(1f))
    }
}

@Composable
fun PregnancyLactationScreen(onBackClick: () -> Unit) {
    BackHandler { onBackClick() }
    val context = LocalContext.current
    val dao = remember { AppDatabase.getInstance(context).drugDao() }
    var tab by remember { mutableStateOf(0) }
    var section by remember { mutableStateOf(0) }
    var queryTfv by remember { mutableStateOf(TextFieldValue("")) }
    val query = queryTfv.text
    var suggestions by remember { mutableStateOf<List<SearchHit>>(emptyList()) }
    var drug by remember { mutableStateOf<SearchHit?>(null) }
    var loaded by remember { mutableStateOf(false) }
    var pregText by remember { mutableStateOf<String?>(null) }
    var halfLifeLabel by remember { mutableStateOf<String?>(null) }
    var lactMed by remember { mutableStateOf<LactMedResult?>(null) }

    var modePlan by remember { mutableStateOf(0) }
    var lmpD by remember { mutableStateOf("") }
    var lmpM by remember { mutableStateOf("") }
    var lmpY by remember { mutableStateOf("") }
    var firstGa by remember { mutableStateOf("") }
    var lastGa by remember { mutableStateOf("") }
    var hlInput by remember { mutableStateOf("") }

    var ridMode by remember { mutableStateOf(0) }
    var mDose by remember { mutableStateOf("") }
    var mWeight by remember { mutableStateOf("") }
    var milkConc by remember { mutableStateOf("") }
    var mpRatio by remember { mutableStateOf("") }
    var plasmaConc by remember { mutableStateOf("") }
    var intake by remember { mutableStateOf("150") }
    var infantWt by remember { mutableStateOf("") }
    var pedDose by remember { mutableStateOf("") }

    val riskChecked = remember { mutableStateMapOf<String, Boolean>() }

    LaunchedEffect(query) {
        val q = query.trim()
        suggestions = if (q.length < 2 || drug?.name == q) emptyList() else dao.search(q)
    }
    LaunchedEffect(drug) {
        val d = drug
        loaded = false
        pregText = null
        halfLifeLabel = null
        lactMed = null
        if (d != null) {
            lactMed = try {
                withContext(Dispatchers.IO) {
                    LactMedRepository.load(context)
                    LactMedRepository.getLactMed(d.name)
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                null
            }
            val lab = dao.getLabel(d.rxcui)
            val ing = dao.getIngredient(d.rxcui)
            pregText = lab?.pregnancy?.takeIf { it.isNotBlank() }?.let { tidyLabel(it) }
            val ht = lab?.halfLife?.toString() ?: ing?.halfLifeText
            halfLifeLabel = ht?.takeIf { it.isNotBlank() }
            exHours(ht)?.let { hlInput = plNum(it) }
            loaded = true
        }
    }

    val split: Pair<String?, String?> = pregText?.let { splitLactation(it) } ?: Pair(null, null)
    val pregPart = split.first
    val lactPart = split.second
    val pregSignals = remember(pregText) { pregPart?.let { detectPregSignals(it) } ?: emptyList() }
    val lactSignals = remember(pregText) { lactPart?.let { detectLactSignals(it) } ?: emptyList() }
    val pregStrong = pregSignals.filter { it.level > 0 }.maxOfOrNull { it.level } ?: 0
    val lactStrong = lactSignals.filter { it.level > 0 }.maxOfOrNull { it.level } ?: 0
    val tabs = listOf("Drug", "Exposure", "RID", "Infant Risk")

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 20.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBackClick) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") }
                    Spacer(Modifier.width(8.dp))
                    Text("Pregnancy & Lactation", fontSize = 26.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = queryTfv,
                    onValueChange = { queryTfv = it; if (drug?.name != it.text) drug = null },
                    placeholder = { Text("Search Drug") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                suggestions.take(6).forEach { hit ->
                    Text(
                        text = hit.name + (hit.drugClass?.let { " · $it" } ?: ""),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                drug = hit
                                queryTfv = TextFieldValue(hit.name, TextRange(hit.name.length))
                                suggestions = emptyList()
                            }
                            .padding(vertical = 12.dp, horizontal = 4.dp),
                        textAlign = TextAlign.Start
                    )
                }
                Spacer(Modifier.height(8.dp))
            }

            ScrollableTabRow(selectedTabIndex = tab, edgePadding = 12.dp) {
                tabs.forEachIndexed { i, t ->
                    Tab(selected = tab == i, onClick = { tab = i }, text = { Text(t, maxLines = 1, fontSize = 14.sp) })
                }
            }

            Column(
                modifier = Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()).padding(20.dp),
                horizontalAlignment = Alignment.Start
            ) {
                when (tab) {
                    0 -> {
                        if (drug == null || !loaded) {
                            EmptyState("Search a drug to check its safety in pregnancy and lactation.")
                        } else {
                            val (pl, pc) = levelOf(pregText != null, pregStrong)
                            val (ll, lc) = levelOf(lactPart != null, lactStrong)
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(drug?.name ?: "", fontSize = 20.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                                    drug?.drugClass?.let {
                                        Text(it, fontSize = 13.sp, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    halfLifeLabel?.takeIf { it.length <= 60 }?.let {
                                        Spacer(Modifier.height(4.dp))
                                        Text("Half-life: $it", fontSize = 13.sp, textAlign = TextAlign.Center)
                                    }
                                }
                            }
                            Spacer(Modifier.height(12.dp))
                            Row(Modifier.fillMaxWidth()) {
                                GlanceCard(Modifier.weight(1f), "Pregnancy", pl, pc)
                                Spacer(Modifier.width(12.dp))
                                GlanceCard(Modifier.weight(1f), "Lactation", ll, lc)
                            }
                            Spacer(Modifier.height(16.dp))
                            TwoWayToggle("Pregnancy", "Lactation", section) { section = it }
                            Spacer(Modifier.height(16.dp))
                            if (section == 0) {
                                if (pregText == null) {
                                    BannerCard("No pregnancy section on file", "Consult current product labelling and a reproductive toxicology reference.", PlGrey)
                                } else {
                                    SignalList(pregSignals)
                                    pregPart?.takeIf { it.isNotBlank() }?.let { LabelTextCard("Pregnancy: label text", it, PlPink) }
                                    Spacer(Modifier.height(12.dp))
                                    Text(
                                        "Current US labelling follows the Pregnancy and Lactation Labeling Rule (PLLR), which replaced the A, B, C, D and X letter categories with a narrative Risk Summary, Clinical Considerations and Data. Older labels may still show a letter category.",
                                        fontSize = 12.sp, textAlign = TextAlign.Justify, color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            } else {
                                if (lactPart == null) {
                                    BannerCard("No lactation section on file", "Check LactMed (NLM) or the full product labelling. Use the RID tab if milk data are available.", PlGrey)
                                } else {
                                    SignalList(lactSignals)
                                    LabelTextCard("Lactation: label text", lactPart, PlBlue)
                                }
                                lactMed?.let { lm -> LactMedCards(lm) }
                            }
                            Spacer(Modifier.height(16.dp))
                            val steps = mutableListOf<String>()
                            if (pregStrong == 2) {
                                steps += "Confirm the indication and whether treatment is essential"
                                steps += "Consider an alternative with better pregnancy data (see Therapeutic Alternatives)"
                                steps += "Check the label for contraception or pregnancy-testing requirements"
                            } else if (pregStrong == 1) {
                                steps += "Weigh benefit against risk for the trimester involved"
                                steps += "Use the Exposure tab to place any exposure in the developmental timeline"
                            } else {
                                steps += "Absence of warning wording is not evidence of safety: consult a reproductive toxicology reference"
                            }
                            if (lactStrong == 2) {
                                steps += "If breastfeeding, review alternatives or discuss interrupting breastfeeding"
                            } else {
                                steps += "If breastfeeding, estimate infant exposure with the RID tab and screen with Infant Risk"
                            }
                            steps += "Document counselling and the shared decision"
                            BulletCard("Suggested next steps", steps, PlGreen)
                        }
                        Disclaimer("Signals are detected from stored label wording and are not a risk classification. Verify against current product labelling and a reproductive toxicology reference before clinical decisions.")
                    }
                    1 -> {
                        SectionTitle("Dating and exposure assessment")
                        TwoWayToggle("Pregnant", "Planning", modePlan) { modePlan = it }
                        Spacer(Modifier.height(16.dp))
                        val t12 = hlInput.toDoubleOrNull()?.takeIf { it > 0 }
                        if (modePlan == 0) {
                            Text("LMP (optional)", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                            Spacer(Modifier.height(8.dp))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                NumField(lmpD, { lmpD = it.filter { c -> c.isDigit() }.take(2) }, "Day", Modifier.weight(1f), false)
                                NumField(lmpM, { lmpM = it.filter { c -> c.isDigit() }.take(2) }, "Month", Modifier.weight(1f), false)
                                NumField(lmpY, { lmpY = it.filter { c -> c.isDigit() }.take(4) }, "Year", Modifier.weight(1.3f), false)
                            }
                            val lmp = if (lmpD.isNotEmpty() && lmpM.isNotEmpty() && lmpY.length == 4)
                                lmpInfo(lmpD.toInt(), lmpM.toInt(), lmpY.toInt()) else null
                            if (lmp != null) {
                                Spacer(Modifier.height(12.dp))
                                val gw = (lmp.gaDays / 7).toInt()
                                InfoCard("Gestational age today", exGa(lmp.gaDays), "${triOf(gw)} · EDD ${lmp.edd} (Naegele) · conception ≈ ${lmp.conception}")
                                TextButton(onClick = { lastGa = gw.toString() }) { Text("Use week $gw as last-dose week") }
                            } else if (lmpD.isNotEmpty() && lmpM.isNotEmpty() && lmpY.length == 4) {
                                Text("Enter a valid LMP within the last 300 days.", fontSize = 13.sp, color = PlRed)
                            }
                            Spacer(Modifier.height(16.dp))
                            Text("Exposure", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                            Spacer(Modifier.height(8.dp))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                NumField(firstGa, { firstGa = it.filter { c -> c.isDigit() }.take(2) }, "First dose (GA wk)", Modifier.weight(1f), false)
                                NumField(lastGa, { lastGa = it.filter { c -> c.isDigit() }.take(2) }, "Last dose (GA wk)", Modifier.weight(1f), false)
                            }
                            Spacer(Modifier.height(8.dp))
                            NumField(hlInput, { hlInput = it }, "Elimination half-life (hours)")
                            if (halfLifeLabel != null) {
                                Text("Label: ${halfLifeLabel}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Start)
                            }
                            val f = firstGa.toIntOrNull()
                            val l = lastGa.toIntOrNull()
                            if (f != null && f in 1..42) {
                                val lo = if (l != null && l in 1..42) minOf(f, l) else f
                                val hi = if (l != null && l in 1..42) maxOf(f, l) else f
                                Spacer(Modifier.height(16.dp))
                                val overlapOrg = lo <= 10 && hi >= 5
                                val allOrNone = hi <= 4 && lo >= 3
                                when {
                                    overlapOrg -> BannerCard("Overlaps organogenesis", "Exposure includes GA weeks 5–10, the period of greatest susceptibility to structural malformations.", PlRed)
                                    allOrNone -> BannerCard("All-or-none period", "Exposure was within the first 2 weeks after conception: usually loss of the conceptus or no effect. Long half-life drugs may persist.", PlAmber)
                                    lo > 10 && lo <= 27 -> BannerCard("After organogenesis", "Structural risk is lower, but growth, CNS and functional effects remain possible.", PlAmber)
                                    lo > 27 -> BannerCard("Late pregnancy", "Main concerns are neonatal adaptation effects and labour or delivery considerations.", PlBlue)
                                    else -> BannerCard("Before conception", "Exposure ended before fertilisation. Check whether the drug persists (below).", PlBlue)
                                }
                                Spacer(Modifier.height(12.dp))
                                Text("Week $lo: ${exPhase(lo)}", fontSize = 14.sp, textAlign = TextAlign.Start)
                                if (hi != lo) Text("Week $hi: ${exPhase(hi)}", fontSize = 14.sp, textAlign = TextAlign.Start)
                                Spacer(Modifier.height(16.dp))
                                SectionTitle("Organ systems in the exposure window")
                                val maxW = 16
                                Row(Modifier.fillMaxWidth()) {
                                    Column(Modifier.width(78.dp)) {
                                        exSystems.forEach { Box(Modifier.height(26.dp), contentAlignment = Alignment.CenterStart) { Text(it.name, fontSize = 12.sp) } }
                                    }
                                    Canvas(Modifier.weight(1f).height(26.dp * exSystems.size)) {
                                        val cw = size.width / maxW
                                        val rh = size.height / exSystems.size
                                        exSystems.forEachIndexed { r, s ->
                                            for (w in 1..maxW) {
                                                val col = if (w in s.from..s.to) PlRed else Color(0xFF94A3B8).copy(alpha = 0.2f)
                                                drawRect(col, Offset((w - 1) * cw, r * rh + 1f), Size(cw, rh - 2f))
                                            }
                                        }
                                        val x0 = (lo - 1).coerceAtMost(maxW) * cw
                                        val x1 = hi.coerceAtMost(maxW) * cw
                                        if (lo <= maxW) {
                                            drawRect(PlInk, Offset(x0, 0f), Size(maxOf(x1 - x0, 4f), size.height), style = Stroke(width = 4f))
                                        }
                                    }
                                }
                                Row(Modifier.fillMaxWidth().padding(start = 78.dp, top = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("GA wk 1", fontSize = 11.sp)
                                    Text("8", fontSize = 11.sp)
                                    Text("16", fontSize = 11.sp)
                                }
                                Text("Red = high-sensitivity window. Black frame = your exposure. Windows are approximate (LMP-based).", fontSize = 11.sp, textAlign = TextAlign.Start, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                val atRisk = exSystems.filter { it.from <= hi && it.to >= lo }.map { it.name }
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    if (atRisk.isEmpty()) "No system is in its peak-sensitivity window during this exposure."
                                    else "Systems in peak window: ${atRisk.joinToString(", ")}",
                                    fontSize = 14.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Start
                                )
                                if (t12 != null) {
                                    Spacer(Modifier.height(16.dp))
                                    val endGa = hi + t12 * 5 / 168.0
                                    InfoCard("Estimated elimination after last dose", exDur(t12 * 5), "5 half-lives (≈97% eliminated) · drug present until ≈ GA ${plNum(endGa)} weeks")
                                    if (hi < 5 && endGa >= 5) {
                                        Spacer(Modifier.height(8.dp))
                                        Text("The drug may still be present when organogenesis begins (GA week 5).", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = PlRed, textAlign = TextAlign.Start)
                                    } else if (hi <= 10 && endGa > hi + 0.3 && endGa >= 5) {
                                        Spacer(Modifier.height(8.dp))
                                        Text("Persistence extends the effective exposure window beyond the last dose.", fontSize = 13.sp, color = PlAmber, textAlign = TextAlign.Start)
                                    }
                                }
                            }
                        } else {
                            NumField(hlInput, { hlInput = it }, "Elimination half-life (hours)")
                            if (halfLifeLabel != null) {
                                Text("Label: ${halfLifeLabel}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Start)
                            }
                            if (t12 != null) {
                                Spacer(Modifier.height(16.dp))
                                InfoCard("Suggested drug-free interval before conception", exDur(t12 * 5), "At least 5 half-lives (≈97% eliminated)")
                                Spacer(Modifier.height(12.dp))
                                BulletCard(
                                    "Planning considerations",
                                    listOf(
                                        "Some labels specify longer intervals than 5 half-lives (for example acitretin, leflunomide, mycophenolate); the label interval takes precedence",
                                        "Active metabolites, depot formulations and renal or hepatic impairment prolong elimination",
                                        "Do not stop essential therapy (for example antiepileptics, insulin, antihypertensives) without prescriber advice",
                                        "Review all medicines, supplements and folic acid at preconception counselling"
                                    ),
                                    PlBlue
                                )
                            } else {
                                Spacer(Modifier.height(16.dp))
                                Text("Enter a half-life, or search a drug that has one on file.", fontSize = 14.sp, textAlign = TextAlign.Start)
                            }
                        }
                        Disclaimer("Gestational age is counted from the first day of the LMP; conception is typically ≈ 2 weeks later. Developmental windows and elimination estimates assume first-order kinetics and are approximate. Drug-specific risk depends on agent, dose and duration: verify against current product labelling and a reproductive toxicology reference.")
                    }
                    2 -> {
                        SectionTitle("Relative Infant Dose (RID)")
                        TwoWayToggle("Milk level known", "Use M/P ratio", ridMode) { ridMode = it }
                        Spacer(Modifier.height(12.dp))
                        NumField(mDose, { mDose = it }, "Maternal dose (mg/day)")
                        Spacer(Modifier.height(8.dp))
                        NumField(mWeight, { mWeight = it }, "Maternal weight (kg)")
                        Spacer(Modifier.height(8.dp))
                        if (ridMode == 0) {
                            NumField(milkConc, { milkConc = it }, "Milk concentration (mg/L)")
                        } else {
                            NumField(mpRatio, { mpRatio = it }, "Milk-to-plasma (M/P) ratio")
                            Spacer(Modifier.height(8.dp))
                            NumField(plasmaConc, { plasmaConc = it }, "Maternal plasma concentration (mg/L)")
                        }
                        Spacer(Modifier.height(8.dp))
                        NumField(intake, { intake = it }, "Infant milk intake (mL/kg/day)")
                        Spacer(Modifier.height(8.dp))
                        NumField(infantWt, { infantWt = it }, "Infant weight (kg, optional)")
                        Spacer(Modifier.height(8.dp))
                        NumField(pedDose, { pedDose = it }, "Infant therapeutic dose (mg/kg/day, optional)")

                        val dose = mDose.toDoubleOrNull()
                        val mw = mWeight.toDoubleOrNull()
                        val cm = if (ridMode == 0) milkConc.toDoubleOrNull()
                        else (mpRatio.toDoubleOrNull()?.let { r -> plasmaConc.toDoubleOrNull()?.let { c -> r * c } })
                        val vol = intake.toDoubleOrNull()
                        if (dose != null && mw != null && cm != null && vol != null && dose > 0 && mw > 0 && cm >= 0 && vol > 0) {
                            val mat = dose / mw
                            val infant = cm * vol / 1000.0
                            val rid = infant / mat * 100.0
                            val (verdict, vcol) = when {
                                rid < 10 -> "Generally considered compatible with breastfeeding (RID below 10%)" to PlGreen
                                rid <= 25 -> "Borderline exposure: consider infant age and prematurity, and monitor the infant" to PlAmber
                                else -> "High infant exposure: generally avoid unless benefit clearly outweighs risk" to PlRed
                            }
                            Spacer(Modifier.height(16.dp))
                            InfoCard("Relative Infant Dose", "${plNum(rid)}%", "Maternal ${plNum(mat)} mg/kg/day · infant ${plNum(infant)} mg/kg/day · milk ${plNum(cm)} mg/L")
                            Spacer(Modifier.height(12.dp))
                            Canvas(Modifier.fillMaxWidth().height(24.dp)) {
                                val w = size.width
                                val h = size.height
                                var x = 0f
                                listOf(10f to PlGreen, 15f to PlAmber, 15f to PlRed).forEach { (len, col) ->
                                    val zw = len / 40f * w
                                    drawRect(col, Offset(x, 0f), Size(zw, h))
                                    x += zw
                                }
                                val mx = (minOf(rid, 40.0) / 40.0 * w).toFloat()
                                drawRect(PlInk, Offset(mx - 3f, 0f), Size(6f, h))
                            }
                            Spacer(Modifier.height(4.dp))
                            Row(Modifier.fillMaxWidth()) {
                                Text("<10%", fontSize = 11.sp, modifier = Modifier.weight(10f), textAlign = TextAlign.Center)
                                Text("10–25%", fontSize = 11.sp, modifier = Modifier.weight(15f), textAlign = TextAlign.Center)
                                Text(">25%", fontSize = 11.sp, modifier = Modifier.weight(15f), textAlign = TextAlign.Center)
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(verdict, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = vcol, textAlign = TextAlign.Start)
                            infantWt.toDoubleOrNull()?.takeIf { it > 0 }?.let { iw ->
                                Spacer(Modifier.height(12.dp))
                                InfoCard("Absolute infant dose", "${plNum(infant * iw)} mg/day", "Infant weight ${plNum(iw)} kg")
                            }
                            pedDose.toDoubleOrNull()?.takeIf { it > 0 }?.let { pd ->
                                Spacer(Modifier.height(12.dp))
                                InfoCard("Percent of infant therapeutic dose", "${plNum(infant / pd * 100)}%", "Infant exposure ÷ therapeutic dose (${plNum(pd)} mg/kg/day)")
                            }
                        }
                        Disclaimer("RID = infant dose ÷ maternal dose × 100, with infant dose = milk concentration × milk intake (default 150 mL/kg/day). The 10% and 25% cut-offs are commonly used guides. Peak and average milk levels differ, prematurity and young age raise vulnerability, and some agents (for example cytotoxics and radiopharmaceuticals) warrant caution regardless of RID.")
                    }
                    else -> {
                        SectionTitle("Infant risk-factor screen")
                        Text("Tick every factor that applies. This heuristic screen supports, but does not replace, clinical judgement.", fontSize = 13.sp, textAlign = TextAlign.Start, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (drug != null && loaded && lactStrong > 0) {
                            Spacer(Modifier.height(8.dp))
                            Text("The label for ${drug?.name} contains lactation caution wording. Consider ticking the last risk item.", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = PlAmber, textAlign = TextAlign.Start)
                        }
                        Spacer(Modifier.height(12.dp))
                        Text("Risk factors", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = PlRed)
                        infantRiskFactors.forEach { f ->
                            CheckRow(f.text, riskChecked[f.text] == true) { riskChecked[f.text] = riskChecked[f.text] != true }
                        }
                        CheckRow(infantCritical.text, riskChecked[infantCritical.text] == true) {
                            riskChecked[infantCritical.text] = riskChecked[infantCritical.text] != true
                        }
                        Spacer(Modifier.height(12.dp))
                        Text("Protective factors", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = PlGreen)
                        infantProtective.forEach { t ->
                            CheckRow(t, riskChecked[t] == true) { riskChecked[t] = riskChecked[t] != true }
                        }
                        val riskScore = infantRiskFactors.sumOf { if (riskChecked[it.text] == true) it.weight else 0 }
                        val critical = riskChecked[infantCritical.text] == true
                        val protect = infantProtective.count { riskChecked[it] == true }
                        val net = (riskScore - protect).coerceAtLeast(0)
                        val (lvl, lcol) = when {
                            critical || net >= 4 -> "Higher concern" to PlRed
                            net >= 2 -> "Moderate concern" to PlAmber
                            else -> "Lower concern" to PlGreen
                        }
                        Spacer(Modifier.height(16.dp))
                        BannerCard(lvl, "Risk factors ${riskScore + (if (critical) infantCritical.weight else 0)} · protective factors $protect", lcol)
                        Spacer(Modifier.height(12.dp))
                        val advice = when {
                            critical || net >= 4 -> listOf(
                                "Review whether an alternative with more breastfeeding data is suitable",
                                "If continued, monitor closely for sedation, poor feeding, irritability, apnoea and poor weight gain",
                                "Consider timing doses after a feed, and discuss with a specialist (for example paediatrics or a lactation service)"
                            )
                            net >= 2 -> listOf(
                                "Monitor the infant for sedation, feeding difficulty, irritability and weight gain",
                                "Use the lowest effective dose and time doses after feeds",
                                "Re-screen if the infant is unwell or the regimen changes"
                            )
                            else -> listOf(
                                "Routine counselling: report drowsiness, poor feeding or unusual irritability",
                                "Re-screen if the infant is very young, preterm or unwell, or if the regimen changes"
                            )
                        }
                        BulletCard("Suggested monitoring", advice, lcol)
                        Spacer(Modifier.height(12.dp))
                        TextButton(onClick = { riskChecked.clear() }) { Text("Clear all") }
                        Disclaimer("This is a heuristic screening aid based on commonly cited determinants of infant drug exposure (infant age and maturity, drug half-life, protein binding, lipophilicity, oral bioavailability and CNS activity). It is not a validated score. Always verify against LactMed or an equivalent reference.")
                    }
                }
            }
        }
    }
}
