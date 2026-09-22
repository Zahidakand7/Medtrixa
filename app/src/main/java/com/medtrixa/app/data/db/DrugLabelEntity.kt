package com.medtrixa.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "drug_labels")
data class DrugLabelEntity(
    @PrimaryKey val genericName: String,
    val setId: String?,
    val indications: String?,
    val adultDose: String?,
    val adultAdministration: String?,
    val childDose: String?,
    val childAdministration: String?,
    val renalHepaticDose: String?,
    val contraindications: String?,
    val warnings: String?,
    val adverseReactions: String?,
    val pregnancy: String?,
    val mechanism: String?,
    val fetchStatus: String?
)
