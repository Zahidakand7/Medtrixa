package com.medtrixa.app.ui.screens.more.topics

import com.medtrixa.app.ui.screens.more.*

val ConversionTablesTopic = MoreTopic(
    id = "conversion_tables",
    title = "Conversion Tables",
    subtitle = "Steroid, opioid and insulin equivalence at a glance",
    badge = "Conv",
    accent = TopicAccent.VIOLET,
    category = "Medications",
    keywords = listOf("conversion", "equivalence", "steroid", "prednisone", "dexamethasone", "hydrocortisone", "opioid", "morphine", "mme", "equianalgesic", "insulin", "onset", "peak", "duration", "nph", "glargine"),
    sections = conversionA + conversionB + conversionC,
    references = listOf(
        "Dowell D, et al. CDC Clinical Practice Guideline for Prescribing Opioids for Pain. MMWR Recomm Rep. 2022;71(3):1-95.",
        "McPherson ML. Demystifying Opioid Conversion Calculations: A Guide for Effective Dosing. 2nd ed. ASHP; 2018.",
        "Meikle AW, Tyler FH. Potency and duration of action of glucocorticoids. Am J Med. 1977;63:200-207.",
        "Liu D, et al. A practical guide to the monitoring and management of the complications of systemic corticosteroid therapy. Allergy Asthma Clin Immunol. 2013;9:30.",
        "American Diabetes Association. Standards of Care in Diabetes. Diabetes Care. Current edition.",
        "Manufacturer prescribing information for insulin products. Current editions."
    )
)
