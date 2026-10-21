package com.medtrixa.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medtrixa.app.ui.components.PremiumBackButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Source: AHRQ TeamSTEPPS, "Tool: SBAR" (ahrq.gov), page last reviewed by AHRQ Nov 2019.
// Wording is our own. Not affiliated with or endorsed by AHRQ or HHS.
const val SBAR_SOURCE = "AHRQ TeamSTEPPS"
private const val SBAR_REVIEWED = "last reviewed Nov 2019"

private data class SbarSection(
    val letter: String,
    val title: String,
    val question: String,
    val points: List<String>
)

private val sbarSections = listOf(
    SbarSection(
        "S", "Situation", "What is happening with the patient right now?",
        listOf(
            "Who you are and your role",
            "Patient identifiers such as age and sex",
            "A brief statement of the current problem"
        )
    ),
    SbarSection(
        "B", "Background", "What is the clinical background?",
        listOf(
            "History related to the current situation",
            "Signs and symptoms of the presenting complaint",
            "Test results, such as lab or imaging reports"
        )
    ),
    SbarSection(
        "A", "Assessment", "What do you think the problem is?",
        listOf(
            "Your conclusion from the background, history and your observations",
            "Ask yourself what else it could be",
            "Include objective data such as vital signs"
        )
    ),
    SbarSection(
        "R", "Recommendation", "What do you recommend, and what do you need?",
        listOf(
            "Your recommendation or request",
            "What is needed, and by when"
        )
    )
)

private val tips = listOf(
    "Introduce yourself. Do not assume everyone knows who you are.",
    "SBAR is flexible: use the parts your team needs, in the order that suits.",
    "Naming each step aloud (situation, background, assessment, recommendation) helps you stay on track."
)

private val recExamples = listOf(
    "Please come and assess the patient",
    "Additional tests needed",
    "Specialist review needed",
    "Treatment needs review"
)

@Composable
fun SbarHandoverScreen(onBackClick: () -> Unit) {
    var s by rememberSaveable { mutableStateOf("") }
    var b by rememberSaveable { mutableStateOf("") }
    var a by rememberSaveable { mutableStateOf("") }
    var r by rememberSaveable { mutableStateOf("") }
    var repeated by rememberSaveable { mutableStateOf(false) }
    val clipboard = LocalClipboardManager.current
    val values = listOf(s, b, a, r)

    fun summary(): String = sbarSections.indices
        .filter { values[it].isNotBlank() }
        .joinToString("\n\n") { i -> "${sbarSections[i].title.uppercase()}\n${values[i].trim()}" }

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
                    "SBAR Handover",
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
                    "$SBAR_SOURCE · $SBAR_REVIEWED",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Do not enter names or other patient identifiers. Text is not saved after you leave this screen.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            "How to use SBAR",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(4.dp))
                        tips.forEach { tip ->
                            Text(
                                "•  $tip",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            items(sbarSections.size) { i ->
                val sec = sbarSections[i]
                val requester = remember { BringIntoViewRequester() }
                val scope = rememberCoroutineScope()
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            "${sec.letter} · ${sec.title}",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            sec.question,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(4.dp))
                        sec.points.forEach { pt ->
                            Text(
                                "•  $pt",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = values[i],
                            onValueChange = { v ->
                                when (i) { 0 -> s = v; 1 -> b = v; 2 -> a = v; else -> r = v }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .bringIntoViewRequester(requester)
                                .onFocusEvent { st ->
                                    if (st.isFocused) scope.launch {
                                        delay(350)
                                        requester.bringIntoView()
                                    }
                                },
                            minLines = 3
                        )
                        if (i == 3) {
                            Spacer(Modifier.height(8.dp))
                            Row(
                                Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                recExamples.forEach { ex ->
                                    AssistChip(
                                        onClick = {
                                            r = if (r.isBlank()) ex else r.trimEnd() + "\n" + ex
                                        },
                                        label = { Text(ex, fontSize = 12.sp) }
                                    )
                                }
                            }
                            Spacer(Modifier.height(4.dp))
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .clickable { repeated = !repeated },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(checked = repeated, onCheckedChange = { repeated = it })
                                Text(
                                    "I repeated back the reply to confirm what was agreed",
                                    fontSize = 13.sp,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = { clipboard.setText(AnnotatedString(summary())) },
                        enabled = values.any { it.isNotBlank() },
                        modifier = Modifier.weight(1f)
                    ) { Text("Copy summary") }
                    OutlinedButton(
                        onClick = { s = ""; b = ""; a = ""; r = ""; repeated = false },
                        modifier = Modifier.weight(1f)
                    ) { Text("Clear") }
                }
            }
            item {
                Text(
                    "Adapted from AHRQ TeamSTEPPS, Tool: SBAR (ahrq.gov). Wording is our own. Not affiliated with or endorsed by AHRQ or HHS.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
