package com.medtrixa.app.ui.screens.calculators

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.unit.sp
import com.medtrixa.app.ui.components.PremiumBackButton
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.medtrixa.app.domain.calculator.MapCalculator

@Composable
fun MapScreen(
    onBackClick: () -> Unit
) {
    BackHandler { onBackClick() }

    var sbpInput by remember { mutableStateOf("") }
    var dbpInput by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf<String?>(null) }
    var interpretationText by remember { mutableStateOf<String?>(null) }
    var errorText by remember { mutableStateOf<String?>(null) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                PremiumBackButton(onClick = onBackClick)
                Text(
                    text = "Mean Arterial Pressure",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge, fontSize = 20.sp, lineHeight = 26.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = sbpInput,
                onValueChange = { sbpInput = it },
                label = { Text("Systolic BP (mmHg)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = dbpInput,
                onValueChange = { dbpInput = it },
                label = { Text("Diastolic BP (mmHg)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    errorText = null
                    resultText = null
                    interpretationText = null
                    try {
                        val sbp = sbpInput.toDouble()
                        val dbp = dbpInput.toDouble()
                        val result = MapCalculator().calculate(
                            "map",
                            mapOf("sbp" to sbp, "dbp" to dbp)
                        )
                        resultText = "${result.value} ${result.unit}"
                        interpretationText = result.interpretation
                    } catch (e: Exception) {
                        errorText = e.message ?: "Invalid input"
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Outlined.Calculate, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Calculate")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (resultText != null) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Result: $resultText",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = interpretationText ?: "")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Formula: MAP = DBP + 1/3(SBP − DBP)",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            if (errorText != null) {
                Text(
                    text = errorText ?: "",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
