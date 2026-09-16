package com.medtrixa.app.ui.screens.assess

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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

@Composable
fun Curb65Screen(
    onBackClick: () -> Unit
) {
    BackHandler { onBackClick() }

    var confusion by remember { mutableStateOf(false) }
    var ureaHigh by remember { mutableStateOf(false) }
    var respiratoryRate by remember { mutableStateOf("") }
    var systolicBp by remember { mutableStateOf("") }
    var diastolicBp by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }

    var calculated by remember { mutableStateOf(false) }

    fun resetResult() {
        calculated = false
    }

    val rr = respiratoryRate.toIntOrNull()
    val sbp = systolicBp.toIntOrNull()
    val dbp = diastolicBp.toIntOrNull()
    val patientAge = age.toIntOrNull()

    val score = (if (confusion) 1 else 0) +
        (if (ureaHigh) 1 else 0) +
        (if (rr != null && rr >= 30) 1 else 0) +
        (if ((sbp != null && sbp < 90) || (dbp != null && dbp <= 60)) 1 else 0) +
        (if (patientAge != null && patientAge >= 65) 1 else 0)

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
                        text = "CURB-65",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Pneumonia Severity Assessment",
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

            Curb65SwitchRow(
                title = "Confusion",
                description = "New-onset mental confusion",
                checked = confusion,
                onCheckedChange = {
                    confusion = it
                    resetResult()
                }
            )

            Curb65SwitchRow(
                title = "Urea >7 mmol/L",
                description = "Blood urea nitrogen criterion",
                checked = ureaHigh,
                onCheckedChange = {
                    ureaHigh = it
                    resetResult()
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = respiratoryRate,
                onValueChange = {
                    respiratoryRate = it.filter(Char::isDigit)
                    resetResult()
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Respiratory Rate") },
                suffix = { Text("/min") }
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = systolicBp,
                    onValueChange = {
                        systolicBp = it.filter(Char::isDigit)
                        resetResult()
                    },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    label = { Text("Systolic BP") },
                    suffix = { Text("mmHg") }
                )

                OutlinedTextField(
                    value = diastolicBp,
                    onValueChange = {
                        diastolicBp = it.filter(Char::isDigit)
                        resetResult()
                    },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    label = { Text("Diastolic BP") },
                    suffix = { Text("mmHg") }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = age,
                onValueChange = {
                    age = it.filter(Char::isDigit)
                    resetResult()
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Age") },
                suffix = { Text("years") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    calculated = true
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = rr != null && sbp != null && dbp != null && patientAge != null
            ) {
                Icon(
                    imageVector = Icons.Outlined.Assessment,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.padding(start = 4.dp))
                Text("Calculate CURB-65")
            }

            if (calculated) {
                Spacer(modifier = Modifier.height(18.dp))

                val risk = when {
                    score <= 1 -> "Low Risk"
                    score == 2 -> "Intermediate Risk"
                    else -> "High Risk"
                }

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
                            text = "CURB-65 Score",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = score.toString(),
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = risk,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Curb65SwitchRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
