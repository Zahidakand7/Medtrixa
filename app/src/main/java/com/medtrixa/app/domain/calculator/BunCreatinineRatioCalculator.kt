package com.medtrixa.app.domain.calculator

import com.medtrixa.app.data.model.CalculatorResult
import kotlin.math.round

class BunCreatinineRatioCalculator : CalculatorEngine {
    override fun calculate(calculatorId: String, inputs: Map<String, Double>): CalculatorResult {
        require(calculatorId == "bun_creatinine_ratio") { "Unsupported calculator: $calculatorId" }
        val bun = inputs["bun"] ?: error("BUN is required")
        val creatinine = inputs["creatinine"] ?: error("Creatinine is required")
        require(bun > 0 && creatinine > 0) { "Values must be greater than zero" }

        val ratio = bun / creatinine
        val rounded = round(ratio * 10) / 10

        val interpretation = when {
            rounded < 10 -> "Low — consider intrinsic renal disease, low-protein state, or liver disease"
            rounded <= 20 -> "Normal ratio"
            else -> "High — suggests prerenal cause (e.g. dehydration, GI bleed, high-protein state)"
        }

        return CalculatorResult(
            value = String.format("%.1f", rounded),
            unit = null,
            interpretation = interpretation
        )
    }
}
