package com.medtrixa.app.ui.screens

import android.os.SystemClock
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medtrixa.app.ui.components.PremiumBackButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

// Sources: 2026 AHA/ASA Guideline for the Early Management of Patients With Acute Ischemic Stroke
// (Prabhakaran et al., Stroke 2026, doi:10.1161/STR.0000000000000513), read through published summaries;
// 2019 AHA/ASA update (Powers et al., Stroke 2019;50:e344-e418) for time targets, glucose and alteplase regimen;
// BE-FAST (Aroor et al., Stroke 2017;48:479-481). Wording is our own. Not affiliated with or endorsed by the AHA/ASA.
const val STROKE_SOURCE = "AHA/ASA 2026"
private const val STROKE_CHECKED = "checked Oct 2026"

private data class StrokeStep(val title: String, val note: String, val together: Boolean)

private val strokeSteps = listOf(
    StrokeStep("Activate the stroke code", "Pre-notify the hospital or call the stroke team. Note the last known well time.", true),
    StrokeStep("Support the basics", "Airway, breathing and circulation first.", false),
    StrokeStep("Check blood glucose now", "Low glucose (below 60 mg/dL, 3.3 mmol/L) can mimic stroke and must be treated. It is the only test that has to come before thrombolysis.", true),
    StrokeStep("Brain imaging", "Non-contrast CT or MRI as soon as possible after arrival, to exclude haemorrhage. Add CT angiography of head and neck to look for a large vessel occlusion. Do not delay thrombolysis for advanced imaging.", true),
    StrokeStep("Score severity", "Use the NIHSS. Treat any disabling deficit: there is no minimum NIHSS for thrombolysis.", false),
    StrokeStep("Bring blood pressure down if needed", "Below 185/110 mmHg before thrombolysis.", false),
    StrokeStep("Thrombolysis if eligible", "Within 4.5 hours of last known well: tenecteplase or alteplase. Never before haemorrhage is excluded.", false),
    StrokeStep("Large vessel occlusion: call the thrombectomy team", "Do not delay thrombolysis while arranging transfer. Keep door-in-door-out time short.", false),
    StrokeStep("After treatment", "Keep blood pressure at or below 180/105 mmHg for 24 hours after thrombolysis. After successful thrombectomy of an anterior occlusion, do not push systolic pressure below 140 mmHg for the first 72 hours.", false),
    StrokeStep("Monitor and admit", "Stroke unit or critical care, with frequent neurological and blood pressure checks.", false)
)

private fun stkMmss(totalSeconds: Int): String {
    val s = if (totalSeconds < 0) 0 else totalSeconds
    return String.format(Locale.US, "%02d:%02d", s / 60, s % 60)
}

private fun stkHm(totalMinutes: Int): String {
    val m = if (totalMinutes < 0) 0 else totalMinutes
    return String.format(Locale.US, "%d h %02d min", m / 60, m % 60)
}

private fun stkFmt1(v: Double): String = String.format(Locale.US, "%.1f", v)

