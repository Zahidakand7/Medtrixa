package com.medtrixa.app.data.repository

import com.medtrixa.app.data.model.MedicalCalculator
import com.medtrixa.app.data.model.MedicalContentMetadata

data class CatalogEntry(
    val calculator: MedicalCalculator,
    val metadata: MedicalContentMetadata
)

interface MedicalContentCatalog {

    fun getAll(): List<CatalogEntry>

    fun getById(id: String): CatalogEntry?

    fun search(query: String): List<CatalogEntry>
}
