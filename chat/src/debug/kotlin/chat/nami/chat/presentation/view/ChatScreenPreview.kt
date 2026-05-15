package chat.nami.chat.presentation.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import chat.nami.chat.domain.model.Message
import chat.nami.chat.presentation.viewmodel.ChatState
import chat.nami.design.NamiTheme

@Preview(showBackground = true)
@Composable
private fun ContentPreview() {
    NamiTheme {
        Content(
            state = ChatState(
                loading = false,
                input = "",
                messages = listOf(
                    Message.User(
                        id = "1",
                        content = "Hello! Can you explain how Jetpack Compose works?"
                    ),
                    Message.Assistant(
                        id = "2",
                        content = "Jetpack Compose is Android's modern UI toolkit. " +
                            "It uses a declarative approach where you describe your UI as " +
                            "functions that transform data into UI elements.",
                        model = "Claude Sonnet 4.5"
                    ),
                    Message.User(
                        id = "3",
                        content = "That makes sense! What about state management?"
                    ),
                    Message.Assistant(
                        id = "4",
                        content =
                        "State in Compose is managed through `remember` and `mutableStateOf`. " +
                            "When state changes, Compose automatically recomposes the affected parts of the UI.",
                        model = "Claude Sonnet 4.5"
                    )
                )
            ),
            onInputChange = {},
            onSendClick = {}
        )
    }
}
