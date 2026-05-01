package chat.nami.auth.domain.usecase

import android.content.Context
import chat.nami.auth.domain.model.User

internal fun interface GetUser {
    suspend operator fun invoke(): User?
}

internal fun interface LoginWithGoogle {
    suspend operator fun invoke(activityContext: Context): User
}

internal fun interface Logout {
    suspend operator fun invoke()
}
