package com.medtrixa.app.ui.screens.assess

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun Cha2ds2VascScreen(
    onBackClick: () -> Unit
) {
    var heartFailure by remember { mutableStateOf(false) }
    var hypertension by remember { mutableStateOf(false) }
    var diabetes by remember { mutableStateOf(false) }
    var previousStroke by remember { mutableStateOf(false) }
    var vascularDisease by remember { mutableStateOf(false) }
    var femaleSex by remember { mutableStateOf(false) }
    var age by remember { mutableStateOf(0) }
    var calculatedScore by remember { mutableIntStateOf(-1) }

    val agePoints = when {
        age >= 75 -> 2
        age >= 65 -> 1
        else -> 0
    }

    val score = (if (heartFailure) 1 else 0) +
            (if (hypertension) 1 else 0) +
            agePoints +
            (if (diabetes) 1 else 0) +
            (if (previousStroke) 2 else 0) +
            (if (vascularDisease) 1 else 0) +
            (if (femaleSex) 1 else 0)

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
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }

            Column {
                Text(
                    text = "CHA₂DS₂-VASc",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "Stroke Risk in Atrial Fibrillation",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        RiskSwitch(
            title = "Congestive heart failure / LV dysfunction",
            points = 1,
            checked = heartFailure,
            onCheckedChange = { heartFailure = it }
        )

        RiskSwitch(
            title = "Hypertension",
            points = 1,
            checked = hypertension,
            onCheckedChange = { hypertension = it }
        )

        AgeCard(
            age = age,
            onAgeChange = {
                age = it.coerceAtLeast(0)
                calculatedScore = -1
            }
        )

        RiskSwitch(
            title = "Diabetes mellitus",
            points = 1,
            checked = diabetes,
            onCheckedChange = { diabetes = it }
        )

        RiskSwitch(
            title = "Previous stroke / TIA / thromboembolism",
            points = 2,
            checked = previousStroke,
            onCheckedChange = { previousStroke = it }
        )

        RiskSwitch(
            title = "Vascular disease",
            points = 1,
            checked = vascularDisease,
            onCheckedChange = { vascularDisease = it }
        )

        RiskSwitch(
            title = "Female sex",
            points = 1,
            checked = femaleSex,
            onCheckedChange = { femaleSex = it }
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (calculatedScore < 0) {
            Button(
                onClick = {
                    calculatedScore = score
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Calculate CHA₂DS₂-VASc")
            }
        } else {
            Button(
                onClick = {
                    calculatedScore = -1
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Edit Inputs")
            }
        }

        if (calculatedScore >= 0) {
            Spacer(modifier = Modifier.height(16.dp))

            val anticoagulationMessage = when {
                femaleSex && calculatedScore >= 3 ->
                    "High enough risk that anticoagulation is generally recommended."
                !femaleSex && calculatedScore >= 2 ->
                    "High enough risk that anticoagulation is generally recommended."
                femaleSex && calculatedScore == 2 ->
                    "Intermediate risk — anticoagulation may be reasonable."
                !femaleSex && calculatedScore == 1 ->
                    "Intermediate risk — anticoagulation may be reasonable."
                else ->
                    "Low risk — anticoagulation is generally not indicated based on this score alone."
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "CHA₂DS₂-VASc Score",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = calculatedScore.toString(),
                        style = MaterialTheme.typography.headlineLarge
                    )

                    Text(
                        text = anticoagulationMessage,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
    }
}

@Composable
private fun RiskSwitch(
    title: String,
    points: Int,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "$points point${if (points > 1) "s" else ""}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

@Composable
private fun AgeCard(
    age: Int,
    enabled: Boolean = true,
    onAgeChange: (Int) -> Unit
) {
    var ageText by rememberSaveable {
        mutableStateOf(if (age == 0) "" else age.toString())
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Age",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = ageText,
                onValueChange = { value ->
                    val filtered = value.filter { it.isDigit() }.take(3)
                    ageText = filtered

                    val enteredAge = filtered.toIntOrNull()

                    when {
                        enteredAge != null && enteredAge <= 120 ->
                            onAgeChange(enteredAge)

                        filtered.isEmpty() ->
                            onAgeChange(0)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("Enter patient's age")
                },
                singleLine = true,
                enabled = enabled,
                readOnly = !enabled,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = when {
                    age >= 75 -> "Age ≥75: 2 points"
                    age >= 65 -> "Age 65–74: 1 point"
                    else -> "Age <65: 0 points"
                },
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
