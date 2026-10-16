package com.medtrixa.app.data.repository

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

data class ClsiBreakpoint(
    val antibiotic: String,
    val enterobacterales: String,
    val pseudomonas: String,
    val crab: String,
    val stenotrophomonas: String,
    val footnote: String
)

object AntibioticBreakpointRepository {

    private const val ASSET_FILE = "clsi_breakpoints_2026.csv"

    @Volatile private var items: List<ClsiBreakpoint> = emptyList()

    fun load(context: Context) {
        if (items.isNotEmpty()) return
        synchronized(this) {
            if (items.isNotEmpty()) return
            val result = mutableListOf<ClsiBreakpoint>()
            context.assets.open(ASSET_FILE).use { input ->
                BufferedReader(InputStreamReader(input, Charsets.UTF_8)).use { reader ->
                    var first = true
                    reader.forEachLine { raw ->
                        val line = raw.trimEnd('\r')
                        if (line.isBlank()) return@forEachLine
                        if (first) { first = false; return@forEachLine }
                        val f = parse(line)
                        fun c(i: Int) = f.getOrNull(i)?.trim() ?: ""
                        result.add(ClsiBreakpoint(c(0), c(1), c(2), c(3), c(4), c(5)))
                    }
                }
            }
            items = result
        }
    }

    fun search(query: String): List<ClsiBreakpoint> {
        if (query.isBlank()) return items
        val q = query.trim().lowercase()
        return items.filter { it.antibiotic.lowercase().contains(q) }
    }

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
