package chat.nami.auth.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import chat.nami.auth.domain.usecase.LoginWithGoogle
import chat.nami.viewmodel.EventDelegate
import chat.nami.viewmodel.EventViewModel
import chat.nami.viewmodel.StateDelegate
import chat.nami.viewmodel.StateViewModel
import kotlinx.coroutines.launch

internal class RealLoginViewModel(
    private val loginWithGoogle: LoginWithGoogle,
    private val stateDelegate: StateDelegate<LoginState>,
    private val eventDelegate: EventDelegate<LoginEvent>
) : ViewModel(),
    LoginViewModel,
    StateViewModel<LoginState> by stateDelegate,
    EventViewModel<LoginEvent> by eventDelegate {

    init {
        stateDelegate.setDefaultState(LoginState(loading = false))
    }

    override fun loginWithGoogle(activityContext: Context) {
        stateDelegate.updateState { it.copy(loading = true) }
        viewModelScope.launch {
            try {
                loginWithGoogle.invoke(activityContext)
                eventDelegate.sendEvent(viewModelScope, LoginEvent.Success)
            } catch (_: Exception) {
                eventDelegate.sendEvent(viewModelScope, LoginEvent.Error)
            } finally {
                stateDelegate.updateState { it.copy(loading = false) }
            }
        }
    }
}
