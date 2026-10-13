package com.abcworld.reminder.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Text size for the field next to the label. Same as the label's. */
val LabeledRowFontSize = 20.sp

/**
 * One "제목: ____" row. The field has a fixed width narrower than the screen and sits slightly right
 * of center, with the label attached to its left. [field] must apply the Modifier (width) it is given.
 */
@Composable
fun LabeledRow(label: String, field: @Composable (Modifier) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth()) {
        // Splits the leftover width 6 (left) : 4 (right), putting the field slightly right of center.
        Box(modifier = Modifier.weight(0.6f), contentAlignment = Alignment.TopEnd) {
            Text(
                label,
                fontSize = LabeledRowFontSize,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 10.dp, end = 24.dp),
            )
        }
        field(Modifier.width(228.dp))
        Spacer(Modifier.weight(0.4f))
    }
}
