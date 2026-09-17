package com.medtrixa.app.ui.screens

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

private data class BacterialOrganism(
    val name: String,
    val classification: String,
    val gramStainMorphology: String,
    val clinicalSyndromes: String,
    val resistancePatterns: String,
    val empiricTherapy: String,
    val notes: String,
    val source: String,
    val sourceStatus: String,
    val reviewDate: String
)

private val bacterialOrganisms = listOf(

    // =========================
    // GRAM-POSITIVE COCCI
    // =========================
    BacterialOrganism(
        name = "Staphylococcus aureus (MSSA)",
        classification = "Gram-positive coccus, Staphylococcaceae",
        gramStainMorphology = "Gram-positive cocci in clusters; catalase-positive; coagulase-positive",
        clinicalSyndromes = "Skin/soft-tissue infection, bacteremia, endocarditis, osteomyelitis, septic arthritis, pneumonia (including post-influenza), device-related infection",
        resistancePatterns = "Methicillin-susceptible (mecA negative); beta-lactamase production common, inactivating penicillin but not antistaphylococcal penicillins/cephalosporins",
        empiricTherapy = "Antistaphylococcal beta-lactam (e.g. cefazolin, nafcillin/oxacillin) once MSSA confirmed",
        notes = "Always confirm methicillin susceptibility before narrowing; beta-lactams are preferred over vancomycin for MSSA bacteremia once susceptibility is known.",
        source = "IDSA/ESCMID 2026 SAB Consensus Statements",
        sourceStatus = "Current",
        reviewDate = "2026-09"
    ),
    BacterialOrganism(
        name = "Staphylococcus aureus (MRSA)",
        classification = "Gram-positive coccus, Staphylococcaceae",
        gramStainMorphology = "Gram-positive cocci in clusters; catalase-positive; coagulase-positive",
        clinicalSyndromes = "Skin/soft-tissue infection, bacteremia, endocarditis, osteomyelitis, septic arthritis, necrotizing pneumonia, device-related infection",
        resistancePatterns = "mecA/mecC-mediated methicillin resistance (altered PBP2a); frequently resistant to multiple beta-lactam classes",
        empiricTherapy = "Vancomycin or daptomycin (non-pulmonary); linezolid or ceftaroline in selected scenarios",
        notes = "Risk-stratify per 2026 SAB consensus (low-risk vs increased-risk for deep-seated/metastatic infection); obtain follow-up blood cultures and echocardiography per protocol.",
        source = "IDSA/ESCMID 2026 SAB Consensus Statements",
        sourceStatus = "Current",
        reviewDate = "2026-09"
    ),
    BacterialOrganism(
        name = "Streptococcus pneumoniae",
        classification = "Gram-positive coccus, Streptococcaceae",
        gramStainMorphology = "Gram-positive diplococci, lancet-shaped; alpha-hemolytic; optochin-sensitive",
        clinicalSyndromes = "Community-acquired pneumonia, otitis media, sinusitis, meningitis, bacteremia",
        resistancePatterns = "Penicillin-binding-protein-mediated reduced penicillin susceptibility; variable macrolide resistance depending on region",
        empiricTherapy = "Amoxicillin (non-meningeal) or ceftriaxone/vancomycin combination (meningitis pending susceptibility)",
        notes = "Confirm susceptibility before narrowing in meningitis; standard CAP dosing does not treat meningeal disease.",
        source = "ATS/IDSA 2019 CAP Guideline",
        sourceStatus = "Current IDSA-listed guideline",
        reviewDate = "2026-09"
    ),
    BacterialOrganism(
        name = "Streptococcus pyogenes (Group A Strep)",
        classification = "Gram-positive coccus, Streptococcaceae",
        gramStainMorphology = "Gram-positive cocci in chains; beta-hemolytic; bacitracin-sensitive",
        clinicalSyndromes = "Pharyngitis, cellulitis, necrotizing fasciitis, streptococcal toxic shock syndrome, bacteremia",
        resistancePatterns = "Remains uniformly susceptible to penicillin; macrolide resistance reported regionally",
        empiricTherapy = "Penicillin or amoxicillin; add clindamycin for suspected toxin-mediated severe disease (adjunctive, not substitute)",
        notes = "Clindamycin is added for antitoxin effect in necrotizing/toxic-shock presentations, not as monotherapy.",
        source = "IDSA Skin and Soft Tissue Infection Guideline 2014",
        sourceStatus = "Current IDSA-listed guideline",
        reviewDate = "2026-09"
    ),
    BacterialOrganism(
        name = "Enterococcus faecalis",
        classification = "Gram-positive coccus, Enterococcaceae",
        gramStainMorphology = "Gram-positive cocci in pairs/short chains; catalase-negative",
        clinicalSyndromes = "UTI, bacteremia, endocarditis, intra-abdominal infection (often polymicrobial)",
        resistancePatterns = "Intrinsic low-level aminoglycoside and cephalosporin resistance; usually ampicillin-susceptible (unlike E. faecium)",
        empiricTherapy = "Ampicillin; add synergistic aminoglycoside for endocarditis per susceptibility",
        notes = "Distinguish from E. faecium, which is far more likely to be ampicillin- and vancomycin-resistant.",
        source = "General clinical microbiology reference",
        sourceStatus = "Current",
        reviewDate = "2026-09"
    ),
    BacterialOrganism(
        name = "Enterococcus faecium (VRE)",
        classification = "Gram-positive coccus, Enterococcaceae",
        gramStainMorphology = "Gram-positive cocci in pairs/short chains; catalase-negative",
        clinicalSyndromes = "Bacteremia, endocarditis, intra-abdominal/device-related infection, often in healthcare-associated settings",
        resistancePatterns = "vanA/vanB-mediated vancomycin resistance common; frequent ampicillin resistance",
        empiricTherapy = "Linezolid or daptomycin pending susceptibility for confirmed VRE infection",
        notes = "Empiric coverage for VRE is generally reserved for high-risk healthcare-associated infection, not routine empiric therapy.",
        source = "General clinical microbiology reference",
        sourceStatus = "Current",
        reviewDate = "2026-09"
    ),

    // =========================
    // GRAM-NEGATIVE ENTEROBACTERALES
    // =========================
    BacterialOrganism(
        name = "Escherichia coli",
        classification = "Gram-negative rod, Enterobacterales",
        gramStainMorphology = "Gram-negative bacilli; lactose-fermenting on MacConkey agar",
        clinicalSyndromes = "UTI (uncomplicated and complicated), bacteremia, intra-abdominal infection, neonatal meningitis",
        resistancePatterns = "ESBL production increasingly common; fluoroquinolone and TMP-SMX resistance variable by region",
        empiricTherapy = "Nitrofurantoin or TMP-SMX (uncomplicated cystitis); ceftriaxone or piperacillin-tazobactam (systemic, pending susceptibility)",
        notes = "Check local antibiogram; ESBL-producing strains require susceptibility-directed therapy per current IDSA AMR guidance.",
        source = "IDSA 2026 AMR Guidance",
        sourceStatus = "Current",
        reviewDate = "2026-09"
    ),
    BacterialOrganism(
        name = "Klebsiella pneumoniae",
        classification = "Gram-negative rod, Enterobacterales",
        gramStainMorphology = "Gram-negative encapsulated bacilli; lactose-fermenting; mucoid colonies",
        clinicalSyndromes = "UTI, pneumonia, bacteremia, liver abscess (hypervirulent strains), intra-abdominal infection",
        resistancePatterns = "ESBL and carbapenemase production (notably KPC) well described; can be carbapenem-resistant (CRE)",
        empiricTherapy = "Ceftriaxone or piperacillin-tazobactam pending susceptibility; mechanism-directed therapy if CRE confirmed",
        notes = "For confirmed CRE, select agent by carbapenemase mechanism (KPC/NDM/OXA-48-like) per current IDSA AMR guidance.",
        source = "IDSA 2026 AMR Guidance",
        sourceStatus = "Current",
        reviewDate = "2026-09"
    ),
    BacterialOrganism(
        name = "Enterobacter cloacae complex",
        classification = "Gram-negative rod, Enterobacterales",
        gramStainMorphology = "Gram-negative bacilli; variable lactose fermentation",
        clinicalSyndromes = "Healthcare-associated pneumonia, bacteremia, intra-abdominal and device-related infection",
        resistancePatterns = "Moderate risk of clinically significant inducible AmpC beta-lactamase; can become resistant to third-generation cephalosporins during therapy",
        empiricTherapy = "Cefepime or carbapenem for invasive infection pending susceptibility",
        notes = "2026 IDSA AMR guidance does not suggest ceftriaxone/cefotaxime/ceftazidime for invasive infection with organisms at moderate AmpC risk.",
        source = "IDSA 2026 AMR Guidance",
        sourceStatus = "Current",
        reviewDate = "2026-09"
    ),
    BacterialOrganism(
        name = "Proteus mirabilis",
        classification = "Gram-negative rod, Enterobacterales",
        gramStainMorphology = "Gram-negative bacilli; characteristic swarming motility on agar",
        clinicalSyndromes = "UTI (associated with struvite/staghorn calculi), bacteremia",
        resistancePatterns = "Intrinsically resistant to nitrofurantoin and tetracyclines; generally susceptible to beta-lactams unless ESBL-producing",
        empiricTherapy = "Ceftriaxone or a fluoroquinolone pending susceptibility",
        notes = "Urease production predisposes to struvite stone formation; consider imaging in recurrent infection.",
        source = "General clinical microbiology reference",
        sourceStatus = "Current",
        reviewDate = "2026-09"
    ),
    BacterialOrganism(
        name = "Salmonella species (non-typhoidal)",
        classification = "Gram-negative rod, Enterobacterales",
        gramStainMorphology = "Gram-negative bacilli; non-lactose-fermenting",
        clinicalSyndromes = "Gastroenteritis, bacteremia (especially in immunocompromised or extremes of age), focal infection",
        resistancePatterns = "Fluoroquinolone and third-generation cephalosporin resistance reported regionally",
        empiricTherapy = "Supportive care for uncomplicated gastroenteritis in healthy hosts; ceftriaxone or fluoroquinolone for invasive disease",
        notes = "Antibiotics are generally not indicated for uncomplicated gastroenteritis in immunocompetent adults; treat invasive/high-risk cases.",
        source = "General clinical microbiology reference",
        sourceStatus = "Current",
        reviewDate = "2026-09"
    ),

    // =========================
    // NON-FERMENTING GRAM-NEGATIVE RODS
    // =========================
    BacterialOrganism(
        name = "Pseudomonas aeruginosa",
        classification = "Gram-negative rod, non-fermenter",
        gramStainMorphology = "Gram-negative bacilli; oxidase-positive; often produces blue-green pigment (pyocyanin)",
        clinicalSyndromes = "Healthcare-associated pneumonia (including ventilator-associated), bacteremia, UTI, osteomyelitis, malignant otitis externa",
        resistancePatterns = "Difficult-to-treat (DTR) phenotypes with resistance to multiple beta-lactam classes and fluoroquinolones increasingly reported",
        empiricTherapy = "Antipseudomonal beta-lactam (e.g. piperacillin-tazobactam, cefepime, meropenem) pending susceptibility",
        notes = "For confirmed DTR P. aeruginosa, preferred agents include ceftazidime-avibactam, ceftolozane-tazobactam, imipenem-relebactam and cefiderocol per current IDSA AMR guidance.",
        source = "IDSA 2026 AMR Guidance",
        sourceStatus = "Current",
        reviewDate = "2026-09"
    ),
    BacterialOrganism(
        name = "Acinetobacter baumannii",
        classification = "Gram-negative coccobacillus, non-fermenter",
        gramStainMorphology = "Gram-negative coccobacilli; oxidase-negative; can survive on dry surfaces for extended periods",
        clinicalSyndromes = "Healthcare-associated pneumonia, bacteremia, wound infection, often in ICU settings",
        resistancePatterns = "Carbapenem resistance (CRAB) common in healthcare-associated strains; frequently multidrug-resistant",
        empiricTherapy = "Sulbactam-durlobactam in combination with a carbapenem is the priority approach for invasive CRAB infection",
        notes = "2026 IDSA guidance applies to invasive infection, not colonization; consider infection-control implications given environmental persistence.",
        source = "IDSA 2026 AMR Guidance",
        sourceStatus = "Current",
        reviewDate = "2026-09"
    ),
    BacterialOrganism(
        name = "Stenotrophomonas maltophilia",
        classification = "Gram-negative rod, non-fermenter",
        gramStainMorphology = "Gram-negative bacilli; oxidase-negative; intrinsically resistant to most carbapenems",
        clinicalSyndromes = "Healthcare-associated pneumonia, bacteremia (often catheter-associated), infection in immunocompromised hosts",
        resistancePatterns = "Intrinsic carbapenem resistance via chromosomal beta-lactamases; variable TMP-SMX susceptibility",
        empiricTherapy = "Cefiderocol monotherapy is the preferred approach for invasive infection per 2026 IDSA guidance",
        notes = "Distinguish colonization from true infection; aztreonam-avibactam (preferably with a second active agent) is an alternative.",
        source = "IDSA 2026 AMR Guidance",
        sourceStatus = "Current",
        reviewDate = "2026-09"
    ),

    // =========================
    // OTHER GRAM-NEGATIVE / FASTIDIOUS ORGANISMS
    // =========================
    BacterialOrganism(
        name = "Haemophilus influenzae",
        classification = "Gram-negative coccobacillus, Pasteurellaceae",
        gramStainMorphology = "Small gram-negative coccobacilli; requires factors X and V for growth (chocolate agar)",
        clinicalSyndromes = "Otitis media, sinusitis, community-acquired pneumonia, epiglottitis, meningitis (unvaccinated populations)",
        resistancePatterns = "Beta-lactamase production causes ampicillin resistance in a substantial proportion of isolates",
        empiricTherapy = "Amoxicillin-clavulanate or a third-generation cephalosporin pending susceptibility",
        notes = "Incidence of invasive disease markedly reduced by Hib vaccination; non-typeable strains still cause mucosal infection.",
        source = "General clinical microbiology reference",
        sourceStatus = "Current",
        reviewDate = "2026-09"
    ),
    BacterialOrganism(
        name = "Neisseria meningitidis",
        classification = "Gram-negative diplococcus, Neisseriaceae",
        gramStainMorphology = "Gram-negative diplococci, kidney-bean shaped; oxidase-positive",
        clinicalSyndromes = "Meningitis, meningococcemia, rapidly progressive septic shock with petechial/purpuric rash",
        resistancePatterns = "Remains largely penicillin-susceptible in most regions; resistance reported in some areas",
        empiricTherapy = "Ceftriaxone; add vancomycin empirically until pneumococcal meningitis is excluded",
        notes = "Post-exposure prophylaxis (e.g. rifampin, ciprofloxacin, ceftriaxone) indicated for close contacts.",
        source = "General clinical microbiology reference",
        sourceStatus = "Current",
        reviewDate = "2026-09"
    ),
    BacterialOrganism(
        name = "Listeria monocytogenes",
        classification = "Gram-positive rod, Listeriaceae",
        gramStainMorphology = "Gram-positive bacilli; tumbling motility at room temperature; catalase-positive",
        clinicalSyndromes = "Meningitis/meningoencephalitis, bacteremia, febrile gastroenteritis, invasive disease in pregnancy/neonates and the immunocompromised or elderly",
        resistancePatterns = "Intrinsically resistant to cephalosporins; ampicillin remains active",
        empiricTherapy = "Ampicillin (add gentamicin for synergy in severe disease); add empirically to meningitis regimens in at-risk age groups",
        notes = "Always add ampicillin to empiric meningitis coverage in neonates, pregnancy, age over 50, or immunocompromise, since cephalosporins do not cover Listeria.",
        source = "General clinical microbiology reference",
        sourceStatus = "Current",
        reviewDate = "2026-09"
    ),
    BacterialOrganism(
        name = "Clostridioides difficile",
        classification = "Gram-positive rod (anaerobic), Peptostreptococcaceae",
        gramStainMorphology = "Gram-positive spore-forming bacilli; obligate anaerobe; toxin-producing strains cause disease",
        clinicalSyndromes = "Antibiotic-associated diarrhea, pseudomembranous colitis, fulminant colitis",
        resistancePatterns = "Not treated with systemic antibiotics used for other GI infections; recurrence risk after prior episodes",
        empiricTherapy = "Oral vancomycin or fidaxomicin for an initial episode, per current IDSA C. difficile guidance",
        notes = "Diagnosis requires clinical correlation with symptomatic diarrhea, not colonization; discontinue inciting antibiotics when feasible.",
        source = "IDSA/SHEA C. difficile Guideline",
        sourceStatus = "Current IDSA-listed guideline",
        reviewDate = "2026-09"
    ),
    BacterialOrganism(
        name = "Bacteroides fragilis",
        classification = "Gram-negative rod (anaerobic), Bacteroidaceae",
        gramStainMorphology = "Gram-negative bacilli; obligate anaerobe; predominant anaerobe of the colonic flora",
        clinicalSyndromes = "Intra-abdominal abscess, polymicrobial bacteremia, pelvic infection, often alongside Enterobacterales",
        resistancePatterns = "Beta-lactamase production causes intrinsic resistance to penicillin; generally susceptible to metronidazole and beta-lactam/beta-lactamase-inhibitor combinations",
        empiricTherapy = "Metronidazole or piperacillin-tazobactam as part of combination therapy for polymicrobial intra-abdominal infection",
        notes = "Almost always encountered as part of polymicrobial infection; source control is essential alongside antibiotics.",
        source = "General clinical microbiology reference",
        sourceStatus = "Current",
        reviewDate = "2026-09"
    )
)

