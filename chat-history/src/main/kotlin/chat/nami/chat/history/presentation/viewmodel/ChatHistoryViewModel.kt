package chat.nami.chat.history.presentation.viewmodel

import chat.nami.viewmodel.StateViewModel

internal interface ChatHistoryViewModel : StateViewModel<ChatHistoryState>

internal data class ChatHistoryState(val message: String)
