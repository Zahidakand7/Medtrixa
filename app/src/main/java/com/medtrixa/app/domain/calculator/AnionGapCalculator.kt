package com.medtrixa.app.domain.calculator

import com.medtrixa.app.data.model.CalculatorResult
import kotlin.math.round

class AnionGapCalculator : CalculatorEngine {
    override fun calculate(calculatorId: String, inputs: Map<String, Double>): CalculatorResult {
        require(calculatorId == "anion_gap") { "Unsupported calculator: $calculatorId" }
        val sodium = inputs["sodium"] ?: error("Sodium is required")
        val chloride = inputs["chloride"] ?: error("Chloride is required")
        val bicarbonate = inputs["bicarbonate"] ?: error("Bicarbonate is required")

        val gap = sodium - (chloride + bicarbonate)
        val rounded = round(gap * 10) / 10

        val interpretation = when {
            rounded < 8 -> "Low — consider hypoalbuminemia or lab error"
            rounded <= 12 -> "Normal"
            else -> "Elevated — suggests high anion gap metabolic acidosis"
        }

        return CalculatorResult(
            value = String.format("%.1f", rounded),
            unit = "mEq/L",
            interpretation = interpretation
        )
    }
}
