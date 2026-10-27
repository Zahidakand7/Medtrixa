package com.medtrixa.app.ui.screens

import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MonitorWeight
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material.icons.outlined.Accessibility
import androidx.compose.material.icons.outlined.Colorize
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.Biotech
import androidx.compose.material.icons.outlined.Healing
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.material.icons.outlined.PregnantWoman
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medtrixa.app.ui.components.PremiumBackButton
import com.medtrixa.app.ui.components.AutoSizeText

@Composable
fun ProtocolsHubScreen(
    onBackClick: () -> Unit,
    onPrimaryClick: () -> Unit,
    onEmergencyClick: () -> Unit,
    onInsulinClick: () -> Unit,
    onPregnancyClick: () -> Unit,
    onHypertensionClick: () -> Unit,
    onPancreatitisClick: () -> Unit,
    onThyroidClick: () -> Unit,
    onHeartFailureClick: () -> Unit
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
                Text("Protocols", fontSize = 20.sp, lineHeight = 26.sp, fontWeight = FontWeight.SemiBold)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProtocolHubTile(
                    modifier = Modifier.weight(1f),
                    title = "Primary Protocols",
                    subtitle = "Insulin Dosing",
                    icon = Icons.Outlined.Accessibility,
                    onClick = onPrimaryClick
                )
                ProtocolHubTile(
                    modifier = Modifier.weight(1f),
                    title = "Emergency Protocols",
                    subtitle = "Time-critical care",
                    icon = Icons.Outlined.LocalHospital,
                    onClick = onEmergencyClick
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
                    title = "Diabetes",
                    subtitle = "Insulin Dosing",
                    icon = Icons.Outlined.Colorize,
                    onClick = onInsulinClick
                )
                ProtocolHubTile(
                    modifier = Modifier.weight(1f),
                    title = "Thyroid Disorders",
                    subtitle = "Diagnostic Interpretation & Treatment",
                    icon = Icons.Outlined.Thermostat,
                    onClick = onThyroidClick
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
                    title = "Hypertension",
                    subtitle = "Coming soon",
                    icon = Icons.Outlined.Speed,
                    onClick = onHypertensionClick
                )
                ProtocolHubTile(
                    modifier = Modifier.weight(1f),
                    title = "Heart Failure",
                    subtitle = "Coming soon",
                    icon = Icons.Outlined.MonitorWeight,
                    onClick = onHeartFailureClick
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
                    title = "Pancreatitis",
                    subtitle = "Coming soon",
                    icon = Icons.Outlined.Healing,
                    onClick = onPancreatitisClick
                )
                ProtocolHubTile(
                    modifier = Modifier.weight(1f),
                    title = "Pregnancy",
                    subtitle = "Coming soon",
                    icon = Icons.Outlined.PregnantWoman,
                    onClick = onPregnancyClick
                )
            }
        }
    }
}

@Composable
fun ProtocolHubTile(
    modifier: Modifier,
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(144.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
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
            Spacer(modifier = Modifier.height(8.dp))
            AutoSizeText(
                text = title,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                minFontSize = 10.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 15.sp,
                maxLines = 2
            )
        }
    }
}
