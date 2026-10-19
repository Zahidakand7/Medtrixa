package com.medtrixa.app.data.repository

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

data class LabReference(
    val name: String,
    val category: String,
    val unit: String,
    val lowF: Double?,
    val highF: Double?,
    val lowM: Double?,
    val highM: Double?,
    val bands: String,
    val note: String
) {
    val sexSpecific: Boolean
        get() = lowF != lowM || highF != highM
}

object LabReferenceRepository {

    private const val ASSET_FILE = "lab_reference_ranges.csv"

    @Volatile private var items: List<LabReference> = emptyList()

    fun isLoaded(): Boolean = items.isNotEmpty()

    fun load(context: Context) {
        if (items.isNotEmpty()) return
        synchronized(this) {
            if (items.isNotEmpty()) return
            val result = mutableListOf<LabReference>()
            context.assets.open(ASSET_FILE).use { input ->
                BufferedReader(InputStreamReader(input, Charsets.UTF_8)).use { reader ->
                    var first = true
                    reader.forEachLine { raw ->
                        val line = raw.trimEnd('\r')
                        if (line.isBlank()) return@forEachLine
                        if (first) { first = false; return@forEachLine }
                        val f = parse(line)
                        fun c(i: Int) = f.getOrNull(i)?.trim() ?: ""
                        fun d(i: Int) = c(i).toDoubleOrNull()
                        result.add(
                            LabReference(
                                name = c(0).removePrefix("\uFEFF"),
                                category = c(1),
                                unit = c(2),
                                lowF = d(3),
                                highF = d(4),
                                lowM = d(5),
                                highM = d(6),
                                bands = c(7),
                                note = c(8)
                            )
                        )
                    }
                }
            }
            items = result
        }
    }

    fun getAll(): List<LabReference> = items

    private fun parse(line: String): List<String> {
        val out = mutableListOf<String>()
        val cur = StringBuilder()
        var inQ = false
        var i = 0
        while (i < line.length) {
            val ch = line[i]
            when {
                inQ -> when {
                    ch == '"' && i + 1 < line.length && line[i + 1] == '"' -> { cur.append('"'); i++ }
                    ch == '"' -> inQ = false
                    else -> cur.append(ch)
                }
                ch == '"' -> inQ = true
                ch == ',' -> { out.add(cur.toString()); cur.setLength(0) }
                else -> cur.append(ch)
            }
            i++
        }
        out.add(cur.toString())
        return out
    }
}
