package chat.nami.kotlin.d_EqualityImmutabilityAndState

class TagStore {
    private val _tags = mutableSetOf<String>()
    val tags: Set<String> get() = _tags.toSet()   // copy on read — fully safe, costs allocation

    fun add(tag: String) {
        _tags += tag
    }
}

fun main() {
    val store = TagStore()
    store.add("ev")
    store.add("charging")
    println(store.tags)
}
