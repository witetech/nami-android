package chat.nami.kotlin.equalityImmutabilityState

class Cart(items: List<String>) {
    private val items: List<String> = items.toList()   // defensive copy

    fun items(): List<String> = items
}

fun main() {
    val list = mutableListOf(1, 2)   // reference is fixed; contents are not
    list.add(3)
    println(list)                    // [1, 2, 3]

    val holder = object {
        val time: Long get() = System.nanoTime()   // a val that changes per read!
    }
    println(holder.time == holder.time)            // false

    // Defensive copies at boundaries:
    val source = mutableListOf("a")
    val cart = Cart(source)
    source.add("b")               // caller mutates their list...
    println(cart.items())         // [a] — cart unaffected
}

// Deep-immutability strategies:
// - Immutable data all the way down: data class + val + read-only List/Map + immutable element types.
// - kotlinx.collections.immutable (PersistentList) for GUARANTEED immutability.
// - Defensive copies at boundaries.
