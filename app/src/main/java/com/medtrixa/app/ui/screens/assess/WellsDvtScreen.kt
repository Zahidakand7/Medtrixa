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
fun WellsDvtScreen(
    onBackClick: () -> Unit
) {
    BackHandler { onBackClick() }

    val titles = listOf(
        "Active cancer",
        "Paralysis, paresis or recent plaster immobilisation of the leg",
        "Recently bedridden or major surgery",
        "Localised tenderness along the deep venous system",
        "Entire leg swollen",
        "Calf swelling more than 3 cm versus the other leg",
        "Pitting oedema confined to the symptomatic leg",
        "Collateral superficial veins",
        "Previously documented DVT",
        "Alternative diagnosis at least as likely as DVT"
    )
    val subtitles = listOf(
        "Treatment within 6 months, or palliative",
        "Lower extremity",
        "Bedridden 3 or more days, or major surgery within 12 weeks under general or regional anaesthesia",
        "Along the distribution of the deep veins",
        "Whole limb swelling",
        "Measured 10 cm below the tibial tuberosity",
        "Symptomatic leg only",
        "Non-varicose",
        "Prior objectively confirmed DVT",
        "For example cellulitis, Baker's cyst, muscle injury or superficial thrombophlebitis"
    )
    val points = listOf(1, 1, 1, 1, 1, 1, 1, 1, 1, -2)
    val answers = remember { mutableStateListOf<Boolean?>().apply { repeat(10) { add(null) } } }
    var calculated by remember { mutableStateOf(false) }

    val ready = answers.all { it != null }
    val score = points.indices.sumOf { if (answers[it] == true) points[it] else 0 }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            RiskHeader("Wells Score — DVT", "Pretest Probability for Deep Vein Thrombosis", onBackClick)
            Spacer(modifier = Modifier.height(16.dp))

            titles.forEachIndexed { i, title ->
                val p = points[i]
                YesNoRow(
                    title = title,
                    subtitle = (if (p < 0) "−2 points · " else "+1 point · ") + subtitles[i],
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
                    score >= 3 -> 2
                    score >= 1 -> 1
                    else -> 0
                }
                val bandLabel = listOf("Low probability", "Moderate probability", "High probability")[tier]
                val bandColor = listOf(Color(0xFF1F9D6B), Color(0xFFC27A00), Color(0xFFC0392B))[tier]
                val prevalence = listOf("about 5%", "about 17%", "about 53%")[tier]
                val likely = score >= 2
                val guidance = if (likely) {
                    "DVT likely (score 2 or more). Proceed to compression ultrasound. If imaging is delayed, consider interim anticoagulation per local protocol. If the ultrasound is negative, consider D-dimer testing or repeat imaging in about a week."
                } else {
                    "DVT unlikely (score 1 or less). Check D-dimer: a negative result excludes DVT. If D-dimer is positive, proceed to compression ultrasound."
                }

                RiskResultCard(
                    heading = "Wells Score (DVT)",
                    scoreText = "$score",
                    bandLabel = bandLabel,
                    bandColor = bandColor
                ) {
                    Text(guidance, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
                    RiskNote(
                        "Pretest probability",
                        "Three-tier model: low (0 or less), moderate (1 to 2), high (3 or more). Prevalence of DVT in this band was $prevalence in the validation cohort. Two-tier model: DVT unlikely at 1 or less, likely at 2 or more."
                    )
                    RiskNote(
                        "Interpret with caution",
                        "Validated in outpatients with suspected lower-limb DVT. Not validated for upper-limb DVT or in pregnancy. D-dimer is non-specific and can be raised in cancer, infection, recent surgery, pregnancy and older age. The alternative-diagnosis item is a clinical judgement, so reassess if the picture changes."
                    )
                    RiskReference("Wells PS et al. Lancet 1997;350:1795-1798. Wells PS et al. N Engl J Med 2003;349:1227-1235. Bates SM et al. Chest 2012;141(2 Suppl):e351S-e418S. NICE NG158 (2020). Use with clinical judgement.")
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
