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
fun ChildBmiScreen(
    onBackClick: () -> Unit
) {
    BackHandler { onBackClick() }

    val context = LocalContext.current

    var ageYears by remember { mutableStateOf("") }
    var ageMonths by remember { mutableStateOf("") }
    var sex by remember { mutableStateOf<String?>(null) }

    var heightUnit by remember { mutableStateOf("cm") }
    var weightUnit by remember { mutableStateOf("kg") }

    var heightCm by remember { mutableStateOf("") }
    var heightFt by remember { mutableStateOf("") }
    var heightIn by remember { mutableStateOf("") }
    var weightKg by remember { mutableStateOf("") }
    var weightLb by remember { mutableStateOf("") }

    var result by remember { mutableStateOf<String?>(null) }
    var interpretation by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun resetResult() {
        result = null
        interpretation = null
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
                        text = "Child & Adolescent BMI",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "BMI-for-Age Assessment",
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

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Age",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = ageYears,
                    onValueChange = {
                        ageYears = it
                        resetResult()
                    },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    label = { Text("Years") },
                    suffix = { Text("yr") }
                )

                OutlinedTextField(
                    value = ageMonths,
                    onValueChange = {
                        ageMonths = it
                        resetResult()
                    },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    label = { Text("Months") },
                    suffix = { Text("mo") }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

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
                    selected = sex == "Male",
                    onClick = {
                        sex = "Male"
                        resetResult()
                    },
                    label = { Text("Male") }
                )

                FilterChip(
                    selected = sex == "Female",
                    onClick = {
                        sex = "Female"
                        resetResult()
                    },
                    label = { Text("Female") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

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
                    label = { Text("Height") },
                    suffix = { Text("cm") }
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
                        label = { Text("Feet") },
                        suffix = { Text("ft") }
                    )

                    OutlinedTextField(
                        value = heightIn,
                        onValueChange = {
                            heightIn = it
                            resetResult()
                        },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        label = { Text("Inches") },
                        suffix = { Text("in") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

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
                    label = { Text("Weight") },
                    suffix = { Text("kg") }
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
                    label = { Text("Weight") },
                    suffix = { Text("lb") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    try {
                        val years = ageYears.toDoubleOrNull()
                        val months = if (ageMonths.isBlank()) {
                            0.0
                        } else {
                            ageMonths.toDoubleOrNull()
                        }

                        if (years == null || months == null) {
                            error("Please enter a valid age")
                        }

                        val selectedSex = when (sex) {
                            "Male" -> 1.0
                            "Female" -> 2.0
                            else -> error("Please select sex")
                        }

                        val inputs = mutableMapOf<String, Double>(
                            "age_years" to years!!,
                            "age_months" to months!!,
                            "sex" to selectedSex
                        )

                        if (heightUnit == "cm") {
                            inputs["height_cm"] =
                                heightCm.toDoubleOrNull()
                                    ?: error("Please enter height")
                        } else {
                            inputs["height_ft"] =
                                heightFt.toDoubleOrNull()
                                    ?: error("Please enter feet")

                            inputs["height_in"] =
                                heightIn.toDoubleOrNull()
                                    ?: error("Please enter inches")
                        }

                        if (weightUnit == "kg") {
                            inputs["weight_kg"] =
                                weightKg.toDoubleOrNull()
                                    ?: error("Please enter weight")
                        } else {
                            inputs["weight_lb"] =
                                weightLb.toDoubleOrNull()
                                    ?: error("Please enter weight")
                        }

                        val calculation = ChildBmiCalculator(context).calculate(
                            calculatorId = "bmi_child",
                            inputs = inputs
                        )

                        result = calculation.value
                        interpretation = calculation.interpretation
                        errorMessage = null
                    } catch (e: Exception) {
                        result = null
                        interpretation = null
                        errorMessage = e.message ?: "Unable to calculate BMI"
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

            result?.let { bmi ->
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
                            text = "BMI",
                            style = MaterialTheme.typography.labelLarge
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = bmi,
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = "kg/m²",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        interpretation?.let {
                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = it,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
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
                            text = "BMI-for-age is a screening measure and should be interpreted with other clinical information. This calculator is intended for children and adolescents aged 2 through 19 years.",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Reference: CDC BMI-for-Age Growth Charts • Version 1.0 • Reviewed 12 Sep 2026",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = {
                        ageYears = ""
                        ageMonths = ""
                        sex = null
                        heightUnit = "cm"
                        weightUnit = "kg"
                        heightCm = ""
                        heightFt = ""
                        heightIn = ""
                        weightKg = ""
                        weightLb = ""
                        result = null
                        interpretation = null
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
}
