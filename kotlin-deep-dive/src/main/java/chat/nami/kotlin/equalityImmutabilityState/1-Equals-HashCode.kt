package chat.nami.kotlin.equalityImmutabilityState

// equals/hashCode generated from primary ctor properties
data class Point(val x: Int)

data class Profile(val id: Int) {
    var nickname: String = ""  // body property — IGNORED by equals/hashCode!
}

fun main() {
    val a = Point(1)
    val b = Point(1)
    println(a == b)    // true  — structural: a?.equals(b) ?: (b === null)
    println(a === b)   // false — referential: different objects

    // Boxed-int cache trap (-128..127):
    val x: Int? = 100
    val y: Int? = 100
    val p: Int? = 1000
    val q: Int? = 1000
    println(x === y)   // true  — cached Integer instances! never rely on this
    println(p === q)   // false

    // Body properties ignored:
    val u1 = Profile(1).apply { nickname = "A" }
    val u2 = Profile(1).apply { nickname = "B" }
    println(u1 == u2)  // true!

    // Arrays don't override equals:
    println(intArrayOf(1, 2) == intArrayOf(1, 2))             // false
    println(intArrayOf(1, 2).contentEquals(intArrayOf(1, 2))) // true
}

// Contracts: equal objects MUST have equal hashCodes; override both or neither.
// Data classes holding arrays need manual equals/hashCode (contentDeepEquals).
