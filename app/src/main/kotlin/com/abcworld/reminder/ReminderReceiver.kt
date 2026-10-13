package com.abcworld.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/** Woken by AlarmManager at the scheduled time. Posts the notification and removes the reminder from the store. */
class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getIntExtra(ReminderScheduler.EXTRA_ID, -1)
        val title = intent.getStringExtra(ReminderScheduler.EXTRA_TITLE)
        val content = intent.getStringExtra(ReminderScheduler.EXTRA_CONTENT).orEmpty()
        val priorityName = intent.getStringExtra(ReminderScheduler.EXTRA_PRIORITY)
        val priority = Priority.entries.find { it.name == priorityName } ?: Priority.MEDIUM

        Notifier.post(context, title.orEmpty().ifEmpty { context.getString(R.string.default_title) }, content, priority, popup = true)

        if (id != -1) {
            ReminderStore(context).remove(id)
        }
    }
}
