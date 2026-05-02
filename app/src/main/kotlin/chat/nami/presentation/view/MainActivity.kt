package chat.nami.presentation.view

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import chat.nami.design.NamiTheme
import chat.nami.di.appModule
import chat.nami.presentation.navigation.RootNavigation
import chat.nami.presentation.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewModel = appModule.mainViewModel
        mainViewModel.fetchUser()

        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean = if (!mainViewModel.state.value.loading) {
                    content.viewTreeObserver.removeOnPreDrawListener(this)
                    true
                } else {
                    false
                }
            }
        )

        enableEdgeToEdge()
        setContent {
            NamiTheme {
                val state by mainViewModel.state.collectAsState()
                if (!state.loading) {
                    RootNavigation(state.user)
                }
            }
        }
    }
}
