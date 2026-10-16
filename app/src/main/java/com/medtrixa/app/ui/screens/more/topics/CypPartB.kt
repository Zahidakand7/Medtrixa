package com.medtrixa.app.ui.screens.more.topics

import com.medtrixa.app.ui.screens.more.*

internal val cypB: List<TopicSection> = listOf(
    TopicSection("CYP2C9 and CYP2C19", listOf(
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("CYP2C9 substrates", "", "Warfarin (S-enantiomer), phenytoin, celecoxib, ibuprofen, naproxen, diclofenac, glipizide, glimepiride, glyburide, tolbutamide, losartan"),
            AbbrevRow("CYP2C9 inhibitors", "", "Fluconazole, amiodarone, miconazole, voriconazole, metronidazole, trimethoprim-sulfamethoxazole"),
            AbbrevRow("CYP2C9 inducers", "", "Rifampin, carbamazepine, phenobarbital, enzalutamide"),
            AbbrevRow("CYP2C19 sensitive substrates", "", "Omeprazole, lansoprazole, clobazam, S-mephenytoin"),
            AbbrevRow("CYP2C19 other substrates", "", "Clopidogrel, citalopram, escitalopram, sertraline, diazepam, voriconazole, phenytoin, pantoprazole, esomeprazole"),
            AbbrevRow("CYP2C19 strong inhibitors", "", "Fluconazole, fluoxetine, fluvoxamine, ticlopidine"),
            AbbrevRow("CYP2C19 inducers", "", "Rifampin, carbamazepine, enzalutamide, St John's wort")
        )),
        TopicBlock.Bullets(listOf(
            "Warfarin: INR rises with fluconazole, amiodarone and metronidazole, and falls with rifampin and carbamazepine, so check the INR within a few days of any change.",
            "Phenytoin: Narrow therapeutic index, so inhibitors can cause toxicity.",
            "Sulfonylureas: Higher exposure with CYP2C9 inhibitors increases the risk of hypoglycemia.",
            "Losartan: A prodrug activated by CYP2C9, so inhibitors can reduce its effect.",
            "Clopidogrel: A prodrug activated by CYP2C19. Poor metabolizers and omeprazole or esomeprazole reduce the antiplatelet effect, so avoid that combination.",
            "Citalopram and escitalopram: Lower doses are recommended in CYP2C19 poor metabolizers.",
            "Voriconazole: Levels vary with CYP2C19 genotype, so monitor levels. Rifampin and rifabutin reduce exposure."
        ))
    ), TopicAccent.AMBER),
    TopicSection("CYP1A2", listOf(
        TopicBlock.Text("Induced by tobacco smoke and inhibited by several antibiotics and antidepressants. Dose changes are often needed when smoking status changes."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("Sensitive substrates", "", "Caffeine, tizanidine, melatonin, duloxetine, ramelteon, alosetron"),
            AbbrevRow("Other substrates", "", "Theophylline, clozapine, olanzapine, ropinirole, R-warfarin"),
            AbbrevRow("Strong inhibitors", "", "Fluvoxamine, ciprofloxacin, enoxacin"),
            AbbrevRow("Moderate inhibitors", "", "Oral contraceptives, mexiletine, zileuton"),
            AbbrevRow("Inducers", "", "Tobacco smoke, rifampin, phenytoin, carbamazepine, omeprazole")
        )),
        TopicBlock.Bullets(listOf(
            "Tizanidine: Contraindicated with fluvoxamine and ciprofloxacin because of severe hypotension and sedation.",
            "Clozapine and olanzapine: Levels fall in smokers, and rise after smoking cessation, so monitor and adjust.",
            "Theophylline: Narrow therapeutic index, so ciprofloxacin and fluvoxamine can cause toxicity."
        ))
    ), TopicAccent.TEAL),
    TopicSection("CYP2C8 and CYP2B6", listOf(
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("CYP2C8 substrates", "", "Repaglinide, paclitaxel, pioglitazone, rosiglitazone"),
            AbbrevRow("CYP2C8 inhibitors", "", "Gemfibrozil (strong), clopidogrel (moderate)"),
            AbbrevRow("CYP2C8 inducers", "", "Rifampin"),
            AbbrevRow("CYP2B6 substrates", "", "Bupropion, efavirenz, methadone, cyclophosphamide"),
            AbbrevRow("CYP2B6 inhibitors", "", "Clopidogrel, ticlopidine"),
            AbbrevRow("CYP2B6 inducers", "", "Efavirenz, rifampin, carbamazepine")
        )),
        TopicBlock.Bullets(listOf(
            "Gemfibrozil and repaglinide: Contraindicated, because of a large rise in repaglinide exposure and the risk of hypoglycemia."
        ))
    ), TopicAccent.BLUE)
)
