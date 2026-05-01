package chat.nami.chat.di

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import chat.nami.chat.presentation.view.ChatScreen
import chat.nami.chat.presentation.viewmodel.ChatViewModel
import chat.nami.chat.presentation.viewmodel.RealChatViewModel
import chat.nami.viewmodel.StateDelegate

class ChatModule(applicationContext: Context) {

    @Composable
    private fun makeViewModel(): ChatViewModel = viewModel {
        RealChatViewModel(
            stateDelegate = StateDelegate()
        )
    }

    @Composable
    fun ChatScreenDestination() {
        ChatScreen(viewModel = makeViewModel())
    }
}
