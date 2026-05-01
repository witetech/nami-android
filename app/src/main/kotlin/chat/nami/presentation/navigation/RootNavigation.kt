package chat.nami.presentation.navigation

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import chat.nami.di.appModule
import chat.nami.presentation.view.DrawerContent
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data object LoginRoute

@Serializable
data object ChatRoute

@Serializable
data object ChatHistoryRoute

@Composable
internal fun RootNavigation() {
    val scope = rememberCoroutineScope()

    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val snackbarHostState = remember { SnackbarHostState() }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            DrawerContent(
                userName = "asd",
                recents = mapOf(),
                onNewChatClick = {
                    scope.launch {
                        snackbarHostState.showSnackbar("Message")
                    }
                },
                onChatHistoryClick = {
                    scope.launch {
                        navController.navigate(ChatRoute)
                    }
                },
                onChatClick = {},
                onSettingsClick = {}
            )
        }
    ) {
        NavHost(navController, ChatHistoryRoute) {
            composable<LoginRoute> {
                appModule.authModule.LoginScreenDestination(onLoggedIn = {
                    navController.navigate(ChatRoute)
                })
            }

            composable<ChatRoute> {
                appModule.chatModule.ChatScreenDestination()
            }

            composable<ChatHistoryRoute> {
                appModule.chatHistoryModule.ChatHistoryScreenDestination()
            }
        }
    }
}
