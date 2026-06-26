package chat.nami.accessibility

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import chat.nami.accessibility.demos.MergingBad
import chat.nami.accessibility.demos.MergingCleared
import chat.nami.accessibility.demos.MergingGood
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MergingTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    // Bad: each child is its own TalkBack stop — avatar, name, subtitle, and badge
    // are all independent focusable nodes.
    @Test
    fun bad_contactRowChildrenAreIndependentNodes() {
        composeTestRule.setContent { MergingBad() }
        composeTestRule.onNodeWithText("Ada Lovelace").assertExists()
        composeTestRule.onNodeWithText("Last seen 2 minutes ago").assertExists()
        composeTestRule.onNodeWithText("3").assertExists()
        // Confirm none of these leaf nodes also contain the other text
        // (they would if they were merged — they are not in the Bad variant).
        composeTestRule.onNode(
            hasText("Ada Lovelace") and hasText("Last seen 2 minutes ago")
        ).assertDoesNotExist()
    }

    // Good: mergeDescendants collapses all children into a single focusable node.
    @Test
    fun good_contactRowMergedIntoSingleNode() {
        composeTestRule.setContent { MergingGood() }
        // A single node should now contain all the text from its children.
        composeTestRule.onNode(
            hasText("Ada Lovelace") and hasText("Last seen 2 minutes ago") and hasText("3")
        ).assertExists()
    }

    // Cleared: clearAndSetSemantics replaces all children with one explicit label.
    @Test
    fun cleared_contactRowHasSingleCleanLabel() {
        composeTestRule.setContent { MergingCleared() }
        composeTestRule.onNodeWithContentDescription(
            "Ada Lovelace, last seen 2 minutes ago, 3 unread messages"
        ).assertExists()
        // Raw child text nodes are hidden from the accessibility tree.
        composeTestRule.onNodeWithText("Ada Lovelace").assertDoesNotExist()
    }
}
