package com.medtrixa.app.domain.calculator

import com.medtrixa.app.data.model.CalculatorResult
import kotlin.math.round

class CorrectedSodiumCalculator : CalculatorEngine {
    override fun calculate(calculatorId: String, inputs: Map<String, Double>): CalculatorResult {
        require(calculatorId == "corrected_sodium") { "Unsupported calculator: $calculatorId" }
        val sodium = inputs["sodium"] ?: error("Sodium is required")
        val glucose = inputs["glucose"] ?: error("Glucose is required")
        require(glucose > 0) { "Glucose must be greater than zero" }

        val corrected = sodium + 1.6 * ((glucose - 100.0) / 100.0)
        val rounded = round(corrected * 10) / 10

        val interpretation = when {
            rounded < 135 -> "Corrected hyponatremia"
            rounded <= 145 -> "Normal (corrected)"
            else -> "Corrected hypernatremia"
        }

        return CalculatorResult(
            value = String.format("%.1f", rounded),
            unit = "mEq/L",
            interpretation = interpretation
        )
    }
}
