package com.medtrixa.app.data.model

data class DrugInteractionResult(
    val drugA: String,
    val drugB: String,
    val level: String,
    val rxcuiA: String?,
    val rxcuiB: String?
)
