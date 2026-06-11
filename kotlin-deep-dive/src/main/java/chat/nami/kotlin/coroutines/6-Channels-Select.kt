package chat.nami.kotlin.coroutines

import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.selects.select

// Channel = hot, ONE value → ONE receiver (fan-out distributes among consumers).
// Flow → each collector gets everything.

fun main() = runBlocking {
    // Fan-out work queue:
    val jobs = Channel<Int>()
    repeat(2) { worker ->
        launch { for (job in jobs) println("worker $worker took job $job") }
    }
    (1..4).forEach { jobs.send(it) }
    jobs.close()
    delay(100)

    // select: race two sources, first wins
    val fast = Channel<String>()
    val slow = Channel<String>()
    launch { delay(50); fast.send("fast result") }
    launch { delay(500); slow.send("slow result") }
    val winner = select<String> {
        fast.onReceive { it }
        slow.onReceive { it }
    }
    println(winner)   // fast result
    coroutineContext.cancelChildren()
}
