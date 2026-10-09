package com.medtrixa.app.data.repository

import android.content.Context
import com.medtrixa.app.data.model.LactMedResult
import java.io.BufferedReader
import java.io.InputStreamReader

object LactMedRepository {

    private const val ASSET_FILE = "lactmed.csv"

    @Volatile private var index: Map<String, LactMedResult>? = null
    @Volatile private var drugNames: List<String> = emptyList()

    fun isLoaded(): Boolean = index != null

    // Call from a background thread (e.g. Dispatchers.IO): the file is ~5 MB.
    fun load(context: Context) {
        if (index != null) return
        synchronized(this) {
            if (index != null) return

            val results = ArrayList<LactMedResult>(2_000)

            context.assets.open(ASSET_FILE).use { input ->
                BufferedReader(InputStreamReader(input, Charsets.UTF_8)).use { reader ->
                    var isHeader = true
                    while (true) {
                        val fields = readRecord(reader) ?: break
                        if (isHeader) {
                            isHeader = false
                            continue
                        }
                        if (fields.size < 8) continue

                        val name = fields[0].trim()
                        if (name.isEmpty()) continue

                        results.add(
                            LactMedResult(
                                name = name,
                                synonyms = fields[1].split('|').map { it.trim() }.filter { it.isNotEmpty() },
                                revised = fields[2].trim(),
                                summary = fields[3].trim(),
                                levels = fields[4].trim(),
                                infant = fields[5].trim(),
                                lactation = fields[6].trim(),
                                alternatives = fields[7].trim()
                            )
                        )
                    }
                }
            }

            val map = HashMap<String, LactMedResult>(results.size * 2)
            // Official names first so a synonym never overrides a real monograph name.
            for (r in results) map[r.name.lowercase()] = r
            for (r in results) for (s in r.synonyms) map.putIfAbsent(s.lowercase(), r)

            drugNames = results.map { it.name }.sortedBy { it.lowercase() }
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

    // Matches the monograph name or any synonym.
    fun getLactMed(drugName: String): LactMedResult? =
        index?.get(drugName.trim().lowercase())

    fun isKnownDrug(name: String): Boolean =
        index?.containsKey(name.trim().lowercase()) == true

    // lactmed.csv has line breaks inside quoted fields (paragraphs are separated by a
    // blank line), so records can't be read line by line. Returns null at end of file.
    private fun readRecord(reader: BufferedReader): List<String>? {
        val fields = mutableListOf<String>()
        val current = StringBuilder()
        var inQuotes = false
        var sawAnything = false

        while (true) {
            val code = reader.read()
            if (code == -1) {
                if (!sawAnything) return null
                fields.add(current.toString())
                return fields
            }
            sawAnything = true
            val c = code.toChar()

            if (inQuotes) {
                if (c == '"') {
                    reader.mark(1)
                    val next = reader.read()
                    if (next == '"'.code) {
                        current.append('"')
                    } else {
                        inQuotes = false
                        if (next != -1) reader.reset()
                    }
                } else {
                    current.append(c)
                }
            } else {
                when (c) {
                    '"' -> inQuotes = true
                    ',' -> {
                        fields.add(current.toString())
                        current.setLength(0)
                    }
                    '\r' -> { /* ignore */ }
                    '\n' -> {
                        fields.add(current.toString())
                        return fields
                    }
                    else -> current.append(c)
                }
            }
        }
    }
}
