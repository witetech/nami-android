package chat.nami.accessibility.demos

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 * Custom actions for gestures.
 *
 * Swipe-to-delete is a drag gesture handled in pointerInput. A sighted user
 * swipes the row away, but TalkBack intercepts swipes for navigation, so a
 * screen-reader user can *never* trigger the delete — the action is invisible to
 * the semantics tree. customActions surfaces the same operation in TalkBack's
 * actions menu (the "rotor"), so it works without the gesture.
 */
@Composable
fun CustomActionBad() {
    var deleted by remember { mutableStateOf(false) }
    if (deleted) {
        DeletedHint { deleted = false }
        return
    }
    // Swipe works visually, but TalkBack has no way to reach "delete".
    SwipeableRow(onDelete = { deleted = true })
}

@Composable
fun CustomActionGood() {
    var deleted by remember { mutableStateOf(false) }
    if (deleted) {
        DeletedHint { deleted = false }
        return
    }
    SwipeableRow(
        onDelete = { deleted = true },
        // Expose the gesture as a named action TalkBack lists in its menu.
        semanticsModifier = Modifier.semantics {
            customActions = listOf(
                CustomAccessibilityAction("Delete") {
                    deleted = true
                    true
                }
            )
        }
    )
}

@Composable
private fun SwipeableRow(onDelete: () -> Unit, semanticsModifier: Modifier = Modifier) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    val threshold = -250f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.errorContainer, MaterialTheme.shapes.medium)
    ) {
        Text(
            "Delete",
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 24.dp),
            color = MaterialTheme.colorScheme.onErrorContainer
        )
        Surface(
            modifier = semanticsModifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = { if (offsetX < threshold) onDelete() else offsetX = 0f },
                        onHorizontalDrag = { _, drag ->
                            offsetX = (offsetX + drag).coerceAtMost(0f)
                        }
                    )
                },
            tonalElevation = 3.dp,
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                "Swipe me left to delete",
                modifier = Modifier.padding(20.dp),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
private fun DeletedHint(onReset: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) { detectHorizontalDragGestures { _, _ -> onReset() } },
        tonalElevation = 1.dp,
        shape = MaterialTheme.shapes.medium
    ) {
        Text(
            "Deleted — swipe here to reset",
            modifier = Modifier.padding(20.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
