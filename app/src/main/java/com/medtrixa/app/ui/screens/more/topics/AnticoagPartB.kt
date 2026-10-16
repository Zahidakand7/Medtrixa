package com.medtrixa.app.ui.screens.more.topics

import com.medtrixa.app.ui.screens.more.*

internal val anticoagB: List<TopicSection> = listOf(
    TopicSection("Warfarin Peri-Procedural Guidance", listOf(
        TopicBlock.Text("Warfarin is continued for minimal-bleeding-risk procedures and interrupted for most others. The schedule follows the CHEST perioperative guideline."),
        TopicBlock.Bullets(listOf(
            "Minimal bleeding risk: Continue warfarin with the INR in the therapeutic range, as for dental extractions, minor dermatologic procedures and cataract surgery.",
            "Interruption: Give the last dose 6 days before the procedure, which means 5 days without warfarin.",
            "INR check: Measure the INR the day before, and consider oral vitamin K 1 - 2 mg if it is still above 1.5.",
            "Bridging: Not routinely needed. Consider it for mechanical mitral valves, older-generation mechanical aortic valves, or a stroke, TIA or venous thromboembolism within the previous 3 months.",
            "Resuming: Restart at the usual maintenance dose 12 - 24 hours after the procedure when hemostasis is secure.",
            "High bleeding risk: Delay the restart for 48 - 72 hours."
        ))
    ), TopicAccent.VIOLET),
    TopicSection("DOAC Peri-Procedural Interruption", listOf(
        TopicBlock.Text("Intervals follow the PAUSE protocol, which guided apixaban, rivaroxaban and dabigatran without heparin bridging. Day 0 is the day of the procedure, and omitted days are full days without a dose."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("Apixaban, rivaroxaban, edoxaban", "", "Low bleeding risk: Omit for 1 day before, so the last dose is on day −2\nHigh bleeding risk: Omit for 2 days before, so the last dose is on day −3\nResume after low risk: 1 day after, at about 24 hours\nResume after high risk: 2 - 3 days after, at 48 - 72 hours", "Edoxaban follows the same intervals as apixaban in expert guidance."),
            AbbrevRow("Dabigatran", "CrCl 50 mL/min or above", "Low bleeding risk: Omit for 1 day before\nHigh bleeding risk: Omit for 2 days before\nResume after low risk: 1 day after\nResume after high risk: 2 - 3 days after"),
            AbbrevRow("Dabigatran", "CrCl 30 - 49 mL/min", "Low bleeding risk: Omit for 2 days before\nHigh bleeding risk: Omit for 4 days before\nResume after low risk: 1 day after\nResume after high risk: 2 - 3 days after"),
            AbbrevRow("Neuraxial anesthesia", "Spinal or epidural", "Follow ASRA guidance, which is typically 72 hours after the last dose of apixaban, rivaroxaban or edoxaban\nDabigatran: Allow longer, particularly in renal impairment")
        )),
        TopicBlock.Bullets(listOf(
            "High bleeding risk: Major surgery with extensive tissue injury, neurosurgery, spinal or epidural procedures, cardiac and major vascular surgery, major orthopedic, urologic, abdominal and pelvic surgery.",
            "Low bleeding risk: Dental work, cataract surgery, minor skin procedures, endoscopy without biopsy and pacemaker or defibrillator implantation.",
            "No bridging: Do not give heparin bridging with a DOAC, because DOAC effects fade and return quickly.",
            "Postoperative cover: Give prophylactic-dose anticoagulation while the full dose is delayed after high-risk procedures."
        ))
    ), TopicAccent.TEAL),
    TopicSection("Heparin Peri-Procedural Guidance", listOf(
        TopicBlock.Bullets(listOf(
            "Intravenous unfractionated heparin: Stop 4 - 6 hours before the procedure, and confirm that the aPTT is normal.",
            "Therapeutic enoxaparin: Give the last dose 24 hours before the procedure.",
            "Prophylactic enoxaparin: Give the last dose 12 hours before the procedure.",
            "Resuming after low risk: Restart therapeutic doses 24 hours after the procedure.",
            "Resuming after high risk: Restart therapeutic doses 48 - 72 hours after the procedure, and use prophylactic doses in between.",
            "Neuraxial blocks: Follow the ASRA intervals for the dose and the drug."
        ))
    ), TopicAccent.AMBER),
    TopicSection("Special Situations", listOf(
        TopicBlock.Bullets(listOf(
            "Renal impairment: Prolongs the half-life of DOACs, especially dabigatran, so extend the interruption.",
            "Emergency surgery: Delay for 12 - 24 hours after the last dose where possible, and reverse only when the need is life-threatening or the procedure cannot wait.",
            "Residual drug: A drug-specific anti-Xa level or a normal dilute thrombin time can confirm a low residual effect.",
            "Antiplatelets: Review concurrent aspirin or P2Y12 inhibitors, because stents and recent events change the plan.",
            "Pregnancy: Warfarin is avoided, and LMWH timing should be planned with the anesthesia team for delivery and neuraxial blocks.",
            "High thrombotic risk: Involve a specialist for recent venous thromboembolism, cancer or a mechanical heart valve."
        )),
        TopicBlock.Callout(
            "Disclaimer",
            "This guide is an educational reference summarizing product labeling and published guidelines, which are updated as new evidence appears. Reversal and interruption decisions depend on the patient, the procedure and local protocol, so confirm them against current prescribing information and with the responsible specialist.",
            CalloutKind.INFO
        )
    ), TopicAccent.GREEN)
)
