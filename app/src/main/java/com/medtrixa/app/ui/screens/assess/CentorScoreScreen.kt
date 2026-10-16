package com.medtrixa.app.ui.screens.assess

import androidx.activity.compose.BackHandler
import androidx.compose.ui.unit.sp
import com.medtrixa.app.ui.components.PremiumBackButton
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.medtrixa.app.domain.scoring.CentorScoreCalculator

@Composable
fun CentorScoreScreen(
    onBackClick: () -> Unit
) {
    BackHandler { onBackClick() }

    var ageGroup by remember { mutableStateOf<CentorScoreCalculator.AgeGroup?>(null) }
    var exudate by remember { mutableStateOf(false) }
    var nodes by remember { mutableStateOf(false) }
    var noCough by remember { mutableStateOf(false) }
    var fever by remember { mutableStateOf(false) }

    var result by remember {
        mutableStateOf<CentorScoreCalculator.Result?>(null)
    }

    fun resetResult() {
        result = null
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PremiumBackButton(onClick = onBackClick)

                Column() {
                    Text(
                        text = "Centor Score",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 26.sp
                    )

                    Text(
                        text = "Streptococcal Pharyngitis Assessment",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Clinical Parameters",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Age (McIsaac modification)",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )

            CentorCalcAgeRow(
                label = "3\u201314 years  (+1)",
                selected = ageGroup == CentorScoreCalculator.AgeGroup.CHILD
            ) {
                ageGroup = CentorScoreCalculator.AgeGroup.CHILD
                resetResult()
            }
            CentorCalcAgeRow(
                label = "15\u201344 years  (0)",
                selected = ageGroup == CentorScoreCalculator.AgeGroup.ADULT
            ) {
                ageGroup = CentorScoreCalculator.AgeGroup.ADULT
                resetResult()
            }
            CentorCalcAgeRow(
                label = "\u226545 years  (\u22121)",
                selected = ageGroup == CentorScoreCalculator.AgeGroup.OLDER
            ) {
                ageGroup = CentorScoreCalculator.AgeGroup.OLDER
                resetResult()
            }

            Text(
                text = "Not validated for children under 3 years.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 12.dp, top = 2.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            CentorCheckRow(
                title = "Tonsillar swelling or exudate",
                checked = exudate,
                onCheckedChange = {
                    exudate = it
                    resetResult()
                }
            )

            CentorCheckRow(
                title = "Tender/swollen anterior cervical lymphadenopathy",
                checked = nodes,
                onCheckedChange = {
                    nodes = it
                    resetResult()
                }
            )

            CentorCheckRow(
                title = "Absence of cough",
                checked = noCough,
                onCheckedChange = {
                    noCough = it
                    resetResult()
                }
            )

            CentorCheckRow(
                title = "History of fever (>38 \u00B0C / 100.4 \u00B0F)",
                checked = fever,
                onCheckedChange = {
                    fever = it
                    resetResult()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    result = CentorScoreCalculator.calculate(
                        exudate,
                        nodes,
                        noCough,
                        fever,
                        ageGroup ?: CentorScoreCalculator.AgeGroup.ADULT
                    )
                },
                enabled = ageGroup != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Outlined.Assessment,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.padding(start = 4.dp))

                Text("Calculate")
            }

            result?.let { r ->
                Spacer(modifier = Modifier.height(18.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    )
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Modified Centor (McIsaac) Score",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = r.total.toString(),
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = r.interpretation,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Estimated GAS probability: ${r.riskRange}",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                CentorSection(
                    title = "Suggested Approach",
                    body = r.recommendation
                )
                CentorSection(
                    title = "Clinical Caveat",
                    body = "The score estimates the probability of Group A streptococcal pharyngitis and does not replace clinical judgment. It does not account for local epidemiology, known exposure, suspected complications or immunocompromise. Management should follow current local guidelines."
                )
                CentorSection(
                    title = "References",
                    body = "Centor RM et al. Med Decis Making 1981;1:239-246. McIsaac WJ et al. CMAJ 1998;158:75-83. Shulman ST et al. Clin Infect Dis 2012;55:e86-e102 (IDSA guideline). Harris AM et al. Ann Intern Med 2016;164:425-434 (ACP/CDC guidance). Probability ranges are derived from the McIsaac validation cohorts."
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CentorCalcAgeRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun CentorSection(title: String, body: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CentorCheckRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )

        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
