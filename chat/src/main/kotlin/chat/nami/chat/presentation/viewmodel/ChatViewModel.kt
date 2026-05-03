package chat.nami.chat.presentation.viewmodel

import chat.nami.chat.domain.model.Message
import chat.nami.viewmodel.StateViewModel

internal interface ChatViewModel : StateViewModel<ChatState> {
    fun onInputChange(text: String)
    fun sendMessage()
}

internal data class ChatState(val loading: Boolean, val input: String, val messages: List<Message>)
