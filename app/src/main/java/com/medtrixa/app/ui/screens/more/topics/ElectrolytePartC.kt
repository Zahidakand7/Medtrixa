package com.medtrixa.app.ui.screens.more.topics

import com.medtrixa.app.ui.screens.more.*

internal val electrolyteC1: List<TopicSection> = listOf(
    TopicSection("Diagnostic Approach", listOf(
        TopicBlock.Text("Start with the pattern of the abnormal value, the volume status and the drug history, then use the targeted tests below."),
        TopicBlock.Bullets(listOf(
            "Hyponatremia, serum osmolality: Below 275 mOsm/kg confirms hypotonic hyponatremia. A high value points to hyperglycemia or mannitol, and a normal value to pseudohyponatremia.",
            "Hyponatremia, urine osmolality: Below 100 mOsm/kg suggests primary polydipsia or very low solute intake, and above 100 mOsm/kg indicates ADH activity.",
            "Hyponatremia, urine sodium: Below 30 mmol/L suggests low effective arterial volume or extrarenal loss, and above 30 mmol/L suggests SIADH, diuretics, adrenal insufficiency or renal salt loss.",
            "SIADH criteria: Euvolemia, serum osmolality below 275 mOsm/kg, urine osmolality above 100 mOsm/kg, urine Na⁺ above 30 mmol/L, and normal thyroid, adrenal and renal function.",
            "SIADH causes: CNS disease, lung disease, malignancy such as small-cell lung cancer, pain, nausea, the postoperative state and drugs.",
            "Hypernatremia, urine osmolality: Below 300 mOsm/kg with polyuria suggests diabetes insipidus, above 600 mOsm/kg suggests extrarenal water loss or poor intake, and 300 - 600 mOsm/kg suggests osmotic diuresis or partial diabetes insipidus.",
            "Hypokalemia, urine potassium: Below 15 mmol per day suggests extrarenal loss or a shift into cells, and above 15 mmol per day suggests renal loss.",
            "Hyperkalemia, exclude artifact: Check for hemolysis, a delayed sample, fist clenching, and marked leukocytosis or thrombocytosis, and repeat the sample.",
            "Hypercalcemia, PTH: A raised or inappropriately normal PTH indicates primary hyperparathyroidism, and a suppressed PTH points to malignancy, vitamin D excess or granulomatous disease.",
            "Hypocalcemia, PTH: A low PTH suggests hypoparathyroidism, and a high PTH suggests vitamin D deficiency, kidney disease or pseudohypoparathyroidism. Always check magnesium and phosphate.",
            "Hypomagnesemia, urinary loss: A fractional excretion of Mg²⁺ above 2% suggests renal loss, and below 2% suggests gastrointestinal loss or poor intake."
        ))
    ), TopicAccent.BLUE)
)

