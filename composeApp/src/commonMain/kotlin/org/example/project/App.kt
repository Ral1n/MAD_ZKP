package org.example.project

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.example.project.screens.FilesScreen
import org.example.project.screens.HistoryScreen
import org.example.project.screens.HomeScreen
import org.example.project.screens.LoginScreen
import org.example.project.theme.AppTheme

@Composable
fun App() {
    val navController = rememberNavController()
    var isDarkTheme by remember { mutableStateOf(true) }

    AppTheme(isDark = isDarkTheme) {
        NavHost(
            navController = navController,
            startDestination = "login"
        ) {
            composable("login") {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }
            composable("home") {
                HomeScreen(
                    onNavigateToHistory = { navController.navigate("history") },
                    onNavigateToFiles   = { navController.navigate("files") },
                    isDarkTheme         = isDarkTheme,
                    onToggleTheme       = { isDarkTheme = !isDarkTheme }
                )
            }
            composable("history") {
                HistoryScreen(
                    onNavigateToScan    = { navController.navigate("home") },
                    isDarkTheme         = isDarkTheme,
                    onToggleTheme       = { isDarkTheme = !isDarkTheme }
                )
            }
            composable("files") {
                FilesScreen(
                    onNavigateToScan    = { navController.navigate("home") },
                    onNavigateToHistory = { navController.navigate("history") },
                    isDarkTheme         = isDarkTheme,
                    onToggleTheme       = { isDarkTheme = !isDarkTheme },
                    repository          = null
                )
            }
        }
    }
}