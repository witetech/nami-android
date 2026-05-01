package chat.nami.chat.history.presentation.viewmodel

import androidx.lifecycle.ViewModel
import chat.nami.viewmodel.StateDelegate
import chat.nami.viewmodel.StateViewModel

internal class RealChatHistoryViewModel(stateDelegate: StateDelegate<ChatHistoryState>) :
    ViewModel(),
    ChatHistoryViewModel,
    StateViewModel<ChatHistoryState> by stateDelegate {

    init {
        stateDelegate.setDefaultState(ChatHistoryState(message = ""))
    }
}
