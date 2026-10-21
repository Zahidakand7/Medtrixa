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
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medtrixa.app.ui.components.PremiumBackButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

// Sources: WAO Anaphylaxis Guidance 2020 (Cardona et al., World Allergy Organ J 2020;13:100472)
// and AAAAI/ACAAI Anaphylaxis Practice Parameter 2023 (Golden et al., Ann Allergy Asthma Immunol 2024;132:124-176).
// Wording is our own. Not affiliated with or endorsed by WAO, AAAAI or ACAAI.
const val ANAPHYLAXIS_SOURCE = "WAO 2020 · AAAAI/ACAAI 2023"
private const val ANAPHYLAXIS_CHECKED = "checked Oct 2026"

private data class AnaStep(val title: String, val note: String, val together: Boolean)

private val anaSteps = listOf(
    AnaStep("Remove the trigger", "Stop any infusion or agent that seems to be causing the reaction, if possible.", false),
    AnaStep("Assess quickly", "Airway, breathing, circulation, mental state and skin. Get the patient's weight.", false),
    AnaStep("Call for help", "Resuscitation team in hospital, emergency services in the community.", true),
    AnaStep("Give IM epinephrine", "Mid-anterolateral thigh. Note the time of the dose.", true),
    AnaStep("Position the patient", "Lie flat with legs raised, or a position of comfort if breathing is hard or they are vomiting. Do not let them stand or sit up suddenly.", true),
    AnaStep("Oxygen", "High-flow oxygen (6 to 8 L/min) by face mask when indicated.", false),
    AnaStep("IV access and fluids", "Wide-bore access. Consider rapid isotonic (0.9%) saline: adults 5 to 10 mL/kg in the first 5 to 10 minutes (1 to 2 L), children 10 mL/kg.", false),
    AnaStep("CPR if needed", "Continuous chest compressions if the patient arrests.", false),
    AnaStep("Monitor", "Blood pressure, heart rate, breathing and oxygenation at frequent intervals, continuously if possible.", false)
)

private fun anaMmss(totalSeconds: Int): String {
    val s = if (totalSeconds < 0) 0 else totalSeconds
    return String.format(Locale.US, "%02d:%02d", s / 60, s % 60)
}

private fun anaFmt(v: Double): String = String.format(Locale.US, "%.2f", v)

