package chat.nami.chat.domain.model

internal sealed interface Message {
    val id: String

    data class User(override val id: String, val content: String) : Message

    data class Assistant(override val id: String, val content: String, val model: String) : Message
}
