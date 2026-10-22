package com.medtrixa.app.ui.screens

import com.medtrixa.app.ui.components.BulletDot
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
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medtrixa.app.ui.components.PremiumBackButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

// Sources: Neurocritical Care Society, Emergency Neurological Life Support (ENLS) Status Epilepticus
// Protocol v6.0 (Swor and Waybright, last updated July 2024), and the American Epilepsy Society guideline
// on convulsive status epilepticus (Glauser et al., Epilepsy Curr 2016). The ESETT trial (Kapur et al.,
// N Engl J Med 2019) is the basis for treating the three second-line drugs as comparable options.
// Wording is our own. Not affiliated with or endorsed by NCS or AES.
const val STATUS_EPILEPTICUS_SOURCE = "NCS ENLS 6.0 · AES 2016"
private const val SE_CHECKED = "checked Oct 2026"

// Clock landmarks, in seconds
private const val SE_STATUS_AT = 300      // 5 min: seizure this long is status epilepticus
private const val SE_SECOND_AT = 1200     // 20 min: second-line drug due if still seizing
private const val SE_REFRACTORY_AT = 2400 // 40 min: refractory if the second-line drug failed

private data class SeStep(val title: String, val note: String, val together: Boolean)

private val seSteps = listOf(
    SeStep("Protect airway and breathing", "Position safely, give oxygen, suction, and support breathing and blood pressure as needed.", true),
    SeStep("Check glucose", "Fingerstick glucose. If low, give IV dextrose (IM glucagon if there is no IV). Do not hold the benzodiazepine while you wait for the result.", true),
    SeStep("Give a benzodiazepine", "Use the first-line doses below. Under-dosing is common.", true),
    SeStep("IV access and monitoring", "ECG, blood pressure and oxygen saturation. Give fluids as needed.", false),
    SeStep("Send bloods", "Blood count, basic metabolic panel, calcium, magnesium and antiseizure drug levels. Add a pregnancy test where relevant.", false),
    SeStep("Look for the cause", "Work it up alongside treatment. Tests must not delay seizure control.", false),
    SeStep("Arrange EEG", "Continuous EEG if available, as soon as possible, unless the patient is back to baseline.", false)
)

private data class SeDose(
    val name: String,
    val perKg: Double,
    val cap: Double?,
    val unit: String,
    val how: String
)

private val seAdultFirst = listOf(
    SeDose("Lorazepam IV", 0.1, 4.0, "mg", "Over about 2 minutes. Repeat once after 3 to 5 minutes if seizures continue."),
    SeDose("Diazepam IV (alternative)", 0.15, 10.0, "mg", "An alternative to lorazepam.")
)

private val seChildFirst = listOf(
    SeDose("Lorazepam IV", 0.1, 4.0, "mg", "Repeat once after 3 to 5 minutes if seizures continue."),
    SeDose("Midazolam intranasal (no IV access)", 0.2, 10.0, "mg", "Single dose when there is no IV.")
)

private val seAdultSecond = listOf(
    SeDose("Fosphenytoin IV", 20.0, 1500.0, "mg PE", "Phenytoin equivalents. At up to 150 mg PE per minute."),
    SeDose("Phenytoin IV", 20.0, 1500.0, "mg", "At up to 50 mg per minute."),
    SeDose("Valproate IV", 40.0, 3000.0, "mg", "Over 10 minutes."),
    SeDose("Levetiracetam IV", 60.0, 4500.0, "mg", "Single loading dose.")
)

private val seChildSecond = listOf(
    SeDose("Fosphenytoin IV", 20.0, 1500.0, "mg PE", "Phenytoin equivalents. At up to 150 mg PE per minute."),
    SeDose("Levetiracetam IV", 60.0, 4500.0, "mg", "Single loading dose."),
    SeDose("Valproate IV", 40.0, 3000.0, "mg", "Over 10 minutes."),
    SeDose("Phenobarbital IV", 40.0, 1000.0, "mg", "Single loading dose.")
)

