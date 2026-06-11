package chat.nami.kotlin.classesInitializationLifecycle

const val APP_NAME = "Kilowatt"     // top-level const — inlined into consumers at compile time

class Repo private constructor() {
    companion object {
        const val TIMEOUT_SECONDS = 30        // inlined at call sites
        val createdAt = System.currentTimeMillis()  // accessed via Companion instance
        fun create() = Repo()      // real static method for Java callers
    }
}

fun main() {
    println(APP_NAME)
    println(Repo.TIMEOUT_SECONDS)
    println(Repo.createdAt)
    val repo = Repo.create()                  // Java without @JvmStatic: Repo.Companion.create()
    println(repo)
}
