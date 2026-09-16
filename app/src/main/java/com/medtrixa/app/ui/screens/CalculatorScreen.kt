package com.medtrixa.app.ui.screens

import androidx.compose.runtime.Composable
import com.medtrixa.app.ui.screens.calculators.AdultBmiScreen
import com.medtrixa.app.ui.screens.calculators.ChildBmiScreen
import com.medtrixa.app.ui.screens.calculators.BsaScreen
import com.medtrixa.app.ui.screens.calculators.CreatinineClearanceScreen
import com.medtrixa.app.ui.screens.calculators.MapScreen
import com.medtrixa.app.ui.screens.calculators.ShockIndexScreen
import com.medtrixa.app.ui.screens.calculators.AnionGapScreen
import com.medtrixa.app.ui.screens.calculators.CorrectedCalciumScreen
import com.medtrixa.app.ui.screens.calculators.QtcScreen
import com.medtrixa.app.ui.screens.calculators.PfRatioScreen
import com.medtrixa.app.ui.screens.calculators.CorrectedSodiumScreen
import com.medtrixa.app.ui.screens.calculators.AaGradientScreen
import com.medtrixa.app.ui.screens.calculators.EgfrScreen
import com.medtrixa.app.ui.screens.calculators.BunCreatinineRatioScreen
import com.medtrixa.app.ui.screens.calculators.IvDripRateScreen
import com.medtrixa.app.ui.screens.calculators.InfusionRateScreen
import com.medtrixa.app.ui.screens.calculators.PediatricDoseScreen
import com.medtrixa.app.ui.screens.calculators.WeightBasedDoseScreen

@Composable
fun CalculatorScreen(
    calculatorId: String,
    onBackClick: () -> Unit
) {
    when (calculatorId) {
        "bmi_child" -> ChildBmiScreen(onBackClick = onBackClick)
        "bsa" -> BsaScreen(onBackClick = onBackClick)
        "creatinine_clearance" -> CreatinineClearanceScreen(onBackClick = onBackClick)
        "map" -> MapScreen(onBackClick = onBackClick)
        "shock_index" -> ShockIndexScreen(onBackClick = onBackClick)
        "anion_gap" -> AnionGapScreen(onBackClick = onBackClick)
        "corrected_calcium" -> CorrectedCalciumScreen(onBackClick = onBackClick)
        "qtc" -> QtcScreen(onBackClick = onBackClick)
        "pf_ratio" -> PfRatioScreen(onBackClick = onBackClick)
        "corrected_sodium" -> CorrectedSodiumScreen(onBackClick = onBackClick)
        "aa_gradient" -> AaGradientScreen(onBackClick = onBackClick)
        "egfr" -> EgfrScreen(onBackClick = onBackClick)
        "bun_creatinine_ratio" -> BunCreatinineRatioScreen(onBackClick = onBackClick)
        "iv_drip_rate" -> IvDripRateScreen(onBackClick = onBackClick)
        "infusion_rate" -> InfusionRateScreen(onBackClick = onBackClick)
        "pediatric_dose" -> PediatricDoseScreen(onBackClick = onBackClick)
        "weight_based_dose" -> WeightBasedDoseScreen(onBackClick = onBackClick)

        "bmi_adult" -> AdultBmiScreen(
            calculatorId = calculatorId,
            onBackClick = onBackClick
        )

        else -> AdultBmiScreen(
            calculatorId = calculatorId,
            onBackClick = onBackClick
        )
    }
}
