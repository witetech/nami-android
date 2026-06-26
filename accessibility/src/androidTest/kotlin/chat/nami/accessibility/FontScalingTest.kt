package chat.nami.accessibility

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import chat.nami.accessibility.demos.FontScalingBad
import chat.nami.accessibility.demos.FontScalingGood
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Font scaling is primarily a visual regression — clipping only appears at system
 * font scale ≥ 1.5x and cannot be detected via semantics. These tests confirm the
 * text node exists in both variants; the real verification is manual:
 *
 *   Settings ▸ Display ▸ Font size → Largest (or 200% on Android 14+)
 *   Bad:  "Confirm purchase" label is clipped inside the fixed-height Surface.
 *   Good: Surface grows to wrap the scaled text with no clipping.
 */
@RunWith(AndroidJUnit4::class)
class FontScalingTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun bad_confirmPurchaseTextExists() {
        composeTestRule.setContent { FontScalingBad() }
        composeTestRule.onNodeWithText("Confirm purchase").assertExists()
    }

    @Test
    fun good_confirmPurchaseTextExists() {
        composeTestRule.setContent { FontScalingGood() }
        composeTestRule.onNodeWithText("Confirm purchase").assertExists()
    }
}
