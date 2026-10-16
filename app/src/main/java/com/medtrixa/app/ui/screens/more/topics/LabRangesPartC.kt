package com.medtrixa.app.ui.screens.more.topics

import com.medtrixa.app.ui.screens.more.*

internal val labSectionsC: List<TopicSection> = listOf(
    TopicSection("Glycemic and Endocrine", listOf(
        TopicBlock.Text("Diagnostic thresholds follow the American Diabetes Association criteria. Thyroid and inflammatory markers vary by assay."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("HbA1c", "Glycated hemoglobin", "Male and female:\n> Normal: Below 5.7%\n> Prediabetes: 5.7 - 6.4%\n> Diabetes: 6.5% or higher\nChild: The same thresholds apply in adolescents", "Interpret with care in hemoglobinopathies and altered red cell turnover."),
            AbbrevRow("FPG", "Fasting plasma glucose", "Male and female:\n> Normal: Below 100 mg/dL\n> Prediabetes: 100 - 125 mg/dL\n> Diabetes: 126 mg/dL or higher\nChild: The same thresholds apply in adolescents"),
            AbbrevRow("OGTT", "2-hour oral glucose tolerance test", "Male and female:\n> Normal: Below 140 mg/dL\n> Prediabetes: 140 - 199 mg/dL\n> Diabetes: 200 mg/dL or higher\nChild: The same thresholds apply in adolescents"),
            AbbrevRow("TSH", "Thyroid-stimulating hormone", "Male and female: 0.4 - 4.0 mIU/L\nChild: Higher in infancy and early childhood, so use age-specific intervals"),
            AbbrevRow("fT4", "Free thyroxine", "Male and female: 0.8 - 1.8 ng/dL (10 - 23 pmol/L)\nChild: Similar to adults after infancy, and higher in newborns"),
            AbbrevRow("CRP", "C-reactive protein", "Male and female: Below 5 mg/L\nChild: Below 5 mg/L, and higher in the first days of life"),
            AbbrevRow("ESR", "Erythrocyte sedimentation rate", "Male: Up to 15 mm/h under 50 years\nFemale: Up to 20 mm/h under 50 years\nChild: 0 - 10 mm/h")
        ))
    ), TopicAccent.TEAL),
    TopicSection("Arterial Blood Gas", listOf(
        TopicBlock.Text("Arterial values at sea level on room air. Venous gases cannot replace arterial gases for assessing oxygenation."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("pH", "Arterial pH", "Male and female: 7.35 - 7.45\nChild:\n> Newborn: 7.30 - 7.45\n> Infants and children: 7.35 - 7.45"),
            AbbrevRow("PaCO₂", "Partial pressure of carbon dioxide", "Male and female: 35 - 45 mmHg (4.7 - 6.0 kPa)\nChild:\n> Newborn: 30 - 40 mmHg\n> Infants and children: 35 - 45 mmHg", "Values fall during pregnancy."),
            AbbrevRow("PaO₂", "Partial pressure of oxygen", "Male and female: 80 - 100 mmHg (10.7 - 13.3 kPa)\nChild: 80 - 100 mmHg, and lower in newborns"),
            AbbrevRow("HCO₃⁻", "Bicarbonate", "Male and female: 22 - 26 mmol/L\nChild:\n> Newborn: 18 - 24 mmol/L\n> Infants and children: 20 - 26 mmol/L"),
            AbbrevRow("BE", "Base excess", "Male and female: −2 to +2 mmol/L\nChild: −2 to +2 mmol/L, and wider in newborns"),
            AbbrevRow("SaO₂", "Arterial oxygen saturation", "Male and female: 95 - 100%\nChild: 95 - 100%"),
            AbbrevRow("Lac", "Lactate", "Male and female: 0.5 - 2.2 mmol/L\nChild: 0.5 - 2.2 mmol/L, and higher in the first hours of life")
        ))
    ), TopicAccent.BLUE),
    TopicSection("Urine Studies", listOf(
        TopicBlock.Text("Urine tests screen for kidney disease, infection and concentration disorders."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("ACR", "Albumin-to-creatinine ratio", "Male and female: Below 30 mg/g (3 mg/mmol)\nChild: Below 30 mg/g"),
            AbbrevRow("SG", "Specific gravity", "Male and female: 1.005 - 1.030\nChild: 1.003 - 1.030"),
            AbbrevRow("pH", "Urine pH", "Male and female: 4.5 - 8.0\nChild:\n> Newborn: 5.0 - 7.0\n> Infants and children: 4.5 - 8.0"),
            AbbrevRow("Protein", "24-hour urine protein", "Male and female: Below 150 mg per 24 hours\nChild: Below 100 mg/m² per day"),
            AbbrevRow("LE, Nitrite", "Leukocyte esterase and nitrite", "Male and female: Negative\nChild: Negative")
        ))
    ), TopicAccent.AMBER),
    TopicSection("Critical Values", listOf(
        TopicBlock.Text("Critical values are institution-specific. The examples below are commonly used thresholds for urgent notification and must be confirmed against local policy."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("K⁺", "Potassium", "Male and female: Below 2.5 or above 6.5 mmol/L\nChild: Confirm against the local pediatric critical-value list"),
            AbbrevRow("Na⁺", "Sodium", "Male and female: Below 120 or above 160 mmol/L\nChild: Confirm against the local pediatric critical-value list"),
            AbbrevRow("Glu", "Glucose", "Male and female: Below 40 or above 500 mg/dL\nChild: Below 40 mg/dL, and confirm the upper limit locally"),
            AbbrevRow("Hb", "Hemoglobin", "Male and female: Below 7 g/dL\nChild: Below 7 g/dL beyond infancy, and newborn thresholds are higher"),
            AbbrevRow("Plt", "Platelets", "Male and female: Below 20 ×10⁹/L\nChild: Below 20 ×10⁹/L"),
            AbbrevRow("INR", "International normalized ratio", "Male and female: Above 5.0\nChild: Confirm against the local pediatric critical-value list"),
            AbbrevRow("pH", "Arterial pH", "Male and female: Below 7.20 or above 7.60\nChild: Below 7.20 or above 7.60")
        ))
    ), TopicAccent.RED),
    TopicSection("Clinical Pearls", listOf(
        TopicBlock.Bullets(listOf(
            "Corrected calcium: measured Ca²⁺ plus 0.8 × (4.0 − albumin in g/dL).",
            "Anion gap: Na⁺ − (Cl⁻ + HCO₃⁻). For low albumin, add 2.5 mmol/L for every 1 g/dL below 4.0 g/dL.",
            "Corrected sodium: add 1.6 mmol/L for every 100 mg/dL of glucose above 100 mg/dL.",
            "Pseudohyperkalemia: hemolysis, delayed processing and marked leukocytosis or thrombocytosis can falsely raise K⁺.",
            "Acute kidney injury: creatinine rise of 0.3 mg/dL within 48 hours, or 1.5 times baseline within 7 days (KDIGO).",
            "Anemia work-up: read MCV with RDW and reticulocytes to separate microcytic, normocytic and macrocytic causes.",
            "Physiologic anemia: hemoglobin in term infants reaches its lowest value at about 8 - 12 weeks of age.",
            "Differential count: lymphocytes predominate until about 4 - 6 years of age."
        )),
        TopicBlock.Callout(
            "Disclaimer",
            "Reference intervals vary by laboratory, method, age, sex, pregnancy and altitude. Always interpret a result against the reporting laboratory's range and the clinical context. This guide is an educational reference and does not replace clinical judgment or institutional policy.",
            CalloutKind.INFO
        )
    ), TopicAccent.GREEN)
)
