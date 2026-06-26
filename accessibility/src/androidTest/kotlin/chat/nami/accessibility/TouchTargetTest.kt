package chat.nami.accessibility

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import chat.nami.accessibility.demos.TouchTargetBad
import chat.nami.accessibility.demos.TouchTargetGood
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// Note: enableAccessibilityChecks() + performClick() crashes on API 34+ due to
// InputManager.getInstance() removal. We check touch-target size directly via
// SemanticsNode.boundsInRoot instead — same assertion, no Espresso dependency.
@RunWith(AndroidJUnit4::class)
class TouchTargetTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    // Bad: clickable is constrained to the icon's 24dp visual size.
    @Test
    fun bad_dismissIconTouchTargetIsTooSmall() {
        composeTestRule.setContent { TouchTargetBad() }
        val bounds = composeTestRule
            .onNodeWithContentDescription("Dismiss")
            .fetchSemanticsNode()
            .boundsInRoot
        with(composeTestRule.density) {
            assertTrue(
                "Expected touch target < 48dp, got ${bounds.width.toDp()} × ${bounds.height.toDp()}",
                bounds.width.toDp() < 48.dp || bounds.height.toDp() < 48.dp
            )
        }
    }

    // Good: minimumInteractiveComponentSize lays the element out at ≥48dp in both axes.
    @Test
    fun good_dismissIconTouchTargetMeetsMinimum() {
        composeTestRule.setContent { TouchTargetGood() }
        val bounds = composeTestRule
            .onNodeWithContentDescription("Dismiss")
            .fetchSemanticsNode()
            .boundsInRoot
        with(composeTestRule.density) {
            assertTrue(
                "Expected touch target ≥48dp, got ${bounds.width.toDp()} × ${bounds.height.toDp()}",
                bounds.width.toDp() >= 48.dp && bounds.height.toDp() >= 48.dp
            )
        }
    }
}
