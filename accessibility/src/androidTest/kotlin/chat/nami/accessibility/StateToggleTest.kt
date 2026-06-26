package chat.nami.accessibility

import androidx.activity.ComponentActivity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import chat.nami.accessibility.demos.StateToggleBad
import chat.nami.accessibility.demos.StateToggleGood
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StateToggleTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    // Bad: plain clickable has no role — TalkBack only says "double tap to activate".
    @Test
    fun bad_pillHasNoRole() {
        composeTestRule.setContent { StateToggleBad() }
        composeTestRule.onNodeWithText("Notifications")
            .assertHasClickAction()
            .assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.Role))
    }

    // Bad: no stateDescription, so TalkBack never announces the current on/off value.
    @Test
    fun bad_pillHasNoStateDescription() {
        composeTestRule.setContent { StateToggleBad() }
        composeTestRule.onNodeWithText("Notifications")
            .assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.StateDescription))
    }

    // Good: toggleable sets Role.Switch so TalkBack announces the correct widget type.
    @Test
    fun good_pillHasSwitchRole() {
        composeTestRule.setContent { StateToggleGood() }
        composeTestRule.onNodeWithText("Notifications")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Switch))
    }

    // Good: stateDescription reflects current value and updates after toggle.
    @Test
    fun good_pillStateDescriptionUpdatesOnToggle() {
        composeTestRule.setContent { StateToggleGood() }
        val node = composeTestRule.onNodeWithText("Notifications")

        var stateDescription = node.fetchSemanticsNode()
            .config[SemanticsProperties.StateDescription]
        assertEquals("Off", stateDescription)

        node.performClick()

        stateDescription = node.fetchSemanticsNode()
            .config[SemanticsProperties.StateDescription]
        assertEquals("On", stateDescription)
    }
}
