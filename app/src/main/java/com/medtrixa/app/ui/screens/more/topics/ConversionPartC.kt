package com.medtrixa.app.ui.screens.more.topics

import com.medtrixa.app.ui.screens.more.*

internal val conversionC: List<TopicSection> = listOf(
    TopicSection("Insulin Time-Action Profiles", listOf(
        TopicBlock.Text("Typical profiles after subcutaneous injection. Onset, peak and duration vary with dose, injection site, exercise, temperature and renal function."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("Rapid-acting", "Lispro, aspart, glulisine", "Role: Mealtime and correction dosing\nOnset: 10 - 30 minutes\nPeak: 1 - 3 hours\nDuration: 3 - 5 hours"),
            AbbrevRow("Ultra-rapid-acting", "Faster aspart, lispro-aabc", "Role: Mealtime dosing with a slightly earlier onset\nOnset: About 15 minutes\nPeak: 1 - 3 hours\nDuration: 3 - 5 hours"),
            AbbrevRow("Short-acting", "Regular human insulin", "Role: Mealtime dosing, and IV use in hospital\nOnset: 30 - 60 minutes\nPeak: 2 - 4 hours\nDuration: 5 - 8 hours"),
            AbbrevRow("Intermediate-acting", "NPH insulin", "Role: Basal or twice-daily coverage\nOnset: 1 - 2 hours\nPeak: 4 - 12 hours\nDuration: 12 - 18 hours"),
            AbbrevRow("Long-acting", "Insulin glargine U-100", "Role: Basal coverage\nOnset: 1 - 2 hours\nPeak: No pronounced peak\nDuration: 20 - 24 hours"),
            AbbrevRow("Long-acting", "Insulin detemir", "Role: Basal coverage\nOnset: 1 - 3 hours\nPeak: Relatively flat, with a mild peak at 3 - 9 hours\nDuration: 6 - 24 hours, dose-dependent"),
            AbbrevRow("Ultra-long-acting", "Insulin degludec", "Role: Basal coverage\nOnset: About 1 hour\nPeak: No pronounced peak\nDuration: Over 42 hours"),
            AbbrevRow("Ultra-long-acting", "Insulin glargine U-300", "Role: Basal coverage\nOnset: About 6 hours\nPeak: No pronounced peak\nDuration: Up to 36 hours"),
            AbbrevRow("Premixed", "NPH and regular 70/30", "Role: Fixed-ratio basal and mealtime coverage\nOnset: 30 - 60 minutes\nPeak: Dual, at 2 - 12 hours\nDuration: 10 - 16 hours"),
            AbbrevRow("Premixed", "Protamine analog and rapid-acting analog", "Role: Fixed-ratio basal and mealtime coverage\nOnset: 10 - 20 minutes\nPeak: Dual, at 1 - 4 hours\nDuration: 10 - 16 hours"),
            AbbrevRow("Concentrated", "Regular insulin U-500", "Role: Severe insulin resistance\nOnset: About 30 minutes\nPeak: 4 - 8 hours\nDuration: Up to 24 hours", "Use only the dedicated U-500 pen or syringe, with doses written in units.")
        ))
    ), TopicAccent.TEAL),
    TopicSection("Insulin Safety Points", listOf(
        TopicBlock.Bullets(listOf(
            "Units: Write \"units\" in full and never \"U\", which can be misread as zero.",
            "Concentration: Check U-100 versus U-200, U-300 and U-500 before drawing up or dialing a dose.",
            "IV infusion: Use regular human insulin as defined by local protocol.",
            "Mixing: Do not mix insulin glargine or detemir with other insulins in the same syringe.",
            "Timing: Give rapid-acting insulin up to 15 minutes before a meal or just after starting it, and regular insulin about 30 minutes before.",
            "Hypoglycemia risk: Peak action and long duration set the highest-risk windows, so match monitoring to the insulin profile.",
            "Pens: Never use one pen for more than one patient."
        )),
        TopicBlock.Callout(
            "Disclaimer",
            "This guide is an educational reference with approximate values from product labeling and published guidelines. Always confirm doses against current product information, institutional protocols and the patient's clinical status.",
            CalloutKind.INFO
        )
    ), TopicAccent.BLUE)
)