@Composable
private fun StrokeCard(title: String, accent: Color, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold, color = accent)
            Spacer(Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun StrokeBullet(text: String) {
    Row(Modifier.padding(vertical = 2.dp)) {
        Text("•", fontSize = 13.sp, modifier = Modifier.width(14.dp))
        Text(
            text,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StrokeScreen(onBackClick: () -> Unit, onNihssClick: () -> Unit = {}) {
    var hoursText by rememberSaveable { mutableStateOf("") }
    var minutesText by rememberSaveable { mutableStateOf("") }
    var weight by rememberSaveable { mutableStateOf("") }
    var doorStart by rememberSaveable { mutableStateOf(0L) }
    var checked by rememberSaveable { mutableStateOf(0) }
    var now by remember { mutableStateOf(SystemClock.elapsedRealtime()) }
    var confirmReset by remember { mutableStateOf(false) }
    var nihssRaw by rememberSaveable { mutableStateOf("") }
    var nihssOpen by rememberSaveable { mutableStateOf(false) }
    val clipboard = LocalClipboardManager.current

    val view = LocalView.current
    val scope = rememberCoroutineScope()
    val weightRequester = remember { BringIntoViewRequester() }
    val doorRunning = doorStart > 0L

    SideEffect { view.keepScreenOn = doorRunning }
    DisposableEffect(Unit) { onDispose { view.keepScreenOn = false } }
    LaunchedEffect(doorRunning) {
        while (doorRunning) {
            now = SystemClock.elapsedRealtime()
            delay(500)
        }
    }

    val primary = MaterialTheme.colorScheme.primary
    val danger = MaterialTheme.colorScheme.error
    val caution = MaterialTheme.colorScheme.tertiary
    val muted = MaterialTheme.colorScheme.onSurfaceVariant

    // NIHSS: 15 entries, -1 = not scored, -2 = untestable
    val nihss: List<Int> = remember(nihssRaw) {
        val parts = nihssRaw.split(",")
        List(15) { i -> parts.getOrNull(i)?.toIntOrNull() ?: -1 }
    }
    val nihssComa = nihss[0] == 3
    val nihssTotal = (0..14).fold(0) { acc, i ->
        acc + when {
            i == 11 && nihssComa -> 2
            i == 12 && nihssComa -> 3
            nihss[i] >= 0 -> nihss[i]
            else -> 0
        }
    }
    val nihssAnswered = (0..14).count { i -> ((i == 11 || i == 12) && nihssComa) || nihss[i] != -1 }
    val nihssUntestable = (0..14).count { nihss[it] == -2 }
    val nihssSummary =
        if (nihssAnswered == 0) "Open the full NIHSS score screen."
        else ("Total " + nihssTotal + " · " + nihssBand(nihssTotal) +
            (if (nihssAnswered < 15) " (partial: $nihssAnswered of 15 entered)" else ""))
    val nihssCopy = "NIHSS " + nihssTotal + " (" + nihssBand(nihssTotal) + ")" +
        (if (nihssAnswered < 15) ", incomplete: $nihssAnswered of 15 items entered" else "") + "\n" +
        nihssEntries.indices.joinToString(", ") { i ->
            nihssEntries[i].code + "=" + when {
                i == 11 && nihssComa -> "2*"
                i == 12 && nihssComa -> "3*"
                nihss[i] == -1 -> "-"
                nihss[i] == -2 -> "UN"
                else -> nihss[i].toString()
            }
        }
    val nihssSet: (Int, Int) -> Unit = { i, v ->
        val l = nihss.toMutableList()
        l[i] = v
        nihssRaw = l.joinToString(",")
    }

    // Last known well
    val hasInput = hoursText.isNotBlank() || minutesText.isNotBlank()
    val hv = if (hoursText.isBlank()) 0 else hoursText.trim().toIntOrNull()
    val mv = if (minutesText.isBlank()) 0 else minutesText.trim().toIntOrNull()
    val lkw: Int? =
        if (hasInput && hv != null && mv != null && hv in 0..999 && mv in 0..59) hv * 60 + mv else null

    // Door clock
    val doorSec = if (doorRunning) maxOf(0, ((now - doorStart) / 1000).toInt()) else 0
    val (doorMsg, doorColor) = when {
        !doorRunning -> "Start when the patient arrives." to muted
        doorSec < 1200 -> "Brain imaging target: within 20 min (${stkMmss(1200 - doorSec)} left)." to primary
        doorSec < 3600 -> "Past 20 min. Thrombolysis target: within 60 min (${stkMmss(3600 - doorSec)} left)." to caution
        else -> "Past 60 min. Treat as fast as possible and review the delay." to danger
    }

    // Thrombolytic doses (adults)
    val w = weight.trim().replace(',', '.').toDoubleOrNull()
    val tnk: Double? = if (w != null && w in 20.0..250.0) minOf(w * 0.25, 25.0) else null
    val alt: Double? = if (w != null && w in 20.0..250.0) minOf(w * 0.9, 90.0) else null

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(top = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PremiumBackButton(onClick = onBackClick, edgeInset = 16.dp)
                Text(
                    "Stroke",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).consumeWindowInsets(padding).imePadding().fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Text("$STROKE_SOURCE · $STROKE_CHECKED", fontSize = 12.sp, color = muted)
            }

            item {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(listOf(primary.copy(alpha = 0.16f), primary.copy(alpha = 0.03f))),
                            RoundedCornerShape(24.dp)
                        )
                        .padding(horizontal = 20.dp, vertical = 18.dp)
                ) {
                    Column {
                        Text("Time is brain", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = primary)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Note the last known well time. Image the brain before any thrombolytic. " +
                                "Treat any disabling deficit, with no minimum NIHSS and without waiting for advanced imaging.",
                            fontSize = 13.sp
                        )
                    }
                }
            }

            item {
                StrokeCard("Recognise stroke (BE-FAST)", primary) {
                    StrokeBullet("Balance: sudden loss of balance or coordination")
                    StrokeBullet("Eyes: sudden change or loss of vision")
                    StrokeBullet("Face: one side drooping or numb")
                    StrokeBullet("Arm: weakness of an arm or leg, especially on one side")
                    StrokeBullet("Speech: slurred, confused or absent speech")
                    StrokeBullet("Time: note when it started and act immediately")
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "About 14% of ischaemic strokes had no FAST signs at presentation (Aroor 2017), which is why balance and vision are added. " +
                            "Low blood glucose can mimic stroke. This protocol covers ischaemic stroke; imaging must exclude haemorrhage first.",
                        fontSize = 12.sp,
                        color = muted
                    )
                }
            }

            item {
                StrokeCard("Last known well", primary) {
                    Text(
                        "Use the time the patient was last known to be well. For a wake-up stroke, that is when they went to sleep.",
                        fontSize = 12.sp,
                        color = muted
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = hoursText,
                            onValueChange = { hoursText = it },
                            label = { Text("Hours ago") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = minutesText,
                            onValueChange = { minutesText = it },
                            label = { Text("Minutes") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    if (hasInput && lkw == null) {
                        Text("Enter whole numbers: hours 0 to 999, minutes 0 to 59.", fontSize = 13.sp, color = danger)
                    } else if (lkw != null) {
                        val t = lkw
                        val statusTitle: String
                        val statusBody: String
                        val statusColor: Color
                        when {
                            t <= 270 -> {
                                statusTitle = "Within the 4.5 h thrombolysis window"
                                statusBody = "${stkHm(270 - t)} left. Move fast: glucose, imaging, decision."
                                statusColor = primary
                            }
                            t <= 1440 -> {
                                statusTitle = "Beyond 4.5 h"
                                statusBody = "Thrombolysis can still be considered only in selected large vessel occlusion patients with salvageable tissue on advanced imaging who cannot have thrombectomy (class 2b). " +
                                    "Thrombectomy may apply up to 24 h in selected patients. Involve the stroke team now."
                                statusColor = caution
                            }
                            else -> {
                                statusTitle = "More than 24 h since last known well"
                                statusBody = "Treatment decisions rest with the stroke team."
                                statusColor = danger
                            }
                        }
                        Text(stkHm(t) + " since last known well", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { minOf(t / 270f, 1f) },
                            modifier = Modifier.fillMaxWidth().height(8.dp),
                            color = statusColor
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(statusTitle, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = statusColor)
                        Text(statusBody, fontSize = 12.sp, color = muted)
                    }
                }
            }

            item {
                StrokeCard("Door clock", primary) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                if (doorRunning) stkMmss(doorSec) else "--:--",
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text("since arrival", fontSize = 12.sp, color = muted)
                        }
                        if (!doorRunning) {
                            Button(onClick = {
                                doorStart = SystemClock.elapsedRealtime()
                                now = doorStart
                            }) { Text("Start clock") }
                        } else {
                            OutlinedButton(onClick = { confirmReset = true }) { Text("Reset") }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(doorMsg, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = doorColor)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Targets from the 2019 AHA/ASA guideline: imaging within 20 minutes and thrombolysis within 60 minutes of arrival. Faster is better.",
                        fontSize = 11.sp,
                        color = muted
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onNihssClick() },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("NIHSS score", fontWeight = FontWeight.Bold, color = primary)
                            Text(nihssSummary, fontSize = 12.sp, color = muted)
                        }
                        Text(if (nihssOpen) "Hide" else "Open", fontWeight = FontWeight.SemiBold, color = primary)
                    }
                }
            }

            if (nihssOpen) {
                item {
                    Text(
                        "Scoring aid based on the NINDS NIH Stroke Scale. The prompts are condensed. Items 9 and 10 need the official picture, " +
                            "naming and sentence sheets, and the exam should follow the NINDS form and certified training.",
                        fontSize = 11.sp,
                        color = muted
                    )
                }
                items(nihssEntries.size) { idx ->
                    NihssEntryCard(
                        entry = nihssEntries[idx],
                        selected = nihss[idx],
                        autoScore = if (nihssComa && idx == 11) 2 else if (nihssComa && idx == 12) 3 else null,
                        onSelect = { v -> nihssSet(idx, v) }
                    )
                }
                item {
                    StrokeCard("NIHSS total", primary) {
                        Text("$nihssTotal", fontSize = 40.sp, fontWeight = FontWeight.Bold, color = primary)
                        Text(
                            nihssBand(nihssTotal) + (if (nihssAnswered < 15) " (incomplete: $nihssAnswered of 15 entered)" else ""),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (nihssUntestable > 0) {
                            Text("$nihssUntestable untestable item(s) are not counted.", fontSize = 12.sp, color = muted)
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = { clipboard.setText(AnnotatedString(nihssCopy)) },
                                enabled = nihssAnswered > 0
                            ) { Text("Copy score") }
                            OutlinedButton(onClick = { nihssRaw = "" }) { Text("Clear") }
                        }
                    }
                }
            }

            item {
                StrokeCard("Thrombolytic dose (adults)", danger) {
                    OutlinedTextField(
                        value = weight,
                        onValueChange = { weight = it },
                        label = { Text("Weight (kg)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier
                            .fillMaxWidth()
                            .bringIntoViewRequester(weightRequester)
                            .onFocusEvent { st ->
                                if (st.isFocused) scope.launch {
                                    delay(350)
                                    weightRequester.bringIntoView()
                                }
                            }
                    )
                    Spacer(Modifier.height(10.dp))
                    if (w != null && tnk == null) {
                        Text("Enter a weight between 20 and 250 kg.", fontSize = 13.sp, color = danger)
                    } else if (tnk != null && alt != null) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Column(Modifier.weight(1f)) {
                                Text("Tenecteplase", fontSize = 12.sp, color = muted)
                                Text("${stkFmt1(tnk)} mg", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = primary)
                                Text("single IV bolus", fontSize = 11.sp, color = muted)
                            }
                            Column(Modifier.weight(1f)) {
                                Text("Alteplase", fontSize = 12.sp, color = muted)
                                Text("${stkFmt1(alt)} mg", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = primary)
                                Text("total dose", fontSize = 11.sp, color = muted)
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Alteplase: ${stkFmt1(alt * 0.1)} mg bolus over 1 minute, then ${stkFmt1(alt * 0.9)} mg over 60 minutes.",
                            fontSize = 13.sp
                        )
                    } else {
                        Text("Enter the weight to calculate the dose.", fontSize = 13.sp, color = muted)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Tenecteplase 0.25 mg/kg (maximum 25 mg). Alteplase 0.9 mg/kg (maximum 90 mg), 10% as a bolus. " +
                            "Either is recommended within 4.5 hours in eligible adults. The stroke team and pharmacy confirm eligibility and the final dose. " +
                            "Not for children.",
                        fontSize = 11.sp,
                        color = muted
                    )
                }
            }

            item {
                StrokeCard("Initial actions", primary) {
                    strokeSteps.forEachIndexed { idx, st ->
                        val on = (checked and (1 shl idx)) != 0
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clickable { checked = checked xor (1 shl idx) }
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                Modifier
                                    .size(28.dp)
                                    .background(if (on) primary else primary.copy(alpha = 0.12f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "${idx + 1}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (on) MaterialTheme.colorScheme.onPrimary else primary
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(st.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, modifier = Modifier.weight(1f, fill = false))
                                    if (st.together) {
                                        Spacer(Modifier.width(8.dp))
                                        Text("IN PARALLEL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = danger)
                                    }
                                }
                                Text(st.note, fontSize = 12.sp, color = muted)
                            }
                        }
                    }
                    if (checked != 0) {
                        TextButton(onClick = { checked = 0 }) { Text("Clear ticks") }
                    }
                }
            }

            item {
                StrokeCard("Before thrombolysis", danger) {
                    StrokeBullet("Haemorrhage excluded on brain imaging.")
                    StrokeBullet("Blood glucose checked and any hypoglycaemia treated.")
                    StrokeBullet("Blood pressure below 185/110 mmHg.")
                    StrokeBullet("Other exclusions, such as bleeding risk, recent surgery or trauma and anticoagulant use, are in the guideline's eligibility table. Use your local checklist; none is listed here.")
                }
            }

            item {
                StrokeCard("Blood pressure", primary) {
                    StrokeBullet("Before thrombolysis: below 185/110 mmHg. The same ceiling is reasonable for thrombectomy candidates who are not receiving thrombolysis.")
                    StrokeBullet("First 24 hours after thrombolysis: at or below 180/105 mmHg.")
                    StrokeBullet("During and for 24 hours after thrombectomy: a ceiling of 180/105 mmHg is reasonable.")
                    StrokeBullet("After successful thrombectomy of an anterior circulation occlusion: intensive lowering of systolic pressure below 140 mmHg is harmful in the first 72 hours.")
                }
            }

            item {
                StrokeCard("Blood glucose", primary) {
                    StrokeBullet("Check it first: low and high glucose can both mimic stroke.")
                    StrokeBullet("Treat hypoglycaemia (below 60 mg/dL, 3.3 mmol/L).")
                    StrokeBullet("Intensive glucose lowering (80 to 130 mg/dL) gives no benefit and is not recommended (class 3).")
                }
            }

            item {
                StrokeCard("Thrombectomy windows", primary) {
                    StrokeBullet("Standard care for large vessel occlusion, decided by the stroke or neurointerventional team on imaging.")
                    StrokeBullet("Anterior circulation (ICA or M1): within 6 hours, including large cores (ASPECTS 3 to 10).")
                    StrokeBullet("Anterior circulation, 6 to 24 hours: selected patients with a significant deficit (NIHSS 6 or more), good pre-stroke function and favourable imaging.")
                    StrokeBullet("Basilar artery occlusion: within 24 hours when NIHSS is 10 or more with limited early damage (class 1).")
                    Spacer(Modifier.height(4.dp))
                    Text("Summarised from the 2026 guideline. Exact selection criteria are in the full text.", fontSize = 11.sp, color = muted)
                }
            }

            item {
                Text(
                    "Decision support only. Recommendations follow the 2026 AHA/ASA guideline (read through published summaries) and the 2019 " +
                        "AHA/ASA update; always follow your local stroke protocol. Wording is our own. Not affiliated with or endorsed by the AHA/ASA.",
                    fontSize = 11.sp,
                    color = muted
                )
            }
        }
    }

    if (confirmReset) {
        AlertDialog(
            onDismissRequest = { confirmReset = false },
            title = { Text("Reset door clock?") },
            text = { Text("This clears the clock.") },
            confirmButton = {
                TextButton(onClick = {
                    doorStart = 0L
                    confirmReset = false
                }) { Text("Reset") }
            },
            dismissButton = { TextButton(onClick = { confirmReset = false }) { Text("Cancel") } }
        )
    }
}


