package com.medtrixa.app.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medtrixa.app.ui.components.PremiumBackButton

private data class DataSource(
    val name: String,
    val publisher: String,
    val usedFor: String,
    val license: String,
    val attribution: String,
    val icon: ImageVector,
    val color: Color
)

private data class ClinicalReference(
    val tool: String,
    val area: String,
    val citations: List<String>,
    val icon: ImageVector,
    val color: Color
)

private val clinicalReferences = listOf(
    ClinicalReference(
        tool = "Creatinine Clearance (Cockcroft-Gault)",
        area = "Renal function",
        citations = listOf("Cockcroft DW, Gault MH. Prediction of creatinine clearance from serum creatinine. Nephron 1976;16(1):31-41."),
        icon = Icons.Outlined.Healing,
        color = Color(0xFF00A8A8)
    ),
    ClinicalReference(
        tool = "Body Surface Area (Mosteller)",
        area = "Anthropometry",
        citations = listOf("Mosteller RD. Simplified calculation of body-surface area. N Engl J Med 1987;317(17):1098."),
        icon = Icons.Outlined.Opacity,
        color = Color(0xFF1F9D6B)
    ),
    ClinicalReference(
        tool = "Adult BMI Categories",
        area = "Anthropometry",
        citations = listOf("Centers for Disease Control and Prevention. Adult BMI Categories."),
        icon = Icons.Outlined.MonitorWeight,
        color = Color(0xFF0984E3)
    ),
    ClinicalReference(
        tool = "BMI-for-Age",
        area = "Pediatrics",
        citations = listOf("Centers for Disease Control and Prevention. BMI-for-Age Growth Charts."),
        icon = Icons.Outlined.MonitorWeight,
        color = Color(0xFF0984E3)
    ),
    ClinicalReference(
        tool = "CHA2DS2-VASc Score",
        area = "Atrial fibrillation",
        citations = listOf(
            "Lip GYH et al. Chest 2010;137:263-272.",
            "Friberg L et al. Eur Heart J 2012;33:1500-1510.",
            "Joglar JA et al. Circulation 2024;149:e1-e156.",
            "Van Gelder IC et al. Eur Heart J 2024;45:3314-3414."
        ),
        icon = Icons.Outlined.FavoriteBorder,
        color = Color(0xFFD6456B)
    ),
    ClinicalReference(
        tool = "HAS-BLED Score",
        area = "Atrial fibrillation",
        citations = listOf(
            "Pisters R et al. Chest 2010;138:1093-1100.",
            "Lip GYH et al. J Am Coll Cardiol 2011;57:173-180.",
            "Joglar JA et al. Circulation 2024;149:e1-e156.",
            "Van Gelder IC et al. Eur Heart J 2024;45:3314-3414."
        ),
        icon = Icons.Outlined.FavoriteBorder,
        color = Color(0xFFD6456B)
    ),
    ClinicalReference(
        tool = "Wells Score for DVT",
        area = "Venous thromboembolism",
        citations = listOf(
            "Wells PS et al. Lancet 1997;350:1795-1798.",
            "Wells PS et al. N Engl J Med 2003;349:1227-1235.",
            "Bates SM et al. Chest 2012;141(2 Suppl):e351S-e418S.",
            "NICE NG158 (2020)."
        ),
        icon = Icons.Outlined.MedicalServices,
        color = Color(0xFF5B5FD6)
    ),
    ClinicalReference(
        tool = "Wells Score for PE",
        area = "Venous thromboembolism",
        citations = listOf(
            "Wells PS et al. Thromb Haemost 2000;83:416-420.",
            "Wells PS et al. Ann Intern Med 2001;135:98-107.",
            "Righini M et al. JAMA 2014;311:1117-1124.",
            "Konstantinides SV et al. Eur Heart J 2020;41:543-603."
        ),
        icon = Icons.Outlined.MedicalServices,
        color = Color(0xFF5B5FD6)
    ),
    ClinicalReference(
        tool = "Glasgow Coma Scale",
        area = "Neurology",
        citations = listOf(
            "Teasdale G, Jennett B. Lancet 1974;2:81-84.",
            "Teasdale G et al. Lancet Neurol 2014;13:844-854."
        ),
        icon = Icons.Outlined.MedicalServices,
        color = Color(0xFF5B5FD6)
    ),
    ClinicalReference(
        tool = "qSOFA",
        area = "Sepsis",
        citations = listOf(
            "Seymour CW et al. JAMA 2016;315:762-774.",
            "Singer M et al. JAMA 2016;315:801-810."
        ),
        icon = Icons.Outlined.MedicalServices,
        color = Color(0xFF5B5FD6)
    ),
    ClinicalReference(
        tool = "MELD 3.0",
        area = "Hepatology",
        citations = listOf(
            "Kim WR et al. Gastroenterology 2021;161:1887-1895.",
            "Kamath PS et al. Hepatology 2001;33:464-470.",
            "Malinchoc M et al. Hepatology 2000;31:864-871."
        ),
        icon = Icons.Outlined.Healing,
        color = Color(0xFF00A8A8)
    ),
    ClinicalReference(
        tool = "APGAR Score",
        area = "Neonatology",
        citations = listOf(
            "Apgar V. Curr Res Anesth Analg 1953;32:260-267.",
            "American Academy of Pediatrics and American College of Obstetricians and Gynecologists. Pediatrics 2015;136:819-822."
        ),
        icon = Icons.Outlined.ChildCare,
        color = Color(0xFF0984E3)
    ),
    ClinicalReference(
        tool = "QTc (Bazett)",
        area = "Cardiology",
        citations = listOf(
            "Bazett HC. Heart 1920;7:353-370.",
            "Rautaharju PM et al. Circulation 2009;119:e241-e250."
        ),
        icon = Icons.Outlined.FavoriteBorder,
        color = Color(0xFFD6456B)
    ),
    ClinicalReference(
        tool = "Shock Index",
        area = "Critical care",
        citations = listOf(
            "Allgöwer M, Burri C. Dtsch Med Wochenschr 1967;92:1947-1950.",
            "Rady MY et al. Ann Emerg Med 1994;24:685-690."
        ),
        icon = Icons.Outlined.FavoriteBorder,
        color = Color(0xFFD6456B)
    ),
    ClinicalReference(
        tool = "Mean Arterial Pressure",
        area = "Critical care",
        citations = listOf(
            "Evans L et al. Crit Care Med 2021;49:e1063-e1143."
        ),
        icon = Icons.Outlined.FavoriteBorder,
        color = Color(0xFFD6456B)
    ),
    ClinicalReference(
        tool = "Anion Gap",
        area = "Acid-base",
        citations = listOf(
            "Emmett M, Narins RG. Medicine (Baltimore) 1977;56:38-54."
        ),
        icon = Icons.Outlined.Science,
        color = Color(0xFF7B4FE0)
    ),
    ClinicalReference(
        tool = "P/F Ratio",
        area = "Respiratory",
        citations = listOf(
            "ARDS Definition Task Force. JAMA 2012;307:2526-2533."
        ),
        icon = Icons.Outlined.Science,
        color = Color(0xFF7B4FE0)
    ),
    ClinicalReference(
        tool = "Corrected Calcium",
        area = "Electrolytes",
        citations = listOf(
            "Payne RB et al. BMJ 1973;4:643-646."
        ),
        icon = Icons.Outlined.Science,
        color = Color(0xFF7B4FE0)
    ),
    ClinicalReference(
        tool = "Corrected Sodium",
        area = "Electrolytes",
        citations = listOf(
            "Katz MA. N Engl J Med 1973;289:843-844.",
            "Hillier TA et al. Am J Med 1999;106:399-403."
        ),
        icon = Icons.Outlined.Science,
        color = Color(0xFF7B4FE0)
    ),
    ClinicalReference(
        tool = "BUN/Creatinine Ratio",
        area = "Renal function",
        citations = listOf(
            "Hosten AO. BUN and creatinine. In: Clinical Methods, 3rd ed. Butterworths; 1990. Chapter 193."
        ),
        icon = Icons.Outlined.Healing,
        color = Color(0xFF00A8A8)
    ),
    ClinicalReference(
        tool = "IV Drip Rate",
        area = "Dosing",
        citations = listOf(
            "Standard arithmetic calculation. No dedicated primary publication."
        ),
        icon = Icons.Outlined.Vaccines,
        color = Color(0xFF1F9D6B)
    ),
    ClinicalReference(
        tool = "Infusion Rate",
        area = "Dosing",
        citations = listOf(
            "Standard arithmetic calculation. No dedicated primary publication."
        ),
        icon = Icons.Outlined.Vaccines,
        color = Color(0xFF1F9D6B)
    ),
    ClinicalReference(
        tool = "CURB-65",
        area = "Pneumonia",
        citations = listOf(
            "Lim WS et al. Thorax 2003;58:377-382.",
            "British Thoracic Society. Guidelines for the management of community-acquired pneumonia in adults."
        ),
        icon = Icons.Outlined.MedicalServices,
        color = Color(0xFF5B5FD6)
    ),
    ClinicalReference(
        tool = "HEART Score",
        area = "Chest pain",
        citations = listOf(
            "Six AJ et al. Neth Heart J 2008;16:191-196.",
            "Backus BE et al. Int J Cardiol 2013;168:2153-2158.",
            "Gulati M et al. 2021 AHA/ACC/ASE/CHEST/SAEM/SCCT/SCMR Guideline for the Evaluation and Diagnosis of Chest Pain. Circulation 2021;144:e368-e454."
        ),
        icon = Icons.Outlined.FavoriteBorder,
        color = Color(0xFFD6456B)
    ),
    ClinicalReference(
        tool = "Child-Pugh Score",
        area = "Hepatology",
        citations = listOf(
            "Child CG, Turcotte JG. Surgery and portal hypertension. In: The Liver and Portal Hypertension. 1964.",
            "Pugh RNH et al. Br J Surg 1973;60:646-649.",
            "EASL Clinical Practice Guidelines for the management of patients with decompensated cirrhosis. J Hepatol 2018;69:406-460."
        ),
        icon = Icons.Outlined.Healing,
        color = Color(0xFF00A8A8)
    ),
    ClinicalReference(
        tool = "Centor Score",
        area = "Pharyngitis",
        citations = listOf(
            "Centor RM et al. Med Decis Making 1981;1:239-246.",
            "McIsaac WJ et al. CMAJ 1998;158:75-83.",
            "Shulman ST et al. Clin Infect Dis 2012;55:e86-e102.",
            "Harris AM et al. Ann Intern Med 2016;164:425-434."
        ),
        icon = Icons.Outlined.MedicalServices,
        color = Color(0xFF5B5FD6)
    ),
    ClinicalReference(
        tool = "eGFR (CKD-EPI 2021)",
        area = "Renal function",
        citations = listOf(
            "Inker LA et al. N Engl J Med 2021;385:1737-1749."
        ),
        icon = Icons.Outlined.Healing,
        color = Color(0xFF00A8A8)
    ),
    ClinicalReference(
        tool = "Weight-Based Dose",
        area = "Dosing",
        citations = listOf(
            "Standard arithmetic calculation. No dedicated primary publication."
        ),
        icon = Icons.Outlined.Vaccines,
        color = Color(0xFF1F9D6B)
    ),
    ClinicalReference(
        tool = "A-a Gradient",
        area = "Respiratory",
        citations = listOf(
            "Standard formula (alveolar gas equation). No dedicated primary publication."
        ),
        icon = Icons.Outlined.Science,
        color = Color(0xFF7B4FE0)
    ),
    ClinicalReference(
        tool = "Pediatric Dose (Clark's Rule)",
        area = "Pediatrics",
        citations = listOf(
            "Historical weight-based rule of thumb, not a validated dosing method. Always verify against a drug-specific pediatric reference."
        ),
        icon = Icons.Outlined.ChildCare,
        color = Color(0xFF0984E3)
    )
)

