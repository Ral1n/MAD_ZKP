package org.example.project

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.EaseInOutQuart
import androidx.compose.runtime.*
import org.example.project.screens.HistoryScreen
import org.example.project.screens.HomeScreen
import org.example.project.screens.FilesScreen
import org.example.project.theme.AppTheme

enum class Screen { HOME, HISTORY, FILES }

@Composable
fun App() {
    var currentScreen by remember { mutableStateOf(Screen.HOME) }
    var isDarkTheme by remember { mutableStateOf(true) }

    AppTheme(isDark = isDarkTheme) {
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = {
                when {
                    initialState == Screen.HOME && targetState == Screen.HISTORY ->
                        slideInHorizontally(tween(320, easing = EaseInOutQuart)) { it } +
                                fadeIn(tween(320)) togetherWith
                                slideOutHorizontally(tween(320, easing = EaseInOutQuart)) { -it / 3 } +
                                fadeOut(tween(200))

                    initialState == Screen.HISTORY && targetState == Screen.HOME ->
                        slideInHorizontally(tween(320, easing = EaseInOutQuart)) { -it } +
                                fadeIn(tween(320)) togetherWith
                                slideOutHorizontally(tween(320, easing = EaseInOutQuart)) { it / 3 } +
                                fadeOut(tween(200))

                    targetState == Screen.FILES ->
                        slideInHorizontally(tween(320, easing = EaseInOutQuart)) { it } +
                                fadeIn(tween(320)) togetherWith
                                slideOutHorizontally(tween(320, easing = EaseInOutQuart)) { -it / 3 } +
                                fadeOut(tween(200))

                    initialState == Screen.FILES ->
                        slideInHorizontally(tween(320, easing = EaseInOutQuart)) { -it } +
                                fadeIn(tween(320)) togetherWith
                                slideOutHorizontally(tween(320, easing = EaseInOutQuart)) { it / 3 } +
                                fadeOut(tween(200))

                    else ->
                        fadeIn(tween(300)) togetherWith fadeOut(tween(200))
                }
            },
            label = "nav"
        ) { screen ->
            when (screen) {
                Screen.HOME -> HomeScreen(
                    onNavigateToHistory = { currentScreen = Screen.HISTORY },
                    onNavigateToFiles   = { currentScreen = Screen.FILES },
                    isDarkTheme         = isDarkTheme,
                    onToggleTheme       = { isDarkTheme = !isDarkTheme }
                )
                Screen.HISTORY -> HistoryScreen(
                    onNavigateToScan    = { currentScreen = Screen.HOME },
                    isDarkTheme         = isDarkTheme,
                    onToggleTheme       = { isDarkTheme = !isDarkTheme }
                )
                Screen.FILES -> FilesScreen(
                    onNavigateToScan    = { currentScreen = Screen.HOME },
                    onNavigateToHistory = { currentScreen = Screen.HISTORY },
                    isDarkTheme         = isDarkTheme,
                    onToggleTheme       = { isDarkTheme = !isDarkTheme },
                    repository          = null  // ← înlocuiește cu implementarea reală când ai DB-ul
                )
            }
        }
    }
}