package com.medtrixa.app.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.ChildCare
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Healing
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.outlined.Opacity
import androidx.compose.material.icons.outlined.ReportProblem
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material.icons.outlined.Vaccines
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medtrixa.app.data.RecentDrugStore
import com.medtrixa.app.data.db.AppDatabase
import com.medtrixa.app.data.db.DrugLabelEntity
import com.medtrixa.app.data.db.IngredientEntity
import com.medtrixa.app.data.db.ProductEntity
import kotlinx.coroutines.launch

private val HeroStart = Color(0xFF3B3FC4)
private val HeroEnd = Color(0xFF7B4FE0)
private const val FIRST_SECTION_INDEX = 4
private const val CUT_MARKER = " \u2026 (continued in the full prescribing information)"

private data class LabelSection(
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val text: String,
    val group: String
)

private data class DrugFact(val label: String, val value: String, val extras: List<String> = emptyList())

private fun cleanLabelText(raw: String?): String {
    if (raw.isNullOrBlank()) return ""
    return raw
        .split(Regex("\\n\\s*\\n"))
        .map { para ->
            para.lines().map { it.trim() }.filter { it.isNotEmpty() }
                .joinToString(" ")
                .replace(Regex("\\s{2,}"), " ")
                .trim()
        }
        .filter { it.isNotEmpty() }
        .joinToString("\n\n")
}

private fun buildSections(l: DrugLabelEntity): List<LabelSection> = listOf(
    LabelSection("Indications", Icons.Outlined.MedicalServices, Color(0xFF5B5FD6), cleanLabelText(l.indications), "Overview"),
    LabelSection("Adult dose", Icons.Outlined.Opacity, Color(0xFF1F9D6B), cleanLabelText(l.adultDose), "Dosing"),
    LabelSection("Administration", Icons.Outlined.Vaccines, Color(0xFF1F9D6B), cleanLabelText(l.adultAdministration), "Dosing"),
    LabelSection("Child dose", Icons.Outlined.ChildCare, Color(0xFF0984E3), cleanLabelText(l.childDose), "Dosing"),
    LabelSection("Renal / hepatic dose", Icons.Outlined.Healing, Color(0xFF00A8A8), cleanLabelText(l.renalHepaticDose), "Dosing"),
    LabelSection("Contraindications", Icons.Outlined.Block, Color(0xFFE5484D), cleanLabelText(l.contraindications), "Safety"),
    LabelSection("Warnings", Icons.Outlined.WarningAmber, Color(0xFFC27A00), cleanLabelText(l.warnings), "Safety"),
    LabelSection("Adverse reactions", Icons.Outlined.ReportProblem, Color(0xFFE17055), cleanLabelText(l.adverseReactions), "Safety"),
    LabelSection("Pregnancy", Icons.Outlined.FavoriteBorder, Color(0xFFD6456B), cleanLabelText(l.pregnancy), "Pregnancy"),
    LabelSection("Mechanism of action", Icons.Outlined.Science, Color(0xFF7B4FE0), cleanLabelText(l.mechanism), "Mechanism")
).filter { it.text.isNotBlank() }

private fun justifiedParagraphs(text: String): AnnotatedString = buildAnnotatedString {
    val paras = text.split("\n\n")
    paras.forEachIndexed { i, p ->
        val justify = !p.startsWith("\u2022") && p.length >= 90
        withStyle(
            ParagraphStyle(
                textAlign = if (justify) TextAlign.Justify else TextAlign.Start,
                lineBreak = LineBreak.Paragraph,
                hyphens = Hyphens.Auto
            )
        ) {
            append(p)
            if (i < paras.lastIndex) append("\n")
        }
    }
}

private fun strengthValue(strength: String?): Double {
    if (strength.isNullOrBlank()) return Double.MAX_VALUE
    return Regex("\\d+(\\.\\d+)?").find(strength)?.value?.toDoubleOrNull() ?: Double.MAX_VALUE
}

