package com.medtrixa.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "label")
data class DrugLabelEntity(
    @PrimaryKey val ingredientRxcui: Long,
    val setId: String?,
    val manufacturer: String?,
    val otherManufacturers: String?,
    val effectiveDate: String?,
    val indications: String?,
    val adultDose: String?,
    val adultAdministration: String?,
    val childDose: String?,
    val renalHepaticDose: String?,
    val contraindications: String?,
    val warnings: String?,
    val adverseReactions: String?,
    val pregnancy: String?,
    val mechanism: String?
)
