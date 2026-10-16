package com.medtrixa.app.ui.screens.more.topics

import com.medtrixa.app.ui.screens.more.*

internal val edSectionsA: List<TopicSection> = listOf(
    TopicSection("About ACLS Drug Doses", listOf(
        TopicBlock.Text("Adult ACLS drug doses from the 2025 AHA algorithms. Doses are IV or IO unless noted. Drugs support resuscitation. High-quality CPR and early defibrillation come first."),
        TopicBlock.Bullets(listOf(
            "IO access is reasonable if IV access fails. Central venous access is for trained staff only, when IV and IO both fail.",
            "Give drugs during CPR without pausing compressions.",
            "Epinephrine is the only vasopressor in the adult arrest algorithm.",
            "Routine bicarbonate, calcium, magnesium or steroids are not recommended in undifferentiated cardiac arrest.",
            "Doses here are for adults. Pediatric doses are in the Pediatric (PALS) section.",
            "Check each dose against the current AHA algorithm, the drug label and local protocol."
        )),
        TopicBlock.Callout(
            "Source",
            "Doses follow the 2025 AHA ACLS Cardiac Arrest, Bradycardia and Tachyarrhythmia algorithms and Part 9 (Adult Advanced Life Support). Adjunct drugs follow AHA special-circumstances guidance and other resuscitation guidelines, and are marked in the notes.",
            CalloutKind.INFO
        )
    ), TopicAccent.RED),
    TopicSection("Cardiac Arrest Drugs", listOf(
        TopicBlock.Text("Shockable rhythms are VF and pulseless VT. Non-shockable rhythms are asystole and PEA."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("Epinephrine", "Vasopressor", "1 mg IV/IO every 3 - 5 minutes.\n> Non-shockable rhythm: give as soon as feasible\n> VF or pulseless VT: give after initial defibrillation attempts fail\n> After the first dose, giving it every second 2-minute CPR cycle meets the interval\n> Do not use high-dose epinephrine routinely", "For IV push in arrest, use the 0.1 mg/mL concentration (1 mg in 10 mL), and flush after each dose."),
            AbbrevRow("Vasopressin", "Not recommended", "Do not give instead of epinephrine, alone or with it.\n> No survival advantage over epinephrine alone"),
            AbbrevRow("Amiodarone", "Antiarrhythmic", "Refractory VF or pulseless VT:\n> First dose: 300 mg IV/IO bolus\n> Second dose: 150 mg IV/IO\n> Usually given after the 3rd shock if the rhythm is still shockable"),
            AbbrevRow("Lidocaine", "Antiarrhythmic", "An alternative to amiodarone for refractory VF or pulseless VT.\n> First dose: 1 - 1.5 mg/kg IV/IO\n> Second dose: 0.5 - 0.75 mg/kg IV/IO"),
            AbbrevRow("Defibrillation", "Shock energy", "Biphasic: use the manufacturer's recommended energy (for example an initial 120 - 200 J). If unknown, use the maximum available.\nMonophasic: 360 J.\n> Resume compressions immediately after each shock\n> Check the rhythm every 2 minutes"),
            AbbrevRow("Asystole, PEA", "Non-shockable rhythms", "Do not shock.\n> Start CPR and give epinephrine as soon as feasible\n> Look for reversible causes (Hs and Ts)\n> Consider an advanced airway and waveform capnography")
        ))
    ), TopicAccent.AMBER),
    TopicSection("Special Circumstances", listOf(
        TopicBlock.Text("Not routine in arrest. Use these only when there is a specific indication. Evidence for several of them is limited."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("Calcium", "Calcium chloride 10%", "Consider for suspected hyperkalemia, hypocalcemia or calcium channel blocker toxicity.\n> 5 - 10 mL (500 - 1000 mg) IV/IO over 2 - 5 minutes\n> Calcium gluconate 10% needs about 3 times the volume (15 - 30 mL)", "AHA 2025 states effectiveness in hyperkalemic arrest is not well established. Dose follows AHA 2010 and other resuscitation guidelines."),
            AbbrevRow("NaHCO₃", "Sodium bicarbonate", "Consider for severe hyperkalemia, or wide-QRS tachycardia or arrest from sodium channel blockers.\n> 1 mEq/kg IV/IO, commonly about 50 mEq\n> Examples: cocaine, tricyclic antidepressants, local anesthetics", "AHA 2025 states effectiveness in hyperkalemic arrest is not well established."),
            AbbrevRow("MgSO₄", "Magnesium sulfate", "For torsades de pointes (polymorphic VT with long QT).\n> No pulse: 1 - 2 g IV/IO, diluted, over 1 - 2 minutes\n> With a pulse: 1 - 2 g over 5 - 60 minutes, then 0.5 - 1 g/hour if needed\n> Not for routine use in arrest", "Infusion timing varies by protocol. Follow local guidance."),
            AbbrevRow("Naloxone", "Opioid antagonist", "Give to anyone in respiratory arrest from suspected opioid overdose.\n> Intranasal 2 - 4 mg, repeated if there is no response\n> IV: start 0.04 - 0.4 mg and repeat or escalate up to 2 mg\n> In cardiac arrest, do not delay CPR to give it", "IM dosing follows the product label."),
            AbbrevRow("Lipid emulsion", "20% intravenous lipid emulsion", "Life-threatening local anesthetic toxicity.\nWeight under 70 kg:\n> Bolus 1.5 mL/kg, then 0.25 mL/kg/min for up to 30 minutes\nWeight 70 kg or more:\n> Bolus 100 mL over 2 - 3 minutes, then 200 - 250 mL over 15 - 20 minutes", "Give with standard resuscitation. Only 20% emulsion has been studied.")
        ))
    ), TopicAccent.VIOLET)
)
