package com.medtrixa.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SortByAlpha
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.Grass
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Coronavirus
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.Biotech
import androidx.compose.material.icons.outlined.Bloodtype
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Healing
import androidx.compose.material.icons.outlined.LocalPharmacy
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Opacity
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medtrixa.app.data.db.AppDatabase
import com.medtrixa.app.data.db.SearchHit
import com.medtrixa.app.ui.components.AutoSizeText
import com.medtrixa.app.ui.components.PremiumBackButton

private fun drugGroupIcon(g: DrugGroup): ImageVector = when (g) {
    DrugGroup.ANTIMICROBIALS -> Icons.Outlined.Coronavirus
    DrugGroup.ONCOLOGY_IMMUNOLOGY -> Icons.Outlined.Shield
    DrugGroup.BLOOD -> Icons.Outlined.Bloodtype
    DrugGroup.CARDIOVASCULAR -> Icons.Outlined.Favorite
    DrugGroup.RESPIRATORY_ALLERGY -> Icons.Outlined.Grass
    DrugGroup.GI_HEPATIC -> Icons.Outlined.Restaurant
    DrugGroup.ENDOCRINE_METABOLIC -> Icons.Outlined.Tune
    DrugGroup.CNS_PAIN -> Icons.Outlined.Psychology
    DrugGroup.OTHER -> Icons.Outlined.MoreHoriz
}

@Composable
private fun DrugsSoftBox(modifier: Modifier = Modifier, onClick: () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    val primary = MaterialTheme.colorScheme.primary
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(listOf(primary.copy(alpha = 0.14f), primary.copy(alpha = 0.05f))))
            .clickable { onClick() }
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        content = content
    )
}

@Composable
private fun DrugsIconBubble(icon: ImageVector) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
    }
}

@Composable
private fun DrugGroupTile(group: DrugGroup, count: Int, modifier: Modifier, onClick: () -> Unit) {
    DrugsSoftBox(modifier = modifier.heightIn(min = 112.dp), onClick = onClick) {
        DrugsIconBubble(drugGroupIcon(group))
        Spacer(Modifier.height(10.dp))
        AutoSizeText(
            text = group.title,
            style = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 15.sp, fontWeight = FontWeight.Bold),
            minFontSize = 11.sp
        )
        Text(
            "$count drugs",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )
    }
}

@Composable
fun DrugsBrowse(onGroupClick: (DrugGroup) -> Unit, onAllClick: () -> Unit) {
    val context = LocalContext.current
    var counts by remember { mutableStateOf<Map<DrugGroup, Int>>(emptyMap()) }
    var total by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        val dao = AppDatabase.getInstance(context).drugDao()
        total = dao.countAll()
        counts = dao.getClassCounts()
            .groupBy { drugGroupOf(it.drugClass) }
            .mapValues { e -> e.value.sumOf { it.n } }
    }

    val groups = DrugGroup.values().filter { it != DrugGroup.OTHER && (counts[it] ?: 0) > 0 }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            "BROWSE BY GROUP",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp
        )
        groups.chunked(2).forEach { pair ->
            Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                pair.forEach { g ->
                    DrugGroupTile(g, counts[g] ?: 0, Modifier.weight(1f).fillMaxHeight()) { onGroupClick(g) }
                }
                if (pair.size == 1) Spacer(Modifier.weight(1f))
            }
        }
        DrugsSoftBox(modifier = Modifier.fillMaxWidth(), onClick = onAllClick) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                DrugsIconBubble(Icons.Outlined.SortByAlpha)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("All drugs A to Z", fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                    Text(
                        if (total > 0) "$total drugs, including unclassified" else "Full drug list",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
                Icon(Icons.Outlined.ChevronRight, contentDescription = null)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrugGroupListScreen(group: DrugGroup?, onBackClick: () -> Unit, onDrugClick: (String) -> Unit) {
    val context = LocalContext.current
    val primary = MaterialTheme.colorScheme.primary
    val bg = MaterialTheme.colorScheme.background
    var drugs by remember(group) { mutableStateOf<List<SearchHit>?>(null) }

    LaunchedEffect(group) {
        val dao = AppDatabase.getInstance(context).drugDao()
        drugs = if (group == null) {
            dao.getAllDrugs()
        } else {
            val classes = dao.getClassCounts().map { it.drugClass }.filter { drugGroupOf(it) == group }
            dao.getByClasses(classes)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(primary.copy(alpha = 0.06f).compositeOver(bg), bg)))
    ) {
        Scaffold(
            modifier = Modifier.statusBarsPadding(),
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text(group?.title ?: "All drugs A to Z", fontSize = 20.sp, lineHeight = 26.sp, fontWeight = FontWeight.SemiBold, maxLines = 1) },
                    navigationIcon = { PremiumBackButton(onClick = onBackClick, edgeInset = 12.dp) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = androidx.compose.ui.graphics.Color.Transparent)
                )
            }
        ) { padding ->
            val list = drugs
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        when {
                            list == null -> "Loading..."
                            list.isEmpty() -> "No drugs in this group."
                            else -> "${list.size} drugs"
                        },
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (list != null) {
                    items(list, key = { it.rxcui }) { hit ->
                        DrugResultCard(hit, "") { onDrugClick(hit.name) }
                    }
                }
            }
        }
    }
}
