package com.abcworld.reminder.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.abcworld.reminder.R
import com.abcworld.reminder.ui.history.HistoryScreen
import com.abcworld.reminder.ui.notify.NotifyScreen
import com.abcworld.reminder.ui.schedule.ScheduleScreen
import kotlinx.coroutines.launch

/** Bottom bar tabs. Their order is the left-to-right layout. */
private enum class MainTab(@param:StringRes val label: Int, @param:DrawableRes val icon: Int) {
    NOTIFY(R.string.nav_notify, R.drawable.ic_nav_notify),
    SCHEDULE(R.string.nav_schedule, R.drawable.ic_nav_schedule),
    HISTORY(R.string.nav_history, R.drawable.ic_nav_history),
}

/**
 * Switches between the three screens (notify / scheduled / history) with the bottom bar. All screens
 * are kept alive, so whatever was being typed survives switching tabs.
 */
@Composable
fun ReminderApp() {
    val tabs = MainTab.entries
    val pagerState = rememberPagerState { tabs.size }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            // The bottom bar already covers the bottom system bar inset, so the screens don't pad for it again.
            modifier = Modifier.weight(1f).consumeWindowInsets(WindowInsets.navigationBars),
            userScrollEnabled = false,
            beyondViewportPageCount = tabs.size - 1,
        ) { page ->
            when (tabs[page]) {
                MainTab.NOTIFY -> NotifyScreen()
                MainTab.SCHEDULE -> ScheduleScreen(showing = pagerState.currentPage == page)
                MainTab.HISTORY -> HistoryScreen(showing = pagerState.currentPage == page)
            }
        }
        NavigationBar {
            tabs.forEachIndexed { index, tab ->
                NavigationBarItem(
                    selected = pagerState.targetPage == index,
                    onClick = { scope.launch { pagerState.scrollToPage(index) } },
                    icon = { Icon(painterResource(tab.icon), contentDescription = null) },
                    label = { Text(stringResource(tab.label)) },
                )
            }
        }
    }
}