@Composable
private fun AnaCard(title: String, accent: Color, content: @Composable ColumnScope.() -> Unit) {
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
private fun AnaBullet(text: String) {
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
fun AnaphylaxisScreen(onBackClick: () -> Unit) {
    var isChild by rememberSaveable { mutableStateOf(false) }
    var weight by rememberSaveable { mutableStateOf("") }
    var doseCount by rememberSaveable { mutableStateOf(0) }
    var lastDose by rememberSaveable { mutableStateOf(0L) }
    var checked by rememberSaveable { mutableStateOf(0) }
    var now by remember { mutableStateOf(SystemClock.elapsedRealtime()) }
    var confirmReset by remember { mutableStateOf(false) }

    val view = LocalView.current
    val scope = rememberCoroutineScope()
    val weightRequester = remember { BringIntoViewRequester() }
    val running = doseCount > 0

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

    val sinceLast = if (running) maxOf(0, ((now - lastDose) / 1000).toInt()) else 0
    val (windowText, windowColor) = when {
        !running -> "Record each epinephrine dose to track the repeat window." to muted
        sinceLast < 300 -> "Repeat window opens in ${anaMmss(300 - sinceLast)}. Keep reassessing." to muted
        sinceLast <= 900 -> "Repeat window open (${anaMmss(900 - sinceLast)} left). Reassess; repeat if symptoms persist." to primary
        else -> "More than 15 minutes since the last dose. Reassess now." to danger
    }

    val w = weight.trim().replace(',', '.').toDoubleOrNull()
    val cap = if (isChild) 0.3 else 0.5
    val raw = if (w != null && w >= 1.0 && w <= 250.0) w * 0.01 else null
    val dose = raw?.let { minOf(it, cap) }
    val capped = raw != null && raw > cap

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
                    "Anaphylaxis",
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
                    "$ANAPHYLAXIS_SOURCE · $ANAPHYLAXIS_CHECKED",
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
                        Text("Epinephrine first", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = danger)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Give intramuscular epinephrine at the first sign of suspected anaphylaxis. " +
                                "Do not wait for criteria to be met, and never delay it for antihistamines or steroids.",
                            fontSize = 13.sp
                        )
                    }
                }
            }

            item {
                AnaCard("Recognise anaphylaxis", primary) {
                    Text("Likely when either of these is met:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(4.dp))
                    AnaBullet(
                        "Sudden illness (minutes to hours) with skin or mucosal signs, such as widespread hives, itching, " +
                            "flushing, or swollen lips, tongue or uvula, PLUS at least one of: breathing difficulty " +
                            "(breathlessness, wheeze, stridor, low peak flow, low oxygen); low blood pressure or organ " +
                            "dysfunction (collapse, fainting, incontinence); severe gut symptoms (severe cramps, repeated " +
                            "vomiting), especially after a non-food trigger."
                    )
                    AnaBullet(
                        "Sudden low blood pressure, bronchospasm or laryngeal involvement (stridor, voice change, painful " +
                            "swallowing) after a known or highly probable allergen, even with no skin signs."
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Low blood pressure: systolic below 90 mmHg in adults; in children under 10, below 70 + (2 × age in years); " +
                            "or a fall of more than 30% from the person's baseline.",
                        fontSize = 12.sp,
                        color = muted
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Meeting these criteria is not required before giving epinephrine (conditional recommendation, AAAAI/ACAAI 2023).",
                        fontSize = 12.sp,
                        color = muted
                    )
                }
            }

            item {
                AnaCard("Epinephrine dose (IM)", danger) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(selected = !isChild, onClick = { isChild = false }, label = { Text("Adult") })
                        FilterChip(selected = isChild, onClick = { isChild = true }, label = { Text("Child (pre-pubertal)") })
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
                    Spacer(Modifier.height(10.dp))
                    if (w != null && raw == null) {
                        Text("Enter a weight between 1 and 250 kg.", fontSize = 13.sp, color = danger)
                    } else if (dose != null) {
                        Text("${anaFmt(dose)} mg IM", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = primary)
                        Text(
                            "= ${anaFmt(dose)} mL of the 1 mg/mL (1:1000) solution, mid-anterolateral thigh.",
                            fontSize = 13.sp
                        )
                        if (capped) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "Capped at the ${if (isChild) "0.3" else "0.5"} mg maximum (uncapped: ${anaFmt(raw!!)} mg).",
                                fontSize = 12.sp,
                                color = danger
                            )
                        }
                    } else {
                        Text("Enter the weight to calculate 0.01 mg/kg.", fontSize = 13.sp, color = muted)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "0.01 mg/kg, maximum 0.5 mg for adults and 0.3 mg for pre-pubertal children. " +
                            "Teenagers: sources differ on the cap (0.3 or 0.5 mg), so follow your local protocol. " +
                            "Check the dose against your protocol before giving it.",
                        fontSize = 11.sp,
                        color = muted
                    )
                }
            }

            item {
                AnaCard("Epinephrine timer", danger) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                if (running) anaMmss(sinceLast) else "--:--",
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                if (running) "since dose $doseCount" else "No dose recorded yet",
                                fontSize = 12.sp,
                                color = muted
                            )
                        }
                        Button(
                            onClick = {
                                doseCount += 1
                                lastDose = SystemClock.elapsedRealtime()
                                now = lastDose
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = danger)
                        ) { Text(if (running) "Record next dose" else "Record dose") }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(windowText, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = windowColor)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "WAO: repeat every 5 to 15 minutes if needed. Most patients respond to 1 or 2 doses.",
                        fontSize = 12.sp,
                        color = muted
                    )
                    if (running) {
                        TextButton(onClick = { confirmReset = true }) { Text("Reset timer") }
                    }
                }
            }

            item {
                AnaCard("Initial actions", primary) {
                    anaSteps.forEachIndexed { idx, st ->
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
                AnaCard("If it is not settling", danger) {
                    AnaBullet(
                        "Refractory anaphylaxis: still present after appropriate epinephrine dosing and symptom-directed " +
                            "treatment, such as an IV fluid bolus for low blood pressure. A consensus definition adds that " +
                            "3 or more epinephrine doses were given, or an IV epinephrine infusion was started."
                    )
                    AnaBullet("It carries a higher risk of death. Call senior or critical-care help early.")
                    AnaBullet(
                        "An IV epinephrine infusion is a recognised next step. Rates are protocol-specific, so none is shown here."
                    )
                    AnaBullet("Persistent anaphylaxis: symptoms that last at least 4 hours.")
                }
            }

            item {
                AnaCard("Antihistamines and steroids", primary) {
                    AnaBullet("Second-line at most. They act slowly and do not treat airway or circulatory problems.")
                    AnaBullet("They are not reliable for preventing a biphasic reaction.")
                    AnaBullet("Never let them delay or replace epinephrine.")
                    Spacer(Modifier.height(4.dp))
                    Text("Source: AAAAI/ACAAI 2020 GRADE guideline and WAO 2020.", fontSize = 11.sp, color = muted)
                }
            }

            item {
                AnaCard("After the emergency", primary) {
                    AnaBullet("Observe until symptoms have fully resolved. Longer observation is suggested after severe anaphylaxis or when more than one epinephrine dose was needed.")
                    AnaBullet("A biphasic reaction is uncommon (median about 6.5% of adults in one meta-analysis). It is unlikely if the first reaction was not severe and the patient stays symptom-free for 1 hour after resolution.")
                    AnaBullet("Consider an acute serum tryptase, ideally within 2 hours of symptom onset, with a later baseline level for comparison.")
                    AnaBullet("Before discharge: teach trigger avoidance, supply and teach a self-injectable epinephrine device, explain when to seek further care, and refer to an allergist.")
                }
            }

            item {
                AnaCard("Auto-injectors", primary) {
                    AnaBullet("0.3 mg: patients weighing 30 kg or more (US-approved labelling).")
                    AnaBullet("0.15 mg: 15 to 30 kg. Several organisations support switching to 0.3 mg from about 25 kg to avoid underdosing.")
                    AnaBullet("0.1 mg (where available): 7.5 to 15 kg. Where it is not available, 0.15 mg may be used under 15 kg.")
                    AnaBullet("After an auto-injector dose, call emergency services if the reaction is severe, does not resolve promptly and completely, or returns or worsens.")
                }
            }

            item {
                Text(
                    "Decision support only. Doses and thresholds follow WAO 2020 and AAAAI/ACAAI 2023; always follow your " +
                        "local protocol. Wording is our own. Not affiliated with or endorsed by WAO, AAAAI or ACAAI.",
                    fontSize = 11.sp,
                    color = muted
                )
            }
        }
    }

    if (confirmReset) {
        AlertDialog(
            onDismissRequest = { confirmReset = false },
            title = { Text("Reset epinephrine timer?") },
            text = { Text("This clears the recorded doses and the clock.") },
            confirmButton = {
                TextButton(onClick = {
                    doseCount = 0
                    lastDose = 0L
                    confirmReset = false
                }) { Text("Reset") }
            },
            dismissButton = { TextButton(onClick = { confirmReset = false }) { Text("Cancel") } }
        )
    }
}
