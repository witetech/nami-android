package chat.nami.kotlin.functionsAndFunctionalStyle

// no Function0 allocated — body copied into main() | Show decompiled code
inline fun measureNanos(block: () -> Unit): Long {
    val start = System.nanoTime()
    block()
    return System.nanoTime() - start
}

val pending = mutableListOf<() -> Unit>()

fun saveForLater(action: () -> Unit) {
    pending += action
}

/**
 * onEach: inlined, but non-local return FORBIDDEN, it cannot be stored & passed
 * onDone: not inlined, regular Function allocation
 */
inline fun process(
    crossinline onEach: (Int) -> Unit,
    noinline onDone: () -> Unit
) {
    val runnable = Runnable { onEach(1) }
    runnable.run()
    saveForLater(onDone)
}


fun main() {
    val t = measureNanos { Thread.sleep(10) }
    println("took $t ns")

    process(onEach = { println("item $it") }, onDone = { println("done") })
    pending.forEach { it() }
}


