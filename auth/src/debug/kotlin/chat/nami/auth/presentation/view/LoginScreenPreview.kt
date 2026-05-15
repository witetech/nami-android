package chat.nami.auth.presentation.view

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import chat.nami.auth.presentation.viewmodel.LoginState
import chat.nami.design.NamiTheme

@Composable
@Preview
private fun LoginScreenPreview() {
    NamiTheme {
        Content(
            state = LoginState(loading = false),
            snackbarHostState = SnackbarHostState(),
            onLoginClick = {}
        )
    }
}
