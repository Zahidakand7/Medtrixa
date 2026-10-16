package com.medtrixa.app.ui.screens.drugtools

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val TxPink = Color(0xFFD6456B)
private val TxAmber = Color(0xFFC27A00)
private val TxBlue = Color(0xFF2F6F9F)
private val TxRed = Color(0xFFC0392B)
private val TxLow = Color(0xFFE8A33D)
private val TxGrey = Color(0xFF94A3B8)
private val TxInk = Color(0xFF111827)

private data class TxSys(val name: String, val highFrom: Int, val highTo: Int, val lowTo: Int)
private data class TxMilestone(val from: Int, val to: Int, val title: String, val text: String)
private data class TxSize(val week: Int, val length: String, val weight: String)

private val txSystems = listOf(
    TxSys("CNS", 5, 12, 40),
    TxSys("Heart", 5, 10, 14),
    TxSys("Limbs", 6, 10, 14),
    TxSys("Eyes", 6, 10, 40),
    TxSys("Ears", 6, 11, 40),
    TxSys("Palate", 8, 12, 16),
    TxSys("Genitalia", 9, 14, 40)
)

private fun txLevel(s: TxSys, w: Int): Int = when {
    w in s.highFrom..s.highTo -> 2
    w > s.highTo && w <= s.lowTo -> 1
    else -> 0
}

private val txSizes = listOf(
    TxSize(5, "≈ 2 mm", "—"),
    TxSize(6, "≈ 5 mm", "—"),
    TxSize(7, "≈ 1 cm", "—"),
    TxSize(8, "≈ 1.6 cm", "≈ 1 g"),
    TxSize(9, "≈ 2.3 cm", "≈ 2 g"),
    TxSize(10, "≈ 3.1 cm", "≈ 4 g"),
    TxSize(12, "≈ 5.4 cm", "≈ 14 g"),
    TxSize(14, "≈ 8.7 cm", "≈ 43 g"),
    TxSize(16, "≈ 11.6 cm", "≈ 100 g"),
    TxSize(20, "≈ 25 cm", "≈ 300 g"),
    TxSize(24, "≈ 30 cm", "≈ 600 g"),
    TxSize(28, "≈ 37 cm", "≈ 1 kg"),
    TxSize(32, "≈ 42 cm", "≈ 1.7 kg"),
    TxSize(36, "≈ 47 cm", "≈ 2.6 kg"),
    TxSize(40, "≈ 51 cm", "≈ 3.4 kg")
)

private val txMilestones = listOf(
    TxMilestone(1, 2, "Pre-ovulatory phase", "Gestational age (GA) is counted from the last menstrual period (LMP). Conception occurs ≈ week 2, so no embryo exists yet."),
    TxMilestone(3, 4, "Fertilisation to implantation", "The first 2 weeks after conception are the 'all-or-none' period: a harmful exposure usually causes loss of the conceptus or no effect, rather than malformation."),
    TxMilestone(4, 5, "Gastrulation", "hCG rises and the period is missed. The three germ layers form."),
    TxMilestone(5, 6, "Neural tube and heart tube", "The neural plate folds and the heart tube forms. Cardiac activity is usually detectable on transvaginal ultrasound at ≈ GA 6 weeks."),
    TxMilestone(6, 7, "Neural tube closure and limb buds", "Neural tube closure completes ≈ GA week 6 (about day 28 after conception). Limb buds appear."),
    TxMilestone(7, 10, "Peak organogenesis", "Heart septation, eye and ear formation, facial structures, palatal shelves and limb differentiation. This is the window of highest vulnerability to major structural malformations."),
    TxMilestone(10, 10, "Embryo becomes fetus", "At the end of GA week 10 (8 weeks after conception) the embryo is termed a fetus. Major organs have begun forming."),
    TxMilestone(9, 14, "Palate and external genitalia", "The secondary palate fuses ≈ GA weeks 9–12 and external genitalia differentiate ≈ GA weeks 9–14."),
    TxMilestone(11, 14, "First-trimester screening", "Nuchal translucency scan and combined screening are typically done at ≈ 11 to 13+6 weeks."),
    TxMilestone(14, 16, "Rapid growth", "Fetal growth accelerates. The CNS, eyes, ears and genitalia continue to develop."),
    TxMilestone(18, 22, "Anomaly scan", "Detailed anatomy ultrasound ≈ 18–22 weeks. Fetal movements (quickening) are often first felt ≈ 16–22 weeks."),
    TxMilestone(22, 24, "Periviable threshold", "Survival becomes possible with intensive care at ≈ 22–24 weeks. Type II pneumocytes begin producing surfactant ≈ 24 weeks."),
    TxMilestone(24, 28, "Gestational diabetes screening", "Screening for gestational diabetes is commonly performed ≈ 24–28 weeks. Antenatal corticosteroids are considered when preterm birth is anticipated."),
    TxMilestone(28, 32, "Third trimester begins", "Rapid brain growth and fat deposition. Anti-D prophylaxis is usually offered ≈ 28 weeks if the mother is Rh(D)-negative."),
    TxMilestone(34, 37, "Lung maturation", "Surfactant production matures. Respiratory morbidity falls steadily toward term."),
    TxMilestone(37, 40, "Term", "Early term 37 to 38+6 weeks, full term 39 to 40+6 weeks. Drug exposure near delivery can cause neonatal adaptation effects.")
)