internal data class NihssEntry(
    val code: String,
    val title: String,
    val hint: String,
    val options: List<Pair<Int, String>>
)

internal fun nihssBand(total: Int): String = when {
    total == 0 -> "No stroke symptoms"
    total <= 4 -> "Minor stroke"
    total <= 15 -> "Moderate stroke"
    total <= 20 -> "Moderate to severe stroke"
    else -> "Severe stroke"
}

private val nihssArmOptions = listOf(
    0 to "No drift: holds the position for the full 10 seconds",
    1 to "Drift: drifts down before 10 seconds but does not hit the bed",
    2 to "Some effort against gravity: cannot hold the position, drifts to the bed",
    3 to "No effort against gravity: the limb falls",
    4 to "No movement",
    -2 to "Untestable: amputation or joint fusion"
)

private val nihssLegOptions = listOf(
    0 to "No drift: holds 30 degrees for the full 5 seconds",
    1 to "Drift: falls by the end of 5 seconds but does not hit the bed",
    2 to "Some effort against gravity: falls to the bed by 5 seconds",
    3 to "No effort against gravity: falls to the bed immediately",
    4 to "No movement",
    -2 to "Untestable: amputation or joint fusion"
)

internal val nihssEntries = listOf(
    NihssEntry(
        "1a", "1a. Level of consciousness",
        "Choose a response even if the exam is limited (intubation, language barrier). Score 3 only if there is no movement apart from reflex posturing to a painful stimulus.",
        listOf(
            0 to "Alert, keenly responsive",
            1 to "Not alert, but arouses to minor stimulation to obey or answer",
            2 to "Not alert: needs repeated stimulation, or strong or painful stimulation to move",
            3 to "Reflex responses only, or totally unresponsive and flaccid"
        )
    ),
    NihssEntry(
        "1b", "1b. LOC questions",
        "Ask the month and the patient's age. Only the first answer counts, with no partial credit and no cues. Aphasic or stuporous patients who do not understand score 2. Patients who cannot speak for other reasons (intubation, severe dysarthria, language barrier) score 1.",
        listOf(
            0 to "Answers both correctly",
            1 to "Answers one correctly",
            2 to "Answers neither correctly"
        )
    ),
    NihssEntry(
        "1c", "1c. LOC commands",
        "Ask the patient to open and close the eyes, then grip and release the non-paretic hand. Credit an unequivocal attempt. Demonstrate if there is no response. Only the first attempt counts.",
        listOf(
            0 to "Performs both tasks correctly",
            1 to "Performs one task correctly",
            2 to "Performs neither task correctly"
        )
    ),
    NihssEntry(
        "2", "2. Best gaze",
        "Horizontal eye movements only, voluntary or reflexive. A conjugate deviation that can be overcome scores 1. An isolated cranial nerve III, IV or VI palsy scores 1.",
        listOf(
            0 to "Normal",
            1 to "Partial gaze palsy: abnormal in one or both eyes, no forced deviation",
            2 to "Forced deviation, or total gaze paresis not overcome by the oculocephalic manoeuvre"
        )
    ),
    NihssEntry(
        "3", "3. Visual fields",
        "Test upper and lower quadrants by confrontation (finger counting or visual threat). Score 1 only for clear asymmetry. Blind from any cause scores 3. Do double simultaneous stimulation here; extinction feeds item 11.",
        listOf(
            0 to "No visual loss",
            1 to "Partial hemianopia",
            2 to "Complete hemianopia",
            3 to "Bilateral hemianopia, including cortical blindness"
        )
    ),
    NihssEntry(
        "4", "4. Facial palsy",
        "Ask the patient to show teeth, raise eyebrows and close eyes. In poorly responsive patients, score the grimace to a noxious stimulus. Remove tape or bandages if possible.",
        listOf(
            0 to "Normal, symmetrical movements",
            1 to "Minor paralysis: flattened nasolabial fold, asymmetric smile",
            2 to "Partial paralysis: total or near-total paralysis of the lower face",
            3 to "Complete paralysis of one or both sides: no movement in the upper or lower face"
        )
    ),
    NihssEntry(
        "5a", "5a. Motor: left arm",
        "Arms extended, palms down, at 90 degrees if sitting or 45 if supine. Drift counts if the arm falls before 10 seconds. Test the non-paretic arm first.",
        nihssArmOptions
    ),
    NihssEntry("5b", "5b. Motor: right arm", "Same method as the left arm.", nihssArmOptions),
    NihssEntry(
        "6a", "6a. Motor: left leg",
        "Leg held at 30 degrees, always tested supine. Drift counts if the leg falls before 5 seconds. Test the non-paretic leg first.",
        nihssLegOptions
    ),
    NihssEntry("6b", "6b. Motor: right leg", "Same method as the left leg.", nihssLegOptions),
    NihssEntry(
        "7", "7. Limb ataxia",
        "Finger-nose-finger and heel-shin on both sides, eyes open. Score only if out of proportion to weakness. Absent in patients who cannot understand or are paralysed.",
        listOf(
            0 to "Absent",
            1 to "Present in one limb",
            2 to "Present in two limbs",
            -2 to "Untestable: amputation or joint fusion"
        )
    ),
    NihssEntry(
        "8", "8. Sensory",
        "Pinprick, or grimace or withdrawal in obtunded or aphasic patients. Only loss attributed to stroke counts. Score 2 only when severe or total loss is clearly shown.",
        listOf(
            0 to "Normal, no sensory loss",
            1 to "Mild to moderate loss: pinprick duller or less sharp, but aware of being touched",
            2 to "Severe to total loss: not aware of being touched on the face, arm and leg"
        )
    ),
    NihssEntry(
        "9", "9. Best language",
        "Use the picture description, naming sheet and sentence reading, plus comprehension from the whole exam. Score 3 only if the patient is mute and follows no one-step commands.",
        listOf(
            0 to "No aphasia, normal",
            1 to "Mild to moderate aphasia: some loss of fluency or comprehension, conversation about the materials is difficult but possible",
            2 to "Severe aphasia: fragmentary communication, the listener carries the burden, the materials cannot be identified from the response",
            3 to "Mute or global aphasia: no usable speech or comprehension"
        )
    ),
    NihssEntry(
        "10", "10. Dysarthria",
        "Have the patient read or repeat words from the list. In severe aphasia, rate the clarity of spontaneous speech. Do not tell the patient why.",
        listOf(
            0 to "Normal",
            1 to "Mild to moderate: slurs some words, understood with some difficulty",
            2 to "Severe: unintelligible, or mute",
            -2 to "Untestable: intubated or other physical barrier"
        )
    ),
    NihssEntry(
        "11", "11. Extinction and inattention",
        "Information usually comes from earlier testing. It is scored only if present, so it is never untestable.",
        listOf(
            0 to "No abnormality",
            1 to "Inattention or extinction to bilateral simultaneous stimulation in one modality (visual, tactile, auditory, spatial or personal)",
            2 to "Profound hemi-inattention or extinction in more than one modality; does not recognise own hand or attends to only one side of space"
        )
    )
)

@Composable
private fun NihssEntryCard(
    entry: NihssEntry,
    selected: Int,
    autoScore: Int?,
    onSelect: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(entry.title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(4.dp))
            Text(entry.hint, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(6.dp))
            if (autoScore != null) {
                Text(
                    "Coma (item 1a = 3): scored automatically as $autoScore.",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            } else {
                entry.options.forEach { (value, label) ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(if (selected == value) -1 else value) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selected == value,
                            onClick = { onSelect(if (selected == value) -1 else value) }
                        )
                        Text(
                            if (value == -2) "UN" else "$value",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.width(26.dp)
                        )
                        Text(label, fontSize = 13.sp, modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
