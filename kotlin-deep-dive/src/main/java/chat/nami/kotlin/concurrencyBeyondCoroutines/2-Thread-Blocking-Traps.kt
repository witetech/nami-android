package chat.nami.kotlin.concurrencyBeyondCoroutines

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

fun main() = runBlocking {
    // ❌ Blocks a Default pool thread (pool ≈ CPU cores → easy starvation):
    // withContext(Dispatchers.Default) { Thread.sleep(1000) }

    // ❌ runBlocking inside a coroutine on Main → instant deadlock risk
    // ❌ CountDownLatch.await(), future.get(), synchronized + long work

    // ✅ delay suspends; the thread serves other coroutines meanwhile:
    withContext(Dispatchers.Default) {
        delay(100)
        println("suspended politely")
    }

    // ✅ genuinely blocking I/O belongs on IO (the elastic pool exists for exactly this):
    withContext(Dispatchers.IO) {
        Thread.sleep(100)   // stand-in for a blocking JDBC/file call
        println("blocked on IO pool — acceptable")
    }
}

// Why synchronized doesn't suspend: monitors are THREAD-owned. A coroutine may suspend
// on thread A and resume on thread B — the monitor would be "held" by A forever.
// Hence Mutex (coroutine-owned, suspension-based) for suspending critical sections.
