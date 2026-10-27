package com.medtrixa.app.ui.screens
import com.medtrixa.app.ui.components.AutoSizeText
import androidx.compose.ui.unit.sp
import com.medtrixa.app.ui.components.PremiumBackButton
import com.medtrixa.app.ui.theme.medtrixaAuraBrush
import androidx.compose.ui.graphics.Color

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Biotech
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.outlined.Policy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AntimicrobialReferenceHubScreen(
    onBackClick: () -> Unit,
    onGuidelinesClick: () -> Unit,
    onBacterialAtlasClick: () -> Unit,
    onIdsaAmrClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(medtrixaAuraBrush()),
        color = Color.Transparent
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
                PremiumBackButton(onClick = onBackClick)

                Column() {
                    AutoSizeText(
                        text = "Antimicrobial Reference Hub",
                        style = (MaterialTheme.typography.headlineSmall).copy(fontSize = 20.sp, lineHeight = 26.sp),
                        fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Empiric antibiotic selection and dosing by indication",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    top = 8.dp,
                    bottom = 24.dp
                ),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    ReferenceCategoryTile(
                        icon = Icons.Outlined.Biotech,
                        title = "Bacterial Atlas",
                        subtitle = "Searchable Organism Reference and Identification",
                        onClick = onBacterialAtlasClick
                    )
                }
            item {
                ReferenceCategoryTile(
                    icon = Icons.Outlined.Policy,
                    title = "IDSA AMR Guidance",
                    subtitle = "IDSA 2026 guidance for resistant Gram-negative infections",
                    comingSoon = true,
                    onClick = onIdsaAmrClick
                )
            }
                item {
                    ReferenceCategoryTile(
                        icon = Icons.Outlined.Medication,
                        title = "Antibiotic Dosing Guidelines",
                        subtitle = "Renowned IDSA dosing guidelines by indication and organism",
                        onClick = onGuidelinesClick
                    )
                }
            }
        }
    }

    BackHandler(onBack = onBackClick)
}

@Composable
private fun ReferenceCategoryTile(
    icon: ImageVector,
    title: String,
    subtitle: String?,
    onClick: () -> Unit,
    comingSoon: Boolean = false
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                        shape = RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(25.dp)
                )
            }

            Spacer(modifier = Modifier.width(15.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (comingSoon) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "COMING SOON",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.6.sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                    RoundedCornerShape(50)
                                )
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                if (!subtitle.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
