package com.medtrixa.app.domain.scoring

import kotlin.math.ln
import kotlin.math.roundToInt

object Meld30Calculator {

    data class Result(
        val score: Int
    )

    fun calculate(
        bilirubinMgDl: Double,
        sodiumMmolL: Double,
        inr: Double,
        creatinineMgDl: Double,
        albuminGDl: Double,
        female: Boolean,
        dialysisTwoOrMoreTimesLast7Days: Boolean = false,
        cvvhd24HoursLast7Days: Boolean = false
    ): Result {

        val bilirubin = bilirubinMgDl.coerceAtLeast(1.0)
        val inrValue = inr.coerceAtLeast(1.0)

        val sodium = sodiumMmolL.coerceIn(125.0, 137.0)

        val albumin = albuminGDl.coerceIn(1.5, 3.5)

        val creatinine = when {
            dialysisTwoOrMoreTimesLast7Days ||
                cvvhd24HoursLast7Days -> 3.0
            else -> creatinineMgDl.coerceIn(1.0, 3.0)
        }

        val score =
            (if (female) 1.33 else 0.0) +
            (4.56 * ln(bilirubin)) +
            (0.82 * (137.0 - sodium)) -
            (0.24 * (137.0 - sodium) * ln(bilirubin)) +
            (9.09 * ln(inrValue)) +
            (11.14 * ln(creatinine)) +
            (1.85 * (3.5 - albumin)) -
            (1.83 * (3.5 - albumin) * ln(creatinine)) +
            6.0

        return Result(
            score = score.roundToInt().coerceIn(6, 40)
        )
    }
}
