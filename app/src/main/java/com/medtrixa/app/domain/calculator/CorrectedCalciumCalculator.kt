package com.medtrixa.app.domain.calculator

import com.medtrixa.app.data.model.CalculatorResult
import kotlin.math.round

class CorrectedCalciumCalculator : CalculatorEngine {
    override fun calculate(calculatorId: String, inputs: Map<String, Double>): CalculatorResult {
        require(calculatorId == "corrected_calcium") { "Unsupported calculator: $calculatorId" }
        val calcium = inputs["calcium"] ?: error("Calcium is required")
        val albumin = inputs["albumin"] ?: error("Albumin is required")

        val corrected = calcium + 0.8 * (4.0 - albumin)
        val rounded = round(corrected * 100) / 100

        val interpretation = when {
            rounded < 8.5 -> "Low (hypocalcemia, corrected)"
            rounded <= 10.5 -> "Normal (corrected)"
            else -> "High (hypercalcemia, corrected)"
        }

        return CalculatorResult(
            value = String.format("%.2f", rounded),
            unit = "mg/dL",
            interpretation = interpretation
        )
    }
}
