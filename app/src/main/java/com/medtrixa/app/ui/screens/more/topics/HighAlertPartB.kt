package com.medtrixa.app.ui.screens.more.topics

import com.medtrixa.app.ui.screens.more.*

internal val highAlertB: List<TopicSection> = listOf(
    TopicSection("Tall-Man Lettering", listOf(
        TopicBlock.Text("Tall-man lettering uses capital letters inside a drug name to emphasize the part that differs from a look-alike name. It follows the ISMP and FDA name differentiation lists, and it supports verification but never replaces it."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("buPROPion / busPIRone", "", "buPROPion: Antidepressant\nbusPIRone: Anxiolytic"),
            AbbrevRow("hydrALAZINE / hydrOXYzine", "", "hydrALAZINE: Vasodilator for hypertension\nhydrOXYzine: Antihistamine"),
            AbbrevRow("glipiZIDE / glyBURIDE", "", "glipiZIDE: Sulfonylurea with a shorter duration\nglyBURIDE: Sulfonylurea with a longer duration"),
            AbbrevRow("predniSONE / prednisoLONE", "", "predniSONE: Corticosteroid prodrug\nprednisoLONE: Active corticosteroid"),
            AbbrevRow("DOBUTamine / DOPamine", "", "DOBUTamine: Inotrope\nDOPamine: Vasopressor and inotrope with dose-dependent effects"),
            AbbrevRow("NIFEdipine / niCARdipine", "", "NIFEdipine: Calcium channel blocker, commonly oral\nniCARdipine: Calcium channel blocker, commonly an IV infusion"),
            AbbrevRow("vinBLAStine / vinCRIStine", "", "vinBLAStine: Vinca alkaloid with dose-limiting myelosuppression\nvinCRIStine: Vinca alkaloid with dose-limiting neurotoxicity\nBoth are fatal if given intrathecally"),
            AbbrevRow("metFORMIN / metroNIDAZOLE", "", "metFORMIN: Antidiabetic\nmetroNIDAZOLE: Antibiotic"),
            AbbrevRow("CARBOplatin / CISplatin", "", "CARBOplatin: Platinum agent dosed by target AUC\nCISplatin: Platinum agent dosed by body surface area, with higher nephrotoxicity"),
            AbbrevRow("DAUNOrubicin / DOXOrubicin", "", "DAUNOrubicin: Anthracycline\nDOXOrubicin: Anthracycline\nThey differ in dosing and cumulative cardiotoxicity limits"),
            AbbrevRow("hydroCODONE / oxyCODONE", "", "hydroCODONE: Opioid analgesic\noxyCODONE: Opioid analgesic\nThey are not interchangeable mg for mg"),
            AbbrevRow("HYDROmorphone / morphine", "", "HYDROmorphone: Opioid several times more potent than morphine\nmorphine: Reference opioid"),
            AbbrevRow("fentaNYL / SUFentanil", "", "fentaNYL: Opioid dosed in micrograms\nSUFentanil: More potent opioid dosed in micrograms"),
            AbbrevRow("ALPRAZolam / LORazepam", "", "ALPRAZolam: Benzodiazepine used mainly for anxiety and panic\nLORazepam: Benzodiazepine also used for seizures and sedation"),
            AbbrevRow("cefTRIAXone / cefTAZidime", "", "cefTRIAXone: Cephalosporin without antipseudomonal activity\ncefTAZidime: Cephalosporin with antipseudomonal activity"),
            AbbrevRow("TraZODone / traMADol", "", "TraZODone: Antidepressant\ntraMADol: Opioid analgesic"),
            AbbrevRow("PENTobarbital / PHENobarbital", "", "PENTobarbital: Short-acting barbiturate\nPHENobarbital: Long-acting barbiturate and anticonvulsant"),
            AbbrevRow("SUMAtriptan / sitaGLIPtin", "", "SUMAtriptan: Migraine treatment\nsitaGLIPtin: Antidiabetic"),
            AbbrevRow("lamiVUDine / lamoTRIgine", "", "lamiVUDine: Antiviral\nlamoTRIgine: Anticonvulsant"),
            AbbrevRow("ARIPiprazole / RABEprazole", "", "ARIPiprazole: Antipsychotic\nRABEprazole: Proton pump inhibitor"),
            AbbrevRow("clomiPHENE / clomiPRAMINE", "", "clomiPHENE: Ovulation inducer\nclomiPRAMINE: Antidepressant")
        ))
    ), TopicAccent.AMBER),
    TopicSection("Look-Alike Packaging and Storage", listOf(
        TopicBlock.Bullets(listOf(
            "Separate storage: Keep look-alike and sound-alike products apart, and never in adjacent bins.",
            "Shelf alerts: Add auxiliary labels or alerts at the point of selection.",
            "Brand and generic: Confirm the generic name, strength and dosage form before selecting a product.",
            "Strength and units: Read the strength per unit volume, not only the total amount in the container.",
            "Packaging changes: Recheck products after a manufacturer or supplier change."
        ))
    ), TopicAccent.TEAL),
    TopicSection("Error-Prevention Strategies", listOf(
        TopicBlock.Bullets(listOf(
            "Independent double check: Have a second clinician verify high-alert doses, calculations and pump settings independently.",
            "Standardize: Use standard concentrations, order sets and smart pump drug libraries.",
            "Limit access: Remove concentrated high-alert products from patient care areas where possible.",
            "Clear orders: Write the drug name, dose, route, frequency and indication in full, and avoid error-prone abbreviations.",
            "Read back: Repeat back and spell verbal orders, and avoid them except in emergencies.",
            "Scan and verify: Use barcode scanning at dispensing and at administration.",
            "Educate patients: Teach the name, purpose, dose and warning signs of high-alert medicines."
        ))
    ), TopicAccent.BLUE),
    TopicSection("Verification Checklist", listOf(
        TopicBlock.Bullets(listOf(
            "Right patient: Use two identifiers.",
            "Right drug: Read the full name, including tall-man letters.",
            "Right dose: Recalculate weight-based and unit-based doses.",
            "Right route: Match the product to the route, especially IV, epidural and intrathecal products.",
            "Right time and frequency: Confirm the interval and the time of the last dose.",
            "Right reason: Confirm the indication.",
            "Right documentation: Record the dose given and the patient's response."
        )),
        TopicBlock.Callout(
            "Disclaimer",
            "This guide is an educational reference. The lists reflect ISMP and FDA publications and are updated periodically, so consult the current editions and your institutional policy. Tall-man lettering reduces but does not eliminate the risk of mix-ups.",
            CalloutKind.INFO
        )
    ), TopicAccent.GREEN)
)
