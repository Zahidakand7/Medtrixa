package com.medtrixa.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DirectionsWalk
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.ShowChart
import androidx.compose.material.icons.outlined.Vaccines
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.Bloodtype
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Opacity
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medtrixa.app.ui.components.PremiumBackButton

@Composable
fun ConditionsScreen(
    onBackClick: () -> Unit,
    onCkdClick: () -> Unit,
    onChronicLiverDiseaseClick: () -> Unit,
    onMentalHealthClick: () -> Unit,
    onCopdAsthmaClick: () -> Unit,
    onCadClick: () -> Unit,
    onGoutClick: () -> Unit,
    onNeuroClick: () -> Unit,
    onAutoimmuneClick: () -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary
    val background = MaterialTheme.colorScheme.background

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(primary.copy(alpha = 0.06f).compositeOver(background), background)
                )
            )
    ) {
        Column(Modifier.fillMaxSize().statusBarsPadding()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PremiumBackButton(onClick = onBackClick, edgeInset = 16.dp)
                Text("Chronic Care", fontSize = 20.sp, lineHeight = 26.sp, fontWeight = FontWeight.SemiBold)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProtocolHubTile(
                    modifier = Modifier.weight(1f),
                    title = "Chronic Kidney Disease",
                    subtitle = "Coming soon",
                    icon = Icons.Outlined.FilterAlt,
                    onClick = onCkdClick
                )
                ProtocolHubTile(
                    modifier = Modifier.weight(1f),
                    title = "Chronic Liver Disease",
                    subtitle = "Coming soon",
                    icon = Icons.Outlined.Science,
                    onClick = onChronicLiverDiseaseClick
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProtocolHubTile(
                    modifier = Modifier.weight(1f),
                    title = "Autoimmune Disease",
                    subtitle = "Coming soon",
                    icon = Icons.Outlined.Vaccines,
                    onClick = onAutoimmuneClick
                )
                ProtocolHubTile(
                    modifier = Modifier.weight(1f),
                    title = "Mental Health Conditions",
                    subtitle = "Coming soon",
                    icon = Icons.Outlined.SelfImprovement,
                    onClick = onMentalHealthClick
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProtocolHubTile(
                    modifier = Modifier.weight(1f),
                    title = "Coronary Artery Disease",
                    subtitle = "Coming soon",
                    icon = Icons.Outlined.ShowChart,
                    onClick = onCadClick
                )
                ProtocolHubTile(
                    modifier = Modifier.weight(1f),
                    title = "Neurological Disease",
                    subtitle = "Coming soon",
                    icon = Icons.Outlined.Memory,
                    onClick = onNeuroClick
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProtocolHubTile(
                    modifier = Modifier.weight(1f),
                    title = "COPD & Asthma",
                    subtitle = "Coming soon",
                    icon = Icons.Outlined.Air,
                    onClick = onCopdAsthmaClick
                )
                ProtocolHubTile(
                    modifier = Modifier.weight(1f),
                    title = "Gout",
                    subtitle = "Coming soon",
                    icon = Icons.Outlined.DirectionsWalk,
                    onClick = onGoutClick
                )
            }
        }
    }
}
