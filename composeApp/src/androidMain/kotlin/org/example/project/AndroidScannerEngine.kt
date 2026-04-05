package org.example.project

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import com.googlecode.tesseract.android.TessBaseAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual class ScannerEngine actual constructor(private val tessDataPath: String) {
    actual suspend fun scanImage(imageBytes: ByteArray): String = withContext(Dispatchers.IO) {
        val baseApi = TessBaseAPI()
        baseApi.init(tessDataPath, "ron+eng")
        baseApi.pageSegMode = TessBaseAPI.PageSegMode.PSM_AUTO_OSD

        val raw = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        val processed = preprocessBitmap(raw)
        baseApi.setImage(processed)

        val text = baseApi.utF8Text

        baseApi.stop()
        processed.recycle()
        text
    }

    private fun preprocessBitmap(src: Bitmap): Bitmap {
        // Scale up small images so Tesseract has enough pixel detail
        val minDimension = 1500
        val scaled = if (src.width < minDimension || src.height < minDimension) {
            val scale = minDimension.toFloat() / minOf(src.width, src.height)
            Bitmap.createScaledBitmap(src, (src.width * scale).toInt(), (src.height * scale).toInt(), true)
        } else {
            src
        }

        // Convert to grayscale with increased contrast
        val result = Bitmap.createBitmap(scaled.width, scaled.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        val paint = Paint()
        val colorMatrix = ColorMatrix().apply {
            setSaturation(0f) // grayscale
            // Boost contrast: scale=1.5, translate=-38 shifts mid-grays toward black/white
            val contrast = 1.5f
            val translate = (-0.5f * contrast + 0.5f) * 255f
            postConcat(ColorMatrix(floatArrayOf(
                contrast, 0f, 0f, 0f, translate,
                0f, contrast, 0f, 0f, translate,
                0f, 0f, contrast, 0f, translate,
                0f, 0f, 0f, 1f, 0f
            )))
        }
        paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
        canvas.drawBitmap(scaled, 0f, 0f, paint)
        return result
    }
}