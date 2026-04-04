package org.example.project

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.sourceforge.tess4j.Tesseract
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO
import java.io.IOException

actual class ScannerEngine actual constructor(private val tessDataPath: String) {
    actual suspend fun scanImage(imageBytes: ByteArray): String = withContext(Dispatchers.IO) {
        try {
            val tesseract = Tesseract().apply {
                setDatapath(tessDataPath)
                setLanguage("ron+eng")
                // Optional: setăm DPI-ul dacă imaginile sunt scanări de telefon (ajută la acuratețe)
                setVariable("user_defined_dpi", "300")
            }

            val inputStream = ByteArrayInputStream(imageBytes)
            val image = ImageIO.read(inputStream)
                ?: throw IOException("Format imagine neacceptat sau fișier corupt")

            // doOCR este o operație grea, de aceea folosim Dispatchers.IO
            tesseract.doOCR(image).trim()

        } catch (e: Exception) {
            e.printStackTrace()
            "Eroare la extragerea textului: ${e.localizedMessage}"
        }
    }
}