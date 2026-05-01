package chat.nami.chat.presentation.viewmodel

import chat.nami.viewmodel.StateViewModel

internal interface ChatViewModel : StateViewModel<ChatState> {
    fun sendMessage()
}

internal data class ChatState(val message: String)
