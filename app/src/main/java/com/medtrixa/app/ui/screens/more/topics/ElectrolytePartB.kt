package com.medtrixa.app.ui.screens.more.topics

import com.medtrixa.app.ui.screens.more.*

internal val electrolyteB: List<TopicSection> = listOf(
    TopicSection("Calcium Disorders", listOf(
        TopicBlock.Text("Interpret total calcium with albumin. Corrected calcium = measured Ca²⁺ + 0.8 × (4.0 − albumin in g/dL). Ionized calcium is the most reliable measure."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("Hypocalcemia", "Corrected Ca²⁺ below 8.5 mg/dL (2.12 mmol/L)", "Ionized calcium: Below 4.6 mg/dL (1.15 mmol/L)\nCauses: Hypoparathyroidism, vitamin D deficiency, chronic kidney disease, hypomagnesemia, pancreatitis, massive transfusion, bisphosphonates and denosumab\nFeatures: Perioral numbness, paresthesia, muscle cramps, Chvostek and Trousseau signs, laryngospasm, seizures and QT prolongation\nSymptomatic or severe: Calcium gluconate 1 - 2 g IV over 10 - 20 minutes with ECG monitoring, then an infusion if needed\nCaution: Do not mix calcium with bicarbonate or phosphate, and take extra care with digoxin\nAlways: Correct low Mg²⁺\nChronic: Oral calcium with vitamin D, or active vitamin D when parathyroid hormone is deficient"),
            AbbrevRow("Hypercalcemia", "Corrected Ca²⁺ above 10.5 mg/dL (2.62 mmol/L)", "Mild: 10.5 - 11.9 mg/dL (2.62 - 2.99 mmol/L)\nModerate: 12.0 - 13.9 mg/dL (3.00 - 3.49 mmol/L)\nSevere: 14.0 mg/dL (3.50 mmol/L) or higher\nCauses: Primary hyperparathyroidism and malignancy account for most cases, and also vitamin D excess, thiazides, lithium, granulomatous disease and immobilization\nFeatures: Polyuria, dehydration, constipation, nausea, confusion, short QT interval and kidney stones\nTreatment, first: Isotonic saline to restore volume, using loop diuretics only for volume overload\nTreatment, second: Zoledronic acid 4 mg IV over at least 15 minutes after hydration, in malignancy or severe cases\nTreatment, rapid effect: Calcitonin 4 units/kg subcutaneously or intramuscularly every 12 hours\nOther: Glucocorticoids for granulomatous disease and vitamin D excess, denosumab if refractory or in renal failure, and dialysis in severe disease")
        ))
    ), TopicAccent.TEAL),
    TopicSection("Magnesium and Phosphate Disorders", listOf(
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("Hypomagnesemia", "Mg²⁺ below 1.7 mg/dL (0.70 mmol/L)", "Causes: Diuretics, proton pump inhibitors, alcohol use disorder, diarrhea, poor intake, and amphotericin, cisplatin or aminoglycosides\nFeatures: Tremor, tetany, seizures, QT prolongation and torsades de pointes, with refractory hypokalemia and hypocalcemia\nTorsades de pointes: Magnesium sulfate 2 g IV over 5 - 20 minutes\nOther replacement: Magnesium sulfate 1 - 2 g IV over 1 hour, repeated as needed, with doses reduced by about 50% in renal impairment\nOral: Magnesium salts, with diarrhea limiting the dose"),
            AbbrevRow("Hypermagnesemia", "Mg²⁺ above 2.2 mg/dL (0.90 mmol/L)", "Causes: Kidney failure, magnesium sulfate therapy in pre-eclampsia, and magnesium-containing antacids or laxatives\nFeatures: Nausea, weakness, loss of deep tendon reflexes, hypotension, bradycardia, respiratory depression and cardiac arrest\nTreatment: Stop all magnesium, and give calcium gluconate 1 g IV to antagonize cardiac and neuromuscular effects\nRemoval: IV fluids with a loop diuretic when renal function allows, and dialysis in severe cases"),
            AbbrevRow("Hypophosphatemia", "PO₄³⁻ below 2.5 mg/dL (0.81 mmol/L)", "Mild to moderate: 1.0 - 2.4 mg/dL (0.32 - 0.77 mmol/L)\nSevere: Below 1.0 mg/dL (0.32 mmol/L)\nCauses: Refeeding syndrome, alcohol use disorder, treatment of diabetic ketoacidosis, respiratory alkalosis, vitamin D deficiency and phosphate binders\nFeatures: Weakness, respiratory failure, rhabdomyolysis, hemolysis and heart failure\nMild to moderate: Oral phosphate, with diarrhea as a limiting effect\nSevere or symptomatic: IV phosphate 15 - 30 mmol over 4 - 6 hours, with a lower dose in renal impairment\nMonitoring: Check Ca²⁺, K⁺ and Mg²⁺, because IV phosphate can lower calcium"),
            AbbrevRow("Hyperphosphatemia", "PO₄³⁻ above 4.5 mg/dL (1.45 mmol/L)", "Causes: Chronic kidney disease, hypoparathyroidism, tumor lysis, rhabdomyolysis, and phosphate-containing enemas or laxatives\nFeatures: Often silent, with hypocalcemia and, over time, vascular and soft tissue calcification\nTreatment: Treat the cause, restrict dietary phosphate, and give phosphate binders with meals in chronic kidney disease\nAcute and severe: Hydration, and dialysis when needed")
        ))
    ), TopicAccent.VIOLET),
    TopicSection("ECG Changes", listOf(
        TopicBlock.Bullets(listOf(
            "Hypokalemia: Flattened T waves, U waves, ST depression and arrhythmias.",
            "Hyperkalemia: Peaked T waves, loss of P waves, wide QRS, and a sine-wave pattern before cardiac arrest.",
            "Hypocalcemia: Prolonged QT interval.",
            "Hypercalcemia: Shortened QT interval, and in severe cases bradycardia and heart block.",
            "Hypomagnesemia: Prolonged QT interval and torsades de pointes.",
            "Hypermagnesemia: Prolonged PR and QRS intervals, then heart block."
        ))
    ), TopicAccent.RED),
    TopicSection("Safety Principles", listOf(
        TopicBlock.Bullets(listOf(
            "Concentrated electrolytes: Concentrated potassium chloride, potassium phosphate, hypertonic sodium chloride and magnesium sulfate are high-alert products, so use premixed solutions and independent double checks.",
            "Check magnesium: Low Mg²⁺ makes hypokalemia and hypocalcemia refractory.",
            "Mind the rate: Cap IV potassium and phosphate rates, and use cardiac monitoring for rapid or central infusions.",
            "Renal function: Reduce replacement doses in kidney impairment, and avoid potassium and magnesium excess.",
            "Avoid overcorrection: Check sodium frequently during correction, and be ready to relower if the limit is exceeded.",
            "Recheck: Repeat levels after treatment, because shifts and ongoing losses change needs.",
            "Verify unusual values: Rule out hemolysis, delayed processing and sampling from a line with infusion running before treating."
        )),
        TopicBlock.Callout(
            "Disclaimer",
            "This guide is an educational reference summarizing published guidelines and expert recommendations, which differ between sources and are updated over time. Treatment depends on the patient, the cause, the rate of change and local protocol, so confirm doses and monitoring against current guidelines and institutional policy.",
            CalloutKind.INFO
        )
    ), TopicAccent.GREEN)
)