private fun labelYear(raw: String?): String? {
    val t = raw?.trim() ?: return null
    return if (t.length >= 4 && t.take(4).all { it.isDigit() }) t.take(4) else null
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DrugDetailScreen(genericName: String, onBackClick: () -> Unit) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var ingredient by remember { mutableStateOf<IngredientEntity?>(null) }
    var products by remember { mutableStateOf<List<ProductEntity>>(emptyList()) }
    var brands by remember { mutableStateOf<List<String>>(emptyList()) }
    var label by remember { mutableStateOf<DrugLabelEntity?>(null) }
    var loaded by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(setOf("Indications")) }
    var showAllBrands by remember { mutableStateOf(false) }
    var showAllStrengths by remember { mutableStateOf(false) }

    LaunchedEffect(genericName) {
        val dao = AppDatabase.getInstance(context).drugDao()
        val ing = dao.getIngredientByName(genericName)
        ingredient = ing
        if (ing != null) {
            RecentDrugStore.addRecent(context, ing.name)
            products = dao.getProducts(ing.rxcui)
            brands = dao.getBrands(ing.rxcui)
            label = dao.getLabel(ing.rxcui)
        }
        loaded = true
    }

    val sections = remember(label) { label?.let { buildSections(it) } ?: emptyList() }
    val groups = remember(sections) { sections.map { it.group }.distinct() }
    val thresholdPx = with(density) { 80.dp.roundToPx() }
    val activeGroup by remember(sections) {
        derivedStateOf {
            val info = listState.layoutInfo.visibleItemsInfo
                .firstOrNull { it.index >= FIRST_SECTION_INDEX && it.offset + it.size > thresholdPx }
            val idx = (info?.index ?: FIRST_SECTION_INDEX) - FIRST_SECTION_INDEX
            sections.getOrNull(idx.coerceIn(0, (sections.size - 1).coerceAtLeast(0)))?.group
        }
    }
    val strengths = remember(products) {
        products.mapNotNull { it.strength }.distinct().sortedBy { strengthValue(it) }
    }
    val facts = remember(products, label) {
        buildList<DrugFact> {
            val forms = products.mapNotNull { it.form }.distinct()
            if (forms.isNotEmpty()) add(DrugFact("FORM", forms.take(2).joinToString(", "), forms.drop(2)))
            val routes = products.mapNotNull { it.route }.distinct()
            if (routes.isNotEmpty()) add(DrugFact("ROUTE", routes.take(2).joinToString(", "), routes.drop(2)))
            val m = label?.manufacturer
            if (!m.isNullOrBlank()) {
                val others = label?.otherManufacturers.orEmpty().split(";")
                    .map { it.trim() }
                    .filter { it.isNotEmpty() && !it.equals(m, ignoreCase = true) }
                add(DrugFact("MANUFACTURER", m, others))
            }
            labelYear(label?.effectiveDate)?.let { add(DrugFact("LABEL UPDATED", it)) }
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        if (!loaded) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding(),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                        }
                        Text(
                            "Search results",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                val ing = ingredient
                if (ing == null) {
                    item {
                        Text(
                            "We couldn't find this drug in the database.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    item {
                        DrugHeroCard(
                            name = ing.name,
                            drugClass = ing.drugClass,
                            brands = brands,
                            strengths = strengths,
                            showAllBrands = showAllBrands,
                            onToggleBrands = { showAllBrands = !showAllBrands },
                            showAllStrengths = showAllStrengths,
                            onToggleStrengths = { showAllStrengths = !showAllStrengths }
                        )
                    }
                    item { DrugFactsGrid(facts) }

                    if (sections.isEmpty()) {
                        item {
                            Text(
                                "Clinical details aren't available for this drug yet.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 12.dp)
                            )
                        }
                    } else {
                        stickyHeader {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.background)
                                    .horizontalScroll(rememberScrollState())
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                groups.forEach { g ->
                                    DrugNavChip(g, g == activeGroup) {
                                        val i = sections.indexOfFirst { it.group == g }
                                        if (i >= 0) {
                                            expanded = expanded + sections[i].title
                                            scope.launch {
                                                listState.animateScrollToItem(
                                                    FIRST_SECTION_INDEX + i,
                                                    -with(density) { 64.dp.roundToPx() }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        itemsIndexed(sections, key = { _, s -> s.title }) { _, s ->
                            DrugSectionCard(
                                section = s,
                                setId = label?.setId,
                                expanded = s.title in expanded,
                                onToggle = {
                                    expanded = if (s.title in expanded) expanded - s.title else expanded + s.title
                                }
                            )
                        }
                    }
                    item { DrugSourceFooter(label) }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DrugHeroCard(
    name: String,
    drugClass: String?,
    brands: List<String>,
    strengths: List<String>,
    showAllBrands: Boolean,
    onToggleBrands: () -> Unit,
    showAllStrengths: Boolean,
    onToggleStrengths: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.linearGradient(listOf(HeroStart, HeroEnd)), RoundedCornerShape(24.dp))
            .padding(18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White.copy(alpha = 0.18f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Medication, contentDescription = null, tint = Color.White, modifier = Modifier.size(26.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                if (!drugClass.isNullOrBlank()) {
                    Text(drugClass, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.85f))
                }
            }
        }

        if (brands.isNotEmpty()) {
            DrugHeroLabel("BRAND NAMES")
            val shownBrands = if (showAllBrands) brands else brands.take(8)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                shownBrands.forEach { DrugHeroChip(it, filled = false) }
                if (brands.size > 8) {
                    DrugHeroChip(
                        if (showAllBrands) "Show less" else "+${brands.size - 8} more",
                        filled = false,
                        onClick = onToggleBrands
                    )
                }
            }
        }

        if (strengths.isNotEmpty()) {
            DrugHeroLabel("STRENGTHS")
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                (if (showAllStrengths) strengths else strengths.take(10)).forEach { DrugHeroChip(it, filled = true) }
                if (strengths.size > 10) DrugHeroChip(
                    if (showAllStrengths) "Show less" else "+${strengths.size - 10} more",
                    filled = false,
                    onClick = onToggleStrengths
                )
            }
        }
    }
}

@Composable
private fun DrugHeroLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
        color = Color.White.copy(alpha = 0.75f),
        modifier = Modifier.padding(top = 14.dp, bottom = 6.dp)
    )
}

@Composable
private fun DrugHeroChip(text: String, filled: Boolean, onClick: (() -> Unit)? = null) {
    val bg = if (filled) Color.White else Color.White.copy(alpha = 0.22f)
    val fg = if (filled) HeroStart else Color.White
    var m = Modifier
        .clip(RoundedCornerShape(20.dp))
        .background(bg)
    if (onClick != null) m = m.clickable { onClick() }
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = fg,
        fontWeight = FontWeight.SemiBold,
        modifier = m.padding(horizontal = 12.dp, vertical = 4.dp)
    )
}

@Composable
private fun DrugFactsGrid(facts: List<DrugFact>) {
    if (facts.isEmpty()) return
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        facts.chunked(2).forEach { pair ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                pair.forEach { fact ->
                    DrugFactCard(fact, Modifier.weight(1f).fillMaxHeight())
                }
                if (pair.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun DrugFactCard(fact: DrugFact, modifier: Modifier) {
    var open by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = modifier
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f), shape)
            .padding(12.dp)
    ) {
        Text(
            fact.label,
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.5.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(fact.value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        if (open) {
            fact.extras.forEach {
                Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        if (fact.extras.isNotEmpty()) {
            Text(
                text = if (open) "Show less" else "+${fact.extras.size} more",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clickable { open = !open }
                    .padding(top = 3.dp)
            )
        }
    }
}

@Composable
private fun DrugNavChip(text: String, selected: Boolean, onClick: () -> Unit) {
    val bg = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val fg = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = fg,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp)
    )
}

@Composable
private fun DrugSectionCard(section: LabelSection, setId: String?, expanded: Boolean, onToggle: () -> Unit) {
    var full by remember { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current
    val cut = section.text.contains(CUT_MARKER)
    val body = section.text.replace(CUT_MARKER, "")
    val long = body.length > 700
    val shape = RoundedCornerShape(18.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f), shape)
            .animateContentSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggle() }
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(section.color.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = section.icon, contentDescription = null, tint = section.color, modifier = Modifier.size(19.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                section.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (expanded) {
            Column(modifier = Modifier.padding(start = 14.dp, end = 14.dp, bottom = 14.dp)) {
                Text(
                    text = justifiedParagraphs(body),
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp, lineHeight = 19.sp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                    maxLines = if (long && !full) 8 else Int.MAX_VALUE,
                    overflow = TextOverflow.Ellipsis
                )
                if (long) {
                    Text(
                        text = if (full) "Show less" else "Read more",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clickable { full = !full }
                            .padding(top = 8.dp, bottom = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DrugSourceFooter(label: DrugLabelEntity?) {
    val text = buildString {
        append("Sources: RxNorm (U.S. National Library of Medicine, Sep 2026)")
        val m = label?.manufacturer
        if (!m.isNullOrBlank()) append(" and the FDA drug label from $m")
        append(". For clinical reference only; always verify against current prescribing information.")
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            Icons.Outlined.VerifiedUser,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
