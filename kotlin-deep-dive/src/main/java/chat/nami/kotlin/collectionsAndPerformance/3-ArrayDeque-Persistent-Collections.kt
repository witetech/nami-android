package chat.nami.kotlin.collectionsAndPerformance

import kotlinx.collections.immutable.persistentListOf

fun main() {
    val undoStack = ArrayDeque<String>()        // kotlin.collections.ArrayDeque — ring buffer
    undoStack.addLast("typed a")
    undoStack.addLast("typed b")
    println(undoStack.removeLast())             // typed b — stack
    undoStack.addFirst("urgent")
    println(undoStack.removeFirst())            // urgent — queue

    // Persistent collections (kotlinx-collections-immutable):
    val v1 = persistentListOf(1, 2)
    val v2 = v1.add(3)        // v1 unchanged; v2 shares structure with v1
    println(v1)               // [1, 2]
    println(v2)               // [1, 2, 3]
}

// Compose angle: a List parameter is UNSTABLE; ImmutableList is stable → fewer recompositions.
