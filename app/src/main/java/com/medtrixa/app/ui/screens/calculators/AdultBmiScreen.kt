package com.medtrixa.app.ui.screens.calculators

import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.activity.compose.BackHandler
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.medtrixa.app.domain.calculator.BmiCalculator
import com.medtrixa.app.domain.calculator.ChildBmiCalculator
import com.medtrixa.app.domain.calculator.BsaCalculator

@Composable
fun AdultBmiScreen(
    calculatorId: String,
    onBackClick: () -> Unit
) {
    BackHandler { onBackClick() }

    var heightUnit by remember { mutableStateOf("cm") }
    var weightUnit by remember { mutableStateOf("kg") }

    var heightCm by remember { mutableStateOf("") }
    var heightFt by remember { mutableStateOf("") }
    var heightIn by remember { mutableStateOf("") }

    var weightKg by remember { mutableStateOf("") }
    var weightLb by remember { mutableStateOf("") }

    var result by remember { mutableStateOf<String?>(null) }
    var category by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isCalculated by remember { mutableStateOf(false) }

    fun resetResult() {
        result = null
        category = null
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
                IconButton(
                    onClick = onBackClick
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBack,
                        contentDescription = "Back"
                    )
                }

                Column(
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    Text(
                        text = "Adult BMI",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Body Mass Index",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Patient Measurements",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Height",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = heightUnit == "cm",
                    onClick = {
                        heightUnit = "cm"
                        resetResult()
                    },
                    label = { Text("cm") }
                )

                FilterChip(
                    selected = heightUnit == "ft/in",
                    onClick = {
                        heightUnit = "ft/in"
                        resetResult()
                    },
                    label = { Text("ft + in") }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (heightUnit == "cm") {

                OutlinedTextField(
                    value = heightCm,
                    onValueChange = {
                        heightCm = it
                        resetResult()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = {
                        Text("Height")
                    },
                    suffix = {
                        Text("cm")
                    }
                )

            } else {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = heightFt,
                        onValueChange = {
                            heightFt = it
                            resetResult()
                        },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        label = {
                            Text("Feet")
                        },
                        suffix = {
                            Text("ft")
                        }
                    )

                    OutlinedTextField(
                        value = heightIn,
                        onValueChange = {
                            heightIn = it
                            resetResult()
                        },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        label = {
                            Text("Inches")
                        },
                        suffix = {
                            Text("in")
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Weight",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = weightUnit == "kg",
                    onClick = {
                        weightUnit = "kg"
                        resetResult()
                    },
                    label = { Text("kg") }
                )

                FilterChip(
                    selected = weightUnit == "lb",
                    onClick = {
                        weightUnit = "lb"
                        resetResult()
                    },
                    label = { Text("lb") }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (weightUnit == "kg") {

                OutlinedTextField(
                    value = weightKg,
                    onValueChange = {
                        weightKg = it
                        resetResult()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = {
                        Text("Weight")
                    },
                    suffix = {
                        Text("kg")
                    }
                )

            } else {

                OutlinedTextField(
                    value = weightLb,
                    onValueChange = {
                        weightLb = it
                        resetResult()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = {
                        Text("Weight")
                    },
                    suffix = {
                        Text("lb")
                    }
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = {

                    try {
                        val inputs = mutableMapOf<String, Double>()

                        if (heightUnit == "cm") {
                            val value = heightCm.toDoubleOrNull()

                            if (value == null || value <= 0) {
                                errorMessage = "Enter a valid height."
                                result = null
                                category = null
                                return@Button
                            }

                            inputs["height_cm"] = value

                        } else {
                            val feet = heightFt.toDoubleOrNull()
                            val inches = heightIn.toDoubleOrNull()

                            if (feet == null || feet < 0) {
                                errorMessage = "Enter valid feet."
                                result = null
                                category = null
                                return@Button
                            }

                            if (inches == null || inches < 0 || inches >= 12) {
                                errorMessage = "Enter inches from 0 to less than 12."
                                result = null
                                category = null
                                return@Button
                            }

                            if (feet == 0.0 && inches == 0.0) {
                                errorMessage = "Height must be greater than zero."
                                result = null
                                category = null
                                return@Button
                            }

                            inputs["height_ft"] = feet
                            inputs["height_in"] = inches
                        }

                        if (weightUnit == "kg") {
                            val value = weightKg.toDoubleOrNull()

                            if (value == null || value <= 0) {
                                errorMessage = "Enter a valid weight."
                                result = null
                                category = null
                                return@Button
                            }

                            inputs["weight_kg"] = value

                        } else {
                            val value = weightLb.toDoubleOrNull()

                            if (value == null || value <= 0) {
                                errorMessage = "Enter a valid weight."
                                result = null
                                category = null
                                return@Button
                            }

                            inputs["weight_lb"] = value
                        }

                        val calculation = BmiCalculator().calculate(
                            calculatorId = calculatorId,
                            inputs = inputs
                        )

                        result = calculation.value
                        category = calculation.interpretation
                        errorMessage = null

                    } catch (exception: Exception) {
                        errorMessage = exception.message
                            ?: "Unable to calculate BMI."
                        result = null
                        category = null
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

                Text(if (isCalculated) "New Calculation" else "Calculate BMI")
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (result != null && category != null) {

                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 3.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "BMI",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = result!!,
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = "kg/m²",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = category!!,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp)
                    ) {
                        Text(
                            text = "Interpretation",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = when (category) {
                                "Underweight" ->
                                    "BMI is below 18.5 kg/m²."

                                "Healthy Weight" ->
                                    "BMI is within the healthy-weight range for adults."

                                "Overweight" ->
                                    "BMI is within the overweight range for adults."

                                "Obesity — Class 1" ->
                                    "BMI is within obesity Class 1."

                                "Obesity — Class 2" ->
                                    "BMI is within obesity Class 2."

                                else ->
                                    "BMI is within obesity Class 3."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "BMI is a screening measure and should be interpreted with other clinical information. This calculator is intended for adults aged 20 years and older.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Reference: CDC Adult BMI Categories • Version 1.0 • Reviewed 12 Sep 2026",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedButton(
                    onClick = {
                        heightUnit = "cm"
                        weightUnit = "kg"
                        heightCm = ""
                        heightFt = ""
                        heightIn = ""
                        weightKg = ""
                        weightLb = ""
                        result = null
                        category = null
                        errorMessage = null
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Reset")
                }
            }
        }
    }
}
