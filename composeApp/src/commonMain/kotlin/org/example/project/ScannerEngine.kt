package org.example.project

expect class ScannerEngine(tessDataPath: String) {
    suspend fun scanImage(imageBytes: ByteArray): String
}