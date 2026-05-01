package chat.nami.chat.history.di

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import chat.nami.chat.history.presentation.view.ChatHistoryScreen
import chat.nami.chat.history.presentation.viewmodel.ChatHistoryViewModel
import chat.nami.chat.history.presentation.viewmodel.RealChatHistoryViewModel
import chat.nami.viewmodel.StateDelegate

class ChatHistoryModule(applicationContext: Context) {

    @Composable
    private fun makeViewModel(): ChatHistoryViewModel = viewModel {
        RealChatHistoryViewModel(stateDelegate = StateDelegate())
    }

    @Composable
    fun ChatHistoryScreenDestination() {
        ChatHistoryScreen(viewModel = makeViewModel())
    }
}
