package chat.nami.kotlin.coroutines

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope

fun main() = runBlocking {
    // supervisorScope: children fail independently (default Job: one failure kills siblings)
    supervisorScope {
        val handler = CoroutineExceptionHandler { _, e -> println("caught: ${e.message}") }
        launch(handler) { delay(50); error("task A failed") }   // handler works: supervisor child
        launch { delay(100); println("task B survived") }
    }

    // async holds its exception until await():
    supervisorScope {
        val deferred = async { error("async boom") }
        try {
            deferred.await()
        } catch (e: IllegalStateException) {
            println("awaited: ${e.message}")
        }
    }
}

// Rules that trip everyone up:
// - launch propagates exceptions UP the Job tree; async holds until await() — but still
//   cancels the parent immediately unless under a SupervisorJob.
// - CoroutineExceptionHandler on a non-root child is ignored.
// - try/catch around launch { } catches NOTHING — catch inside the coroutine or around await().
// - Android: viewModelScope = SupervisorJob() + Dispatchers.Main.immediate.
