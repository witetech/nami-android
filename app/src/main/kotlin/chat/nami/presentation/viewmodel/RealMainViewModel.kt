package chat.nami.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import chat.nami.auth.domain.usecase.GetUser
import chat.nami.viewmodel.StateDelegate
import chat.nami.viewmodel.StateViewModel
import kotlinx.coroutines.launch

internal class RealMainViewModel(
    private val getUser: GetUser,
    private val stateDelegate: StateDelegate<MainState>
) : ViewModel(),
    MainViewModel,
    StateViewModel<MainState> by stateDelegate {

    init {
        stateDelegate.setDefaultState(MainState(loading = true, user = null))
    }

    override fun fetchUser() {
        stateDelegate.updateState { it.copy(loading = true) }
        viewModelScope.launch {
            try {
                val user = getUser()
                stateDelegate.updateState { it.copy(user = user) }
            } catch (_: Exception) {
            } finally {
                stateDelegate.updateState { it.copy(loading = false) }
            }
        }
    }
}
