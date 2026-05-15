package chat.nami.chat.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import chat.nami.chat.R
import chat.nami.chat.domain.model.Message
import chat.nami.chat.presentation.viewmodel.ChatState
import chat.nami.chat.presentation.viewmodel.ChatViewModel

@Composable
internal fun ChatScreen(viewModel: ChatViewModel) {
    val state by viewModel.state.collectAsState()
    Content(
        state = state,
        onInputChange = viewModel::onInputChange,
        onSendClick = viewModel::sendMessage
    )
}

@Composable
internal fun Content(state: ChatState, onInputChange: (String) -> Unit, onSendClick: () -> Unit) {
    Scaffold(
        modifier = Modifier.imePadding(),
        bottomBar = {
            Input(
                text = state.input,
                onInputChange = onInputChange,
                onSendClick = onSendClick
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            reverseLayout = true,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(state.messages.reversed(), key = { it.id }) { message ->
                when (message) {
                    is Message.User -> UserMessage(message.content)
                    is Message.Assistant -> AssistantMessage(
                        content = message.content,
                        modelName = message.model
                    )
                }
            }
        }
    }
}

@Composable
private fun UserMessage(content: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction = 0.75f)
                .clip(
                    RoundedCornerShape(
                        topStart = 12.dp,
                        topEnd = 12.dp,
                        bottomStart = 12.dp,
                        bottomEnd = 0.dp
                    )
                )
                .background(MaterialTheme.colorScheme.onSurface)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = content,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.surface
            )
        }
    }
}

@Composable
private fun AssistantMessage(
    content: String,
    modelName: String,
    onCopy: () -> Unit = {},
    onShare: () -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        if (modelName.isNotEmpty()) {
            Text(
                text = modelName,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        Text(
            text = content,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Row(
            modifier = Modifier.padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconButton(
                onClick = onCopy,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.ContentCopy,
                    contentDescription = "Copy",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(
                onClick = onShare,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Share,
                    contentDescription = "Share",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun Input(text: String, onInputChange: (String) -> Unit, onSendClick: () -> Unit) {
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .navigationBarsPadding(),
        value = text,
        onValueChange = onInputChange,
        placeholder = { Text(stringResource(R.string.input_placeholder)) },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
        keyboardActions = KeyboardActions(onSend = { onSendClick() }),
        maxLines = 3,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        trailingIcon = {
            IconButton(
                onClick = onSendClick,
                enabled = text.isNotBlank()
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Send,
                    contentDescription = "Send"
                )
            }
        }
    )
}
