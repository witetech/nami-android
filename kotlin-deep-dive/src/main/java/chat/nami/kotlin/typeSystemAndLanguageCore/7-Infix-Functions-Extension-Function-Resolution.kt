package chat.nami.kotlin.typeSystemAndLanguageCore

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

    val x: Base = Derived()
    println(x.describe())   // "base" — resolved by STATIC type, at compile time
}
