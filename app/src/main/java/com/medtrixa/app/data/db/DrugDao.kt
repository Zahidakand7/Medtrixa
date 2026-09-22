package com.medtrixa.app.data.db

import androidx.room.Dao
import androidx.room.Query

@Dao
interface DrugDao {
    @Query("SELECT * FROM drugs WHERE genericName LIKE :query || '%' GROUP BY genericName ORDER BY genericName ASC LIMIT 50")
    suspend fun search(query: String): List<DrugEntity>

    @Query("SELECT * FROM drugs WHERE genericName = :genericName")
    suspend fun getStrengths(genericName: String): List<DrugEntity>

    @Query("SELECT * FROM drug_labels WHERE genericName = :genericName LIMIT 1")
    suspend fun getLabel(genericName: String): DrugLabelEntity?
}
