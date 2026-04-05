package org.example.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Inițializăm datele pentru Tesseract
        val tessDataPath = TessDataHelper.initTessData(this)

        setContent {
            App(tessDataPath = tessDataPath)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}