internal val electrolyteC2: List<TopicSection> = listOf(
    TopicSection("Correction Formulas and Conversions", listOf(
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("Adrogué–Madias", "Sodium change per liter of infusate", "Formula: (Infusate Na⁺ + infusate K⁺ − serum Na⁺) ÷ (total body water + 1)\nInfusate Na⁺: 3% saline 513 mmol/L, 0.9% saline 154 mmol/L, 0.45% saline 77 mmol/L, 5% dextrose 0 mmol/L\nTotal body water: Weight in kg × 0.6 in children and men, × 0.5 in women and older men, × 0.45 in older women\nExample: Na⁺ 120 mmol/L in a 70 kg man (water 42 L) gives (513 − 120) ÷ 43 = 9.1 mmol/L per liter of 3% saline, or about 0.9 mmol/L per 100 mL", "A guide only, because ongoing losses and urine output change the result. Recheck sodium frequently."),
            AbbrevRow("3% saline estimate", "Hypertonic saline", "Rule of thumb: 1 mL/kg raises Na⁺ by about 1 mmol/L\nBolus: 100 - 150 mL over 10 - 20 minutes for severe symptoms"),
            AbbrevRow("Overcorrection rescue", "Sodium rising too fast", "Step 1: Stop hypertonic saline and other treatments that raise Na⁺\nStep 2: Desmopressin 2 - 4 mcg IV or subcutaneously every 8 hours\nStep 3: 5% dextrose 3 mL/kg per hour, adjusted to bring Na⁺ back within the limit\nMonitoring: Check Na⁺ every 1 - 2 hours"),
            AbbrevRow("DKA potassium", "During insulin therapy", "K⁺ below 3.3 mmol/L: Hold insulin and replace potassium until K⁺ is above 3.3 mmol/L\nK⁺ 3.3 - 5.2 mmol/L: Add 20 - 30 mmol per liter of IV fluid, aiming for 4 - 5 mmol/L\nK⁺ above 5.2 mmol/L: Give no potassium and recheck every 2 hours"),
            AbbrevRow("Calcium products", "Elemental calcium", "Calcium gluconate 10%: 1 g (10 mL) contains 93 mg elemental calcium, or 4.65 mEq\nCalcium chloride 10%: 1 g (10 mL) contains 273 mg elemental calcium, or 13.6 mEq, and needs a central line"),
            AbbrevRow("Potassium and magnesium products", "Content per dose", "Potassium: 1 mmol equals 1 mEq, and potassium chloride 1 g contains 13.4 mmol\nMagnesium sulfate: 1 g contains about 8 mEq, or 4 mmol, of magnesium"),
            AbbrevRow("Phosphate products", "Per mL", "Potassium phosphate: 3 mmol phosphate and 4.4 mEq potassium\nSodium phosphate: 3 mmol phosphate and 4 mEq sodium")
        ))
    ), TopicAccent.VIOLET),
    TopicSection("Drug-Induced Causes", listOf(
        TopicBlock.Bullets(listOf(
            "Hyponatremia: Thiazides, SSRIs, carbamazepine, oxcarbazepine, desmopressin, cyclophosphamide, vincristine, opioids and antipsychotics.",
            "Hypokalemia: Loop and thiazide diuretics, insulin, beta-agonists, corticosteroids, amphotericin B, theophylline and laxatives.",
            "Hyperkalemia: ACE inhibitors, ARBs, spironolactone, eplerenone, amiloride, triamterene, trimethoprim, NSAIDs, heparin, calcineurin inhibitors, succinylcholine and potassium supplements.",
            "Hypocalcemia: Bisphosphonates, denosumab, loop diuretics, cinacalcet, phenytoin, foscarnet and citrate in transfusion.",
            "Hypercalcemia: Thiazides, lithium, calcium and vitamin D excess, vitamin A and teriparatide.",
            "Hypomagnesemia: Proton pump inhibitors, loop and thiazide diuretics, cisplatin, amphotericin B, aminoglycosides, cyclosporine and tacrolimus.",
            "Hypermagnesemia: Magnesium-containing laxatives and antacids, and magnesium sulfate."
        ))
    ), TopicAccent.AMBER),
    TopicSection("Pediatric Notes", listOf(
        TopicBlock.Text("Doses in children are weight-based. Verify every dose against a pediatric formulary and local protocol."),
        TopicBlock.Bullets(listOf(
            "Maintenance fluids: Isotonic fluids reduce the risk of hospital-acquired hyponatremia in children.",
            "Hyponatremia with severe symptoms: 3% saline 2 - 5 mL/kg IV over 10 - 20 minutes, per local pediatric protocol.",
            "Hyperkalemia with ECG changes: Calcium gluconate 10%, 0.5 - 1 mL/kg (50 - 100 mg/kg) IV over 5 - 10 minutes.",
            "Hyperkalemia, shifting potassium: Regular insulin 0.1 units/kg with glucose 0.5 g/kg, and monitor glucose.",
            "Hypokalemia, IV replacement: Up to 0.5 mmol/kg per hour with continuous ECG monitoring.",
            "Hypocalcemia: Calcium gluconate 10%, 1 mL/kg (100 mg/kg) IV over 10 - 30 minutes with ECG monitoring.",
            "Hypomagnesemia: Magnesium sulfate 25 - 50 mg/kg IV over 20 - 30 minutes, to a maximum of 2 g.",
            "Normal values: See the Child lines in Lab Reference Ranges for age-specific intervals."
        ))
    ), TopicAccent.TEAL)
)
