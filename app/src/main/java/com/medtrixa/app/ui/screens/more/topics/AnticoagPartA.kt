package com.medtrixa.app.ui.screens.more.topics

import com.medtrixa.app.ui.screens.more.*

internal val anticoagA: List<TopicSection> = listOf(
    TopicSection("Anticoagulant Overview", listOf(
        TopicBlock.Text("Mechanism, monitoring and half-life guide the choice of reversal and the timing of interruption. Half-lives assume normal renal function."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("Warfarin", "Vitamin K antagonist", "Monitoring: INR\nHalf-life: About 40 hours, with the effect lasting several days\nReversal: Vitamin K and 4-factor PCC"),
            AbbrevRow("Unfractionated heparin", "Potentiates antithrombin", "Monitoring: aPTT or anti-Xa\nHalf-life: About 60 - 90 minutes, and dose-dependent\nReversal: Protamine"),
            AbbrevRow("Enoxaparin, dalteparin", "Low molecular weight heparin", "Monitoring: Anti-Xa in renal impairment, pregnancy and extremes of weight\nHalf-life: About 4.5 - 7 hours, and longer in renal impairment\nReversal: Protamine, with partial effect"),
            AbbrevRow("Fondaparinux", "Indirect factor Xa inhibitor", "Monitoring: Fondaparinux-calibrated anti-Xa, rarely needed\nHalf-life: About 17 - 21 hours\nReversal: No specific agent"),
            AbbrevRow("Dabigatran", "Direct thrombin inhibitor", "Monitoring: Dilute thrombin time or ecarin clotting time\nHalf-life: About 12 - 17 hours, and longer in renal impairment\nRenal elimination: About 80%\nReversal: Idarucizumab, with hemodialysis as an adjunct"),
            AbbrevRow("Apixaban", "Direct factor Xa inhibitor", "Monitoring: Apixaban-calibrated anti-Xa, if needed\nHalf-life: About 12 hours\nRenal elimination: About 27%\nReversal: 4-factor PCC"),
            AbbrevRow("Rivaroxaban", "Direct factor Xa inhibitor", "Monitoring: Rivaroxaban-calibrated anti-Xa, if needed\nHalf-life: 5 - 9 hours in young adults and 11 - 13 hours in older adults\nRenal elimination: About 35% unchanged\nReversal: 4-factor PCC"),
            AbbrevRow("Edoxaban", "Direct factor Xa inhibitor", "Monitoring: Edoxaban-calibrated anti-Xa, if needed\nHalf-life: About 10 - 14 hours\nRenal elimination: About 50%\nReversal: 4-factor PCC")
        ))
    ), TopicAccent.BLUE),
    TopicSection("Reversal Agents", listOf(
        TopicBlock.Text("Reserve reversal for life-threatening or uncontrolled bleeding, or an emergency procedure that cannot wait. Doses follow product labeling and guideline recommendations, and local protocols take precedence."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("Vitamin K (phytonadione)", "Warfarin", "Major bleeding: 5 - 10 mg IV by slow infusion, together with 4-factor PCC\nINR above 10 without bleeding: 2.5 - 5 mg orally\nINR 4.5 - 10 without bleeding: Routine vitamin K is not suggested\nOnset: INR begins to fall within 6 - 12 hours", "Give IV doses slowly, at a rate not above 1 mg per minute, because of the risk of anaphylactoid reactions."),
            AbbrevRow("4-factor PCC", "Warfarin", "INR 2 to below 4: 25 units/kg, maximum 2,500 units\nINR 4 - 6: 35 units/kg, maximum 3,500 units\nINR above 6: 50 units/kg, maximum 5,000 units\nDosing weight: Capped at 100 kg\nPair with: Vitamin K, because the PCC effect is shorter than that of warfarin", "Carries a boxed warning for arterial and venous thromboembolic events."),
            AbbrevRow("4-factor PCC", "Factor Xa inhibitors, off-label", "Dose: 25 - 50 units/kg, or a fixed dose of 2,000 units, per local protocol\nAlternative: Activated PCC 50 units/kg when 4-factor PCC is unavailable\nUse: Life-threatening bleeding with apixaban, rivaroxaban or edoxaban"),
            AbbrevRow("Idarucizumab", "Dabigatran", "Use: Life-threatening bleeding or an emergency procedure\nDose: 5 g IV, given as two consecutive 2.5 g infusions or boluses no more than 15 minutes apart\nOnset: Reversal is immediate\nRestart: Dabigatran can be restarted 24 hours after reversal"),
            AbbrevRow("Protamine sulfate", "Heparins", "Unfractionated heparin: 1 mg per 100 units given in the previous 2 - 3 hours, maximum 50 mg\nEnoxaparin within 8 hours: 1 mg per 1 mg of enoxaparin, maximum 50 mg\nEnoxaparin 8 - 12 hours: 0.5 mg per 1 mg of enoxaparin\nBeyond 12 hours: Usually not required\nAdministration: Slow IV injection over at least 10 minutes", "Reverses only part of the anti-Xa activity of low molecular weight heparin. Watch for hypotension, bradycardia and anaphylaxis."),
            AbbrevRow("Andexanet alfa", "Withdrawn in the US", "Status: Withdrawn from the US market in December 2025\nReason: In ANNEXA-I, thrombotic events were more frequent than with usual care, which was mostly 4-factor PCC\nAlternative: 4-factor PCC for apixaban, rivaroxaban and edoxaban\nElsewhere: Availability and licensing vary by country, so follow local policy"),
            AbbrevRow("Adjuncts", "", "Activated charcoal: 50 g if the last DOAC dose was within about 2 - 4 hours and the airway is protected\nHemodialysis: Removes dabigatran, and helps in severe bleeding with renal failure\nPlasma: Does not reverse DOACs and is not recommended for that purpose\nVitamin K: Has no effect on DOACs, heparins or fondaparinux")
        ))
    ), TopicAccent.RED),
    TopicSection("Managing Major Bleeding", listOf(
        TopicBlock.Bullets(listOf(
            "Stop the anticoagulant: Record the drug, the dose and the time of the last dose.",
            "Control the source: Apply direct pressure, and use endoscopic, radiologic or surgical hemostasis as needed.",
            "Support the patient: Give fluids and blood products as required, and avoid hypothermia and acidosis.",
            "Check renal function: Reduced clearance prolongs the effect of dabigatran in particular.",
            "Send levels: Obtain a drug-specific level or anti-Xa where available, without delaying reversal.",
            "Reverse when indicated: Choose the agent by the drug, as in Reversal Agents above.",
            "Restart decision: Individualize the timing from the bleeding source, the thrombotic risk and the secure hemostasis."
        ))
    ), TopicAccent.ROSE),
    TopicSection("Supratherapeutic INR Without Bleeding", listOf(
        TopicBlock.Bullets(listOf(
            "INR above range up to 4.5: Lower or omit one dose, and recheck the INR.",
            "INR 4.5 - 10 without bleeding: Omit 1 - 2 doses, and avoid routine vitamin K.",
            "INR above 10 without bleeding: Give oral vitamin K 2.5 - 5 mg, and recheck within 24 hours.",
            "Any INR with major bleeding: Give vitamin K and 4-factor PCC, as in Reversal Agents."
        ))
    ), TopicAccent.AMBER)
)
