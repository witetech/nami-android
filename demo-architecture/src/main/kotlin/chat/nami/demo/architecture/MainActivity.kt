package chat.nami.demo.architecture

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import chat.nami.demo.architecture.mvvm.View as MVVMView
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NamiTheme {
                // MVIView()
                MVVMView()
            }
        }
    }
}

data class User(val id: String, val name: String)

private val users = listOf(
    User("1", "Alice"),
    User("2", "Bob"),
    User("3", "Charlie"),
    User("4", "Diana"),
    User("5", "Eve")
)

suspend fun searchUsers(query: String): List<User> {
    delay(1_500)
    if (query == "error") error("Network error")
    return users.filter { it.name.contains(query, ignoreCase = true) }
}
