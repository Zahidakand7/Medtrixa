package com.medtrixa.app.ui.screens.assess

import androidx.activity.compose.BackHandler
import androidx.compose.ui.unit.sp
import com.medtrixa.app.ui.components.PremiumBackButton
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background

private data class GcsOption(val points: Int, val label: String)

@Composable
fun GcsScreen(
    onBackClick: () -> Unit
) {
    BackHandler { onBackClick() }

    val eyeOptions = listOf(
        GcsOption(4, "Spontaneous"),
        GcsOption(3, "To speech"),
        GcsOption(2, "To pain"),
        GcsOption(1, "None")
    )
    val verbalOptions = listOf(
        GcsOption(5, "Oriented"),
        GcsOption(4, "Confused"),
        GcsOption(3, "Inappropriate words"),
        GcsOption(2, "Incomprehensible sounds"),
        GcsOption(1, "None")
    )
    val motorOptions = listOf(
        GcsOption(6, "Obeys commands"),
        GcsOption(5, "Localizes pain"),
        GcsOption(4, "Withdraws from pain"),
        GcsOption(3, "Abnormal flexion"),
        GcsOption(2, "Abnormal extension"),
        GcsOption(1, "None")
    )

    var eyeScore by remember { mutableStateOf<Int?>(null) }
    var verbalScore by remember { mutableStateOf<Int?>(null) }
    var motorScore by remember { mutableStateOf<Int?>(null) }

    val allSelected = eyeScore != null && verbalScore != null && motorScore != null
    val total = (eyeScore ?: 0) + (verbalScore ?: 0) + (motorScore ?: 0)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PremiumBackButton(onClick = onBackClick)
                Column() {
                    Text("Glasgow Coma Scale", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 26.sp)
                    Text("Level of Consciousness Assessment", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            GcsSection("Eye Opening (E)", eyeOptions, eyeScore) { eyeScore = it }
            Spacer(modifier = Modifier.height(16.dp))
            GcsSection(
                "Verbal Response (V)",
                verbalOptions,
                verbalScore,
                singleLineLabels = setOf("Incomprehensible sounds")
            ) { verbalScore = it }
            Spacer(modifier = Modifier.height(16.dp))
            GcsSection(
                "Motor Response (M)",
                motorOptions,
                motorScore,
                singleLineLabels = setOf("Abnormal flexion")
            ) { motorScore = it }

            Spacer(modifier = Modifier.height(20.dp))

            val (bandLabel, bandColor, guidance) = when {
                total <= 8 -> Triple("Severe (3–8)", Color(0xFFC0392B), "Secure the airway (intubation is generally indicated at GCS 8 or below). Urgent CT head and neurosurgical referral. Avoid hypoxia and hypotension, and manage raised intracranial pressure.")
                total <= 12 -> Triple("Moderate (9–12)", Color(0xFFC27A00), "Urgent CT head and neurosurgical consultation. Admit for close monitoring with frequent repeat GCS and pupil checks. Be ready to intubate if the score falls.")
                else -> Triple("Mild (13–15)", Color(0xFF1F9D6B), "Observe with serial neurological checks. Obtain CT head if decision-rule criteria are met, for example GCS below 15 at 2 hours, vomiting, age 65 or older, anticoagulant use or suspected skull fracture.")
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Total GCS", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(6.dp))
                    if (allSelected) {
                        Text("$total / 15", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "E$eyeScore V$verbalScore M$motorScore",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = bandLabel,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            modifier = Modifier
                                .background(bandColor, RoundedCornerShape(20.dp))
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(guidance, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
                        GcsNote(
                            "Report the components",
                            "Record E, V and M separately, not just the sum. The motor score is the strongest single predictor of outcome."
                        )
                        GcsNote(
                            "Scoring",
                            "Use the best response observed. If there is no response to a peripheral stimulus, apply a central one. Repeat serially: the trend matters more than a single score."
                        )
                        GcsNote(
                            "Interpret with caution",
                            "Sedation, paralysis, intoxication, aphasia, deafness, post-ictal state, hypoxia and hypotension can lower the score without structural injury. Intubated patients cannot give a verbal score. In children under 5 use the paediatric GCS."
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Teasdale G, Jennett B. Lancet 1974;2:81-84. Teasdale G et al. Lancet Neurol 2014;13:844-854. Use with clinical judgement.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                    } else {
                        Text("— / 15", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "E${eyeScore ?: "-"} V${verbalScore ?: "-"} M${motorScore ?: "-"}  •  Select all three categories",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun GcsSection(
    title: String,
    options: List<GcsOption>,
    selected: Int?,
    singleLineLabels: Set<String> = emptySet(),
    onSelect: (Int) -> Unit
) {
    Column {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            val rows = mutableListOf<List<GcsOption>>()
            var i = 0
            while (i < options.size) {
                val option = options[i]
                if (option.label in singleLineLabels) {
                    rows.add(listOf(option))
                    i++
                } else if (i + 1 < options.size && options[i + 1].label !in singleLineLabels) {
                    rows.add(listOf(option, options[i + 1]))
                    i += 2
                } else {
                    rows.add(listOf(option))
                    i++
                }
            }

            rows.forEach { rowOptions ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    rowOptions.forEach { option ->
                        val isWide = option.label in singleLineLabels
                        FilterChip(
                            selected = selected == option.points,
                            onClick = { onSelect(option.points) },
                            label = {
                                Text(
                                    "${option.points} — ${option.label}",
                                    maxLines = 1
                                )
                            },
                            modifier = if (isWide) Modifier.fillMaxWidth() else Modifier
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GcsNote(title: String, body: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
        Text(title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(2.dp))
        Text(body, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Start)
    }
}
