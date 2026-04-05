package org.example.project

/** Returns a 2-letter ISO country code (e.g. "RO", "US") or empty string on failure. */
expect suspend fun detectCountryCode(): String