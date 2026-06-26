package chat.nami.accessibility.demos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

/**
 * Merging & clearing.
 *
 * A contact row is one logical unit, but its avatar / name / subtitle / unread
 * count are four separate leaf nodes. Left alone TalkBack stops on each. Merging
 * reads it as a single focusable item; clearAndSetSemantics goes further and
 * replaces the children's noisy text with one clean label.
 */
@Composable
fun MergingBad() {
    ContactRow()
}

@Composable
fun MergingGood() {
    // mergeDescendants collapses the children into one TalkBack stop, read in order.
    ContactRow(Modifier.semantics(mergeDescendants = true) {})
}

/** Alternative shown in the talk: hide the raw children and expose one label. */
@Composable
fun MergingCleared() {
    ContactRow(
        Modifier.clearAndSetSemantics {
            contentDescription = "Ada Lovelace, last seen 2 minutes ago, 3 unread messages"
        }
    )
}

@Composable
private fun ContactRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(shape = CircleShape, tonalElevation = 4.dp) {
            Icon(
                Icons.Filled.Person,
                contentDescription = "Avatar",
                modifier = Modifier.size(40.dp).padding(4.dp)
            )
        }
        Column(Modifier.weight(1f)) {
            Text("Ada Lovelace", style = MaterialTheme.typography.titleMedium)
            Text("Last seen 2 minutes ago", style = MaterialTheme.typography.bodySmall)
        }
        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primary) {
            Text(
                "3",
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}
