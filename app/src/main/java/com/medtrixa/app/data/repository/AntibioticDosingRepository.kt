package com.medtrixa.app.data.repository

import android.content.Context
import com.medtrixa.app.data.model.AntibioticDosingGuideline
import java.io.BufferedReader
import java.io.InputStreamReader

object AntibioticDosingRepository {

    private const val ASSET_FILE = "antibiotic_dosing_guidelines.csv"

    @Volatile private var guidelines: List<AntibioticDosingGuideline> = emptyList()

    fun isLoaded(): Boolean = guidelines.isNotEmpty()

    fun load(context: Context) {
        if (guidelines.isNotEmpty()) return
        synchronized(this) {
            if (guidelines.isNotEmpty()) return

            val result = mutableListOf<AntibioticDosingGuideline>()
            context.assets.open(ASSET_FILE).use { input ->
                BufferedReader(InputStreamReader(input, Charsets.UTF_8)).use { reader ->
                    var header: Map<String, Int>? = null
                    reader.forEachLine { rawLine ->
                        val line = rawLine.trimEnd('\r')
                        if (line.isBlank()) return@forEachLine

                        val fields = parseCsvLine(line)

                        if (header == null) {
                            header = fields.mapIndexed { index, name -> name.trim().removePrefix("\uFEFF") to index }.toMap()
                            return@forEachLine
                        }

                        val h = header!!
                        fun col(name: String): String =
                            h[name]?.let { fields.getOrNull(it)?.trim() } ?: ""

                        result.add(
                            AntibioticDosingGuideline(
                                indication = col("indication"),
                                setting = col("setting"),
                                organisms = col("organisms"),
                                antibiotic = col("antibiotic"),
                                dose = col("dose"),
                                route = col("route"),
                                frequency = col("frequency"),
                                duration = col("duration"),
                                patientGroup = col("patientGroup"),
                                renalAdjustment = col("renalAdjustment"),
                                hepaticAdjustment = col("hepaticAdjustment"),
                                alternatives = col("alternatives"),
                                notes = col("notes"),
                                source = col("source"),
                                sourceStatus = col("sourceStatus"),
                                reviewDate = col("reviewDate"),
                                tier = col("tier"),
                                group = col("group")
                            )
                        )
                    }
                }
            }

            guidelines = result
        }
    }

    fun getAll(): List<AntibioticDosingGuideline> = guidelines

    fun search(query: String): List<AntibioticDosingGuideline> {
        if (query.isBlank()) return guidelines
        val q = query.trim().lowercase()
        return guidelines.filter {
            it.indication.lowercase().contains(q) ||
                it.antibiotic.lowercase().contains(q) ||
                it.organisms.lowercase().contains(q) ||
                it.group.lowercase().contains(q)
        }
    }

    private fun parseCsvLine(line: String): List<String> {
        val fields = mutableListOf<String>()
        val current = StringBuilder()
        var inQuotes = false
        var i = 0
        while (i < line.length) {
            val c = line[i]
            when {
                inQuotes -> when {
                    c == '"' && i + 1 < line.length && line[i + 1] == '"' -> {
                        current.append('"'); i++
                    }
                    c == '"' -> inQuotes = false
                    else -> current.append(c)
                }
                c == '"' -> inQuotes = true
                c == ',' -> {
                    fields.add(current.toString())
                    current.setLength(0)
                }
                else -> current.append(c)
            }
            i++
        }
        fields.add(current.toString())
        return fields
    }
}
