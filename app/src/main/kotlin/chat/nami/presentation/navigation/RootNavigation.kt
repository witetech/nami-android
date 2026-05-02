package chat.nami.presentation.navigation

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
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

@Serializable
data object SettingsRoute

@Composable
internal fun RootNavigation() {
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            DrawerContent(
                userName = "asd",
                recents = mapOf(),
                onNewChatClick = {
                    scope.launch {
                        drawerState.close()
                        navController.navigate(ChatRoute) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    }
                },
                onChatHistoryClick = {
                    scope.launch {
                        drawerState.close()
                        navController.navigate(ChatHistoryRoute) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    }
                },
                onChatClick = {
                    scope.launch {
                        drawerState.close()
                        navController.navigate(ChatRoute) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    }
                },
                onSettingsClick = {
                    scope.launch {
                        drawerState.close()
                        navController.navigate(SettingsRoute) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    }
                }
            )
        }
    ) {
        NavHost(navController, LoginRoute) {
            composable<LoginRoute> {
                appModule.authModule.LoginScreenDestination(onLoggedIn = {
                    navController.navigate(ChatRoute) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                })
            }

            composable<ChatRoute> {
                appModule.chatModule.ChatScreenDestination()
            }

            composable<ChatHistoryRoute> {
                appModule.chatHistoryModule.ChatHistoryScreenDestination()
            }

            composable<SettingsRoute> {
                appModule.settingsModule.SettingsScreenDestination()
            }
        }
    }
}
