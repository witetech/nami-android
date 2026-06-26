package chat.nami.accessibility.demos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp

/**
 * State & toggles.
 *
 * A custom pill toggle that only changes color communicates nothing to TalkBack:
 * no role, no on/off state, and a plain clickable announces "double tap to
 * activate" with no hint of what it does. toggleable adds the Switch role and a
 * stateDescription makes the current value spoken.
 */
@Composable
fun StateToggleBad() {
    var enabled by remember { mutableStateOf(false) }
    Pill(
        enabled = enabled,
        modifier = Modifier.clickable { enabled = !enabled }
    )
}

@Composable
fun StateToggleGood() {
    var enabled by remember { mutableStateOf(false) }
    Pill(
        enabled = enabled,
        modifier = Modifier
            .toggleable(
                value = enabled,
                role = Role.Switch,
                onValueChange = { enabled = it }
            )
            .semantics {
                stateDescription = if (enabled) "On" else "Off"
            }
    )
}

@Composable
private fun Pill(enabled: Boolean, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val bg = if (enabled) colors.primary else colors.surfaceVariant
    val fg = if (enabled) colors.onPrimary else colors.onSurfaceVariant
    Box(
        modifier = modifier
            .background(bg, RoundedCornerShape(50))
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Text("Notifications", color = fg, style = MaterialTheme.typography.titleSmall)
    }
}
