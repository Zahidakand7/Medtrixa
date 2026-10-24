package com.medtrixa.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "generics")
data class GenericDrug(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val rxcui: String?,
    val generic_name: String,
    val indication: String?,
    val mechanism_of_action: String?,
    val pregnancy_category: String?,
    val boxed_warning: String?
)
