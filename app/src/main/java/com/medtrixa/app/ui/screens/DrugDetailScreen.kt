package com.medtrixa.app.ui.screens

import com.medtrixa.app.ui.components.AutoSizeText
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.BoxWithConstraints
import com.medtrixa.app.data.repository.RenalHepaticRepository
import com.medtrixa.app.data.repository.ClinicalPharmacologyRepository
import androidx.compose.foundation.layout.Box
import com.medtrixa.app.ui.components.PremiumBackButton
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.snapshotFlow
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medtrixa.app.data.RecentDrugStore
import com.medtrixa.app.data.db.AppDatabase
import com.medtrixa.app.data.db.DrugLabelEntity
import com.medtrixa.app.data.db.IngredientEntity
import com.medtrixa.app.data.db.ProductEntity
import kotlinx.coroutines.launch

private val HeroStart = Color(0xFF176B87)
private val HeroEnd = Color(0xFF2A8FA6)
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

private fun buildSections(l: DrugLabelEntity, rh: RenalHepaticRepository.Entry? = null, cp: ClinicalPharmacologyRepository.Entry? = null): List<LabelSection> = listOf(
    LabelSection("Indications", Icons.Outlined.MedicalServices, Color(0xFF5B5FD6), tidyLabel(cleanLabelText(l.indications)), "Overview"),
    LabelSection("Adult Dose", Icons.Outlined.Opacity, Color(0xFF1F9D6B), tidyLabel(cleanLabelText(l.adultDose), true), "Dosing"),
    LabelSection("Administration", Icons.Outlined.Vaccines, Color(0xFF1F9D6B), tidyLabel(cleanLabelText(l.adultAdministration), true), "Dosing"),
    LabelSection("Child Dose", Icons.Outlined.ChildCare, Color(0xFF0984E3), headColon(tidyLabel(cleanLabelText(l.childDose), true), "Pediatric Use"), "Dosing"),
    LabelSection("Renal Dose", Icons.Outlined.Healing, Color(0xFF00A8A8), tidyLabel(cleanLabelText(rh?.renal), true).ifBlank { "No renal dosing text is available in the FDA label for this drug." }, "Dosing"),
    LabelSection("Hepatic Dose", Icons.Outlined.Healing, Color(0xFFB8860B), tidyLabel(cleanLabelText(rh?.hepatic), true).ifBlank { "No hepatic dosing text is available in the FDA label for this drug." }, "Dosing"),
    LabelSection("Contraindications", Icons.Outlined.Block, Color(0xFFE5484D), tidyLabel(cleanLabelText(l.contraindications)), "Safety"),
    LabelSection("Warnings", Icons.Outlined.WarningAmber, Color(0xFFC27A00), tidyLabel(cleanLabelText(l.warnings)), "Safety"),
    LabelSection("Adverse Reactions", Icons.Outlined.ReportProblem, Color(0xFFE17055), tidyLabel(cleanLabelText(l.adverseReactions)), "Safety"),
    LabelSection("Pregnancy", Icons.Outlined.FavoriteBorder, Color(0xFFD6456B), headColon(tidyLabel(cleanLabelText(l.pregnancy)), "Pregnancy Risk Summary"), "Pregnancy"),
    LabelSection("Mechanism of Action", Icons.Outlined.Science, Color(0xFF7B4FE0), headColon(tidyLabel(cleanLabelText(l.mechanism)), "Mechanism of Action"), "Mechanism"),
    LabelSection("Pharmacodynamics", Icons.Outlined.Science, Color(0xFF8E5CF0), tidyLabel(cleanLabelText(cp?.pd)), "Mechanism"),
    LabelSection("Pharmacokinetics", Icons.Outlined.Science, Color(0xFF3D8BFD), tidyLabel(cleanLabelText(cp?.pk)), "Mechanism"),
    LabelSection("Clinical Pharmacology", Icons.Outlined.Science, Color(0xFF3D8BFD), tidyLabel(cleanLabelText(cp?.general)), "Mechanism")
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
    val s = strength ?: return Double.MAX_VALUE
    val m = Regex("(\\d{1,3}(?:,\\d{3})+(?:\\.\\d+)?|\\d+(?:\\.\\d+)?|\\.\\d+)").find(s) ?: return Double.MAX_VALUE
    return m.value.replace(",", "").toDoubleOrNull() ?: Double.MAX_VALUE
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
    var rh by remember { mutableStateOf<RenalHepaticRepository.Entry?>(null) }
    var cp by remember { mutableStateOf<ClinicalPharmacologyRepository.Entry?>(null) }
    var loaded by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(emptySet<String>()) }
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
            rh = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                RenalHepaticRepository.load(context)
                RenalHepaticRepository.get(label?.setId)
            }
            cp = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                ClinicalPharmacologyRepository.load(context)
                ClinicalPharmacologyRepository.get(label?.setId)
            }
        }
        loaded = true
    }

    val sections = remember(label, rh, cp) { label?.let { buildSections(it, rh, cp) } ?: emptyList() }
    val groups = remember(sections) { sections.map { it.group }.distinct() }
    val thresholdPx = with(density) { 80.dp.roundToPx() }
    var tappedGroup by remember { mutableStateOf<String?>(null) }
    var autoScrolling by remember { mutableStateOf(false) }
    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }.collect { scrolling ->
            if (scrolling && !autoScrolling) tappedGroup = null
        }
    }
    val activeGroup by remember(sections) {
        derivedStateOf {
            val info = listState.layoutInfo.visibleItemsInfo
                .firstOrNull { it.index >= FIRST_SECTION_INDEX && it.offset + it.size > thresholdPx }
            val idx = (info?.index ?: FIRST_SECTION_INDEX) - FIRST_SECTION_INDEX
            sections.getOrNull(idx.coerceIn(0, (sections.size - 1).coerceAtLeast(0)))?.group
        }
    }
    val strengths = remember(products) {
        products.mapNotNull { it.strength }
            .distinct()
            .sortedWith(
                compareBy<String>(
                    { if (it.contains("ml", ignoreCase = true)) 1 else 0 },
                    { strengthValue(it) },
                    { it.lowercase() }
                )
            )
    }
    val facts = remember(products, label) {
        buildList<DrugFact> {
            val forms = products.sortedBy { formRank(it.form, it.route) }.mapNotNull { it.form }.distinct()
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
            approvalText(label?.effectiveDate)?.let { add(DrugFact("FDA APPROVED", it)) }
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
                        PremiumBackButton(onClick = onBackClick)
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
                            rxcui = ing.rxcui.toString(),
                            facts = facts,
                            halfLife = label?.halfLife ?: ing.halfLifeText?.takeIf { it.length <= 40 },
                            eliminationRoute = label?.eliminationRoute,
                            brands = brands,
                            strengths = strengths,
                            showAllBrands = showAllBrands,
                            onToggleBrands = { showAllBrands = !showAllBrands },
                            showAllStrengths = showAllStrengths,
                            onToggleStrengths = { showAllStrengths = !showAllStrengths }
                        )
                    }

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
                                    DrugNavChip(g, g == (tappedGroup ?: activeGroup)) {
                                    tappedGroup = g
                                        val i = sections.indexOfFirst { it.group == g }
                                        if (i >= 0) {
                                            expanded = setOf(sections[i].title)
                                            scope.launch {
                                                autoScrolling = true
                                    listState.animateScrollToItem(FIRST_SECTION_INDEX + i, -with(density) { 64.dp.roundToPx() })
                                    autoScrolling = false
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        itemsIndexed(sections, key = { _, s -> s.title }) { idx, s ->
                            DrugSectionCard(
                                section = s,
                                setId = label?.setId,
                                expanded = s.title in expanded,
                                onToggle = {
                                    if (s.title in expanded) {
                                        expanded = expanded - s.title
                                    } else {
                                        expanded = setOf(s.title)
                                        scope.launch {
                                            listState.animateScrollToItem(
                                                FIRST_SECTION_INDEX + idx,
                                                -with(density) { 64.dp.roundToPx() }
                                            )
                                        }
                                    }
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
private fun Modifier.fadeRightEdge(enabled: Boolean): Modifier = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
        drawContent()
        if (enabled) {
            val w = 28.dp.toPx()
            drawRect(
                brush = Brush.horizontalGradient(
                    listOf(Color.Black, Color.Transparent),
                    startX = size.width - w,
                    endX = size.width
                ),
                blendMode = BlendMode.DstIn
            )
        }
    }

@Composable
private fun DrugHeroCard(
    name: String,
    drugClass: String?,
    rxcui: String,
    halfLife: String?,
    eliminationRoute: String?,
    brands: List<String>,
    strengths: List<String>,
    showAllBrands: Boolean,
    onToggleBrands: () -> Unit,
    showAllStrengths: Boolean,
    onToggleStrengths: () -> Unit,
    facts: List<DrugFact> = emptyList()
) {
    fun fact(label: String) = facts.firstOrNull { it.label == label }
    val factsShape = RoundedCornerShape(24.dp)

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.16f), MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))))
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.14f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Medication, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(30.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    AutoSizeText(
                        text = name,
                        style = MaterialTheme.typography.headlineSmall.copy(color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold),
                        minFontSize = 16.sp
                    )
                    if (!drugClass.isNullOrBlank()) {
                        Text(
                            drugClass,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2
                        )
                    }
                    if (brands.isNotEmpty()) {
                        val brandScroll = rememberScrollState()
                        Row(
                            modifier = Modifier.padding(top = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                if (brands.size > 1) "Brand Names:" else "Brand Name:",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                            Spacer(Modifier.width(8.dp))
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .fadeRightEdge(brandScroll.canScrollForward)
                                    .horizontalScroll(brandScroll),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                brands.forEachIndexed { i, b ->
                                    if (i > 0) Text("  ·  ", color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                                    Text(
                                        b,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(factsShape)
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f), factsShape)
                .padding(18.dp)
        ) {
            Text(
                "DRUG FACTS",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.6.sp, fontSize = 11.sp),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            if (strengths.isEmpty()) {
                DrugSpecRow("Strengths", null)
            } else {
                DrugScrollRow("Strengths") { strengths.forEach { DrugSpecChip(it, tinted = true) } }
            }
            DrugFactScrollRow("Form", fact("FORM"))
            DrugFactScrollRow("Route", fact("ROUTE"))
            DrugSpecRow("Half-life", halfLife?.takeIf { it.isNotBlank() })
            DrugSpecRow("Elimination", eliminationRoute?.takeIf { it.isNotBlank() })
            DrugSpecRow("RxCUI", rxcui.takeIf { it.isNotBlank() }, muted = true)
            DrugFactScrollRow("Manufacturer", fact("MANUFACTURER"))
            DrugFactSpecRow("FDA approved", fact("FDA APPROVED"))
        }
    }
}

@Composable
private fun DrugSpecChip(text: String, tinted: Boolean, onClick: (() -> Unit)? = null) {
    val shape = RoundedCornerShape(20.dp)
    var m = Modifier.clip(shape)
    m = if (tinted) m.background(HeroStart.copy(alpha = 0.10f))
        else m.border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape)
    if (onClick != null) m = m.clickable { onClick() }
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = if (tinted) FontWeight.Bold else FontWeight.Medium,
        color = if (tinted) HeroStart else MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1,
        softWrap = false,
        modifier = m.padding(horizontal = 12.dp, vertical = 4.dp)
    )
}

@Composable
private fun DrugScrollRow(label: String, content: @Composable RowScope.() -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    )
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(16.dp))
        val scroll = rememberScrollState()
        BoxWithConstraints(Modifier.weight(1f)) {
            Row(
                modifier = Modifier
                    .fadeRightEdge(scroll.canScrollForward)
                    .horizontalScroll(scroll)
                    .widthIn(min = maxWidth),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                verticalAlignment = Alignment.CenterVertically,
                content = content
            )
        }
    }
}

