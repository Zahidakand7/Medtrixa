package com.medtrixa.app.ui.screens.more.topics

import com.medtrixa.app.ui.screens.more.*

internal val highAlertA: List<TopicSection> = listOf(
    TopicSection("High-Alert Medication Classes", listOf(
        TopicBlock.Text("High-alert medications carry a heightened risk of significant patient harm when used in error. Errors with them are not necessarily more frequent, but their consequences are more severe. Each class below lists examples, the typical risk and a core safeguard."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("Insulin", "", "Examples: All insulin products, given subcutaneously or intravenously\nRisk: Severe hypoglycemia or hyperglycemia from a wrong product, strength or unit confusion\nSafeguard: Write \"units\" and never \"U\", use an independent double check, and store concentrated U-500 separately"),
            AbbrevRow("Anticoagulants", "", "Examples: Warfarin, unfractionated heparin, enoxaparin, fondaparinux, rivaroxaban, apixaban, dabigatran\nRisk: Major bleeding or thrombosis from a wrong dose, interval or monitoring plan\nSafeguard: Use dosing protocols, check weight and renal function, and monitor INR, aPTT or anti-Xa as appropriate"),
            AbbrevRow("Opioids", "", "Examples: Morphine, hydromorphone, fentanyl, oxycodone, methadone\nRisk: Respiratory depression, mg versus mL confusion with liquid concentrates, and wrong potency conversion\nSafeguard: Start low in opioid-naive patients, verify equianalgesic conversions and keep naloxone available"),
            AbbrevRow("Concentrated Electrolytes", "", "Examples: Potassium chloride concentrate, potassium phosphates, hypertonic sodium chloride above 0.9%, magnesium sulfate IV\nRisk: Fatal arrhythmia or cardiac arrest if given undiluted or too fast\nSafeguard: Use premixed solutions and remove concentrates from patient care areas"),
            AbbrevRow("Neuromuscular Blockers", "", "Examples: Succinylcholine, rocuronium, vecuronium\nRisk: Respiratory paralysis when given in error\nSafeguard: Label \"Warning: Paralyzing Agent\", store separately and restrict access"),
            AbbrevRow("Chemotherapy", "", "Examples: Methotrexate, cisplatin, carboplatin, vincristine\nRisk: Severe toxicity from dose, schedule or route errors. Vincristine is fatal if given intrathecally, and weekly methotrexate must never be written as daily\nSafeguard: Use verified protocols with an independent double check, and write the dosing day of the week in full"),
            AbbrevRow("Vasoactive Infusions", "", "Examples: Norepinephrine, epinephrine IV, vasopressin, dopamine, dobutamine\nRisk: Severe hypotension, hypertension or arrhythmia from a wrong rate or concentration\nSafeguard: Use standardized concentrations and smart pump drug libraries"),
            AbbrevRow("Sedatives", "", "Examples: Midazolam, propofol, dexmedetomidine\nRisk: Over-sedation and respiratory depression\nSafeguard: Titrate to a sedation scale with continuous monitoring"),
            AbbrevRow("Oral Hypoglycemics", "", "Examples: Glipizide, glyburide\nRisk: Prolonged hypoglycemia, especially in older adults and in renal impairment\nSafeguard: Monitor glucose and review renal function"),
            AbbrevRow("Epidural and Intrathecal Agents", "", "Examples: Epidural analgesic infusions and intrathecal medications\nRisk: Wrong-route administration, which can be fatal\nSafeguard: Use distinct labels and non-interchangeable connectors"),
            AbbrevRow("Oxytocin IV", "", "Examples: Intravenous oxytocin\nRisk: Uterine hyperstimulation\nSafeguard: Use standardized premixed concentrations and defined titration protocols"),
            AbbrevRow("Narrow Therapeutic Index Inotropes", "", "Examples: Digoxin, milrinone\nRisk: Toxicity from small differences in dose\nSafeguard: Monitor drug levels and electrolytes"),
            AbbrevRow("Parenteral Nutrition", "", "Examples: Compounded multi-component solutions\nRisk: Compounding, electrolyte and dose errors\nSafeguard: Use standardized formulations with pharmacist verification")
        ))
    ), TopicAccent.RED)
)
