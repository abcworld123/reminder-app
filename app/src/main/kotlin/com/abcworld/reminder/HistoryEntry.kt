package com.abcworld.reminder

import org.json.JSONObject

/**
 * The record of one posted notification. The id doubles as the notification id and the store key.
 * [showing] is whether it is still in the notification shade: it turns false when the user dismisses
 * it, and only entries that are still true are restored after a reboot.
 */
data class HistoryEntry(
    val id: Int,
    val title: String,
    val content: String,
    val priority: Priority,
    val postedAtMillis: Long,
    val showing: Boolean = true,
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("id", id)
        put("title", title)
        put("content", content)
        put("priority", priority.name)
        put("postedAtMillis", postedAtMillis)
        put("showing", showing)
    }

    companion object {
        fun fromJson(o: JSONObject): HistoryEntry = HistoryEntry(
            id = o.getInt("id"),
            title = o.optString("title", ""),
            content = o.optString("content", ""),
            priority = Priority.entries.find { it.name == o.getString("priority") } ?: Priority.MEDIUM,
            postedAtMillis = o.getLong("postedAtMillis"),
            showing = o.optBoolean("showing", false),
        )
    }
}
