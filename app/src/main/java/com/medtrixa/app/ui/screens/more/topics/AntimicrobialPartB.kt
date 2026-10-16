package com.medtrixa.app.ui.screens.more.topics

import com.medtrixa.app.ui.screens.more.*

internal val antimicrobialB: List<TopicSection> = listOf(
    TopicSection("MRSA and Resistant Gram-Positive Agents", listOf(
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("Vancomycin", "Glycopeptide", "Gram-positive: MRSA, MSSA (less effective than beta-lactams), coagulase-negative staphylococci, streptococci and Enterococcus faecalis\nGram-negative: None\nAnaerobes: Oral vancomycin treats Clostridioides difficile\nGaps: VRE and all Gram-negatives", "Monitor with AUC-guided dosing, and watch for nephrotoxicity and infusion reactions."),
            AbbrevRow("Daptomycin", "Lipopeptide", "Gram-positive: MRSA, VRE and streptococci\nGram-negative: None\nGaps: Pneumonia, because surfactant inactivates it", "Monitor creatine kinase, and report muscle pain."),
            AbbrevRow("Linezolid", "Oxazolidinone", "Gram-positive: MRSA, VRE and streptococci\nOther: Nocardia and some nontuberculous mycobacteria\nGram-negative: None", "Fully bioavailable by mouth, with good lung and CNS penetration. It can cause serotonin syndrome and myelosuppression beyond 2 weeks.")
        ))
    ), TopicAccent.BLUE),
    TopicSection("Aminoglycosides", listOf(
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("Gentamicin, tobramycin, amikacin", "", "Gram-negative: Enterobacterales and Pseudomonas aeruginosa, with tobramycin and amikacin the more active\nGram-positive: Synergy with cell-wall agents against staphylococci, streptococci and enterococci\nAnaerobes: None\nGaps: Anaerobes, Stenotrophomonas, and as a single agent for streptococci and enterococci", "Nephrotoxic and ototoxic. Penetration is poor in the lung, in acidic abscesses and under anaerobic conditions.")
        ))
    ), TopicAccent.VIOLET),
    TopicSection("Fluoroquinolones", listOf(
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("Ciprofloxacin", "", "Gram-negative: Enterobacterales and Pseudomonas aeruginosa, the most active quinolone\nGram-positive: Weak streptococcal activity\nAtypicals: Legionella, though levofloxacin is preferred\nGaps: MRSA, anaerobes and Streptococcus pneumoniae"),
            AbbrevRow("Levofloxacin", "", "Gram-positive: Streptococcus pneumoniae and some MSSA\nGram-negative: Enterobacterales, Haemophilus, Moraxella and Pseudomonas\nAtypicals: Legionella, Mycoplasma and Chlamydophila\nGaps: MRSA, anaerobes and reliable Enterococcus activity"),
            AbbrevRow("Moxifloxacin", "", "Gram-positive: Streptococcus pneumoniae and MSSA\nGram-negative: Enterobacterales and Haemophilus\nAnaerobes: Some activity, but unreliable against Bacteroides fragilis\nAtypicals: Legionella, Mycoplasma and Chlamydophila\nGaps: Pseudomonas, MRSA and the urinary tract", "Class warnings: tendon rupture, QT prolongation, aortic aneurysm and dissection, CNS effects and Clostridioides difficile infection. Cations such as calcium and iron reduce absorption.")
        ))
    ), TopicAccent.ROSE),
    TopicSection("Macrolides and Tetracyclines", listOf(
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("Macrolides", "Azithromycin, clarithromycin", "Gram-positive: Streptococci and MSSA, with high streptococcal resistance\nGram-negative: Haemophilus influenzae, Moraxella and Bordetella\nAtypicals: Mycoplasma, Chlamydophila and Legionella\nOther: Mycobacterium avium complex\nGaps: Enterobacterales, Pseudomonas, MRSA and most anaerobes", "QT prolongation. Clarithromycin is a strong CYP3A4 inhibitor."),
            AbbrevRow("Doxycycline, minocycline", "Tetracyclines", "Gram-positive: Community MRSA, with variable activity\nAtypicals: Mycoplasma, Chlamydophila and Legionella\nOther: Rickettsia, Borrelia, Anaplasma, Ehrlichia, Vibrio, Brucella and Bacillus anthracis\nGaps: Pseudomonas, Enterobacterales and Enterococcus", "Minocycline also has activity against some Acinetobacter and Stenotrophomonas."),
            AbbrevRow("Tigecycline", "Glycylcycline", "Gram-positive: MRSA and VRE\nGram-negative: Enterobacterales including CRE, and Acinetobacter\nAnaerobes: Yes\nGaps: Pseudomonas, Proteus and Morganella", "Serum levels are low, so avoid it for bacteremia. It carries a boxed warning for higher mortality.")
        ))
    ), TopicAccent.AMBER),
    TopicSection("Other Antibacterials", listOf(
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("Trimethoprim-sulfamethoxazole", "", "Gram-positive: Community MRSA and MSSA\nGram-negative: Enterobacterales with rising resistance, Haemophilus and Stenotrophomonas maltophilia\nOther: Pneumocystis, Nocardia and Toxoplasma\nGaps: Pseudomonas, anaerobes, Enterococcus and group A streptococci", "Can cause hyperkalemia, a rise in creatinine and myelosuppression, and interacts with warfarin."),
            AbbrevRow("Clindamycin", "", "Gram-positive: MSSA, streptococci and some MRSA\nAnaerobes: Above the diaphragm, with rising Bacteroides resistance\nGram-negative: None\nGaps: Enterococcus and Gram-negative rods", "Suppresses toxin production in staphylococcal and streptococcal toxic shock. High risk of Clostridioides difficile infection."),
            AbbrevRow("Metronidazole", "", "Anaerobes: Bacteroides fragilis, Clostridium and Fusobacterium\nOther: Trichomonas, Giardia and Entamoeba\nGaps: All aerobes, Actinomyces and Cutibacterium", "Good CNS penetration. Prolonged use can cause neuropathy."),
            AbbrevRow("Nitrofurantoin", "", "Gram-positive: Enterococcus faecalis and Staphylococcus saprophyticus\nGram-negative: Most E. coli\nGaps: Proteus, Pseudomonas, Serratia and variable Klebsiella", "Lower urinary tract only. Avoid when creatinine clearance is below 30 mL/min."),
            AbbrevRow("Fosfomycin", "", "Gram-positive: Enterococcus faecalis\nGram-negative: E. coli, including many ESBL producers\nGaps: Pseudomonas and variable Klebsiella", "A single oral 3 g dose treats uncomplicated cystitis.")
        ))
    ), TopicAccent.TEAL),
    TopicSection("Last-Resort Agents", listOf(
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("Colistin, polymyxin B", "Polymyxins", "Gram-negative: Pseudomonas aeruginosa, Acinetobacter and CRE\nGaps: Proteus, Serratia, Providencia, Morganella, Burkholderia, Gram-positives and anaerobes", "Nephrotoxic and neurotoxic, with poor lung penetration.")
        ))
    ), TopicAccent.RED),
    TopicSection("Antifungal Spectrum", listOf(
        TopicBlock.Text("Identification to species level matters, because Candida glabrata and Candida krusei differ in azole susceptibility."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("Fluconazole", "Azole", "Candida: C. albicans, C. parapsilosis and C. tropicalis\nOther: Cryptococcus and Coccidioides\nGaps: C. krusei (intrinsic resistance), reduced susceptibility in C. glabrata, and all molds", "QT prolongation and many CYP interactions."),
            AbbrevRow("Voriconazole", "Azole", "Candida: Most species, including C. krusei\nMolds: Aspergillus, where it is first-line\nGaps: Mucorales", "Monitor levels. It can cause visual symptoms and hepatotoxicity."),
            AbbrevRow("Posaconazole, isavuconazole", "Azoles", "Candida: Most species\nMolds: Aspergillus and Mucorales", "Active against Mucorales, unlike voriconazole."),
            AbbrevRow("Echinocandins", "Caspofungin, micafungin, anidulafungin", "Candida: All species, including C. glabrata, C. krusei and C. auris, and first-line for candidemia\nMolds: Aspergillus, as adjunct or salvage\nGaps: Cryptococcus, Fusarium and Mucorales", "Poor penetration into urine and the CNS."),
            AbbrevRow("Amphotericin B", "Liposomal formulation preferred", "Candida: Most species, except some C. lusitaniae\nMolds: Aspergillus except A. terreus, and Mucorales\nOther: Cryptococcus induction therapy\nGaps: Aspergillus terreus and Scedosporium", "Nephrotoxicity and infusion reactions, with less toxicity for the liposomal form.")
        ))
    ), TopicAccent.GREEN)
)
