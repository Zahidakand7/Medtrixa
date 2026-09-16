package com.medapp.tools

import com.medtrixa.app.R

object DrugToolsRepository {

    fun getTools(): List<DrugTool> = listOf(
        DrugTool(
            id = "dosage_calculator",
            title = "Dosage Calculator",
            subtitle = "Weight- and BSA-based dose calculations",
            iconRes = R.drawable.ic_calculator
        ),
        DrugTool(
            id = "renal_adjustment",
            title = "Renal Dose Adjustment",
            subtitle = "Cockcroft-Gault and CKD-EPI based adjustments",
            iconRes = R.drawable.ic_kidney
        ),
        DrugTool(
            id = "interaction_checker",
            title = "Drug Interaction Checker",
            subtitle = "Check interactions between two or more drugs",
            iconRes = R.drawable.ic_warning
        ),
        DrugTool(
            id = "pediatric_dosing",
            title = "Pediatric Dosing",
            subtitle = "Weight-based pediatric dose ranges",
            iconRes = R.drawable.ic_child
        ),
        DrugTool(
            id = "iv_infusion",
            title = "IV Infusion Rate Calculator",
            subtitle = "Convert dose, rate, and concentration",
            iconRes = R.drawable.ic_drip
        ),
        DrugTool(
            id = "opioid_conversion",
            title = "Opioid Conversion Calculator",
            subtitle = "Morphine milligram equivalents (MME)",
            iconRes = R.drawable.ic_pills
        ),
        DrugTool(
            id = "anticoagulation",
            title = "Anticoagulation Dosing",
            subtitle = "Warfarin and heparin nomogram support",
            iconRes = R.drawable.ic_blood
        ),
        DrugTool(
            id = "antibiotic_guide",
            title = "Antibiotic Dosing Guide",
            subtitle = "Empiric dosing by indication and organism",
            iconRes = R.drawable.ic_book
        )
    )
}
