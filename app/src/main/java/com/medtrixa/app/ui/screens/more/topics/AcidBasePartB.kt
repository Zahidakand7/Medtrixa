package com.medtrixa.app.ui.screens.more.topics

import com.medtrixa.app.ui.screens.more.*

internal val acidBaseB: List<TopicSection> = listOf(
    TopicSection("Anion Gap and Delta Ratio", listOf(
        TopicBlock.Bullets(listOf(
            "Anion gap: Na⁺ − (Cl⁻ + HCO₃⁻). Normal is 8 - 12 mmol/L when K⁺ is not included.",
            "Albumin correction: Add 2.5 mmol/L for every 1 g/dL that albumin is below 4.0 g/dL.",
            "Delta ratio: (Anion gap − 12) ÷ (24 − HCO₃⁻).",
            "Below 0.4: A normal anion gap (hyperchloremic) acidosis.",
            "0.4 - 0.8: A mixed high and normal anion gap acidosis.",
            "0.8 - 2.0: An uncomplicated high anion gap acidosis.",
            "Above 2.0: A high anion gap acidosis with a concurrent metabolic alkalosis.",
            "Urine anion gap: Urine Na⁺ + K⁺ − Cl⁻ in normal gap acidosis. A negative value suggests gastrointestinal bicarbonate loss, and a positive value a renal tubular cause.",
            "Osmolal gap: Measured minus calculated osmolality. A gap above 10 mOsm/kg suggests toxic alcohols. Calculated osmolality = 2 × Na⁺ + glucose ÷ 18 + BUN ÷ 2.8, with glucose and BUN in mg/dL."
        ))
    ), TopicAccent.TEAL),
    TopicSection("Oxygenation Assessment", listOf(
        TopicBlock.Text("Oxygenation is judged separately from acid-base status. The Berlin ARDS categories apply with PEEP of 5 cmH₂O or more."),
        TopicBlock.Bullets(listOf(
            "A-a gradient: [FiO₂ × (barometric pressure − 47) − PaCO₂ ÷ 0.8] − PaO₂. On room air at sea level this is 150 − PaCO₂ ÷ 0.8 − PaO₂.",
            "Normal A-a gradient: Up to about (age ÷ 4) + 4 mmHg on room air.",
            "Raised A-a gradient: V/Q mismatch, shunt or diffusion impairment.",
            "Normal A-a gradient: With a low PaO₂ this indicates hypoventilation or low inspired oxygen, including altitude.",
            "PaO₂/FiO₂ ratio: Normal is above 400 mmHg.",
            "Mild ARDS: PaO₂/FiO₂ above 200 and up to 300 mmHg.",
            "Moderate ARDS: PaO₂/FiO₂ above 100 and up to 200 mmHg.",
            "Severe ARDS: PaO₂/FiO₂ of 100 mmHg or below.",
            "Poor response to oxygen: Suggests a shunt."
        ))
    ), TopicAccent.ROSE),
    TopicSection("Pitfalls and Sampling", listOf(
        TopicBlock.Bullets(listOf(
            "Venous samples: Venous pH is about 0.03 lower and PvCO₂ about 4 - 6 mmHg higher than arterial values, so venous gases screen for acid-base disorders but not for oxygenation.",
            "Air bubbles: They move PaO₂ toward 150 mmHg and lower PaCO₂, so expel air and cap the syringe.",
            "Delay: Cells keep consuming oxygen and producing acid, so analyze within 15 - 30 minutes or keep the sample on ice.",
            "Temperature: Analyzers report values at 37°C, and the difference is small for most patients.",
            "Mixed disorders: A normal pH with abnormal PaCO₂ and HCO₃⁻ suggests opposing disorders.",
            "Pregnancy: PaCO₂ normally falls to about 28 - 32 mmHg.",
            "Pulse oximetry: SpO₂ can read falsely normal with carboxyhemoglobin or methemoglobin, so use co-oximetry."
        ))
    ), TopicAccent.RED),
    TopicSection("Worked Examples", listOf(
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("Example 1", "Metabolic acidosis with a high anion gap", "Values: pH 7.25, PaCO₂ 24 mmHg, HCO₃⁻ 10 mmol/L, Na⁺ 140 mmol/L, Cl⁻ 100 mmol/L\nExpected PaCO₂: 1.5 × 10 + 8 = 23 ± 2 mmHg, so compensation is appropriate\nAnion gap: 140 − (100 + 10) = 30 mmol/L\nDelta ratio: (30 − 12) ÷ (24 − 10) = 1.3, an uncomplicated high anion gap acidosis"),
            AbbrevRow("Example 2", "Metabolic alkalosis", "Values: pH 7.50, PaCO₂ 48 mmHg, HCO₃⁻ 36 mmol/L\nExpected PaCO₂: 40 + 0.7 × (36 − 24) = 48 mmHg, so compensation is appropriate"),
            AbbrevRow("Example 3", "Acute respiratory acidosis", "Values: pH 7.28, PaCO₂ 60 mmHg, HCO₃⁻ 27 mmol/L\nPaCO₂ rise: 20 mmHg above 40\nExpected HCO₃⁻ if acute: 24 + 2 = 26 mmol/L, which matches\nExpected HCO₃⁻ if chronic: 24 + 7 = 31 mmol/L, which does not match"),
            AbbrevRow("Example 4", "Acute respiratory alkalosis", "Values: pH 7.48, PaCO₂ 30 mmHg, HCO₃⁻ 22 mmol/L\nPaCO₂ fall: 10 mmHg below 40\nExpected HCO₃⁻ if acute: 24 − 2 = 22 mmol/L, which matches")
        )),
        TopicBlock.Callout(
            "Disclaimer",
            "This guide is an educational reference. Compensation rules are empirical approximations from published studies, and individual patients vary. Interpret every gas together with the clinical picture and the reporting laboratory's ranges, and follow institutional policy.",
            CalloutKind.INFO
        )
    ), TopicAccent.GREEN)
)
