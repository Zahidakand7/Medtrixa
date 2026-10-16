package com.medtrixa.app.ui.screens.stewardship

internal val stewSectionsB: List<SSection> = listOf(
    SSection("Spectrum at a Glance", listOf<SBlock>(
        SText("Match the narrowest reliable drug to the likely organism. The grid shows usual activity against wild-type bacteria."),
        spectrumVisual(
            "Antibiotic coverage map",
            "Scroll sideways to see all organism groups",
            listOf("MSSA", "MRSA", "Strep", "E. faecalis", "Enterics", "Pseudo", "Anaerobes", "Atypicals"),
            listOf(
                SvRow("Amoxicillin", listOf(0, 0, 2, 2, 1, 0, 1, 0)),
                SvRow("Cefazolin", listOf(2, 0, 2, 0, 1, 0, 0, 0)),
                SvRow("Ceftriaxone", listOf(1, 0, 2, 0, 2, 0, 0, 0)),
                SvRow("Cefepime", listOf(2, 0, 2, 0, 2, 2, 0, 0)),
                SvRow("Pip-tazo", listOf(2, 0, 2, 2, 2, 2, 2, 0)),
                SvRow("Meropenem", listOf(2, 0, 2, 1, 2, 2, 2, 0)),
                SvRow("Vancomycin", listOf(2, 2, 2, 2, 0, 0, 0, 0)),
                SvRow("Linezolid", listOf(2, 2, 2, 2, 0, 0, 0, 0)),
                SvRow("TMP-SMX", listOf(2, 2, 1, 0, 1, 0, 0, 0)),
                SvRow("Doxycycline", listOf(1, 1, 1, 0, 0, 0, 0, 2)),
                SvRow("Azithromycin", listOf(0, 0, 1, 0, 0, 0, 0, 2)),
                SvRow("Levofloxacin", listOf(1, 0, 2, 1, 1, 1, 0, 2)),
                SvRow("Metronidazole", listOf(0, 0, 0, 0, 0, 0, 2, 0))
            ),
            "Enterics means Enterobacterales such as E. coli and Klebsiella. The grid does not account for ESBL, AmpC, carbapenemases or local resistance, so always check your antibiogram."
        ),
        SBullets(listOf(
            "Cefazolin is the drug of choice for MSSA, and it outperforms vancomycin for MSSA bacteremia.",
            "Ceftriaxone covers most community streptococci and non-ESBL Enterobacterales. It misses Pseudomonas, enterococci and anaerobes.",
            "Piperacillin-tazobactam and meropenem already cover anaerobes, so adding metronidazole is duplicate therapy.",
            "Azithromycin covers atypicals, but pneumococcal macrolide resistance limits its use.",
            "Fluoroquinolone resistance in E. coli is high in many regions. Check the antibiogram before using one empirically."
        ))
    ), SAccent.BLUE),
    SSection("How Long: Short-Course Evidence", listOf<SBlock>(
        SText("Longer courses do not prevent resistance. They add harm without more cure. These durations apply to clinically improving patients with source control."),
        durationVisual(
            "Evidence-based antibiotic duration",
            "Short bars are the modern standard",
            listOf(
                SvDur("CAP, non-severe", 10, 4, "3 - 5 d", "ATS 2025: fewer than 5 days (minimum 3) once clinically stable."),
                SvDur("CAP, severe", 10, 5, "5+ d", "ATS 2025: 5 or more days, guided by daily stability."),
                SvDur("Cystitis (nitrofurantoin)", 7, 5, "5 d", "TMP-SMX 3 days, fosfomycin once."),
                SvDur("Pyelonephritis, cUTI", 14, 7, "5 - 7 d", "IDSA 2025: 5 - 7 d of a fluoroquinolone, or 7 d of another agent."),
                SvDur("Gram-negative bacteremia", 14, 7, "7 d", "BALANCE 2025 and IDSA 2025: afebrile, stable, source controlled."),
                SvDur("Cellulitis", 10, 5, "5 - 6 d", "IDSA: 5 days if improving, extend if not."),
                SvDur("Intra-abdominal, drained", 10, 4, "4 d", "STOP-IT: about 4 days after source control."),
                SvDur("HAP and VAP", 14, 7, "7 d", "IDSA/ATS 2016.")
            ),
            14,
            "Durations are counted from the first day of active therapy. Longer courses are for undrained collections, immunocompromise, or slow response."
        ),
        SRows(listOf(
            SRow("S. aureus bacteremia", "IDSA/ESCMID 2026", "Risk stratification replaces complicated versus uncomplicated.\n> Low risk, with no deep-seated or metastatic focus after workup: 14 days, not shorter or longer\n> Increased risk, with no deep focus: 14 days or longer, extended for prolonged bacteremia or an incomplete workup\n> Evaluate every patient, including children, for a deep focus\n> Count days from the date of blood culture clearance or source control", "Short courses do not apply to S. aureus. Involve infectious disease early."),
            SRow("Endocarditis", "Valve infection", "Usually 4 - 6 weeks, with specialist input.\n> Some stable patients may be eligible for partial oral step-down"),
            SRow("Bone and joint", "Osteomyelitis, prosthetic joint", "Often about 6 weeks, with surgical debridement when needed.\n> The OVIVA trial found early oral switch non-inferior to IV for many bone and joint infections"),
            SRow("Abscess", "Undrained collections", "Antibiotics do not replace drainage. Source control comes first, and the course is timed from adequate drainage.")
        ))
    ), SAccent.GREEN),
    SSection("Time-Out: Stop, Narrow, Switch", listOf<SBlock>(
        SText("At 48 to 72 hours, review the cultures, the labs and the patient. Most of the stewardship gain happens here."),
        SRows(listOf(
            SRow("IV to oral", "When to switch", "Switch when the patient meets all of these:\n> Clinically improving, with fever settling for 24 hours\n> Hemodynamically stable\n> Able to take and absorb oral medicine\n> Not an infection that needs prolonged IV therapy, such as undrained abscess, endocarditis or meningitis", "Early switch shortens stay and cuts line infections."),
            SRow("Oral options", "High bioavailability", "Fluoroquinolones, linezolid, TMP-SMX, doxycycline, metronidazole and fluconazole reach similar levels orally and by IV.\n> Choose the narrowest oral agent that the culture supports"),
            SRow("Narrow", "De-escalate", "Use culture results to narrow.\n> MSSA grown: stop vancomycin and give cefazolin\n> Susceptible E. coli: step down from piperacillin-tazobactam or meropenem to ceftriaxone\n> Stop double anaerobic coverage\n> Drop empiric antifungals when risk and cultures do not support them"),
            SRow("MRSA PCR", "Nasal swab", "A negative MRSA nasal PCR has a very high negative predictive value for MRSA pneumonia, so vancomycin can usually stop."),
            SRow("Do not treat", "Colonization", "Do not treat:\n> Asymptomatic bacteriuria, except in pregnancy or before urologic procedures with mucosal bleeding\n> Positive sputum or tracheal aspirate without signs of pneumonia\n> Positive catheter-tip cultures without infection signs\n> Fever alone with a negative work-up"),
            SRow("Procalcitonin", "Helps stopping", "SSC 2026 suggests procalcitonin plus clinical review to decide when to stop in sepsis. It is not used to decide whether to start.\n> A common stop rule is a level below 0.5 ng/mL, or an 80% fall from the peak", "Rules vary by protocol."),
            SRow("Prophylaxis", "Surgical", "A single preoperative dose is usually enough.\n> Do not continue beyond 24 hours after surgery for most procedures\n> Use automatic stop orders")
        ))
    ), SAccent.AMBER),
    SSection("PK/PD and Dosing", listOf<SBlock>(
        SText("The target that predicts cure depends on the class. Dosing to the target matters as much as choosing the drug."),
        pkpdVisual(
            "Time above MIC",
            "Why dosing interval matters for beta-lactams",
            "Illustrative curve for a drug dosed every 6 hours. The green area is the time the free drug level is above the MIC.",
            listOf(
                SvStat("fT>MIC", "Beta-lactams: free drug above MIC for about 40 - 70% of the interval, depending on class", SvColor.teal),
                SvStat("AUC24/MIC", "Vancomycin 400 - 600. Fluoroquinolones about 125 or more for gram-negatives", SvColor.violet),
                SvStat("Cmax/MIC", "Aminoglycosides: peak at least 8 - 10 times the MIC", SvColor.amber),
                SvStat("Prolonged infusion", "Suggested by SSC for maintenance beta-lactam dosing in sepsis", SvColor.green)
            )
        ),
        SRows(listOf(
            SRow("Vancomycin", "AUC-guided", "Target AUC24/MIC 400 - 600 (assuming an MIC of 1 mg/L), using Bayesian software or two levels.\n> Trough-only monitoring is no longer recommended for serious MRSA infections\n> Loading dose 20 - 35 mg/kg in critical illness", "2020 consensus guideline."),
            SRow("Beta-lactam", "Infusion", "Give the first dose as a bolus within the hour in sepsis.\n> Then consider extended or continuous infusion for maintenance"),
            SRow("Aminoglycosides", "Once-daily", "Extended-interval dosing, with a peak at least 8 - 10 times the MIC.\n> Monitor kidney function and drug levels\n> Short courses reduce toxicity"),
            SRow("First 48 h", "Kidney injury", "In sepsis, do not reduce the first dose or two for acute kidney injury, because kidney function often recovers.\n> Adjust from dose 2 or 3 as the function declares itself", "Common practice, not a rule for every drug."),
            SRow("Obesity, ICU", "Altered kinetics", "Larger volume of distribution and augmented renal clearance can lower drug levels.\n> Consider drug monitoring and higher doses where the drug allows")
        ))
    ), SAccent.VIOLET)
)
