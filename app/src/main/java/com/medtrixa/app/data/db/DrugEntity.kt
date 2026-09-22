package com.medtrixa.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "drugs")
data class DrugEntity(
    @PrimaryKey(autoGenerate = true) val uid: Long = 0,
    val genericName: String,
    val formulation: String?,
    val strength: String?,
    val route: String?,
    val rxcui: String?,
    val pharmacologicClass: String?,
    val initialApproval: String?,
    val company: String?,
    val marketingDate: String?,
    val brandName: String?
)
