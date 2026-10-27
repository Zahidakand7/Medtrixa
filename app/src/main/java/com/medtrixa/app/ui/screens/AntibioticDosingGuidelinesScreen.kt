package com.medtrixa.app.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medtrixa.app.ui.components.PremiumBackButton
import com.medtrixa.app.ui.theme.medtrixaAuraBrush

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AntibioticDosingGuidelinesScreen(onBackClick: () -> Unit) {
    BackHandler { onBackClick() }
    var selectedTab by rememberSaveable { mutableStateOf(0) }
    val tabs = listOf("Drugs", "Empirical", "Renal and paeds", "TDM", "Breakpoints")

    Scaffold(
        modifier = Modifier.background(medtrixaAuraBrush()).statusBarsPadding(),
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                title = {
                    AutoSizeText(
                        "Antibiotic Dosing Guidelines",
                        TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                    )
                },
                navigationIcon = {
                    PremiumBackButton(onClick = onBackClick, edgeInset = 12.dp)
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 8.dp,
                containerColor = Color.Transparent
            ) {
                tabs.forEachIndexed { index, label ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(label, fontWeight = FontWeight.SemiBold) }
                    )
                }
            }
            when (selectedTab) {
                0 -> AntibioticExtrasPanel(
                    file = "rx_drugs.json",
                    eyebrow = "ANTIBIOTIC MONOGRAPHS",
                    title = "Drugs A to Z",
                    blurb = "Adult dose, renal adjustment, pregnancy, administration and monitoring for each antibiotic.",
                    searchHint = "Search antibiotic, class or use",
                )
                1 -> AntibioticExtrasPanel(
                    file = "rx_empirical.json",
                    eyebrow = "EMPIRICAL THERAPY",
                    title = "Treatment by infection",
                    blurb = "First-line regimens, alternatives and usual duration for common infection syndromes.",
                    searchHint = "Search infection, organism or drug",
                )
                2 -> AntibioticExtrasPanel(
                    file = "rx_renal_paeds.json",
                    eyebrow = "SPECIAL POPULATIONS",
                    title = "Renal and paediatric dosing",
                    blurb = "Creatinine clearance bands, dialysis, obesity and weight-based paediatric doses.",
                    searchHint = "Search drug, class or population",
                )
                3 -> AntibioticExtrasPanel(
                    file = "rx_tdm.json",
                    eyebrow = "MONITORING AND INFUSION",
                    title = "TDM and infusion strategies",
                    blurb = "Vancomycin AUC dosing, aminoglycoside monitoring and extended-infusion beta-lactams.",
                    searchHint = "Search drug or strategy",
                )
                4 -> ClsiBreakpointsPanel()
                else -> {}
            }
        }
    }
}

@Composable
private fun AutoSizeText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier
) {
    var scaledFontSize by remember(text) { mutableStateOf(style.fontSize) }
    var readyToDraw by remember(text) { mutableStateOf(false) }

    Text(
        text = text,
        style = style.copy(fontSize = scaledFontSize),
        maxLines = 1,
        softWrap = false,
        modifier = modifier.drawWithContent { if (readyToDraw) drawContent() },
        onTextLayout = { result ->
            if (result.didOverflowWidth && scaledFontSize.value > 8f) {
                scaledFontSize = androidx.compose.ui.unit.TextUnit(
                    scaledFontSize.value * 0.94f,
                    androidx.compose.ui.unit.TextUnitType.Sp
                )
            } else {
                readyToDraw = true
            }
        }
    )
}
