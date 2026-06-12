package chat.nami.kotlin.f_Coroutines

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit

var counter = 0
val mutex = Mutex()
val limiter = Semaphore(permits = 2)

suspend fun fetch(id: Int) = limiter.withPermit {   // max 2 concurrent "requests"
    println("fetching $id")
    delay(100)
}

fun main() = runBlocking {
    // Mutex: suspends instead of blocking the thread
    val incrementJobs = List(100) {
        launch(Dispatchers.Default) {
            repeat(100) { mutex.withLock { counter++ } }
        }
    }
    incrementJobs.joinAll()
    println("counter = $counter")   // always 10000

    (1..5).map { launch { fetch(it) } }.joinAll()   // observe batches of 2
}

// - synchronized/ReentrantLock BLOCK the thread; never suspend inside synchronized
//   (the monitor is thread-owned; you may resume on a different thread).
// - Mutex is NOT reentrant — locking twice from the same coroutine deadlocks.
// - synchronized is fine for short, non-suspending critical sections.
