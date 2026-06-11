package chat.nami.kotlin.compilerInteropMetaprogramming

class Utils {
    companion object {
        @JvmStatic fun helper() = println("static for Java")
        // Java without @JvmStatic: Utils.Companion.helper()
    }
}

class Toaster {
    @JvmOverloads
    fun toast(msg: String, duration: Int = 1, gravity: Int = 0) =
        println("toast($msg, $duration, $gravity)")
    // generates 3 overloads for Java; default-value evaluation stays in Kotlin
}

@JvmField val EXPOSED_FIELD = 42       // raw field, no getter

fun main() {
    Utils.helper()
    Toaster().toast("merhaba")
    println(EXPOSED_FIELD)
}

// - Platform types: the #1 interop NPE source. Kotlin adds checkNotNullParameter intrinsics
//   on public function params — Java passing null into a non-null Kotlin param fails FAST
//   at the boundary (good).
// - @JvmName fixes mangled/clashing names; @file:JvmName("StringUtils") renames the FileKt facade.
// - Kotlin List ↔ Java List: read-only-ness is NOT enforced for Java callers.