private val txTriNames = listOf("First trimester", "Second trimester", "Third trimester")

private val txTriItems = listOf(
    listOf(
        "Neural tube closure completes ≈ GA week 6. Folic acid 0.4 mg/day is advised from preconception, with higher doses for high-risk patients per local guidance.",
        "Classic first-trimester teratogens include isotretinoin, valproate, methotrexate, mycophenolate, thalidomide and warfarin (embryopathy risk ≈ weeks 6–12).",
        "Before the missed period exposure is largely all-or-none, but long half-life agents can persist into organogenesis (see the Half-life & Washout tool).",
        "Review every chronic medicine at the first positive test. Do not stop essential therapy (for example antiepileptics, insulin, antihypertensives) without medical advice."
    ),
    listOf(
        "ACE inhibitors and ARBs: fetal renal dysgenesis, oligohydramnios, pulmonary and skull hypoplasia. Avoid in the second and third trimesters.",
        "Tetracyclines: tooth discolouration and enamel hypoplasia. Avoid from the second trimester.",
        "Aminoglycosides: risk of fetal ototoxicity (notably streptomycin).",
        "The anomaly scan (≈ 18–22 weeks) reviews fetal structure after earlier exposures."
    ),
    listOf(
        "NSAIDs: from ≈ 20 weeks they can cause fetal renal impairment and oligohydramnios; after ≈ 30 weeks there is a risk of premature ductus arteriosus closure. Generally avoid.",
        "Opioids, benzodiazepines and SSRIs near term: neonatal withdrawal or adaptation syndrome. Inform the paediatric team.",
        "Opioids, magnesium sulfate and sedatives near delivery: neonatal respiratory depression.",
        "Beta-blockers near term: neonatal bradycardia and hypoglycaemia, so monitor the newborn."
    )
)

private val txPkItems = listOf(
    "Plasma volume rises ≈ 40–50%, which increases the volume of distribution of hydrophilic drugs.",
    "GFR rises ≈ 50% by mid-pregnancy, which speeds clearance of renally eliminated drugs.",
    "CYP3A4, CYP2D6, CYP2C9 and UGT1A4 activity increases; CYP1A2 and CYP2C19 activity decreases.",
    "Albumin falls, which raises the free fraction of highly protein-bound drugs.",
    "Dose adjustment with drug-level monitoring may be needed in the second and third trimesters (for example lamotrigine, lithium)."
)

@Composable
private fun TxBullets(title: String, items: List<String>, accent: Color) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = accent, textAlign = TextAlign.Start)
            Spacer(Modifier.height(8.dp))
            items.forEach {
                Text(
                    "•  $it", fontSize = 14.sp, lineHeight = 21.sp, textAlign = TextAlign.Start,
                    modifier = Modifier.padding(vertical = 3.dp)
                )
            }
        }
    }
}

