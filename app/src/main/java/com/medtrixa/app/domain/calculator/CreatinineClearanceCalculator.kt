package com.medtrixa.app.domain.calculator

import com.medtrixa.app.data.model.CalculatorResult
import kotlin.math.round

class CreatinineClearanceCalculator : CalculatorEngine {

    override fun calculate(
        calculatorId: String,
        inputs: Map<String, Double>
    ): CalculatorResult {

        require(calculatorId == "creatinine_clearance") {
            "Unsupported calculator: $calculatorId"
        }

        val age = inputs["age_years"]
            ?: error("Age is required")

        val weightKg = inputs["weight_kg"]
            ?: error("Weight is required")

        val serumCreatinine = inputs["serum_creatinine"]
            ?: error("Serum creatinine is required")

        val sex = inputs["sex"]
            ?: error("Sex is required")

        require(age > 0.0) {
            "Age must be greater than zero"
        }

        require(weightKg > 0.0) {
            "Weight must be greater than zero"
        }

        require(serumCreatinine > 0.0) {
            "Serum creatinine must be greater than zero"
        }

        val sexFactor = if (sex == 2.0) 0.85 else 1.0

        val creatinineClearance =
            ((140.0 - age) * weightKg) /
                (72.0 * serumCreatinine) *
                sexFactor

        val roundedValue = round(creatinineClearance * 10) / 10

        return CalculatorResult(
            value = String.format("%.1f", roundedValue),
            unit = "mL/min",
            interpretation = "Cockcroft–Gault Equation"
        )
    }
}
