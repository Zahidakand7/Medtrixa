package com.medtrixa.app.data.repository

import android.content.Context
import com.medtrixa.app.data.model.ContraindicationResult
import java.io.BufferedReader
import java.io.InputStreamReader

object ContraindicationRepository {

    private const val ASSET_FILE = "contraindications.csv"

    @Volatile private var index: Map<String, ContraindicationResult>? = null
    @Volatile private var drugNames: List<String> = emptyList()

    fun isLoaded(): Boolean = index != null

    fun load(context: Context) {
        if (index != null) return
        synchronized(this) {
            if (index != null) return

            val map = HashMap<String, ContraindicationResult>(7_000)
            val names = LinkedHashSet<String>()

            context.assets.open(ASSET_FILE).use { input ->
                BufferedReader(InputStreamReader(input, Charsets.UTF_8)).use { reader ->
                    var isHeader = true
                    reader.forEachLine { rawLine ->
                        val line = rawLine.trimEnd('\r')
                        if (line.isBlank()) return@forEachLine
                        if (isHeader) {
                            isHeader = false
                            return@forEachLine
                        }

                        val fields = parseCsvLine(line)
                        if (fields.size < 2) return@forEachLine

                        val genericName = fields[0].trim()
                        val contraindications = fields[1].trim()
                        val status = fields.getOrNull(2)?.trim() ?: ""
                        if (genericName.isEmpty()) return@forEachLine

                        map[genericName.lowercase()] = ContraindicationResult(genericName, contraindications, status)
                        names.add(genericName)
                    }
                }
            }

            drugNames = names.sortedBy { it.lowercase() }
            index = map
        }
    }

    fun searchNames(query: String, limit: Int = 20): List<String> {
        if (query.isBlank()) return emptyList()
        val q = query.trim().lowercase()
        val starts = mutableListOf<String>()
        val contains = mutableListOf<String>()
        for (name in drugNames) {
            val lower = name.lowercase()
            when {
                lower.startsWith(q) -> starts.add(name)
                lower.contains(q) -> contains.add(name)
            }
            if (starts.size >= limit) break
        }
        return (starts + contains).distinct().take(limit)
    }

    fun getContraindications(genericName: String): ContraindicationResult? =
        index?.get(genericName.trim().lowercase())

    fun isKnownDrug(name: String): Boolean =
        drugNames.any { it.equals(name.trim(), ignoreCase = true) }

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
