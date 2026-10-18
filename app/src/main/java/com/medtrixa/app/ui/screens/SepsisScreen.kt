package com.medtrixa.app.ui.screens

import android.os.SystemClock
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medtrixa.app.ui.components.PremiumBackButton
import kotlinx.coroutines.delay
import java.util.Locale

private data class SepsisStep(
    val id: Int,
    val title: String,
    val note: String,
    val strength: String,
    val deadlineSec: Int?
)

private fun hms(totalSeconds: Int): String {
    val s = if (totalSeconds < 0) 0 else totalSeconds
    val h = s / 3600
    val m = (s % 3600) / 60
    val sec = s % 60
    return if (h > 0) String.format(Locale.US, "%d:%02d:%02d", h, m, sec)
    else String.format(Locale.US, "%02d:%02d", m, sec)
}

private fun String.toNumS(): Double? = trim().replace(',', '.').toDoubleOrNull()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SepsisScreen(onBackClick: () -> Unit) {
    var t0 by rememberSaveable { mutableStateOf(0L) }
    var now by remember { mutableStateOf(SystemClock.elapsedRealtime()) }
    var shockMode by rememberSaveable { mutableStateOf(true) }
    var doneStr by rememberSaveable { mutableStateOf("") }
    var weight by rememberSaveable { mutableStateOf("") }
    var age65 by rememberSaveable { mutableStateOf(false) }
    var confirmReset by remember { mutableStateOf(false) }

    val clipboard = LocalClipboardManager.current
    val view = LocalView.current
    val started = t0 > 0L

    SideEffect { view.keepScreenOn = started }
    DisposableEffect(Unit) { onDispose { view.keepScreenOn = false } }

    LaunchedEffect(started) {
        while (started) {
            now = SystemClock.elapsedRealtime()
            delay(500)
        }
    }

    val elapsed = if (started) ((now - t0) / 1000).toInt() else 0

    val doneMap: Map<Int, Int> = remember(doneStr) {
        val m = mutableMapOf<Int, Int>()
        doneStr.split(";").forEach { part ->
            val bits = part.split(":")
            if (bits.size == 2) {
                val a = bits[0].toIntOrNull()
                val b = bits[1].toIntOrNull()
                if (a != null && b != null) m[a] = b
            }
        }
        m
    }

    fun toggle(id: Int) {
        val m = doneMap.toMutableMap()
        if (m.containsKey(id)) m.remove(id) else m[id] = elapsed
        doneStr = m.entries.joinToString(";") { it.key.toString() + ":" + it.value.toString() }
    }

    val steps = listOf(
        SepsisStep(
            0, "Blood cultures",
            "Collect as soon as possible, ideally before antimicrobial therapy.",
            "Strong", null
        ),
        SepsisStep(
            1, "Measure lactate",
            "Suggested for possible, probable or definite sepsis or septic shock. If raised, repeat to guide resuscitation.",
            "Conditional", null
        ),
        if (shockMode) SepsisStep(
            2, "Antimicrobial therapy",
            "Give immediately, ideally within 1 hour of recognition (septic shock, or probable or definite sepsis without shock).",
            "Strong", 3600
        ) else SepsisStep(
            2, "Antimicrobial therapy",
            "Possible sepsis without shock: rapid investigation, then antimicrobials within 3 hours of first suspicion if concern for infection persists.",
            "Conditional", 10800
        ),
        SepsisStep(
            3, "IV crystalloid, at least 30 mL/kg",
            "For sepsis-induced hypoperfusion or septic shock: within the first 3 hours. Reassess often to avoid under- or over-resuscitation.",
            "Conditional", 10800
        ),
        SepsisStep(
            4, "Vasopressor if hypotension persists",
            "Norepinephrine first-line. May be started peripherally without waiting for central access.",
            "Strong", null
        ),
        SepsisStep(
            5, "Source control, if a source needs it",
            "Rapidly look for a source that needs emergent control. Early control, ideally within 6 hours.",
            "Conditional", 21600
        ),
        SepsisStep(
            6, "ICU admission, if ICU care is needed",
            "Admit within 6 hours.",
            "Conditional", 21600
        )
    )

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
                    title = {
                        Text("Sepsis", fontSize = 20.sp, lineHeight = 26.sp, fontWeight = FontWeight.SemiBold)
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
                            .clip(RoundedCornerShape(24.dp))
                            .background(primary.copy(alpha = 0.12f))
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Time since recognition (time zero)",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            if (started) hms(elapsed) else "00:00",
                            fontSize = 50.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = {
                                    val t = SystemClock.elapsedRealtime()
                                    t0 = t
                                    now = t
                                },
                                enabled = !started,
                                shape = RoundedCornerShape(16.dp)
                            ) { Text("Start clock") }
                            OutlinedButton(
                                onClick = { confirmReset = true },
                                shape = RoundedCornerShape(16.dp)
                            ) { Text("Reset") }
                        }
                    }
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Presentation", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = shockMode,
                                onClick = { shockMode = true },
                                label = { Text("Shock, or probable/definite sepsis", fontSize = 12.sp) }
                            )
                        }
                        FilterChip(
                            selected = !shockMode,
                            onClick = { shockMode = false },
                            label = { Text("Possible sepsis, no shock", fontSize = 12.sp) }
                        )
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Checklist", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = primary)
                            steps.forEach { step ->
                                val doneAt = doneMap[step.id]
                                val isDone = doneAt != null
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { toggle(step.id) }
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Checkbox(checked = isDone, onCheckedChange = { toggle(step.id) })
                                    Column(modifier = Modifier.padding(top = 10.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(step.title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                            Spacer(Modifier.width(8.dp))
                                            Text(
                                                step.strength,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = primary
                                            )
                                        }
                                        Text(
                                            step.note,
                                            fontSize = 12.sp,
                                            lineHeight = 17.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        val dl = step.deadlineSec
                                        val status: String
                                        var late = false
                                        if (doneAt != null) {
                                            if (dl != null && doneAt > dl) late = true
                                            status = "Done at " + hms(doneAt) + if (late) " (after target)" else ""
                                        } else if (dl != null && started) {
                                            val rem = dl - elapsed
                                            if (rem >= 0) status = "Due in " + hms(rem)
                                            else {
                                                late = true
                                                status = "Overdue by " + hms(-rem)
                                            }
                                        } else if (dl != null) {
                                            status = "Target: within " + (dl / 3600) + " h"
                                        } else status = ""
                                        if (status.isNotEmpty()) {
                                            Text(
                                                status,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = if (late) MaterialTheme.colorScheme.error else primary
                                            )
                                        }
                                    }
                                }
                            }
                            TextButton(onClick = {
                                val sb = StringBuilder("Sepsis timeline (SSC 2026)")
                                steps.forEach { st ->
                                    val d = doneMap[st.id]
                                    sb.append("\n").append(if (d != null) hms(d) else "--:--").append("  ").append(st.title)
                                }
                                clipboard.setText(AnnotatedString(sb.toString()))
                            }) { Text("Copy summary") }
                        }
                    }
                }

                item {
                    val w = weight.toNumS()
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Fluid volume", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = primary)
                            OutlinedTextField(
                                value = weight,
                                onValueChange = { weight = it },
                                label = { Text("Weight (kg)") },
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.fillMaxWidth()
                            )
                            if (w != null && w > 0.0) {
                                val ml = w * 30.0
                                Text(
                                    String.format(Locale.US, "30 mL/kg = %.0f mL (%.2f L)", ml, ml / 1000.0),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                "Use actual body weight, or adjusted or ideal body weight if BMI is above 30 kg/m\u00B2. " +
                                    "Individualize the volume and reassess frequently.",
                                fontSize = 12.sp,
                                lineHeight = 17.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Blood pressure target", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = primary)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Switch(checked = age65, onCheckedChange = { age65 = it })
                                Spacer(Modifier.width(10.dp))
                                Text("Age 65 or older", fontSize = 13.sp)
                            }
                            Text(
                                if (age65) "Initial MAP range 60 to 65 mm Hg is suggested (conditional)."
                                else "Initial MAP target 65 mm Hg (strong recommendation).",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Holding MAP at exactly 65 is not practical: titrate vasopressors to stay within about 5 mm Hg of the target.",
                                fontSize = 12.sp,
                                lineHeight = 17.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                item {
                    SepsisInfoCard(
                        "Septic shock: vasopressors and inotropes",
                        listOf(
                            "Norepinephrine is the first-line vasopressor (strong).",
                            "Escalating norepinephrine dose: add vasopressin (conditional).",
                            "MAP still inadequate on norepinephrine plus vasopressin: add epinephrine (conditional).",
                            "Cardiac dysfunction with persistent hypoperfusion despite adequate volume and pressure: add dobutamine to norepinephrine, or use epinephrine alone (conditional).",
                            "IV corticosteroids are suggested for septic shock (conditional). Choose agent and dose per local protocol.",
                            "Invasive arterial pressure monitoring is advised with escalating or multiple vasopressors, frequent arterial sampling, or inconsistent cuff readings."
                        )
                    )
                }

                item {
                    SepsisInfoCard(
                        "Fluids",
                        listOf(
                            "Crystalloids are first-line (strong). Balanced crystalloids are suggested over 0.9% saline (conditional). Use 0.9% saline with traumatic brain injury.",
                            "Do not use starches (strong). Gelatin is suggested against.",
                            "Crystalloids alone are suggested over adding albumin, unless large crystalloid volumes have been given or there is cirrhosis (conditional).",
                            "After the first bolus, guide more fluid with dynamic measures, serial lactate and capillary refill time rather than examination alone."
                        )
                    )
                }

                item {
                    SepsisInfoCard(
                        "Antimicrobial approach",
                        listOf(
                            "Give beta-lactams as a prolonged infusion for maintenance, after an initial loading dose (strong).",
                            "Cover a specific multidrug-resistant pathogen only when the risk is high: colonization or prior infection with it, prolonged broad-spectrum antibiotics, or prolonged stay in a unit where it is common.",
                            "No routine empiric antifungal therapy. Add anaerobic cover only with risk factors: intra-abdominal or deep gynecologic source, necrotizing soft-tissue infection, head and neck infection, or CNS abscess or empyema.",
                            "Decide whether to start antimicrobials by clinical evaluation alone, not procalcitonin.",
                            "De-escalate once culture and susceptibility results allow (strong). With adequate source control, shorter courses are suggested."
                        )
                    )
                }

                item {
                    SepsisInfoCard(
                        "Supportive care",
                        listOf(
                            "Start insulin when glucose is 180 mg/dL (10 mmol/L) or higher (strong).",
                            "Pharmacologic VTE prophylaxis unless contraindicated, with low molecular weight heparin preferred over unfractionated heparin (strong).",
                            "Restrictive transfusion strategy (strong).",
                            "Stress ulcer prophylaxis with a proton-pump inhibitor if GI bleeding risk factors are present (conditional).",
                            "Sodium bicarbonate is not suggested for lactic acidemia alone. It is suggested for pH 7.2 or below with acute kidney injury (AKIN 2 or 3).",
                            "Antipyretics are not suggested to improve outcomes (symptom control is separate).",
                            "ARDS: tidal volume 6 mL/kg and plateau pressure limit of 30 cm H\u2082O (strong).",
                            "Start enteral nutrition early, within 72 hours (conditional)."
                        )
                    )
                }

                item {
                    SepsisInfoCard(
                        "Recognition",
                        listOf(
                            "Sepsis is a clinical diagnosis. Do not rule it in or out with a single biomarker or test.",
                            "In hospital, screen with NEWS, NEWS2, MEWS or SIRS rather than qSOFA alone (strong)."
                        )
                    )
                }

                item {
                    Text(
                        "Source: Surviving Sepsis Campaign International Guidelines for Management of Sepsis and Septic Shock 2026 " +
                            "(Prescott et al., Crit Care Med 2026; published 23 March 2026). Each item shows the recommendation " +
                            "strength given in the guideline. Decision support only; follow your local protocol.",
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }
        }
    }

    if (confirmReset) {
        AlertDialog(
            onDismissRequest = { confirmReset = false },
            title = { Text("Reset clock and checklist?") },
            text = { Text("This clears the clock and all ticked items.") },
            confirmButton = {
                TextButton(onClick = {
                    t0 = 0L
                    doneStr = ""
                    confirmReset = false
                }) { Text("Reset") }
            },
            dismissButton = { TextButton(onClick = { confirmReset = false }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun SepsisInfoCard(title: String, lines: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
            lines.forEach { line ->
                Text("\u2022 $line", fontSize = 13.sp, lineHeight = 19.sp)
            }
        }
    }
}
