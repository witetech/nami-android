package chat.nami.accessibility.demos

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Font scaling.
 *
 * Set system font size to its largest (Settings ▸ Display ▸ Font size, or 200%
 * on Android 14+) and watch the difference. The fixed-height button clips its
 * label; the fluid one grows with the text. Text is always sized in sp so it
 * responds to the user's preference — only the *layout* uses dp.
 */
@Composable
fun FontScalingBad() {
    // Fixed 40dp height can't grow, so scaled text is cut off.
    Surface(
        modifier = Modifier.fillMaxWidth().height(40.dp),
        color = MaterialTheme.colorScheme.primary,
        shape = MaterialTheme.shapes.small
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                "Confirm purchase",
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun FontScalingGood() {
    // No fixed height: the surface wraps however tall the scaled text needs.
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primary,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            "Confirm purchase",
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.titleMedium
        )
    }
}
