package com.medtrixa.app.ui.screens

import androidx.compose.runtime.Composable
import com.medtrixa.app.ui.screens.calculators.AdultBmiScreen
import com.medtrixa.app.ui.screens.calculators.ChildBmiScreen
import com.medtrixa.app.ui.screens.calculators.BsaScreen
import com.medtrixa.app.ui.screens.calculators.CreatinineClearanceScreen

@Composable
fun CalculatorScreen(
    calculatorId: String,
    onBackClick: () -> Unit
) {
    when (calculatorId) {
        "bmi_child" -> ChildBmiScreen(
            onBackClick = onBackClick
        )

        "bsa" -> BsaScreen(
            onBackClick = onBackClick
        )

        "creatinine_clearance" -> CreatinineClearanceScreen(
            onBackClick = onBackClick
        )

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
