package com.medtrixa.app.ui.screens.more.topics

import com.medtrixa.app.ui.screens.more.*

internal val labSectionsB: List<TopicSection> = listOf(
    TopicSection("Liver Function", listOf(
        TopicBlock.Text("Hepatocellular enzymes, cholestatic markers and synthetic function. Upper limits differ by assay, sex and age."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("ALT", "Alanine aminotransferase", "Male: 7 - 55 U/L\nFemale: 7 - 45 U/L\nChild:\n> Infants: Up to 45 U/L\n> Older children: Up to 35 U/L"),
            AbbrevRow("AST", "Aspartate aminotransferase", "Male: 8 - 48 U/L\nFemale: 8 - 43 U/L\nChild:\n> Newborn: 25 - 75 U/L\n> Infant: 15 - 60 U/L\n> Child: 10 - 40 U/L"),
            AbbrevRow("ALP", "Alkaline phosphatase", "Male: 40 - 129 U/L\nFemale: 35 - 104 U/L\nChild: 150 - 420 U/L, and higher during growth spurts", "Raised values can be normal during puberty."),
            AbbrevRow("GGT", "Gamma-glutamyl transferase", "Male: 8 - 61 U/L\nFemale: 5 - 36 U/L\nChild:\n> Newborn: Up to 200 U/L\n> Children: 5 - 30 U/L"),
            AbbrevRow("TBil", "Total bilirubin", "Male and female: 0.1 - 1.2 mg/dL (2 - 21 µmol/L)\nChild: 0.2 - 1.0 mg/dL after the first month", "In newborns, interpret by hours of life on a validated nomogram."),
            AbbrevRow("DBil", "Direct bilirubin", "Male and female: 0.0 - 0.3 mg/dL (0 - 5 µmol/L)\nChild: 0.0 - 0.3 mg/dL"),
            AbbrevRow("Alb", "Albumin", "Male and female: 3.5 - 5.0 g/dL (35 - 50 g/L)\nChild:\n> Newborn: 2.8 - 4.4 g/dL\n> Child: 3.8 - 5.4 g/dL"),
            AbbrevRow("TP", "Total protein", "Male and female: 6.0 - 8.3 g/dL (60 - 83 g/L)\nChild:\n> Newborn: 4.6 - 7.4 g/dL\n> Child: 6.0 - 8.0 g/dL")
        ))
    ), TopicAccent.AMBER),
    TopicSection("Coagulation", listOf(
        TopicBlock.Text("Clotting tests reflect different parts of the coagulation cascade. Therapeutic targets depend on the drug and the indication."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("PT", "Prothrombin time", "Male and female: 11 - 13.5 seconds\nChild: 11 - 14 seconds, and slightly longer in newborns"),
            AbbrevRow("INR", "International normalized ratio", "Male and female: 0.8 - 1.1 without anticoagulant\nChild: 0.8 - 1.2 without anticoagulant", "The usual warfarin target is 2.0 - 3.0 and is higher for selected mechanical valves."),
            AbbrevRow("aPTT", "Activated partial thromboplastin time", "Male and female: 25 - 35 seconds\nChild:\n> Newborn: Up to 55 seconds\n> Child: 25 - 40 seconds"),
            AbbrevRow("Fib", "Fibrinogen", "Male and female: 200 - 400 mg/dL (2.0 - 4.0 g/L)\nChild:\n> Newborn: 150 - 400 mg/dL\n> Child: 200 - 400 mg/dL"),
            AbbrevRow("D-dimer", "Fibrin degradation product", "Male and female: Below 500 ng/mL FEU in most assays\nChild: Below 500 ng/mL FEU, with limited pediatric validation", "Cut-offs are assay-specific. Age-adjusted cut-offs exist for patients over 50 years, and pregnancy raises values.")
        ))
    ), TopicAccent.VIOLET),
    TopicSection("Cardiac Markers and Lipids", listOf(
        TopicBlock.Text("Cardiac biomarkers are assay-specific. Lipid values are general goals, and lower targets apply to high-risk patients."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("hs-cTn", "High-sensitivity troponin", "Male: Assay-specific 99th percentile upper reference limit, usually higher than in women\nFemale: Assay-specific 99th percentile, often lower than in men\nChild: Assay-specific, with limited validated pediatric cut-offs", "Use serial values to detect a rise or fall."),
            AbbrevRow("BNP", "B-type natriuretic peptide", "Male and female: Below 100 pg/mL makes acute heart failure unlikely\nChild: Higher in the first weeks of life, so use age-specific intervals", "Values rise with age and are higher in women."),
            AbbrevRow("NT-proBNP", "N-terminal pro-BNP", "Male and female (acute): Below 300 pg/mL rules out acute heart failure\nMale and female (non-acute): Below 125 pg/mL\nChild: Age-specific, with high values in the first weeks of life"),
            AbbrevRow("TC", "Total cholesterol", "Male and female: Below 200 mg/dL (5.2 mmol/L)\nChild: Below 170 mg/dL (4.4 mmol/L) is acceptable"),
            AbbrevRow("LDL-C", "Low-density lipoprotein cholesterol", "Male and female: Below 100 mg/dL (2.6 mmol/L) in the general population\nChild: Below 110 mg/dL (2.8 mmol/L) is acceptable", "Lower targets apply in very high-risk adults."),
            AbbrevRow("HDL-C", "High-density lipoprotein cholesterol", "Male: 40 mg/dL (1.0 mmol/L) or higher\nFemale: 50 mg/dL (1.3 mmol/L) or higher\nChild: Above 45 mg/dL (1.2 mmol/L)"),
            AbbrevRow("TG", "Triglycerides", "Male and female: Below 150 mg/dL (1.7 mmol/L)\nChild:\n> 0 - 9 years: Below 75 mg/dL\n> 10 - 19 years: Below 90 mg/dL"),
            AbbrevRow("Non-HDL-C", "Non-HDL cholesterol", "Male and female: Below 130 mg/dL (3.4 mmol/L)\nChild: Below 120 mg/dL (3.1 mmol/L)")
        ))
    ), TopicAccent.RED)
)
