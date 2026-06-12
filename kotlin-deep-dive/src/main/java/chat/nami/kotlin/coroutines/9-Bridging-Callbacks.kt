package chat.nami.kotlin.coroutines

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.concurrent.thread
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

// Pre-coroutine APIs hand results to callbacks (OkHttp Callback, LocationListener,
// billing client…). suspendCancellableCoroutine turns one-shot callbacks into a
// suspend function.

private class LegacyClient {
    @Volatile private var cancelled = false

    fun request(onSuccess: (String) -> Unit, onError: (Throwable) -> Unit) {
        thread {
            Thread.sleep(100)
            if (!cancelled) onSuccess("legacy payload")
        }
    }

    fun cancel() {
        cancelled = true
        println("legacy request cancelled")
    }
}

private suspend fun LegacyClient.requestSuspending(): String =
    suspendCancellableCoroutine { cont ->
        request(
            onSuccess = { cont.resume(it) },
            onError = { cont.resumeWithException(it) },
        )
        cont.invokeOnCancellation { cancel() }   // propagate coroutine cancellation to the API
    }

fun main() = runBlocking {
    println("got: ${LegacyClient().requestSuspending()}")

    val job = launch { LegacyClient().requestSuspending() }
    delay(20)
    job.cancel()        // → invokeOnCancellation fires, legacy work stops
    job.join()
}

// - Prefer suspendCancellableCoroutine over suspendCoroutine: without invokeOnCancellation
//   a cancelled coroutine still waits for the callback forever.
// - Resume exactly once (twice → IllegalStateException); resume after cancellation is ignored.
// - One-shot callback → suspend fun; repeating callback (listener) → callbackFlow { }.
