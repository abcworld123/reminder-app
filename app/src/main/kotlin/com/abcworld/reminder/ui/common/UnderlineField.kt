package com.abcworld.reminder.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

/**
 * A single-line input field with only an underline. No border or background, and [hint] shows
 * faintly while it is empty. While editing, the underline thickens in the accent color.
 */
@Composable
fun UnderlineField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    fontSize: TextUnit,
    modifier: Modifier = Modifier,
) {
    var focused by remember { mutableStateOf(false) }
    val accent = MaterialTheme.colorScheme.tertiary
    val textColor = MaterialTheme.colorScheme.onSurface

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = TextStyle(color = textColor, fontSize = fontSize),
        cursorBrush = SolidColor(accent),
        modifier = modifier
            .onFocusChanged { focused = it.isFocused }
            .underline(if (focused) accent else textColor.copy(alpha = 0.42f), if (focused) 2.dp else 1.dp),
        decorationBox = { innerTextField ->
            Box(modifier = Modifier.fieldPadding()) {
                if (value.isEmpty()) {
                    Text(hint, color = textColor.copy(alpha = 0.5f), fontSize = fontSize, maxLines = 1)
                }
                innerTextField()
            }
        },
    )
}

/**
 * Looks like [UnderlineField] but isn't typed into. Tapping it calls [onClick], which opens
 * something like a dialog.
 */
@Composable
fun UnderlineValue(value: String, onClick: () -> Unit, fontSize: TextUnit, modifier: Modifier = Modifier) {
    val textColor = MaterialTheme.colorScheme.onSurface
    Box(
        modifier = modifier
            .clickable(onClick = onClick)
            .underline(textColor.copy(alpha = 0.42f), 1.dp)
            .fieldPadding(),
    ) {
        Text(value, color = textColor, fontSize = fontSize, maxLines = 1)
    }
}

/** The underline is drawn slightly inset from both ends and the bottom of the field. */
private fun Modifier.underline(color: Color, thickness: Dp): Modifier = drawBehind {
    val inset = 4.dp.toPx()
    val y = size.height - 7.dp.toPx()
    drawLine(color, Offset(inset, y), Offset(size.width - inset, y), thickness.toPx())
}

private fun Modifier.fieldPadding(): Modifier = padding(start = 7.dp, end = 4.dp, top = 10.dp, bottom = 13.dp)
