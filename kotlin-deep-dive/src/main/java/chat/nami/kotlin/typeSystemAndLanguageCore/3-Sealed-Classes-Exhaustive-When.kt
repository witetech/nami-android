package chat.nami.kotlin.typeSystemAndLanguageCore

data class Item(val title: String)

sealed interface UiState {
    data object Loading : UiState
    data class Success(val data: List<Item>) : UiState
    data class Error(val cause: Throwable) : UiState
}

fun render(state: UiState): String = when (state) {  // exhaustive — no `else` needed
    UiState.Loading -> "spinner"
    is UiState.Success -> "list of ${state.data.size}"   // smart cast
    is UiState.Error -> "error: ${state.cause.message}"
}

fun main() {
    println(render(UiState.Loading))
    println(render(UiState.Success(listOf(Item("a")))))
    println(render(UiState.Error(IllegalStateException("boom"))))
}