@Composable
private fun DrugSpecRow(
    label: String,
    value: String?,
    moreLabel: String? = null,
    muted: Boolean = false,
    onMore: (() -> Unit)? = null
) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    )
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 9.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
            if (value.isNullOrBlank()) {
                Text(
                    "Not in database",
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    textAlign = TextAlign.End
                )
            } else {
                Text(
                    value,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (muted) FontWeight.Normal else FontWeight.Bold,
                    color = if (muted) MaterialTheme.colorScheme.onSurfaceVariant else Color.Unspecified,
                    textAlign = TextAlign.End
                )
            }
            if (moreLabel != null && onMore != null) {
                Text(
                    moreLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onMore() }.padding(top = 3.dp)
                )
            }
        }
    }
}

@Composable
private fun DrugFactSpecRow(label: String, fact: DrugFact?) {
    var open by remember { mutableStateOf(false) }
    val value = if (fact == null) null
        else if (open) (listOf(fact.value) + fact.extras).joinToString(", ")
        else fact.value
    val more = if (fact != null && fact.extras.isNotEmpty())
        (if (open) "Show less" else "+${fact.extras.size} more") else null
    DrugSpecRow(label, value, more) { open = open.not() }
}

@Composable
private fun DrugFactScrollRow(label: String, fact: DrugFact?) {
    if (fact == null || fact.value.isBlank()) {
        DrugSpecRow(label, null)
    } else {
        DrugScrollRow(label) {
            Text(
                (listOf(fact.value) + fact.extras).joinToString(", "),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                softWrap = false
            )
        }
    }
}

