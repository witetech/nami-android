package chat.nami.accessibility.demos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay

/**
 * Announcements via liveRegion.
 *
 * Tapping "Reload" updates a status line. Without a liveRegion TalkBack stays
 * silent — a sighted user sees "Loading… / Updated", a blind user gets nothing
 * unless they happen to be focused on that text. Marking it Polite makes
 * TalkBack announce the change automatically when it settles.
 */
@Composable
fun AnnouncementBad() {
    StatusDemo(statusModifier = Modifier)
}

@Composable
fun AnnouncementGood() {
    // Polite waits for a pause; Assertive interrupts. Errors usually warrant Assertive.
    StatusDemo(statusModifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite })
}

@Composable
private fun StatusDemo(statusModifier: Modifier) {
    var status by remember { mutableStateOf("Idle") }
    var loadId by remember { mutableStateOf(0) }

    LaunchedEffect(loadId) {
        if (loadId == 0) return@LaunchedEffect
        status = "Loading…"
        delay(1_200.milliseconds)
        status = "Updated just now"
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Button(onClick = { loadId++ }) { Text("Reload") }
        Text(
            text = status,
            modifier = statusModifier,
            style = MaterialTheme.typography.titleMedium
        )
    }
}
