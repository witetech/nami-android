package chat.nami.demo.architecture.mvi

import androidx.lifecycle.ViewModel as AndroidViewModel
import androidx.lifecycle.viewModelScope
import chat.nami.demo.architecture.User
import chat.nami.demo.architecture.searchUsers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val users: List<User> = emptyList(),
    val error: String? = null
)

sealed interface Intent {
    data class Search(val query: String) : Intent
    data object Retry : Intent
}

class ViewModel : AndroidViewModel() {

    val state: StateFlow<UiState> get() = _state
    private val _state = MutableStateFlow(UiState())

    private var searchJob: Job? = null

    fun processIntent(intent: Intent) {
        when (intent) {
            is Intent.Search -> search(intent.query)
            Intent.Retry -> search(_state.value.query)
        }
    }

    private fun search(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _state.update { UiState(query = query, isLoading = true) }
            runCatching { searchUsers(query) }
                .onSuccess { result ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            users = result
                        )
                    }
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }
}
