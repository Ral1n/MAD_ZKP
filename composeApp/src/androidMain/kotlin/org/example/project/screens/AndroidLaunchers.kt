package org.example.project.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File

@Composable
actual fun rememberCameraLauncher(onPhoto: (ByteArray?) -> Unit): () -> Unit {
    val context = LocalContext.current
    val photoFile = remember { File(context.cacheDir, "photo_${System.currentTimeMillis()}.jpg") }
    val photoUri = remember {
        FileProvider.getUriForFile(context, "${context.packageName}.provider", photoFile)
    }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) onPhoto(photoFile.readBytes()) else onPhoto(null)
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) cameraLauncher.launch(photoUri) else onPhoto(null)
    }
    return remember {
        {
            val ok = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
            if (ok) cameraLauncher.launch(photoUri) else permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
}

@Composable
actual fun rememberGalleryLauncher(onFile: (ByteArray?) -> Unit): () -> Unit {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        onFile(uri?.let { context.contentResolver.openInputStream(it)?.readBytes() })
    }
    return remember { { launcher.launch("image/*") } }
}

@Composable
actual fun rememberFileLauncher(onFile: (ByteArray?) -> Unit): () -> Unit {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        onFile(uri?.let { context.contentResolver.openInputStream(it)?.readBytes() })
    }
    return remember { { launcher.launch("*/*") } }
}