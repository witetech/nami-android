package chat.nami.kotlin.classesInitializationLifecycle

class ListAdapter(val label: String)

class Screen {
    lateinit var adapter: ListAdapter

    fun bindIfReady() {
        if (::adapter.isInitialized) {        // reflection-free check via property reference
            println("bound: ${adapter.label}")
        } else {
            println("not initialized yet")
        }
    }
}

fun main() {
    val screen = Screen()
    screen.bindIfReady() // not initialized yet
    screen.adapter = ListAdapter("items")
    screen.bindIfReady() // bound: items
}
