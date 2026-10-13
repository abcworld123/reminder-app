package com.abcworld.reminder.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.abcworld.reminder.R

/**
 * Keypad dialog for entering a time. Digits fill in over 00:00 from the left, and four digits make
 * a time: 1·9 is 19:00, 1·1·2·4 is 11:24. A fifth digit doesn't push the earlier ones out; it
 * starts over from the beginning. C resets to 00:00.
 *
 * The hour and minute boxes at the top only display the input. They are not tappable.
 */
@Composable
fun TimeKeypadDialog(
    initialHour: Int,
    initialMinute: Int,
    onDismiss: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit,
) {
    // The digits tapped so far, at most four. Empty until the first digit: the dialog opens showing
    // the current time and the first digit clears it. C sets it to "0000", which counts as 00:00
    // having been entered.
    var typed by remember { mutableStateOf("") }
    val digits = if (typed.isEmpty()) "%02d%02d".format(initialHour, initialMinute) else typed.padEnd(4, '0')
    val hour = digits.take(2).toInt()
    val minute = digits.takeLast(2).toInt()
    val valid = hour <= 23 && minute <= 59
    // Digits actually tapped show dark; the zeros filling the rest show faint.
    val strong = if (typed.isEmpty()) 4 else typed.length

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            modifier = Modifier.fillMaxWidth(0.72f).widthIn(max = 320.dp),
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(stringResource(R.string.pick_time), style = MaterialTheme.typography.titleMedium)

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                ) {
                    TimeBox(digits.take(2), strong.coerceAtMost(2), error = hour > 23)
                    Text(
                        ":",
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp),
                    )
                    TimeBox(digits.takeLast(2), (strong - 2).coerceAtLeast(0), error = minute > 59)
                }
                // Takes up space even when the input is valid, so the keys don't shift when the error appears.
                Text(
                    if (valid) " " else stringResource(R.string.time_out_of_range),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (valid) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                )

                val type: (String) -> Unit = { d -> typed = if (typed.length >= 4) d else typed + d }
                listOf(listOf("1", "2", "3"), listOf("4", "5", "6"), listOf("7", "8", "9")).forEach { row ->
                    KeypadRow { row.forEach { d -> KeypadKey(d, Modifier.weight(1f), height = KEY_HEIGHT) { type(d) } } }
                }
                KeypadRow {
                    KeypadKey(
                        stringResource(R.string.key_clear),
                        Modifier.weight(1f),
                        MaterialTheme.colorScheme.surfaceContainerHighest,
                        height = KEY_HEIGHT,
                    ) { typed = "0000" }
                    KeypadKey("0", Modifier.weight(1f), height = KEY_HEIGHT) { type("0") }
                    KeypadKey(
                        stringResource(R.string.confirm),
                        Modifier.weight(1f),
                        MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        enabled = valid,
                        height = KEY_HEIGHT,
                    ) { onConfirm(hour, minute) }
                }
            }
        }
    }
}

private val KEY_HEIGHT = 72.dp

/** A rounded box showing the hour or the minute. */
@Composable
private fun TimeBox(text: String, strongCount: Int, error: Boolean) {
    val faint = MaterialTheme.colorScheme.onSurfaceVariant
    val strong = if (error) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
        modifier = Modifier.width(88.dp).height(68.dp),
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = buildAnnotatedString {
                    text.forEachIndexed { i, c ->
                        withStyle(SpanStyle(color = if (i < strongCount) strong else faint)) { append(c) }
                    }
                },
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
