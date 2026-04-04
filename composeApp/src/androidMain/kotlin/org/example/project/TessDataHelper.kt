package org.example.project

import android.content.Context
import java.io.File
import java.io.FileOutputStream

object TessDataHelper {
    /**
     * Copiază fișierele .traineddata din assets/tessdata în folderul intern al aplicației.
     * Returnează calea către folderul părinte (cel care conține folderul 'tessdata').
     */
    fun initTessData(context: Context): String {
        // Tesseract are nevoie de calea către folderul PĂRINTE al lui 'tessdata'
        val dataPath = context.filesDir.absolutePath
        val tessDir = File(dataPath, "tessdata")

        if (!tessDir.exists()) {
            tessDir.mkdirs()
        }

        // Listăm fișierele din assets/tessdata
        try {
            val assetManager = context.assets
            val files = assetManager.list("tessdata") ?: return dataPath

            for (filename in files) {
                val outFile = File(tessDir, filename)

                // Copiem doar dacă fișierul nu există deja (pentru a nu consuma resurse la fiecare pornire)
                if (!outFile.exists()) {
                    assetManager.open("tessdata/$filename").use { inputStream ->
                        FileOutputStream(outFile).use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return dataPath
    }
}