@Composable
fun TrimesterExplorer(
    pregText: String?,
    drugName: String?,
    gaInput: String,
    onGaChange: (String) -> Unit
) {
    var week by remember { mutableStateOf(gaInput.toIntOrNull()?.coerceIn(1, 40) ?: 8) }
    fun setWeek(w: Int) {
        week = w.coerceIn(1, 40)
        onGaChange(week.toString())
    }
    val tri = when {
        week <= 13 -> 0
        week <= 27 -> 1
        else -> 2
    }
    val triColor = listOf(TxPink, TxAmber, TxBlue)[tri]
    val stage = when {
        week <= 2 -> "Pre-conception / fertilisation"
        week <= 4 -> "Germinal period (implantation)"
        week <= 10 -> "Embryonic period"
        else -> "Fetal period"
    }
    val sz = txSizes.lastOrNull { it.week <= week }

    Text("Interactive gestational timeline", fontWeight = FontWeight.SemiBold, fontSize = 18.sp, textAlign = TextAlign.Start)
    Spacer(Modifier.height(4.dp))
    Text(
        "Drag the slider or tap the chart to move to any week. Tap T1, T2 or T3 to jump.",
        fontSize = 13.sp, textAlign = TextAlign.Start,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(Modifier.height(12.dp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = triColor.copy(alpha = 0.12f))
    ) {
        Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Gestational week", fontSize = 13.sp, textAlign = TextAlign.Center)
            Text("$week", fontSize = 46.sp, fontWeight = FontWeight.Bold, color = triColor)
            Text(
                "${txTriNames[tri]} · $stage", fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(6.dp))
            Text(
                if (sz == null) "Size: microscopic (pre-embryonic)"
                else "Length ${sz.length} · Weight ${sz.weight}",
                fontSize = 13.sp, textAlign = TextAlign.Center
            )
            Text("Approximate population averages", fontSize = 11.sp, textAlign = TextAlign.Center)
        }
    }

    Spacer(Modifier.height(12.dp))
    Row(Modifier.fillMaxWidth().height(40.dp).clip(RoundedCornerShape(10.dp))) {
        listOf(13, 14, 13).forEachIndexed { i, wt ->
            Box(
                Modifier
                    .weight(wt.toFloat())
                    .fillMaxHeight()
                    .background(listOf(TxPink, TxAmber, TxBlue)[i].copy(alpha = if (tri == i) 1f else 0.35f))
                    .clickable { setWeek(listOf(7, 20, 34)[i]) },
                contentAlignment = Alignment.Center
            ) {
                Text("T${i + 1}", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
    Slider(
        value = week.toFloat(),
        onValueChange = { setWeek(it.toInt()) },
        valueRange = 1f..40f,
        steps = 38
    )

    Spacer(Modifier.height(8.dp))
    Text("Sensitivity of organ systems to teratogens", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, textAlign = TextAlign.Start)
    Spacer(Modifier.height(8.dp))
    Row(Modifier.fillMaxWidth()) {
        Column(Modifier.width(78.dp)) {
            txSystems.forEach {
                Box(Modifier.height(26.dp), contentAlignment = Alignment.CenterStart) {
                    Text(it.name, fontSize = 12.sp)
                }
            }
        }
        Canvas(
            Modifier
                .weight(1f)
                .height(26.dp * txSystems.size)
                .pointerInput(Unit) {
                    detectTapGestures { o -> setWeek((o.x / size.width * 40).toInt() + 1) }
                }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { c, _ -> setWeek((c.position.x / size.width * 40).toInt() + 1) }
                }
        ) {
            val cw = size.width / 40f
            val rh = size.height / txSystems.size
            txSystems.forEachIndexed { r, s ->
                for (w in 1..40) {
                    val col = when (txLevel(s, w)) {
                        2 -> TxRed
                        1 -> TxLow.copy(alpha = 0.55f)
                        else -> TxGrey.copy(alpha = 0.18f)
                    }
                    drawRect(col, Offset((w - 1) * cw, r * rh + 1f), Size(cw, rh - 2f))
                }
            }
            listOf(13, 27).forEach {
                drawLine(TxInk.copy(alpha = 0.4f), Offset(it * cw, 0f), Offset(it * cw, size.height), 2f)
            }
            drawRect(TxInk, Offset((week - 0.5f) * cw - 2f, 0f), Size(4f, size.height))
        }
    }
    Row(Modifier.fillMaxWidth().padding(start = 78.dp, top = 4.dp)) {
        Text("T1 · 1–13", fontSize = 11.sp, modifier = Modifier.weight(13f), textAlign = TextAlign.Center, color = TxPink)
        Text("T2 · 14–27", fontSize = 11.sp, modifier = Modifier.weight(14f), textAlign = TextAlign.Center, color = TxAmber)
        Text("T3 · 28–40", fontSize = 11.sp, modifier = Modifier.weight(13f), textAlign = TextAlign.Center, color = TxBlue)
    }
    Spacer(Modifier.height(8.dp))
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(12.dp).background(TxRed))
        Text("  High     ", fontSize = 12.sp)
        Box(Modifier.size(12.dp).background(TxLow.copy(alpha = 0.55f)))
        Text("  Lower     ", fontSize = 12.sp)
        Box(Modifier.size(12.dp).background(TxGrey.copy(alpha = 0.25f)))
        Text("  Minimal / not formed", fontSize = 12.sp)
    }

    Spacer(Modifier.height(16.dp))
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Text("At week $week", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = triColor)
            Spacer(Modifier.height(8.dp))
            txSystems.forEach { s ->
                val lv = txLevel(s, week)
                val label = when (lv) { 2 -> "High"; 1 -> "Lower"; else -> "Minimal" }
                val col = when (lv) { 2 -> TxRed; 1 -> TxAmber; else -> TxGrey }
                Row(Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
                    Text(s.name, fontSize = 14.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Start)
                    Text(label, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = col)
                }
            }
        }
    }

    Spacer(Modifier.height(16.dp))
    val now = txMilestones.filter { week in it.from..it.to }
    val shown = if (now.isNotEmpty()) now else listOfNotNull(txMilestones.lastOrNull { it.from <= week })
    if (shown.isNotEmpty()) {
        TxBullets(
            "Key events around week $week",
            shown.map { "${it.title}: ${it.text}" },
            triColor
        )
        Spacer(Modifier.height(16.dp))
    }

    TxBullets("${txTriNames[tri]}: clinical considerations", txTriItems[tri], triColor)
    Spacer(Modifier.height(16.dp))

    if (drugName != null) {
        val re = when (tri) {
            0 -> Regex("first[- ]trimester|organogenesis|early pregnancy|periconcept|first 8 weeks", RegexOption.IGNORE_CASE)
            1 -> Regex("second[- ]trimester|second and third", RegexOption.IGNORE_CASE)
            else -> Regex("third[- ]trimester|late pregnancy|near term|labor|delivery|neonat|second and third", RegexOption.IGNORE_CASE)
        }
        val excerpts = remember(pregText, tri) {
            pregText
                ?.replace("\n", " ")
                ?.split(Regex("(?<=[.;])\\s+"))
                ?.map { it.trim() }
                ?.filter { it.length in 30..400 && re.containsMatchIn(it) }
                ?.take(5)
                ?: emptyList()
        }
        if (pregText == null) {
            Text(
                "No pregnancy text is stored for $drugName.",
                fontSize = 13.sp, textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else if (excerpts.isEmpty()) {
            Text(
                "No ${txTriNames[tri].lowercase()}-specific wording found in the stored label for $drugName. See the Pregnancy tab for the full text.",
                fontSize = 13.sp, textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            TxBullets("From the $drugName label (${txTriNames[tri].lowercase()})", excerpts, TxRed)
        }
        Spacer(Modifier.height(16.dp))
    } else {
        Text(
            "Select a drug above to also see trimester-specific wording from its label.",
            fontSize = 13.sp, textAlign = TextAlign.Start,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(16.dp))
    }

    TxBullets("Pharmacokinetic changes in pregnancy", txPkItems, TxBlue)
    Spacer(Modifier.height(20.dp))
    Text(
        "Sensitivity windows are approximate, adapted from classic critical-period teaching charts and converted to gestational age (LMP-based); the CNS remains vulnerable throughout gestation. Developmental timings and sizes vary between individuals. Drug-specific risk depends on the agent, dose and duration, so verify against current product labelling and a reproductive toxicology reference.",
        fontSize = 12.sp, textAlign = TextAlign.Justify,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}
