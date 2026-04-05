package org.example.project

import java.io.File

object TessDataHelperDesktop {

    /**
     * Extracts tessdata files bundled as classpath resources (jvmMain/resources/tessdata/)
     * into ~/.mad_zkp/tessdata/ on first run, then returns the parent directory path
     * (the path Tess4J needs — the folder that *contains* the tessdata/ subfolder).
     */
    fun initTessData(): String {
        val appDir  = File(System.getProperty("user.home"), ".mad_zkp")
        val tessDir = File(appDir, "tessdata")
        tessDir.mkdirs()

        for (lang in listOf("eng", "ron")) {
            val filename = "$lang.traineddata"
            val dest     = File(tessDir, filename)
            if (dest.exists()) continue

            val stream = TessDataHelperDesktop::class.java
                .classLoader
                ?.getResourceAsStream("tessdata/$filename")

            if (stream != null) {
                stream.use { input ->
                    dest.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            } else {
                System.err.println("TessDataHelperDesktop: resource 'tessdata/$filename' not found in classpath")
            }
        }

        return tessDir.absolutePath
    }
}