package chat.nami.demo.architecture.mvvm

import androidx.lifecycle.ViewModel as AndroidViewModel
import androidx.lifecycle.viewModelScope
import chat.nami.demo.architecture.User
import chat.nami.demo.architecture.searchUsers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ViewModel : AndroidViewModel() {
    val isLoading: StateFlow<Boolean> get() = _isLoading
    val users: StateFlow<List<User>> get() = _users
    val error: StateFlow<String?> get() = _error

    private val _isLoading = MutableStateFlow(false)
    private val _users = MutableStateFlow<List<User>>(emptyList())
    private val _error = MutableStateFlow<String?>(null)

    private var searchJob: Job? = null

    fun search(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _isLoading.value = true
            _users.value = emptyList()
            _error.value = null

            try {
                _users.value = searchUsers(query)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}
