package chat.nami.kotlin.collectionsAndPerformance

fun main() {
    val list = (1..1_000_000).toList()

    // EAGER: 2 intermediate million-element lists, full passes
    val eager = list.filter { it % 2 == 0 }.map { it * 2 }.take(3)

    // LAZY: element-by-element pipeline, stops after 3 results, ZERO intermediate lists
    val lazy = list.asSequence()
        .filter { it % 2 == 0 }
        .map { it * 2 }
        .take(3)
        .toList()

    println(eager)   // [4, 8, 12]
    println(lazy)    // [4, 8, 12]
}

// Sequences win: large collections + multiple ops, early termination (take, first, any),
// infinite sources (generateSequence).
// Collections win: small lists (sequence overhead > savings), needing size/index/multiple
// passes, ops that must materialize anyway (sorted).
// Sequence lambdas are NOT inlined (collection operators are) — for a 5-element list, eager is faster.
