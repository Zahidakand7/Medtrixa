package com.medtrixa.app.ui.screens.more.topics

import com.medtrixa.app.ui.screens.more.*

internal val beersSectionsB: List<TopicSection> = listOf(
    TopicSection("Cardiovascular and Antithrombotics", listOf(
        TopicBlock.Text("Heart, blood pressure and clotting drugs often cause hypotension, bradycardia or bleeding in older adults. When choosing a DOAC or its dose, consider kidney function (see Kidney Function), indication and body weight."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("Alpha-1", "Non-selective peripheral alpha-1 blockers", "Avoid as an antihypertensive.\n> Examples: doxazosin, prazosin, terazosin\n> Risk: orthostatic hypotension and related harms"),
            AbbrevRow("Central a-2", "Central alpha-agonists", "Avoid clonidine as first-line treatment for hypertension.\nAvoid other central alpha-agonists (such as guanfacine) for hypertension.\n> Risk: adverse CNS effects, bradycardia, orthostatic hypotension"),
            AbbrevRow("Nifedipine IR", "Immediate-release nifedipine", "Avoid.\n> Risk: hypotension and possible myocardial ischemia"),
            AbbrevRow("Amiodarone", "Antiarrhythmic", "Avoid as first-line therapy for atrial fibrillation, unless heart failure or substantial left ventricular hypertrophy is present.\n> Risk: greater toxicity than other antiarrhythmics", "May be reasonable first-line with heart failure or substantial LVH if rhythm control is preferred over rate control."),
            AbbrevRow("Dronedarone", "Antiarrhythmic", "Avoid in permanent atrial fibrillation or severe or recently decompensated heart failure.\n> Use caution in reduced ejection fraction with milder symptoms (NYHA class I or II)"),
            AbbrevRow("Digoxin", "Cardiac glycoside", "Avoid as first-line therapy for atrial fibrillation or heart failure.\n> If used, avoid doses above 0.125 mg/day\n> Reduce the dose further in CKD stage 4 or 5", "Use caution when stopping digoxin in long-term users with reduced ejection fraction heart failure."),
            AbbrevRow("Dipyridamole", "Oral short-acting", "Avoid.\n> Risk: orthostatic hypotension, and more effective alternatives exist", "Does not apply to the extended-release combination with aspirin. The IV form is acceptable for cardiac stress testing."),
            AbbrevRow("Aspirin", "Primary prevention", "Avoid initiating aspirin for primary prevention of cardiovascular disease.\n> Consider deprescribing in those already taking it\n> Risk: major bleeding rises markedly with age", "Aspirin is generally indicated for secondary prevention in established cardiovascular disease."),
            AbbrevRow("Warfarin", "Vitamin K antagonist", "Avoid starting as initial therapy for nonvalvular atrial fibrillation or VTE, unless DOACs are contraindicated or there are substantial barriers.\n> Risk: more major bleeding, especially intracranial, than DOACs", "Long-term users with well-controlled INR (time in therapeutic range above 70%) and no adverse effects may reasonably continue."),
            AbbrevRow("Rivaroxaban", "Factor Xa inhibitor", "Avoid for long-term treatment of atrial fibrillation or VTE in favor of safer alternatives.\n> Risk: more major and GI bleeding than other DOACs, especially apixaban", "May be reasonable in special situations, such as when once-daily dosing is needed for adherence.")
        ))
    ), TopicAccent.RED),
    TopicSection("Endocrine", listOf(
        TopicBlock.Text("Hypoglycemia and hormone-related harms outweigh benefit in many older adults."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("Sliding scale", "Insulin sliding scale", "Avoid regimens with only short- or rapid-acting insulin dosed by current glucose, without basal or long-acting insulin.\n> Risk: more hypoglycemia without better glucose control", "Does not apply to regimens that contain basal or long-acting insulin."),
            AbbrevRow("SU", "Sulfonylureas, all", "Avoid as first- or second-line monotherapy or add-on, unless there are substantial barriers to safer agents.\n> Examples: gliclazide, glimepiride, glipizide, glyburide\n> If used, choose short-acting agents such as glipizide over long-acting ones such as glyburide or glimepiride\n> Risk: cardiovascular events, mortality, prolonged hypoglycemia"),
            AbbrevRow("Androgens", "Methyltestosterone, testosterone", "Avoid unless indicated for confirmed hypogonadism with clinical symptoms.\n> Risk: possible cardiac problems, and potential risk in men with prostate cancer"),
            AbbrevRow("Estrogens", "With or without progestins", "Do not start systemic estrogen (oral tablets or patches).\n> Consider deprescribing in older women already using it\n> Risk: breast and endometrial cancer, heart disease, stroke, blood clots, dementia", "Low-dose vaginal cream or tablets are acceptable for dyspareunia, recurrent lower urinary tract infections and other vaginal symptoms. After breast cancer, discuss low-dose vaginal estrogen (estradiol below 25 mcg twice weekly) with the clinician."),
            AbbrevRow("Thyroid", "Desiccated thyroid", "Avoid.\n> Risk: cardiac effects, and safer alternatives exist"),
            AbbrevRow("Megestrol", "Appetite stimulant", "Avoid.\n> Risk: thrombotic events and possibly death, with minimal effect on weight"),
            AbbrevRow("GH", "Growth hormone", "Avoid, except for patients rigorously diagnosed with growth hormone deficiency from an established cause.\n> Risk: edema, arthralgia, carpal tunnel syndrome, gynecomastia, impaired fasting glucose")
        ))
    ), TopicAccent.TEAL),
    TopicSection("Pain, Gut, Urinary and Other", listOf(
        TopicBlock.Text("Analgesics, acid suppressants and other drugs that are riskier in later life."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("NSAIDs", "Non-COX-2-selective, oral", "Avoid chronic use unless other options are not effective and the patient can take a gastroprotective agent (proton-pump inhibitor or misoprostol).\nAvoid short-term scheduled use with oral or parenteral corticosteroids, anticoagulants or antiplatelets, unless other options fail and gastroprotection is used.\n> Examples: aspirin above 325 mg/day, diclofenac, diflunisal, etodolac, flurbiprofen, ibuprofen, meloxicam, nabumetone, naproxen, oxaprozin, piroxicam, sulindac\n> Risk: GI bleeding or ulcer, raised blood pressure, kidney injury", "GI risk is highest in people over 75, or on corticosteroids, anticoagulants or antiplatelets."),
            AbbrevRow("Indo/Ketorolac", "Indomethacin and ketorolac, oral and parenteral", "Avoid.\n> Risk: GI bleeding, peptic ulcer, acute kidney injury\n> Indomethacin has the most adverse effects, including CNS effects"),
            AbbrevRow("Meperidine", "Opioid analgesic", "Avoid.\n> Risk: not effective orally at usual doses, and higher risk of neurotoxicity including delirium"),
            AbbrevRow("PPIs", "Proton-pump inhibitors", "Avoid scheduled use for more than 8 weeks, unless high risk or a demonstrated need.\n> Examples of need: oral corticosteroids or chronic NSAIDs, erosive esophagitis, Barrett esophagus, pathologic hypersecretory condition, failed drug discontinuation or H2-blocker trial\n> Examples: dexlansoprazole, esomeprazole, lansoprazole, omeprazole, pantoprazole, rabeprazole\n> Risk: C. difficile infection, pneumonia, GI cancers, bone loss, fractures"),
            AbbrevRow("Metoclopramide", "Prokinetic antiemetic", "Avoid, unless for gastroparesis, with duration not above 12 weeks except in rare cases.\n> Risk: extrapyramidal effects including tardive dyskinesia"),
            AbbrevRow("Mineral oil", "Oral laxative", "Avoid.\n> Risk: aspiration and other adverse effects, and safer options exist"),
            AbbrevRow("Desmopressin", "Antidiuretic hormone analog", "Avoid for nocturia or nocturnal polyuria.\n> Risk: hyponatremia"),
            AbbrevRow("Nitrofurantoin", "Urinary antibiotic", "Avoid if CrCl is below 30 mL/min, or for long-term suppression.\n> Risk: pulmonary toxicity, hepatotoxicity, peripheral neuropathy"),
            AbbrevRow("Ergoloids", "Ergoloid mesylates", "Avoid.\n> Risk: lack of efficacy")
        ))
    ), TopicAccent.GREEN)
)
