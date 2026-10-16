package com.medtrixa.app.ui.screens.more.topics

import com.medtrixa.app.ui.screens.more.*

internal val conversionA: List<TopicSection> = listOf(
    TopicSection("Glucocorticoid Equivalence", listOf(
        TopicBlock.Text("Approximate equivalent doses of systemic glucocorticoids. Equivalence reflects glucocorticoid effect and does not apply to topical, inhaled or intra-articular use."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("Hydrocortisone", "Short-acting", "Equivalent dose: 20 mg\nAnti-inflammatory potency: 1\nMineralocorticoid potency: 1\nDuration of action: 8 - 12 hours"),
            AbbrevRow("Cortisone", "Short-acting", "Equivalent dose: 25 mg\nAnti-inflammatory potency: 0.8\nMineralocorticoid potency: 0.8\nDuration of action: 8 - 12 hours"),
            AbbrevRow("Prednisone", "Intermediate-acting", "Equivalent dose: 5 mg\nAnti-inflammatory potency: 4\nMineralocorticoid potency: 0.8\nDuration of action: 12 - 36 hours"),
            AbbrevRow("Prednisolone", "Intermediate-acting", "Equivalent dose: 5 mg\nAnti-inflammatory potency: 4\nMineralocorticoid potency: 0.8\nDuration of action: 12 - 36 hours"),
            AbbrevRow("Methylprednisolone", "Intermediate-acting", "Equivalent dose: 4 mg\nAnti-inflammatory potency: 5\nMineralocorticoid potency: 0\nDuration of action: 12 - 36 hours"),
            AbbrevRow("Triamcinolone", "Intermediate-acting", "Equivalent dose: 4 mg\nAnti-inflammatory potency: 5\nMineralocorticoid potency: 0\nDuration of action: 12 - 36 hours"),
            AbbrevRow("Dexamethasone", "Long-acting", "Equivalent dose: 0.75 mg\nAnti-inflammatory potency: 25 - 30\nMineralocorticoid potency: 0\nDuration of action: 36 - 72 hours"),
            AbbrevRow("Betamethasone", "Long-acting", "Equivalent dose: 0.6 mg\nAnti-inflammatory potency: 25 - 30\nMineralocorticoid potency: 0\nDuration of action: 36 - 72 hours"),
            AbbrevRow("Fludrocortisone", "Mineralocorticoid", "Equivalent dose: Not used for glucocorticoid equivalence\nAnti-inflammatory potency: 10\nMineralocorticoid potency: 125\nDuration of action: 12 - 36 hours")
        )),
        TopicBlock.Bullets(listOf(
            "Reference point: Hydrocortisone 20 mg equals prednisone 5 mg and dexamethasone 0.75 mg.",
            "Worked example: Prednisone 40 mg is equivalent to dexamethasone 6 mg.",
            "Prodrugs: Prednisone and cortisone need hepatic conversion to prednisolone and hydrocortisone.",
            "Tapering: Courses longer than about 3 weeks can suppress the hypothalamic-pituitary-adrenal axis and need a gradual taper.",
            "Stress cover: Patients on long-term steroids may need stress-dose cover during acute illness or surgery."
        ))
    ), TopicAccent.VIOLET)
)
