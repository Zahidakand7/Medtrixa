package com.medtrixa.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.medtrixa.data.local.entity.GenericDrug
import com.medtrixa.data.local.entity.BrandName

@Dao
interface DrugDao {
    @Query("SELECT * FROM generics WHERE generic_name LIKE '%' || :query || '%'")
    suspend fun searchGenerics(query: String): List<GenericDrug>

    @Query("SELECT * FROM brand_names WHERE brand_name LIKE '%' || :query || '%'")
    suspend fun searchBrands(query: String): List<BrandName>
}
