package com.medtrixa.app.data

import android.content.Context

object RecentDrugStore {
    private const val PREFS = "recent_drugs"
    private const val KEY = "names"
    private const val MAX_ITEMS = 10

    fun addRecent(context: Context, genericName: String) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val current = getRecents(context).toMutableList()
        current.removeAll { it.equals(genericName, ignoreCase = true) }
        current.add(0, genericName)
        val trimmed = current.take(MAX_ITEMS)
        prefs.edit().putString(KEY, trimmed.joinToString("|")).apply()
    }

    fun getRecents(context: Context): List<String> {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val raw = prefs.getString(KEY, null) ?: return emptyList()
        return raw.split("|").filter { it.isNotBlank() }
    }

    fun removeRecent(context: Context, genericName: String) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val current = getRecents(context).filterNot { it.equals(genericName, ignoreCase = true) }
        prefs.edit().putString(KEY, current.joinToString("|")).apply()
    }
}
