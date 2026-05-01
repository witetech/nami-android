package chat.nami.auth.presentation.viewmodel

import android.content.Context
import chat.nami.viewmodel.EventViewModel
import chat.nami.viewmodel.StateViewModel

internal interface LoginViewModel :
    StateViewModel<LoginState>,
    EventViewModel<LoginEvent> {
    fun loginWithGoogle(activityContext: Context)
}

internal data class LoginState(val loading: Boolean)

internal sealed interface LoginEvent {
    data object Success : LoginEvent
    data object Error : LoginEvent
}
