package org.example.project.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

@Composable
actual fun rememberCameraLauncher(onPhoto: (ByteArray?) -> Unit): () -> Unit {
    return remember { { onPhoto(null) } }
}

@Composable
actual fun rememberGalleryLauncher(onFile: (ByteArray?) -> Unit): () -> Unit {
    return remember {
        {
            val chooser = JFileChooser().apply {
                dialogTitle = "Alege o imagine"
                fileFilter = FileNameExtensionFilter("Imagini", "jpg", "jpeg", "png")
            }
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
                onFile(chooser.selectedFile.readBytes())
            else onFile(null)
        }
    }
}

@Composable
actual fun rememberFileLauncher(onFile: (ByteArray?) -> Unit): () -> Unit {
    return remember {
        {
            val chooser = JFileChooser().apply {
                dialogTitle = "Alege un document"
                fileFilter = FileNameExtensionFilter("Documente", "pdf", "jpg", "jpeg", "png")
            }
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
                onFile(chooser.selectedFile.readBytes())
            else onFile(null)
        }
    }
}