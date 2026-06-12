package chat.nami.kotlin.f_Coroutines

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

suspend fun releaseRemoteLock() {
    delay(50)
    println("lock released")
}

fun main() = runBlocking {
    val job = launch(Dispatchers.Default) {
        var i = 0
        while (isActive) {           // cooperative check — without it the loop never stops
            i++
        }
        println("stopped at $i")
    }
    delay(100)
    job.cancelAndJoin()

    // CancellationException is NORMAL control flow — never swallow it;
    // cleanup needs NonCancellable:
    val worker = launch {
        try {
            repeat(100) { delay(100); println("working $it") }
        } catch (e: CancellationException) {
            println("cancelled — rethrowing")
            throw e          // ALWAYS rethrow
        } finally {
            withContext(NonCancellable) { releaseRemoteLock() }  // suspending cleanup while cancelled
        }
    }
    delay(250)
    worker.cancelAndJoin()
}
