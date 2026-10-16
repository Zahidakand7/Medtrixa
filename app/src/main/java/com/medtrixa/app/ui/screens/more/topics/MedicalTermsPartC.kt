package com.medtrixa.app.ui.screens.more.topics

import com.medtrixa.app.ui.screens.more.*

internal val mtSectionsC: List<TopicSection> = listOf(
    TopicSection("Disease and Clinical Terms", listOf(
        TopicBlock.Text("Words used to describe the course, cause and nature of illness."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("Acute, chronic", "Time course", "Acute starts suddenly and is short. Chronic lasts a long time, often more than 3 months. Subacute lies in between."),
            AbbrevRow("Etiology", "Cause of a disease", "Pathogenesis is how the disease develops. Pathophysiology is how it changes normal function."),
            AbbrevRow("Diagnosis", "Naming the disease", "A differential diagnosis is the list of possible causes. Prognosis is the expected outcome."),
            AbbrevRow("Benign, malignant", "Tumor behavior", "Benign tumors stay local and are not cancer. Malignant tumors are cancer and can invade. Metastasis is spread to distant sites."),
            AbbrevRow("Remission, relapse", "Disease activity", "Remission is reduced or absent disease. Relapse is its return. An exacerbation is a flare of a chronic disease."),
            AbbrevRow("Morbidity, mortality", "Illness, death", "Morbidity is illness or complications. Mortality is death."),
            AbbrevRow("Incidence, prevalence", "Counting disease", "Incidence is new cases in a period. Prevalence is all existing cases at a time."),
            AbbrevRow("Idiopathic", "Unknown cause", "Iatrogenic means caused by medical care. Nosocomial means acquired in hospital."),
            AbbrevRow("Comorbidity", "Coexisting disease", "Another condition present with the main one, such as diabetes with heart disease."),
            AbbrevRow("Ischemia", "Reduced blood supply", "Infarction is tissue death from ischemia. Necrosis is cell death."),
            AbbrevRow("Hypoxia", "Low oxygen in tissues", "Hypoxemia is low oxygen in the blood (low PaO₂ or SpO₂). Hypercapnia is a high CO₂ level in the blood."),
            AbbrevRow("Sepsis", "Organ dysfunction from infection", "A life-threatening response to infection. Bacteremia is bacteria in the blood. Septic shock is sepsis with persistent low blood pressure needing vasopressors."),
            AbbrevRow("Thrombus, embolus", "Clots", "A thrombus is a clot that stays where it formed. An embolus travels in the blood, and can be a clot, fat, air or other material."),
            AbbrevRow("Hypertrophy", "Cell size and number", "Hypertrophy: cells grow larger.\nHyperplasia: more cells.\nAtrophy: shrinkage.\nDysplasia: abnormal cell growth.\nMetaplasia: one mature cell type replaced by another.\nNeoplasia: new abnormal growth."),
            AbbrevRow("Stenosis", "Narrowing", "Regurgitation or insufficiency is backward flow, such as through a heart valve."),
            AbbrevRow("Pneumothorax", "Air in the pleural space", "Hemothorax is blood there. A pleural effusion is fluid there. Atelectasis is collapse of part of the lung."),
            AbbrevRow("Aneurysm", "Bulge in a vessel wall", "A hernia is a protrusion through a weak area. A fistula is an abnormal connection. An abscess is a collection of pus."),
            AbbrevRow("Ulcer", "Break in skin or lining", "A lesion is any abnormal area of tissue. A polyp is a growth on a surface. A cyst is a fluid-filled sac.")
        ))
    ), TopicAccent.RED),
    TopicSection("Tests and Procedures", listOf(
        TopicBlock.Text("Common examinations, tests and interventions."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("Physical exam", "Four basic methods", "Inspection: look.\nPalpation: feel.\nPercussion: tap and listen.\nAuscultation: listen with a stethoscope."),
            AbbrevRow("Biopsy", "Tissue sample", "A small sample is removed and examined under a microscope to confirm a diagnosis."),
            AbbrevRow("Endoscopy", "Looking inside with a scope", "Examples: gastroscopy, colonoscopy, bronchoscopy. Laparoscopy views the abdomen through small incisions."),
            AbbrevRow("Imaging", "Pictures of the body", "X-ray: bone and chest.\nCT: detailed cross-sections using X-rays.\nMRI: magnetic fields, good for soft tissue.\nUltrasound: sound waves, no radiation.\nPET: shows metabolic activity."),
            AbbrevRow("ECG, EEG, EMG", "Electrical recordings", "ECG: heart. EEG: brain. EMG: muscle and nerve. An echocardiogram is an ultrasound of the heart."),
            AbbrevRow("Oxygen tests", "Measuring oxygen and CO₂", "Pulse oximetry gives SpO₂.\nAn arterial blood gas (ABG) gives pH, PaO₂, PaCO₂ and HCO₃⁻.\nCapnography measures end-tidal CO₂ (etCO₂)."),
            AbbrevRow("Blood tests", "Common panels", "CBC: blood cell counts.\nBMP: electrolytes, glucose, kidney tests.\nCulture and sensitivity: grows bacteria and tests which drugs work."),
            AbbrevRow("Lumbar puncture", "Spinal tap", "A needle takes cerebrospinal fluid from the lower back."),
            AbbrevRow("Centesis", "Draining fluid", "Paracentesis: abdomen.\nThoracentesis: chest.\nArthrocentesis: joint."),
            AbbrevRow("Intubation", "Placing a tube", "An endotracheal tube is placed in the airway to ventilate. Catheterization places a tube into a body cavity or vessel, such as the bladder."),
            AbbrevRow("Surgical terms", "Common actions", "Resection: removing part of an organ.\nAnastomosis: joining two structures.\nDebridement: removing dead tissue.\nIncision and drainage: opening an abscess."),
            AbbrevRow("Dialysis", "Replacing kidney function", "Hemodialysis filters blood through a machine. Peritoneal dialysis uses the abdominal lining.")
        ))
    ), TopicAccent.BLUE),
    TopicSection("Easily Confused Terms", listOf(
        TopicBlock.Text("Pairs that look or sound alike but mean different things."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("Sign, symptom", "Objective, subjective", "A sign is observed or measured, such as a fever. A symptom is felt by the patient, such as pain."),
            AbbrevRow("Hypoxia, hypoxemia", "Tissues, blood", "Hypoxia is low oxygen at the tissue level. Hypoxemia is low oxygen in the blood."),
            AbbrevRow("Ischemia, infarction", "Reduced supply, tissue death", "Ischemia can be reversed. Infarction means the tissue has died."),
            AbbrevRow("Dysphagia, dysphasia", "Swallowing, language", "Dysphagia is difficulty swallowing. Dysphasia is impaired language. Aphasia is loss of language."),
            AbbrevRow("Ileum, ilium", "Gut, bone", "The ileum is the last part of the small intestine. The ilium is part of the pelvic bone."),
            AbbrevRow("Hemoptysis, hematemesis", "Cough, vomit", "Hemoptysis is blood coughed up from the airway. Hematemesis is blood vomited from the stomach or upper gut."),
            AbbrevRow("Hyperplasia, hypertrophy", "More cells, bigger cells", "Hyperplasia is an increase in cell number. Hypertrophy is an increase in cell size."),
            AbbrevRow("Abduction, adduction", "Away, toward", "Abduction moves a limb away from the midline. Adduction moves it toward the midline."),
            AbbrevRow("Palpation, palpitation", "Touch, heartbeat", "Palpation is examining by touch. Palpitation is awareness of the heartbeat."),
            AbbrevRow("Oliguria, anuria", "Low urine, none", "Oliguria is a reduced urine output. Anuria is almost no urine."),
            AbbrevRow("Hemostasis, homeostasis", "Stopping bleeding, balance", "Hemostasis is stopping bleeding. Homeostasis is keeping the body's internal balance."),
            AbbrevRow("Ureter, urethra", "Kidney to bladder, bladder to outside", "Ureters carry urine from the kidneys to the bladder. The urethra carries it out of the body."),
            AbbrevRow("Prostate, prostrate", "Gland, lying down", "The prostate is a gland. Prostrate means lying face down, or exhausted."),
            AbbrevRow("Mucous, mucus", "Adjective, noun", "Mucous describes a membrane. Mucus is the substance it makes.")
        ))
    ), TopicAccent.VIOLET),
    TopicSection("Clinical Pearls", listOf(
        TopicBlock.Bullets(listOf(
            "Break an unfamiliar term into prefix, root and suffix, then put the meanings together.",
            "The suffix often tells you the kind of term: -itis is a disease, -ectomy is surgery, and -scopy or -gram is a test.",
            "Check hyper- and hypo-, and abduction and adduction, whenever a note could be misread.",
            "Always say hemoptysis, hematemesis, melena and hematochezia correctly, because they point to different bleeding sites.",
            "Hypoxemia is measured (SpO₂, PaO₂), while hypoxia is a state in the tissues that you infer from the picture.",
            "Write terms in full when the abbreviation is on a Do Not Use list. See Prescription Terminology.",
            "When unsure of a term, look it up in a medical dictionary before documenting it."
        )),
        TopicBlock.Callout(
            "Scope",
            "This is a quick reference to common terms. Normal ranges, such as respiratory rate and urine output, are approximate adult values. Pediatric values and local definitions differ.",
            CalloutKind.INFO
        )
    ), TopicAccent.TEAL)
)
