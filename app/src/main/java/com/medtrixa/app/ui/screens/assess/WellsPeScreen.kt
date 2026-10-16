package com.medtrixa.app.ui.screens.assess

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun WellsPeScreen(
    onBackClick: () -> Unit
) {
    BackHandler { onBackClick() }

    val titles = listOf(
        "Clinical signs of DVT",
        "PE is the leading diagnosis",
        "Heart rate above 100 per minute",
        "Immobilisation or recent surgery",
        "Previous PE or DVT",
        "Haemoptysis",
        "Malignancy"
    )
    val subtitles = listOf(
        "Leg swelling and pain on palpation of the deep veins",
        "An alternative diagnosis is less likely than PE",
        "Resting heart rate",
        "Immobilised 3 or more days, or surgery in the previous 4 weeks",
        "Prior objectively confirmed event",
        "Coughing up blood",
        "On treatment, treated within 6 months, or palliative"
    )
    val points = listOf(3.0, 3.0, 1.5, 1.5, 1.5, 1.0, 1.0)
    val answers = remember { mutableStateListOf<Boolean?>().apply { repeat(7) { add(null) } } }
    var calculated by remember { mutableStateOf(false) }

    val ready = answers.all { it != null }
    val score = points.indices.sumOf { if (answers[it] == true) points[it] else 0.0 }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            RiskHeader("Wells Score — PE", "Pretest Probability for Pulmonary Embolism", onBackClick)
            Spacer(modifier = Modifier.height(16.dp))

            titles.forEachIndexed { i, title ->
                val p = points[i]
                val pText = if (p == 1.0) "1 point" else (if (p == p.toInt().toDouble()) "${p.toInt()} points" else "$p points")
                YesNoRow(
                    title = title,
                    subtitle = "$pText · " + subtitles[i],
                    value = answers[i],
                    onChange = { answers[i] = it; calculated = false }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(10.dp))
            RiskCalculateButton(enabled = ready) { calculated = true }

            if (calculated && ready) {
                Spacer(modifier = Modifier.height(16.dp))

                val tier = when {
                    score > 6.0 -> 2
                    score >= 2.0 -> 1
                    else -> 0
                }
                val bandLabel = listOf("Low probability", "Moderate probability", "High probability")[tier]
                val bandColor = listOf(Color(0xFF1F9D6B), Color(0xFFC27A00), Color(0xFFC0392B))[tier]
                val prevalence = listOf("about 1 to 4%", "about 16 to 21%", "about 41 to 67%")[tier]
                val likely = score > 4.0
                val guidance = if (likely) {
                    "PE likely (score above 4). Proceed to CT pulmonary angiography. If imaging is delayed, start interim anticoagulation per local protocol unless contraindicated. D-dimer is not used to exclude PE in this group."
                } else {
                    "PE unlikely (score 4 or less). Check D-dimer: a negative result excludes PE. In patients over 50, use an age-adjusted cut-off (age × 10 µg/L FEU). If D-dimer is positive, proceed to CT pulmonary angiography."
                }

                RiskResultCard(
                    heading = "Wells Score (PE)",
                    scoreText = "%.1f".format(score),
                    bandLabel = bandLabel,
                    bandColor = bandColor
                ) {
                    Text(guidance, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
                    RiskNote(
                        "Pretest probability",
                        "Three-tier model: low (below 2), moderate (2 to 6), high (above 6). PE prevalence in this band was $prevalence in the derivation and validation cohorts. Two-tier model: PE unlikely at 4 or less, likely above 4."
                    )
                    if (tier == 0) {
                        RiskNote(
                            "PERC rule",
                            "With a low clinical probability, PE can be excluded without D-dimer if all eight PERC criteria are negative: age under 50, heart rate below 100, SpO₂ 95% or higher, no haemoptysis, no oestrogen use, no prior DVT or PE, no unilateral leg swelling, and no surgery or trauma in the past 4 weeks."
                        )
                    }
                    RiskNote(
                        "Interpret with caution",
                        "Not for haemodynamically unstable patients: suspected high-risk PE needs urgent echocardiography or CT angiography and a separate pathway. In pregnancy use pregnancy-adapted algorithms (for example YEARS) rather than Wells. The first two items carry the most weight and the leading-diagnosis item is a clinical judgement."
                    )
                    RiskReference("Wells PS et al. Thromb Haemost 2000;83:416-420. Wells PS et al. Ann Intern Med 2001;135:98-107. Righini M et al. JAMA 2014;311:1117-1124. Konstantinides SV et al. Eur Heart J 2020;41:543-603. Use with clinical judgement.")
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