private fun seMmss(totalSeconds: Int): String {
    val s = if (totalSeconds < 0) 0 else totalSeconds
    return String.format(Locale.US, "%02d:%02d", s / 60, s % 60)
}

private fun seFmt(v: Double): String {
    val s = when {
        v >= 100 -> String.format(Locale.US, "%.0f", v)
        v >= 10 -> String.format(Locale.US, "%.1f", v)
        else -> String.format(Locale.US, "%.2f", v)
    }
    return if (s.contains('.')) s.trimEnd('0').trimEnd('.') else s
}

// " (= 8 mg)" when a weight is known, otherwise nothing
private fun seAmt(kg: Double?, perKg: Double, unit: String): String =
    if (kg == null) "" else " (= ${seFmt(kg * perKg)} $unit)"

@Composable
private fun SeCard(title: String, accent: Color, content: @Composable ColumnScope.() -> Unit) {
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
private fun SeBullet(text: String) {
    Row(Modifier.padding(vertical = 2.dp)) {
        BulletDot()
        Text(
            text,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SeHeading(text: String) {
    Text(
        text,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(top = 10.dp, bottom = 2.dp)
    )
}

@Composable
private fun SeDoseRow(d: SeDose, kg: Double?) {
    val raw = if (kg == null) null else kg * d.perKg
    val cap = d.cap
    val amount = if (raw == null) null else if (cap != null && raw > cap) cap else raw
    val capped = raw != null && cap != null && raw > cap
    val capText = if (cap != null) ", max ${seFmt(cap)} ${d.unit}" else ""

    Column(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                d.name,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )
            Text(
                if (amount != null) "${seFmt(amount)} ${d.unit}" else "--",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Text(
            "${seFmt(d.perKg)} ${d.unit}/kg$capText. ${d.how}",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (capped && raw != null) {
            Text(
                "Capped at the maximum (uncapped: ${seFmt(raw)} ${d.unit}).",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun StatusEpilepticusScreen(onBackClick: () -> Unit) {
    var isChild by rememberSaveable { mutableStateOf(false) }
    var weight by rememberSaveable { mutableStateOf("") }
    var onset by rememberSaveable { mutableStateOf(0L) }
    var benzoCount by rememberSaveable { mutableStateOf(0) }
    var lastBenzo by rememberSaveable { mutableStateOf(0L) }
    var secondAt by rememberSaveable { mutableStateOf(0L) }
    var checked by rememberSaveable { mutableStateOf(0) }
    var now by remember { mutableStateOf(SystemClock.elapsedRealtime()) }
    var confirmReset by remember { mutableStateOf(false) }

    val view = LocalView.current
    val scope = rememberCoroutineScope()
    val weightRequester = remember { BringIntoViewRequester() }
    val running = onset > 0L

    SideEffect { view.keepScreenOn = running }
    DisposableEffect(Unit) { onDispose { view.keepScreenOn = false } }
    LaunchedEffect(running) {
        while (running) {
            now = SystemClock.elapsedRealtime()
            delay(500)
        }
    }

    val primary = MaterialTheme.colorScheme.primary
    val danger = MaterialTheme.colorScheme.error
    val muted = MaterialTheme.colorScheme.onSurfaceVariant

    val elapsed = if (running) maxOf(0, ((now - onset) / 1000).toInt()) else 0
    val (phaseTitle, phaseText, phaseColor) = when {
        !running -> Triple(
            "Clock not started",
            "Start the clock at seizure onset. If onset is unknown, start it when you first see the seizure.",
            muted
        )
        elapsed < SE_STATUS_AT -> Triple(
            "0 to 5 min · Stabilise",
            "Airway, breathing, circulation, glucose and IV access. Have the benzodiazepine ready: a seizure of 5 minutes is status epilepticus.",
            muted
        )
        elapsed < SE_SECOND_AT -> Triple(
            "5 to 20 min · Benzodiazepine",
            "Give a benzodiazepine now if you have not. If seizures continue, repeat once after 3 to 5 minutes.",
            primary
        )
        elapsed < SE_REFRACTORY_AT -> Triple(
            "20 to 40 min · Second-line drug",
            "If seizures continue after adequate benzodiazepine doses, give one second-line drug now.",
            danger
        )
        else -> Triple(
            "Over 40 min · Refractory",
            "If seizures continue after the second-line drug: intubate, start an anaesthetic infusion, run continuous EEG and arrange ICU care.",
            danger
        )
    }

    val sinceBenzo = if (benzoCount > 0) maxOf(0, ((now - lastBenzo) / 1000).toInt()) else 0
    val benzoText = when {
        benzoCount == 0 -> "No benzodiazepine recorded yet."
        benzoCount >= 2 -> "$benzoCount doses recorded. If seizures continue, move to a second-line drug."
        sinceBenzo < 180 -> "Dose 1 recorded. A repeat is possible in ${seMmss(180 - sinceBenzo)} if seizures continue."
        else -> "Dose 1 was ${seMmss(sinceBenzo)} ago. Repeat once if seizures continue."
    }
    val sinceSecond = if (secondAt > 0L) maxOf(0, ((now - secondAt) / 1000).toInt()) else 0
    val secondText = if (secondAt > 0L) {
        "Second-line drug given ${seMmss(sinceSecond)} ago."
    } else {
        "No second-line drug recorded yet."
    }

    val w = weight.trim().replace(',', '.').toDoubleOrNull()
    val kg = if (w != null && w >= 1.0 && w <= 250.0) w else null
    val firstLineDrugs = if (isChild) seChildFirst else seAdultFirst
    val secondLineDrugs = if (isChild) seChildSecond else seAdultSecond

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
                    "Status Epilepticus",
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
                Text(
                    "$STATUS_EPILEPTICUS_SOURCE · $SE_CHECKED",
                    fontSize = 12.sp,
                    color = muted
                )
            }

            item {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(listOf(danger.copy(alpha = 0.16f), danger.copy(alpha = 0.04f))),
                            RoundedCornerShape(24.dp)
                        )
                        .padding(horizontal = 20.dp, vertical = 18.dp)
                ) {
                    Column {
                        Text("Time is control", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = danger)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "A seizure lasting 5 minutes or more, or repeated seizures without recovery in between, " +
                                "is status epilepticus. Treat at once: how quickly the first benzodiazepine is given " +
                                "matters most.",
                            fontSize = 13.sp
                        )
                    }
                }
            }

            item {
                SeCard("Seizure clock", danger) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                if (running) seMmss(elapsed) else "--:--",
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                if (running) "since seizure onset" else "Not started",
                                fontSize = 12.sp,
                                color = muted
                            )
                        }
                        if (!running) {
                            Button(
                                onClick = {
                                    onset = SystemClock.elapsedRealtime()
                                    now = onset
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = danger)
                            ) { Text("Start clock") }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(phaseTitle, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = phaseColor)
                    Text(phaseText, fontSize = 13.sp, color = if (phaseColor == muted) muted else phaseColor)
                    if (running) {
                        Spacer(Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = {
                                    benzoCount += 1
                                    lastBenzo = SystemClock.elapsedRealtime()
                                    now = lastBenzo
                                },
                                modifier = Modifier.weight(1f)
                            ) { Text("Benzodiazepine given", fontSize = 12.sp) }
                            OutlinedButton(
                                onClick = {
                                    secondAt = SystemClock.elapsedRealtime()
                                    now = secondAt
                                },
                                modifier = Modifier.weight(1f)
                            ) { Text("Second-line given", fontSize = 12.sp) }
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(benzoText, fontSize = 12.sp, color = muted)
                        Text(secondText, fontSize = 12.sp, color = muted)
                        TextButton(onClick = { confirmReset = true }) { Text("Reset clock") }
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Markers follow the AES phases (5, 20 and 40 minutes). ENLS moves to a second-line drug " +
                            "once seizures continue 10 to 20 minutes after adequate benzodiazepine doses.",
                        fontSize = 11.sp,
                        color = muted
                    )
                }
            }

            item {
                SeCard("Doses by weight", primary) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(selected = !isChild, onClick = { isChild = false }, label = { Text("Adult") })
                        FilterChip(selected = isChild, onClick = { isChild = true }, label = { Text("Child") })
                    }
                    Spacer(Modifier.height(8.dp))
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
                    if (w != null && kg == null) {
                        Spacer(Modifier.height(6.dp))
                        Text("Enter a weight between 1 and 250 kg.", fontSize = 13.sp, color = danger)
                    } else if (kg == null) {
                        Spacer(Modifier.height(6.dp))
                        Text("Enter the weight to calculate each dose.", fontSize = 13.sp, color = muted)
                    }

                    SeHeading("First line: benzodiazepine")
                    firstLineDrugs.forEach { SeDoseRow(it, kg) }
                    if (!isChild) {
                        Text("No IV access: midazolam 10 mg IM.", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Text(
                            "For a small adult, check your protocol for a lower IM dose.",
                            fontSize = 12.sp,
                            color = muted
                        )
                    }

                    SeHeading("Second line: choose one")
                    secondLineDrugs.forEach { SeDoseRow(it, kg) }
                    Spacer(Modifier.height(4.dp))
                    SeBullet("Give one second-line drug, not several. The ESETT trial found levetiracetam, fosphenytoin and valproate similarly effective, so choose by patient factors and local availability.")
                    SeBullet("Benzodiazepine doses are often too low. Make sure the full dose was given before moving on.")

                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Doses are per kg with the stated maximum. Check every dose against your protocol before giving it.",
                        fontSize = 11.sp,
                        color = muted
                    )
                }
            }

            item {
                SeCard("Initial actions", primary) {
                    seSteps.forEachIndexed { idx, st ->
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
                                    Text(st.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                    if (st.together) {
                                        Spacer(Modifier.width(8.dp))
                                        Text("DO TOGETHER", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = danger)
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
                SeCard("If seizures continue: refractory", danger) {
                    if (!isChild) {
                        SeBullet("Intubate, then start one anaesthetic infusion. Start continuous EEG if it is not already running.")
                        SeHeading("Midazolam")
                        SeBullet("Bolus 0.2 mg/kg IV over 2 to 5 minutes${seAmt(kg, 0.2, "mg")}. Repeat every 5 minutes until seizures stop, up to a total load of 2 mg/kg${seAmt(kg, 2.0, "mg")}.")
                        SeBullet("Infusion: start at 0.1 mg/kg/h${seAmt(kg, 0.1, "mg/h")}, adjust to seizure control. Usual range 0.05 to 2 mg/kg/h.")
                        SeHeading("Propofol")
                        SeBullet("Bolus 1 to 2 mg/kg IV over 3 to 5 minutes${if (kg == null) "" else " (= ${seFmt(kg * 1.0)} to ${seFmt(kg * 2.0)} mg)"}. Repeat every 3 to 5 minutes up to a total of 10 mg/kg${seAmt(kg, 10.0, "mg")}.")
                        SeBullet("Infusion: start at 20 mcg/kg/min, adjust to seizure control. Usual range 30 to 200 mcg/kg/min.")
                    } else {
                        SeBullet("Intubate, then start one of the following. Start continuous EEG if it is not already running.")
                        SeHeading("Phenobarbital")
                        SeBullet("20 mg/kg IV${seAmt(kg, 20.0, "mg")} at 1 mg/kg/min.")
                        SeHeading("Or midazolam")
                        SeBullet("Bolus 0.1 to 0.2 mg/kg IV${if (kg == null) "" else " (= ${seFmt(kg * 0.1)} to ${seFmt(kg * 0.2)} mg)"}. Repeat 0.2 mg/kg every 5 minutes until seizures stop.")
                        SeBullet("Infusion: start at 0.1 to 0.2 mg/kg/h, adjust to seizure control, up to 1 mg/kg/h.")
                        SeBullet("Pentobarbital can be considered. Use propofol with special care if a mitochondrial disorder is suspected.")
                    }
                    SeHeading("Alongside")
                    SeBullet("Keep the second-line drug going and consider adding another, guided by the EEG.")
                    SeBullet("Many teams continue the infusion for at least 24 hours once seizures are controlled, then wean gradually. The best duration is not established.")
                    SeBullet("If continuous EEG is not available, consider transfer to a centre that has it.")
                }
            }

            item {
                SeCard("Look for the cause", primary) {
                    SeBullet("Low glucose, low sodium, calcium or magnesium, hypoxia and low blood pressure can all drive seizures. Seizures are hard to control until these are corrected.")
                    SeBullet("Adults: missed antiseizure medication, new-onset seizure, alcohol, drugs, stroke, infection. Children: also CNS infection, inflammation, and genetic or metabolic disease.")
                    SeHeading("Toxic causes")
                    SeBullet("Isoniazid: lorazepam, then pyridoxine 70 mg/kg (max 5 g)${if (kg == null) "" else " (= ${seFmt(minOf(kg * 0.07, 5.0))} g)"}.")
                    SeBullet("Tricyclic antidepressants: look for a wide QRS on the ECG and give sodium bicarbonate.")
                    SeBullet("Organophosphates: atropine, midazolam and pralidoxime.")
                    SeBullet("Also consider theophylline, cocaine and other sympathomimetics. Alcohol withdrawal is treated with escalating benzodiazepine doses.")
                }
            }

            item {
                SeCard("Is it over?", primary) {
                    SeBullet("Status epilepticus has ended when the patient is back to their pre-seizure responsiveness, or the EEG shows the seizures have stopped.")
                    SeBullet("If the patient does not wake promptly after the first-line drug, assume seizures may be continuing and get an EEG. Non-convulsive status is possible.")
                    SeBullet("Benzodiazepines wear off quickly, so a longer-acting drug is given to prevent the seizures returning.")
                    SeBullet("Titrate antiseizure drugs to therapeutic levels. Check levels at least 2 hours after a fosphenytoin, phenytoin or valproate load.")
                }
            }

            item {
                SeCard("Handover checklist", primary) {
                    SeBullet("Duration of the seizure, and what it looked like.")
                    SeBullet("Relevant history and usual medicines.")
                    SeBullet("Every drug given so far, with times and the response to each.")
                    SeBullet("Neurological examination, labs and any antiseizure drug levels.")
                    SeBullet("Brain imaging or lumbar puncture results, if done.")
                }
            }

            item {
                Text(
                    "Decision support only. Doses and timings follow NCS ENLS Status Epilepticus Protocol v6.0 (July 2024) " +
                        "and the AES 2016 guideline; always follow your local protocol. Wording is our own. " +
                        "Not affiliated with or endorsed by NCS or AES.",
                    fontSize = 11.sp,
                    color = muted
                )
            }
        }
    }

    if (confirmReset) {
        AlertDialog(
            onDismissRequest = { confirmReset = false },
            title = { Text("Reset the seizure clock?") },
            text = { Text("This clears the clock and the recorded drug times.") },
            confirmButton = {
                TextButton(onClick = {
                    onset = 0L
                    benzoCount = 0
                    lastBenzo = 0L
                    secondAt = 0L
                    confirmReset = false
                }) { Text("Reset") }
            },
            dismissButton = { TextButton(onClick = { confirmReset = false }) { Text("Cancel") } }
        )
    }
}
