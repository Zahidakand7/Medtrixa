package com.medtrixa.app.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "product", indices = [Index("ingredientRxcui")])
data class ProductEntity(
    @PrimaryKey val rxcui: Long,
    val ingredientRxcui: Long,
    val strength: String?,
    val form: String?,
    val route: String?
)
