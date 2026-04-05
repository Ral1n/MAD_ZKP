package org.example.project

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale

actual suspend fun detectCountryCode(): String = withContext(Dispatchers.IO) {
    try {
        val conn = URL("https://ipinfo.io/country").openConnection() as HttpURLConnection
        conn.connectTimeout = 3000
        conn.readTimeout    = 3000
        val code = conn.inputStream.bufferedReader().readText().trim()
        conn.disconnect()
        code
    } catch (e: Exception) {
        Locale.getDefault().country
    }
}