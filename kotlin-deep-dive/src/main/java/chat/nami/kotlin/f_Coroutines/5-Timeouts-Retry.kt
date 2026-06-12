package chat.nami.kotlin.f_Coroutines

import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.milliseconds

// withTimeout cancels its block when time runs out and throws
// TimeoutCancellationException — a SUBCLASS of CancellationException (see 4):
// it cancels cooperatively, so non-cancellable blocking code ignores it.

private suspend fun slowFetch(attempt: Int): String {
    delay(if (attempt < 3) 300 else 50)   // first two attempts are too slow
    return "payload (attempt $attempt)"
}

private suspend fun <T> retry(
    times: Int,
    initialDelay: Long = 100,
    block: suspend (attempt: Int) -> T,
): T {
    var backoff = initialDelay
    repeat(times - 1) { attempt ->
        try {
            return block(attempt + 1)
        } catch (e: TimeoutCancellationException) {
            println("attempt ${attempt + 1} timed out, retrying in $backoff ms")
            delay(backoff)
            backoff *= 2                  // exponential backoff
        }
    }
    return block(times)                   // last attempt: let failure propagate
}

fun main() = runBlocking {
    // withTimeoutOrNull: null instead of an exception — good for optional work
    val fast = withTimeoutOrNull(100.milliseconds) { slowFetch(attempt = 1) }
    println("got: $fast")                 // null

    val result = retry(times = 3) { attempt ->
        withTimeout(200.milliseconds) { slowFetch(attempt) }
    }
    println("got: $result")               // succeeds on attempt 3
}

// - Catch TimeoutCancellationException OUTSIDE withTimeout (the caller side, as retry does).
//   Catching plain CancellationException there would also swallow real cancellation.
// - Only retry what is safe to repeat (idempotent reads); add jitter in real systems.
