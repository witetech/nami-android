package chat.nami.kotlin.coroutines

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

// Main / Main.immediate → UI thread; .immediate skips re-dispatch if already on Main
// Default               → shared pool ≈ CPU cores; CPU work: parsing, diffing, sorting
// IO                    → elastic pool (default cap 64); blocking I/O: Room, files, OkHttp sync
// Unconfined            → no dispatch; tests/edge cases

suspend fun parseLargeJson(): Int = withContext(Dispatchers.Default) {
    println("parsing on ${Thread.currentThread().name}")
    (1..1_000_000).sum()
}

val dbWriter = Dispatchers.IO.limitedParallelism(1)   // serial executor view of IO

suspend fun writeRow(n: Int) = withContext(dbWriter) {
    println("writing $n on ${Thread.currentThread().name}")
}

fun main() = runBlocking {
    println("sum = ${parseLargeJson()}")
    (1..3).map { launch { writeRow(it) } }.joinAll()   // serialized, never concurrent
}

// - Default and IO SHARE threads — withContext(Dispatchers.IO) from Default often doesn't
//   switch threads, just the bookkeeping (cheap).
// - withContext also acts as a coroutineScope (waits for children).
// - Best practice: make suspend functions MAIN-SAFE (they hop internally); callers
//   shouldn't need withContext.
