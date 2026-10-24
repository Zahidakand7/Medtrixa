package com.medtrixa.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "brand_names")
data class BrandName(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val generic_id: Long,
    val brand_name: String,
    val manufacturer: String?,
    val formulation: String?,
    val strength: String?
)
