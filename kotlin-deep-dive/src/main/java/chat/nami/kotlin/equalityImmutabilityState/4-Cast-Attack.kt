package chat.nami.kotlin.equalityImmutabilityState

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class Repo {
    private val _cache = mutableListOf("legit")
    val cache: List<String> get() = _cache        // read-only INTERFACE...
}

sealed interface ScreenState {
    data object Loading : ScreenState
    data class Ready(val items: List<String>) : ScreenState
}

// Backing property pattern (_state/state) — the standard ViewModel idiom:
class ListViewModel {
    private val _state = MutableStateFlow<ScreenState>(ScreenState.Loading)
    val state: StateFlow<ScreenState> = _state.asStateFlow()  // wrapper — cast attack FAILS

    fun load() {
        _state.value = ScreenState.Ready(listOf("a", "b"))
    }
}

fun main() {
    val repo = Repo()

    @Suppress("UNCHECKED_CAST")
    (repo.cache as MutableList<String>).add("evil")   // ...same object — cast succeeds!
    println(repo.cache)   // [legit, evil]

    val vm = ListViewModel()
    vm.load()
    println(vm.state.value)
    println(vm.state is MutableStateFlow<*>)   // false — asStateFlow() returns a wrapper
}

// Kotlin's List is a read-only VIEW, not an immutability guarantee. Defenses:
// - Return a copy: get() = _cache.toList()
// - java.util.Collections.unmodifiableList(_cache) → cast succeeds but mutation throws
// - kotlinx.collections.immutable → genuinely immutable type
