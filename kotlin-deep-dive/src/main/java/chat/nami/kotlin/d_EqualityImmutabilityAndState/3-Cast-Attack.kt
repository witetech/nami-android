package chat.nami.kotlin.d_EqualityImmutabilityAndState

// UNSAFE: the getter hands out the SAME object the class mutates internally.
// The declared type List<String> is a compile-time label; the runtime object is an ArrayList.
class UnsafeRepo {
    private val _cache = mutableListOf("legit")
    val cache: List<String> get() = _cache        // read-only INTERFACE...
}

// SAFE: publish a wrapper, not the object. The wrapper's runtime class implements ONLY List —
// no add/set anywhere on the object; the mutable list hides in a private delegate field.
class ReadOnlyList<T>(private val backing: List<T>) : List<T> by backing {
    override fun toString() = backing.toString()   // delegation forwards List methods, not Any's
}

class SafeRepo {
    private val _cache = mutableListOf("legit")
    val cache: List<String> = ReadOnlyList(_cache)   // wrapper — cast attack FAILS

    fun add(item: String) {
        _cache += item                               // wrapper is a LIVE view, callers see this
    }
}

fun main() {
    val unsafe = UnsafeRepo()

    @Suppress("UNCHECKED_CAST")
    (unsafe.cache as MutableList<String>).add("evil")   // ...same object — cast succeeds!
    println(unsafe.cache)   // [legit, evil]

    val safe = SafeRepo()
    safe.add("also legit")
    println(safe.cache)                       // [legit, also legit] — live view, no copying
    println(safe.cache is MutableList<*>)     // false — wrapper implements only List

    try {
        @Suppress("UNCHECKED_CAST")
        (safe.cache as MutableList<String>).add("evil")
    } catch (e: ClassCastException) {
        println("attack failed: $e")
    }
}

// Kotlin's List is a read-only VIEW, not an immutability guarantee. Defenses:
// - Wrapper by delegation (above) — exactly how asStateFlow() protects _state in a ViewModel
// - Return a copy: get() = _cache.toList()
// - java.util.Collections.unmodifiableList(_cache) → cast succeeds but mutation throws
// - kotlinx.collections.immutable → genuinely immutable type
