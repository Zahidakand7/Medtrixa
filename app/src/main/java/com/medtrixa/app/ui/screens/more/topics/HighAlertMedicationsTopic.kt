package com.medtrixa.app.ui.screens.more.topics

import com.medtrixa.app.ui.screens.more.*

val HighAlertMedicationsTopic = MoreTopic(
    id = "high_alert_medications",
    title = "High-Alert Medications",
    subtitle = "ISMP high-alert classes and tall-man lettering",
    badge = "HAM",
    accent = TopicAccent.AMBER,
    category = "Medication Safety",
    keywords = listOf("high alert", "high-alert", "ismp", "tall man", "tall-man", "look alike", "sound alike", "lasa", "insulin", "heparin", "opioid", "potassium chloride", "medication errors", "double check"),
    sections = highAlertA + highAlertB,
    references = listOf(
        "Institute for Safe Medication Practices (ISMP). List of High-Alert Medications in Acute Care Settings. Current edition.",
        "Institute for Safe Medication Practices (ISMP). List of High-Alert Medications in Community/Ambulatory Healthcare. Current edition.",
        "FDA and ISMP Lists of Look-Alike Drug Names with Recommended Tall Man (Mixed Case) Letters. Current edition.",
        "The Joint Commission. National Patient Safety Goals, Medication Safety. Current edition.",
        "World Health Organization. Medication Without Harm: Global Patient Safety Challenge on Medication Safety. 2017."
    )
)
