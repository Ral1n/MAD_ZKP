package org.example.project

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.example.project.screens.FilesScreen
import org.example.project.screens.HistoryScreen
import org.example.project.screens.HomeScreen
import org.example.project.screens.LoginScreen
import org.example.project.screens.ProfileScreen
import org.example.project.theme.AppTheme

@Composable
fun App(tessDataPath: String = "") {
    val navController   = rememberNavController()
    var isDarkTheme     by remember { mutableStateOf(true) }
    var hasIdentityCard by remember { mutableStateOf(false) }
    var appLanguage     by remember { mutableStateOf(AppLanguage.ENGLISH) }

    // Detect country on first launch and set language
    LaunchedEffect(Unit) {
        val country = detectCountryCode()
        if (country.equals("RO", ignoreCase = true)) {
            appLanguage = AppLanguage.ROMANIAN
        }
    }

    AppTheme(isDark = isDarkTheme, language = appLanguage) {
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
                    onNavigateToHistory    = { navController.navigate("history") },
                    onNavigateToFiles      = { navController.navigate("files") },
                    onNavigateToProfile    = { navController.navigate("profile") },
                    isDarkTheme            = isDarkTheme,
                    onToggleTheme          = { isDarkTheme = !isDarkTheme },
                    tessDataPath           = tessDataPath,
                    onIdentityCardDetected = { hasIdentityCard = true }
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
                    repository          = null,
                    hasIdentityCard     = hasIdentityCard
                )
            }
            composable("profile") {
                ProfileScreen(
                    onBack           = { navController.popBackStack() },
                    onLogout         = {
                        hasIdentityCard = false
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    currentLanguage  = appLanguage,
                    onLanguageChange = { appLanguage = it }
                )
            }
        }
    }
}