private val dataSources = listOf(
    DataSource(
        name = "FDA Drug Labels",
        publisher = "U.S. FDA, via openFDA and DailyMed (NLM)",
        usedFor = "Drug label sections: dosing, renal and hepatic dosing, warnings, adverse reactions, pregnancy, pharmacodynamics and pharmacokinetics.",
        license = "Public FDA-approved labeling",
        attribution = "Source: openFDA and DailyMed, U.S. National Library of Medicine (dailymed.nlm.nih.gov). Label text is as published by each manufacturer and can change, so always check the current prescribing information.",
        icon = Icons.Outlined.MedicalServices,
        color = Color(0xFF5B5FD6)
    ),
    DataSource(
        name = "RxNorm",
        publisher = "U.S. National Library of Medicine",
        usedFor = "Drug names, ingredients, brand names and identifiers.",
        license = "NLM terms of service",
        attribution = "This product uses publicly available data from the U.S. National Library of Medicine (NLM), National Institutes of Health, Department of Health and Human Services. NLM is not responsible for the product and does not endorse or recommend this or any other product.",
        icon = Icons.Outlined.Vaccines,
        color = Color(0xFF1F9D6B)
    ),
    DataSource(
        name = "LactMed",
        publisher = "U.S. National Library of Medicine",
        usedFor = "Drug safety information for breastfeeding: levels in milk, effects on the infant and alternatives.",
        license = "U.S. government resource",
        attribution = "Source: Drugs and Lactation Database (LactMed), National Library of Medicine.",
        icon = Icons.Outlined.FavoriteBorder,
        color = Color(0xFFD6456B)
    ),
    DataSource(
        name = "DDInter 2.0",
        publisher = "Xiangya Hospital, Central South University",
        usedFor = "Drug-drug interaction checker: pairs, severity and management advice.",
        license = "CC BY-NC-SA 4.0",
        attribution = "Interaction data from DDInter 2.0 (ddinter.scbdd.com), licensed under Creative Commons Attribution-NonCommercial-ShareAlike 4.0.",
        icon = Icons.Outlined.Science,
        color = Color(0xFF0984E3)
    )
)

