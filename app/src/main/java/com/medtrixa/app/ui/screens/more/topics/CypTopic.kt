package com.medtrixa.app.ui.screens.more.topics

import com.medtrixa.app.ui.screens.more.*

val CypTopic = MoreTopic(
    id = "cyp450_reference",
    title = "CYP450 Quick Reference",
    subtitle = "Major substrates, inhibitors and inducers",
    badge = "CYP",
    accent = TopicAccent.VIOLET,
    category = "Medications",
    keywords = listOf("cyp450", "cyp3a4", "cyp2d6", "cyp2c9", "cyp2c19", "cyp1a2", "inhibitor", "inducer", "substrate", "drug interaction", "grapefruit", "rifampin", "clarithromycin", "pharmacogenetics", "prodrug"),
    sections = cypA + cypB + cypC,
    references = listOf(
        "US Food and Drug Administration. Drug Development and Drug Interactions: Table of Substrates, Inhibitors and Inducers. Current edition.",
        "US Food and Drug Administration. Clinical Drug Interaction Studies: Cytochrome P450 Enzyme- and Transporter-Mediated Drug Interactions. Guidance for Industry. Current edition.",
        "Flockhart DA. Drug Interactions: Cytochrome P450 Drug Interaction Table. Indiana University School of Medicine. Current edition.",
        "Zanger UM, Schwab M. Cytochrome P450 enzymes in drug metabolism. Pharmacol Ther. 2013;138:103-141.",
        "Clinical Pharmacogenetics Implementation Consortium (CPIC). Guidelines. Current editions."
    )
)
