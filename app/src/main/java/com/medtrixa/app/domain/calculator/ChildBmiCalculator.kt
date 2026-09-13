package com.medtrixa.app.domain.calculator

import android.content.Context
import com.medtrixa.app.data.model.CalculatorResult
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sqrt

class ChildBmiCalculator(
    private val context: Context
) : CalculatorEngine {

    override fun calculate(
        calculatorId: String,
        inputs: Map<String, Double>
    ): CalculatorResult {

        require(calculatorId == "bmi_child") {
            "Unsupported calculator: $calculatorId"
        }

        val ageYears = inputs["age_years"] ?: error("Age is required")
        val ageMonths = inputs["age_months"] ?: 0.0
        val sex = inputs["sex"] ?: error("Sex is required")

        require(sex == 1.0 || sex == 2.0) {
            "Sex must be 1 (Male) or 2 (Female)"
        }

        require(ageYears >= 2.0) {
            "Child BMI is intended for ages 2 through 19 years"
        }

        require(ageMonths in 0.0..11.0) {
            "Additional months must be between 0 and 11"
        }

        val totalAgeMonths = ageYears * 12.0 + ageMonths
        val referenceAgeMonths = totalAgeMonths + 0.5

        require(totalAgeMonths < 240.0) {
            "Child BMI is intended for ages 2 through 19 years"
        }

        val heightCm = inputs["height_cm"]
        val heightFt = inputs["height_ft"]
        val heightIn = inputs["height_in"]

        val weightKg = inputs["weight_kg"]
        val weightLb = inputs["weight_lb"]

        val finalHeightCm = when {
            heightCm != null -> heightCm
            heightFt != null && heightIn != null ->
                heightFt * 30.48 + heightIn * 2.54
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

        val reference = loadReferenceRow(
            sex = sex.toInt(),
            ageMonths = referenceAgeMonths
        )

        val zScore = if (bmi >= reference.p95) {
            calculateExtendedZScore(
                bmi = bmi,
                reference = reference
            )
        } else {
            calculateLmsZScore(
                bmi = bmi,
                l = reference.l,
                m = reference.m,
                s = reference.s
            )
        }

        val percentile = (normalCdf(zScore) * 100.0)
            .coerceIn(0.0, 99.99)

        val severeObesityThreshold = maxOf(
            reference.pct120OfP95,
            35.0
        )

        val category = when {
            bmi >= severeObesityThreshold -> "Severe Obesity"
            percentile >= 95.0 -> "Obesity"
            percentile >= 85.0 -> "Overweight"
            percentile >= 5.0 -> "Healthy Weight"
            else -> "Underweight"
        }

        return CalculatorResult(
            value = String.format("%.1f", bmi),
            unit = "kg/m²",
            interpretation =
                "$category • ${String.format("%.1f", percentile)}th percentile"
        )
    }

    private data class ReferenceRow(
        val l: Double,
        val m: Double,
        val s: Double,
        val p95: Double,
        val p98: Double,
        val p99: Double,
        val p99_9: Double,
        val p99_99: Double,
        val pct120OfP95: Double,
        val sigma: Double
    )

    private fun loadReferenceRow(
        sex: Int,
        ageMonths: Double
    ): ReferenceRow {

        val rows = context.assets
            .open("bmiagerev_ext.csv")
            .use { input ->
                BufferedReader(InputStreamReader(input)).useLines { lines ->

                    lines
                        .drop(1)
                        .mapNotNull { line ->
                            val parts = line.split(",")

                            if (parts.size < 20) {
                                null
                            } else {
                                val rowSex = parts[0].toIntOrNull()
                                val rowAge = parts[1].toDoubleOrNull()

                                if (rowSex != sex || rowAge == null) {
                                    null
                                } else {
                                    rowAge to parts
                                }
                            }
                        }
                        .minByOrNull { (rowAge, _) ->
                            abs(rowAge - ageMonths)
                        }
                }
            }
            ?: error("CDC reference data not found")

        val parts = rows.second

        return ReferenceRow(
            l = parts[2].toDouble(),
            m = parts[3].toDouble(),
            s = parts[4].toDouble(),
            p95 = parts[14].toDouble(),
            p98 = parts[15].toDouble(),
            p99 = parts[16].toDouble(),
            p99_9 = parts[17].toDouble(),
            p99_99 = parts[18].toDouble(),
            pct120OfP95 = parts[19].toDouble(),
            sigma = parts[5].toDouble()
        )
    }

    private fun calculateLmsZScore(
        bmi: Double,
        l: Double,
        m: Double,
        s: Double
    ): Double {
        return if (l != 0.0) {
            ((bmi / m).pow(l) - 1.0) / (l * s)
        } else {
            ln(bmi / m) / s
        }
    }

    private fun calculateExtendedZScore(
        bmi: Double,
        reference: ReferenceRow
    ): Double {

        if (bmi <= reference.p95) {
            return calculateLmsZScore(
                bmi = bmi,
                l = reference.l,
                m = reference.m,
                s = reference.s
            )
        }

        val extendedPercentile = (
            90.0 +
                10.0 * normalCdf(
                    (bmi - reference.p95) / reference.sigma
                )
            ).coerceIn(95.0, 99.999999)

        return inverseNormalCdf(extendedPercentile / 100.0)
    }

    private fun inverseNormalCdf(p: Double): Double {
        require(p > 0.0 && p < 1.0) {
            "Probability must be between 0 and 1"
        }

        val a = doubleArrayOf(
            -39.6968302866538,
            220.946098424521,
            -275.928510446969,
            138.357751867269,
            -30.6647980661472,
            2.50662827745924
        )

        val b = doubleArrayOf(
            -54.4760987982241,
            161.585836858041,
            -155.698979859887,
            66.8013118877197,
            -13.2806815528857
        )

        val c = doubleArrayOf(
            -0.00778489400243029,
            -0.322396458041136,
            -2.40075827716184,
            -2.54973253934373,
            4.37466414146497,
            2.93816398269878
        )

        val d = doubleArrayOf(
            0.00778469570904146,
            0.32246712907004,
            2.445134137143,
            3.75440866190742
        )

        val plow = 0.02425
        val phigh = 1.0 - plow

        return when {
            p < plow -> {
                val q = sqrt(-2.0 * ln(p))

                val numerator =
                    (((((c[0] * q + c[1]) * q + c[2]) * q + c[3]) * q + c[4]) * q + c[5])

                val denominator =
                    ((((d[0] * q + d[1]) * q + d[2]) * q + d[3]) * q + 1.0)

                numerator / denominator
            }

            p > phigh -> {
                val q = sqrt(-2.0 * ln(1.0 - p))

                val numerator =
                    (((((c[0] * q + c[1]) * q + c[2]) * q + c[3]) * q + c[4]) * q + c[5])

                val denominator =
                    ((((d[0] * q + d[1]) * q + d[2]) * q + d[3]) * q + 1.0)

                -(numerator / denominator)
            }

            else -> {
                val q = p - 0.5
                val r = q * q

                var numerator = a[0]
                numerator = numerator * r + a[1]
                numerator = numerator * r + a[2]
                numerator = numerator * r + a[3]
                numerator = numerator * r + a[4]
                numerator = numerator * r + a[5]
                numerator *= q

                var denominator = b[0]
                denominator = denominator * r + b[1]
                denominator = denominator * r + b[2]
                denominator = denominator * r + b[3]
                denominator = denominator * r + b[4]
                denominator = denominator * r + 1.0

                numerator / denominator
            }
        }
    }

    private fun normalCdf(z: Double): Double {
        return 0.5 * (1.0 + erf(z / sqrt(2.0)))
    }

    private fun erf(x: Double): Double {
        val sign = if (x < 0.0) -1.0 else 1.0
        val ax = abs(x)

        val a1 = 0.254829592
        val a2 = -0.284496736
        val a3 = 1.421413741
        val a4 = -1.453152027
        val a5 = 1.061405429
        val p = 0.3275911

        val t = 1.0 / (1.0 + p * ax)

        val y = 1.0 - (
            (((((a5 * t + a4) * t) + a3) * t + a2) * t + a1) *
                t *
                exp(-ax * ax)
            )

        return sign * y
    }
}
