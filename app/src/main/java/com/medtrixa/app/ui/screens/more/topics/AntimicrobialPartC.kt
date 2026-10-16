package com.medtrixa.app.ui.screens.more.topics

import com.medtrixa.app.ui.screens.more.*

internal val antimicrobialC: List<TopicSection> = listOf(
    TopicSection("Intrinsic Resistance", listOf(
        TopicBlock.Text("These organisms are resistant to the listed drugs by nature, regardless of susceptibility reports."),
        TopicBlock.Bullets(listOf(
            "MRSA: All beta-lactams except ceftaroline.",
            "Enterococci: All cephalosporins, clindamycin, and aminoglycosides used alone.",
            "Klebsiella species: Ampicillin and amoxicillin.",
            "Inducible AmpC organisms: Enterobacter cloacae, Klebsiella aerogenes and Citrobacter freundii resist ampicillin, amoxicillin-clavulanate and first- and second-generation cephalosporins. Avoid ceftriaxone for serious infections, and prefer cefepime or a carbapenem.",
            "Pseudomonas aeruginosa: Ceftriaxone, ertapenem, ampicillin, tetracyclines and trimethoprim-sulfamethoxazole.",
            "Stenotrophomonas maltophilia: Carbapenems and aminoglycosides. Use trimethoprim-sulfamethoxazole, levofloxacin, minocycline or cefiderocol.",
            "Acinetobacter baumannii: Ampicillin, first- and second-generation cephalosporins and ertapenem.",
            "Listeria monocytogenes: Cephalosporins. Use ampicillin.",
            "Anaerobes: Aminoglycosides, aztreonam and ciprofloxacin.",
            "Atypicals: Beta-lactams, because they lack a cell wall."
        ))
    ), TopicAccent.RED),
    TopicSection("Stewardship Principles", listOf(
        TopicBlock.Bullets(listOf(
            "Cultures first: Obtain cultures before antibiotics when possible, without delaying treatment in sepsis.",
            "Local data: Use the local antibiogram, because resistance varies by region and hospital.",
            "Time-out: Review the choice at 48 - 72 hours, and narrow once cultures and susceptibilities return.",
            "Dosing: Adjust for renal and hepatic function and for obesity, and consider extended infusion for beta-lactams.",
            "Route: Switch to oral therapy when the patient is stable and the gut works, particularly for well-absorbed agents such as levofloxacin, linezolid, trimethoprim-sulfamethoxazole, metronidazole and doxycycline.",
            "Duration: Use the shortest effective course, which is 5 - 7 days for many common infections."
        ))
    ), TopicAccent.TEAL),
    TopicSection("Beta-Lactam Allergy", listOf(
        TopicBlock.Bullets(listOf(
            "Most labels are inaccurate: Over 90% of patients with a reported penicillin allergy tolerate penicillins when evaluated.",
            "Cephalosporins: Cross-reactivity with penicillins is about 2%, and is driven by a similar R1 side chain, not by the shared beta-lactam ring.",
            "Carbapenems: Cross-reactivity with penicillins is below 1%.",
            "Aztreonam: It does not cross-react with penicillins, but shares a side chain with ceftazidime.",
            "Evaluation: Take a detailed history, and refer for allergy testing or a supervised challenge to remove an unneeded label.",
            "Severe reactions: Avoid beta-lactams that caused anaphylaxis or severe cutaneous reactions unless an allergist advises otherwise."
        )),
        TopicBlock.Callout(
            "Disclaimer",
            "This guide is an educational reference with typical spectra, which change as resistance evolves. Confirm choices against the local antibiogram, culture and susceptibility results, current guidelines and the prescribing information, and involve infectious diseases or a pharmacist for complex cases.",
            CalloutKind.INFO
        )
    ), TopicAccent.AMBER)
)
