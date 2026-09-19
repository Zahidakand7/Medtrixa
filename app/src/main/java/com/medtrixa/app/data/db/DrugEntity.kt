package com.medtrixa.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "drugs")
data class DrugEntity(
    @PrimaryKey val id: String,
    val genericName: String,
    val brandName: String,
    val drugClass: String,
    val formulation: String,
    val strength: String,
    val route: String,
    val manufacturer: String
)
