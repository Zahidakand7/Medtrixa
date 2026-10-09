package com.medtrixa.app.data.model

data class LactMedResult(
    val name: String,
    val synonyms: List<String>,
    val revised: String,
    val summary: String,
    val levels: String,
    val infant: String,
    val lactation: String,
    val alternatives: String
)
