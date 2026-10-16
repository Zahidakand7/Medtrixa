package com.medtrixa.app.data.repository

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

object ClinicalPharmacologyRepository {

    data class Entry(val pd: String, val pk: String, val general: String = "")

    private const val ASSET_FILE = "clinical_pharmacology.csv"

    @Volatile private var index: Map<String, Entry>? = null

    // Call from a background thread (e.g. Dispatchers.IO).
    fun load(context: Context) {
        if (index != null) return
        synchronized(this) {
            if (index != null) return
            val map = HashMap<String, Entry>(2_000)
            context.assets.open(ASSET_FILE).use { input ->
                BufferedReader(InputStreamReader(input, Charsets.UTF_8)).use { reader ->
                    var isHeader = true
                    while (true) {
                        val f = readRecord(reader) ?: break
                        if (isHeader) {
                            isHeader = false
                            continue
                        }
                        if (f.size < 3) continue
                        val id = f[0].trim()
                        if (id.isEmpty()) continue
                        map[id.lowercase()] = Entry(f[1].trim(), f[2].trim(), f.getOrNull(3)?.trim().orEmpty())
                    }
                }
            }
            index = map
        }
    }

    fun get(setId: String?): Entry? =
        if (setId.isNullOrBlank()) null else index?.get(setId.trim().lowercase())

    // Fields contain line breaks inside quotes, so records can't be read line by line.
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
                    '\r' -> { }
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
