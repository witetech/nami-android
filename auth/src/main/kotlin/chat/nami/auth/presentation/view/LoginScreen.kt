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
import androidx.compose.ui.res.stringArrayResource
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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Composable
internal fun LoginScreen(viewModel: LoginViewModel, onLoggedIn: () -> Unit) {
    val snackbarHostState = remember { SnackbarHostState() }
    val state by viewModel.state.collectAsState()

    Event(
        viewModel = viewModel,
        snackbarHostState = snackbarHostState,
        onLoggedIn = onLoggedIn
    )

    Content(
        state = state,
        snackbarHostState = snackbarHostState,
        onLoginClick = viewModel::loginWithGoogle
    )
}

@Composable
private fun Content(
    state: LoginState,
    snackbarHostState: SnackbarHostState,
    onLoginClick: (Context) -> Unit
) {
    val context = LocalContext.current
    val pagerState = rememberPagerState { 3 }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(Modifier.padding(padding)) {
            HorizontalPager(modifier = Modifier.weight(1f), state = pagerState) { page ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 32.dp, vertical = 48.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(
                        text = stringArrayResource(R.array.onboarding_titles)[page],
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringArrayResource(R.array.onboarding_descriptions)[page],
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
                    repeat(3) { index ->
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
                    enabled = !state.loading,
                    onClick = { onLoginClick(context) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    if (state.loading) {
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
                                        fontWeight = FontWeight.Bold,
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

@Composable
private fun Event(
    viewModel: LoginViewModel,
    snackbarHostState: SnackbarHostState,
    onLoggedIn: () -> Unit
) {
    val scope = rememberCoroutineScope()
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
}

@Composable
@Preview
private fun LoginScreenPreview() {
    NamiTheme {
        Content(
            state = LoginState(loading = false),
            snackbarHostState = SnackbarHostState(),
            onLoginClick = {}
        )
    }
}
