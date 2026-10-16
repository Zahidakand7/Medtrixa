package com.medtrixa.app.ui.screens.more.topics

import com.medtrixa.app.ui.screens.more.*

internal val conversionB: List<TopicSection> = listOf(
    TopicSection("Opioid Equianalgesic Doses", listOf(
        TopicBlock.Text("Approximate doses that give similar analgesia in opioid-tolerant adults. Use them as a starting reference when switching opioids, never as an exact conversion."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("Morphine", "", "Oral: 30 mg\nIV or subcutaneous: 10 mg"),
            AbbrevRow("Hydromorphone", "", "Oral: 7.5 mg\nIV or subcutaneous: 1.5 mg"),
            AbbrevRow("Oxycodone", "", "Oral: 20 mg\nIV: Not available"),
            AbbrevRow("Hydrocodone", "", "Oral: 30 mg\nIV: Not available"),
            AbbrevRow("Oxymorphone", "", "Oral: 10 mg\nIV: 1 mg"),
            AbbrevRow("Codeine", "", "Oral: 200 mg\nParenteral: 130 mg"),
            AbbrevRow("Fentanyl", "", "Oral: Not applicable\nIV: 0.1 mg (100 mcg)"),
            AbbrevRow("Methadone", "", "Conversion is dose-dependent and non-linear, so use specialist guidance")
        ))
    ), TopicAccent.RED),
    TopicSection("Oral Morphine Milligram Equivalents", listOf(
        TopicBlock.Text("Conversion factors that express total daily opioid exposure as oral morphine milligram equivalents (MME). The factors follow the CDC clinical practice guideline and serve risk assessment, not dose switching."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("Codeine", "", "Factor: 0.15\nExample: 60 mg per day is 9 MME per day"),
            AbbrevRow("Hydrocodone", "", "Factor: 1\nExample: 30 mg per day is 30 MME per day"),
            AbbrevRow("Hydromorphone", "", "Factor: 5\nExample: 6 mg per day is 30 MME per day"),
            AbbrevRow("Morphine", "", "Factor: 1\nExample: 30 mg per day is 30 MME per day"),
            AbbrevRow("Oxycodone", "", "Factor: 1.5\nExample: 20 mg per day is 30 MME per day"),
            AbbrevRow("Oxymorphone", "", "Factor: 3\nExample: 10 mg per day is 30 MME per day"),
            AbbrevRow("Tapentadol", "", "Factor: 0.4\nExample: 50 mg per day is 20 MME per day"),
            AbbrevRow("Tramadol", "", "Factor: 0.2\nExample: 100 mg per day is 20 MME per day"),
            AbbrevRow("Fentanyl transdermal", "", "Factor: 2.4 per mcg/hour\nExample: 25 mcg/hour is 60 MME per day"),
            AbbrevRow("Methadone", "", "Dose-dependent factor, so calculate with specialist guidance")
        )),
        TopicBlock.Bullets(listOf(
            "Daily total: Multiply each drug's daily dose by its factor, then add the results."
        ))
    ), TopicAccent.ROSE),
    TopicSection("Opioid Switching Safety", listOf(
        TopicBlock.Bullets(listOf(
            "Incomplete cross-tolerance: Reduce the calculated dose of the new opioid by 25 - 50% when switching, and reassess frequently.",
            "Methadone: Conversion is non-linear and unpredictable, so involve a specialist and expect much larger dose reductions.",
            "Fentanyl patch: Avoid in opioid-naive patients, and note that absorption continues for many hours after removal.",
            "Renal impairment: Metabolites of morphine and codeine accumulate, so choose and dose with care.",
            "Meperidine: Avoid for repeated dosing because its metabolite normeperidine is neurotoxic.",
            "Naloxone: Offer naloxone to patients at increased risk of overdose.",
            "Approximate values: Individual response varies, so titrate to effect and monitor sedation and respiration."
        ))
    ), TopicAccent.AMBER)
)
