package chat.nami.auth.domain.usecase

import chat.nami.auth.domain.repository.AuthRepository

class GetUserUseCase(private val authRepository: AuthRepository) : GetUser {
    override fun invoke() = authRepository.getUser()
}
