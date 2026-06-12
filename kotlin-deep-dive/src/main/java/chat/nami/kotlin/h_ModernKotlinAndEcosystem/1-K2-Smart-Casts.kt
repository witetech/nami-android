package chat.nami.kotlin.h_ModernKotlinAndEcosystem

// K2 (Kotlin 2.0+): complete frontend rewrite (FIR) — up to ~2x compile speed,
// one unified resolution structure, smarter smart casts (stable across more scopes):

fun describe(x: Any): String {
    val isString = x is String
    return if (isString) "string of length ${x.length}"   // K2 smart-casts via a boolean variable
    else "not a string"
}

fun main() {
    println(describe("kotlin"))
    println(describe(42))
}
