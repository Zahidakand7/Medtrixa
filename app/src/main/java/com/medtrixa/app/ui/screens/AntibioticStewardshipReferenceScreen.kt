package com.medtrixa.app.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun AntibioticStewardshipReferenceScreen(
    onBackClick: () -> Unit
) {
    androidx.compose.material3.Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBack,
                    contentDescription = "Back"
                )
            }

            Column(modifier = Modifier.padding(start = 4.dp)) {
                Text(
                    text = "Antibiotic Stewardship",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Quick reference for everyday prescribing",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 8.dp,
                bottom = 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                StewardshipReferenceCard(
                    icon = Icons.Outlined.MenuBook,
                    title = "Antibiotic Stewardship — Quick Reference",
                    subtitle = "General framework adapted from CDC's Core Elements of Hospital Antibiotic Stewardship Programs (2019 update). Always follow your institution's antibiogram, protocols and the current full guideline before prescribing.",
                    sections = listOf(
                        "Before starting antibiotics" to
                            "Confirm the clinical picture actually supports a bacterial infection before treating. Obtain appropriate cultures before starting therapy whenever this will not dangerously delay treatment, and review local antibiogram data and any recent microbiology results. Reassess the chosen regimen once culture and susceptibility results return.",

                        "CDC's Core Elements framework" to
                            "CDC's Core Elements of Hospital Antibiotic Stewardship Programs outlines seven structural components that support good antibiotic use at the institutional level: Leadership Commitment, Accountability, Pharmacy Expertise, Action, Tracking, Reporting and Education. As a prescriber, you mainly encounter this through the 'Action' element — the specific interventions your stewardship program asks you to follow, such as facility-specific empiric treatment guidance or pharmacist review of ongoing therapy.",

                        "IV to oral transition" to
                            "Switching from IV to oral therapy is a widely used stewardship intervention once appropriate. Commonly applied criteria: the patient is clinically improving and hemodynamically stable, able to tolerate oral intake, and the oral agent has adequate bioavailability to reach therapeutic concentrations at the infection site. Confirm your institution's specific IV-to-oral protocol before switching.",

                        "Bedside checklist" to
                            "1) What is the suspected source? 2) How unwell is the patient? 3) What cultures or specimens are needed? 4) What did prior cultures show? 5) What antibiotics were used recently? 6) What resistance is likely locally? 7) Is there a device, obstruction, abscess or other source-control issue? 8) Is the chosen drug active at this infection site? 9) Is renal or hepatic dose adjustment needed? 10) When will therapy be reassessed and narrowed?",

                        "Safety reminders" to
                            "Do not treat colonization as infection. Do not continue an empiric regimen once susceptibility results show it is inactive against the organism. Confirm source control has been addressed. Always verify against your institution's current antibiogram, full guideline text and pharmacist input before prescribing."
                    ),
                    source = "CDC Core Elements of Hospital Antibiotic Stewardship Programs (2019 update)",
                    status = "General reference — verify against current local and national guidance"
                )
            }
        }
    }
    }

    BackHandler(onBack = onBackClick)
}

@Composable
private fun StewardshipReferenceCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    sections: List<Pair<String, String>>,
    source: String,
    status: String
) {
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
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Justify
            )

            sections.forEach { (label, value) ->
                Column {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Justify
                    )
                }
            }

            Text(
                text = "Source: $source",
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = "Status: $status",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
