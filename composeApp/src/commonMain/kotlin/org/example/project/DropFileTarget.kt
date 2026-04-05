package org.example.project

import androidx.compose.ui.Modifier

expect fun Modifier.dropFileTarget(
    onDrop: (ByteArray) -> Unit,
    onHoverChange: (Boolean) -> Unit
): Modifier