private fun BacterialOrganism.matches(query: String): Boolean {
    if (query.isBlank()) return true
    val q = query.trim().lowercase()
    val searchableText = listOf(
        name,
        classification,
        gramStainMorphology,
        clinicalSyndromes,
        resistancePatterns,
        empiricTherapy,
        notes,
        source
    ).joinToString(" ").lowercase()

    return q.split(Regex("\\s+"))
        .filter { it.isNotBlank() }
        .all { searchableText.contains(it) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BacterialAtlasScreen(
    onBackClick: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    val results = remember(query) {
        bacterialOrganisms.filter { it.matches(query) }
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
                    Text(
                        text = "Bacterial Atlas",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Organism classification, morphology, syndromes and resistance patterns",
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
                            Text("Search organism, syndrome or resistance pattern")
                        },
                        placeholder = {
                            Text("e.g. MRSA, meningitis, ESBL")
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
                                    text = "No matching organism",
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "Try an organism name, syndrome, or resistance pattern.",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                } else {
                    items(
                        items = results,
                        key = { "${it.name}|${it.classification}" }
                    ) { organism ->
                        BacterialOrganismCard(organism)
                    }
                }

                item {
                    BacterialAtlasGuideCard(
                        icon = Icons.Outlined.MenuBook,
                        title = "Bacterial Atlas — Intern Quick Reference",
                        subtitle = "Organism-focused overview • Always confirm with local microbiology, susceptibility results and current full guidelines before prescribing",
                        sections = listOf(
                            "1. How to use this atlas" to
                                "Use organism identification alongside Gram stain morphology, clinical syndrome and local resistance patterns to guide empiric therapy. Always narrow to susceptibility-directed therapy once culture results are available.",
                            "2. Gram stain interpretation" to
                                "Gram-positive organisms retain crystal violet stain (purple); Gram-negative organisms do not and counterstain pink/red with safranin. Cocci are round, bacilli are rod-shaped. Gram stain morphology narrows the differential before culture results return.",
                            "3. Key resistance mechanisms to know" to
                                "Beta-lactamases (including ESBLs and carbapenemases) hydrolyze beta-lactam antibiotics. mecA/mecC confer methicillin resistance in staphylococci. vanA/vanB confer vancomycin resistance in enterococci. Inducible AmpC beta-lactamases can emerge during cephalosporin therapy in certain Enterobacterales.",
                            "4. Colonization versus infection" to
                                "Many organisms in this atlas (e.g. Enterococcus, Stenotrophomonas, C. difficile) are frequently isolated as colonizers or from non-sterile sites. Treat clinical infection supported by signs, symptoms and appropriate specimen collection, not isolated positive cultures alone.",
                            "5. Empiric versus definitive therapy" to
                                "Empiric therapy is chosen based on likely pathogens, local antibiogram, infection site and patient risk factors. Definitive therapy should be narrowed once organism identification and susceptibility results are available.",
                            "6. Intern bedside checklist" to
                                "1) What is the suspected source? 2) What does the Gram stain show? 3) What organisms are typical for this syndrome and setting? 4) What resistance patterns are locally common? 5) Has therapy been reassessed once culture and susceptibility data return?"
                        ),
                        source = "General clinical microbiology reference; IDSA 2026 AMR Guidance; IDSA/ESCMID 2026 SAB Consensus Statements",
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
                        text = "This atlas is a reference aid, not a substitute for patient-specific clinical judgment, local microbiology results, susceptibility testing, or institutional protocols.",
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
private fun BacterialAtlasGuideCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    sections: List<Pair<String, String>>,
    source: String,
    status: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
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

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

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
private fun BacterialOrganismCard(
    organism: BacterialOrganism
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
                text = organism.name,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = organism.classification,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = organism.gramStainMorphology,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            DetailRow("Clinical syndromes", organism.clinicalSyndromes)
            DetailRow("Resistance patterns", organism.resistancePatterns)
            DetailRow("Empiric therapy", organism.empiricTherapy)
            DetailRow("Notes", organism.notes)

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Source: ${organism.source}",
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = "Status: ${organism.sourceStatus} • Reviewed ${organism.reviewDate}",
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
