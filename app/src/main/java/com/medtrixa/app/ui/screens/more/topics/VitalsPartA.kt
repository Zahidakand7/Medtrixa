package com.medtrixa.app.ui.screens.more.topics

import com.medtrixa.app.ui.screens.more.*

internal val vitalsA: List<TopicSection> = listOf(
    TopicSection("Normal Vital Signs by Age", listOf(
        TopicBlock.Text("Typical resting ranges for adults and for children by age band. Values vary with activity, illness, altitude and measurement method, so interpret them in clinical context."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("HR", "Heart rate", "Male and female: 60 - 100 beats/min at rest\nChild:\n> Newborn (0 - 28 days): 100 - 205 beats/min awake and 90 - 160 beats/min asleep\n> Infant (1 - 12 months): 100 - 180 beats/min awake and 90 - 160 beats/min asleep\n> Toddler (1 - 3 years): 98 - 140 beats/min awake and 80 - 120 beats/min asleep\n> Preschool (3 - 5 years): 80 - 120 beats/min awake and 65 - 100 beats/min asleep\n> School age (6 - 12 years): 75 - 118 beats/min awake and 58 - 90 beats/min asleep\n> Adolescent (13 - 18 years): 60 - 100 beats/min awake and 50 - 90 beats/min asleep"),
            AbbrevRow("RR", "Respiratory rate", "Male and female: 12 - 20 breaths/min\nChild:\n> Newborn (0 - 28 days): 30 - 60 breaths/min\n> Infant (1 - 12 months): 30 - 53 breaths/min\n> Toddler (1 - 3 years): 22 - 37 breaths/min\n> Preschool (3 - 5 years): 20 - 28 breaths/min\n> School age (6 - 12 years): 18 - 25 breaths/min\n> Adolescent (13 - 18 years): 12 - 20 breaths/min"),
            AbbrevRow("BP", "Blood pressure", "Male and female:\n> Normal: Below 120/80 mmHg\n> Elevated: 120 - 129 and below 80 mmHg\n> Stage 1 hypertension: 130 - 139 or 80 - 89 mmHg\n> Stage 2 hypertension: 140/90 mmHg or higher\n> Hypertensive crisis: Above 180 and/or above 120 mmHg\nChild (systolic / diastolic):\n> Newborn (0 - 28 days): 60 - 90 / 20 - 60 mmHg\n> Infant (1 - 12 months): 87 - 105 / 53 - 66 mmHg\n> Toddler (1 - 3 years): 95 - 105 / 53 - 66 mmHg\n> Preschool (3 - 5 years): 95 - 110 / 56 - 70 mmHg\n> School age (6 - 12 years): 97 - 112 / 57 - 71 mmHg\n> Adolescent (13 - 18 years): 112 - 128 / 66 - 80 mmHg", "Pediatric diagnosis uses percentiles for age, sex and height. The values shown are typical resting ranges."),
            AbbrevRow("SBP", "Hypotension threshold", "Male and female: Below 90 mmHg systolic\nChild:\n> Newborn (0 - 28 days): Below 60 mmHg\n> Infant (1 - 12 months): Below 70 mmHg\n> 1 - 10 years: Below 70 + (2 × age in years) mmHg\n> Over 10 years: Below 90 mmHg"),
            AbbrevRow("Temp", "Body temperature", "Male and female:\n> Normal: 36.5 - 37.5°C (97.7 - 99.5°F)\n> Fever: 38.0°C (100.4°F) or higher\n> Hypothermia: Below 35.0°C (95.0°F)\nChild:\n> Newborn and infant under 3 months: Fever of 38.0°C (100.4°F) or higher needs urgent evaluation\n> Infants and children: The same fever threshold of 38.0°C (100.4°F) applies"),
            AbbrevRow("SpO₂", "Oxygen saturation", "Male and female:\n> Healthy adult: 95 - 100% on room air\n> Risk of hypercapnic respiratory failure: Target 88 - 92%\nChild:\n> Infants and children: 95 - 100% on room air"),
            AbbrevRow("SpO₂ newborn", "Targets after birth", "1 minute: 60 - 65%\n2 minutes: 65 - 70%\n3 minutes: 70 - 75%\n4 minutes: 75 - 80%\n5 minutes: 80 - 85%\n10 minutes: 85 - 95%", "Measure preductal SpO₂ on the right hand or wrist, per the Neonatal Resuscitation Program.")
        ))
    ), TopicAccent.GREEN),
    TopicSection("Derived Values", listOf(
        TopicBlock.Bullets(listOf(
            "Mean arterial pressure: (Systolic + 2 × Diastolic) ÷ 3. Normal is 70 - 100 mmHg, and 65 mmHg or higher is a common perfusion target.",
            "Pulse pressure: Systolic − Diastolic. Normal is about 30 - 50 mmHg.",
            "Shock index: Heart rate ÷ Systolic BP. Normal is 0.5 - 0.7, and a value above 0.9 in adults suggests hemodynamic compromise."
        ))
    ), TopicAccent.BLUE),
    TopicSection("Pediatric Quick Estimates", listOf(
        TopicBlock.Text("Estimates for emergencies when a measured weight is unavailable. A length-based tape such as the Broselow tape is more accurate than age-based formulas."),
        TopicBlock.Bullets(listOf(
            "Weight 3 - 12 months: (Age in months + 9) ÷ 2, in kg.",
            "Weight 1 - 5 years: (Age in years + 4) × 2, in kg.",
            "Weight 6 - 12 years: (Age in years × 3) + 7, in kg.",
            "Cuffed endotracheal tube: (Age in years ÷ 4) + 3.5, as internal diameter in mm.",
            "Uncuffed endotracheal tube: (Age in years ÷ 4) + 4, as internal diameter in mm.",
            "Oral tube depth: (Age in years ÷ 2) + 12, in cm at the lip."
        ))
    ), TopicAccent.TEAL),
    TopicSection("Red Flag Vital Signs", listOf(
        TopicBlock.Text("Extreme adult values that score highest on the National Early Warning Score 2 (NEWS2) and call for urgent review. Children need age-specific early warning tools."),
        TopicBlock.Bullets(listOf(
            "Respiratory rate: 8 breaths/min or below, or 25 breaths/min or above.",
            "SpO₂: 91% or below on room air.",
            "Systolic BP: 90 mmHg or below, or 220 mmHg or above.",
            "Pulse: 40 beats/min or below, or 131 beats/min or above.",
            "Temperature: 35.0°C or below, or 39.1°C or above.",
            "Consciousness: New confusion, or responding only to voice, pain or not at all."
        ))
    ), TopicAccent.RED)
)
