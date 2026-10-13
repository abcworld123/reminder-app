package com.abcworld.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.atomic.AtomicInteger

/**
 * Shared helper for posting notifications. Instant notifications (the notify screen) and scheduled
 * reminders (ReminderReceiver) both go through it.
 * Each priority has its own group, posted together with a group summary notification.
 * Posted notifications are recorded in HistoryStore, which feeds the history screen and restoring
 * after a reboot.
 */
object Notifier {

    const val EXTRA_NOTIFICATION_ID = "extra_notification_id"

    private val counter = AtomicInteger(0)
    private val ID_SUFFIX = DateTimeFormatter.ofPattern("ddHHmmss")

    /**
     * With [popup], a heads-up notification appears briefly at the top of the screen, with sound and
     * vibration (scheduled reminders). Otherwise only an icon appears silently in the status bar
     * (instant notifications).
     */
    fun post(context: Context, title: String, content: String, priority: Priority, popup: Boolean = false) {
        val entry = HistoryEntry(nextId(), title, content, priority, System.currentTimeMillis())
        HistoryStore(context).add(entry)
        show(context, entry, popup)
    }

    /**
     * Re-posts notifications that a reboot cleared from the notification shade. Only the ones the user
     * hasn't dismissed, with no sound or heads-up, under their original id and time. Since the ids are
     * reused, calling this repeatedly doesn't add notifications.
     */
    fun restore(context: Context) {
        // Posts the oldest first so the most recent ends up on top.
        HistoryStore(context).getAll().filter { it.showing }.reversed().forEach { show(context, it, popup = false) }
    }

    private fun show(context: Context, entry: HistoryEntry, popup: Boolean) {
        val priority = entry.priority
        val manager = context.getSystemService(NotificationManager::class.java)
        val quietChannel = quietChannel(context, manager)
        val channel = if (popup) popupChannel(context, manager) else quietChannel
        val group: String
        val groupId: Int
        val icon: Int

        if (priority == Priority.HIGH) {
            group = "com.abcworld.reminder.high"
            groupId = 1
            icon = R.drawable.siren
        } else {
            group = "com.abcworld.reminder.medium"
            groupId = 2
            icon = R.drawable.ic_stat_bell
        }

        // The summary always stays on the quiet channel with no heads-up: the one that should pop up
        // is the single notification under it.
        val summary = NotificationCompat.Builder(context, quietChannel)
            .setSmallIcon(icon)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setGroup(group)
            .setGroupSummary(true)
            .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)

        val notification = NotificationCompat.Builder(context, channel)
            .setSmallIcon(icon)
            .setContentTitle(entry.title)
            .setContentText(entry.content)
            .setWhen(entry.postedAtMillis)
            .setGroup(group)
            .setAutoCancel(true)
            .setDeleteIntent(dismissIntent(context, entry.id))

        if (priority == Priority.HIGH) {
            // The image shown next to the notification in the opened shade. On some devices the small
            // icon slot is taken by the app icon instead.
            notification.setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.siren))
        }

        manager.notify(entry.id, notification.build())
        manager.notify(groupId, summary.build())
    }

    /** The intent that wakes NotificationDismissReceiver when the user dismisses a notification. */
    private fun dismissIntent(context: Context, id: Int): PendingIntent {
        val intent = Intent(context, NotificationDismissReceiver::class.java).putExtra(EXTRA_NOTIFICATION_ID, id)
        return PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    /** For instant notifications: they pile up silently, with no heads-up. */
    private fun quietChannel(context: Context, manager: NotificationManager): String {
        val id = context.getString(R.string.notification_channel_id)
        val name = context.getString(R.string.notification_channel_name)
        manager.createNotificationChannel(NotificationChannel(id, name, NotificationManager.IMPORTANCE_LOW))
        return id
    }

    /** For scheduled reminders: heads-up importance, with the device's default notification sound and vibration. */
    private fun popupChannel(context: Context, manager: NotificationManager): String {
        // The old channel, created without sound or vibration. An app can't change a channel's sound
        // or vibration once it exists, so the channel is recreated under a new id and the old one deleted.
        manager.deleteNotificationChannel(SILENT_POPUP_CHANNEL_ID)

        val id = context.getString(R.string.popup_channel_id)
        val name = context.getString(R.string.popup_channel_name)
        val channel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH).apply {
            enableVibration(true)
        }
        manager.createNotificationChannel(channel)
        return id
    }

    private const val SILENT_POPUP_CHANNEL_ID = "scheduled_channel"

    /**
     * A distinct id per notification: a counter that increments while the process runs, followed by
     * the day, hour, minute, and second. Wrapped back into the Int range if it exceeds it.
     */
    private fun nextId(): Int =
        (counter.incrementAndGet().toString() + LocalDateTime.now().format(ID_SUFFIX)).toLong().mod(Int.MAX_VALUE)
}
