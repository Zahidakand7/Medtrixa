package com.medtrixa.app.domain.calculator

import com.medtrixa.app.data.model.CalculatorResult
import kotlin.math.round

class MapCalculator : CalculatorEngine {

    override fun calculate(
        calculatorId: String,
        inputs: Map<String, Double>
    ): CalculatorResult {

        require(calculatorId == "map") {
            "Unsupported calculator: $calculatorId"
        }

        val sbp = inputs["sbp"] ?: error("Systolic BP is required")
        val dbp = inputs["dbp"] ?: error("Diastolic BP is required")

        require(sbp > 0 && dbp > 0) { "Values must be greater than zero" }
        require(sbp >= dbp) { "Systolic must be greater than or equal to diastolic" }

        val map = dbp + (sbp - dbp) / 3.0
        val rounded = round(map * 10) / 10

        val interpretation = when {
            rounded < 65 -> "Low — below organ perfusion threshold (<65 mmHg)"
            rounded in 65.0..100.0 -> "Normal perfusion range"
            else -> "Elevated"
        }

        return CalculatorResult(
            value = String.format("%.1f", rounded),
            unit = "mmHg",
            interpretation = interpretation
        )
    }
}
