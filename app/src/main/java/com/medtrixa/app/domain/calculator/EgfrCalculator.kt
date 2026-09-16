package com.medtrixa.app.domain.calculator

import com.medtrixa.app.data.model.CalculatorResult
import kotlin.math.min
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.round

class EgfrCalculator : CalculatorEngine {
    override fun calculate(calculatorId: String, inputs: Map<String, Double>): CalculatorResult {
        require(calculatorId == "egfr") { "Unsupported calculator: $calculatorId" }
        val creatinine = inputs["creatinine"] ?: error("Creatinine is required")
        val age = inputs["age"] ?: error("Age is required")
        val sexCode = inputs["sex_female"] ?: error("Sex is required")
        require(creatinine > 0 && age > 0) { "Values must be greater than zero" }

        val isFemale = sexCode == 1.0
        val kappa = if (isFemale) 0.7 else 0.9
        val alpha = if (isFemale) -0.241 else -0.302
        val sexFactor = if (isFemale) 1.012 else 1.0

        val scrOverKappa = creatinine / kappa
        val minTerm = min(scrOverKappa, 1.0).pow(alpha)
        val maxTerm = max(scrOverKappa, 1.0).pow(-1.200)

        val egfr = 142 * minTerm * maxTerm * 0.9938.pow(age) * sexFactor
        val rounded = round(egfr)

        val interpretation = when {
            rounded >= 90 -> "G1 — Normal or high (confirm with albuminuria staging)"
            rounded >= 60 -> "G2 — Mildly decreased"
            rounded >= 45 -> "G3a — Mildly to moderately decreased"
            rounded >= 30 -> "G3b — Moderately to severely decreased"
            rounded >= 15 -> "G4 — Severely decreased"
            else -> "G5 — Kidney failure"
        }

        return CalculatorResult(
            value = String.format("%.0f", rounded),
            unit = "mL/min/1.73m²",
            interpretation = interpretation
        )
    }
}
