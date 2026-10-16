package com.medtrixa.app.ui.screens.calculators

import androidx.compose.foundation.layout.Column
import androidx.compose.ui.unit.sp
import com.medtrixa.app.ui.components.PremiumBackButton
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.medtrixa.app.domain.calculator.AnionGapCalculator

@Composable
fun AnionGapScreen(onBackClick: () -> Unit) {
    BackHandler { onBackClick() }

    var naInput by remember { mutableStateOf("") }
    var clInput by remember { mutableStateOf("") }
    var hco3Input by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf<String?>(null) }
    var interpretationText by remember { mutableStateOf<String?>(null) }
    var errorText by remember { mutableStateOf<String?>(null) }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding().verticalScroll(rememberScrollState()).navigationBarsPadding().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                PremiumBackButton(onClick = onBackClick)
                Column(Modifier.weight(1f)) {
                    Text("Anion Gap", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge, fontSize = 20.sp, lineHeight = 26.sp)
                    Text("Evaluate Metabolic Acidosis", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(value = naInput, onValueChange = { naInput = it }, label = { Text("Sodium (mEq/L)") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(value = clInput, onValueChange = { clInput = it }, label = { Text("Chloride (mEq/L)") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(value = hco3Input, onValueChange = { hco3Input = it }, label = { Text("Bicarbonate (mEq/L)") }, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    errorText = null; resultText = null; interpretationText = null
                    try {
                        val na = naInput.toDouble()
                        val cl = clInput.toDouble()
                        val hco3 = hco3Input.toDouble()
                        val result = AnionGapCalculator().calculate(
                            "anion_gap",
                            mapOf("sodium" to na, "chloride" to cl, "bicarbonate" to hco3)
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
                Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Result: $resultText", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(interpretationText ?: "")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Formula: AG = Na − (Cl + HCO₃)", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            if (errorText != null) {
                Text(errorText ?: "", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
