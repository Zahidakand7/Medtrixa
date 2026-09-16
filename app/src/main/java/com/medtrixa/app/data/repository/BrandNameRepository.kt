package com.medtrixa.app.data.repository

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

object BrandNameRepository {

    private const val ASSET_FILE = "brand_generic_map.csv"

    @Volatile private var brandIndex: Map<String, List<String>>? = null
    @Volatile private var brandNames: List<String> = emptyList()

    fun isLoaded(): Boolean = brandIndex != null

    fun load(context: Context) {
        if (brandIndex != null) return
        synchronized(this) {
            if (brandIndex != null) return

            val index = HashMap<String, List<String>>(45_000)
            val names = mutableListOf<String>()

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

                        val brand = fields[0].trim()
                        val generics = fields[1].split('|').map { it.trim() }.filter { it.isNotEmpty() }
                        if (brand.isEmpty() || generics.isEmpty()) return@forEachLine

                        index[brand.lowercase()] = generics
                        names.add(brand)
                    }
                }
            }

            brandNames = names.sortedBy { it.lowercase() }
            brandIndex = index
        }
    }

    fun resolveExact(name: String): List<String>? = brandIndex?.get(name.trim().lowercase())

    fun searchBrands(query: String, limit: Int = 20): List<String> {
        if (query.isBlank()) return emptyList()
        val q = query.trim().lowercase()
        val starts = mutableListOf<String>()
        val contains = mutableListOf<String>()
        for (name in brandNames) {
            val lower = name.lowercase()
            when {
                lower.startsWith(q) -> starts.add(name)
                lower.contains(q) -> contains.add(name)
            }
            if (starts.size >= limit) break
        }
        return (starts + contains).distinct().take(limit)
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
