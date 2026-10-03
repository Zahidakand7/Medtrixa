package com.medtrixa.app.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "brand", indices = [Index("ingredientRxcui"), Index("name")])
data class BrandEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ingredientRxcui: Long,
    val name: String
)
