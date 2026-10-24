package com.medtrixa.app.ui.screens.more.topics

import com.medtrixa.app.ui.screens.more.*

val AnticoagTopic = MoreTopic(
    id = "anticoagulation_reversal",
    title = "Anticoagulation",
    subtitle = "Reversal agents and peri-procedural holding guidance",
    badge = "AC",
    accent = TopicAccent.RED,
    category = "Medications",
    keywords = listOf("anticoagulation", "anticoagulant", "reversal", "warfarin", "heparin", "enoxaparin", "apixaban", "rivaroxaban", "dabigatran", "edoxaban", "doac", "vitamin k", "pcc", "kcentra", "idarucizumab", "protamine", "bridging", "pause", "peri-procedural", "perioperative"),
    sections = anticoagA + anticoagB,
    references = listOf(
        "Douketis JD, et al. Perioperative Management of Antithrombotic Therapy: An American College of Chest Physicians Clinical Practice Guideline. Chest. 2022;162:e207-e243.",
        "Douketis JD, et al. Perioperative Management of Patients With Atrial Fibrillation Receiving a Direct Oral Anticoagulant (PAUSE). JAMA Intern Med. 2019;179:1469-1478.",
        "Tomaselli GF, et al. 2020 ACC Expert Consensus Decision Pathway on Management of Bleeding in Patients on Oral Anticoagulants. J Am Coll Cardiol. 2020;76:594-622.",
        "Horlocker TT, et al. Regional Anesthesia in the Patient Receiving Antithrombotic or Thrombolytic Therapy: ASRA Evidence-Based Guidelines (Fourth Edition). Reg Anesth Pain Med. 2018;43:263-309.",
        "FDA. Update on the Safety of Andexxa (andexanet alfa). 2025.",
        "Prescribing information: Kcentra, Praxbind and protamine sulfate. Current editions."
    )
)
