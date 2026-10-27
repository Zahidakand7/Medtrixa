package com.medtrixa.app.data.db

import androidx.room.Dao
import androidx.room.Query

data class SearchHit(
    val rxcui: Long,
    val name: String,
    val drugClass: String?,
    val matchedBrand: String?,
    val brandCount: Int
)

@Dao
interface DrugDao {
    @Query(
        """SELECT i.rxcui AS rxcui, i.name AS name, i.drugClass AS drugClass,
        (SELECT b.name FROM brand b WHERE b.ingredientRxcui = i.rxcui AND b.name LIKE :q || '%' ORDER BY b.name LIMIT 1) AS matchedBrand,
        (SELECT COUNT(*) FROM brand b WHERE b.ingredientRxcui = i.rxcui) AS brandCount
        FROM ingredient i
        WHERE i.name LIKE '%' || :q || '%'
           OR EXISTS (SELECT 1 FROM brand b WHERE b.ingredientRxcui = i.rxcui AND b.name LIKE :q || '%')
        ORDER BY ((i.name LIKE :q || '%') OR EXISTS (SELECT 1 FROM brand b WHERE b.ingredientRxcui = i.rxcui AND b.name LIKE :q || '%')) DESC, (length(i.name) - length(replace(i.name, '/', ''))) ASC, i.name ASC
        LIMIT 50"""
    )
    suspend fun search(q: String): List<SearchHit>

    @Query("SELECT * FROM ingredient WHERE rxcui = :rxcui LIMIT 1")
    suspend fun getIngredient(rxcui: Long): IngredientEntity?

    @Query("SELECT * FROM ingredient WHERE name = :name COLLATE NOCASE LIMIT 1")
    suspend fun getIngredientByName(name: String): IngredientEntity?

    @Query("SELECT * FROM product WHERE ingredientRxcui = :rxcui ORDER BY strength")
    suspend fun getProducts(rxcui: Long): List<ProductEntity>

    @Query("SELECT name FROM brand WHERE ingredientRxcui = :rxcui ORDER BY name")
    suspend fun getBrands(rxcui: Long): List<String>

    @Query("SELECT * FROM label WHERE ingredientRxcui = :rxcui LIMIT 1")
    suspend fun getLabel(rxcui: Long): DrugLabelEntity?

    @Query("""SELECT i.rxcui AS rxcui, i.name AS name, i.drugClass AS drugClass, NULL AS matchedBrand,
        (SELECT COUNT(*) FROM brand b WHERE b.ingredientRxcui = i.rxcui) AS brandCount
        FROM ingredient i WHERE i.drugClass = :cls COLLATE NOCASE AND i.rxcui != :rxcui ORDER BY i.name""")
    suspend fun getSameClass(cls: String, rxcui: Long): List<SearchHit>

    @Query("SELECT drugClass AS drugClass, COUNT(*) AS n FROM ingredient WHERE drugClass IS NOT NULL AND drugClass != '' GROUP BY drugClass")
    suspend fun getClassCounts(): List<ClassCount>

    @Query("SELECT COUNT(*) FROM ingredient")
    suspend fun countAll(): Int

    @Query("""SELECT i.rxcui AS rxcui, i.name AS name, i.drugClass AS drugClass, NULL AS matchedBrand,
        (SELECT COUNT(*) FROM brand b WHERE b.ingredientRxcui = i.rxcui) AS brandCount
        FROM ingredient i WHERE i.drugClass IN (:classes) ORDER BY i.name COLLATE NOCASE""")
    suspend fun getByClasses(classes: List<String>): List<SearchHit>

    @Query("""SELECT i.rxcui AS rxcui, i.name AS name, i.drugClass AS drugClass, NULL AS matchedBrand,
        (SELECT COUNT(*) FROM brand b WHERE b.ingredientRxcui = i.rxcui) AS brandCount
        FROM ingredient i ORDER BY i.name COLLATE NOCASE""")
    suspend fun getAllDrugs(): List<SearchHit>

    @Query("SELECT name FROM ingredient")
    suspend fun getAllNames(): List<String>
}

data class ClassCount(val drugClass: String, val n: Int)
