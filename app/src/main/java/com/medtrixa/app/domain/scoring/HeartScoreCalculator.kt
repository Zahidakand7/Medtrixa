package com.medtrixa.app.domain.scoring

object HeartScoreCalculator {

    data class Result(
        val history: Int,
        val ecg: Int,
        val age: Int,
        val riskFactors: Int,
        val troponin: Int
    ) {
        val total: Int
            get() = history + ecg + age + riskFactors + troponin

        val category: String
            get() = when (total) {
                in 0..3 -> "Low risk"
                in 4..6 -> "Intermediate risk"
                else -> "High risk"
            }
    }

    fun calculate(
        history: Int,
        ecg: Int,
        ageYears: Int,
        riskFactorCount: Int,
        knownAtheroscleroticDisease: Boolean,
        troponinMultipleOfNormal: Double
    ): Result {

        val historyPoints = history.coerceIn(0, 2)
        val ecgPoints = ecg.coerceIn(0, 2)

        val agePoints = when {
            ageYears >= 65 -> 2
            ageYears > 45 -> 1
            else -> 0
        }

        val riskPoints = when {
            knownAtheroscleroticDisease || riskFactorCount >= 3 -> 2
            riskFactorCount in 1..2 -> 1
            else -> 0
        }

        val troponinPoints = when {
            troponinMultipleOfNormal >= 3.0 -> 2
            troponinMultipleOfNormal > 1.0 -> 1
            else -> 0
        }

        return Result(
            history = historyPoints,
            ecg = ecgPoints,
            age = agePoints,
            riskFactors = riskPoints,
            troponin = troponinPoints
        )
    }
}
