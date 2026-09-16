package com.medtrixa.app.domain.calculator

import com.medtrixa.app.data.model.CalculatorResult
import kotlin.math.round

class InfusionRateCalculator : CalculatorEngine {

    override fun calculate(
        calculatorId: String,
        inputs: Map<String, Double>
    ): CalculatorResult {

        require(calculatorId == "infusion_rate") {
            "Unsupported calculator: $calculatorId"
        }

        val volumeMl = inputs["volume_ml"] ?: error("Volume is required")
        val timeHours = inputs["time_hours"] ?: error("Time is required")

        require(volumeMl > 0 && timeHours > 0) { "Values must be greater than zero" }

        val rate = volumeMl / timeHours
        val rounded = round(rate * 10) / 10

        val interpretation = "Set infusion pump to ${String.format("%.1f", rounded)} mL/hr"

        return CalculatorResult(
            value = String.format("%.1f", rounded),
            unit = "mL/hr",
            interpretation = interpretation
        )
    }
}
