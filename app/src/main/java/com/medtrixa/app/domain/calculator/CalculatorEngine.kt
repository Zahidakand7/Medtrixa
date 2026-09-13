package com.medtrixa.app.domain.calculator

import com.medtrixa.app.data.model.CalculatorResult

interface CalculatorEngine {
    fun calculate(
        calculatorId: String,
        inputs: Map<String, Double>
    ): CalculatorResult
}
