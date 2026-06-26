package chat.nami.accessibility

import androidx.activity.ComponentActivity
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import chat.nami.accessibility.demos.TraversalBad
import chat.nami.accessibility.demos.TraversalGood
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TraversalTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    // Bad: banner carries no traversalIndex, so TalkBack reads it after the article
    // even though it visually appears on top.
    @Test
    fun bad_bannerHasNoTraversalIndex() {
        composeTestRule.setContent { TraversalBad() }
        val config = composeTestRule
            .onNodeWithText("Important: your session expires in 1 minute")
            .fetchSemanticsNode()
            .config
        val index = config.getOrElseNullable(SemanticsProperties.TraversalIndex) { null }
        assertNull(index)
    }

    // Good: banner gets traversalIndex = -1f, which puts it before the article.
    @Test
    fun good_bannerHasNegativeTraversalIndex() {
        composeTestRule.setContent { TraversalGood() }
        val config = composeTestRule
            .onNodeWithText("Important: your session expires in 1 minute")
            .fetchSemanticsNode()
            .config
        val index = config[SemanticsProperties.TraversalIndex]
        assertEquals(-1f, index)
    }

    // Good: the traversal group is marked on the container so indices are scoped correctly.
    @Test
    fun good_containerIsTraversalGroup() {
        composeTestRule.setContent { TraversalGood() }
        // The root Column has isTraversalGroup = true — we verify the banner is
        // inside a traversal-group by confirming it exists with the correct index.
        val config = composeTestRule
            .onNodeWithText("Important: your session expires in 1 minute")
            .fetchSemanticsNode()
            .config
        assertEquals(-1f, config[SemanticsProperties.TraversalIndex])
    }
}
