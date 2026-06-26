package chat.nami.accessibility.demos

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp

/**
 * contentDescription & Role.
 *
 * Icon-only buttons carry no text, so without a contentDescription TalkBack can
 * only fall back to the icon resource name ("Filled.Send") or nothing at all.
 * Decorative imagery should be the opposite: explicitly silenced with null.
 */
@Composable
fun ContentDescriptionBad() {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        // No label: TalkBack announces nothing meaningful.
        IconButton(onClick = {}) {
            Icon(Icons.Filled.Send, contentDescription = null)
        }
        IconButton(onClick = {}) {
            Icon(Icons.Filled.Favorite, contentDescription = null)
        }
        // Decorative flourish that TalkBack will read out as noise.
        Image(
            painter = rememberVectorPainter(Icons.Filled.Star),
            contentDescription = "star icon",
            modifier = Modifier.size(48.dp)
        )
    }
}

@Composable
fun ContentDescriptionGood() {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        IconButton(onClick = {}) {
            Icon(Icons.Filled.Send, contentDescription = "Send message")
        }
        IconButton(onClick = {}) {
            Icon(Icons.Filled.Favorite, contentDescription = "Add to favorites")
        }
        // Purely decorative: null removes it from the semantics tree entirely.
        Image(
            painter = rememberVectorPainter(Icons.Filled.Star),
            contentDescription = null,
            modifier = Modifier.size(48.dp)
        )
    }
}
