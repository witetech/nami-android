package chat.nami.settings.presentation.viewmodel

import chat.nami.viewmodel.StateViewModel

internal interface SettingsViewModel : StateViewModel<SettingsState>

internal data class SettingsState(val message: String)
