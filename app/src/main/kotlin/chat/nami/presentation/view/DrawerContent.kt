package chat.nami.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowRight
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import chat.nami.R
import chat.nami.design.NamiTheme

@Composable
fun DrawerContent(
    userName: String,
    recents: Map<String, String>,
    modifier: Modifier = Modifier,
    onNewChatClick: () -> Unit = {},
    onChatHistoryClick: () -> Unit = {},
    onChatClick: (String) -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    ModalDrawerSheet(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                stringResource(R.string.app_name),
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.headlineLarge
            )

            DrawerItem(
                text = stringResource(R.string.new_chat),
                icon = Icons.Outlined.ChatBubbleOutline,
                onClick = onNewChatClick
            )

            DrawerItem(
                text = stringResource(R.string.chat_history),
                icon = Icons.Outlined.Forum,
                onClick = onChatHistoryClick
            )

            Column(modifier = Modifier.weight(1f)) {
                if (recents.isNotEmpty()) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                    Text(
                        stringResource(R.string.recents),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.labelMedium
                    )

                    recents.entries.take(3).forEach { chat ->
                        DrawerItem(text = chat.value, onClick = {
                            onChatClick(chat.key)
                        })
                    }

                    DrawerItem(
                        text = stringResource(R.string.see_all),
                        badge = Icons.AutoMirrored.Outlined.ArrowRight,
                        onClick = onChatHistoryClick
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            SettingsDrawerItem(userName = userName, onClick = onSettingsClick)
        }
    }
}

@Composable
private fun DrawerItem(
    text: String,
    icon: ImageVector? = null,
    badge: ImageVector? = null,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        label = {
            Text(
                text = text,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        icon = icon?.let { { Icon(imageVector = it, contentDescription = text) } },
        badge = badge?.let { { Icon(it, contentDescription = text) } },
        selected = false,
        onClick = onClick
    )
}

@Composable
private fun SettingsDrawerItem(userName: String, onClick: () -> Unit) {
    NavigationDrawerItem(
        label = {
            Text(
                text = userName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        icon = {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    userName.firstOrNull()?.uppercaseChar()?.toString() ?: "W",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },
        badge = {
            Icon(
                Icons.Outlined.Settings,
                contentDescription = stringResource(R.string.settings)
            )
        },
        selected = false,
        onClick = onClick
    )
}

@Composable
@Preview
private fun DrawerContentPreview() {
    NamiTheme {
        DrawerContent(
            userName = "Ömer Karaca",
            recents = mapOf(
                "id-0" to "Android Jetpack Compose tips",
                "id-1" to "Fix navigation bug",
                "id-2" to "Material 3 drawer design",
                "id-3" to "Hello World!"
            )
        )
    }
}
