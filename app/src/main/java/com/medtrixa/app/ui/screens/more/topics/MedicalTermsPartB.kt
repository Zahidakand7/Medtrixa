package com.medtrixa.app.ui.screens.more.topics

import com.medtrixa.app.ui.screens.more.*

internal val mtSectionsB: List<TopicSection> = listOf(
    TopicSection("Root Words by Body System", listOf(
        TopicBlock.Text("The core of a term. Combine with a prefix or suffix, for example nephr/o + -pathy = nephropathy (kidney disease)."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("Heart and blood", "Cardiovascular", "cardi/o: heart\n> angi/o, vas/o: vessel\n> arteri/o: artery\n> ven/o, phleb/o: vein\n> hem/o, hemat/o: blood"),
            AbbrevRow("Respiratory", "Breathing", "pulmon/o, pneum/o: lung or air\n> bronch/o: bronchus\n> trache/o: windpipe\n> laryng/o: voice box\n> thorac/o: chest"),
            AbbrevRow("Digestive", "Gut and liver", "gastr/o: stomach\n> enter/o: small intestine\n> col/o: colon\n> hepat/o: liver\n> cholecyst/o: gallbladder\n> esophag/o: esophagus\n> proct/o: rectum and anus"),
            AbbrevRow("Urinary and reproductive", "Kidney to reproduction", "nephr/o, ren/o: kidney\n> cyst/o: bladder\n> ur/o: urine\n> hyster/o: uterus\n> oophor/o: ovary\n> orchi/o: testis\n> mamm/o, mast/o: breast"),
            AbbrevRow("Nervous", "Brain and nerves", "neur/o: nerve\n> encephal/o: brain\n> cephal/o: head\n> myel/o: spinal cord, also bone marrow\n> mening/o: meninges"),
            AbbrevRow("Muscle and bone", "Musculoskeletal", "oste/o: bone\n> arthr/o: joint\n> my/o: muscle\n> chondr/o: cartilage\n> ten/o: tendon"),
            AbbrevRow("Skin, eye, ear", "Senses and covering", "derm/o, dermat/o: skin\n> cutane/o: skin\n> ophthalm/o, ocul/o: eye\n> ot/o: ear\n> rhin/o: nose"),
            AbbrevRow("General", "Cells, glands, disease", "cyt/o: cell\n> hist/o: tissue\n> aden/o: gland\n> thyr/o: thyroid\n> adren/o: adrenal gland\n> lymph/o: lymph\n> onc/o: tumor\n> path/o: disease")
        ))
    ), TopicAccent.GREEN),
    TopicSection("Directions and Body Positions", listOf(
        TopicBlock.Text("Directions assume the person is standing upright with palms facing forward (the anatomical position)."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("Anterior, posterior", "Front, back", "Anterior (ventral) is toward the front.\nPosterior (dorsal) is toward the back."),
            AbbrevRow("Superior, inferior", "Above, below", "Superior (cranial) is toward the head.\nInferior (caudal) is toward the feet."),
            AbbrevRow("Medial, lateral", "Toward, away from the midline", "Medial is nearer the midline.\nLateral is farther from the midline."),
            AbbrevRow("Proximal, distal", "Near, far from the attachment", "Proximal is nearer the trunk or point of origin.\nDistal is farther from it. The wrist is distal to the elbow."),
            AbbrevRow("Superficial, deep", "Near, far from the surface", "Superficial is nearer the body surface.\nDeep is farther from it."),
            AbbrevRow("Planes", "Ways to cut the body", "Sagittal: left and right.\nCoronal (frontal): front and back.\nTransverse (axial): top and bottom."),
            AbbrevRow("Quadrants", "Abdominal regions", "RUQ: liver, gallbladder\n> LUQ: stomach, spleen\n> RLQ: appendix, cecum\n> LLQ: sigmoid colon\n> The ovaries lie in the lower quadrants"),
            AbbrevRow("Supine, prone", "Lying positions", "Supine: lying on the back, face up.\nProne: lying face down.\nLateral recumbent: lying on the side."),
            AbbrevRow("Fowler's", "Head-up positions", "Semi-Fowler: head raised 30 - 45 degrees.\nFowler: 45 - 60 degrees.\nHigh Fowler: 60 - 90 degrees. This often eases breathing."),
            AbbrevRow("Trendelenburg", "Head-down tilt", "The head is lower than the feet.\n> Reverse Trendelenburg has the head higher than the feet"),
            AbbrevRow("Lithotomy", "Pelvic exam position", "Lying on the back with the hips and knees flexed and the feet in supports."),
            AbbrevRow("Joint movements", "Common actions", "Flexion bends a joint, and extension straightens it.\nAbduction moves away from the midline, and adduction moves toward it.\nRotation turns around an axis.")
        ))
    ), TopicAccent.AMBER),
    TopicSection("Signs and Symptoms", listOf(
        TopicBlock.Text("Terms used to describe what a patient has or what an examiner finds."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("Dyspnea", "Shortness of breath", "Orthopnea is breathlessness lying flat, relieved by sitting up. PND (paroxysmal nocturnal dyspnea) wakes the patient at night. Both are typical of heart failure."),
            AbbrevRow("Tachypnea", "Fast breathing", "A respiratory rate above 20/min in adults.\n> Apnea: no breathing\n> Bradypnea: slow breathing"),
            AbbrevRow("Hemoptysis", "Coughing up blood", "Blood comes from the airway or lungs."),
            AbbrevRow("Hematemesis", "Vomiting blood", "The blood may be bright red or look like coffee grounds."),
            AbbrevRow("Melena", "Black, tarry stool", "Digested blood, usually from the upper GI tract. Hematochezia is bright red blood from the rectum, usually from the lower GI tract."),
            AbbrevRow("Epistaxis", "Nosebleed", "Bleeding from the nose."),
            AbbrevRow("Syncope", "Fainting", "A brief loss of consciousness from reduced blood flow to the brain."),
            AbbrevRow("Vertigo", "Spinning sensation", "A feeling that the person or the room is moving. It differs from general dizziness."),
            AbbrevRow("Edema", "Swelling from fluid", "Pitting edema leaves a dent when pressed. Ascites is fluid in the abdomen. Anasarca is widespread swelling."),
            AbbrevRow("Cyanosis", "Blue discoloration", "Seen in the lips, nails or skin when blood oxygen is low."),
            AbbrevRow("Jaundice", "Yellow skin and eyes", "From a high bilirubin level. Also called icterus."),
            AbbrevRow("Diaphoresis", "Heavy sweating", "A sign in many emergencies such as a heart attack or shock."),
            AbbrevRow("Fever", "Pyrexia", "A body temperature of 38 °C (100.4 °F) or above.\n> Hypothermia: core temperature below 35 °C (95 °F)"),
            AbbrevRow("Dysphagia", "Difficulty swallowing", "Odynophagia is painful swallowing."),
            AbbrevRow("Dysuria", "Painful urination", "Often a sign of urinary infection. Hematuria is blood in the urine."),
            AbbrevRow("Oliguria", "Low urine output", "Below about 400 mL/day. Anuria is below about 100 mL/day. Polyuria is a large urine volume, and nocturia is waking at night to urinate."),
            AbbrevRow("Palpitations", "Awareness of the heartbeat", "A feeling of a pounding, fluttering or skipped heartbeat."),
            AbbrevRow("Claudication", "Pain on walking", "Calf pain on exertion that eases with rest, a sign of narrowed leg arteries."),
            AbbrevRow("Pruritus", "Itching", "Urticaria means hives."),
            AbbrevRow("Paresthesia", "Tingling or pins and needles", "An abnormal skin sensation without a clear stimulus."),
            AbbrevRow("Ataxia", "Poor coordination", "Unsteady movement and gait, often from cerebellar disease."),
            AbbrevRow("Malaise", "General unwell feeling", "Fatigue is tiredness. Cachexia is severe wasting."),
            AbbrevRow("Anorexia", "Loss of appetite", "This is a symptom, and it is different from the eating disorder anorexia nervosa.")
        ))
    ), TopicAccent.ROSE)
)
