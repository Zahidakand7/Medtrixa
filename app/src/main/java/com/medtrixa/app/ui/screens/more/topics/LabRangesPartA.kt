package com.medtrixa.app.ui.screens.more.topics

import com.medtrixa.app.ui.screens.more.*

internal val labSectionsA: List<TopicSection> = listOf(
    TopicSection("Complete Blood Count", listOf(
        TopicBlock.Text("Reference intervals for red cells, white cells and platelets. Every test lists male, female and pediatric values, with SI units where they differ."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("Hb", "Hemoglobin", "Male: 13.5 - 17.5 g/dL\nFemale: 12.0 - 15.5 g/dL\nChild:\n> Newborn: 14.0 - 24.0 g/dL\n> 6 months - 6 years: 10.5 - 14.0 g/dL\n> 6 - 12 years: 11.5 - 15.5 g/dL\n> 12 - 18 years: 13.0 - 16.0 g/dL (Male)\n> 12 - 18 years: 12.0 - 16.0 g/dL (Female)"),
            AbbrevRow("Hct", "Hematocrit", "Male: 41 - 50%\nFemale: 36 - 44%\nChild:\n> Newborn: 44 - 64%\n> 6 months - 6 years: 33 - 42%\n> 6 - 12 years: 35 - 45%"),
            AbbrevRow("RBC", "Red blood cell count", "Male: 4.5 - 5.9 ×10¹²/L\nFemale: 4.1 - 5.1 ×10¹²/L\nChild:\n> Newborn: 4.0 - 6.6 ×10¹²/L\n> 6 months - 6 years: 3.9 - 5.3 ×10¹²/L\n> 6 - 12 years: 4.0 - 5.2 ×10¹²/L"),
            AbbrevRow("MCV", "Mean corpuscular volume", "Male and female: 80 - 100 fL\nChild:\n> Newborn: 95 - 120 fL\n> 6 months - 6 years: 70 - 87 fL\n> 6 - 12 years: 77 - 95 fL"),
            AbbrevRow("MCH", "Mean corpuscular hemoglobin", "Male and female: 27 - 33 pg\nChild:\n> 6 months - 6 years: 23 - 31 pg\n> 6 - 12 years: 25 - 33 pg"),
            AbbrevRow("MCHC", "Mean corpuscular hemoglobin concentration", "Male and female: 32 - 36 g/dL\nChild: 32 - 36 g/dL beyond the newborn period"),
            AbbrevRow("RDW", "Red cell distribution width", "Male and female: 11.5 - 14.5%\nChild:\n> Newborn: Higher than the adult range\n> After infancy: 11.5 - 14.5%"),
            AbbrevRow("Retic", "Reticulocytes", "Male and female: 0.5 - 2.5% of red cells\nChild:\n> Newborn (first days): 2.0 - 6.0%\n> Infants and children: 0.5 - 2.5%"),
            AbbrevRow("WBC", "White blood cell count", "Male and female: 4.0 - 11.0 ×10⁹/L\nChild:\n> Newborn: 9.0 - 30.0 ×10⁹/L\n> 1 - 2 years: 6.0 - 17.0 ×10⁹/L\n> 2 - 6 years: 5.5 - 15.5 ×10⁹/L\n> 6 - 12 years: 4.5 - 13.5 ×10⁹/L"),
            AbbrevRow("Neut", "Neutrophils", "Male and female: 2.0 - 7.5 ×10⁹/L\nChild: 1.5 - 8.5 ×10⁹/L beyond infancy"),
            AbbrevRow("Lymph", "Lymphocytes", "Male and female: 1.0 - 4.0 ×10⁹/L\nChild:\n> 1 - 6 years: 3.0 - 9.5 ×10⁹/L\n> 6 - 12 years: 1.5 - 6.5 ×10⁹/L"),
            AbbrevRow("Mono", "Monocytes", "Male and female: 0.2 - 1.0 ×10⁹/L\nChild: 0.2 - 1.2 ×10⁹/L"),
            AbbrevRow("Eos", "Eosinophils", "Male and female: 0.0 - 0.5 ×10⁹/L\nChild: 0.0 - 0.7 ×10⁹/L"),
            AbbrevRow("Baso", "Basophils", "Male and female: 0.0 - 0.1 ×10⁹/L\nChild: 0.0 - 0.2 ×10⁹/L"),
            AbbrevRow("Plt", "Platelets", "Male and female: 150 - 400 ×10⁹/L\nChild: 150 - 450 ×10⁹/L from birth to adolescence")
        ))
    ), TopicAccent.ROSE),
    TopicSection("Electrolytes and Renal Function", listOf(
        TopicBlock.Text("Basic metabolic panel values. Total calcium should be read with albumin, and creatinine with age, sex and muscle mass."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("Na⁺", "Sodium", "Male and female: 135 - 145 mmol/L\nChild:\n> Newborn: 133 - 146 mmol/L\n> Infants and children: 135 - 145 mmol/L"),
            AbbrevRow("K⁺", "Potassium", "Male and female: 3.5 - 5.0 mmol/L\nChild:\n> Newborn: 3.7 - 5.9 mmol/L\n> Infants and children: 3.5 - 5.0 mmol/L"),
            AbbrevRow("Cl⁻", "Chloride", "Male and female: 98 - 106 mmol/L\nChild:\n> Newborn: 96 - 110 mmol/L\n> Infants and children: 98 - 106 mmol/L"),
            AbbrevRow("HCO₃⁻", "Bicarbonate", "Male and female: 22 - 29 mmol/L\nChild:\n> Infant: 18 - 24 mmol/L\n> Child: 20 - 28 mmol/L"),
            AbbrevRow("Ca²⁺", "Total calcium", "Male and female: 8.5 - 10.5 mg/dL (2.12 - 2.62 mmol/L)\nChild:\n> Newborn (0 - 10 days): 7.6 - 10.4 mg/dL\n> 10 days - 2 years: 9.0 - 11.0 mg/dL\n> Over 2 years: 9.0 - 10.5 mg/dL"),
            AbbrevRow("Ca²⁺ ionized", "Ionized calcium", "Male and female: 4.6 - 5.3 mg/dL (1.15 - 1.32 mmol/L)\nChild: 4.8 - 5.5 mg/dL (1.20 - 1.38 mmol/L)"),
            AbbrevRow("Mg²⁺", "Magnesium", "Male and female: 1.7 - 2.2 mg/dL (0.70 - 0.90 mmol/L)\nChild: 1.7 - 2.3 mg/dL (0.70 - 0.95 mmol/L)"),
            AbbrevRow("PO₄³⁻", "Phosphate", "Male and female: 2.5 - 4.5 mg/dL (0.81 - 1.45 mmol/L)\nChild:\n> Newborn: 5.0 - 7.8 mg/dL\n> 1 - 5 years: 4.5 - 6.5 mg/dL\n> 6 - 12 years: 3.6 - 5.8 mg/dL\n> Over 12 years: 2.3 - 4.5 mg/dL"),
            AbbrevRow("BUN", "Blood urea nitrogen", "Male and female: 7 - 20 mg/dL (2.5 - 7.1 mmol/L urea)\nChild:\n> Newborn: 3 - 12 mg/dL\n> Infant and child: 5 - 18 mg/dL"),
            AbbrevRow("Cr", "Creatinine", "Male: 0.7 - 1.3 mg/dL (62 - 115 µmol/L)\nFemale: 0.6 - 1.1 mg/dL (53 - 97 µmol/L)\nChild:\n> Infant: 0.2 - 0.4 mg/dL (18 - 35 µmol/L)\n> Child: 0.3 - 0.7 mg/dL (27 - 62 µmol/L)\n> Adolescent: 0.5 - 1.0 mg/dL (44 - 88 µmol/L)"),
            AbbrevRow("eGFR", "Estimated glomerular filtration rate", "Male and female: 90 mL/min/1.73 m² or higher, declining with age\nChild:\n> First 2 years: Lower, rising as the kidney matures\n> Over 2 years: About 90 - 130 mL/min/1.73 m²"),
            AbbrevRow("UA", "Uric acid", "Male: 3.4 - 7.0 mg/dL (200 - 420 µmol/L)\nFemale: 2.4 - 6.0 mg/dL (140 - 360 µmol/L)\nChild: 2.0 - 5.5 mg/dL before puberty"),
            AbbrevRow("Glu", "Fasting glucose", "Male and female: 70 - 99 mg/dL (3.9 - 5.5 mmol/L)\nChild:\n> Fasting: 60 - 100 mg/dL (3.3 - 5.5 mmol/L)\n> Newborn: Below 45 mg/dL (2.5 mmol/L) needs urgent evaluation"),
            AbbrevRow("AG", "Anion gap", "Male and female: 8 - 12 mmol/L, calculated as Na⁺ − (Cl⁻ + HCO₃⁻)\nChild:\n> Infants and children: 8 - 12 mmol/L\n> Newborn: Up to 16 mmol/L")
        ))
    ), TopicAccent.BLUE)
)
