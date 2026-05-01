package chat.nami.auth.presentation.view

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import chat.nami.auth.R
import chat.nami.auth.presentation.viewmodel.LoginEvent
import chat.nami.auth.presentation.viewmodel.LoginState
import chat.nami.auth.presentation.viewmodel.LoginViewModel
import chat.nami.design.NamiTheme
import chat.nami.viewmodel.EventViewModel
import chat.nami.viewmodel.StateViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

private data class OnboardingPage(val titleRes: Int, val descriptionRes: Int)

private val pages = listOf(
    OnboardingPage(
        titleRes = R.string.onboarding_title_1,
        descriptionRes = R.string.onboarding_description_1
    ),
    OnboardingPage(
        titleRes = R.string.onboarding_title_2,
        descriptionRes = R.string.onboarding_description_2
    ),
    OnboardingPage(
        titleRes = R.string.onboarding_title_3,
        descriptionRes = R.string.onboarding_description_3
    )
)

@Composable
internal fun LoginScreen(viewModel: LoginViewModel, onLoggedIn: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val loginState by viewModel.state.collectAsState()
    val pagerState = rememberPagerState { pages.size }
    val snackbarHostState = remember { SnackbarHostState() }

    val errorMessage = stringResource(R.string.login_error)

    LaunchedEffect(key1 = Unit) {
        viewModel.viewEvent.onEach { viewEvent ->
            when (viewEvent) {
                LoginEvent.Success -> {
                    onLoggedIn()
                }

                LoginEvent.Error -> {
                    scope.launch { snackbarHostState.showSnackbar(errorMessage) }
                }
            }
        }.launchIn(this)
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            HorizontalPager(
                modifier = Modifier.weight(1f),
                state = pagerState
            ) { page ->
                val onboardingPage = pages[page]

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 32.dp, vertical = 48.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(
                        text = stringResource(id = onboardingPage.titleRes),
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(id = onboardingPage.descriptionRes),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                        fontWeight = FontWeight.Thin,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(pages.size) { index ->
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    MaterialTheme.colorScheme.onSurface.copy(
                                        alpha = if (pagerState.currentPage == index) {
                                            1f
                                        } else {
                                            0.25f
                                        }
                                    )
                                )
                        )
                    }
                }

                Button(
                    enabled = !loginState.loading,
                    onClick = { viewModel.loginWithGoogle(context) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    if (loginState.loading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            text = stringResource(id = R.string.onboarding_sign_in_google),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                val onSurfaceFaint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                Text(
                    text = buildAnnotatedString {
                        append(stringResource(id = R.string.onboarding_terms))
                        append(" ")
                        withLink(
                            LinkAnnotation.Url(
                                url = "https://nami.chat/terms-of-service",
                                styles = TextLinkStyles(
                                    style = SpanStyle(
                                        fontWeight = FontWeight.SemiBold,
                                        color = onSurfaceFaint
                                    )
                                )
                            )
                        ) {
                            append(stringResource(id = R.string.onboarding_terms_of_service))
                        }
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = onSurfaceFaint,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

private class TestLoginViewModel :
    LoginViewModel,
    StateViewModel<LoginState>,
    EventViewModel<LoginEvent> {
    override val state = MutableStateFlow(LoginState(loading = false))
    override val viewEvent = emptyFlow<LoginEvent>()

    override fun loginWithGoogle(activityContext: Context) = Unit
}

@Composable
@Preview
private fun LoginScreenPreview() {
    NamiTheme {
        LoginScreen(
            viewModel = TestLoginViewModel(),
            onLoggedIn = {}
        )
    }
}
