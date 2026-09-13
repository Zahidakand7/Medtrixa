package com.medtrixa.app.data.repository

import com.medtrixa.app.data.model.CalculatorInput
import com.medtrixa.app.data.model.MedicalCalculator

class CalculatorRepository {

    private val calculators = mutableListOf<MedicalCalculator>()

    init {
        calculators.add(
            MedicalCalculator(
                id = "bmi_adult",
                name = "Adult BMI",
                category = "General",
                specialty = "General Medicine",
                description = "Body mass index for adults aged 20 years and older.",
                inputs = listOf(
                    CalculatorInput(
                        id = "height_cm",
                        label = "Height",
                        unit = "cm"
                    ),
                    CalculatorInput(
                        id = "weight_kg",
                        label = "Weight",
                        unit = "kg"
                    )
                ),
                version = "1.0",
                evidenceLevel = "Established clinical screening measure",
                reference = "CDC Adult BMI Categories and Adult BMI Calculator",
                sourceUrl = "https://www.cdc.gov/bmi/adult-calculator/bmi-categories.html",
                lastReviewed = "2026-09-12"
            )
        )

        calculators.add(
            MedicalCalculator(
                id = "bmi_child",
                name = "Child & Adolescent BMI",
                category = "Pediatrics",
                specialty = "Pediatrics",
                description = "BMI-for-age assessment for children and adolescents aged 2 through 19 years.",
                inputs = listOf(
                    CalculatorInput(
                        id = "age_years",
                        label = "Age",
                        unit = "years"
                    ),
                    CalculatorInput(
                        id = "age_months",
                        label = "Age",
                        unit = "months"
                    ),
                    CalculatorInput(
                        id = "sex",
                        label = "Sex",
                        unit = null
                    ),
                    CalculatorInput(
                        id = "height_cm",
                        label = "Height",
                        unit = "cm"
                    ),
                    CalculatorInput(
                        id = "weight_kg",
                        label = "Weight",
                        unit = "kg"
                    )
                ),
                version = "1.0",
                evidenceLevel = "CDC BMI-for-age reference",
                reference = "CDC BMI-for-Age Growth Charts",
                sourceUrl = "https://www.cdc.gov/growth-chart-training/hcp/using-bmi/screening-measure.html",
                lastReviewed = "2026-09-12"
            )
        )

        calculators.add(
            MedicalCalculator(
                id = "creatinine_clearance",
                name = "Creatinine Clearance",
                category = "Renal",
                specialty = "General Medicine",
                description = "Estimated creatinine clearance using the Cockcroft–Gault equation.",
                inputs = listOf(
                    CalculatorInput(
                        id = "age_years",
                        label = "Age",
                        unit = "years"
                    ),
                    CalculatorInput(
                        id = "sex",
                        label = "Sex",
                        unit = null
                    ),
                    CalculatorInput(
                        id = "weight_kg",
                        label = "Weight",
                        unit = "kg"
                    ),
                    CalculatorInput(
                        id = "serum_creatinine",
                        label = "Serum Creatinine",
                        unit = "mg/dL"
                    )
                ),
                version = "1.0",
                evidenceLevel = "Cockcroft–Gault equation",
                reference = "Cockcroft DW, Gault MH. Prediction of creatinine clearance from serum creatinine.",
                sourceUrl = null,
                lastReviewed = "2026-09-12"
            )
        )

        calculators.add(
            MedicalCalculator(
                id = "bsa",
                name = "Body Surface Area",
                category = "General",
                specialty = "General Medicine",
                description = "Body surface area calculation using the Mosteller formula.",
                inputs = listOf(
                    CalculatorInput(
                        id = "height_cm",
                        label = "Height",
                        unit = "cm"
                    ),
                    CalculatorInput(
                        id = "weight_kg",
                        label = "Weight",
                        unit = "kg"
                    )
                ),
                version = "1.0",
                evidenceLevel = "Mosteller formula",
                reference = "Mosteller RD. Simplified calculation of body-surface area.",
                sourceUrl = null,
                lastReviewed = "2026-09-12"
            )
        )
    }

    fun getAll(): List<MedicalCalculator> = calculators.toList()

    fun getById(id: String): MedicalCalculator? {
        return calculators.find { it.id == id }
    }

    fun search(query: String): List<MedicalCalculator> {
        if (query.isBlank()) return getAll()

        val normalizedQuery = query.trim().lowercase()

        return calculators.filter {
            it.name.lowercase().contains(normalizedQuery) ||
            it.category.lowercase().contains(normalizedQuery) ||
            it.specialty.lowercase().contains(normalizedQuery) ||
            it.description.lowercase().contains(normalizedQuery)
        }
    }
}
