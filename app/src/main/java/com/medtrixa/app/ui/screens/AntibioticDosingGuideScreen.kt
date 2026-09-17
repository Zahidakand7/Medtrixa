package com.medtrixa.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private data class AntibioticRegimen(
    val indication: String,
    val setting: String,
    val organisms: String,
    val antibiotic: String,
    val dose: String,
    val route: String,
    val frequency: String,
    val duration: String,
    val patientGroup: String,
    val renalAdjustment: String,
    val alternatives: String,
    val notes: String,
    val source: String,
    val sourceStatus: String,
    val reviewDate: String
)

private val antibioticRegimens = listOf(
    // =========================
    // IDSA AMR GUIDANCE 2026
    // Published July 30, 2026
    // Current — replaces earlier IDSA AMR guidance
    // =========================

    AntibioticRegimen(
        indication = "ESBL-producing Enterobacterales (ESBL-E) — uncomplicated cystitis",
        setting = "Uncomplicated UTI",
        organisms = "ESBL-producing Enterobacterales",
        antibiotic = "Nitrofurantoin",
        dose = "100 mg",
        route = "PO",
        frequency = "Every 12 hours",
        duration = "Refer to syndrome-specific guidance",
        patientGroup = "Adult",
        renalAdjustment = "Use only when renal function and product labeling permit.",
        alternatives = "Aminoglycoside single dose; gepotidacin; pivmecillinam; sulopenem; TMP-SMX — all are preferred options with no preferential order. Oral fosfomycin is an alternative for ESBL-producing E. coli.",
        notes = "2026 IDSA AMR preferred options are listed alphabetically and are not presented as a universal ranking. Use susceptibility and clinical context.",
        source = "IDSA 2026 AMR Guidance",
        sourceStatus = "Current",
        reviewDate = "2026-09"
    ),

    AntibioticRegimen(
        indication = "ESBL-producing Enterobacterales (ESBL-E) — complicated UTI",
        setting = "Complicated UTI",
        organisms = "ESBL-producing Enterobacterales",
        antibiotic = "TMP-SMX",
        dose = "Use susceptibility-guided dosing",
        route = "PO/IV",
        frequency = "Per product labeling and clinical context",
        duration = "IDSA cUTI duration guidance applies",
        patientGroup = "Adult",
        renalAdjustment = "Renal adjustment required when indicated.",
        alternatives = "Ciprofloxacin or levofloxacin if susceptible; cefepime-enmetazobactam or carbapenem when oral options cannot be used; IV fosfomycin, aminoglycoside or piperacillin-tazobactam as alternatives in selected cases.",
        notes = "2026 AMR guidance requires confirmed in-vitro susceptibility. Transition to an active oral agent may be appropriate when clinical criteria are met.",
        source = "IDSA 2026 AMR Guidance / IDSA 2025 cUTI Guideline",
        sourceStatus = "Current",
        reviewDate = "2026-09"
    ),

    AntibioticRegimen(
        indication = "ESBL-producing Enterobacterales — invasive infection",
        setting = "Bloodstream or other invasive infection",
        organisms = "ESBL-producing Enterobacterales",
        antibiotic = "Meropenem",
        dose = "Per IDSA Table 1 / renal function",
        route = "IV",
        frequency = "Per IDSA dosing table and renal function",
        duration = "Individualize by infection syndrome and response",
        patientGroup = "Adult",
        renalAdjustment = "Renal adjustment required.",
        alternatives = "Ertapenem or imipenem when appropriate; cefepime-enmetazobactam has a defined role in selected ESBL-E infections.",
        notes = "For invasive ESBL-E infections, carbapenem therapy remains an important preferred approach. Do not use susceptibility-independent empiric selection as a substitute for microbiology.",
        source = "IDSA 2026 AMR Guidance",
        sourceStatus = "Current",
        reviewDate = "2026-09"
    ),

    AntibioticRegimen(
        indication = "AmpC-producing Enterobacterales — invasive infection",
        setting = "Invasive infection caused by organisms at moderate risk of clinically significant inducible AmpC production",
        organisms = "Enterobacter cloacae complex; Klebsiella aerogenes; Citrobacter freundii; Hafnia alvei",
        antibiotic = "Cefepime",
        dose = "Per IDSA Table 1 / renal function",
        route = "IV",
        frequency = "Per IDSA dosing table and renal function",
        duration = "Individualize by syndrome and clinical response",
        patientGroup = "Adult",
        renalAdjustment = "Renal adjustment required when indicated.",
        alternatives = "Carbapenem or other susceptibility-directed therapy when clinically appropriate.",
        notes = "2026 guidance does not suggest ceftriaxone/cefotaxime/ceftazidime for invasive infection in organisms at moderate risk of clinically significant AmpC production.",
        source = "IDSA 2026 AMR Guidance",
        sourceStatus = "Current",
        reviewDate = "2026-09"
    ),

    AntibioticRegimen(
        indication = "CRE infection",
        setting = "Carbapenem-resistant Enterobacterales infection",
        organisms = "CRE",
        antibiotic = "Mechanism-directed therapy",
        dose = "Per IDSA Table 1 / renal function",
        route = "IV",
        frequency = "Per IDSA dosing table and renal function",
        duration = "Individualize by infection syndrome",
        patientGroup = "Adult",
        renalAdjustment = "Renal adjustment required when indicated.",
        alternatives = "KPC-producing CRE: ceftazidime-avibactam, meropenem-vaborbactam, or imipenem-relebactam. NDM-producing CRE: aztreonam-avibactam or cefiderocol. OXA-48-like CRE: ceftazidime-avibactam. Non-carbapenemase-producing CRE: select therapy according to carbapenem susceptibility and clinical status.",
        notes = "CRE treatment must be selected according to carbapenemase mechanism, carbapenem susceptibility, infection site, and susceptibility testing. Combination therapy is not routinely suggested once an active beta-lactam has been identified.",
        source = "IDSA 2026 AMR Guidance",
        sourceStatus = "Current",
        reviewDate = "2026-09"
    ),

    AntibioticRegimen(
        indication = "NDM-producing Enterobacterales infection",
        setting = "Carbapenem-resistant infection with metallo-beta-lactamase mechanism",
        organisms = "NDM-producing Enterobacterales",
        antibiotic = "Aztreonam-avibactam",
        dose = "Per IDSA Table 1 / renal function",
        route = "IV",
        frequency = "Per IDSA dosing table and renal function",
        duration = "Individualize by infection syndrome",
        patientGroup = "Adult",
        renalAdjustment = "Renal adjustment required when indicated.",
        alternatives = "Ceftazidime-avibactam plus aztreonam; cefiderocol in appropriate susceptibility/resistance contexts.",
        notes = "2026 IDSA notes a slight preference for aztreonam-avibactam over ceftazidime-avibactam plus aztreonam for NDM-producing Enterobacterales.",
        source = "IDSA 2026 AMR Guidance",
        sourceStatus = "Current",
        reviewDate = "2026-09"
    ),

    AntibioticRegimen(
        indication = "DTR Pseudomonas aeruginosa — complicated UTI",
        setting = "Complicated UTI caused by DTR P. aeruginosa",
        organisms = "DTR Pseudomonas aeruginosa",
        antibiotic = "Ceftazidime-avibactam",
        dose = "Per IDSA Table 1 / renal function",
        route = "IV",
        frequency = "Per IDSA dosing table and renal function",
        duration = "Individualize by infection syndrome",
        patientGroup = "Adult",
        renalAdjustment = "Renal adjustment required when indicated.",
        alternatives = "Cefiderocol; ceftolozane-tazobactam; imipenem-relebactam; once-daily amikacin or tobramycin as alternatives.",
        notes = "The four preferred beta-lactams are listed alphabetically without an overall order of preference.",
        source = "IDSA 2026 AMR Guidance",
        sourceStatus = "Current",
        reviewDate = "2026-09"
    ),

    AntibioticRegimen(
        indication = "DTR Pseudomonas aeruginosa — non-urinary infection",
        setting = "Pneumonia or other infection outside urinary tract",
        organisms = "DTR Pseudomonas aeruginosa",
        antibiotic = "Ceftolozane-tazobactam",
        dose = "Per IDSA Table 1 / renal function",
        route = "IV",
        frequency = "Per IDSA dosing table and renal function",
        duration = "Individualize by infection syndrome",
        patientGroup = "Adult",
        renalAdjustment = "Renal adjustment required when indicated.",
        alternatives = "Ceftazidime-avibactam; imipenem-relebactam are preferred alternatives according to susceptibility. Cefiderocol is an alternative for non-urinary infection.",
        notes = "For DTR P. aeruginosa infections outside the urinary tract, IDSA 2026 lists ceftazidime-avibactam, ceftolozane-tazobactam, and imipenem-relebactam as preferred options. For pneumonia, ceftolozane-tazobactam is preferred. Cefiderocol is an alternative.",
        source = "IDSA 2026 AMR Guidance",
        sourceStatus = "Current",
        reviewDate = "2026-09"
    ),

    AntibioticRegimen(
        indication = "Carbapenem-resistant Acinetobacter baumannii (CRAB)",
        setting = "Invasive CRAB infection",
        organisms = "CRAB",
        antibiotic = "Sulbactam-durlobactam + carbapenem",
        dose = "Per IDSA Table 1 / renal function",
        route = "IV",
        frequency = "Per IDSA dosing table and renal function",
        duration = "Individualize by infection syndrome",
        patientGroup = "Adult",
        renalAdjustment = "Renal adjustment required when indicated.",
        alternatives = "Alternative non-sulbactam combination therapy only when sulbactam-durlobactam is unavailable or resistance is present.",
        notes = "2026 IDSA identifies sulbactam-durlobactam in combination with a carbapenem as the priority approach for invasive CRAB infection.",
        source = "IDSA 2026 AMR Guidance",
        sourceStatus = "Current",
        reviewDate = "2026-09"
    ),

    AntibioticRegimen(
        indication = "Stenotrophomonas maltophilia infection",
        setting = "Clinically significant infection",
        organisms = "Stenotrophomonas maltophilia",
        antibiotic = "Susceptibility-directed combination/selected therapy",
        dose = "Per IDSA 2026 AMR dosing table",
        route = "PO/IV",
        frequency = "Per selected agent and renal function",
        duration = "Individualize by infection syndrome",
        patientGroup = "Adult",
        renalAdjustment = "Agent-specific renal adjustment required when applicable.",
        alternatives = "Aztreonam-avibactam, preferably in combination with a second active agent, is an alternative option.",
        notes = "For invasive S. maltophilia infection, 2026 IDSA designates cefiderocol monotherapy as the preferred treatment. Treatment should be based on susceptibility and clinical assessment; distinguish infection from colonization.",
        source = "IDSA 2026 AMR Guidance",
        sourceStatus = "Current",
        reviewDate = "2026-09"
    ),

    // =========================
    // IDSA 2025 cUTI
    // =========================

    AntibioticRegimen(
        indication = "Complicated urinary tract infection",
        setting = "Clinically improving patient initially treated parenterally",
        organisms = "Pathogen-directed cUTI",
        antibiotic = "Effective oral antibiotic",
        dose = "According to selected agent",
        route = "PO",
        frequency = "According to selected agent",
        duration = "Usually complete the recommended short course",
        patientGroup = "Adult",
        renalAdjustment = "Agent-specific.",
        alternatives = "Continue parenteral therapy when oral therapy is not effective, feasible or appropriate.",
        notes = "IDSA 2025 suggests IV-to-oral transition when the patient is clinically improving, can take oral medication, and an effective oral option is available.",
        source = "IDSA 2025 cUTI Guideline",
        sourceStatus = "Current",
        reviewDate = "2026-09"
    ),

    AntibioticRegimen(
        indication = "Complicated UTI — duration",
        setting = "Clinically improving cUTI",
        organisms = "Pathogen-directed",
        antibiotic = "Effective antimicrobial",
        dose = "Agent-specific",
        route = "PO/IV",
        frequency = "Agent-specific",
        duration = "5–7 days for fluoroquinolone OR 7 days for non-fluoroquinolone in typical improving cases",
        patientGroup = "Adult",
        renalAdjustment = "Agent-specific.",
        alternatives = "Individualize for subgroups excluded from trials or when clinical response/source control is inadequate.",
        notes = "For cUTI with Gram-negative bacteremia and clinical improvement, IDSA suggests 7 days of effective therapy. Duration is counted from the first day of effective therapy.",
        source = "IDSA 2025 cUTI Guideline",
        sourceStatus = "Current",
        reviewDate = "2026-09"
    ),

    // =========================
    // CURRENT ADULT CAP
    // =========================

    AntibioticRegimen(
        indication = "Community-acquired pneumonia (CAP)",
        setting = "Outpatient healthy adult without comorbidities",
        organisms = "Typical and atypical respiratory pathogens",
        antibiotic = "Amoxicillin",
        dose = "1 g",
        route = "PO",
        frequency = "Every 8 hours",
        duration = "At least 5 days and until clinically stable",
        patientGroup = "Adult",
        renalAdjustment = "Adjust for renal function as appropriate.",
        alternatives = "Doxycycline; macrolide only where guideline resistance criteria are satisfied.",
        notes = "2019 ATS/IDSA adult CAP guideline remains listed as CURRENT by IDSA.",
        source = "ATS/IDSA 2019 CAP Guideline",
        sourceStatus = "Current IDSA-listed guideline",
        reviewDate = "2026-09"
    ),

    AntibioticRegimen(
        indication = "Community-acquired pneumonia (CAP)",
        setting = "Hospitalized adult, non-severe CAP",
        organisms = "Typical and atypical respiratory pathogens",
        antibiotic = "Ceftriaxone + azithromycin",
        dose = "Ceftriaxone 1–2 g + azithromycin 500 mg",
        route = "IV/PO",
        frequency = "Every 24 hours",
        duration = "At least 5 days and until clinically stable",
        patientGroup = "Adult",
        renalAdjustment = "Agent-specific.",
        alternatives = "Ampicillin/sulbactam + azithromycin; respiratory fluoroquinolone monotherapy.",
        notes = "2019 ATS/IDSA adult CAP guideline remains current in the IDSA catalog.",
        source = "ATS/IDSA 2019 CAP Guideline",
        sourceStatus = "Current IDSA-listed guideline",
        reviewDate = "2026-09"
    ),

    // =========================
    // CURRENT SSTI
    // =========================

    AntibioticRegimen(
        indication = "Non-purulent cellulitis",
        setting = "Mild outpatient infection",
        organisms = "Beta-hemolytic streptococci; possible MSSA",
        antibiotic = "Cephalexin",
        dose = "500 mg",
        route = "PO",
        frequency = "Every 6 hours",
        duration = "At least 5 days; extend if not clinically improved",
        patientGroup = "Adult",
        renalAdjustment = "Adjust for renal function when indicated.",
        alternatives = "Penicillin, amoxicillin, dicloxacillin or clindamycin depending on clinical context.",
        notes = "IDSA SSTI guideline remains listed as CURRENT.",
        source = "IDSA Skin and Soft Tissue Infection Guideline 2014",
        sourceStatus = "Current IDSA-listed guideline",
        reviewDate = "2026-09"
    ),

    // =========================
    // ARCHIVED — RETAINED ONLY
    // =========================

    AntibioticRegimen(
        indication = "Acute bacterial rhinosinusitis",
        setting = "Adult meeting bacterial diagnostic criteria",
        organisms = "S. pneumoniae; H. influenzae; M. catarrhalis",
        antibiotic = "Amoxicillin/clavulanate",
        dose = "500/125 mg or 875/125 mg",
        route = "PO",
        frequency = "Every 8 or 12 hours depending on formulation",
        duration = "5–7 days",
        patientGroup = "Adult",
        renalAdjustment = "Dose/formulation requires renal-function assessment.",
        alternatives = "Doxycycline or selected respiratory fluoroquinolone depending on clinical context.",
        notes = "RETAINED FOR REFERENCE ONLY. The 2012 IDSA acute bacterial rhinosinusitis guideline is ARCHIVED and must not be presented as a current IDSA recommendation.",
        source = "IDSA Acute Bacterial Rhinosinusitis Guideline 2012",
        sourceStatus = "ARCHIVED — reference only",
        reviewDate = "2026-09"
    )
)
private fun AntibioticRegimen.matches(query: String): Boolean {
    if (query.isBlank()) return true

    val q = query.trim().lowercase()

    val searchableText = listOf(
        indication,
        setting,
        organisms,
        antibiotic,
        dose,
        route,
        frequency,
        duration,
        patientGroup,
        renalAdjustment,
        alternatives,
        notes,
        source
    ).joinToString(" ").lowercase()

    return q.split(Regex("\\s+"))
        .filter { it.isNotBlank() }
        .all { searchableText.contains(it) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AntibioticDosingGuideScreen(
    onBackClick: () -> Unit
) {
    var query by remember { mutableStateOf("") }

    val results = remember(query) {
        antibioticRegimens.filter { it.matches(query) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Antibiotic Dosing Guide") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
item {
    ClinicalGuidelineCard(
        title = "🦠 Bacterial Atlas",
        subtitle = "Searchable organism reference — identification, laboratory characteristics, clinical syndromes, resistance and taxonomy",
        sections = listOf(
            "How it will work" to
                "Search by organism, Gram reaction, morphology, oxygen requirement, clinical syndrome or resistance phenotype. Select an organism to open its complete profile.",
            "Profile categories" to
                "Identification • Gram stain • morphology • arrangement • oxygen requirement • spores • motility • catalase/oxidase • culture clues • reservoir • transmission • clinical syndromes • specimen selection • colonization vs infection • virulence • resistance mechanisms • taxonomy • clinical pearls.",
            "Important limitation" to
                "The atlas will be expandable rather than claiming that a finite mobile dataset contains literally every bacterium on Earth. The initial dataset will prioritize clinically important organisms and can be expanded without redesigning the interface."
        ),
        source = "Clinical microbiology reference module — expandable dataset",
        status = "STRUCTURE READY — organism dataset to be expanded"
    )
}
item {
    ClinicalGuidelineCard(
        title = "💊 Antibiotic Explorer",
        subtitle = "Drug-focused reference linked conceptually to organism and guideline information",
        sections = listOf(
            "Each antibiotic profile" to
                "Class • mechanism • spectrum • Gram-positive activity • Gram-negative activity • anaerobic activity • Pseudomonas activity • important resistance mechanisms • major indications • route • renal/hepatic considerations • important adverse effects • interactions • monitoring • guideline references.",
            "Clinical decision support" to
                "The explorer should distinguish spectrum from guideline-preferred therapy. A drug can have microbiologic activity without being a preferred treatment for a particular syndrome.",
            "Intern workflow" to
                "Start with syndrome and patient factors, identify the likely organism/resistance phenotype, review susceptibility, then use the antibiotic profile to confirm spectrum, dosing considerations, toxicity and monitoring."
        ),
        source = "Antibiotic reference architecture aligned with current IDSA clinical guidance",
        status = "STRUCTURE READY — drug dataset to be expanded"
    )
}

            item {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = null
                        )
                    },
                    label = {
                        Text("Search indication, organism or antibiotic")
                    },
                    placeholder = {
                        Text("e.g. pneumonia, ceftriaxone, MRSA")
                    }
                )
            }

            item {
                Text(
                    text = if (query.isBlank()) {
                        "${results.size} verified reference entries"
                    } else {
                        "${results.size} result${if (results.size == 1) "" else "s"}"
                    },
                    style = MaterialTheme.typography.titleMedium
                )
            }

            if (results.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp)
                        ) {
                            Text(
                                text = "No matching regimen",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Try an indication, organism, or antibiotic name.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            } else {
                items(
                    items = results,
                    key = {
                        "${it.indication}|${it.setting}|${it.antibiotic}|${it.dose}"
                    }
                ) { regimen ->

                    AntibioticRegimenCard(regimen)
                }
            }

            item {
    ClinicalGuidelineCard(
        title = "2026 IDSA Clinical Guidelines — Intern Quick Reference",
        subtitle = "Evidence-based clinical workflow • Verify patient-specific factors, local antibiogram, susceptibility results and current full guideline before prescribing",
        sections = listOf(
            "1. Before starting antibiotics" to
                "Assess infection source and severity; obtain appropriate cultures when this will not dangerously delay therapy; review prior microbiology from the previous 12 months, recent antibiotic exposure, allergy history, renal/hepatic function, immune status, devices and local resistance patterns. Reassess empiric therapy once organism identification and susceptibility results are available.",

            "2. Antimicrobial stewardship" to
                "Do not treat colonization as infection. Select the narrowest effective active regimen, use the correct dose and interval, obtain source control when required, reassess clinical response and microbiology, and de-escalate when adequate susceptibility information becomes available.",

            "3. IV → oral transition" to
                "For appropriate infections, transition from IV to oral therapy when the pathogen is susceptible to an appropriate oral agent, the patient is hemodynamically stable, the oral drug is expected to reach the infection site, and gastrointestinal absorption is adequate.",

            "4. ESBL-producing Enterobacterales" to
                "ESBLs commonly inactivate penicillins, cephalosporins and aztreonam. Common organisms include E. coli, Klebsiella pneumoniae and K. oxytoca. For uncomplicated cystitis, 2026 IDSA lists aminoglycoside single-dose therapy, gepotidacin, nitrofurantoin, pivmecillinam, sulopenem and TMP-SMX as preferred options when appropriate; oral fosfomycin is an alternative for ESBL-producing E. coli. For cUTI, susceptible TMP-SMX, ciprofloxacin or levofloxacin are preferred oral options; cefepime-enmetazobactam or a carbapenem may be used when oral options cannot be used. Treatment of invasive infection is generally carbapenem-centered, with selection individualized to susceptibility and site.",

            "5. AmpC-producing Enterobacterales" to
                "Important organisms include Enterobacter cloacae complex, Klebsiella aerogenes and Citrobacter freundii; Hafnia alvei is also recognized as having moderate risk. Cefepime has an important role for invasive infections when active. Third-generation cephalosporins and piperacillin-tazobactam require careful consideration in organisms at moderate risk for clinically significant inducible AmpC, particularly invasive disease. In non-severe infection where ceftriaxone was started empirically and the patient improves with adequate source control, continuation may be reasonable.",

            "6. Carbapenem-resistant Enterobacterales (CRE)" to
                "Treatment should be mechanism-directed. KPC-producing CRE: ceftazidime-avibactam, meropenem-vaborbactam or imipenem-relebactam. NDM-producing CRE: aztreonam-avibactam or cefiderocol; 2026 guidance gives a slight preference to aztreonam-avibactam, with ceftazidime-avibactam plus aztreonam also reasonable. OXA-48-like CRE: ceftazidime-avibactam. Non-carbapenemase CRE requires susceptibility- and infection-site-directed therapy. Identify the resistance mechanism whenever clinically relevant.",

            "7. DTR Pseudomonas aeruginosa" to
                "For DTR P. aeruginosa cUTI, preferred active beta-lactams include ceftazidime-avibactam, ceftolozane-tazobactam, imipenem-relebactam and cefiderocol; once-daily amikacin or tobramycin are alternatives in appropriate patients. For non-urinary infection, preferred agents include ceftazidime-avibactam, ceftolozane-tazobactam and imipenem-relebactam, with cefiderocol as an alternative. For pneumonia, updated evidence supports preference for ceftolozane-tazobactam. Use susceptibility results and consider resistance emergence during therapy.",

            "8. Carbapenem-resistant Acinetobacter baumannii (CRAB)" to
                "The 2026 guidance applies to invasive infection, not colonization. Sulbactam-durlobactam in combination with a carbapenem is the priority approach when available. If unavailable, alternative regimens are used as bridge therapy. If resistance to sulbactam-durlobactam is present, approaches include two non-sulbactam active agents or sulbactam-durlobactam combined with cefiderocol. Therapy should be guided by susceptibility, infection site and clinical response.",

            "9. Stenotrophomonas maltophilia" to
                "Treat infection, not colonization. For invasive S. maltophilia infection, cefiderocol monotherapy is the preferred approach in the 2026 IDSA guidance, although supportive clinical data remain limited and susceptibility should be confirmed. Aztreonam-avibactam, preferably combined with a second active agent, is an alternative.",

            "10. Complicated UTI (cUTI)" to
                "The 2025 IDSA cUTI guideline provides a stepwise approach to classification, empiric antibiotic selection, IV-to-oral transition and duration. cUTI includes infections extending beyond the bladder, such as pyelonephritis, febrile or bacteremic UTI, catheter-associated UTI and prostatitis. Once susceptibility results are available, inactive empiric therapy should generally be changed to an active agent and duration counted from the first day of effective therapy.",

            "11. cUTI treatment duration" to
                "In typical clinically improving patients, the 2025 guideline supports shorter courses: generally 5–7 days for fluoroquinolones or 7 days for non-fluoroquinolone therapy, with important exclusions and individualization. Gram-negative bacteremia associated with cUTI may often be treated for 7 days when clinically improving on effective therapy. Do not apply a fixed duration without considering source control, obstruction, abscess, immunocompromise, prostatitis and clinical response.",

            "12. Staphylococcus aureus bacteremia — 2026" to
                "The new IDSA/ESCMID Part 1 framework replaces the simplistic 'complicated vs uncomplicated' concept with low-risk vs increased-risk assessment for deep-seated or metastatic infection and relapse. Initial evaluation includes detailed history and examination, follow-up blood cultures and TTE. Prompt removal of an infected central venous catheter is recommended when appropriate. Infectious-diseases consultation is strongly encouraged.",

            "13. SAB — follow-up blood cultures" to
                "Adults: obtain at least 2 blood-culture sets 48 hours after the first positive culture, then 1–2 sets every 24–48 hours until clearance. A positive culture at or beyond 48 hours should trigger reassessment for inadequate source control and deep-seated or metastatic infection. The clearance date is used as the starting point for calculating bacteremia treatment duration.",

            "14. SAB — echocardiography" to
                "Adults with SAB should routinely undergo TTE. After a negative TTE, TEE is suggested when increased-risk features are present, including an intracardiac device, predisposing valve disease or previous endocarditis, positive follow-up cultures at or beyond 48 hours, embolic events, or multiple non-contiguous infection foci. TEE may be considered with community-onset SAB or injection drug use depending on the overall risk and expected effect on management.",

            "15. SAB — metastatic infection search" to
                "Patients with increased-risk SAB and an unknown focus after appropriate initial evaluation may require whole-body imaging such as FDG-PET/CT or targeted combinations of imaging. Imaging should also be symptom-directed, such as spine MRI for compatible back pain. Reassess repeatedly because SAB risk is dynamic and a patient initially considered low risk can become increased risk.",

            "16. SAB — treatment duration" to
                "For adults classified as low risk and without deep-seated or metastatic infection after appropriate evaluation, the 2026 consensus suggests 14 days. Adults initially classified as increased risk but ultimately found to have no deep-seated or metastatic focus may also receive 14 days if signs and symptoms resolve and diagnostic evaluation is adequate; longer therapy should be considered when deep infection cannot be confidently excluded, particularly with prolonged bacteremia. In children without deep-seated or metastatic infection after evaluation, 14 days is suggested, but pediatric evidence is more limited.",

            "17. CAP and common bacterial syndromes" to
                "Use syndrome-based therapy rather than organism-based memorization alone. For community-acquired pneumonia, select empiric therapy according to severity, comorbidities, risk factors for MRSA or Pseudomonas, local resistance patterns and current CAP guidance. For skin/soft-tissue infections, distinguish purulent from nonpurulent disease, assess severity and prioritize drainage/source control when indicated.",

            "18. Culture and susceptibility interpretation" to
                "A susceptibility result must be interpreted together with infection site, achievable drug exposure, organism, resistance mechanism and patient factors. Do not assume that an in-vitro susceptible result automatically makes an agent clinically appropriate for every site. For AMR infections, the 2026 IDSA guidance assumes identification of the pathogen and confirmation of in-vitro susceptibility to proposed agents.",

            "19. Intern bedside checklist" to
                "1) What is the suspected source? 2) How sick is the patient? 3) What cultures/specimens are needed? 4) What did previous cultures show? 5) What antibiotics were used recently? 6) What resistance is likely locally? 7) Is there a device, obstruction, abscess or other source-control problem? 8) Is the chosen drug active at this infection site? 9) Is renal/hepatic dose adjustment required? 10) When will therapy be reassessed and narrowed?",

            "20. High-value safety reminders" to
                "Do not treat colonization as infection. Do not continue inactive empiric therapy simply because the patient initially improved when the infection requires active treatment. Do not overlook source control. Do not assume a single duration fits every patient. Always check the full current guideline, susceptibility report, patient-specific contraindications and local antimicrobial policy before prescribing."
        ),
        source = "IDSA 2026 AMR Guidance; IDSA/ESCMID 2026 SAB Consensus Statements; IDSA 2025 cUTI Guideline; current IDSA syndrome-specific guidance",
        status = "CURRENT — 2026 clinical reference"
    )
}

item {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Clinical reference notice",
                    style = MaterialTheme.typography.titleSmall
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "This guide is a reference aid, not a substitute for patient-specific clinical judgment, local antibiograms, culture results, allergy assessment, renal/hepatic function, drug interactions, or institutional protocols.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


@Composable
private fun ClinicalGuidelineCard(
    title: String,
    subtitle: String,
    sections: List<Pair<String, String>>,
    source: String,
    status: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            sections.forEach { (label, value) ->
                Column {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyMedium
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

@Composable
private fun AntibioticRegimenCard(
    regimen: AntibioticRegimen
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {

            Text(
                text = regimen.antibiotic,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = regimen.indication,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = regimen.setting,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            DetailRow("Likely organisms", regimen.organisms)
            DetailRow("Dose", regimen.dose)
            DetailRow("Route", regimen.route)
            DetailRow("Frequency", regimen.frequency)
            DetailRow("Duration", regimen.duration)
            DetailRow("Patient group", regimen.patientGroup)
            DetailRow("Renal adjustment", regimen.renalAdjustment)
            DetailRow("Alternatives", regimen.alternatives)
            DetailRow("Notes", regimen.notes)

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Source: ${regimen.source}",
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = "Status: ${regimen.sourceStatus} • Reviewed ${regimen.reviewDate}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "$label: ",
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
