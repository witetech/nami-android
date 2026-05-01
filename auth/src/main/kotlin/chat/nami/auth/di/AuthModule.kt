package chat.nami.auth.di

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.credentials.CredentialManager
import androidx.lifecycle.viewmodel.compose.viewModel
import chat.nami.auth.data.mapper.RealUserMapper
import chat.nami.auth.data.repository.RealAuthRepository
import chat.nami.auth.domain.usecase.GetUserUseCase
import chat.nami.auth.domain.usecase.LoginWithGoogleUseCase
import chat.nami.auth.domain.usecase.LogoutUseCase
import chat.nami.auth.presentation.view.LoginScreen
import chat.nami.auth.presentation.viewmodel.LoginViewModel
import chat.nami.auth.presentation.viewmodel.RealLoginViewModel
import chat.nami.viewmodel.EventDelegate
import chat.nami.viewmodel.StateDelegate
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class AuthModule(applicationContext: Context) {

    private val firebaseAuth by lazy { Firebase.auth }

    private val firebaseFirestore by lazy { Firebase.firestore }

    private val credentialManager by lazy { CredentialManager.create(applicationContext) }

    private val userMapper by lazy { RealUserMapper() }

    private val authRepository by lazy {
        RealAuthRepository(firebaseAuth, firebaseFirestore, credentialManager, userMapper)
    }

    val getUser by lazy {
        GetUserUseCase(authRepository)
    }

    private val loginWithGoogle by lazy {
        LoginWithGoogleUseCase(authRepository)
    }

    private val logout by lazy {
        LogoutUseCase(authRepository)
    }

    @Composable
    private fun makeViewModel(): LoginViewModel = viewModel {
        RealLoginViewModel(
            loginWithGoogle = loginWithGoogle,
            stateDelegate = StateDelegate(),
            eventDelegate = EventDelegate()
        )
    }

    @Composable
    fun LoginScreenDestination(onLoggedIn: () -> Unit) {
        LoginScreen(
            viewModel = makeViewModel(),
            onLoggedIn = onLoggedIn
        )
    }
}
