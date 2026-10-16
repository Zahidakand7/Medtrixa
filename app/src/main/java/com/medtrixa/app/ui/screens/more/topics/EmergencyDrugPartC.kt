package com.medtrixa.app.ui.screens.more.topics

import com.medtrixa.app.ui.screens.more.*

internal val edSectionsC: List<TopicSection> = listOf(
    TopicSection("Pediatric (PALS) Quick Reference", listOf(
        TopicBlock.Text("Weight-based doses from the 2025 AHA PALS algorithms. Calculate the volume as well as the dose, and have a second person check it."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("Epinephrine", "Cardiac arrest", "0.01 mg/kg IV/IO of the 0.1 mg/mL (1:10,000) concentration.\n> Maximum single dose 1 mg\n> Every 3 - 5 minutes", "Example: 15 kg gives 0.15 mg, which is 1.5 mL of 0.1 mg/mL."),
            AbbrevRow("Amiodarone", "Shockable arrest", "5 mg/kg IV/IO bolus, with a maximum of 300 mg.\n> May repeat up to 3 doses, with subsequent doses limited to 150 mg", "Example: 15 kg gives 75 mg."),
            AbbrevRow("Lidocaine", "Shockable arrest", "1 mg/kg IV/IO, as an alternative to amiodarone."),
            AbbrevRow("Defibrillation", "Shock energy", "First shock 2 J/kg.\nSecond shock 4 J/kg.\n> Later shocks 4 J/kg or more, up to a maximum of 10 J/kg or the adult dose"),
            AbbrevRow("Adenosine", "Stable SVT", "First dose 0.1 mg/kg rapid IV push, maximum 6 mg.\nSecond dose 0.2 mg/kg, maximum 12 mg.\n> Follow each dose with a rapid saline flush", "Example: 30 kg gives 3 mg, then 6 mg."),
            AbbrevRow("Cardioversion", "Synchronized shock", "First 0.5 - 1 J/kg.\nThen 2 J/kg if needed.")
        ))
    ), TopicAccent.ROSE),
    TopicSection("Giving the Drugs", listOf(
        TopicBlock.Bullets(listOf(
            "Give each IV or IO push followed by a saline flush, so the drug reaches the central circulation.",
            "Adenosine has a very short half-life. Use a proximal vein and push it rapidly with an immediate flush.",
            "Epinephrine for arrest: 1 mg (10 mL of the 0.1 mg/mL concentration), then flush.",
            "Prepare the next drug during the current 2-minute CPR cycle, so there is no delay at the rhythm check.",
            "Do not give vasopressin instead of epinephrine.",
            "Amiodarone and magnesium can cause hypotension and prolong the QT interval.",
            "For infusions (dopamine, epinephrine, amiodarone, procainamide), use a pump and a dedicated line where possible, and monitor blood pressure and rhythm."
        ))
    ), TopicAccent.AMBER),
    TopicSection("Clinical Pearls", listOf(
        TopicBlock.Bullets(listOf(
            "High-quality CPR: push hard (at least 2 inches or 5 cm) and fast (100 - 120/min), allow full recoil and minimize interruptions.",
            "Without an advanced airway, use 30 compressions to 2 breaths. With one, give 1 breath every 6 seconds.",
            "Use waveform capnography to confirm airway placement and to monitor CPR quality.",
            "Check the rhythm every 2 minutes and shock only for VF or pulseless VT.",
            "Look for reversible causes: hypovolemia, hypoxia, acidosis, hypo- or hyperkalemia, hypothermia, tension pneumothorax, tamponade, toxins, and pulmonary or coronary thrombosis.",
            "Epinephrine early for non-shockable rhythms, and after initial shocks for shockable rhythms.",
            "Start post-arrest care after return of spontaneous circulation, including oxygen and ventilation targets and a search for the cause."
        )),
        TopicBlock.Callout(
            "Scope",
            "This is a quick reference for trained clinicians and does not replace ACLS or PALS training. Doses may differ by country, product and local protocol, so confirm against the current guideline and the drug label.",
            CalloutKind.INFO
        )
    ), TopicAccent.GREEN)
)
