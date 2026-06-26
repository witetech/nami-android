package chat.nami.accessibility.demos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Touch targets.
 *
 * The visible icon can stay small (24dp) for design reasons, but the *touch
 * target* must be at least 48x48dp. Shrinking the clickable to the icon makes it
 * hard to hit for anyone with a motor impairment or large fingers.
 * minimumInteractiveComponentSize expands the touch area without resizing the icon.
 */
@Composable
fun TouchTargetBad() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            Icons.Filled.Close,
            contentDescription = "Dismiss",
            // Tiny 24dp clickable: the whole hit area is the glyph itself.
            modifier = Modifier
                .size(24.dp)
                .clickable {}
        )
        Text("24dp hit area")
    }
}

@Composable
fun TouchTargetGood() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Column {
            Icon(
                Icons.Filled.Close,
                contentDescription = "Dismiss",
                // Icon stays 24dp; the modifier guarantees a ≥48dp interactive area.
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .size(24.dp)
                    .clickable {}
            )
        }
        Text("≥48dp hit area")
    }
}
