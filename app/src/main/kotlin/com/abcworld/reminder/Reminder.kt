package com.abcworld.reminder

import org.json.JSONObject

/** One scheduled reminder. The id doubles as the AlarmManager PendingIntent requestCode and the store key. */
data class Reminder(
    val id: Int,
    val title: String,
    val content: String,
    val priority: Priority,
    val triggerAtMillis: Long,
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("id", id)
        put("title", title)
        put("content", content)
        put("priority", priority.name)
        put("triggerAtMillis", triggerAtMillis)
    }

    companion object {
        fun fromJson(o: JSONObject): Reminder = Reminder(
            id = o.getInt("id"),
            title = o.optString("title", ""),
            content = o.optString("content", ""),
            priority = Priority.entries.find { it.name == o.getString("priority") } ?: Priority.MEDIUM,
            triggerAtMillis = o.getLong("triggerAtMillis"),
        )
    }
}
