package chat.nami

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import chat.nami.design.NamiTheme
import chat.nami.presentation.navigation.RootNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            NamiTheme {
                RootNavigation()
            }
        }
    }
}
