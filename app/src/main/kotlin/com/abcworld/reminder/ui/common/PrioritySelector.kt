package com.abcworld.reminder.ui.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.abcworld.reminder.Priority
import com.abcworld.reminder.R

/**
 * A horizontal group of radio buttons for picking a priority. [gap] is the spacing between buttons.
 * With [showLow], '낮음', which can't be picked yet, is shown too, disabled.
 */
@Composable
fun PrioritySelector(
    selected: Priority,
    onSelect: (Priority) -> Unit,
    fontSize: TextUnit,
    gap: Dp,
    modifier: Modifier = Modifier,
    showLow: Boolean = false,
) {
    Row(modifier = modifier.selectableGroup(), verticalAlignment = Alignment.CenterVertically) {
        PriorityRadio(stringResource(R.string.priority_high), selected == Priority.HIGH, fontSize) { onSelect(Priority.HIGH) }
        Spacer(Modifier.width(gap))
        PriorityRadio(stringResource(R.string.priority_medium), selected == Priority.MEDIUM, fontSize) { onSelect(Priority.MEDIUM) }
        if (showLow) {
            Spacer(Modifier.width(gap))
            PriorityRadio(stringResource(R.string.priority_low), selected = false, fontSize = fontSize, onClick = null)
        }
    }
}

/** One radio button and its label. Disabled when [onClick] is null. Tapping the label selects it too. */
@Composable
private fun PriorityRadio(label: String, selected: Boolean, fontSize: TextUnit, onClick: (() -> Unit)?) {
    val enabled = onClick != null
    val textColor = MaterialTheme.colorScheme.onSurface
    Row(
        modifier = Modifier
            .selectable(selected = selected, enabled = enabled, role = Role.RadioButton, onClick = onClick ?: {})
            .padding(start = 6.dp, top = 12.dp, end = 4.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            onClick = null,
            enabled = enabled,
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.tertiary,
                unselectedColor = textColor.copy(alpha = 0.6f),
            ),
        )
        Spacer(Modifier.width(7.dp))
        Text(label, fontSize = fontSize, color = if (enabled) textColor else textColor.copy(alpha = 0.38f))
    }
}
