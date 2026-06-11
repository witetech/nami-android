package chat.nami.kotlin.errorModeling

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

suspend fun fetchData(): String {
    delay(1000)
    return "data"
}

// The cancellation-aware variant every codebase ends up writing:
inline fun <T> runCatchingCancellable(block: () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e   // let cancellation propagate
    } catch (e: Throwable) {
        Result.failure(e)
    }

fun main() = runBlocking {
    // ⚠️ BUG: runCatching catches Throwable — INCLUDING CancellationException.
    // Cancellation is swallowed; the coroutine "completes" with a failure Result.
    val buggy = launch {
        val result = runCatching { fetchData() }
        println("swallowed cancellation, got: $result")   // this PRINTS — coroutine didn't cancel properly
    }
    delay(100)
    buggy.cancelAndJoin()

    val fixed = launch {
        val result = runCatchingCancellable { fetchData() }
        println("unreachable on cancel: $result")
    }
    delay(100)
    fixed.cancelAndJoin()
    println("cancelled cleanly")
}

// Other Result notes: it's a value class over Any?; failures are untyped (Throwable) —
// you lose the error taxonomy.
