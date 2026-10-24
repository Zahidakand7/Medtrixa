package com.medtrixa.app.ui.screens.more.topics

import com.medtrixa.app.ui.screens.more.*

val AntimicrobialTopic = MoreTopic(
    id = "antimicrobial_spectrum",
    title = "Antimicrobial Spectrum Guide",
    subtitle = "Coverage by drug class",
    badge = "Abx",
    accent = TopicAccent.TEAL,
    category = "Medications",
    keywords = listOf("antibiotic", "antimicrobial", "spectrum", "coverage", "penicillin", "cephalosporin", "carbapenem", "vancomycin", "mrsa", "pseudomonas", "anaerobes", "atypicals", "esbl", "ampc", "fluoroquinolone", "macrolide", "antifungal", "stewardship", "intrinsic resistance", "allergy"),
    sections = antimicrobialA + antimicrobialB + antimicrobialC,
    references = listOf(
        "Tamma PD, et al. Infectious Diseases Society of America Guidance on the Treatment of Antimicrobial-Resistant Gram-Negative Infections. Clin Infect Dis. Current edition.",
        "Bennett JE, Dolin R, Blaser MJ, eds. Mandell, Douglas, and Bennett's Principles and Practice of Infectious Diseases. Current edition.",
        "European Committee on Antimicrobial Susceptibility Testing (EUCAST). Expected Resistant Phenotypes. Current edition.",
        "Clinical and Laboratory Standards Institute. Performance Standards for Antimicrobial Susceptibility Testing (M100). Current edition.",
        "Pappas PG, et al. Clinical Practice Guideline for the Management of Candidiasis: 2016 Update. Clin Infect Dis. 2016;62:e1-e50.",
        "Shenoy ES, et al. Evaluation and Management of Penicillin Allergy: A Review. JAMA. 2019;321:188-199.",
        "Gilbert DN, et al., eds. The Sanford Guide to Antimicrobial Therapy. Current edition."
    )
)
