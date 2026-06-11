package chat.nami.kotlin.typeSystemAndLanguageCore

@DslMarker
annotation class HtmlDsl

@HtmlDsl
class Div {
    fun text(s: String) = println("  text: $s")
}

@HtmlDsl
class Body {
    fun div(block: Div.() -> Unit) {
        println("<div>")
        Div().block()
        println("</div>")
    }
}

fun body(block: Body.() -> Unit) = Body().block()

// Extension function
fun Int.powExt(exp: Int): Long {
    var r = 1L; repeat(exp) { r *= this }; return r
}

// Infix function
infix fun Int.pow(exp: Int): Long {
    var r = 1L; repeat(exp) { r *= this }; return r
}

// Extension function resolution
open class Base
class Derived : Base()

fun Base.describe() = "base"
fun Derived.describe() = "derived"

fun main() {

    println(2.powExt(10))   // 1024 — extension
    println(2 pow 10)   // 1024 — infix

    body {
        div {
            text("ok")
            // div { }   // ❌ compile error thanks to @DslMarker —
            //           // without it, this would silently call body.div
        }
    }

    val x: Base = Derived()
    println(x.describe())   // "base" — resolved by STATIC type, at compile time
}
