package com.medtrixa.app.domain.calculator

import com.medtrixa.app.data.model.CalculatorResult
import kotlin.math.round

class ShockIndexCalculator : CalculatorEngine {
    override fun calculate(calculatorId: String, inputs: Map<String, Double>): CalculatorResult {
        require(calculatorId == "shock_index") { "Unsupported calculator: $calculatorId" }
        val hr = inputs["hr"] ?: error("Heart rate is required")
        val sbp = inputs["sbp"] ?: error("Systolic BP is required")
        require(hr > 0 && sbp > 0) { "Values must be greater than zero" }

        val si = hr / sbp
        val rounded = round(si * 100) / 100

        val interpretation = when {
            rounded < 0.7 -> "Normal"
            rounded < 1.0 -> "Borderline — monitor closely"
            else -> "High — suggests hemodynamic instability / shock"
        }

        return CalculatorResult(
            value = String.format("%.2f", rounded),
            unit = null,
            interpretation = interpretation
        )
    }
}
