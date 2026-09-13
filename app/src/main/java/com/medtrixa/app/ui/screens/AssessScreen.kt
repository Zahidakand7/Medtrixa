package com.medtrixa.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    top = 14.dp,
                    bottom = 24.dp
                ),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
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
                                text = "Assess",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "Clinical scoring and risk assessment tools",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "Clinical Scores",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                    )
                }

                items(clinicalScores) { tool ->
                    AssessmentToolCard(
                        tool,
                        onClick = { if (tool.name == "CURB-65") onToolClick("curb65") }
                    )
                }

                item {
                    Text(
                        text = "Risk Assessment Tools",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 14.dp, bottom = 2.dp)
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
        shape = RoundedCornerShape(18.dp),
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
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Assessment,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 14.dp)
            ) {
                Text(
                    text = tool.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = tool.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
