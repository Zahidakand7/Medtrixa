package com.medtrixa.app.ui.screens.more.topics

import com.medtrixa.app.ui.screens.more.*

internal val antimicrobialA: List<TopicSection> = listOf(
    TopicSection("How to Read This Guide", listOf(
        TopicBlock.Text("This guide summarizes the usual spectrum of antibacterial and antifungal classes against common pathogens. Susceptibility varies by region, hospital and isolate, so always use the local antibiogram and culture results. Examples are not complete lists."),
        TopicBlock.Bullets(listOf(
            "Gram-positive: Staphylococci, streptococci and enterococci.",
            "Gram-negative: Enterobacterales, Pseudomonas aeruginosa, Acinetobacter and Haemophilus.",
            "Anaerobes: Including Bacteroides fragilis, Clostridium and Fusobacterium.",
            "Atypicals: Mycoplasma, Chlamydophila and Legionella.",
            "MSSA and MRSA: Methicillin-susceptible and methicillin-resistant Staphylococcus aureus.",
            "ESBL: Extended-spectrum beta-lactamase producers, usually Escherichia coli or Klebsiella.",
            "AmpC: Inducible cephalosporinases in Enterobacter cloacae, Klebsiella aerogenes, Citrobacter freundii and Serratia.",
            "CRE: Carbapenem-resistant Enterobacterales, such as those producing KPC or metallo-beta-lactamases."
        ))
    ), TopicAccent.BLUE),
    TopicSection("Penicillins", listOf(
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("Natural penicillins", "Penicillin G, penicillin V", "Gram-positive: Streptococci, many penicillin-susceptible pneumococci, and some Enterococcus faecalis\nGram-negative: Neisseria meningitidis\nSpirochetes: Treponema pallidum\nAnaerobes: Mouth flora above the diaphragm\nGaps: Staphylococcus aureus, most Gram-negative rods and Bacteroides fragilis", "First-line for syphilis (benzathine penicillin G) and group A streptococcal pharyngitis."),
            AbbrevRow("Aminopenicillins", "Amoxicillin, ampicillin", "Gram-positive: Streptococci, Enterococcus faecalis and Listeria monocytogenes\nGram-negative: Susceptible Escherichia coli, Proteus mirabilis and Haemophilus influenzae\nAnaerobes: Mouth flora\nGaps: Beta-lactamase producers including MSSA, Klebsiella and Moraxella, and Pseudomonas"),
            AbbrevRow("Aminopenicillin with beta-lactamase inhibitor", "Amoxicillin-clavulanate, ampicillin-sulbactam", "Gram-positive: Streptococci, MSSA and Enterococcus faecalis\nGram-negative: Beta-lactamase-producing Haemophilus and Moraxella, and many E. coli, Klebsiella and Proteus\nAnaerobes: Including Bacteroides fragilis\nGaps: MRSA, Pseudomonas, ESBL and AmpC producers", "Sulbactam also has intrinsic activity against Acinetobacter baumannii."),
            AbbrevRow("Antistaphylococcal penicillins", "Nafcillin, oxacillin, dicloxacillin", "Gram-positive: MSSA and streptococci\nGram-negative: None\nAnaerobes: None\nGaps: MRSA, Enterococcus and all Gram-negative rods", "Standard therapy for MSSA bacteremia, along with cefazolin."),
            AbbrevRow("Antipseudomonal penicillin", "Piperacillin-tazobactam", "Gram-positive: Streptococci, MSSA and Enterococcus faecalis\nGram-negative: Broad Enterobacterales coverage and Pseudomonas aeruginosa\nAnaerobes: Including Bacteroides fragilis\nGaps: MRSA, ESBL producers, serious AmpC infections, Stenotrophomonas and atypicals", "Combined with vancomycin, it has been linked to a higher rate of acute kidney injury.")
        ))
    ), TopicAccent.AMBER),
    TopicSection("Cephalosporins", listOf(
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("First generation", "Cefazolin, cephalexin", "Gram-positive: MSSA and streptococci\nGram-negative: Community-acquired E. coli, Klebsiella pneumoniae and Proteus mirabilis\nAnaerobes: Mouth flora only\nGaps: MRSA, Enterococcus, Pseudomonas, Haemophilus, Enterobacter and Serratia", "First-line for surgical prophylaxis. Cefazolin does not reach therapeutic CNS levels."),
            AbbrevRow("Second generation", "Cefuroxime", "Gram-positive: Streptococci and MSSA\nGram-negative: Haemophilus influenzae, Moraxella and some Enterobacterales\nGaps: MRSA, Enterococcus, Pseudomonas and anaerobes"),
            AbbrevRow("Cephamycins", "Cefoxitin, cefotetan", "Gram-positive: Streptococci and MSSA\nGram-negative: E. coli, Klebsiella and Proteus\nAnaerobes: Including Bacteroides fragilis\nGaps: MRSA, Enterococcus, Pseudomonas, ESBL and AmpC producers"),
            AbbrevRow("Third generation", "Ceftriaxone, cefotaxime", "Gram-positive: Streptococci, including penicillin-resistant pneumococci, and MSSA with modest activity\nGram-negative: Enterobacterales without AmpC, Haemophilus, Neisseria and Moraxella\nAnaerobes: Limited\nGaps: MRSA, Enterococcus, Pseudomonas, ESBL and inducible AmpC producers", "Reaches the CNS and is first-line for meningitis and gonorrhea."),
            AbbrevRow("Third generation, antipseudomonal", "Ceftazidime", "Gram-positive: Weak activity against streptococci and MSSA\nGram-negative: Pseudomonas aeruginosa and Enterobacterales\nAnaerobes: None\nGaps: MRSA, Enterococcus and ESBL producers"),
            AbbrevRow("Fourth generation", "Cefepime", "Gram-positive: Streptococci and MSSA\nGram-negative: Pseudomonas aeruginosa and Enterobacterales, including many AmpC producers\nGaps: MRSA, Enterococcus, anaerobes and reliable ESBL activity", "Can cause neurotoxicity in renal impairment."),
            AbbrevRow("Fifth generation", "Ceftaroline", "Gram-positive: MRSA, MSSA and streptococci, including penicillin-resistant pneumococci\nGram-negative: Enterobacterales without ESBL, and Haemophilus\nGaps: Pseudomonas, ESBL producers, Enterococcus faecium and atypicals")
        ))
    ), TopicAccent.TEAL),
    TopicSection("Newer Beta-Lactam Combinations", listOf(
        TopicBlock.Text("Reserve these agents for multidrug-resistant infections, with infectious diseases input."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("Ceftolozane-tazobactam", "", "Gram-negative: Pseudomonas aeruginosa, including many multidrug-resistant strains, and Enterobacterales including ESBL producers\nGram-positive: Streptococci\nAnaerobes: Limited\nGaps: KPC and metallo-beta-lactamase producers, Acinetobacter, MRSA and Enterococcus"),
            AbbrevRow("Ceftazidime-avibactam", "", "Gram-negative: Enterobacterales including ESBL, AmpC, KPC and OXA-48 producers, and Pseudomonas aeruginosa\nGaps: Metallo-beta-lactamase producers (NDM, VIM, IMP), Acinetobacter, anaerobes and Gram-positives"),
            AbbrevRow("Meropenem-vaborbactam, imipenem-relebactam", "", "Gram-negative: Enterobacterales including KPC producers, and Pseudomonas aeruginosa with imipenem-relebactam\nGaps: Metallo-beta-lactamase producers and most Acinetobacter"),
            AbbrevRow("Cefiderocol", "", "Gram-negative: Broad, including Pseudomonas, Acinetobacter, Stenotrophomonas and many carbapenem-resistant strains\nGram-positive: Limited\nGaps: Reserved for resistant Gram-negative infections")
        ))
    ), TopicAccent.VIOLET),
    TopicSection("Carbapenems and Aztreonam", listOf(
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("Ertapenem", "", "Gram-positive: Streptococci and MSSA\nGram-negative: Enterobacterales, including ESBL producers\nAnaerobes: Yes\nGaps: Pseudomonas, Acinetobacter, Enterococcus and MRSA", "Convenient once-daily dosing."),
            AbbrevRow("Meropenem, imipenem-cilastatin", "", "Gram-positive: Streptococci, MSSA and Enterococcus faecalis, with imipenem the more active\nGram-negative: Enterobacterales including ESBL and AmpC producers, Pseudomonas and many Acinetobacter\nAnaerobes: Yes\nGaps: MRSA, Enterococcus faecium, Stenotrophomonas, atypicals and carbapenem-resistant organisms", "Carbapenems sharply lower valproate levels, and imipenem carries a higher seizure risk."),
            AbbrevRow("Aztreonam", "Monobactam", "Gram-negative: Aerobic Enterobacterales and Pseudomonas aeruginosa\nGram-positive: None\nAnaerobes: None", "Cross-reactivity with penicillins is negligible, but it shares a side chain with ceftazidime.")
        ))
    ), TopicAccent.RED)
)
