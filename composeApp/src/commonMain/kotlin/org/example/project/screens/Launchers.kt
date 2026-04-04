package org.example.project.screens

import androidx.compose.runtime.Composable

@Composable
expect fun rememberCameraLauncher(onPhoto: (ByteArray?) -> Unit): () -> Unit

@Composable
expect fun rememberGalleryLauncher(onFile: (ByteArray?) -> Unit): () -> Unit

@Composable
expect fun rememberFileLauncher(onFile: (ByteArray?) -> Unit): () -> Unit