package com.medtrixa.app.domain.calculator

import com.medtrixa.app.data.model.CalculatorResult
import kotlin.math.round
import kotlin.math.sqrt

class BsaCalculator : CalculatorEngine {

    override fun calculate(
        calculatorId: String,
        inputs: Map<String, Double>
    ): CalculatorResult {

        require(calculatorId == "bsa") {
            "Unsupported calculator: $calculatorId"
        }

        val heightCm = inputs["height_cm"]
        val heightFt = inputs["height_ft"]
        val heightIn = inputs["height_in"]
        val weightKg = inputs["weight_kg"]
        val weightLb = inputs["weight_lb"]

        val finalHeightCm = when {
            heightCm != null -> heightCm
            heightFt != null && heightIn != null ->
                (heightFt * 30.48) + (heightIn * 2.54)
            else -> error("Height is required")
        }

        val finalWeightKg = when {
            weightKg != null -> weightKg
            weightLb != null -> weightLb * 0.45359237
            else -> error("Weight is required")
        }

        require(finalHeightCm > 0.0) {
            "Height must be greater than zero"
        }

        require(finalWeightKg > 0.0) {
            "Weight must be greater than zero"
        }

        val bsa = sqrt(
            (finalHeightCm * finalWeightKg) / 3600.0
        )

        val roundedBsa = round(bsa * 100) / 100

        return CalculatorResult(
            value = String.format("%.2f", roundedBsa),
            unit = "m²",
            interpretation = "Mosteller Formula"
        )
    }
}
