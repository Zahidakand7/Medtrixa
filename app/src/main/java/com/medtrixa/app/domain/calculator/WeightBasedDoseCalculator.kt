package com.medtrixa.app.domain.calculator

import com.medtrixa.app.data.model.CalculatorResult
import kotlin.math.round

class WeightBasedDoseCalculator : CalculatorEngine {

    override fun calculate(
        calculatorId: String,
        inputs: Map<String, Double>
    ): CalculatorResult {

        require(calculatorId == "weight_based_dose") {
            "Unsupported calculator: $calculatorId"
        }

        val weightKg = inputs["weight_kg"] ?: error("Weight is required")
        val dosePerKg = inputs["dose_per_kg"] ?: error("Dose per kg is required")
        val maxDoseMg = inputs["max_dose_mg"]

        require(weightKg > 0 && dosePerKg > 0) { "Values must be greater than zero" }
        require(maxDoseMg == null || maxDoseMg > 0) { "Max dose must be greater than zero" }

        val calculatedDose = weightKg * dosePerKg
        val cappedAtMax = maxDoseMg != null && calculatedDose > maxDoseMg
        val finalDose = if (cappedAtMax) maxDoseMg!! else calculatedDose
        val rounded = round(finalDose * 100) / 100

        val interpretation = if (cappedAtMax) {
            "Calculated dose (${String.format("%.2f", calculatedDose)} mg) exceeded the maximum " +
                "dose and was capped at ${String.format("%.2f", maxDoseMg)} mg"
        } else {
            "Weight-based dose calculated as dose/kg × weight"
        }

        return CalculatorResult(
            value = String.format("%.2f", rounded),
            unit = "mg",
            interpretation = interpretation
        )
    }
}
