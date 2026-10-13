package com.abcworld.reminder.ui.common

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * The top bar. Like the old action bar, it is purple in light mode and a dark surface in dark mode,
 * and the title is always white. The same color fills in behind the status bar. The height is 56dp,
 * also matching the old action bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderTopBar(title: String) {
    TopAppBar(
        title = { Text(title, fontSize = 20.sp, fontWeight = FontWeight.Medium) },
        expandedHeight = 56.dp,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.surfaceContainer else MaterialTheme.colorScheme.primary,
            titleContentColor = Color.White,
        ),
    )
}
