package chat.nami.settings.di

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import chat.nami.settings.presentation.view.SettingsScreen
import chat.nami.settings.presentation.viewmodel.RealSettingsViewModel
import chat.nami.settings.presentation.viewmodel.SettingsViewModel
import chat.nami.viewmodel.StateDelegate

class SettingsModule(applicationContext: Context) {

    @Composable
    private fun makeViewModel(): SettingsViewModel = viewModel {
        RealSettingsViewModel(stateDelegate = StateDelegate())
    }

    @Composable
    fun SettingsScreenDestination() {
        SettingsScreen(viewModel = makeViewModel())
    }
}
