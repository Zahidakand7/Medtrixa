package com.medtrixa.app.ui.screens.more.topics

import com.medtrixa.app.ui.screens.more.*

val LabReferenceRangesTopic = MoreTopic(
    id = "lab_reference_ranges",
    title = "Lab Reference Ranges",
    subtitle = "Male, female and pediatric reference intervals",
    badge = "Lab",
    accent = TopicAccent.RED,
    category = "Labs & Fluids",
    keywords = listOf("lab", "laboratory", "reference range", "cbc", "bmp", "lft", "coagulation", "inr", "abg", "lipids", "hba1c", "troponin", "bnp", "electrolytes", "critical values", "normal values"),
    sections = labSectionsA + labSectionsB + labSectionsC,
    references = listOf(
        "Goldman L, Schafer AI, eds. Goldman-Cecil Medicine. 26th ed. Elsevier; 2020. Reference intervals appendix.",
        "Mayo Clinic Laboratories. Test Catalog and Reference Values. Current edition.",
        "American Diabetes Association. Standards of Care in Diabetes. Diabetes Care. Current edition.",
        "KDIGO CKD Work Group. KDIGO 2024 Clinical Practice Guideline for the Evaluation and Management of Chronic Kidney Disease. Kidney Int. 2024;105(4S):S117–S314.",
        "Grundy SM, et al. 2018 AHA/ACC Guideline on the Management of Blood Cholesterol. Circulation. 2019;139:e1082–e1143.",
        "McDonagh TA, et al. 2021 ESC Guidelines for the diagnosis and treatment of acute and chronic heart failure. Eur Heart J. 2021;42:3599–3726."
    )
)
