package com.medtrixa.app.domain.scoring

object CentorScoreCalculator {

    data class Result(
        val tonsillarExudateOrSwelling: Int,
        val tenderAnteriorCervicalNodes: Int,
        val absenceOfCough: Int,
        val fever: Int
    ) {
        val total: Int
            get() = tonsillarExudateOrSwelling +
                    tenderAnteriorCervicalNodes +
                    absenceOfCough +
                    fever

        val interpretation: String
            get() = when (total) {
                0, 1 -> "Low likelihood"
                2 -> "Intermediate likelihood"
                3, 4 -> "Higher likelihood"
                else -> "Unknown"
            }
    }

    fun calculate(
        tonsillarExudateOrSwelling: Boolean,
        tenderAnteriorCervicalNodes: Boolean,
        absenceOfCough: Boolean,
        fever: Boolean
    ): Result {
        return Result(
            tonsillarExudateOrSwelling = if (tonsillarExudateOrSwelling) 1 else 0,
            tenderAnteriorCervicalNodes = if (tenderAnteriorCervicalNodes) 1 else 0,
            absenceOfCough = if (absenceOfCough) 1 else 0,
            fever = if (fever) 1 else 0
        )
    }
}
