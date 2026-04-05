package org.example.project

import androidx.compose.ui.Modifier

actual fun Modifier.dropFileTarget(
    onDrop: (ByteArray) -> Unit,
    onHoverChange: (Boolean) -> Unit
): Modifier = this