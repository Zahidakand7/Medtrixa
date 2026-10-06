package com.medtrixa.app.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "ingredient", indices = [Index("name")])
data class IngredientEntity(
    @PrimaryKey val rxcui: Long,
    val name: String,
    val kind: String,
    val drugClass: String?,
    @ColumnInfo(name = "half_life_text") val halfLifeText: String? = null
)
