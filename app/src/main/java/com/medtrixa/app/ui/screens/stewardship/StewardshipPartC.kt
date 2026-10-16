package com.medtrixa.app.ui.screens.stewardship

internal val stewSectionsC: List<SSection> = listOf(
    SSection("Penicillin Allergy: Delabel It", listOf<SBlock>(
        SText("About 1 in 10 people carry a penicillin allergy label, and most can safely take penicillin after assessment. The label drives broader antibiotic use, more C. difficile and worse outcomes."),
        scoreVisual(
            "PEN-FAST risk score",
            "F: five years or less since reaction (2). A/S: anaphylaxis, angioedema or severe skin reaction (2). T: treatment needed (1).",
            listOf(
                SvScore("0", "Under 1%", "Very low risk of a true allergy. Direct oral challenge.", SvColor.green),
                SvScore("1 - 2", "About 5%", "Low risk. Direct oral amoxicillin challenge (PALACE trial).", SvColor.teal),
                SvScore("3", "About 20%", "Moderate risk. Allergy referral and skin testing.", SvColor.amber),
                SvScore("4 - 5", "About 50%", "High risk. Allergist evaluation before any penicillin.", SvColor.red)
            ),
            "Scores below 3 have a negative predictive value of about 96%. Percentages are the risk of a positive allergy test."
        ),
        SRows(listOf(
            SRow("Challenge", "Direct oral amoxicillin", "For a PEN-FAST score of 0 - 2, give a single amoxicillin dose and observe for 60 - 120 minutes.\n> Dose follows local protocol, commonly 250 - 500 mg\n> Do it with resuscitation equipment and trained staff", "Exclude severe cutaneous reactions (SJS, TEN, DRESS), organ injury, and those on antihistamines or high-dose steroids."),
            SRow("Cephalosporins", "Cross-reactivity", "Cefazolin has a unique side chain and can be given even with a true penicillin allergy.\n> Dissimilar side-chain cephalosporins carry a very low cross-reaction risk, about 2% or less"),
            SRow("Carbapenems", "Cross-reactivity", "Cross-reactivity with penicillin is below 1%, and carbapenems can usually be given without testing."),
            SRow("Update the chart", "After delabeling", "Remove or update the allergy label in the record, and tell the patient and the community pharmacy.")
        ))
    ), SAccent.ROSE),
    SSection("Resistant Gram-Negative Organisms", listOf<SBlock>(
        SText("Preferred options from the IDSA 2024 guidance, with newer agents kept in reserve for carbapenem-resistant organisms. Always confirm with susceptibility testing and an infectious disease specialist."),
        SRows(listOf(
            SRow("ESBL-E", "Extended-spectrum beta-lactamase", "Outside the urinary tract: meropenem, imipenem-cilastatin or ertapenem are preferred, even if piperacillin-tazobactam or cefepime looks susceptible.\n> Critically ill or low albumin: prefer meropenem or imipenem over ertapenem\n> Cystitis: nitrofurantoin or TMP-SMX\n> Pyelonephritis or cUTI: TMP-SMX, ciprofloxacin or levofloxacin\n> Step down to oral agents once improving\n> Keep newer beta-lactam combinations for carbapenem-resistant organisms"),
            SRow("AmpC-E", "Enterobacter, Citrobacter, Klebsiella aerogenes", "Cefepime is suggested for moderate to high AmpC risk when the cefepime MIC is 2 mg/L or less.\n> Use a carbapenem if the MIC is higher\n> Avoid ceftriaxone for invasive infections"),
            SRow("CRE: KPC", "Carbapenemase", "Meropenem-vaborbactam, ceftazidime-avibactam or imipenem-cilastatin-relebactam."),
            SRow("CRE: MBL", "NDM, VIM, IMP", "Ceftazidime-avibactam plus aztreonam, or aztreonam-avibactam, or cefiderocol.\n> OXA-48-like: ceftazidime-avibactam is preferred", "Aztreonam-avibactam was approved in 2025."),
            SRow("DTR P. aeruginosa", "Difficult-to-treat resistance", "If still susceptible to traditional beta-lactams, give them in high-dose extended infusion.\n> If DTR: ceftolozane-tazobactam, ceftazidime-avibactam, imipenem-cilastatin-relebactam or cefiderocol"),
            SRow("CRAB", "Carbapenem-resistant Acinetobacter", "Sulbactam-durlobactam plus meropenem or imipenem-cilastatin.\n> If it is not available: high-dose ampicillin-sulbactam (27 g/day) plus at least one other agent, as bridge therapy"),
            SRow("S. maltophilia", "Stenotrophomonas", "TMP-SMX, minocycline, cefiderocol or levofloxacin.\n> Two-drug combinations are suggested for severe infection"),
            SRow("MRSA, VRE", "Gram-positive resistance", "MRSA bacteremia: vancomycin (AUC-guided) or daptomycin.\nVRE: linezolid, or daptomycin at 8 - 12 mg/kg.")
        ))
    ), SAccent.RED),
    SSection("Outpatient Quick Guide", listOf<SBlock>(
        SText("Adult first-line choices for common outpatient infections. Adjust for kidney function, allergy and local resistance. Many respiratory illnesses need no antibiotic at all."),
        SRows(listOf(
            SRow("No antibiotic", "Usually viral", "Acute bronchitis, the common cold, most sore throats without strep, and most acute cough illness.\n> Give symptom relief and clear safety-net advice\n> Consider delayed prescribing when the picture is uncertain"),
            SRow("Strep throat", "Group A strep", "Penicillin V 500 mg two or three times daily, or amoxicillin 1,000 mg once daily, for 10 days.\n> Test before treating, with a rapid test or culture"),
            SRow("Sinusitis", "Acute bacterial", "Do not treat in the first 10 days unless severe or showing a double worsening.\n> Amoxicillin-clavulanate 500/125 mg three times daily or 875/125 mg twice daily for 5 - 7 days"),
            SRow("Otitis media", "Children", "Amoxicillin 80 - 90 mg/kg/day in two doses.\n> Duration: 10 days under age 2, 7 days at age 2 - 5, 5 - 7 days from age 6\n> Observation is an option for mild unilateral disease"),
            SRow("Cystitis", "Uncomplicated", "Nitrofurantoin 100 mg twice daily for 5 days (avoid if CrCl is below 30), or TMP-SMX double strength twice daily for 3 days, or fosfomycin 3 g once.\n> Avoid fluoroquinolones for simple cystitis"),
            SRow("Cellulitis", "Non-purulent", "Cephalexin 500 mg four times daily for 5 days if improving.\n> Add MRSA cover only for purulence, penetrating trauma, MRSA history or systemic toxicity"),
            SRow("CAP", "Outpatient adult", "Healthy: amoxicillin 1 g three times daily, or doxycycline 100 mg twice daily.\n> With comorbidities: amoxicillin-clavulanate or cefpodoxime plus a macrolide or doxycycline, or a respiratory fluoroquinolone\n> Duration: fewer than 5 days (minimum 3) once stable (ATS 2025)")
        ))
    ), SAccent.TEAL),
    SSection("C. difficile and Stewardship", listOf<SBlock>(
        SText("Antibiotics are the main risk factor for C. difficile infection, and the best prevention is using fewer of them."),
        SRows(listOf(
            SRow("Treatment", "Initial episode", "Fidaxomicin 200 mg twice daily for 10 days is preferred.\n> Oral vancomycin 125 mg four times daily for 10 days is acceptable\n> Stop the inciting antibiotic as soon as possible", "IDSA/SHEA 2021 focused update."),
            SRow("Fulminant", "Shock, ileus, megacolon", "Oral or nasogastric vancomycin 500 mg four times daily plus IV metronidazole 500 mg every 8 hours.\n> Add rectal vancomycin if ileus is present\n> Early surgical consultation"),
            SRow("Recurrence", "Second or later", "Fidaxomicin (standard or extended-pulsed), a vancomycin taper and pulse, or fecal microbiota products after antibiotic treatment."),
            SRow("High risk", "Antibiotics", "Clindamycin, fluoroquinolones and broad cephalosporins carry the highest risk.\n> Use alternatives where reasonable, and keep courses short")
        ))
    ), SAccent.AMBER),
    SSection("Metrics and Pearls", listOf<SBlock>(
        SRows(listOf(
            SRow("DOT", "Days of therapy", "Days of therapy per 1,000 patient-days present. The standard measure of hospital antibiotic use."),
            SRow("SAAR", "Standardized Antimicrobial Administration Ratio", "NHSN's risk-adjusted observed-to-predicted ratio. Above 1.0 means more use than expected."),
            SRow("Process", "What to track", "Share of orders with a documented indication and stop date, time to first antibiotic in sepsis, de-escalation at 72 hours, and IV-to-oral conversion."),
            SRow("Outcomes", "Balancing measures", "C. difficile rate, resistance trends, readmission, and mortality, to show that shorter courses stay safe.")
        )),
        SBullets(listOf(
            "Prospective audit and feedback, and preauthorization, are the best-supported stewardship interventions.",
            "Handshake stewardship, with pharmacists and physicians rounding on the ward, improves acceptance.",
            "Write the indication, dose and planned duration on every order.",
            "Do a time-out at 48 - 72 hours on every course.",
            "Check the allergy label, because delabeling is a stewardship win.",
            "Treat the patient, not the culture. Colonization is not infection.",
            "Shorter is safe for most infections, with S. aureus bacteremia, endocarditis, bone and undrained collections as the main exceptions."
        )),
        SCallout(
            "Scope",
            "This is a clinical reference for trained health professionals. Doses and durations are adult values unless stated, and local protocols and the product label take precedence.",
            SKind.INFO
        )
    ), SAccent.GREEN)
)
