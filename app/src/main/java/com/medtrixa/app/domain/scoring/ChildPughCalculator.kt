package com.medtrixa.app.domain.scoring

object ChildPughCalculator {

    data class Result(
        val bilirubin: Int,
        val albumin: Int,
        val inr: Int,
        val ascites: Int,
        val encephalopathy: Int
    ) {
        val total: Int
            get() = bilirubin + albumin + inr + ascites + encephalopathy

        val className: String
            get() = when (total) {
                in 5..6 -> "Class A"
                in 7..9 -> "Class B"
                else -> "Class C"
            }
    }

    fun calculate(
        bilirubinMgDl: Double,
        albuminGDl: Double,
        inr: Double,
        ascitesLevel: Int,
        encephalopathyGrade: Int
    ): Result {

        val bilirubinPoints = when {
            bilirubinMgDl < 2.0 -> 1
            bilirubinMgDl <= 3.0 -> 2
            else -> 3
        }

        val albuminPoints = when {
            albuminGDl > 3.5 -> 1
            albuminGDl >= 2.8 -> 2
            else -> 3
        }

        val inrPoints = when {
            inr < 1.7 -> 1
            inr <= 2.3 -> 2
            else -> 3
        }

        return Result(
            bilirubin = bilirubinPoints,
            albumin = albuminPoints,
            inr = inrPoints,
            ascites = ascitesLevel.coerceIn(1, 3),
            encephalopathy = encephalopathyGrade.coerceIn(1, 3)
        )
    }
}
