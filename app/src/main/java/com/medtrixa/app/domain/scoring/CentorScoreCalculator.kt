package com.medtrixa.app.domain.scoring

object CentorScoreCalculator {

    enum class AgeGroup(val label: String, val points: Int) {
        CHILD("3\u201314 years", 1),
        ADULT("15\u201344 years", 0),
        OLDER("\u226545 years", -1)
    }

    data class Result(
        val ageGroup: AgeGroup,
        val tonsillarExudateOrSwelling: Int,
        val tenderAnteriorCervicalNodes: Int,
        val absenceOfCough: Int,
        val fever: Int
    ) {
        val total: Int
            get() = tonsillarExudateOrSwelling +
                tenderAnteriorCervicalNodes +
                absenceOfCough +
                fever +
                ageGroup.points

        val interpretation: String
            get() = when {
                total <= 1 -> "Low probability of GAS pharyngitis"
                total <= 3 -> "Intermediate probability of GAS pharyngitis"
                else -> "High probability of GAS pharyngitis"
            }

        val riskRange: String
            get() = when {
                total <= 0 -> "1\u20132.5%"
                total == 1 -> "5\u201310%"
                total == 2 -> "11\u201317%"
                total == 3 -> "28\u201335%"
                else -> "51\u201353%"
            }

        val recommendation: String
            get() = if (ageGroup == AgeGroup.CHILD) {
                when {
                    total <= 1 -> "Low risk. Routine testing is generally not needed; use clinical judgment."
                    else -> "Test with rapid antigen detection test (RADT). A negative RADT should be confirmed with throat culture. Treat if GAS is confirmed."
                }
            } else {
                when {
                    total <= 1 -> "No testing or empiric antibiotics indicated."
                    total <= 3 -> "Test with RADT and/or throat culture. Treat only if GAS is confirmed."
                    else -> "Test with RADT and/or throat culture, or consider empiric treatment based on clinical judgment."
                }
            }
    }

    fun calculate(
        tonsillarExudateOrSwelling: Boolean,
        tenderAnteriorCervicalNodes: Boolean,
        absenceOfCough: Boolean,
        fever: Boolean,
        ageGroup: AgeGroup = AgeGroup.ADULT
    ): Result {
        return Result(
            ageGroup = ageGroup,
            tonsillarExudateOrSwelling = if (tonsillarExudateOrSwelling) 1 else 0,
            tenderAnteriorCervicalNodes = if (tenderAnteriorCervicalNodes) 1 else 0,
            absenceOfCough = if (absenceOfCough) 1 else 0,
            fever = if (fever) 1 else 0
        )
    }
}
