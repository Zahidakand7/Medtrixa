package com.medtrixa.app.ui.screens.assess

import androidx.activity.compose.BackHandler
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

    val b = bilirubin.toDoubleOrNull()
    val s = sodium.toDoubleOrNull()
    val i = inr.toDoubleOrNull()
    val c = creatinine.toDoubleOrNull()
    val a = albumin.toDoubleOrNull()

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
                        text = "MELD 3.0",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Severity and mortality assessment in advanced liver disease",
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
                value = bilirubin,
                onValueChange = {
                    bilirubin = it
                    resetResult()
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Bilirubin (mg/dL)") }
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = sodium,
                onValueChange = {
                    sodium = it
                    resetResult()
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Sodium (mmol/L)") }
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = inr,
                onValueChange = {
                    inr = it
                    resetResult()
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("INR") }
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = creatinine,
                onValueChange = {
                    creatinine = it
                    resetResult()
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Creatinine (mg/dL)") }
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = albumin,
                onValueChange = {
                    albumin = it
                    resetResult()
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Albumin (g/dL)") }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Meld30SwitchRow(
                title = "Female",
                checked = female,
                onCheckedChange = {
                    female = it
                    resetResult()
                }
            )

            Meld30SwitchRow(
                title = "Dialysis ≥2 times in last 7 days",
                checked = dialysis,
                onCheckedChange = {
                    dialysis = it
                    resetResult()
                }
            )

            Meld30SwitchRow(
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
                    if (b != null && s != null && i != null && c != null && a != null) {
                        result = Meld30Calculator.calculate(
                            b, s, i, c, a, female, dialysis, cvvhd
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = b != null && s != null && i != null && c != null && a != null
            ) {
                Icon(
                    imageVector = Icons.Outlined.Assessment,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.padding(start = 4.dp))
                Text("Calculate MELD 3.0")
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
                            text = "MELD 3.0 Score",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = it.score.toString(),
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Meld30SwitchRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
