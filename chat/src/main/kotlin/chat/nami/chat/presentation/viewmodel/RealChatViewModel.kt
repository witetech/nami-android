package chat.nami.chat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import chat.nami.viewmodel.StateDelegate
import chat.nami.viewmodel.StateViewModel

internal class RealChatViewModel(stateDelegate: StateDelegate<ChatState>) :
    ViewModel(),
    ChatViewModel,
    StateViewModel<ChatState> by stateDelegate {

    init {
        stateDelegate.setDefaultState(ChatState(message = ""))
    }

    override fun sendMessage() {
        TODO("Not yet implemented")
    }
}
