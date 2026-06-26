package chat.nami.accessibility

import androidx.activity.ComponentActivity
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import chat.nami.accessibility.demos.CustomActionBad
import chat.nami.accessibility.demos.CustomActionGood
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// The CustomActions key is private inside SemanticsActions, so we locate it by name
// via SemanticsConfiguration's Iterable<Map.Entry<SemanticsPropertyKey<*>, Any?>> API.
private val hasCustomActions = SemanticsMatcher("has customActions") { node ->
    node.config.any { it.key.name == "CustomActions" }
}

@RunWith(AndroidJUnit4::class)
class CustomActionTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    // Bad: the swipeable row has no customActions — TalkBack cannot reach "delete".
    @Test
    fun bad_swipeRowHasNoCustomActions() {
        composeTestRule.setContent { CustomActionBad() }
        composeTestRule.onNodeWithText("Swipe me left to delete")
            .assert(
                SemanticsMatcher("has no customActions") { node ->
                    node.config.none { it.key.name == "CustomActions" }
                }
            )
    }

    // Good: a "Delete" custom action is surfaced so TalkBack users can reach it via the rotor.
    @Test
    fun good_swipeRowHasDeleteAction() {
        composeTestRule.setContent { CustomActionGood() }
        val node = composeTestRule.onNode(hasCustomActions)

        @Suppress("UNCHECKED_CAST")
        val actions = node.fetchSemanticsNode().config
            .first { it.key.name == "CustomActions" }
            .value as List<CustomAccessibilityAction>

        assertTrue(
            "Expected a 'Delete' custom action",
            actions.any { it.label == "Delete" }
        )
    }

    // Good: invoking the custom action removes the row (state transitions correctly).
    @Test
    fun good_deleteActionRemovesRow() {
        composeTestRule.setContent { CustomActionGood() }
        val node = composeTestRule.onNode(hasCustomActions)

        @Suppress("UNCHECKED_CAST")
        val actions = node.fetchSemanticsNode().config
            .first { it.key.name == "CustomActions" }
            .value as List<CustomAccessibilityAction>

        actions.first { it.label == "Delete" }.action?.invoke()

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Swipe me left to delete").assertDoesNotExist()
        composeTestRule.onNodeWithText("Deleted — swipe here to reset").assertExists()
    }
}
