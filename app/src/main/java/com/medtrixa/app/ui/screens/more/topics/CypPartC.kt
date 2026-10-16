package com.medtrixa.app.ui.screens.more.topics

import com.medtrixa.app.ui.screens.more.*

internal val cypC: List<TopicSection> = listOf(
    TopicSection("Time Course and Clinical Approach", listOf(
        TopicBlock.Bullets(listOf(
            "Identify the pathway: Find which enzymes clear each drug, and which drugs inhibit or induce them.",
            "Check the margin: Narrow therapeutic index substrates such as warfarin, tacrolimus, cyclosporine, theophylline and phenytoin need the closest attention.",
            "Consider prodrugs: An inhibitor lowers the effect of a prodrug such as clopidogrel, codeine or tamoxifen.",
            "Inhibition: Reversible inhibition starts quickly, and its duration follows the half-life of the inhibitor. Mechanism-based inhibitors such as clarithromycin and diltiazem can persist for days after stopping.",
            "Induction: Takes about 1 - 2 weeks to reach full effect, and fades over 1 - 4 weeks after the inducer is stopped.",
            "Act on the pair: Choose an alternative without the interaction, adjust the dose, or monitor levels or clinical effect.",
            "Look beyond CYP: P-glycoprotein, OATP transporters, UGT enzymes and renal transporters cause interactions too."
        ))
    ), TopicAccent.GREEN),
    TopicSection("Pharmacogenetics", listOf(
        TopicBlock.Bullets(listOf(
            "CYP2D6: Genotype ranges from poor to ultrarapid metabolizer. Ultrarapid metabolizers form more morphine from codeine and tramadol, and the FDA warns against codeine use in children and nursing mothers.",
            "CYP2C19: Poor metabolizers have reduced clopidogrel activation and higher exposure to citalopram, escitalopram and voriconazole.",
            "CYP2C9: Variant alleles *2 and *3 lower warfarin and phenytoin clearance and call for lower starting doses.",
            "Guidance: Consult the Clinical Pharmacogenetics Implementation Consortium (CPIC) guidelines when genotype results are available."
        )),
        TopicBlock.Callout(
            "Disclaimer",
            "This guide is an educational reference. The lists give examples classified by the FDA and published tables and are updated as new data appear. Consult the current prescribing information, an interaction database and a pharmacist before changing therapy.",
            CalloutKind.INFO
        )
    ), TopicAccent.ROSE)
)
