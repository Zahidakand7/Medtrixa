package com.medtrixa.app.ui.screens.stewardship

internal val stewSectionsA: List<SSection> = listOf(
    SSection("Stewardship at a Glance", listOf<SBlock>(
        SText("Antibiotic stewardship is the coordinated effort to give the right drug, at the right dose, by the right route, for the right duration, so patients recover and resistance is slowed. Everything here reflects guidance current to October 2026."),
        statsVisual(
            "The global burden",
            "Bacterial antimicrobial resistance",
            listOf(
                SvStat("1 in 6", "bacterial infections resistant to treatment worldwide (2023)", SvColor.red),
                SvStat("1.14 M", "deaths directly caused by resistant bacteria (2021)", SvColor.rose),
                SvStat("4.71 M", "deaths associated with resistant bacteria (2021)", SvColor.amber),
                SvStat("40%+", "of tracked drug-bug pairs worsened, 5 - 15% a year (2018 - 2023)", SvColor.violet)
            ),
            "WHO GLASS 2025 and GBD 2021 AMR Collaborators (Lancet 2024). Resistance was highest in the South-East Asia and Eastern Mediterranean regions, where 1 in 3 infections was resistant."
        ),
        statsVisual(
            "The United States",
            "Antibiotic use and stewardship uptake",
            listOf(
                SvStat("756", "outpatient antibiotic prescriptions per 1,000 people (2023)", SvColor.blue),
                SvStat("Up to 30%", "of outpatient antibiotic use is unnecessary", SvColor.amber),
                SvStat("96%", "of US hospitals meet all 7 Core Elements (2023)", SvColor.green),
                SvStat("13%", "meet all six Priority elements (2023)", SvColor.teal)
            ),
            "CDC Antibiotic Use and Stewardship in the US, 2024 update. About 30% of hospital antibiotic use is unnecessary or suboptimal."
        ),
        awareVisual(
            "Global antibiotic use by WHO AWaRe group, 2022",
            "The white line marks the 2030 target",
            listOf(
                SvSeg("Access", 53f, "53%", SvColor.green, "Amoxicillin, cefazolin, doxycycline, nitrofurantoin, gentamicin, TMP-SMX"),
                SvSeg("Watch", 45f, "45%", SvColor.amber, "Ceftriaxone, azithromycin, ciprofloxacin, vancomycin, piperacillin-tazobactam, meropenem"),
                SvSeg("Reserve", 2f, "about 2%", SvColor.red, "Colistin, linezolid, cefiderocol, ceftazidime-avibactam, tigecycline")
            ),
            70f,
            "UN 2024 target: at least 70% of antibiotic use from the Access group by 2030, and 10% fewer deaths associated with bacterial AMR (2019 baseline).",
            "WHO 2026 fact sheet. Watch antibiotics exceeded 70% of use in nearly a third of countries. The Reserve share is the remainder."
        ),
        SBullets(listOf(
            "ATS 2025 CAP update: fewer than 5 days (minimum 3) once clinically stable, and corticosteroids for severe CAP.",
            "IDSA 2025 complicated UTI guideline: 7 days for most improving patients, including with gram-negative bacteremia.",
            "BALANCE trial (NEJM 2025): 7 days matched 14 days for bloodstream infection.",
            "IDSA 2024 resistant gram-negative guidance: sulbactam-durlobactam for CRAB, and carbapenems for ESBL-E outside the urinary tract.",
            "Surviving Sepsis Campaign 2026: antibiotics within 1 hour for shock or probable sepsis, shorter courses, and procalcitonin with clinical review to stop.",
            "IDSA/ESCMID 2026 S. aureus bacteremia guidance: risk stratification replaces complicated versus uncomplicated.",
            "CDC 2025: outpatient Core Elements updated to the 7-element framework.",
            "WHO Global Action Plan on AMR 2026 - 2036 adopted in May 2026."
        )),
        SCallout(
            "Currency note",
            "Guidelines change. Use this topic with your local antibiogram, formulary and institutional protocols, and check the original guidance before changing practice.",
            SKind.INFO
        )
    ), SAccent.TEAL),
    SSection("Start Smart, Then Focus", listOf<SBlock>(
        SText("The CDC's four moments of antibiotic decision-making, with a planned duration added. Ask them at the start, and again at the 48 to 72 hour time-out."),
        flowVisual(
            "The antibiotic decision path",
            "Five questions for every antibiotic order",
            listOf(
                SvStep("Does my patient have an infection that needs antibiotics?", "Viral illness, colonization and asymptomatic bacteriuria do not. Document the indication in the order.", SvColor.teal),
                SvStep("Have I sent the right cultures?", "Blood, urine and site cultures before the first dose, without delaying treatment in sepsis.", SvColor.blue),
                SvStep("Is it the right drug, dose and route?", "Use local guidelines and the antibiogram. Adjust for kidney function and weight, and check allergies and interactions.", SvColor.violet),
                SvStep("A day or more has passed. Can I stop, narrow or switch to oral?", "Do a time-out at 48 - 72 hours with culture results, labs and the clinical course.", SvColor.amber),
                SvStep("What is the planned duration?", "Write a stop date or a review date when ordering.", SvColor.green)
            )
        ),
        SRows(listOf(
            SRow("Septic shock", "Surviving Sepsis Campaign 2026", "Give antimicrobials immediately, ideally within 1 hour of recognition."),
            SRow("Probable sepsis", "No shock", "Antimicrobials immediately, ideally within 1 hour.\n> Blood cultures first if this causes no substantial delay"),
            SRow("Possible sepsis", "No shock", "Investigate rapidly, and if concern for infection persists, give antimicrobials within 3 hours of first suspicion."),
            SRow("Stopping", "Duration in sepsis", "With adequate source control, shorter courses are suggested.\n> Use procalcitonin plus clinical evaluation to decide when to stop, when the right duration is unclear")
        ))
    ), SAccent.BLUE),
    SSection("CDC Core Elements", listOf<SBlock>(
        SText("The CDC framework for stewardship programs. The hospital version has 7 elements, and the outpatient version was updated in 2025 to align with the same structure."),
        SRows(listOf(
            SRow("1. Leadership", "Commitment", "Dedicate the human, financial and IT resources the program needs."),
            SRow("2. Accountability", "A named leader", "Appoint a leader responsible for program management and outcomes."),
            SRow("3. Expertise", "Pharmacy and ID", "Appoint a pharmacist leader to improve antibiotic use, with infectious disease support."),
            SRow("4. Action", "Interventions", "Run at least one effective intervention.\n> Prospective audit and feedback\n> Preauthorization\n> Facility guidelines for common infections\n> Automatic stop orders, such as for surgical prophylaxis"),
            SRow("5. Tracking", "Measure use and resistance", "Monitor prescribing, outcomes and resistance.\n> Report to the NHSN Antimicrobial Use (AU) Option, and use the SAAR for benchmarking", "As of 1 January 2025, 4,362 US acute care hospitals had submitted AU data to NHSN."),
            SRow("6. Reporting", "Feedback", "Share antibiotic use and resistance data with prescribers, pharmacy and leadership."),
            SRow("7. Education", "Teach", "Educate clinicians about resistance and optimal prescribing, and use data to focus the teaching."),
            SRow("Outpatient 2025", "Updated framework", "Same 7-element structure.\n> Use clinician, practice and health-system prescribing data, and dashboards\n> Target diagnoses where prescribing often deviates from guidelines, such as respiratory infections"),
            SRow("Priorities", "CDC, fall 2022", "Six priority actions drawn from the hospital elements, to help programs improve quality and impact.", "Only 13% of hospitals met all six in 2023.")
        ))
    ), SAccent.VIOLET)
)
