package chat.nami.settings.presentation.viewmodel

import androidx.lifecycle.ViewModel
import chat.nami.viewmodel.StateDelegate
import chat.nami.viewmodel.StateViewModel

internal class RealSettingsViewModel(stateDelegate: StateDelegate<SettingsState>) :
    ViewModel(),
    SettingsViewModel,
    StateViewModel<SettingsState> by stateDelegate {

    init {
        stateDelegate.setDefaultState(SettingsState(message = ""))
    }
}
