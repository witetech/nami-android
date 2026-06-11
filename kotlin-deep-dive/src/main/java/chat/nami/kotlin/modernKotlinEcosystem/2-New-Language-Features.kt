package chat.nami.kotlin.modernKotlinEcosystem

// Guard conditions in when (stable since 2.2):

sealed interface LoadState {
    data class Success(val data: List<String>) : LoadState
    data object Error : LoadState
}

fun render(state: LoadState): String = when (state) {
    is LoadState.Success if state.data.isEmpty() -> "empty view"
    is LoadState.Success -> "list of ${state.data.size}"
    LoadState.Error -> "error view"
}

fun main() {
    println(render(LoadState.Success(emptyList())))
    println(render(LoadState.Success(listOf("a"))))

    // Non-local break/continue through inline lambdas (stable since 2.2):
    outer@ for (batch in listOf(listOf(1, 2), listOf(3, -1), listOf(4))) {
        batch.forEach { item ->
            if (item < 0) break@outer      // breaks the OUTER loop through an inline lambda
            println("processing $item")
        }
    }
}

// Explicit backing fields (preview; needs -Xexplicit-backing-fields) —
// kills the _state/state two-property idiom:
//
// class VM {
//     val state: StateFlow<UiState>
//         field = MutableStateFlow(UiState.Loading)   // internally MutableStateFlow
//     fun load() { state.value = UiState.Ready }      // inside: mutable; outside: StateFlow
// }

// Context parameters (preview, replaces context receivers; needs -Xcontext-parameters):
//
// interface Logger { fun log(msg: String) }
// context(logger: Logger)
// fun process() { logger.log("processing...") }
