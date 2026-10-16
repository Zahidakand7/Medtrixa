package com.medtrixa.app.ui.screens.more.topics

import com.medtrixa.app.ui.screens.more.*

internal val mtSectionsA: List<TopicSection> = listOf(
    TopicSection("About Medical Terms", listOf(
        TopicBlock.Text("Most medical terms are built from a root, with a prefix before it and a suffix after it. Learning the parts lets you work out new terms. Many terms come from Greek and Latin, so their spellings and plurals follow those languages."),
        TopicBlock.Bullets(listOf(
            "Root: the core meaning, such as cardi = heart.",
            "Prefix: comes first and changes the meaning, such as hyper- = above or excess.",
            "Suffix: ends the term, such as -itis = inflammation.",
            "Combining vowel: usually o, and it joins roots, as in gastr/o/enter/itis.",
            "Example: gastroenteritis = gastr (stomach) + enter (intestine) + -itis (inflammation).",
            "A sign is something an examiner observes or measures. A symptom is something the patient feels and reports.",
            "Drug and prescription abbreviations are in the Prescription Terminology topic."
        )),
        TopicBlock.Callout(
            "Note",
            "Plurals follow Latin or Greek rules: bronchus becomes bronchi, diagnosis becomes diagnoses, and vertebra becomes vertebrae. Meanings can differ slightly between specialties and regions.",
            CalloutKind.INFO
        )
    ), TopicAccent.BLUE),
    TopicSection("Prefixes", listOf(
        TopicBlock.Text("The start of a term. Each row gives the meaning and an example."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("a-, an-", "without, not", "Examples: anemia (without enough blood), apnea (without breathing), anuria (without urine)."),
            AbbrevRow("brady-", "slow", "Bradycardia is a slow heart rate, usually below 60/min in adults."),
            AbbrevRow("tachy-", "fast", "Tachycardia is a fast heart rate, usually above 100/min in adults. Tachypnea is fast breathing."),
            AbbrevRow("hyper-", "above, excess", "Examples: hypertension, hyperglycemia, hyperthermia."),
            AbbrevRow("hypo-", "below, deficient", "Examples: hypotension, hypoglycemia, hypothermia."),
            AbbrevRow("dys-", "difficult, painful, abnormal", "Examples: dyspnea (difficult breathing), dysphagia (difficulty swallowing), dysuria (painful urination)."),
            AbbrevRow("poly-", "many, much", "Examples: polyuria (large urine volume), polydipsia (excess thirst), polycythemia (too many red cells)."),
            AbbrevRow("oligo-", "few, scanty", "Oliguria is a low urine output."),
            AbbrevRow("hemi-", "half", "Hemiplegia is paralysis of one side of the body."),
            AbbrevRow("macro-, micro-", "large, small", "Macrocytic and microcytic describe large and small red cells in anemia."),
            AbbrevRow("peri-", "around", "Examples: pericardium (around the heart), periosteum (around the bone)."),
            AbbrevRow("epi-", "upon, above", "Examples: epidermis (outer skin), epigastric (above the stomach)."),
            AbbrevRow("sub-", "under", "Examples: subcutaneous (under the skin), sublingual (under the tongue)."),
            AbbrevRow("inter-, intra-", "between, within", "Examples: intercostal (between ribs), intravenous (within a vein)."),
            AbbrevRow("trans-", "across, through", "Examples: transdermal (through the skin), transfusion."),
            AbbrevRow("pre-, post-", "before, after", "Examples: preoperative, postoperative."),
            AbbrevRow("anti-", "against", "Examples: antibiotic, anticoagulant."),
            AbbrevRow("neo-", "new", "Examples: neonate (newborn), neoplasm (new growth)."),
            AbbrevRow("mal-", "bad, poor", "Examples: malabsorption, malnutrition, malaise."),
            AbbrevRow("contra-", "against, opposite", "A contraindication is a reason not to give a treatment.")
        ))
    ), TopicAccent.VIOLET),
    TopicSection("Suffixes", listOf(
        TopicBlock.Text("The end of a term. These often tell you whether it is a disease, a procedure or a test."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("-itis", "inflammation", "Examples: appendicitis, hepatitis, arthritis."),
            AbbrevRow("-ectomy", "surgical removal", "Examples: appendectomy, mastectomy."),
            AbbrevRow("-otomy", "cutting into", "Examples: laparotomy (opening the abdomen), tracheotomy."),
            AbbrevRow("-ostomy", "making a new opening", "Examples: colostomy, tracheostomy."),
            AbbrevRow("-plasty", "surgical repair or reshaping", "Examples: angioplasty, rhinoplasty."),
            AbbrevRow("-scopy", "viewing with an instrument", "Examples: colonoscopy, bronchoscopy."),
            AbbrevRow("-gram", "a record or image", "Examples: electrocardiogram, angiogram."),
            AbbrevRow("-graphy", "the process of recording", "Examples: mammography, echocardiography."),
            AbbrevRow("-pathy", "disease", "Examples: neuropathy, cardiomyopathy."),
            AbbrevRow("-emia", "condition of the blood", "Examples: anemia, hyperkalemia (high potassium in the blood)."),
            AbbrevRow("-uria", "condition of the urine", "Examples: hematuria (blood in urine), glycosuria (glucose in urine)."),
            AbbrevRow("-algia, -dynia", "pain", "Examples: myalgia (muscle pain), neuralgia."),
            AbbrevRow("-oma", "tumor or mass", "Examples: lymphoma, hematoma. A -oma is not always cancer."),
            AbbrevRow("-osis", "abnormal condition", "Examples: cyanosis, thrombosis."),
            AbbrevRow("-penia", "deficiency", "Examples: leukopenia (low white cells), thrombocytopenia (low platelets)."),
            AbbrevRow("-cytosis", "increase in cells", "Leukocytosis is a raised white cell count."),
            AbbrevRow("-rrhea", "flow or discharge", "Examples: diarrhea, rhinorrhea (runny nose)."),
            AbbrevRow("-rrhage", "bursting forth of blood", "Hemorrhage is bleeding."),
            AbbrevRow("-megaly", "enlargement", "Examples: cardiomegaly, hepatomegaly."),
            AbbrevRow("-plegia", "paralysis", "Examples: paraplegia (both legs), quadriplegia. -paresis means weakness."),
            AbbrevRow("-pnea", "breathing", "Examples: dyspnea, apnea, tachypnea."),
            AbbrevRow("-sclerosis", "hardening", "Atherosclerosis is hardening of the arteries."),
            AbbrevRow("-stasis", "stopping or control", "Hemostasis is stopping bleeding."),
            AbbrevRow("-lysis", "breakdown or destruction", "Hemolysis is destruction of red blood cells."),
            AbbrevRow("-centesis", "puncture to remove fluid", "Examples: paracentesis (abdomen), thoracentesis (chest), amniocentesis.")
        ))
    ), TopicAccent.TEAL)
)
