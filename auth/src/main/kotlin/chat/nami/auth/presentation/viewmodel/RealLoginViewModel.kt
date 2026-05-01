package chat.nami.auth.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import chat.nami.auth.domain.usecase.GetUser
import chat.nami.auth.domain.usecase.LoginWithGoogle
import chat.nami.auth.domain.usecase.Logout
import chat.nami.viewmodel.EventDelegate
import chat.nami.viewmodel.EventViewModel
import chat.nami.viewmodel.StateDelegate
import chat.nami.viewmodel.StateViewModel
import kotlinx.coroutines.launch

internal class RealLoginViewModel(
    private val getUser: GetUser,
    private val loginWithGoogle: LoginWithGoogle,
    private val logout: Logout,
    private val stateDelegate: StateDelegate<LoginState>,
    private val eventDelegate: EventDelegate<LoginEvent>
) : ViewModel(),
    LoginViewModel,
    StateViewModel<LoginState> by stateDelegate,
    EventViewModel<LoginEvent> by eventDelegate {

    init {
        stateDelegate.setDefaultState(LoginState(user = null, loading = false))
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            try {
                val user = getUser()
                if (user != null) {
                    stateDelegate.updateState { it.copy(user = user) }
                }
            } catch (e: Exception) {

            }
        }
    }

    override fun loginWithGoogle(activityContext: Context) {
        stateDelegate.updateState { it.copy(loading = true) }
        viewModelScope.launch {
            try {
                val user = loginWithGoogle.invoke(activityContext)
                stateDelegate.updateState { it.copy(user = user) }
                eventDelegate.sendEvent(viewModelScope, LoginEvent.Success)
            } catch (e: Exception) {
                eventDelegate.sendEvent(
                    viewModelScope,
                    LoginEvent.ShowError(message = e.message ?: "")
                )
            } finally {
                stateDelegate.updateState { it.copy(loading = false) }
            }
        }
    }

    override fun logout() {
        stateDelegate.updateState { it.copy(loading = true) }
        viewModelScope.launch {
            try {
                logout.invoke()
                stateDelegate.updateState { it.copy(user = null) }
            } catch (e: Exception) {
                eventDelegate.sendEvent(
                    viewModelScope,
                    LoginEvent.ShowError(message = e.message ?: "")
                )
            } finally {
                stateDelegate.updateState { it.copy(loading = false) }
            }
        }
    }
}
