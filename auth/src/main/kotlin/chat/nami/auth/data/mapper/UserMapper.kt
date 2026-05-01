package chat.nami.auth.data.mapper

import chat.nami.auth.data.model.UserDto
import chat.nami.auth.domain.model.User

internal interface UserMapper {
    fun mapToUser(uid: String, userDto: UserDto): User
}
