package com.medtrixa.app.ui.screens.more.topics

import com.medtrixa.app.ui.screens.more.*

internal val beersSectionsC: List<TopicSection> = listOf(
    TopicSection("Use with Caution", listOf(
        TopicBlock.Text("These drugs raise concern but not to the level of avoid, because evidence is limited, the harm is smaller than with alternatives, or special circumstances often apply."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("Dabigatran", "Direct thrombin inhibitor", "Use caution in choosing dabigatran over other DOACs (such as apixaban) for long-term treatment of atrial fibrillation or VTE.\n> Risk: more GI and major bleeding than apixaban, and more GI bleeding than warfarin"),
            AbbrevRow("Prasugrel, ticagrelor", "P2Y12 inhibitors", "Use with caution, especially in adults 75 and older.\n> If prasugrel is used, consider a lower dose of 5 mg at age 75 and older\n> Risk: more major bleeding than clopidogrel", "The bleeding risk may be offset by cardiovascular benefit in selected patients."),
            AbbrevRow("SIADH", "Drugs that can cause hyponatremia", "Use with caution, and monitor sodium closely when starting or changing doses.\n> Antidepressants: mirtazapine, SNRIs, SSRIs, TCAs\n> Antiepileptics: carbamazepine, oxcarbazepine\n> Antipsychotics, diuretics, tramadol"),
            AbbrevRow("DM-Q", "Dextromethorphan-quinidine", "Use with caution.\n> Risk: limited efficacy for behavioral symptoms of dementia, falls, drug interactions, and concern in heart failure", "Does not apply to treatment of pseudobulbar affect."),
            AbbrevRow("TMP-SMX", "Trimethoprim-sulfamethoxazole", "Use with caution in patients on an ACE inhibitor, ARB or ARNI who also have decreased CrCl.\n> Risk: hyperkalemia"),
            AbbrevRow("SGLT2i", "SGLT2 inhibitors", "Use with caution, and monitor for urogenital infections and ketoacidosis.\n> Examples: canagliflozin, dapagliflozin, empagliflozin, ertugliflozin\n> Risk: urogenital infection (women in the first month), euglycemic diabetic ketoacidosis", "The panel recognizes their value. This is a monitoring advisory.")
        ))
    ), TopicAccent.AMBER),
    TopicSection("Drug Interactions to Avoid", listOf(
        TopicBlock.Text("Combinations that raise risk beyond either drug alone. This is not a comprehensive list of interactions."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("RAS + RAS", "RAS inhibitors or potassium-sparing diuretics", "Avoid routinely using 2 or more of these together in CKD stage 3a or higher.\n> RAS inhibitors: ACE inhibitors, ARBs, ARNIs, aliskiren\n> Potassium-sparing diuretics: amiloride, triamterene\n> Risk: hyperkalemia"),
            AbbrevRow("Opioid + BZD", "Opioid with benzodiazepine", "Avoid.\n> Risk: overdose and adverse events"),
            AbbrevRow("Opioid + gabapentinoid", "Opioid with gabapentin or pregabalin", "Avoid, except when moving from opioid to gabapentinoid or using a gabapentinoid to reduce opioid dose. Use caution in all cases.\n> Risk: severe sedation, respiratory depression, death"),
            AbbrevRow("Anticholinergics", "Two or more anticholinergic drugs", "Avoid, and minimize the number (see Table 7).\n> Risk: cognitive decline, delirium, falls and fractures"),
            AbbrevRow("CNS x3", "Any combination of 3 or more CNS-active drugs", "Avoid, and minimize the number.\n> Antiepileptics including gabapentinoids\n> Antidepressants: TCAs, SSRIs, SNRIs\n> Antipsychotics\n> Benzodiazepines and Z-drugs\n> Opioids\n> Skeletal muscle relaxants\n> Risk: falls and fractures"),
            AbbrevRow("Lithium", "Lithium with ACE inhibitors, ARBs, ARNIs or loop diuretics", "Avoid, and monitor lithium concentrations.\n> Risk: lithium toxicity"),
            AbbrevRow("Alpha-1 + loop", "Non-selective peripheral alpha-1 blocker with loop diuretic", "Avoid in older women, unless conditions warrant both drugs.\n> Risk: urinary incontinence"),
            AbbrevRow("Phenytoin", "Phenytoin with TMP-SMX", "Avoid.\n> Risk: phenytoin toxicity"),
            AbbrevRow("Theophylline", "Theophylline with cimetidine or ciprofloxacin", "Avoid.\n> Risk: theophylline toxicity"),
            AbbrevRow("Warfarin", "Warfarin with interacting drugs", "Avoid when possible. If used together, monitor INR closely.\n> Amiodarone, ciprofloxacin, macrolides except azithromycin, TMP-SMX, SSRIs\n> Risk: bleeding")
        ))
    ), TopicAccent.BLUE),
    TopicSection("Kidney Function", listOf(
        TopicBlock.Text("Cut-offs are creatinine clearance (CrCl) in mL/min unless stated. This is not a comprehensive list of drugs needing renal adjustment. Apixaban was removed in 2023 because evidence supports its use at low kidney function."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("Cipro", "Ciprofloxacin", "CrCl below 30: reduce the dose used for common infections\n> Risk: CNS effects such as seizures and confusion, and tendon rupture"),
            AbbrevRow("Nitrofurantoin", "Urinary antibiotic", "CrCl below 30: avoid\n> Risk: pulmonary toxicity, hepatotoxicity, peripheral neuropathy"),
            AbbrevRow("TMP-SMX", "Trimethoprim-sulfamethoxazole", "CrCl 15 - 29: reduce dose\nCrCl below 15: avoid\n> Risk: worse kidney function and hyperkalemia, especially with ACE inhibitor, ARB or ARNI"),
            AbbrevRow("Amiloride", "Potassium-sparing diuretic", "CrCl below 30: avoid\n> Risk: hyperkalemia and hyponatremia"),
            AbbrevRow("Triamterene", "Potassium-sparing diuretic", "CrCl below 30: avoid\n> Risk: hyperkalemia and hyponatremia"),
            AbbrevRow("Spironolactone", "Mineralocorticoid antagonist", "CrCl below 30: avoid\n> Risk: hyperkalemia"),
            AbbrevRow("Dabigatran", "Direct thrombin inhibitor", "CrCl below 30: avoid\n> At CrCl above 30, adjust the dose when interacting drugs are present", "The US label gives a dose for CrCl 15 - 30, based on pharmacokinetic data. Beers advises avoiding it."),
            AbbrevRow("Edoxaban", "Factor Xa inhibitor", "CrCl 15 - 50: reduce dose\nCrCl below 15 or above 95: avoid", "The label reduced dose is 30 mg once daily."),
            AbbrevRow("Rivaroxaban", "Factor Xa inhibitor", "CrCl 15 - 50: reduce dose per the manufacturer, based on indication\nCrCl below 15: avoid", "Dosing depends on the indication, so follow the product label."),
            AbbrevRow("Enoxaparin", "Low molecular weight heparin", "CrCl below 30: reduce dose\n> Risk: bleeding"),
            AbbrevRow("Fondaparinux", "Factor Xa inhibitor", "CrCl below 30: avoid\n> Risk: bleeding"),
            AbbrevRow("Dofetilide", "Antiarrhythmic", "CrCl 20 - 59: reduce dose\nCrCl below 20: avoid\n> Risk: QTc prolongation and torsades de pointes"),
            AbbrevRow("Baclofen", "Muscle relaxant for spasticity", "eGFR below 60: avoid\n> If it cannot be avoided, use the lowest effective dose and watch for CNS toxicity such as altered mental status\n> Risk: encephalopathy needing hospitalization"),
            AbbrevRow("Gabapentin", "Gabapentinoid", "CrCl below 60: reduce dose\n> Risk: CNS adverse effects"),
            AbbrevRow("Pregabalin", "Gabapentinoid", "CrCl below 60: reduce dose\n> Risk: CNS adverse effects"),
            AbbrevRow("Levetiracetam", "Antiepileptic", "CrCl 80 or below: reduce dose\n> Risk: CNS adverse effects"),
            AbbrevRow("Tramadol", "Opioid analgesic", "CrCl below 30:\n> Immediate release: reduce dose\n> Extended release: avoid\n> Risk: CNS adverse effects"),
            AbbrevRow("Duloxetine", "SNRI", "CrCl below 30: avoid\n> Risk: nausea and diarrhea"),
            AbbrevRow("NSAIDs", "Non-selective, COX-2 selective and non-acetylated salicylates, oral and parenteral", "CrCl below 30: avoid\n> Includes celecoxib, diflunisal and magnesium salicylate\n> Risk: acute kidney injury and further decline in kidney function"),
            AbbrevRow("H2 blockers", "Cimetidine, famotidine, nizatidine", "CrCl below 50: reduce dose\n> Risk: mental status changes"),
            AbbrevRow("Colchicine", "Gout therapy", "CrCl below 30: reduce dose and monitor for adverse effects\n> Risk: GI, neuromuscular and bone marrow toxicity"),
            AbbrevRow("Probenecid", "Uricosuric", "CrCl below 30: avoid\n> Risk: loss of effectiveness")
        ))
    ), TopicAccent.VIOLET),
    TopicSection("Strong Anticholinergics", listOf(
        TopicBlock.Text("Drugs with strong anticholinergic properties, from Table 7. They add up, so count them at each medication review. This is not a comprehensive list."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("Antidepressants", "TCAs and paroxetine", "Amitriptyline, amoxapine, clomipramine, desipramine, doxepin above 6 mg/day, imipramine, nortriptyline, paroxetine"),
            AbbrevRow("Antiemetics", "Antiemetics", "Prochlorperazine, promethazine"),
            AbbrevRow("Antihistamines", "First-generation H1 blockers", "Brompheniramine, chlorpheniramine, cyproheptadine, dimenhydrinate, diphenhydramine, doxylamine, hydroxyzine, meclizine, promethazine, triprolidine"),
            AbbrevRow("Bladder", "Antimuscarinics for urinary incontinence", "Darifenacin, fesoterodine, flavoxate, oxybutynin, solifenacin, tolterodine, trospium", "Oxybutynin has the best evidence for adverse cognitive effects, but caution applies to all bladder antimuscarinics."),
            AbbrevRow("Parkinson", "Anti-parkinson agents", "Benztropine, trihexyphenidyl"),
            AbbrevRow("Antipsychotics", "Antipsychotics", "Chlorpromazine, clozapine, olanzapine, perphenazine"),
            AbbrevRow("Antispasmodics", "Antispasmodics", "Atropine, clidinium-chlordiazepoxide, dicyclomine, homatropine, hyoscyamine, scopolamine"),
            AbbrevRow("Relaxants", "Skeletal muscle relaxants", "Cyclobenzaprine, orphenadrine")
        ))
    ), TopicAccent.TEAL),
    TopicSection("Clinical Pearls", listOf(
        TopicBlock.Bullets(listOf(
            "Medicines in the criteria are potentially inappropriate, not definitely inappropriate. Read the rationale and exceptions for each.",
            "Use the criteria as a starting point for a full medication review that considers the person's goals and preferences.",
            "Count anticholinergic and CNS-active drugs together, not one by one.",
            "Estimate CrCl before prescribing, and recheck it when kidney function may have changed.",
            "Deprescribing needs a plan: taper where needed, involve the patient and family, and tell the pharmacy to stop refills.",
            "Prefer non-drug measures first for behaviors in dementia, insomnia and pain where possible.",
            "Document the reason when a listed drug is continued."
        )),
        TopicBlock.Callout(
            "Scope",
            "These criteria do not apply to hospice or end-of-life care. They are designed for use in the United States, so confirm local drug availability and the product label before prescribing.",
            CalloutKind.INFO
        )
    ), TopicAccent.GREEN)
)
