package chat.nami.presentation.viewmodel

import chat.nami.auth.domain.model.User
import chat.nami.viewmodel.StateViewModel

internal interface MainViewModel : StateViewModel<MainState> {
    fun fetchUser()
}

internal data class MainState(val loading: Boolean, val user: User?)