@Composable
fun ClinicalSourcesScreen(onBackClick: () -> Unit) {
    BackHandler { onBackClick() }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PremiumBackButton(onClick = onBackClick)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Clinical Sources",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 26.sp
                    )
                    Text(
                        "References, data sources and licenses",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        item { SectionHeader("Clinical References", clinicalReferences.size) }
        items(clinicalReferences.sortedBy { it.tool }, key = { it.tool }) { ReferenceCard(it) }
        item { SectionHeader("Data Sources and Licenses", dataSources.size) }
        items(dataSources, key = { it.name }) { SourceCard(it) }
        item { SourcesFooter() }
        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
private fun SourceCard(s: DataSource) {
    val shape = RoundedCornerShape(18.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f), shape)
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(s.color.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = s.icon, contentDescription = null, tint = s.color, modifier = Modifier.size(19.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(s.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(s.publisher, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Spacer(Modifier.height(12.dp))
        FieldLabel("Used for")
        Text(s.usedFor, style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp, lineHeight = 19.sp))
        Spacer(Modifier.height(10.dp))
        FieldLabel("License")
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                s.license,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
        Spacer(Modifier.height(10.dp))
        FieldLabel("Attribution")
        Text(
            s.attribution,
            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 17.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.5.sp),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(bottom = 3.dp)
    )
}

@Composable
private fun SourcesFooter() {
    val shape = RoundedCornerShape(14.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            .padding(14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(10.dp))
        Text(
            "For clinical reference only. Always verify against current prescribing information before making clinical decisions.",
            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 17.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SectionHeader(title: String, count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            title.uppercase(),
            style = MaterialTheme.typography.titleSmall.copy(letterSpacing = 0.6.sp),
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )
        Text(
            count.toString(),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ReferenceCard(r: ClinicalReference) {
    val shape = RoundedCornerShape(18.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f), shape)
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(r.color.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = r.icon, contentDescription = null, tint = r.color, modifier = Modifier.size(19.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(r.tool, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(r.area, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Spacer(Modifier.height(12.dp))
        r.citations.forEachIndexed { i, c ->
            Row(modifier = Modifier.padding(bottom = if (i < r.citations.lastIndex) 8.dp else 0.dp)) {
                Text(
                    "${i + 1}.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.width(22.dp)
                )
                Text(
                    c,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp, lineHeight = 18.sp),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
