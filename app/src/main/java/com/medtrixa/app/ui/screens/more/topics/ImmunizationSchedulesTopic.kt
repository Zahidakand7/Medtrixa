package com.medtrixa.app.ui.screens.more.topics

import com.medtrixa.app.ui.screens.more.*

val ImmunizationSchedulesTopic = MoreTopic(
    id = "immunization_schedules",
    title = "Immunization Schedules",
    subtitle = "US pediatric and adult vaccine schedules (CDC)",
    badge = "VAX",
    accent = TopicAccent.TEAL,
    category = "Prevention",
    keywords = listOf("immunization", "vaccine", "vaccination", "schedule", "pediatric", "childhood", "adolescent", "adult", "catch-up", "hepatitis b", "dtap", "tdap", "mmr", "varicella", "hpv", "meningococcal", "pneumococcal", "influenza", "flu", "covid", "rsv", "shingrix", "zoster", "rotavirus", "polio", "hib", "pregnancy"),
    sections = immunSectionsA + immunSectionsB + immunSectionsC,
    references = listOf(
        "Centers for Disease Control and Prevention. Child and Adolescent Immunization Schedule by Age, Notes and Addendum. Recommendations for ages 18 years or younger, United States, 2025 (addendum of 2 July 2025).",
        "Centers for Disease Control and Prevention. Adult Immunization Schedule, Notes and Addendum. Recommendations for ages 19 years or older, United States, 2025 (revised 2 July 2025, amended 27 April 2026).",
        "Centers for Disease Control and Prevention. General Best Practice Guidelines for Immunization. Advisory Committee on Immunization Practices. Current edition.",
        "American Academy of Pediatrics. Recommended Child and Adolescent Immunization Schedule. Current edition."
    )
)
