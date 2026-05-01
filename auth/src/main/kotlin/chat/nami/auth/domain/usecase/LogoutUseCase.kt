package chat.nami.auth.domain.usecase

import chat.nami.auth.domain.repository.AuthRepository

internal class LogoutUseCase(private val authRepository: AuthRepository) : Logout {
    override suspend fun invoke() {
        authRepository.logout()
    }
}
