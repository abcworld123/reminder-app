package com.abcworld.reminder.ui.notify

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abcworld.reminder.Notifier
import com.abcworld.reminder.Priority
import com.abcworld.reminder.R
import com.abcworld.reminder.ui.common.LabeledRow
import com.abcworld.reminder.ui.common.LabeledRowFontSize
import com.abcworld.reminder.ui.common.PrioritySelector
import com.abcworld.reminder.ui.common.RectButton
import com.abcworld.reminder.ui.common.ReminderTopBar
import com.abcworld.reminder.ui.common.UnderlineField

/**
 * The instant-notification screen. Enter a title and content, pick a priority, and saving posts the
 * notification right away. The input fields, radio buttons, and save button are stacked vertically
 * in the middle of the screen.
 */
@Composable
fun NotifyScreen() {
    val context = LocalContext.current
    val defaultTitle = stringResource(R.string.default_title)
    val hint = stringResource(R.string.input)
    var title by rememberSaveable { mutableStateOf("") }
    var content by rememberSaveable { mutableStateOf("") }
    var priority by rememberSaveable { mutableStateOf(Priority.MEDIUM) }

    Scaffold(
        topBar = { ReminderTopBar(stringResource(R.string.app_name)) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(192.dp))
            LabeledRow(stringResource(R.string.label_title)) { modifier ->
                UnderlineField(title, { title = it }, hint, LabeledRowFontSize, modifier)
            }
            Spacer(Modifier.height(40.dp))
            LabeledRow(stringResource(R.string.label_content)) { modifier ->
                UnderlineField(content, { content = it }, hint, LabeledRowFontSize, modifier)
            }

            Spacer(Modifier.height(60.dp))
            PrioritySelector(selected = priority, onSelect = { priority = it }, fontSize = 20.sp, gap = 46.dp, showLow = true)

            Spacer(Modifier.height(86.dp))
            RectButton(
                text = stringResource(R.string.save),
                onClick = { Notifier.post(context, title.ifEmpty { defaultTitle }, content, priority) },
                modifier = Modifier.size(width = 109.dp, height = 59.dp),
                fontSize = 20.sp,
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}
