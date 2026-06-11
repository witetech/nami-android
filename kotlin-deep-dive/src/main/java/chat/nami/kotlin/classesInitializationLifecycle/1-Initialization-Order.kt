package chat.nami.kotlin.classesInitializationLifecycle

// Execution order: superclass constructor → property initializers + `init` blocks in source order → constructor body.

open class BaseScreen {
    open val size: Int = 10

    init {
        println("BaseScreen init, size = $size")
    }   // calls the OVERRIDDEN getter!
}

class DerivedScreen : BaseScreen() {
    override val size: Int = 20 // stored
    // override val size: Int get() = 20 // computed
}

fun main() {
    DerivedScreen()  // prints "BaseScreen init, size = 0"
    // ← DerivedScreen.size backing field not yet initialized!
}
