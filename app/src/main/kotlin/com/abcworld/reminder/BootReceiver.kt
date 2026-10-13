package com.abcworld.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Re-registers the saved reminders after a reboot, which is needed because AlarmManager alarms don't
 * survive one. Reminders already in the past are cleaned up. Notifications that were in the shade
 * are lost on reboot too, so they are re-posted here as well.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        Notifier.restore(context)

        val store = ReminderStore(context)
        val now = System.currentTimeMillis()
        store.getAll().forEach { reminder ->
            if (reminder.triggerAtMillis > now) {
                ReminderScheduler.schedule(context, reminder)
            } else {
                store.remove(reminder.id)
            }
        }
    }
}
