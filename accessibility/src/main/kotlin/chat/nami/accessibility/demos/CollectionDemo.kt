package chat.nami.accessibility.demos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.CollectionItemInfo
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.collectionItemInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

/**
 * Lists & collections.
 *
 * LazyColumn/LazyRow emit collection semantics for free, but a plain Column used
 * as a list does not: TalkBack reads each row with no sense of position.
 * collectionInfo on the container plus collectionItemInfo on each row restore
 * "2 of 5", so a screen-reader user knows where they are in the list.
 */
private val folders = listOf(
    "Inbox" to 12,
    "Sent" to 3,
    "Drafts" to 1,
    "Spam" to 48,
    "Trash" to 0
)

@Composable
fun CollectionBad() {
    // A Column masquerading as a list — each row is read with no position.
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        folders.forEach { (name, count) ->
            FolderRow(name, count, Modifier.semantics(mergeDescendants = true) {})
        }
    }
}

@Composable
fun CollectionGood() {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        // Declare the container as a collection of N rows.
        modifier = Modifier.semantics {
            collectionInfo = CollectionInfo(rowCount = folders.size, columnCount = 1)
        }
    ) {
        folders.forEachIndexed { index, (name, count) ->
            FolderRow(
                name = name,
                count = count,
                modifier = Modifier.semantics(mergeDescendants = true) {
                    // Position in the list → TalkBack appends "2 of 5".
                    collectionItemInfo = CollectionItemInfo(
                        rowIndex = index,
                        rowSpan = 1,
                        columnIndex = 0,
                        columnSpan = 1
                    )
                }
            )
        }
    }
}

@Composable
private fun FolderRow(name: String, count: Int, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        tonalElevation = 2.dp,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Folder, contentDescription = null)
            Text(name, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
            Text("$count", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
