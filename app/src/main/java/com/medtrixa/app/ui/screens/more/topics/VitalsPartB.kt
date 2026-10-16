package com.medtrixa.app.ui.screens.more.topics

import com.medtrixa.app.ui.screens.more.*

internal val vitalsB: List<TopicSection> = listOf(
    TopicSection("Glasgow Coma Scale", listOf(
        TopicBlock.Text("The Glasgow Coma Scale (GCS) rates the best response in three areas. The total ranges from 3 to 15, and each component should be recorded alongside the total."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("Eye Opening (E)", "", "4: Spontaneously\n3: To sound\n2: To pressure\n1: None"),
            AbbrevRow("Verbal Response (V)", "", "5: Oriented\n4: Confused\n3: Words only\n2: Sounds only\n1: None"),
            AbbrevRow("Motor Response (M)", "", "6: Obeys commands\n5: Localizes to pressure\n4: Normal flexion (withdrawal)\n3: Abnormal flexion (decorticate)\n2: Extension (decerebrate)\n1: None")
        ))
    ), TopicAccent.VIOLET),
    TopicSection("Pediatric Glasgow Coma Scale", listOf(
        TopicBlock.Text("The modified scale for infants and pre-verbal children changes the verbal and motor descriptors. Eye opening is scored as in adults."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("Verbal Response (V)", "Infants and pre-verbal children", "5: Coos, babbles or uses appropriate words\n4: Irritable, cries but consolable\n3: Cries to pain, inconsistently consolable\n2: Moans to pain, inconsolable\n1: None"),
            AbbrevRow("Motor Response (M)", "Infants", "6: Moves spontaneously and purposefully\n5: Withdraws from touch\n4: Withdraws from pain\n3: Abnormal flexion\n2: Abnormal extension\n1: None")
        ))
    ), TopicAccent.AMBER),
    TopicSection("GCS Interpretation", listOf(
        TopicBlock.Bullets(listOf(
            "Mild: A total of 13 - 15.",
            "Moderate: A total of 9 - 12.",
            "Severe: A total of 8 or less, which signals the need to assess airway protection.",
            "Recording: Report components, for example E3 V4 M5 = 12, because the same total can reflect different deficits.",
            "Best response: Score the best motor response from either side, and the best verbal response the patient can give.",
            "Untestable components: Record that a component could not be tested, for example eyes swollen shut or the patient intubated, and do not score it as 1.",
            "GCS-P: Subtract the pupil reactivity score from the total: 2 if neither pupil reacts, 1 if one pupil reacts, 0 if both react.",
            "Trend: Repeat the scale at regular intervals, because a fall of 2 points or more needs urgent reassessment."
        )),
        TopicBlock.Callout(
            "Disclaimer",
            "This guide is an educational reference. Vital sign norms differ between sources, populations and measurement methods, and the pediatric values and age-based formulas are approximations. Use age-specific charts and institutional policy, and rely on trained assessment when scoring the Glasgow Coma Scale.",
            CalloutKind.INFO
        )
    ), TopicAccent.ROSE)
)
