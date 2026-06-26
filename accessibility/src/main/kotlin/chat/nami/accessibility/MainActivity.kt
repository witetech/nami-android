package chat.nami.accessibility

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import chat.nami.design.NamiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NamiTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        HomeScreen(onOpen = { route -> navController.navigate(route) })
                    }

                    accessibilityDemos.forEach { demo ->
                        composable(demo.route) {
                            DemoScaffold(demo = demo, onBack = { navController.popBackStack() })
                        }
                    }
                }
            }
        }
    }
}
