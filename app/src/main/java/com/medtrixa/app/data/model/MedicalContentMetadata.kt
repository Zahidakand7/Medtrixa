package com.medtrixa.app.data.model

data class MedicalContentMetadata(
    val source: String,
    val sourceUrl: String? = null,
    val publicationDate: String? = null,
    val lastReviewed: String? = null,
    val nextReview: String? = null,
    val evidenceLevel: String? = null,
    val status: ContentStatus = ContentStatus.CURRENT,
    val supersededBy: String? = null
)

enum class ContentStatus {
    CURRENT,
    UNDER_REVIEW,
    SUPERSEDED,
    RETIRED
}
