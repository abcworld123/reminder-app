package com.abcworld.reminder.ui.common

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** A rectangular button with slightly rounded corners. White text on purple, with a faint shadow. */
@Composable
fun RectButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, fontSize: TextUnit = 14.sp) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp, pressedElevation = 8.dp),
    ) {
        Text(text, fontSize = fontSize, fontWeight = FontWeight.Medium, maxLines = 1)
    }
}
