package com.abcworld.reminder.ui.common

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

/**
 * Date picker dialog. Tapping a date selects it and closes the dialog at once. There are no
 * confirm/cancel buttons; tapping outside just closes it. Opens with [initial] selected.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickDialog(initial: LocalDate, onDismiss: () -> Unit, onPick: (LocalDate) -> Unit) {
    val base = rememberDatePickerState(initialSelectedDateMillis = initial.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli())
    val pick by rememberUpdatedState(onPick)
    val state = remember(base) {
        PickOnTapState(base) { millis -> pick(Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()) }
    }
    DatePickerDialog(onDismissRequest = onDismiss, confirmButton = {}) {
        // No text input mode: a fully typed date also counts as a tap, so the dialog would close in
        // the middle of an edit.
        DatePicker(state = state, showModeToggle = false)
    }
}

/**
 * [DatePicker] writes the tapped date straight into [DatePickerState.selectedDateMillis]. This also
 * passes that value to [onPick], even when the already-selected date is tapped again (where the
 * value doesn't change, so there would be no change to observe).
 */
@OptIn(ExperimentalMaterial3Api::class)
private class PickOnTapState(
    private val base: DatePickerState,
    private val onPick: (Long) -> Unit,
) : DatePickerState by base {
    override var selectedDateMillis: Long?
        get() = base.selectedDateMillis
        set(value) {
            base.selectedDateMillis = value
            value?.let(onPick)
        }
}
