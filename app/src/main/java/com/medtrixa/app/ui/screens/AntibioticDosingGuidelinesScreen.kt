package com.medtrixa.app.ui.screens
import com.medtrixa.app.ui.components.AutoSizeText

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextOverflow

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
        dose = "8–15 mg/kg/day (trimethoprim component)",
        route = "PO/IV",
        frequency = "Divided every 8 to 12 hours",
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
        dose = "1–2 g (2 g preferred for obesity, CNS infection, ECMO, or CrCl ≥130 mL/min)",
        route = "IV",
        frequency = "Every 8 hours, infused over 3 hours if feasible",
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
        dose = "2 g loading dose (over 30 min), THEN 2 g",
        route = "IV",
        frequency = "Every 8 hours, infused over 3 hours",
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
        dose = "Ceftazidime-avibactam 2.5 g (2 g/0.5 g); meropenem-vaborbactam 4 g (2 g/2 g); imipenem-relebactam 1.25 g (500 mg/500 mg/250 mg); aztreonam-avibactam 2.67 g (2 g/0.67 g) loading dose THEN 2 g (1.5 g/0.5 g); cefiderocol 2 g",
        route = "IV",
        frequency = "Ceftazidime-avibactam every 8 h (over 3 h); meropenem-vaborbactam every 8 h (over 3 h); imipenem-relebactam every 6 h (over 30 min); aztreonam-avibactam every 6 h (over 3 h); cefiderocol every 8 h (over 3 h; every 6 h if CrCl ≥120 mL/min)",
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
        dose = "2.67 g (aztreonam 2 g + avibactam 0.67 g) once over 3 hours as loading dose, THEN 2 g (aztreonam 1.5 g + avibactam 0.5 g)",
        route = "IV",
        frequency = "Every 6 hours for maintenance, infused over 3 hours",
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
        dose = "2.5 g (ceftazidime 2 g + avibactam 0.5 g)",
        route = "IV",
        frequency = "Every 8 hours, infused over 3 hours",
        duration = "Individualize by infection syndrome",
        patientGroup = "Adult",
        renalAdjustment = "Renal adjustment required when indicated.",
        alternatives = "Ceftolozane-tazobactam, imipenem-relebactam, and cefiderocol are equally preferred beta-lactam options (see notes); once-daily amikacin or tobramycin are alternatives.",
        notes = "Cefiderocol, ceftazidime-avibactam, ceftolozane-tazobactam, and imipenem-relebactam are ALL preferred options for DTR P. aeruginosa cUTI in the 2026 IDSA AMR guidance — listed alphabetically with no overall order of preference. (Cefiderocol drops to alternative-only status for non-urinary DTR Pseudomonas infection, not for cUTI.)",
        source = "IDSA 2026 AMR Guidance",
        sourceStatus = "Current",
        reviewDate = "2026-09"
    ),

    AntibioticRegimen(
        indication = "DTR Pseudomonas aeruginosa — non-urinary infection",
        setting = "Pneumonia or other infection outside urinary tract",
        organisms = "DTR Pseudomonas aeruginosa",
        antibiotic = "Ceftolozane-tazobactam",
        dose = "3 g (ceftolozane 2 g + tazobactam 1 g)",
        route = "IV",
        frequency = "Every 8 hours, infused over 3 hours",
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
        dose = "Sulbactam-durlobactam 2 g (1 g/1 g) PLUS carbapenem: imipenem 500 mg (or 1 g) OR meropenem 1–2 g",
        route = "IV",
        frequency = "Sulbactam-durlobactam every 6 h, infused over 3 h (every 4 h if CrCl ≥130 mL/min); imipenem every 6 h (or every 8 h for 1 g) OR meropenem every 8 h",
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
        dose = "Cefiderocol 2 g; alternative: aztreonam-avibactam 2.67 g loading dose THEN 2 g",
        route = "PO/IV",
        frequency = "Cefiderocol every 8 h, infused over 3 h (every 6 h if CrCl ≥120 mL/min); aztreonam-avibactam every 6 h, infused over 3 h",
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
        dose = "Ciprofloxacin 750 mg; levofloxacin 750 mg; TMP-SMX 8–15 mg/kg/day (trimethoprim component)",
        route = "PO",
        frequency = "Ciprofloxacin every 12 h; levofloxacin every 24 h; TMP-SMX divided every 8 to 12 h",
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
        dose = "Ciprofloxacin 750 mg PO; levofloxacin 750 mg; TMP-SMX 8–15 mg/kg/day (trimethoprim component)",
        route = "PO/IV",
        frequency = "Ciprofloxacin every 12 h; levofloxacin every 24 h; TMP-SMX divided every 8 to 12 h",
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
        duration = "At least 5 days and until clinically stable (2019 ATS/IDSA). A 2025 ATS-only update — not IDSA-endorsed — suggests <5 days (minimum 3) once clinical stability is reached.",
        patientGroup = "Adult",
        renalAdjustment = "Adjust for renal function as appropriate.",
        alternatives = "Doxycycline; macrolide only where guideline resistance criteria are satisfied.",
        notes = "2019 ATS/IDSA guideline is the only IDSA-endorsed version and remains CURRENT. ATS published a 2025 update independently after IDSA declined to co-sign it (dispute was over empiric antibiotics for virus-positive outpatients); that update also suggests no empiric antibiotics for outpatients without comorbidities who test positive for a respiratory virus. Treat 2025 changes as ATS guidance, not joint IDSA-endorsed guidance.",
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
        notes = "2019 ATS/IDSA guideline remains the only IDSA-endorsed version and is current. A 2025 ATS-only update (not IDSA-endorsed) also addresses lung ultrasound as an imaging alternative and corticosteroid use in severe inpatient CAP — ATS guidance only, not jointly IDSA-endorsed.",
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
fun AntibioticDosingGuidelinesScreen(
    onBackClick: () -> Unit
) {
    var query by remember { mutableStateOf("") }

    val results = remember(query) {
        antibioticRegimens.filter { it.matches(query) }
    }

    Surface(
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
                    AutoSizeText(
                        text = "Antibiotic Dosing Guidelines",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Renowned empiric antibiotic selection and dosing by indication",
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

    BackHandler(onBack = onBackClick)
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
