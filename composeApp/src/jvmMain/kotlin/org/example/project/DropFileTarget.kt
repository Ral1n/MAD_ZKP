package org.example.project

import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragData
import androidx.compose.ui.draganddrop.dragData
import java.io.File
import java.net.URI

@OptIn(ExperimentalComposeUiApi::class)
actual fun Modifier.dropFileTarget(
    onDrop: (ByteArray) -> Unit,
    onHoverChange: (Boolean) -> Unit
): Modifier = this.dragAndDropTarget(
    shouldStartDragAndDrop = { event ->
        event.dragData() is DragData.FilesList
    },
    target = object : DragAndDropTarget {
        override fun onEntered(event: DragAndDropEvent) {
            onHoverChange(true)
        }
        override fun onExited(event: DragAndDropEvent) {
            onHoverChange(false)
        }
        override fun onEnded(event: DragAndDropEvent) {
            onHoverChange(false)
        }
        override fun onDrop(event: DragAndDropEvent): Boolean {
            onHoverChange(false)
            val filesList = event.dragData() as? DragData.FilesList ?: return false
            val uri = filesList.readFiles().firstOrNull() ?: return false
            return try {
                onDrop(File(URI(uri)).readBytes())
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
)