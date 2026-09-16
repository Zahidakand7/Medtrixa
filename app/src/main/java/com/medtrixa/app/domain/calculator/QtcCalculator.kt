package com.medtrixa.app.domain.calculator

import com.medtrixa.app.data.model.CalculatorResult
import kotlin.math.round
import kotlin.math.sqrt

class QtcCalculator : CalculatorEngine {
    override fun calculate(calculatorId: String, inputs: Map<String, Double>): CalculatorResult {
        require(calculatorId == "qtc") { "Unsupported calculator: $calculatorId" }
        val qtMs = inputs["qt_ms"] ?: error("QT interval is required")
        val hr = inputs["hr"] ?: error("Heart rate is required")
        require(qtMs > 0 && hr > 0) { "Values must be greater than zero" }

        val rrSec = 60.0 / hr
        val qtc = qtMs / sqrt(rrSec)
        val rounded = round(qtc)

        val interpretation = when {
            rounded < 440 -> "Normal"
            rounded <= 460 -> "Borderline prolonged"
            else -> "Prolonged — increased arrhythmia risk"
        }

        return CalculatorResult(
            value = String.format("%.0f", rounded),
            unit = "ms",
            interpretation = interpretation
        )
    }
}
