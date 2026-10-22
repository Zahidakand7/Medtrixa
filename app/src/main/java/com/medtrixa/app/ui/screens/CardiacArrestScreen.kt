package com.medtrixa.app.ui.screens

import com.medtrixa.app.ui.components.BulletText
import android.os.SystemClock
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medtrixa.app.ui.components.PremiumBackButton
import kotlinx.coroutines.delay

private const val CYCLE_SECONDS = 120

private fun fmt(totalSeconds: Int): String {
    val s = if (totalSeconds < 0) 0 else totalSeconds
    return "%02d:%02d".format(s / 60, s % 60)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardiacArrestScreen(onBackClick: () -> Unit) {
    var running by rememberSaveable { mutableStateOf(false) }
    var accumulatedMs by rememberSaveable { mutableStateOf(0L) }
    var runStart by rememberSaveable { mutableStateOf(0L) }
    var now by remember { mutableStateOf(SystemClock.elapsedRealtime()) }
    var shocks by rememberSaveable { mutableStateOf(0) }
    var epiCount by rememberSaveable { mutableStateOf(0) }
    var lastEpiSec by rememberSaveable { mutableStateOf(-1) }
    var antiCount by rememberSaveable { mutableStateOf(0) }
    var roscSec by rememberSaveable { mutableStateOf(-1) }
    var log by rememberSaveable { mutableStateOf("") }
    var confirmReset by remember { mutableStateOf(false) }

    val haptic = LocalHapticFeedback.current
    val clipboard = LocalClipboardManager.current
    val view = LocalView.current

    SideEffect { view.keepScreenOn = running }
    DisposableEffect(Unit) { onDispose { view.keepScreenOn = false } }

    LaunchedEffect(running) {
        while (running) {
            now = SystemClock.elapsedRealtime()
            delay(200)
        }
    }

    val totalMs = accumulatedMs + if (running) (now - runStart) else 0L
    val totalSec = (totalMs / 1000).toInt()
    val cycleLeft = CYCLE_SECONDS - (totalSec % CYCLE_SECONDS)
    val cycleNumber = totalSec / CYCLE_SECONDS + 1
    val cycleProgress = ((CYCLE_SECONDS - cycleLeft).toFloat() / CYCLE_SECONDS)

    LaunchedEffect(cycleNumber) {
        if (running && cycleNumber > 1) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    fun addEvent(text: String) {
        log = (if (log.isEmpty()) "" else log + "\n") + fmt(totalSec) + "  " + text
    }

    fun startTimer() {
        val t = SystemClock.elapsedRealtime()
        runStart = t
        now = t
        running = true
        if (log.isEmpty()) log = "00:00  CPR started"
    }

    fun pauseTimer() {
        accumulatedMs += SystemClock.elapsedRealtime() - runStart
        running = false
    }

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
                        Text("Cardiac Arrest", fontSize = 20.sp, lineHeight = 26.sp, fontWeight = FontWeight.SemiBold)
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
                            if (roscSec >= 0) "ROSC at ${fmt(roscSec)}" else "CPR cycle $cycleNumber \u00b7 2 minutes",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            fmt(cycleLeft),
                            fontSize = 54.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        LinearProgressIndicator(
                            progress = cycleProgress,
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(50)),
                            color = primary,
                            trackColor = primary.copy(alpha = 0.18f)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Total time ${fmt(totalSec)}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        val prompt = when {
                            roscSec >= 0 -> "Go to post\u2013cardiac arrest care."
                            running && totalSec >= CYCLE_SECONDS && totalSec % CYCLE_SECONDS < 10 ->
                                "Rhythm check now. Keep the pause under 10 seconds, then resume CPR."
                            running && cycleLeft <= 10 ->
                                "Prepare for rhythm check in $cycleLeft s. Change compressor if due."
                            running -> "Continue high-quality CPR."
                            else -> "Press Start when compressions begin."
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(prompt, fontSize = 13.sp, textAlign = TextAlign.Center, lineHeight = 18.sp)
                    }
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = { if (running) pauseTimer() else startTimer() },
                            modifier = Modifier.weight(1f).height(52.dp),
                            shape = RoundedCornerShape(16.dp),
                            enabled = roscSec < 0
                        ) { Text(if (running) "Pause" else if (accumulatedMs > 0) "Resume" else "Start") }
                        OutlinedButton(
                            onClick = { confirmReset = true },
                            modifier = Modifier.weight(1f).height(52.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) { Text("Reset") }
                    }
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        EventButton(
                            label = "Shock given",
                            count = shocks,
                            modifier = Modifier.weight(1f)
                        ) { shocks++; addEvent("Shock #$shocks") }
                        EventButton(
                            label = "Epinephrine 1 mg",
                            count = epiCount,
                            modifier = Modifier.weight(1f)
                        ) { epiCount++; lastEpiSec = totalSec; addEvent("Epinephrine 1 mg IV/IO (#$epiCount)") }
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        EventButton(
                            label = "Amiodarone / lidocaine",
                            count = antiCount,
                            modifier = Modifier.weight(1f)
                        ) {
                            antiCount++
                            addEvent("Amiodarone or lidocaine, dose #$antiCount")
                        }
                        Button(
                            onClick = {
                                if (running) pauseTimer()
                                roscSec = totalSec
                                addEvent("ROSC")
                            },
                            modifier = Modifier.weight(1f).height(64.dp),
                            shape = RoundedCornerShape(16.dp),
                            enabled = roscSec < 0 && (totalSec > 0 || running)
                        ) { Text("ROSC achieved", fontWeight = FontWeight.SemiBold) }
                    }
                }

                item {
                    val epiText = if (lastEpiSec < 0) {
                        "Epinephrine 1 mg IV/IO every 3 to 5 minutes. Give as soon as possible for non-shockable rhythms (asystole/PEA)."
                    } else {
                        val ago = totalSec - lastEpiSec
                        "Last epinephrine ${fmt(ago)} ago. " +
                            if (ago >= 300) "More than 5 minutes: re-dose is due."
                            else if (ago >= 180) "Re-dose window is open (every 3 to 5 min)."
                            else "Next dose due in 3 to 5 minutes from the last."
                    }
                    val antiText = if (antiCount == 0) {
                        "Refractory VF/pVT: amiodarone 300 mg bolus, or lidocaine 1 to 1.5 mg/kg."
                    } else {
                        "Next antiarrhythmic dose: amiodarone 150 mg, or lidocaine 0.5 to 0.75 mg/kg."
                    }
                    InfoCard("Drug prompts", listOf(epiText, antiText))
                }

                item {
                    InfoCard(
                        "High-quality CPR",
                        listOf(
                            "Push hard: at least 2 inches (5 cm).",
                            "Push fast: 100 to 120 per minute, with complete chest recoil.",
                            "Minimize interruptions in compressions. Avoid excessive ventilation.",
                            "Change compressor every 2 minutes, or sooner if fatigued.",
                            "No advanced airway: 30:2 compression to ventilation ratio.",
                            "Advanced airway in place: 1 breath every 6 seconds (10 per minute) with continuous compressions.",
                            "Use continuous waveform capnography. If ETCO\u2082 is low or falling, reassess CPR quality."
                        )
                    )
                }

                item {
                    InfoCard(
                        "Shock energy (VF/pVT)",
                        listOf(
                            "Biphasic: follow the manufacturer recommendation (for example, an initial 120 to 200 J). If unknown, use the maximum available.",
                            "Second and later doses should be equivalent; higher doses may be considered.",
                            "Monophasic: 360 J."
                        )
                    )
                }

                item {
                    InfoCard(
                        "Reversible causes",
                        listOf(
                            "Hypovolemia, hypoxia, hydrogen ion (acidosis), hypo/hyperkalemia, hypothermia.",
                            "Tension pneumothorax, tamponade (cardiac), toxins, thrombosis (pulmonary), thrombosis (coronary)."
                        )
                    )
                }

                if (log.isNotEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Event log", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                log.split("\n").forEach { line -> Text(line, fontSize = 13.sp, lineHeight = 18.sp) }
                                TextButton(onClick = { clipboard.setText(AnnotatedString(log)) }) { Text("Copy log") }
                            }
                        }
                    }
                }

                item {
                    Text(
                        "Source: AHA 2025 Adult Cardiac Arrest Algorithm (Circulation 2025;152 suppl 2). " +
                            "Reviewed October 2026. Decision support only; follow your local protocol.",
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
            title = { Text("Reset timer and log?") },
            text = { Text("This clears the timer, counters and event log.") },
            confirmButton = {
                TextButton(onClick = {
                    running = false
                    accumulatedMs = 0L
                    runStart = 0L
                    shocks = 0
                    epiCount = 0
                    lastEpiSec = -1
                    antiCount = 0
                    roscSec = -1
                    log = ""
                    confirmReset = false
                }) { Text("Reset") }
            },
            dismissButton = { TextButton(onClick = { confirmReset = false }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun EventButton(label: String, count: Int, modifier: Modifier, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(64.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center, lineHeight = 16.sp)
            Text("Given: $count", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun InfoCard(title: String, lines: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
            lines.forEach { line ->
                BulletText(line)
            }
        }
    }
}
