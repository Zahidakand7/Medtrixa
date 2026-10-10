package com.medtrixa.app.ui.screens.drugtools

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medtrixa.app.data.db.AppDatabase
import com.medtrixa.app.data.db.SearchHit

private fun parseHalfLifeHours(text: String?): Double? {
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

private fun num(x: Double): String = "%.2f".format(x).trimEnd('0').trimEnd('.')

private fun pct(x: Double): String = num(x * 100) + "%"

private fun fmtDuration(h: Double): String = when {
    h < 1 -> "${num(h * 60)} min"
    h < 48 -> "${num(h)} h"
    else -> "${num(h / 24)} days"
}

private fun fmtRange(lo: Double, hi: Double): String = when {
    lo >= 48 -> "${num(lo / 24)} – ${num(hi / 24)} days"
    lo >= 1 && hi < 48 -> "${num(lo)} – ${num(hi)} h"
    else -> "${fmtDuration(lo)} – ${fmtDuration(hi)}"
}

@Composable
fun HalfLifeWashoutScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val dao = remember { AppDatabase.getInstance(context).drugDao() }
    var stage by remember { mutableStateOf(0) }
    var loaded by remember { mutableStateOf(false) }
    var queryTfv by remember { mutableStateOf(TextFieldValue("")) }
    val query = queryTfv.text
    var suggestions by remember { mutableStateOf<List<SearchHit>>(emptyList()) }
    var drug by remember { mutableStateOf<SearchHit?>(null) }
    var sourceText by remember { mutableStateOf<String?>(null) }
    var halfLife by remember { mutableStateOf<Double?>(null) }
    var tauInput by remember { mutableStateOf("") }
    var targetIdx by remember { mutableStateOf(1) }
    var sliderN by remember { mutableStateOf(1f) }
    val scroll = rememberScrollState()

    fun backToSearch() {
        stage = 0
        drug = null
        sourceText = null
        halfLife = null
        tauInput = ""
        loaded = false
    }

    fun openDrug(hit: SearchHit) {
        halfLife = null
        sourceText = null
        tauInput = ""
        sliderN = 1f
        targetIdx = 1
        loaded = false
        drug = hit
        stage = 1
    }

    BackHandler { if (stage == 1) backToSearch() else onBackClick() }

    LaunchedEffect(stage) { scroll.scrollTo(0) }
    LaunchedEffect(query) {
        val q = query.trim()
        suggestions = if (q.length < 2) emptyList() else dao.search(q)
    }
    LaunchedEffect(drug) {
        val d = drug
        if (d == null) {
            sourceText = null
            halfLife = null
        } else {
            val ing = dao.getIngredient(d.rxcui)
            val lab = dao.getLabel(d.rxcui)
            val t = lab?.halfLife?.toString() ?: ing?.halfLifeText
            sourceText = t
            halfLife = parseHalfLifeHours(t)
            loaded = true
        }
    }

    val t12 = halfLife?.takeIf { it > 0 }
    val tau = tauInput.toDoubleOrNull()?.takeIf { it > 0 }
    val primary = MaterialTheme.colorScheme.primary
    val heroBrush = Brush.linearGradient(listOf(lerp(primary, Color.Black, 0.4f), primary))
    val cardShape = RoundedCornerShape(24.dp)

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(scroll)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 3.dp,
                    modifier = Modifier.size(44.dp)
                ) {
                    IconButton(onClick = { if (stage == 1) backToSearch() else onBackClick() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
                Spacer(Modifier.width(14.dp))
                Column {
                    Text("Half-life & Washout", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "Time to steady state and time to clearance",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(Modifier.height(20.dp))

            if (stage == 0) {
                // ---------- SEARCH WINDOW ----------
                OutlinedTextField(
                    value = queryTfv,
                    onValueChange = { queryTfv = it },
                    placeholder = { Text("Search Drug") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = {
                                queryTfv = TextFieldValue("")
                                suggestions = emptyList()
                            }) {
                                Icon(Icons.Filled.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                if (suggestions.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Column {
                            suggestions.take(8).forEachIndexed { i, hit ->
                                if (i > 0) {
                                    Box(
                                        Modifier.fillMaxWidth().height(1.dp)
                                            .background(MaterialTheme.colorScheme.outlineVariant)
                                    )
                                }
                                Column(
                                    Modifier
                                        .fillMaxWidth()
                                        .clickable { openDrug(hit) }
                                        .padding(horizontal = 16.dp, vertical = 12.dp)
                                ) {
                                    Text(hit.name, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                                    hit.drugClass?.let {
                                        Text(it, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Spacer(Modifier.height(16.dp))
                    InfoCard(
                        "Search for a medicine",
                        "Select a drug to view its elimination half-life, time to steady state, washout time, decay curve and accumulation estimates."
                    )
                }
            } else {
                // ---------- RESULT WINDOW ----------
                val d = drug
                DrugHeroCard(
                    name = d?.name ?: "",
                    drugClass = d?.drugClass,
                    halfLifeValue = when {
                        t12 != null -> fmtDuration(t12)
                        loaded -> "Not available"
                        else -> "…"
                    },
                    note = when {
                        !loaded -> ""
                        sourceText != null -> "Label: $sourceText"
                        else -> "No elimination half-life data available for this agent."
                    },
                    brush = heroBrush
                )

                if (t12 == null && loaded) {
                    Spacer(Modifier.height(20.dp))
                    InfoCard(
                        "Pharmacokinetic estimates unavailable",
                        "A numeric elimination half-life is not available for this agent, so time to steady state, washout and accumulation cannot be estimated. Refer to the current product labelling or a clinical pharmacist."
                    )
                }

                if (t12 != null) {
                    Spacer(Modifier.height(20.dp))
                    ResultCard(
                        "TIME TO STEADY STATE (4–5 t½)",
                        fmtRange(t12 * 4, t12 * 5),
                        "≈94–97% of Css with constant dosing",
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(Modifier.height(12.dp))
                    ResultCard(
                        "WASHOUT / CLEARANCE (≥5 t½)",
                        fmtDuration(t12 * 5),
                        "≈97% eliminated; 5.5 t½ (${fmtDuration(t12 * 5.5)}) for ≈98%",
                        MaterialTheme.colorScheme.tertiaryContainer,
                        MaterialTheme.colorScheme.onTertiaryContainer
                    )

                    Spacer(Modifier.height(20.dp))
                    val targets = listOf(0.90, 0.95, 0.99, 0.999)
                    val targetLabels = listOf("90%", "95%", "99%", "99.9%")
                    SectionCard("Time to target elimination", cardShape) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            targetLabels.forEachIndexed { i, label ->
                                SelectChip(label, targetIdx == i, Modifier.weight(1f)) { targetIdx = i }
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        val x = targets[targetIdx]
                        val n = Math.log(1.0 / (1.0 - x)) / Math.log(2.0)
                        Text(
                            fmtDuration(t12 * n),
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = primary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "${num(n)} × t½ for ${targetLabels[targetIdx]} eliminated",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(Modifier.height(20.dp))
                    SectionCard("Drug remaining vs time (0–6 × t½)", cardShape) {
                        DecayCurve(t12, sliderN)
                        Spacer(Modifier.height(8.dp))
                        Slider(
                            value = sliderN,
                            onValueChange = { sliderN = it },
                            valueRange = 0f..6f
                        )
                        val remaining = Math.pow(0.5, sliderN.toDouble())
                        Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                            StatTile(Modifier.weight(1f).fillMaxHeight(), "Elapsed", fmtDuration(t12 * sliderN))
                            Spacer(Modifier.width(8.dp))
                            StatTile(Modifier.weight(1f).fillMaxHeight(), "Remaining", pct(remaining))
                            Spacer(Modifier.width(8.dp))
                            StatTile(Modifier.weight(1f).fillMaxHeight(), "Eliminated", pct(1.0 - remaining))
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Drag the slider to read the amount remaining at any time point.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(Modifier.height(20.dp))
                    SectionCard("Accumulation at steady state", cardShape) {
                        OutlinedTextField(
                            value = tauInput,
                            onValueChange = { tauInput = it },
                            label = { Text("Dosing interval, τ (hours)") },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(10.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(6, 8, 12, 24).forEach { h ->
                                SelectChip("q${h}h", tauInput == h.toString(), Modifier.weight(1f)) { tauInput = h.toString() }
                            }
                        }
                        Spacer(Modifier.height(14.dp))
                        if (tau == null) {
                            Text(
                                "Enter a dosing interval to estimate drug accumulation and the peak–trough ratio.",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            val r = Math.pow(0.5, tau / t12)
                            val acc = 1.0 / (1.0 - r)
                            Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                                StatTile(Modifier.weight(1f).fillMaxHeight(), "Accumulation (R)", "×${num(acc)}")
                                Spacer(Modifier.width(8.dp))
                                StatTile(Modifier.weight(1f).fillMaxHeight(), "Trough / peak", pct(r))
                                Spacer(Modifier.width(8.dp))
                                StatTile(Modifier.weight(1f).fillMaxHeight(), "τ / t½", num(tau / t12))
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "One-compartment, first-order model with instantaneous input. Absorption phase is ignored.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))
                    SectionCard("Fraction eliminated by t½ elapsed", cardShape) {
                        Row(Modifier.fillMaxWidth().padding(bottom = 6.dp)) {
                            listOf("Half-lives", "Time", "Eliminated").forEach {
                                Text(
                                    it,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        listOf(
                            Triple(1, "50%", 0.5f),
                            Triple(2, "75%", 0.75f),
                            Triple(3, "87.5%", 0.875f),
                            Triple(4, "93.75%", 0.9375f),
                            Triple(5, "96.9%", 0.969f)
                        ).forEach { (n, p, frac) ->
                            Column(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                                Row(Modifier.fillMaxWidth()) {
                                    Text("$n × t½", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 16.sp)
                                    Text(fmtDuration(t12 * n), modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 16.sp)
                                    Text(p, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = primary)
                                }
                                Spacer(Modifier.height(6.dp))
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Box(
                                        Modifier
                                            .fillMaxWidth(frac)
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(primary)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(20.dp))
                    SectionCard("Clinical considerations", cardShape) {
                        JustifiedText(
                            text = "Estimates assume first-order kinetics. Half-life may be prolonged in renal or hepatic impairment and in the elderly; active metabolites, depot formulations and enzyme induction/inhibition can extend the time to clearance. Verify against current product labelling.",
                            fontSizeSp = 13f,
                            lineHeightSp = 20f,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun JustifiedText(text: String, fontSizeSp: Float, lineHeightSp: Float, color: Color) {
    val density = LocalDensity.current
    BoxWithConstraints(Modifier.fillMaxWidth().clearAndSetSemantics { contentDescription = text }) {
        val maxPx = with(density) { maxWidth.toPx() } - 6f
        // Measure with the same text style the words are drawn with, so the
        // measured widths match the rendered widths and no word gets clipped.
        val measurer = rememberTextMeasurer()
        val style = LocalTextStyle.current.merge(
            TextStyle(fontSize = fontSizeSp.sp, lineHeight = lineHeightSp.sp)
        )
        val measureW: (String) -> Float = { w ->
            measurer.measure(w, style = style, maxLines = 1, softWrap = false).size.width.toFloat()
        }
        val spaceW = remember(style) { measureW(" ") }
        val lines = remember(text, maxPx, style) {
            val out = mutableListOf<List<String>>()
            var cur = mutableListOf<String>()
            var used = 0f
            for (word in text.split(" ").filter { it.isNotEmpty() }) {
                val ww = measureW(word)
                val add = if (cur.isEmpty()) ww else spaceW + ww
                if (cur.isNotEmpty() && used + add > maxPx) {
                    out.add(cur)
                    cur = mutableListOf(word)
                    used = ww
                } else {
                    cur.add(word)
                    used += add
                }
            }
            if (cur.isNotEmpty()) out.add(cur)
            out.toList()
        }
        val gap = with(density) { spaceW.toDp() }
        Column(Modifier.fillMaxWidth()) {
            lines.forEachIndexed { i, ln ->
                val last = i == lines.lastIndex
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = if (last || ln.size == 1) Arrangement.spacedBy(gap) else Arrangement.SpaceBetween
                ) {
                    ln.forEach { word ->
                        Text(word, fontSize = fontSizeSp.sp, lineHeight = lineHeightSp.sp, color = color, maxLines = 1, softWrap = false)
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoCard(title: String, body: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("t½", fontSize = 40.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
            Text(title, fontSize = 17.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
            Spacer(Modifier.height(6.dp))
            Text(
                body,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun DrugHeroCard(name: String, drugClass: String?, halfLifeValue: String, note: String, brush: Brush) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(brush)
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(name, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White, textAlign = TextAlign.Center)
            if (!drugClass.isNullOrBlank()) {
                Spacer(Modifier.height(10.dp))
                Surface(shape = RoundedCornerShape(50), color = Color.White.copy(alpha = 0.18f)) {
                    Text(
                        drugClass,
                        fontSize = 13.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp)
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            Box(Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(alpha = 0.25f)))
            Spacer(Modifier.height(16.dp))
            Text(
                "ELIMINATION HALF-LIFE",
                fontSize = 11.sp,
                letterSpacing = 1.5.sp,
                color = Color.White.copy(alpha = 0.75f),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(4.dp))
            Text(halfLifeValue, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White, textAlign = TextAlign.Center)
            if (note.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(note, fontSize = 12.sp, lineHeight = 17.sp, color = Color.White.copy(alpha = 0.8f), textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
private fun ResultCard(title: String, value: String, note: String, container: Color, content: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = container),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 12.sp, letterSpacing = 1.sp, fontWeight = FontWeight.SemiBold, color = content.copy(alpha = 0.8f), textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
            Text(value, fontSize = 30.sp, lineHeight = 36.sp, fontWeight = FontWeight.Bold, color = content, textAlign = TextAlign.Center)
            Spacer(Modifier.height(6.dp))
            Text(note, fontSize = 13.sp, color = content.copy(alpha = 0.8f), textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    shape: RoundedCornerShape,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(Modifier.fillMaxWidth().padding(18.dp)) {
            Text(
                title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(14.dp))
            content()
        }
    }
}

@Composable
private fun SelectChip(label: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(label, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun StatTile(modifier: Modifier, label: String, value: String) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 6.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, maxLines = 1)
        Spacer(Modifier.height(2.dp))
        Text(
            label,
            fontSize = 10.sp,
            lineHeight = 12.sp,
            maxLines = 2,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun DecayCurve(t12: Double, marker: Float) {
    val curve = MaterialTheme.colorScheme.primary
    val grid = MaterialTheme.colorScheme.outlineVariant
    val dotCore = MaterialTheme.colorScheme.surface
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) { progress.animateTo(1f, tween(900)) }

    Canvas(modifier = Modifier.fillMaxWidth().height(190.dp)) {
        val pad = 12f
        val w = size.width - 2 * pad
        val h = size.height - 2 * pad
        val maxN = 6f
        fun px(n: Float) = pad + n / maxN * w
        fun py(f: Float) = pad + h - f * h
        for (i in 0..6) {
            drawLine(color = grid, start = Offset(px(i.toFloat()), pad), end = Offset(px(i.toFloat()), pad + h), strokeWidth = 1f)
        }
        listOf(0f, 0.25f, 0.5f, 0.75f, 1f).forEach { f ->
            drawLine(
                color = grid,
                start = Offset(pad, py(f)),
                end = Offset(pad + w, py(f)),
                strokeWidth = if (f == 0.5f) 2f else 1f,
                pathEffect = if (f == 0.5f) PathEffect.dashPathEffect(floatArrayOf(12f, 10f)) else null
            )
        }
        val p = progress.value
        val steps = (120 * p).toInt()
        val line = Path()
        val area = Path()
        for (s in 0..steps) {
            val n = s / 120f * maxN
            val f = Math.pow(0.5, n.toDouble()).toFloat()
            if (s == 0) {
                line.moveTo(px(n), py(f))
                area.moveTo(px(n), py(f))
            } else {
                line.lineTo(px(n), py(f))
                area.lineTo(px(n), py(f))
            }
        }
        val nLast = steps / 120f * maxN
        area.lineTo(px(nLast), py(0f))
        area.lineTo(px(0f), py(0f))
        area.close()
        drawPath(
            area,
            Brush.verticalGradient(
                listOf(curve.copy(alpha = 0.28f), curve.copy(alpha = 0.02f)),
                startY = pad,
                endY = pad + h
            )
        )
        drawPath(line, curve, style = Stroke(width = 6f, cap = StrokeCap.Round))
        for (i in 0..6) {
            if (i <= p * maxN) {
                val f = Math.pow(0.5, i.toDouble()).toFloat()
                drawCircle(curve, 10f, Offset(px(i.toFloat()), py(f)))
                drawCircle(dotCore, 4.5f, Offset(px(i.toFloat()), py(f)))
            }
        }
        if (p >= 1f) {
            val mf = Math.pow(0.5, marker.toDouble()).toFloat()
            drawLine(
                color = curve.copy(alpha = 0.6f),
                start = Offset(px(marker), pad),
                end = Offset(px(marker), pad + h),
                strokeWidth = 2f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f))
            )
            drawCircle(curve.copy(alpha = 0.25f), 20f, Offset(px(marker), py(mf)))
            drawCircle(curve, 12f, Offset(px(marker), py(mf)))
            drawCircle(dotCore, 5f, Offset(px(marker), py(mf)))
        }
    }
    Spacer(Modifier.height(6.dp))
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        listOf(0, 1, 2, 3, 4, 5, 6).forEach { i ->
            Text(
                if (i == 0) "0" else fmtDuration(t12 * i),
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
    Spacer(Modifier.height(4.dp))
    Text(
        "Dashed horizontal line = 50% remaining (one half-life)",
        fontSize = 11.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}
