package org.example.project

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.example.project.screens.HistoryScreen

@Composable
@Preview
fun App() {
    MaterialTheme {
        HistoryScreen()
    }
}