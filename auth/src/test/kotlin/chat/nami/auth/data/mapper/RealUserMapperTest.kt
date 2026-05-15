package chat.nami.auth.data.mapper

import chat.nami.auth.data.model.UserDto
import chat.nami.auth.domain.model.User
import junit.framework.TestCase.assertEquals
import org.junit.Test

class RealUserMapperTest {

    private val sut = RealUserMapper()

    @Test
    fun `EXPECT mapped user`() {
        val result = sut.mapToUser(UID, ITEM)

        assertEquals(result, MAPPED)
    }

    private companion object {
        const val UID = "uid"
        const val EMAIL = "email"
        const val EMAIL_VERIFIED = true
        const val DISPLAY_NAME = "display_name"
        const val PHOTO_URL = "photo_url"
        const val PHONE_NUMBER = "phone_number"
        const val DISABLED = "disabled"
        const val CREATION_TIME = "creation_time"

        val ITEM = UserDto(
            email = EMAIL,
            emailVerified = EMAIL_VERIFIED,
            displayName = DISPLAY_NAME,
            photoURL = PHOTO_URL,
            phoneNumber = PHONE_NUMBER,
            disabled = DISABLED,
            creationTime = CREATION_TIME
        )

        val MAPPED = User(
            id = UID,
            email = EMAIL,
            displayName = DISPLAY_NAME
        )
    }
}
