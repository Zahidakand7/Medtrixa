package com.medtrixa.app.domain.calculator

import com.medtrixa.app.data.model.CalculatorResult
import kotlin.math.round

class AaGradientCalculator : CalculatorEngine {
    override fun calculate(calculatorId: String, inputs: Map<String, Double>): CalculatorResult {
        require(calculatorId == "aa_gradient") { "Unsupported calculator: $calculatorId" }
        val fio2Percent = inputs["fio2_percent"] ?: error("FiO2 is required")
        val paco2 = inputs["paco2"] ?: error("PaCO2 is required")
        val pao2 = inputs["pao2"] ?: error("PaO2 is required")
        val age = inputs["age"] ?: error("Age is required")
        require(fio2Percent in 21.0..100.0) { "FiO2 must be between 21 and 100 percent" }
        require(paco2 > 0 && pao2 > 0 && age > 0) { "Values must be greater than zero" }

        val fio2 = fio2Percent / 100.0
        val pAO2 = fio2 * 713.0 - (paco2 / 0.8)
        val gradient = pAO2 - pao2
        val rounded = round(gradient * 10) / 10

        val expectedUpperNormal = (age / 4.0) + 4.0
        val interpretation = if (rounded <= expectedUpperNormal) {
            "Within age-adjusted normal range (≤${String.format("%.1f", expectedUpperNormal)} mmHg)"
        } else {
            "Widened — suggests V/Q mismatch, shunt, or diffusion defect"
        }

        return CalculatorResult(
            value = String.format("%.1f", rounded),
            unit = "mmHg",
            interpretation = interpretation
        )
    }
}
