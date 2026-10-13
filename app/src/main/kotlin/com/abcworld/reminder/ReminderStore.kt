package com.abcworld.reminder

import android.content.Context
import androidx.core.content.edit
import org.json.JSONArray
import org.json.JSONException

/** Persists scheduled reminders to SharedPreferences as a JSON array, with no extra dependency such as Room. */
class ReminderStore(context: Context) {

    private val prefs = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    /** Issues a unique positive id to use as the PendingIntent requestCode. */
    fun nextId(): Int {
        val id = prefs.getInt(KEY_NEXT_ID, 1)
        prefs.edit { putInt(KEY_NEXT_ID, id + 1) }
        return id
    }

    /** All scheduled reminders, sorted by trigger time ascending. */
    fun getAll(): List<Reminder> {
        val raw = prefs.getString(KEY_LIST, "[]")
        return try {
            val arr = JSONArray(raw)
            List(arr.length()) { Reminder.fromJson(arr.getJSONObject(it)) }.sortedBy { it.triggerAtMillis }
        } catch (_: JSONException) {
            // Corrupted data is treated as an empty list
            emptyList()
        }
    }

    fun add(reminder: Reminder) = save(getAll() + reminder)

    fun remove(id: Int) = save(getAll().filterNot { it.id == id })

    private fun save(list: List<Reminder>) {
        val arr = JSONArray()
        list.forEach { arr.put(it.toJson()) }
        prefs.edit { putString(KEY_LIST, arr.toString()) }
    }

    private companion object {
        const val PREFS = "reminders"
        const val KEY_LIST = "list"
        const val KEY_NEXT_ID = "next_id"
    }
}
