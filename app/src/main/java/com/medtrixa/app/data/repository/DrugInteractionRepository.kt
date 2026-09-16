package com.medtrixa.app.data.repository

import android.content.Context
import com.medtrixa.app.data.model.DrugInteractionResult
import java.io.BufferedReader
import java.io.InputStreamReader

object DrugInteractionRepository {

    private const val ASSET_FILE = "DDInter_2.0.csv"

    @Volatile private var pairIndex: Map<String, DrugInteractionResult>? = null
    @Volatile private var drugNames: List<String> = emptyList()

    fun isLoaded(): Boolean = pairIndex != null

    fun load(context: Context) {
        if (pairIndex != null) return
        synchronized(this) {
            if (pairIndex != null) return

            val index = HashMap<String, DrugInteractionResult>(180_000)
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
                        if (fields.size < 5) return@forEachLine

                        val drugA = fields[1].trim()
                        val drugB = fields[4].trim()
                        val level = fields[6].trim()
                        if (drugA.isEmpty() || drugB.isEmpty()) return@forEachLine

                        index[pairKey(drugA, drugB)] = DrugInteractionResult(drugA, drugB, level)
                        names.add(drugA)
                        names.add(drugB)
                    }
                }
            }

            drugNames = names.sortedBy { it.lowercase() }
            pairIndex = index
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

    fun checkInteraction(drugA: String, drugB: String): DrugInteractionResult? =
        pairIndex?.get(pairKey(drugA, drugB))

    fun isKnownDrug(name: String): Boolean =
        drugNames.any { it.equals(name.trim(), ignoreCase = true) }

    private fun pairKey(a: String, b: String): String {
        val na = a.trim().lowercase()
        val nb = b.trim().lowercase()
        return if (na <= nb) "$na|$nb" else "$nb|$na"
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
