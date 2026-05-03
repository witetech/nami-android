package chat.nami.chat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import chat.nami.chat.domain.model.Message
import chat.nami.viewmodel.StateDelegate
import chat.nami.viewmodel.StateViewModel
import kotlin.random.Random
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal class RealChatViewModel(private val stateDelegate: StateDelegate<ChatState>) :
    ViewModel(),
    ChatViewModel,
    StateViewModel<ChatState> by stateDelegate {

    init {
        stateDelegate.setDefaultState(
            ChatState(
                loading = false,
                input = "",
                messages = emptyList()
            )
        )
    }

    override fun onInputChange(text: String) {
        stateDelegate.updateState { it.copy(input = text) }
    }

    override fun sendMessage() {
        stateDelegate.updateState {
            it.copy(
                input = "",
                messages = state.value.messages + Message.User(
                    id = "id" + Random(1000).nextInt(),
                    content = state.value.input
                )
            )
        }

        viewModelScope.launch {
            delay(1000)
            stateDelegate.updateState {
                it.copy(
                    messages = state.value.messages + Message.Assistant(
                        id = "id" + Random(1000).nextInt(),
                        content = "Hey",
                        model = "Model"
                    )
                )
            }
        }
    }
}
