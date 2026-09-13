package com.medtrixa.app.domain.calculator

import com.medtrixa.app.data.model.CalculatorResult
import kotlin.math.round

class BmiCalculator : CalculatorEngine {

    override fun calculate(
        calculatorId: String,
        inputs: Map<String, Double>
    ): CalculatorResult {

        require(calculatorId == "bmi_adult") {
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

        val heightM = finalHeightCm / 100.0
        val bmi = finalWeightKg / (heightM * heightM)
        val roundedBmi = round(bmi * 10) / 10

        val category = when {
            bmi < 18.5 -> "Underweight"
            bmi < 25.0 -> "Healthy Weight"
            bmi < 30.0 -> "Overweight"
            bmi < 35.0 -> "Obesity — Class 1"
            bmi < 40.0 -> "Obesity — Class 2"
            else -> "Obesity — Class 3"
        }

        return CalculatorResult(
            value = String.format("%.1f", roundedBmi),
            unit = "kg/m²",
            interpretation = category
        )
    }
}
