package com.medtrixa.app.ui.screens.more.topics

import com.medtrixa.app.ui.screens.more.*

val VitalSignsTopic = MoreTopic(
    id = "vital_signs_gcs",
    title = "Vital Signs and Pediatric Norms",
    subtitle = "Normal ranges by age with the Glasgow Coma Scale",
    badge = "Vitals",
    accent = TopicAccent.GREEN,
    category = "Clinical Assessment",
    keywords = listOf("vital signs", "heart rate", "respiratory rate", "blood pressure", "temperature", "spo2", "oxygen saturation", "pediatric", "gcs", "glasgow coma scale", "news2", "map", "shock index", "normal values"),
    sections = vitalsA + vitalsB,
    references = listOf(
        "Whelton PK, et al. 2017 ACC/AHA Guideline for the Prevention, Detection, Evaluation, and Management of High Blood Pressure in Adults. Hypertension. 2018;71:e13-e115.",
        "Flynn JT, et al. Clinical Practice Guideline for Screening and Management of High Blood Pressure in Children and Adolescents. Pediatrics. 2017;140(3):e20171904.",
        "American Heart Association. Pediatric Advanced Life Support Provider Manual. Current edition.",
        "Advanced Life Support Group. Advanced Paediatric Life Support: A Practical Approach to Emergencies. Current edition.",
        "American Academy of Pediatrics and American Heart Association. Textbook of Neonatal Resuscitation. Current edition.",
        "Royal College of Physicians. National Early Warning Score (NEWS) 2. 2017.",
        "Teasdale G, Jennett B. Assessment of coma and impaired consciousness. Lancet. 1974;2:81-84.",
        "Teasdale G, et al. The Glasgow Coma Scale at 40 years: standing the test of time. Lancet Neurol. 2014;13:844-854."
    )
)
