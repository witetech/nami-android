package chat.nami.auth.domain.usecase

import android.content.Context
import chat.nami.auth.domain.repository.AuthRepository

internal class LoginWithGoogleUseCase(private val authRepository: AuthRepository) :
    LoginWithGoogle {
    override suspend fun invoke(activityContext: Context) =
        authRepository.loginWithGoogle(activityContext)
}
