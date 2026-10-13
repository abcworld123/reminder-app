package com.abcworld.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

/**
 * Schedules and cancels reminders with AlarmManager. At the trigger time ReminderReceiver wakes up
 * and posts the notification.
 */
object ReminderScheduler {

    const val EXTRA_ID = "extra_id"
    const val EXTRA_TITLE = "extra_title"
    const val EXTRA_CONTENT = "extra_content"
    const val EXTRA_PRIORITY = "extra_priority"

    fun schedule(context: Context, reminder: Reminder) {
        val am = context.getSystemService(AlarmManager::class.java) ?: return
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, reminder.triggerAtMillis, pendingIntent(context, reminder))
    }

    fun cancel(context: Context, reminder: Reminder) {
        val am = context.getSystemService(AlarmManager::class.java) ?: return
        am.cancel(pendingIntent(context, reminder))
    }

    /**
     * Always true when USE_EXACT_ALARM is declared. Checked defensively so the caller can send the
     * user to settings if the permission is missing.
     */
    fun canScheduleExact(context: Context): Boolean =
        context.getSystemService(AlarmManager::class.java)?.canScheduleExactAlarms() == true

    private fun pendingIntent(context: Context, reminder: Reminder): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java)
            .putExtra(EXTRA_ID, reminder.id)
            .putExtra(EXTRA_TITLE, reminder.title)
            .putExtra(EXTRA_CONTENT, reminder.content)
            .putExtra(EXTRA_PRIORITY, reminder.priority.name)
        return PendingIntent.getBroadcast(
            context,
            reminder.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}
