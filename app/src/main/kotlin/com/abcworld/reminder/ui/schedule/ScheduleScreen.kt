package com.abcworld.reminder.ui.schedule

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.abcworld.reminder.Priority
import com.abcworld.reminder.R
import com.abcworld.reminder.Reminder
import com.abcworld.reminder.ReminderScheduler
import com.abcworld.reminder.ReminderStore
import com.abcworld.reminder.ui.common.DatePickDialog
import com.abcworld.reminder.ui.common.LabeledRow
import com.abcworld.reminder.ui.common.LabeledRowFontSize
import com.abcworld.reminder.ui.common.PrioritySelector
import com.abcworld.reminder.ui.common.RectButton
import com.abcworld.reminder.ui.common.ReminderTopBar
import com.abcworld.reminder.ui.common.TimeKeypadDialog
import com.abcworld.reminder.ui.common.UnderlineField
import com.abcworld.reminder.ui.common.UnderlineValue
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

private val DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

/** Gap between input rows. Narrower than on the notify screen because there are four rows. */
private val FIELD_GAP = 28.dp

/**
 * The scheduled-reminder screen. Pick a date, time, and priority to schedule a reminder; the list
 * below shows what is scheduled and lets you cancel it. As on the notify screen, the inputs are
 * stacked vertically in the middle of the screen. [showing] is whether this tab is currently visible.
 */
@Composable
fun ScheduleScreen(showing: Boolean) {
    val context = LocalContext.current
    val hint = stringResource(R.string.input)
    val store = remember { ReminderStore(context) }
    val zone = remember { ZoneId.systemDefault() }

    var title by rememberSaveable { mutableStateOf("") }
    var content by rememberSaveable { mutableStateOf("") }
    var priority by rememberSaveable { mutableStateOf(Priority.MEDIUM) }
    // The default time is one hour from now.
    var selectedMillis by rememberSaveable {
        mutableLongStateOf(LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.MINUTES).atZone(zone).toInstant().toEpochMilli())
    }
    val selected = Instant.ofEpochMilli(selectedMillis).atZone(zone).toLocalDateTime()
    val select = { dateTime: LocalDateTime -> selectedMillis = dateTime.atZone(zone).toInstant().toEpochMilli() }

    var reminders by remember { mutableStateOf(store.getAll()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    // Drops reminders that have fired from the list: on returning to the app and on switching to this tab.
    LifecycleResumeEffect(showing) {
        reminders = store.getAll()
        onPauseOrDispose {}
    }

    Scaffold(
        topBar = { ReminderTopBar(stringResource(R.string.nav_schedule)) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(56.dp))
            LabeledRow(stringResource(R.string.label_title)) { modifier ->
                UnderlineField(title, { title = it }, hint, LabeledRowFontSize, modifier)
            }
            Spacer(Modifier.height(FIELD_GAP))
            LabeledRow(stringResource(R.string.label_content)) { modifier ->
                UnderlineField(content, { content = it }, hint, LabeledRowFontSize, modifier)
            }
            Spacer(Modifier.height(FIELD_GAP))
            // Date and time are not typed in; tapping each one opens its dialog.
            LabeledRow(stringResource(R.string.label_date)) { modifier ->
                UnderlineValue(selected.toLocalDate().toString(), { showDatePicker = true }, LabeledRowFontSize, modifier)
            }
            Spacer(Modifier.height(FIELD_GAP))
            LabeledRow(stringResource(R.string.label_time)) { modifier ->
                UnderlineValue("%02d:%02d".format(selected.hour, selected.minute), { showTimePicker = true }, LabeledRowFontSize, modifier)
            }

            Spacer(Modifier.height(32.dp))
            PrioritySelector(selected = priority, onSelect = { priority = it }, fontSize = 20.sp, gap = 46.dp)

            Spacer(Modifier.height(44.dp))
            RectButton(
                text = stringResource(R.string.schedule),
                onClick = {
                    val reminder = schedule(context, store, title, content, priority, selectedMillis)
                    if (reminder != null) {
                        title = ""
                        content = ""
                        reminders = store.getAll()
                    }
                },
                modifier = Modifier.size(width = 109.dp, height = 59.dp),
                fontSize = 20.sp,
            )
            Spacer(Modifier.height(24.dp))

            ReminderList(
                reminders = reminders,
                zone = zone,
                onCancel = { reminder ->
                    ReminderScheduler.cancel(context, reminder)
                    store.remove(reminder.id)
                    Toast.makeText(context, R.string.toast_canceled, Toast.LENGTH_SHORT).show()
                    reminders = store.getAll()
                },
            )
        }
    }

    if (showDatePicker) {
        DatePickDialog(
            initial = selected.toLocalDate(),
            onDismiss = { showDatePicker = false },
            onPick = { date ->
                select(LocalDateTime.of(date, selected.toLocalTime()))
                showDatePicker = false
            },
        )
    }

    if (showTimePicker) {
        TimeKeypadDialog(
            initialHour = selected.hour,
            initialMinute = selected.minute,
            onDismiss = { showTimePicker = false },
            onConfirm = { hour, minute ->
                select(LocalDateTime.of(selected.toLocalDate(), LocalTime.of(hour, minute)))
                showTimePicker = false
            },
        )
    }
}

/** The scheduled list. Unlike the centered inputs, it spans the full width and is left-aligned. */
@Composable
private fun ReminderList(reminders: List<Reminder>, zone: ZoneId, onCancel: (Reminder) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
        )

        Text(stringResource(R.string.scheduled_list), fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        if (reminders.isEmpty()) {
            Text(stringResource(R.string.no_scheduled), fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        reminders.forEach { reminder ->
            ReminderRow(
                reminder = reminder,
                time = Instant.ofEpochMilli(reminder.triggerAtMillis).atZone(zone).format(DATE_TIME_FORMATTER),
                onCancel = { onCancel(reminder) },
            )
        }
    }
}

/** One scheduled reminder: title, content, scheduled time, and cancel on the right. */
@Composable
private fun ReminderRow(reminder: Reminder, time: String, onCancel: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(reminder.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(reminder.content, fontSize = 14.sp)
            Text(time, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
        }
        TextButton(onClick = onCancel, shape = RoundedCornerShape(4.dp)) {
            Text(stringResource(R.string.cancel), fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
    }
}

/**
 * Saves the reminder and sets its alarm. If the time is in the past or the exact-alarm permission is
 * missing, only tells the user and returns null.
 */
private fun schedule(
    context: Context,
    store: ReminderStore,
    title: String,
    content: String,
    priority: Priority,
    triggerAtMillis: Long,
): Reminder? {
    if (triggerAtMillis <= System.currentTimeMillis()) {
        Toast.makeText(context, R.string.toast_past_time, Toast.LENGTH_SHORT).show()
        return null
    }

    if (!ReminderScheduler.canScheduleExact(context)) {
        Toast.makeText(context, R.string.toast_need_exact_alarm, Toast.LENGTH_LONG).show()
        context.startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, "package:${context.packageName}".toUri()))
        return null
    }

    val reminder = Reminder(
        id = store.nextId(),
        title = title.ifEmpty { context.getString(R.string.default_title) },
        content = content,
        priority = priority,
        triggerAtMillis = triggerAtMillis,
    )
    store.add(reminder)
    ReminderScheduler.schedule(context, reminder)

    val time = Instant.ofEpochMilli(triggerAtMillis).atZone(ZoneId.systemDefault()).format(DATE_TIME_FORMATTER)
    Toast.makeText(context, context.getString(R.string.toast_scheduled, time), Toast.LENGTH_SHORT).show()
    return reminder
}
