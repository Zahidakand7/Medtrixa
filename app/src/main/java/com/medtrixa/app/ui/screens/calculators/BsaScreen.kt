package com.medtrixa.app.ui.screens.calculators

import android.content.Context
import androidx.compose.ui.unit.sp
import com.medtrixa.app.ui.components.PremiumBackButton
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
fun BsaScreen(
    onBackClick: () -> Unit
) {
    BackHandler { onBackClick() }

    val context = LocalContext.current

    var heightUnit by remember { mutableStateOf("cm") }
    var weightUnit by remember { mutableStateOf("kg") }

    var heightCm by remember { mutableStateOf("") }
    var heightFt by remember { mutableStateOf("") }
    var heightIn by remember { mutableStateOf("") }
    var weightKg by remember { mutableStateOf("") }
    var weightLb by remember { mutableStateOf("") }

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
                PremiumBackButton(onClick = onBackClick)

                Column() {
                    Text(
                        text = "Body Surface Area",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 26.sp
                    )

                    Text(
                        text = "Mosteller Calculation",
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

            Spacer(modifier = Modifier.height(12.dp))

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

            Spacer(modifier = Modifier.height(14.dp))

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

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = {
                    try {
                        val inputs = mutableMapOf<String, Double>()

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

                        val calculation = BsaCalculator().calculate(
                            calculatorId = "bsa",
                            inputs = inputs
                        )

                        result = calculation.value
                        errorMessage = null
                    } catch (e: Exception) {
                        result = null
                        errorMessage = e.message ?: "Unable to calculate BSA"
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

            result?.let { bsa ->
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
                            text = "Body Surface Area",
                            style = MaterialTheme.typography.labelLarge
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = bsa,
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = "m²",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Mosteller Formula",
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
                            text = "BSA is commonly used in clinical calculations such as medication dosing and physiological assessment. Clinical context and institutional guidance should be considered.",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Formula: BSA = √(Height × Weight / 3600)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Reference: Mosteller RD • Version 1.0 • Reviewed 12 Sep 2026",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

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