@Composable
private fun DrugHeroLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.6.sp, fontSize = 10.sp),
        color = Color.White.copy(alpha = 0.7f),
        modifier = Modifier.padding(top = 10.dp, bottom = 6.dp)
    )
}

@Composable
private fun DrugHeroChip(text: String, filled: Boolean, onClick: (() -> Unit)? = null, glass: Boolean = false) {
    val bg = if (glass) Color.White.copy(alpha = 0.14f) else if (filled) Color.White else Color.White.copy(alpha = 0.22f)
    val fg = if (filled && !glass) HeroStart else Color.White
    var m = Modifier
        .clip(RoundedCornerShape(20.dp))
        .background(bg)
    if (glass) m = m.border(0.5.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
    if (onClick != null) m = m.clickable { onClick() }
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium.copy(fontSize = 11.sp),
        color = fg,
        fontWeight = FontWeight.SemiBold,
        modifier = m.padding(horizontal = 10.dp, vertical = 3.dp)
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
            }
        }
    }
}

@Composable
private fun DrugFactCard(fact: DrugFact, modifier: Modifier) {
    var open by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(16.dp)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f), shape)
            .padding(12.dp)
    ) {
        Text(
            fact.label,
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.5.sp), textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(fact.value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
        if (open) {
            fact.extras.forEach {
                Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
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

private fun formRank(form: String?, route: String?): Int {
    val f = (form ?: "").lowercase()
    val r = (route ?: "").lowercase()
    return when {
        "tablet" in f -> 0
        "capsule" in f -> 1
        "ophthalmic" in r || "ophthalmic" in f -> 5
        "inject" in f || "syringe" in f || "cartridge" in f -> 2
        "syrup" in f || ("solution" in f && r == "oral") -> 3
        "suspension" in f -> 4
        else -> 6
    }
}


private fun headColon(text: String, heading: String): String {
    val t = text.trimStart()
    return if (t.startsWith("$heading ", ignoreCase = true))
        heading + ": " + t.substring(heading.length).trimStart()
    else text
}


private val approvalMonths = listOf("January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December")

private fun approvalText(s: String?): String? {
    val p = s?.trim()?.take(10)?.split("-") ?: return null
    if (p.size != 3) return null
    val y = p[0].toIntOrNull() ?: return null
    if (y < 1982) return "Before 1982"
    val m = p[1].toIntOrNull() ?: return null
    val d = p[2].toIntOrNull() ?: return null
    if (m !in 1..12) return null
    return "$d ${approvalMonths[m - 1]}, $y"
}


private val labelHeadings = listOf(
    "Indications and Usage", "Dosage and Administration", "Warnings and Precautions",
    "Use in Specific Populations", "Pregnancy Risk Summary", "Mechanism of Action",
    "Contraindications", "Adverse Reactions", "Drug Interactions", "Pediatric Use",
    "Overdosage", "Warnings")

private fun tidyLabel(text: String?, keepTables: Boolean = false): String {
    var t = text ?: return ""
    if (!keepTables) t = t.replace(Regex("""[\(\[]\s*(?:see\s+)?Tables?\s[^)\]]*[\)\]]""", RegexOption.IGNORE_CASE), "")
    if (!keepTables) t = t.replace(Regex("""\bTables?\s+\d+[A-Za-z]?\s+(?:illustrates|shows|lists|presents|summarizes|provides|describes|displays)\b[^.]*\."""), "")
    if (!keepTables) t = t.replace(Regex("""\bTables?\s+\d+(?:\.\d+)?[A-Za-z]?(?:\s*(?:,|and|-|through)\s*\d+[A-Za-z]?)*"""), "")
    t = t.replace(Regex("""\(\s*\d+(?:\.\d+)*(?:\s*,\s*\d+(?:\.\d+)*)*\s*\)"""), "")
    t = t.replace(Regex("""\s{2,}"""), " ").replace(Regex("""\s+([.,;:])"""), "\$1").trim()
    val num = Regex("""^\d+(?:\.\d+)*\s+""").find(t)
    val rest = if (num != null) t.substring(num.range.last + 1) else t
    val h = labelHeadings.firstOrNull { rest.startsWith(it + " ", ignoreCase = true) }
    if (h != null) t = h + ": " + rest.substring(h.length).trimStart()
    return t
}

@Composable
private fun DrugHeroDivider() {
    Box(
        Modifier
            .padding(top = 14.dp)
            .fillMaxWidth()
            .height(1.dp)
            .background(
                Brush.horizontalGradient(
                    listOf(Color.Transparent, Color.White.copy(alpha = 0.45f), Color.Transparent)
                )
            )
    )
}
