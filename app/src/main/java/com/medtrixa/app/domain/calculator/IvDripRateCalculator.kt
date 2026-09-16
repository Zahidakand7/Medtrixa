package com.medtrixa.app.domain.calculator

import com.medtrixa.app.data.model.CalculatorResult
import kotlin.math.round

class IvDripRateCalculator : CalculatorEngine {

    override fun calculate(
        calculatorId: String,
        inputs: Map<String, Double>
    ): CalculatorResult {

        require(calculatorId == "iv_drip_rate") {
            "Unsupported calculator: $calculatorId"
        }

        val volumeMl = inputs["volume_ml"] ?: error("Volume is required")
        val dropFactor = inputs["drop_factor"] ?: error("Drop factor is required")
        val timeMinutes = inputs["time_minutes"] ?: error("Time is required")

        require(volumeMl > 0 && dropFactor > 0 && timeMinutes > 0) {
            "Values must be greater than zero"
        }

        val dripRate = (volumeMl * dropFactor) / timeMinutes
        val rounded = round(dripRate)

        val interpretation = "Set to approximately ${rounded.toInt()} drops per minute using a " +
            "${dropFactor.toInt()} gtt/mL administration set"

        return CalculatorResult(
            value = String.format("%.0f", rounded),
            unit = "gtt/min",
            interpretation = interpretation
        )
    }
}
