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
import kotlinx.serialization.Serializable

@Serializable
data object LoginRoute

@Composable
internal fun RootNavigation() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                userName = appModule.authModule.getUser()?.id ?: "",
                recents = mapOf(),
                onNewChatClick = {},
                onChatHistoryClick = {},
                onChatClick = {},
                onSettingsClick = {}
            )
        }
    ) {
        NavHost(navController, LoginRoute) {
            composable<LoginRoute> {
                appModule.authModule.LoginScreenDestination(onLoggedIn = {})
            }
        }
    }
}
