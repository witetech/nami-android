package chat.nami.auth.domain.repository

import android.content.Context
import chat.nami.auth.domain.model.User

interface AuthRepository {
    fun getUser(): User?
    suspend fun loginWithGoogle(activityContext: Context): User
    suspend fun logout()
}
