package chat.nami.kotlin.compilerInteropMetaprogramming

// Tools → Kotlin → Show Kotlin Bytecode → Decompile — recurring demo device. Best demos:
// 1. inline vs normal higher-order fn → the lambda class disappears (functionsAndFunctionalStyle/1-Inline.kt)
// 2. data class → generated equals/hashCode/toString/componentN/copy (below)
// 3. suspend fun with 2 suspension points → state machine + label (coroutines/8-Coroutine-Internals.kt)
// 4. const val vs val in companion → inlining vs Companion.getX() (classesInitializationLifecycle/4-Companion-Object.kt)
// 5. value class param → mangled method with primitive signature
// 6. when on sealed → instanceof chain / tableswitch (below)

data class Vehicle(val plate: String, val kwh: Double)   // decompile: see the generated members

sealed interface ChargeState {
    data object Idle : ChargeState
    data class Charging(val percent: Int) : ChargeState
}

fun describeCharge(state: ChargeState): String = when (state) {   // decompile: instanceof chain
    ChargeState.Idle -> "idle"
    is ChargeState.Charging -> "charging ${state.percent}%"
}

fun main() {
    println(Vehicle("34 ABC 123", 77.4))
    println(describeCharge(ChargeState.Charging(80)))
}
