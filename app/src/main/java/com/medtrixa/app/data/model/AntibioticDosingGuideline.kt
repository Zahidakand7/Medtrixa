package com.medtrixa.app.data.model

data class AntibioticDosingGuideline(
    val indication: String,
    val setting: String,
    val organisms: String,
    val antibiotic: String,
    val dose: String,
    val route: String,
    val frequency: String,
    val duration: String,
    val patientGroup: String,
    val renalAdjustment: String,
    val hepaticAdjustment: String,
    val alternatives: String,
    val notes: String,
    val source: String,
    val sourceStatus: String,
    val reviewDate: String
)
