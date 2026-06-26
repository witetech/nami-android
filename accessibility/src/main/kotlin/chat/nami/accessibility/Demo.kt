package chat.nami.accessibility

import androidx.compose.runtime.Composable

/**
 * One catalog entry = one talk topic, shown as a "Before / After" pair.
 *
 * Each demo renders the *same* UI twice so you can swipe TalkBack across both on
 * stage: [bad] reproduces the accessibility bug, [good] is the fix. [title]
 * carries the slide section, and [summary] both describes the topic and tells
 * the audience what to listen for.
 */
data class Demo(
    val route: String,
    val title: String,
    val summary: String,
    val bad: @Composable () -> Unit,
    val good: @Composable () -> Unit
)
