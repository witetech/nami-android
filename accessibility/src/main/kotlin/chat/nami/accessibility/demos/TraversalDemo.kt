package chat.nami.accessibility.demos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp

/**
 * Traversal order.
 *
 * The "important announcement" banner is composed *after* the article so it
 * paints on top, but TalkBack reads in composition order, so a screen-reader
 * user hears the whole article before discovering the alert. traversalIndex
 * pulls the banner to the front of the reading order without moving it visually.
 */
@Composable
fun TraversalBad() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Article()
        Banner()
    }
}

@Composable
fun TraversalGood() {
    // isTraversalGroup scopes the indices; a negative index sorts the banner first.
    // The index must sit on the *focusable* node, so merge the banner into one node
    // that carries it — otherwise the inner Text keeps the default 0f and wins on
    // geometric order.
    Column(
        modifier = Modifier.semantics { isTraversalGroup = true },
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Article()
        Banner(Modifier.semantics(mergeDescendants = true) { traversalIndex = -1f })
    }
}

@Composable
private fun Article() {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("The history of the loom", style = MaterialTheme.typography.titleMedium)
        Text(
            "A long article body that a TalkBack user would have to swipe all the " +
                "way through before reaching anything else on the screen…",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun Banner(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.errorContainer,
        shape = MaterialTheme.shapes.medium
    ) {
        Text(
            "Important: your session expires in 1 minute",
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.onErrorContainer,
            style = MaterialTheme.typography.titleSmall
        )
    }
}
