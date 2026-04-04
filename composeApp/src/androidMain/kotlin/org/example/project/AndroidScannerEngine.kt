package org.example.project

import com.googlecode.tesseract.android.TessBaseAPI
import android.graphics.BitmapFactory

actual class ScannerEngine actual constructor(private val tessDataPath: String) {
    actual suspend fun scanImage(imageBytes: ByteArray): String {
        val baseApi = TessBaseAPI()
        // Inițializare cu folderul pregătit anterior
        baseApi.init(tessDataPath, "ron+eng")

        // Convertim ByteArray în Bitmap (în RAM)
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        baseApi.setImage(bitmap)

        val text = baseApi.utF8Text

        baseApi.stop() // Eliberăm memoria
        return text
    }
}
