package com.abcworld.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Woken when the user dismisses a notification by swiping it away (or with clear all). Marks that
 * notification so it isn't restored after a reboot.
 */
class NotificationDismissReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getIntExtra(Notifier.EXTRA_NOTIFICATION_ID, -1)
        if (id != -1) {
            HistoryStore(context).markDismissed(id)
        }
    }
}
