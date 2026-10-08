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
                    CalculatorInput(id = "height_cm", label = "Height", unit = "cm"),
                    CalculatorInput(id = "weight_kg", label = "Weight", unit = "kg")
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
                    CalculatorInput(id = "age_years", label = "Age", unit = "years"),
                    CalculatorInput(id = "age_months", label = "Age", unit = "months"),
                    CalculatorInput(id = "sex", label = "Sex", unit = null),
                    CalculatorInput(id = "height_cm", label = "Height", unit = "cm"),
                    CalculatorInput(id = "weight_kg", label = "Weight", unit = "kg")
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
                id = "bsa",
                name = "Body Surface Area",
                category = "General",
                specialty = "General Medicine",
                description = "Body surface area calculation using the Mosteller formula.",
                inputs = listOf(
                    CalculatorInput(id = "height_cm", label = "Height", unit = "cm"),
                    CalculatorInput(id = "weight_kg", label = "Weight", unit = "kg")
                ),
                version = "1.0",
                evidenceLevel = "Mosteller formula",
                reference = "Mosteller RD. Simplified calculation of body-surface area.",
                sourceUrl = null,
                lastReviewed = "2026-09-12"
            )
        )

        calculators.add(
            MedicalCalculator(
                id = "map",
                name = "Mean Arterial Pressure",
                category = "Cardiology",
                specialty = "Critical Care",
                description = "Estimated average arterial pressure during a single cardiac cycle.",
                inputs = listOf(
                    CalculatorInput(id = "sbp", label = "Systolic BP", unit = "mmHg"),
                    CalculatorInput(id = "dbp", label = "Diastolic BP", unit = "mmHg")
                ),
                version = "1.0",
                evidenceLevel = "Standard hemodynamic formula",
                reference = "MAP = DBP + 1/3(SBP − DBP)",
                sourceUrl = null,
                lastReviewed = "2026-09-13"
            )
        )

        calculators.add(
            MedicalCalculator(
                id = "shock_index",
                name = "Shock Index",
                category = "Critical Care",
                specialty = "Emergency Medicine",
                description = "Ratio of heart rate to systolic blood pressure, used to assess hemodynamic instability.",
                inputs = listOf(
                    CalculatorInput(id = "hr", label = "Heart Rate", unit = "bpm"),
                    CalculatorInput(id = "sbp", label = "Systolic BP", unit = "mmHg")
                ),
                version = "1.0",
                evidenceLevel = "Standard hemodynamic index",
                reference = "Shock Index = HR / SBP",
                sourceUrl = null,
                lastReviewed = "2026-09-13"
            )
        )

        calculators.add(
            MedicalCalculator(
                id = "anion_gap",
                name = "Anion Gap",
                category = "Metabolic",
                specialty = "General Medicine",
                description = "Estimates unmeasured anions to assess acid–base status.",
                inputs = listOf(
                    CalculatorInput(id = "sodium", label = "Sodium", unit = "mEq/L"),
                    CalculatorInput(id = "chloride", label = "Chloride", unit = "mEq/L"),
                    CalculatorInput(id = "bicarbonate", label = "Bicarbonate", unit = "mEq/L")
                ),
                version = "1.0",
                evidenceLevel = "Standard acid-base formula",
                reference = "Anion Gap = Na − (Cl + HCO₃)",
                sourceUrl = null,
                lastReviewed = "2026-09-13"
            )
        )

        calculators.add(
            MedicalCalculator(
                id = "corrected_calcium",
                name = "Corrected Calcium",
                category = "Metabolic",
                specialty = "General Medicine",
                description = "Adjusts total calcium for albumin level to estimate true calcium status.",
                inputs = listOf(
                    CalculatorInput(id = "calcium", label = "Measured Calcium", unit = "mg/dL"),
                    CalculatorInput(id = "albumin", label = "Albumin", unit = "g/dL")
                ),
                version = "1.0",
                evidenceLevel = "Standard correction formula",
                reference = "Corrected Ca = Ca + 0.8 × (4.0 − Albumin)",
                sourceUrl = null,
                lastReviewed = "2026-09-13"
            )
        )

        calculators.add(
            MedicalCalculator(
                id = "qtc",
                name = "QTc (Bazett)",
                category = "Cardiology",
                specialty = "Critical Care",
                description = "Heart-rate corrected QT interval using Bazett's formula.",
                inputs = listOf(
                    CalculatorInput(id = "qt_ms", label = "QT Interval", unit = "ms"),
                    CalculatorInput(id = "hr", label = "Heart Rate", unit = "bpm")
                ),
                version = "1.0",
                evidenceLevel = "Bazett formula",
                reference = "QTc = QT / √(RR), RR in seconds",
                sourceUrl = null,
                lastReviewed = "2026-09-13"
            )
        )

        calculators.add(
            MedicalCalculator(
                id = "pf_ratio",
                name = "P/F Ratio",
                category = "Respiratory",
                specialty = "Critical Care",
                description = "Ratio of arterial oxygen partial pressure to fractional inspired oxygen, used in ARDS classification.",
                inputs = listOf(
                    CalculatorInput(id = "pao2", label = "PaO2", unit = "mmHg"),
                    CalculatorInput(id = "fio2_percent", label = "FiO2", unit = "%")
                ),
                version = "1.0",
                evidenceLevel = "Berlin ARDS criteria",
                reference = "ARDS Definition Task Force. Berlin Definition of ARDS. JAMA 2012.",
                sourceUrl = null,
                lastReviewed = "2026-09-13"
            )
        )

        calculators.add(
            MedicalCalculator(
                id = "corrected_sodium",
                name = "Corrected Sodium (Glucose)",
                category = "Metabolic",
                specialty = "General Medicine",
                description = "Adjusts measured sodium for hyperglycemia to estimate true sodium status.",
                inputs = listOf(
                    CalculatorInput(id = "sodium", label = "Measured Sodium", unit = "mEq/L"),
                    CalculatorInput(id = "glucose", label = "Glucose", unit = "mg/dL")
                ),
                version = "1.0",
                evidenceLevel = "Katz correction formula",
                reference = "Katz MA. Hyperglycemia-induced hyponatremia. N Engl J Med 1973.",
                sourceUrl = null,
                lastReviewed = "2026-09-14"
            )
        )

        calculators.add(
            MedicalCalculator(
                id = "aa_gradient",
                name = "A-a Gradient",
                category = "Respiratory",
                specialty = "Critical Care",
                description = "Alveolar-arterial oxygen gradient, used to assess causes of hypoxemia.",
                inputs = listOf(
                    CalculatorInput(id = "age", label = "Age", unit = "years"),
                    CalculatorInput(id = "fio2_percent", label = "FiO2", unit = "%"),
                    CalculatorInput(id = "paco2", label = "PaCO2", unit = "mmHg"),
                    CalculatorInput(id = "pao2", label = "PaO2", unit = "mmHg")
                ),
                version = "1.0",
                evidenceLevel = "Standard alveolar gas equation",
                reference = "A-a Gradient = PAO2 − PaO2 (PAO2 via alveolar gas equation, sea level)",
                sourceUrl = null,
                lastReviewed = "2026-09-14"
            )
        )

        calculators.add(
            MedicalCalculator(
                id = "egfr",
                name = "eGFR (CKD-EPI 2021)",
                category = "Renal",
                specialty = "General Medicine",
                description = "Estimated glomerular filtration rate using the race-free 2021 CKD-EPI creatinine equation.",
                inputs = listOf(
                    CalculatorInput(id = "age", label = "Age", unit = "years"),
                    CalculatorInput(id = "sex_female", label = "Sex", unit = null),
                    CalculatorInput(id = "creatinine", label = "Serum Creatinine", unit = "mg/dL")
                ),
                version = "1.0",
                evidenceLevel = "CKD-EPI 2021 equation",
                reference = "Inker LA, et al. New Creatinine- and Cystatin C-Based Equations. N Engl J Med 2021.",
                sourceUrl = null,
                lastReviewed = "2026-09-14"
            )
        )

        calculators.add(
            MedicalCalculator(
                id = "bun_creatinine_ratio",
                name = "BUN/Creatinine Ratio",
                category = "Renal",
                specialty = "General Medicine",
                description = "Ratio used to help distinguish prerenal from intrinsic renal causes of azotemia.",
                inputs = listOf(
                    CalculatorInput(id = "bun", label = "BUN", unit = "mg/dL"),
                    CalculatorInput(id = "creatinine", label = "Creatinine", unit = "mg/dL")
                ),
                version = "1.0",
                evidenceLevel = "Standard clinical ratio",
                reference = "Ratio = BUN / Creatinine",
                sourceUrl = null,
                lastReviewed = "2026-09-14"
            )
        )
        calculators.add(
            MedicalCalculator(
                id = "iv_drip_rate",
                name = "IV Drip Rate",
                category = "Dosage",
                specialty = "General Medicine",
                description = "Gravity IV flow rate in drops per minute based on volume, drop factor, and time.",
                inputs = listOf(
                    CalculatorInput(id = "volume_ml", label = "Volume", unit = "mL"),
                    CalculatorInput(id = "drop_factor", label = "Drop Factor", unit = "gtt/mL"),
                    CalculatorInput(id = "time_minutes", label = "Time", unit = "min")
                ),
                version = "1.0",
                evidenceLevel = "Standard nursing flow-rate formula",
                reference = "Drip Rate (gtt/min) = (Volume \u00d7 Drop Factor) / Time",
                sourceUrl = null,
                lastReviewed = "2026-09-14"
            )
        )

        calculators.add(
            MedicalCalculator(
                id = "infusion_rate",
                name = "Infusion Rate",
                category = "Dosage",
                specialty = "General Medicine",
                description = "Infusion pump rate in mL/hr based on volume to be infused and duration.",
                inputs = listOf(
                    CalculatorInput(id = "volume_ml", label = "Volume", unit = "mL"),
                    CalculatorInput(id = "time_hours", label = "Time", unit = "hr")
                ),
                version = "1.0",
                evidenceLevel = "Standard infusion pump formula",
                reference = "Rate (mL/hr) = Volume / Time",
                sourceUrl = null,
                lastReviewed = "2026-09-14"
            )
        )

        calculators.add(
            MedicalCalculator(
                id = "pediatric_dose",
                name = "Pediatric Dose (Clark's Rule)",
                category = "Dosage",
                specialty = "Pediatrics",
                description = "Estimates a pediatric dose from the standard adult dose using body weight.",
                inputs = listOf(
                    CalculatorInput(id = "weight_kg", label = "Child's Weight", unit = "kg"),
                    CalculatorInput(id = "adult_dose_mg", label = "Adult Dose", unit = "mg")
                ),
                version = "1.0",
                evidenceLevel = "Clark's Rule (weight-based estimation)",
                reference = "Child Dose = (Weight in kg / 70) \u00d7 Adult Dose",
                sourceUrl = null,
                lastReviewed = "2026-09-14"
            )
        )

        calculators.add(
            MedicalCalculator(
                id = "weight_based_dose",
                name = "Weight-Based Dose",
                category = "Dosage",
                specialty = "General Medicine",
                description = "Calculates total dose from a per-kilogram dose and patient weight, with an optional maximum dose cap.",
                inputs = listOf(
                    CalculatorInput(id = "weight_kg", label = "Weight", unit = "kg"),
                    CalculatorInput(id = "dose_per_kg", label = "Dose per kg", unit = "mg/kg"),
                    CalculatorInput(id = "max_dose_mg", label = "Max Dose", unit = "mg", required = false)
                ),
                version = "1.0",
                evidenceLevel = "Standard weight-based dosing formula",
                reference = "Dose = Dose per kg \u00d7 Weight (capped at max dose if specified)",
                sourceUrl = null,
                lastReviewed = "2026-09-14"
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
