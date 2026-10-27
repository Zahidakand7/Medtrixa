package com.medtrixa.app.ui.screens

enum class DrugGroup(val title: String, val hint: String) {
    ANTIMICROBIALS("Antimicrobials", "Antibiotics, antivirals, antifungals"),
    ONCOLOGY_IMMUNOLOGY("Oncology & Immunology", "Cancer and immune therapies"),
    BLOOD("Blood & Hematology", "Anticoagulants, antiplatelets"),
    CARDIOVASCULAR("Cardiovascular", "BP, lipids, rhythm"),
    RESPIRATORY_ALLERGY("Respiratory & Allergy", "Asthma, COPD, antihistamines"),
    GI_HEPATIC("GI & Hepatic", "Acid, nausea, bowel"),
    ENDOCRINE_METABOLIC("Endocrine & Metabolic", "Diabetes, thyroid, hormones"),
    CNS_PAIN("CNS & Pain", "Analgesics, psychiatric, neurology"),
    OTHER("Other", "Remaining classes")
}

private val groupRules: List<Pair<DrugGroup, List<String>>> = listOf(
    DrugGroup.OTHER to listOf("rho kinase"),
    DrugGroup.ANTIMICROBIALS to listOf(
        "antibacterial", "antimicrobial", "antifungal", "antiviral", "antimycobacterial", "antimalarial",
        "antiprotozoal", "anthelmintic", "antiparasitic", "ectoparasiticide", "pediculicide",
        "human immunodeficiency", "hepatitis b virus", "hepatitis c virus", "herpes", "cytomegalovirus",
        "influenza", "neuraminidase", "sars-cov", "orthopoxvirus", "protease inhibitor",
        "cytochrome p450 3a inhibitor", "beta lactamase", "macrolide", "sulfone", "tetracycline",
        "rifamycin", "nitrofuran", "pyrethroid", "quinolone", "penem", "glycopeptide", "lipopeptide",
        "lincosamide", "oxazolidinone", "polymyxin", "amphenicol", "monobactam", "pleuromutilin",
        "nitroimidazole", "sulfonamide", "echinocandin", "azole", "polyene", "allylamine", "oxaborole",
        "diarylquinoline", "pyridone", "aminoglycoside", "antiseptic", "pyrophosphate analog", "ccr5",
        "capsid inhibitor", "bacillus calmette", "dihydrofolate reductase inhibitor anti", "dehydropeptidase"
    ),
    DrugGroup.ONCOLOGY_IMMUNOLOGY to listOf(
        "kinase inhibitor", "alkylating", "antimetabolite", "anthracycline", "microtubule", "vinca",
        "platinum", "topoisomerase", "nucleoside metabolic", "folate analog", "hedgehog",
        "histone deacetylase", "proteasome", "poly(adp", "bcl-2", "menin", "isocitrate", "farnesyl",
        "gamma secretase", "androgen receptor inhibitor", "aromatase", "estrogen receptor antagonist",
        "cd20", "cd22", "cd30", "cd33", "cd52", "ctla-4", "programmed death", "her2", "epidermal growth",
        "vascular endothelial", "bispecific", "nuclear export", "methyltransferase", "methylating",
        "thalidomide", "slamf7", "asparagine", "tubulin", "purine antimetabolite", "radioactive alpha",
        "cytochrome p450 17a1", "telomerase", "actinomycin", "cytoprotective", "tumor necrosis",
        "interleukin", "calcineurin", "immunosuppressant", "antirheumatic", "sphingosine", "integrin",
        "complement", "b lymphocyte", "selective t cell", "lymphocyte function", "interferon", "anti-ige",
        "tyrosine kinase 2", "immunoglobulin", "mtor", "pyrimidine synthesis",
        "glycolipid disialoganglioside", "hypoxia-inducible factor inhibitor", "decorporation",
        "antisense", "small interfering"
    ),
    DrugGroup.BLOOD to listOf(
        "anticoagul", "anti-coagulant", "factor xa", "heparin", "thrombin", "p2y12", "platelet",
        "vitamin k antagonist", "antifibrinolytic", "thrombopoietin", "erythropoiesis", "iron chelator",
        "parenteral iron", "coagulation factor", "blood viscosity", "plasma volume", "hematopoietic",
        "leukocyte growth", "lymphocyte growth", "hypoxia-inducible factor prolyl", "antithrombin",
        "cxc chemokine"
    ),
    DrugGroup.CARDIOVASCULAR to listOf(
        "angiotensin", "beta-adrenergic blocker", "alpha-adrenergic blocker", "calcium channel blocker",
        "diuretic", "aldosterone antagonist", "antiarrhythmic", "cardiac glycoside", "cardiac myosin",
        "nitrate", "vasodilator", "endothelin", "soluble guanylate", "prostacycl", "renin inhibitor",
        "central alpha-2", "vasoconstrictor", "anti-anginal", "hyperpolarization",
        "potassium channel blocker", "hmg-coa", "pcsk9", "cholesterol absorption",
        "bile acid sequestrant", "microsomal triglyceride", "citrate lyase",
        "peroxisome proliferator-activated receptor alpha", "nicotinic acid", "phosphodiesterase 3",
        "catecholamine", "alpha-1 adrenergic agonist", "adenosine receptor", "arteriolar", "bradykinin",
        "osmotic diuretic"
    ),
    DrugGroup.RESPIRATORY_ALLERGY to listOf(
        "beta2-adrenergic agonist", "beta-adrenergic agonist", "leukotriene", "histamine-1",
        "antihistamine", "mast cell", "expectorant", "mucolytic", "antitussive", "phosphodiesterase 4",
        "methylxanthine", "surfactant", "respiratory stimulant", "cystic fibrosis", "deoxyribonuclease",
        "allergen", "allergenic", "skin test"
    ),
    DrugGroup.GI_HEPATIC to listOf(
        "proton pump", "histamine-2", "antiemetic", "serotonin-3", "serotonin-4", "neurokinin-1",
        "antidiarrheal", "osmotic laxative", "guanylate cyclase-c", "ileal bile acid", "bile acid",
        "cholecystokinin", "glp-2", "chloride channel", "sodium-hydrogen exchanger",
        "dopamine-2 receptor antagonist", "aminosalicylate", "gastrointestinal motility",
        "prostaglandin e1", "helicobacter", "sucrose-specific", "calculi",
        "thyroid hormone receptor beta", "plasma kallikrein"
    ),
    DrugGroup.ENDOCRINE_METABOLIC to listOf(
        "anorectic", "insulin", "glp-1", "glucose-dependent", "biguanide", "sulfonylurea",
        "dipeptidyl peptidase", "sodium-glucose", "glinide", "alpha-glucosidase", "amylin",
        "peroxisome proliferator", "thyroid", "thyroxine", "triiodothyronine", "corticosteroid",
        "adrenocorticotropic", "cortisol", "adrenal steroid", "estrogen", "progest", "androgen",
        "gonadotropin", "growth hormone", "somatostatin", "parathyroid", "calcitonin", "calcium-sensing",
        "vitamin", "bisphosphonate", "rank ligand", "vasopressin", "antihypoglycemic", "leptin",
        "melanocortin", "natriuretic", "calcium", "phosphate binder", "potassium binder",
        "hydrolytic lysosomal", "glucosylceramide", "phenylalanine", "carbamoyl", "cystine", "uric acid",
        "xanthine oxidase", "carnitine", "medium-chain", "amino acid", "copper chelator", "lead chelator",
        "intestinal lipase", "aryl hydrocarbon", "pyruvate kinase", "alkaline phosphatase",
        "enzyme stabilizer", "aldehyde dehydrogenase", "cyclic pyranopterin"
    ),
    DrugGroup.CNS_PAIN to listOf(
        "opioid", "nonsteroidal", "benzodiazepine", "antipsychotic", "antidepressant", "serotonin reuptake",
        "serotonin and norepinephrine", "monoamine oxidase", "anti-epileptic", "barbiturate", "anesthetic",
        "muscle relaxant", "neuromuscular", "central nervous system", "amphetamine",
        "cholinesterase inhibitor", "dopamine", "dopaminergic", "catechol-o-methyltransferase",
        "aromatic amino acid", "n-methyl-d-aspartate", "orexin", "melatonin", "gamma-aminobutyric",
        "serotonin-1b", "calcitonin gene", "ergot", "mood stabilizer", "phenothiazine", "cannabinoid",
        "vesicular monoamine", "aminoketone", "norepinephrine reuptake", "cholinergic nicotinic", "ampa",
        "neuroactive steroid", "n-type calcium", "histamine-3", "tricyclic", "sigma-1"
    )
)

/** Maps a drugClass string to one broad group. First matching rule wins; no class means OTHER. */
fun drugGroupOf(drugClass: String?): DrugGroup {
    if (drugClass.isNullOrBlank()) return DrugGroup.OTHER
    val s = drugClass.lowercase()
    for ((group, keys) in groupRules) {
        if (keys.any { s.contains(it) }) return group
    }
    return DrugGroup.OTHER
}
