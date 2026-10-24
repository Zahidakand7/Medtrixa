package com.medtrixa.app.ui.screens.more.topics

import com.medtrixa.app.ui.screens.more.*

val MedicalTermsTopic = MoreTopic(
    id = "medical_terms",
    title = "Medical Terms",
    subtitle = "Word parts, key terms and look-alike pairs",
    badge = "MED",
    accent = TopicAccent.AMBER,
    category = "Terminology",
    keywords = listOf("medical terms", "terminology", "glossary", "prefix", "suffix", "root", "word parts", "anatomy", "directions", "position", "supine", "prone", "fowler", "trendelenburg", "sign", "symptom", "dyspnea", "melena", "hematemesis", "hemoptysis", "edema", "cyanosis", "jaundice", "ischemia", "infarction", "hypoxia", "hypoxemia", "sepsis", "biopsy", "endoscopy", "imaging", "ecg", "confused terms"),
    sections = mtSectionsA + mtSectionsB + mtSectionsC,
    references = listOf(
        "Dorland's Illustrated Medical Dictionary. 33rd ed. Elsevier; 2020.",
        "Stedman's Medical Dictionary. 28th ed. Wolters Kluwer; 2005.",
        "Chabner DE. The Language of Medicine. 12th ed. Elsevier; 2020.",
        "Singer M, et al. The Third International Consensus Definitions for Sepsis and Septic Shock (Sepsis-3). JAMA. 2016;315(8):801-810."
    )
)
