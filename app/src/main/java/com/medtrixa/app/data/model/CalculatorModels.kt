package com.medtrixa.app.data.model

data class CalculatorInput(
    val id: String,
    val label: String,
    val unit: String? = null,
    val required: Boolean = true
)

data class CalculatorResult(
    val value: String,
    val unit: String? = null,
    val interpretation: String? = null
)

data class MedicalCalculator(
    val id: String,
    val name: String,
    val category: String,
    val specialty: String,
    val description: String,
    val inputs: List<CalculatorInput>,
    val version: String,
    val evidenceLevel: String,
    val reference: String,
    val sourceUrl: String? = null,
    val lastReviewed: String? = null
)
