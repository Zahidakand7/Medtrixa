package com.medtrixa.app.ui.screens.calculators

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.medtrixa.app.domain.calculator.CreatinineClearanceCalculator

@Composable
fun CreatinineClearanceScreen(
    onBackClick: () -> Unit
) {
    BackHandler { onBackClick() }

    var age by remember { mutableStateOf("") }
    var sex by remember { mutableStateOf("male") }
    var weight by remember { mutableStateOf("") }
    var serumCreatinine by remember { mutableStateOf("") }

    var result by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun resetResult() {
        result = null
        errorMessage = null
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
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
                        text = "Creatinine Clearance",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Cockcroft–Gault Calculation",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Patient Information",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Age",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = age,
                onValueChange = {
                    age = it
                    resetResult()
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Age") },
                suffix = { Text("years") }
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Sex",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = sex == "male",
                    onClick = {
                        sex = "male"
                        resetResult()
                    },
                    label = { Text("Male") }
                )

                FilterChip(
                    selected = sex == "female",
                    onClick = {
                        sex = "female"
                        resetResult()
                    },
                    label = { Text("Female") }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Weight",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = weight,
                onValueChange = {
                    weight = it
                    resetResult()
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Weight") },
                suffix = { Text("kg") }
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Serum Creatinine",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = serumCreatinine,
                onValueChange = {
                    serumCreatinine = it
                    resetResult()
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Serum Creatinine") },
                suffix = { Text("mg/dL") }
            )

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = {
                    try {
                        val inputs = mapOf(
                            "age_years" to (
                                age.toDoubleOrNull()
                                    ?: error("Please enter age")
                                ),
                            "sex" to if (sex == "female") 2.0 else 1.0,
                            "weight_kg" to (
                                weight.toDoubleOrNull()
                                    ?: error("Please enter weight")
                                ),
                            "serum_creatinine" to (
                                serumCreatinine.toDoubleOrNull()
                                    ?: error("Please enter serum creatinine")
                                )
                        )

                        val calculation =
                            CreatinineClearanceCalculator().calculate(
                                calculatorId = "creatinine_clearance",
                                inputs = inputs
                            )

                        result = calculation.value
                        errorMessage = null
                    } catch (e: Exception) {
                        result = null
                        errorMessage =
                            e.message ?: "Unable to calculate creatinine clearance"
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Calculate,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text("Calculate")
            }

            errorMessage?.let {
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            result?.let { clearance ->
                Spacer(modifier = Modifier.height(18.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 3.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Creatinine Clearance",
                            style = MaterialTheme.typography.labelLarge
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = clearance,
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = "mL/min",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Cockcroft–Gault Equation",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Creatinine clearance is an estimate of renal filtration based on age, body weight, sex, and serum creatinine. Clinical context and institutional guidance should be considered.",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Formula: CrCl = ((140 − Age) × Weight) / (72 × SCr) × 0.85 if female",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Reference: Cockcroft DW • Gault MH • Version 1.0 • Reviewed 12 Sep 2026",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = {
                    age = ""
                    sex = "male"
                    weight = ""
                    serumCreatinine = ""
                    result = null
                    errorMessage = null
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Reset")
            }
        }
    }
}
