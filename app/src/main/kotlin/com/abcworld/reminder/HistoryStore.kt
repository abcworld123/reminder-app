package com.abcworld.reminder

import android.content.Context
import androidx.core.content.edit
import org.json.JSONArray
import org.json.JSONException

/** Persists the history of posted notifications to SharedPreferences as a JSON array, like ReminderStore. */
class HistoryStore(context: Context) {

    private val prefs = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    /** The whole history, most recently posted first. */
    fun getAll(): List<HistoryEntry> {
        val raw = prefs.getString(KEY_LIST, "[]")
        return try {
            val arr = JSONArray(raw)
            List(arr.length()) { HistoryEntry.fromJson(arr.getJSONObject(it)) }.sortedByDescending { it.postedAtMillis }
        } catch (_: JSONException) {
            // Corrupted data is treated as an empty list
            emptyList()
        }
    }

    fun add(entry: HistoryEntry) = save(getAll() + entry)

    /** When the user dismisses a notification: keeps the entry but stops restoring it after a reboot. */
    fun markDismissed(id: Int) = save(getAll().map { if (it.id == id) it.copy(showing = false) else it })

    private fun save(list: List<HistoryEntry>) {
        // Keeps only recent entries so the history doesn't grow forever. Notifications still showing
        // are never dropped, however old.
        val kept = list.sortedByDescending { it.postedAtMillis }.filterIndexed { index, entry -> index < MAX_ENTRIES || entry.showing }
        val arr = JSONArray()
        kept.forEach { arr.put(it.toJson()) }
        prefs.edit { putString(KEY_LIST, arr.toString()) }
    }

    private companion object {
        const val PREFS = "history"
        const val KEY_LIST = "list"
        const val MAX_ENTRIES = 500
    }
}
