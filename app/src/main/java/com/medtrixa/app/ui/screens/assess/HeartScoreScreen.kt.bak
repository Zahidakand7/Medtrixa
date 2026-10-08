package com.medtrixa.app.ui.screens.assess

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.medtrixa.app.domain.scoring.HeartScoreCalculator

@Composable
fun HeartScoreScreen(
    onBackClick: () -> Unit
) {
    BackHandler { onBackClick() }

    var history by remember { mutableStateOf("") }
    var ecg by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var riskFactors by remember { mutableStateOf("") }
    var troponin by remember { mutableStateOf("") }
    var atherosclerotic by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<HeartScoreCalculator.Result?>(null) }

    fun resetResult() {
        result = null
    }

    val historyValue = history.toIntOrNull()
    val ecgValue = ecg.toIntOrNull()
    val ageValue = age.toIntOrNull()
    val riskValue = riskFactors.toIntOrNull()
    val troponinValue = troponin.toDoubleOrNull()

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
                        text = "HEART Score",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Acute Chest Pain Risk Assessment",
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

            OutlinedTextField(
                value = history,
                onValueChange = {
                    history = it.filter(Char::isDigit)
                    resetResult()
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("History") },
                suffix = { Text("0–2") }
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = ecg,
                    onValueChange = {
                        ecg = it.filter(Char::isDigit)
                        resetResult()
                    },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    label = { Text("ECG") },
                    suffix = { Text("0–2") }
                )

                OutlinedTextField(
                    value = age,
                    onValueChange = {
                        age = it.filter(Char::isDigit)
                        resetResult()
                    },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    label = { Text("Age") },
                    suffix = { Text("years") }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = riskFactors,
                    onValueChange = {
                        riskFactors = it.filter(Char::isDigit)
                        resetResult()
                    },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    label = { Text("Risk Factors") },
                    suffix = { Text("count") }
                )

                OutlinedTextField(
                    value = troponin,
                    onValueChange = {
                        troponin = it
                        resetResult()
                    },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    label = { Text("Troponin") },
                    suffix = { Text("× normal") }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = atherosclerotic,
                    onCheckedChange = {
                        atherosclerotic = it
                        resetResult()
                    }
                )

                Text(
                    text = "Known atherosclerotic disease",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    if (
                        historyValue != null &&
                        ecgValue != null &&
                        ageValue != null &&
                        riskValue != null &&
                        troponinValue != null
                    ) {
                        result = HeartScoreCalculator.calculate(
                            historyValue,
                            ecgValue,
                            ageValue,
                            riskValue,
                            atherosclerotic,
                            troponinValue
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled =
                    historyValue != null &&
                    ecgValue != null &&
                    ageValue != null &&
                    riskValue != null &&
                    troponinValue != null
            ) {
                Icon(
                    imageVector = Icons.Outlined.Assessment,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.padding(start = 4.dp))

                Text("Calculate")
            }

            result?.let {
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
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "HEART Score",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = it.total.toString(),
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = it.category,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
