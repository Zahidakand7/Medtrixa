package com.medtrixa.app.ui.screens

import android.content.Context
import com.medtrixa.app.data.db.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object VoiceDrugMatch {
    private var cache: List<Pair<String, String>>? = null

    private fun norm(s: String): String =
        s.lowercase()
            .filter { it in 'a'..'z' }
            .replace(Regex("(.)\\1+"), "$1")
            .replace("ph", "f")
            .replace("ck", "k")

    private fun similarity(a: String, b: String): Double {
        if (a.isEmpty() || b.isEmpty()) return 0.0
        var prev = IntArray(b.length + 1) { it }
        var cur = IntArray(b.length + 1)
        for (i in 1..a.length) {
            cur[0] = i
            for (j in 1..b.length) {
                val cost = if (a[i - 1] == b[j - 1]) 0 else 1
                cur[j] = minOf(prev[j] + 1, cur[j - 1] + 1, prev[j - 1] + cost)
            }
            val t = prev
            prev = cur
            cur = t
        }
        return 1.0 - prev[b.length].toDouble() / maxOf(a.length, b.length)
    }

    /** Returns the text to search for: a spoken phrase that already matches, a clear fuzzy match, or the first phrase. */
    suspend fun resolve(context: Context, spoken: List<String>): String {
        val first = spoken.firstOrNull()?.trim().orEmpty()
        if (first.isEmpty()) return first
        val dao = AppDatabase.getInstance(context).drugDao()

        for (s in spoken) {
            val q = s.trim()
            if (q.isNotEmpty() && dao.search(q).any { it.name.startsWith(q, ignoreCase = true) }) return q
        }

        return withContext(Dispatchers.Default) {
            val names = cache ?: dao.getAllNames()
                .flatMap { it.split(" / ") }
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .distinct()
                .map { it to norm(it) }
                .also { cache = it }

            val heard = spoken.map { norm(it) }.filter { it.isNotEmpty() }
            val scored = names
                .map { (name, n) -> name to (heard.maxOfOrNull { similarity(it, n) } ?: 0.0) }
                .sortedByDescending { it.second }

            val best = scored.firstOrNull()
            val second = scored.getOrNull(1)
            if (best != null && best.second >= 0.6 && best.second - (second?.second ?: 0.0) >= 0.08) best.first else first
        }
    }
}
