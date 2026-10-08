package com.medtrixa.app.ui.screens.assess

import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.dp
import com.medtrixa.app.domain.scoring.Meld30Calculator

@Composable
fun Meld30Screen(
    onBackClick: () -> Unit
) {
    BackHandler { onBackClick() }

    var bilirubin by remember { mutableStateOf("") }
    var sodium by remember { mutableStateOf("") }
    var inr by remember { mutableStateOf("") }
    var creatinine by remember { mutableStateOf("") }
    var albumin by remember { mutableStateOf("") }

    var female by remember { mutableStateOf(false) }
    var dialysis by remember { mutableStateOf(false) }
    var cvvhd by remember { mutableStateOf(false) }

    var result by remember { mutableStateOf<Meld30Calculator.Result?>(null) }

    fun resetResult() {
        result = null
    }

    val bilirubinValue = bilirubin.toDoubleOrNull()
    val sodiumValue = sodium.toDoubleOrNull()
    val inrValue = inr.toDoubleOrNull()
    val creatinineValue = creatinine.toDoubleOrNull()
    val albuminValue = albumin.toDoubleOrNull()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBack,
                        contentDescription = "Back"
                    )
                }

                Column(
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    Text(
                        text = "MELD 3.0",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Liver Disease Severity Assessment",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Laboratory Parameters",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = bilirubin,
                onValueChange = {
                    bilirubin = it
                    resetResult()
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Bilirubin") },
                suffix = { Text("mg/dL") }
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = sodium,
                    onValueChange = {
                        sodium = it
                        resetResult()
                    },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    label = { Text("Sodium") },
                    suffix = { Text("mmol/L") }
                )

                OutlinedTextField(
                    value = inr,
                    onValueChange = {
                        inr = it
                        resetResult()
                    },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    label = { Text("INR") }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = creatinine,
                    onValueChange = {
                        creatinine = it
                        resetResult()
                    },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    label = { Text("Creatinine") },
                    suffix = { Text("mg/dL") }
                )

                OutlinedTextField(
                    value = albumin,
                    onValueChange = {
                        albumin = it
                        resetResult()
                    },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    label = { Text("Albumin") },
                    suffix = { Text("g/dL") }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            MeldCheckboxRow(
                title = "Female",
                checked = female,
                onCheckedChange = {
                    female = it
                    resetResult()
                }
            )

            MeldCheckboxRow(
                title = "Dialysis ≥2 times in last 7 days",
                checked = dialysis,
                onCheckedChange = {
                    dialysis = it
                    resetResult()
                }
            )

            MeldCheckboxRow(
                title = "CVVHD ≥24 hours in last 7 days",
                checked = cvvhd,
                onCheckedChange = {
                    cvvhd = it
                    resetResult()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (
                        bilirubinValue != null &&
                        sodiumValue != null &&
                        inrValue != null &&
                        creatinineValue != null &&
                        albuminValue != null
                    ) {
                        result = Meld30Calculator.calculate(
                            bilirubinMgDl = bilirubinValue,
                            sodiumMmolL = sodiumValue,
                            inr = inrValue,
                            creatinineMgDl = creatinineValue,
                            albuminGDl = albuminValue,
                            female = female,
                            dialysisTwoOrMoreTimesLast7Days = dialysis,
                            cvvhd24HoursLast7Days = cvvhd
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled =
                    bilirubinValue != null &&
                    sodiumValue != null &&
                    inrValue != null &&
                    creatinineValue != null &&
                    albuminValue != null
            ) {
                Icon(
                    imageVector = Icons.Outlined.Assessment,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.padding(start = 4.dp))

                Text("Calculate")
            }

            result?.let { meldResult ->
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
                            text = "MELD 3.0 Score",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = meldResult.score.toString(),
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold
                        )

                        val scoreValue = meldResult.score.toString().toDoubleOrNull()
                        if (scoreValue != null) {
                            val (bandLabel, bandColor, impact) = when {
                                scoreValue >= 40 -> Triple("Maximum score", Color(0xFF8E1B1B), "Highest-risk range. MELD 3.0 is capped at 40.")
                                scoreValue >= 30 -> Triple("Very high risk", Color(0xFFC0392B), "Very high short-term mortality. Needs urgent specialist management and high transplant priority.")
                                scoreValue >= 20 -> Triple("High risk", Color(0xFFD9622B), "High short-term mortality. Transplant evaluation and listing are generally indicated.")
                                scoreValue >= 10 -> Triple("Moderate risk", Color(0xFFC27A00), "Intermediate short-term mortality. Transplant referral is usually considered from MELD 15 or after a decompensation event.")
                                else -> Triple("Low risk", Color(0xFF1F9D6B), "Lowest short-term mortality. Routine hepatology follow-up.")
                            }
                            val mortality = (1.0 - Math.pow(0.946, Math.exp(0.17698 * scoreValue - 3.56))) * 100.0
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
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Estimated 90-day mortality: " + "%.1f".format(mortality) + "%",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = impact,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Population-based estimate (Kim et al., Gastroenterology 2021), not an individual prediction. Use with clinical judgement.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MeldCheckboxRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
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
