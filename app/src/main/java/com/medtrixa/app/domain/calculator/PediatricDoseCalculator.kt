package com.medtrixa.app.domain.calculator

import com.medtrixa.app.data.model.CalculatorResult
import kotlin.math.round

class PediatricDoseCalculator : CalculatorEngine {

    override fun calculate(
        calculatorId: String,
        inputs: Map<String, Double>
    ): CalculatorResult {

        require(calculatorId == "pediatric_dose") {
            "Unsupported calculator: $calculatorId"
        }

        val weightKg = inputs["weight_kg"] ?: error("Weight is required")
        val adultDoseMg = inputs["adult_dose_mg"] ?: error("Adult dose is required")

        require(weightKg > 0 && adultDoseMg > 0) { "Values must be greater than zero" }

        val childDose = (weightKg / 70.0) * adultDoseMg
        val rounded = round(childDose * 100) / 100

        val interpretation = "Estimated pediatric dose from adult dose using Clark's Rule " +
            "(average adult weight 70 kg) — confirm against weight-based or formulary dosing"

        return CalculatorResult(
            value = String.format("%.2f", rounded),
            unit = "mg",
            interpretation = interpretation
        )
    }
}
