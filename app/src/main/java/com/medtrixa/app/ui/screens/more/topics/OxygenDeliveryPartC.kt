package com.medtrixa.app.ui.screens.more.topics

import com.medtrixa.app.ui.screens.more.*

internal val o2SectionsC: List<TopicSection> = listOf(
    TopicSection("Targets and Choosing a Device", listOf(
        TopicBlock.Text("Oxygen is a drug. Prescribe a target, monitor, and adjust the device to keep the patient in range."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("Most adults", "Target SpO₂", "94 - 98% for most acutely ill adults.\n> Start with a nasal cannula, or a mask if more oxygen is needed\n> Critically ill: start with a non-rebreather at 15 L/min, then reduce once stable"),
            AbbrevRow("CO₂ risk", "At risk of hypercapnic failure", "Target SpO₂ 88 - 92%.\n> Examples: COPD, obesity hypoventilation, neuromuscular disease, chest wall deformity\n> Start with a 24% Venturi at 2 - 3 L/min, or 28% at 4 L/min, or a nasal cannula at 1 - 2 L/min\n> Check an arterial blood gas, and adjust the target if PaCO₂ and pH are normal", "Never withhold oxygen from a hypoxic patient. Titrate and monitor."),
            AbbrevRow("CO poisoning", "Carbon monoxide", "High-flow 100% oxygen by non-rebreather at 15 L/min, or by ventilator.\n> SpO₂ reads falsely normal, so do not use it as the target\n> Consider hyperbaric oxygen and specialist advice"),
            AbbrevRow("Pregnancy", "Pregnant patients", "Target SpO₂ 94 - 98%.\n> Avoid maternal hypoxemia, because the fetus is very sensitive to it"),
            AbbrevRow("After arrest", "Return of circulation", "Use 100% oxygen during resuscitation.\n> After return of circulation, titrate to avoid both hypoxemia and hyperoxia, at about 92 - 98%"),
            AbbrevRow("Escalation", "Device ladder", "Nasal cannula 1 - 6 L/min\n> Simple mask 5 - 10 L/min\n> Venturi mask when a precise FiO₂ is needed\n> Non-rebreather 10 - 15 L/min\n> High-flow nasal cannula\n> CPAP or BiPAP\n> Intubation and ventilation")
        ))
    ), TopicAccent.GREEN),
    TopicSection("Calculations", listOf(
        TopicBlock.Text("Quick formulas for planning and monitoring."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("Cylinder time", "Minutes of oxygen left", "Minutes = (gauge pressure in psi × cylinder factor) divided by flow in L/min.\n> Factors: D 0.16, E 0.28, M 1.56, G 2.41, H or K 3.14\n> Example: a full E cylinder at 2000 psi on 2 L/min lasts 2000 × 0.28 ÷ 2 = 280 minutes\n> Keep a reserve, such as 500 psi, for transport", "Check the cylinder gauge and the factor for the cylinder you have."),
            AbbrevRow("NC FiO₂", "Nasal cannula estimate", "FiO₂ = 21% + (4% × flow in L/min).\n> Example: 3 L/min gives about 33%"),
            AbbrevRow("P/F ratio", "PaO₂ divided by FiO₂", "Use FiO₂ as a fraction, such as 0.4.\n> Normal is above 400\n> ARDS, with PEEP of 5 or more: mild 200 - 300, moderate 100 - 200, severe 100 or below", "Example: PaO₂ 80 on FiO₂ 0.5 gives a P/F ratio of 160."),
            AbbrevRow("ROX", "HFNC index", "ROX = (SpO₂ / FiO₂) divided by respiratory rate.\n> See High-Flow and Advanced for the thresholds")
        ))
    ), TopicAccent.BLUE),
    TopicSection("Safety and Troubleshooting", listOf(
        TopicBlock.Bullets(listOf(
            "Oxygen supports burning. Keep it away from open flames, smoking and heat sources, and never use oil or grease on oxygen connections.",
            "Document the device, flow, FiO₂ and the SpO₂ target with every set of observations.",
            "If the non-rebreather bag collapses on inspiration, raise the flow and check the mask seal.",
            "Do not turn down the flow on a Venturi adaptor, because that changes the FiO₂.",
            "Humidify when using high flows or for long-term use, and always with high-flow nasal cannula and tracheostomy circuits.",
            "Check the skin behind the ears and over the nose and cheeks for pressure from straps and masks.",
            "Watch for CO₂ retention: drowsiness, headache, flapping tremor and a rising PaCO₂.",
            "Prolonged FiO₂ above 60% can injure the lungs and cause absorption atelectasis, so use the lowest FiO₂ that meets the target.",
            "A rising oxygen need, or a falling SpO₂ despite oxygen, means reassess the patient and escalate."
        )),
        TopicBlock.Callout(
            "Scope",
            "Adult values follow general guidance. Pediatric and neonatal flow limits, and device specifications, vary by manufacturer and unit protocol, so confirm against the product label and local policy.",
            CalloutKind.INFO
        )
    ), TopicAccent.RED)
)
