package com.medtrixa.app.ui.screens.more.topics

import com.medtrixa.app.ui.screens.more.*

internal val acidBaseA: List<TopicSection> = listOf(
    TopicSection("Stepwise Approach", listOf(
        TopicBlock.Text("Work through the same steps every time. Normal arterial values are listed in Lab Reference Ranges, under Arterial Blood Gas."),
        TopicBlock.Bullets(listOf(
            "1. Check the pH: Below 7.35 is acidemia and above 7.45 is alkalemia, and a normal pH can still hide a mixed disorder.",
            "2. Find the primary process: Match the direction of the pH with HCO₃⁻ and PaCO₂, using the table below.",
            "3. Check compensation: Compare the measured value with the expected value from the compensation rules.",
            "4. Calculate the anion gap: Na⁺ − (Cl⁻ + HCO₃⁻), corrected for albumin.",
            "5. Calculate the delta ratio: Use it when the anion gap is high, to uncover a second metabolic disorder.",
            "6. Assess oxygenation: Review PaO₂, SaO₂, the A-a gradient and the PaO₂/FiO₂ ratio.",
            "7. Interpret in context: Match the result to the clinical picture, and repeat the gas after treatment."
        ))
    ), TopicAccent.BLUE),
    TopicSection("Primary Acid-Base Disorders", listOf(
        TopicBlock.Text("Compensation limits the change in pH but does not fully normalize it in a simple disorder."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("Metabolic acidosis", "", "pH: Low\nHCO₃⁻: Low\nPaCO₂: Falls as compensation\nHigh anion gap causes: Lactic acidosis, ketoacidosis, uremia, toxic alcohols, salicylates\nNormal anion gap causes: Diarrhea, renal tubular acidosis, saline infusion, pancreatic fistula"),
            AbbrevRow("Metabolic alkalosis", "", "pH: High\nHCO₃⁻: High\nPaCO₂: Rises as compensation\nCauses: Vomiting, nasogastric suction, diuretics, hypokalemia, mineralocorticoid excess\nUrine chloride: Below 20 mmol/L suggests a chloride-responsive cause, and above 20 mmol/L a chloride-resistant cause"),
            AbbrevRow("Respiratory acidosis", "", "pH: Low\nPaCO₂: High\nHCO₃⁻: Rises as compensation\nCauses: Hypoventilation, COPD, opioids or sedatives, neuromuscular weakness, obesity hypoventilation, airway obstruction"),
            AbbrevRow("Respiratory alkalosis", "", "pH: High\nPaCO₂: Low\nHCO₃⁻: Falls as compensation\nCauses: Pain, anxiety, hypoxemia, sepsis, pulmonary embolism, pregnancy, salicylates, mechanical overventilation")
        ))
    ), TopicAccent.VIOLET),
    TopicSection("Compensation Rules", listOf(
        TopicBlock.Text("Expected compensation for each simple disorder. A measured value outside the expected range points to a second process."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("Metabolic acidosis", "Winter formula", "Rule: Expected PaCO₂ = 1.5 × HCO₃⁻ + 8, ± 2 mmHg\nQuick check: PaCO₂ is close to the last two digits of the pH\nExample: HCO₃⁻ 12 mmol/L gives 1.5 × 12 + 8 = 26 ± 2, or 24 - 28 mmHg\nHigher than expected: Additional respiratory acidosis\nLower than expected: Additional respiratory alkalosis\nTiming: Begins within minutes and is complete in 12 - 24 hours"),
            AbbrevRow("Metabolic alkalosis", "", "Rule: Expected PaCO₂ = 40 + 0.7 × (HCO₃⁻ − 24)\nExample: HCO₃⁻ 34 mmol/L gives 40 + 0.7 × 10 = 47 mmHg\nHigher than expected: Additional respiratory acidosis\nLower than expected: Additional respiratory alkalosis\nLimit: Compensation alone rarely raises PaCO₂ above 55 - 60 mmHg"),
            AbbrevRow("Acute respiratory acidosis", "", "Rule: HCO₃⁻ rises 1 mmol/L for every 10 mmHg rise in PaCO₂\npH: Falls about 0.08 for every 10 mmHg rise in PaCO₂\nTiming: Immediate cellular buffering"),
            AbbrevRow("Chronic respiratory acidosis", "", "Rule: HCO₃⁻ rises 3.5 mmol/L (range 3 - 4) for every 10 mmHg rise in PaCO₂\npH: Falls about 0.03 for every 10 mmHg rise in PaCO₂\nTiming: Renal compensation takes 3 - 5 days"),
            AbbrevRow("Acute respiratory alkalosis", "", "Rule: HCO₃⁻ falls 2 mmol/L for every 10 mmHg fall in PaCO₂\npH: Rises about 0.08 for every 10 mmHg fall in PaCO₂"),
            AbbrevRow("Chronic respiratory alkalosis", "", "Rule: HCO₃⁻ falls 4 - 5 mmol/L for every 10 mmHg fall in PaCO₂\npH: Rises about 0.03 for every 10 mmHg fall in PaCO₂\nTiming: Renal compensation takes 3 - 5 days")
        )),
        TopicBlock.Bullets(listOf(
            "Mixed disorder: A measured HCO₃⁻ above the expected value suggests an additional metabolic alkalosis, and below it an additional metabolic acidosis.",
            "Acute versus chronic: When the HCO₃⁻ falls between the acute and chronic expectations, consider an acute-on-chronic process."
        ))
    ), TopicAccent.AMBER)
)
