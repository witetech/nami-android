package chat.nami.accessibility

import androidx.activity.ComponentActivity
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import chat.nami.accessibility.demos.AnnouncementBad
import chat.nami.accessibility.demos.AnnouncementGood
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AnnouncementTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    // Bad: status text has no liveRegion — TalkBack stays silent when it updates.
    @Test
    fun bad_statusTextHasNoLiveRegion() {
        composeTestRule.setContent { AnnouncementBad() }
        composeTestRule.onNodeWithText("Idle")
            .assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.LiveRegion))
    }

    // Good: status text is marked Polite so TalkBack announces changes automatically.
    @Test
    fun good_statusTextHasPoliteRegion() {
        composeTestRule.setContent { AnnouncementGood() }
        val liveRegion = composeTestRule
            .onNodeWithText("Idle")
            .fetchSemanticsNode()
            .config[SemanticsProperties.LiveRegion]

        assertEquals(LiveRegionMode.Polite, liveRegion)
    }
}
