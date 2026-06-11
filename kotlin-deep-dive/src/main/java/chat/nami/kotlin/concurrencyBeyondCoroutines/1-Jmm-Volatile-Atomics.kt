package chat.nami.kotlin.concurrencyBeyondCoroutines

import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread

class Worker {
    @Volatile var running = true        // visibility: writes seen by all threads
    val processed = AtomicInteger(0)    // atomicity: increment is CAS, race-free

    fun loop() {
        while (running) {
            processed.incrementAndGet()
        }
    }
}

fun main() {
    val w = Worker()
    val t = thread { w.loop() }
    Thread.sleep(50)
    w.running = false       // without @Volatile, t might NEVER see this write
    t.join()
    println("processed ${w.processed.get()}")
}

// - @Volatile ≠ atomic: running++ would still be a read-modify-write race; that's what atomics are for.
// - Multiplatform: kotlinx-atomicfu → val count = atomic(0); count.incrementAndGet().
// - JMM mental model: without synchronization (@Volatile, locks, atomics, or coroutine
//   happens-before edges), one thread's writes may never become visible to another.
//   Coroutine primitives (launch, join, Mutex, channels) establish happens-before —
//   data handed through them is safe.
