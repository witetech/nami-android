package chat.nami.kotlin.f_Coroutines

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

// The compiler rewrites every suspend fun via Continuation-Passing Style.

suspend fun stepOne(): String {
    delay(10)
    return "A"
}

suspend fun stepTwo(prev: String): String {
    delay(10)
    return prev + "B"
}

suspend fun demo(): String {
    val a = stepOne()      // suspension point → label 0 → 1
    val b = stepTwo(a)     // suspension point → label 1 → 2
    return a + b
}

fun main() = runBlocking {
    println(demo())   // AAB
}

// What the compiler actually generates for demo (conceptually):
//
// fun demo(completion: Continuation<String>): Any?   // returns String or COROUTINE_SUSPENDED
// class DemoContinuation : ContinuationImpl { var label = 0; var a: String? ... }
// invokeSuspend() = when (label) { 0 -> ...; 1 -> ...; 2 -> ... }   // locals spilled into fields
//
// Live demo: Tools → Kotlin → Show Kotlin Bytecode → Decompile on demo(). Show the label
// switch and the spilled locals. Explains: why suspend functions need a suspend context
// (the hidden continuation parameter), and why coroutines are cheaper than threads
// (a small continuation object vs a ~1MB stack).
