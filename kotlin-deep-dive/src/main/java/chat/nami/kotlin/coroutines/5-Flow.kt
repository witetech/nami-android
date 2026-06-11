package chat.nami.kotlin.coroutines

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// StateFlow: required initial value, replay = 1 (latest), conflated → UI state
// SharedFlow: no initial value, configurable replay, no conflation → one-shot events
//   (events are dropped when no collector is active — for critical events model them
//   as state, or use Channel(...).receiveAsFlow())

fun searchApi(q: String): Flow<String> = flow {
    delay(100)                       // simulated network
    emit("results for '$q'")
}

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
fun main() = runBlocking {
    // COLD: code runs per collector
    val cold: Flow<Int> = flow {
        println("flow body running")
        emit(1)
        emit(2)
    }
    cold.collect { println("A got $it") }
    cold.collect { println("B got $it") }   // body runs AGAIN

    // HOT:
    val state = MutableStateFlow(0)           // always has a value, conflated, replay=1
    val events = MutableSharedFlow<String>()  // no initial value, configurable replay
    state.value = 5
    println("state now: ${state.value}")
    val sub = launch { events.collect { println("event: $it") } }
    delay(10)
    events.emit("clicked")
    delay(10)
    sub.cancel()

    // Operator pipeline (typeahead-search shape):
    val queries = flow {
        emit("k"); delay(50)
        emit("ki"); delay(50)
        emit("kilowatt"); delay(500)     // only this one survives debounce
    }

    queries
        .debounce(200)
        .distinctUntilChanged()
        .flatMapLatest { q -> searchApi(q) }   // cancels previous in-flight search
        .catch { e -> emit("fallback: ${e.message}") }  // catches UPSTREAM only
        .flowOn(Dispatchers.IO)                          // changes UPSTREAM context only
        .collect { println(it) }                          // results for 'kilowatt'
}

// Backpressure: flow suspends the emitter when the collector is slow (natural backpressure).
// Tuning: buffer(n), conflate() (keep latest), collectLatest { } (cancel in-progress handling).
// Hot sharing: shareIn(scope, SharingStarted.WhileSubscribed(5000), replay = 1) / stateIn(...)
// — 5000 ms keeps upstream alive across configuration changes.
