package com.abcworld.reminder.ui.history

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.abcworld.reminder.HistoryEntry
import com.abcworld.reminder.HistoryStore
import com.abcworld.reminder.Priority
import com.abcworld.reminder.R
import com.abcworld.reminder.ui.common.ReminderTopBar
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

/**
 * The history screen. Lists every notification posted so far (instant ones, and scheduled reminders
 * that have fired), most recent first. [showing] is whether this tab is currently visible.
 */
@Composable
fun HistoryScreen(showing: Boolean) {
    val context = LocalContext.current
    val store = remember { HistoryStore(context) }
    val zone = remember { ZoneId.systemDefault() }
    var entries by remember { mutableStateOf(store.getAll()) }

    // Picks up notifications posted from other tabs or receivers: on returning to the app and on
    // switching to this tab.
    LifecycleResumeEffect(showing) {
        entries = store.getAll()
        onPauseOrDispose {}
    }

    Scaffold(
        topBar = { ReminderTopBar(stringResource(R.string.nav_history)) },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        ) {
            if (entries.isEmpty()) {
                item {
                    Text(
                        stringResource(R.string.no_history),
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp),
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            items(entries) { entry ->
                HistoryRow(
                    entry = entry,
                    time = Instant.ofEpochMilli(entry.postedAtMillis).atZone(zone).format(DATE_TIME_FORMATTER),
                )
            }
        }
    }
}

/** One history entry: title, content, and time posted. Urgent notifications are marked on the right. */
@Composable
private fun HistoryRow(entry: HistoryEntry, time: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(entry.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            if (entry.content.isNotEmpty()) {
                Text(entry.content, fontSize = 14.sp)
            }
            Text(time, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
        }
        if (entry.priority == Priority.HIGH) {
            Text(
                stringResource(R.string.priority_high),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}
