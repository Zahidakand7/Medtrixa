package com.medtrixa.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private data class AssessmentTool(
    val name: String,
    val description: String
)

@Composable
fun AssessScreen(
    onBackClick: () -> Unit,
    onToolClick: (String) -> Unit
) {
    val clinicalScores = listOf(
        AssessmentTool(
            "CURB-65",
            "Severity assessment for community-acquired pneumonia"
        ),
        AssessmentTool(
            "Glasgow Coma Scale",
            "Assessment of level of consciousness"
        ),
        AssessmentTool(
            "qSOFA",
            "Bedside assessment for suspected infection"
        ),
        AssessmentTool(
            "APGAR Score",
            "Newborn condition assessment"
        )
    )

    val riskTools = listOf(
        AssessmentTool(
            "CHA₂DS₂-VASc",
            "Stroke risk assessment in atrial fibrillation"
        ),
        AssessmentTool(
            "HAS-BLED",
            "Bleeding risk assessment in anticoagulation"
        ),
        AssessmentTool(
            "Wells — DVT",
            "Pretest probability assessment for deep vein thrombosis"
        ),
        AssessmentTool(
            "Wells — PE",
            "Pretest probability assessment for pulmonary embolism"
        )
    )

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBack,
                    contentDescription = "Back"
                )
            }

            Text(
                text = "Assess",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 16.dp,
                top = 8.dp,
                end = 16.dp,
                bottom = 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text(
                    text = "Clinical Scores",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(
                        top = 4.dp,
                        bottom = 2.dp
                    )
                )
            }

            items(clinicalScores) { tool ->
                AssessmentToolCard(tool, onClick = { if (tool.name == "CURB-65") onToolClick("curb65") })
            }

            item {
                Text(
                    text = "Risk Assessment Tools",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(
                        top = 14.dp,
                        bottom = 2.dp
                    )
                )
            }

            items(riskTools) { tool ->
                AssessmentToolCard(
                    tool,
                    onClick = {
                        if (tool.name == "CHA₂DS₂-VASc") {
                            onToolClick("cha2ds2vasc")
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun AssessmentToolCard(
    tool: AssessmentTool,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Assessment,
                contentDescription = null
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 14.dp)
            ) {
                Text(
                    text = tool.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = tool.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 3.dp)
                )
            }
        }
    }
}
