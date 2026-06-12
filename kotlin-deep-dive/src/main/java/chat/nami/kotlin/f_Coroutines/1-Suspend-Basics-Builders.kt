package chat.nami.kotlin.f_Coroutines

import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis
import kotlin.time.Duration.Companion.seconds

// suspend = "this function can PAUSE without blocking its thread".
// While paused the thread is free to run other coroutines; the compiler does the
// bookkeeping (see 11-Coroutine-Internals). Thread.sleep would hold the thread hostage.

// suspends — does NOT block the thread
private suspend fun fetchToken(): String {
    delay(1.seconds)
    return "token-42"
}

private suspend fun fetchUserName(token: String): String {
    delay(1.seconds)
    return "Ömer ($token)"
}

fun main() = runBlocking {           // bridge from blocking world; main() and tests only

    // suspend code is SEQUENTIAL by default — reads top-to-bottom like blocking code:
    val sequential = measureTimeMillis {
        val token = fetchToken()
        println("sequential: ${fetchUserName(token)}")
    }
    println("took ~$sequential ms")  // ~200

    // launch = fire-and-forget; returns a Job (a handle: join/cancel), no result
    val job = launch {
        delay(50)
        println("side effect done")
    }
    job.join()

    // async = concurrency with a RESULT; returns Deferred<T>, await() suspends for it
    val concurrent = measureTimeMillis {
        val a = async { fetchToken() }
        val b = async { fetchToken() }
        println("two tokens: ${a.await()}, ${b.await()}")
    }
    println("took ~$concurrent ms")  // ~100 — they ran concurrently
}

// - suspend functions can only be called from other suspend functions or from a builder
//   (runBlocking / launch / async) — the compiler enforces it.
// - launch when you don't need a value; async only when you do — async + immediate
//   await() is just a verbose sequential call.
// - delay(100) vs Thread.sleep(100): same wait, but sleep blocks the thread.
