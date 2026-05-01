package chat.nami.auth.presentation.view

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import chat.nami.auth.presentation.viewmodel.LoginEvent
import chat.nami.auth.presentation.viewmodel.LoginState
import chat.nami.auth.presentation.viewmodel.LoginViewModel
import chat.nami.design.NamiTheme
import chat.nami.viewmodel.EventViewModel
import chat.nami.viewmodel.StateViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
internal fun LoginScreen(loginViewModel: LoginViewModel, onLoggedIn: () -> Unit) {
    val context = LocalContext.current

    LaunchedEffect(key1 = Unit) {
        loginViewModel.viewEvent.onEach { viewEvent ->
            when (viewEvent) {
                LoginEvent.Success -> {
                    onLoggedIn()
                }

                is LoginEvent.ShowError -> {
                }
            }
        }.launchIn(this)
    }

    val loginState by loginViewModel.state.collectAsState()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = loginState.userId)

                Button(
                    enabled = !loginState.loading,
                    onClick = { loginViewModel.loginWithGoogle(context) }
                ) {
                    Text(text = "Login with Google")
                }

                Button(
                    enabled = !loginState.loading,
                    onClick = { loginViewModel.logout() }
                ) {
                    Text(text = "Logout")
                }
            }
        }
    }
}

private class TestLoginViewModel :
    LoginViewModel,
    StateViewModel<LoginState>,
    EventViewModel<LoginEvent> {
    override val state = MutableStateFlow(LoginState(userId = "123", loading = false))
    override val viewEvent = emptyFlow<LoginEvent>()

    override fun loginWithGoogle(activityContext: Context) {
        TODO("Not yet implemented")
    }

    override fun logout() {
        TODO("Not yet implemented")
    }
}

@Composable
@Preview
private fun LoginScreenPreview() {
    NamiTheme {
        LoginScreen(
            loginViewModel = TestLoginViewModel(),
            onLoggedIn = {}
        )
    }
}
