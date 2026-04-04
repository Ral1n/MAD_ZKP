package org.example.project

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.EaseInOutQuart
import androidx.compose.runtime.*
import org.example.project.screens.HistoryScreen
import org.example.project.screens.HomeScreen

// ─── Navigare simplă fără librărie externă ────────────────────────────────────
enum class Screen { HOME, HISTORY, FILES }

@Composable
fun App() {
    var currentScreen by remember { mutableStateOf(Screen.HOME) }

    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = {
            when {
                // HOME → HISTORY: slide din dreapta
                initialState == Screen.HOME && targetState == Screen.HISTORY ->
                    slideInHorizontally(tween(320, easing = EaseInOutQuart)) { it } + fadeIn(tween(320)) togetherWith
                            slideOutHorizontally(tween(320, easing = EaseInOutQuart)) { -it / 3 } + fadeOut(tween(200))

                // HISTORY → HOME: slide înapoi
                initialState == Screen.HISTORY && targetState == Screen.HOME ->
                    slideInHorizontally(tween(320, easing = EaseInOutQuart)) { -it } + fadeIn(tween(320)) togetherWith
                            slideOutHorizontally(tween(320, easing = EaseInOutQuart)) { it / 3 } + fadeOut(tween(200))

                // Default
                else ->
                    fadeIn(tween(300)) togetherWith fadeOut(tween(200))
            }
        },
        label = "nav"
    ) { screen ->
        when (screen) {
            Screen.HOME -> HomeScreen(
                onNavigateToHistory = { currentScreen = Screen.HISTORY },
                onNavigateToFiles   = { currentScreen = Screen.FILES }
            )
            Screen.HISTORY -> HistoryScreen(
                onSettingsClick = {},
                onNavigateToScan = { currentScreen = Screen.HOME }
            )
            Screen.FILES -> HomeScreen(  // placeholder finché non crei FilesScreen
                onNavigateToHistory = { currentScreen = Screen.HISTORY },
                onNavigateToFiles   = { currentScreen = Screen.FILES }
            )
        }
    }
}