package chat.nami.auth.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val email: String? = null,
    val emailVerified: Boolean? = null,
    val displayName: String? = null,
    val photoURL: String? = null,
    val phoneNumber: String? = null,
    val disabled: String? = null,
    val creationTime: String? = null,
)
