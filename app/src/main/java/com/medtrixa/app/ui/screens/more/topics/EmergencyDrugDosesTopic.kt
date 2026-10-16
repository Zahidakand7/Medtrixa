package com.medtrixa.app.ui.screens.more.topics

import com.medtrixa.app.ui.screens.more.*

val EmergencyDrugDosesTopic = MoreTopic(
    id = "emergency_drug_doses",
    title = "Emergency Drug Doses",
    subtitle = "ACLS drug summary (AHA 2025)",
    badge = "ACLS",
    accent = TopicAccent.RED,
    category = "Emergency",
    keywords = listOf("acls", "pals", "emergency", "cardiac arrest", "resuscitation", "epinephrine", "amiodarone", "lidocaine", "atropine", "adenosine", "vasopressin", "dopamine", "procainamide", "diltiazem", "metoprolol", "esmolol", "magnesium", "calcium", "bicarbonate", "naloxone", "lipid", "defibrillation", "cardioversion", "bradycardia", "tachycardia", "vf", "pvt", "pea", "asystole", "torsades"),
    sections = edSectionsA + edSectionsB + edSectionsC,
    references = listOf(
        "Part 9: Adult Advanced Life Support. 2025 American Heart Association Guidelines for Cardiopulmonary Resuscitation and Emergency Cardiovascular Care. Circulation. 2025;152(suppl 2).",
        "American Heart Association. 2025 Adult Cardiac Arrest, Bradycardia With a Pulse, and Tachyarrhythmia With a Pulse Algorithms; 2025 Pediatric Cardiac Arrest Algorithm. Advanced Cardiovascular Life Support and Pediatric Advanced Life Support.",
        "Part 10: Adult and Pediatric Special Circumstances of Resuscitation. 2025 American Heart Association Guidelines for Cardiopulmonary Resuscitation and Emergency Cardiovascular Care. Circulation. 2025;152(suppl 2):S578-S672.",
        "Australian and New Zealand Committee on Resuscitation. Guideline 11.5 - Medications in Adult Cardiac Arrest."
    )
)
