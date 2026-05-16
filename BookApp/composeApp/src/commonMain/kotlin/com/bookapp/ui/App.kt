package com.bookapp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import com.bookapp.shared.data.preferences.AppPreferences
import com.bookapp.ui.navigation.BookNavGraph
import com.bookapp.ui.theme.BookAppTheme
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun App() {
    val prefs: AppPreferences = koinInject()
    val isDarkMode by prefs.isDarkMode.collectAsState(initial = false)
    val coroutineScope = rememberCoroutineScope()

    BookAppTheme(darkTheme = isDarkMode) {
        BookNavGraph(
            isDarkMode = isDarkMode,
            onToggleDarkMode = {
                coroutineScope.launch { prefs.setDarkMode(!isDarkMode) }
            }
        )
    }
}
