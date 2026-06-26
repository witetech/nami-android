package chat.nami.accessibility

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToLog
import androidx.test.ext.junit.runners.AndroidJUnit4
import chat.nami.accessibility.demos.ContentDescriptionBad
import chat.nami.accessibility.demos.ContentDescriptionGood
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ContentDescriptionTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    // Bad: icon buttons have null contentDescription — TalkBack has nothing to announce.
    @Test
    fun bad_sendButtonHasNoLabel() {
        composeTestRule.setContent { ContentDescriptionBad() }
        composeTestRule.onNodeWithContentDescription("Send message").assertDoesNotExist()
    }

    @Test
    fun bad_favoriteButtonHasNoLabel() {
        composeTestRule.setContent { ContentDescriptionBad() }
        composeTestRule.onNodeWithContentDescription("Add to favorites").assertDoesNotExist()
    }

    // Bad: the decorative star is labelled "star icon", which TalkBack reads as noise.
    @Test
    fun bad_decorativeImageHasNoisyLabel() {
        composeTestRule.setContent { ContentDescriptionBad() }
        composeTestRule.onNodeWithContentDescription("star icon").assertExists()
    }

    // Good: action icons carry meaningful labels.
    @Test
    fun good_sendButtonHasMeaningfulLabel() {
        composeTestRule.setContent { ContentDescriptionGood() }
        composeTestRule.onNodeWithContentDescription("Send message").assertHasClickAction()
    }

    @Test
    fun good_favoriteButtonHasMeaningfulLabel() {
        composeTestRule.setContent { ContentDescriptionGood() }
        composeTestRule.onNodeWithContentDescription("Add to favorites").assertHasClickAction()
    }

    // Good: decorative image is removed from the semantics tree entirely.
    @Test
    fun good_decorativeImageHasNoSemantics() {
        composeTestRule.setContent { ContentDescriptionGood() }
        composeTestRule.onNodeWithContentDescription("star icon").assertDoesNotExist()
        // The image has contentDescription = null so it produces no semantics node.
        val descriptions = composeTestRule.onNodeWithContentDescription(
            "star icon",
            useUnmergedTree = true
        )
        descriptions.assertDoesNotExist()
    }

    @Test
    fun dumpTree() {
        composeTestRule.setContent { ContentDescriptionGood() }

        composeTestRule.onRoot(useUnmergedTree = false)
            .printToLog("MERGED")

        composeTestRule.onRoot(useUnmergedTree = true)
            .printToLog("UNMERGED")
    }
}
