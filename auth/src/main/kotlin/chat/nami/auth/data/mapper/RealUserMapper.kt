package chat.nami.auth.data.mapper

import chat.nami.auth.data.model.UserDto
import chat.nami.auth.domain.model.User

internal class RealUserMapper : UserMapper {
    override fun mapToUser(uid: String, userDto: UserDto): User = User(
        id = uid,
        email = userDto.email!!,
        displayName = userDto.displayName!!
    )
}
