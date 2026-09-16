package com.medtrixa.app.domain.calculator

import com.medtrixa.app.data.model.CalculatorResult
import kotlin.math.round

class PfRatioCalculator : CalculatorEngine {
    override fun calculate(calculatorId: String, inputs: Map<String, Double>): CalculatorResult {
        require(calculatorId == "pf_ratio") { "Unsupported calculator: $calculatorId" }
        val pao2 = inputs["pao2"] ?: error("PaO2 is required")
        val fio2Percent = inputs["fio2_percent"] ?: error("FiO2 is required")
        require(pao2 > 0) { "PaO2 must be greater than zero" }
        require(fio2Percent in 21.0..100.0) { "FiO2 must be between 21 and 100 percent" }

        val fio2 = fio2Percent / 100.0
        val ratio = pao2 / fio2
        val rounded = round(ratio)

        val interpretation = when {
            rounded > 300 -> "Normal (no ARDS)"
            rounded > 200 -> "Mild ARDS (Berlin criteria)"
            rounded > 100 -> "Moderate ARDS (Berlin criteria)"
            else -> "Severe ARDS (Berlin criteria)"
        }

        return CalculatorResult(
            value = String.format("%.0f", rounded),
            unit = null,
            interpretation = interpretation
        )
    }
}
