package chat.nami.kotlin.coroutines

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking

// Flow<T> = a suspending stream: List<T> gives all values at once,
// Flow<T> delivers them over time. Nothing runs until a TERMINAL operator collects.

private fun temperatures(): Flow<Int> = flow {   // flow { } can call suspend functions
    var celsius = 20
    while (true) {
        delay(50)                                // pretend sensor latency
        emit(celsius++)                          // push a value downstream
    }
}

fun main() = runBlocking {
    // builders
    flowOf(1, 2, 3).collect { println("flowOf → $it") }
    listOf("a", "b").asFlow().collect { println("asFlow → $it") }

    // intermediate operators are LAZY — this line alone runs nothing:
    val pipeline = temperatures()
        .map { it * 9 / 5 + 32 }                 // °C → °F
        .filter { it % 2 == 0 }
        .onEach { println("passing $it") }

    // terminal operators start the flow:
    println("first even °F: ${pipeline.first()}")
    println("next three: ${pipeline.take(3).toList()}")   // body restarts — flows are cold
}

// - Terminal: collect / first / toList / fold. Intermediate: map / filter / take / onEach.
// - An infinite flow is fine: first() and take(n) cancel the upstream when satisfied.
// - Cold vs hot, StateFlow vs SharedFlow, backpressure → next file.
