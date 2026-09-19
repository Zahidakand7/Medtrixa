package com.medtrixa.app.data.db

import androidx.room.Dao
import androidx.room.Query

@Dao
interface DrugDao {
    @Query("SELECT * FROM drugs WHERE genericName LIKE '%' || :query || '%' OR brandName LIKE '%' || :query || '%' LIMIT 50")
    suspend fun search(query: String): List<DrugEntity>

    @Query("SELECT * FROM drugs WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): DrugEntity?
}
