package chat.nami.auth.presentation.viewmodel

import android.content.Context
import chat.nami.viewmodel.EventViewModel
import chat.nami.viewmodel.StateViewModel

internal interface LoginViewModel :
    StateViewModel<LoginState>,
    EventViewModel<LoginEvent> {
    fun loginWithGoogle(activityContext: Context)
    fun logout()
}

internal data class LoginState(val userId: String, val loading: Boolean)

internal sealed interface LoginEvent {
    data object Success : LoginEvent
    data class ShowError(val message: String) : LoginEvent
}
