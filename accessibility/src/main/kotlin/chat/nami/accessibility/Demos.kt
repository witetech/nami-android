package chat.nami.accessibility

import chat.nami.accessibility.demos.AnnouncementBad
import chat.nami.accessibility.demos.AnnouncementGood
import chat.nami.accessibility.demos.CollectionBad
import chat.nami.accessibility.demos.CollectionGood
import chat.nami.accessibility.demos.ContentDescriptionBad
import chat.nami.accessibility.demos.ContentDescriptionGood
import chat.nami.accessibility.demos.CustomActionBad
import chat.nami.accessibility.demos.CustomActionGood
import chat.nami.accessibility.demos.FontScalingBad
import chat.nami.accessibility.demos.FontScalingGood
import chat.nami.accessibility.demos.FormValidationBad
import chat.nami.accessibility.demos.FormValidationGood
import chat.nami.accessibility.demos.MergingBad
import chat.nami.accessibility.demos.MergingGood
import chat.nami.accessibility.demos.ProgressBad
import chat.nami.accessibility.demos.ProgressGood
import chat.nami.accessibility.demos.StateToggleBad
import chat.nami.accessibility.demos.StateToggleGood
import chat.nami.accessibility.demos.TouchTargetBad
import chat.nami.accessibility.demos.TouchTargetGood
import chat.nami.accessibility.demos.TraversalBad
import chat.nami.accessibility.demos.TraversalGood

/**
 * The catalog. Each entry maps a talk section to a runnable Before/After screen.
 * Add a topic by writing a `*Bad` / `*Good` pair in [chat.nami.accessibility.demos]
 * and appending it here — navigation and the home list pick it up automatically.
 */
val accessibilityDemos: List<Demo> = listOf(
    Demo(
        route = "content-description",
        title = "Content description & roles",
        summary = "Icon-only buttons need a label; decorative images should be silenced. " +
            "Listen for \"Send message, button\" vs silence.",
        bad = { ContentDescriptionBad() },
        good = { ContentDescriptionGood() }
    ),
    Demo(
        route = "merging",
        title = "Merging & hiding nodes",
        summary = "A contact row is one unit, not four separate TalkBack stops. " +
            "Listen for one stop vs four swipes.",
        bad = { MergingBad() },
        good = { MergingGood() }
    ),
    Demo(
        route = "state-toggle",
        title = "State & toggles",
        summary = "A color-only custom toggle tells TalkBack nothing about role or state. " +
            "Listen for \"switch, On\" vs nothing.",
        bad = { StateToggleBad() },
        good = { StateToggleGood() }
    ),
    Demo(
        route = "progress",
        title = "Sliders & progress",
        summary = "A custom-drawn volume bar is just \"button\" to TalkBack: " +
            "no value, no way to change it. " +
            "Listen for \"60%\" and adjust with the volume keys vs silence.",
        bad = { ProgressBad() },
        good = { ProgressGood() }
    ),
    Demo(
        route = "traversal",
        title = "Traversal & focus order",
        summary = "An overlay banner painted on top is read last unless you reorder traversal. " +
            "Listen for the alert read first vs last.",
        bad = { TraversalBad() },
        good = { TraversalGood() }
    ),
    Demo(
        route = "collection",
        title = "Lists & collections",
        summary = "A plain Column used as a list announces no position; " +
            "collectionInfo restores it (LazyColumn gives it free). " +
            "Listen for \"Inbox, 12, 2 of 5\" vs just \"Inbox, 12\".",
        bad = { CollectionBad() },
        good = { CollectionGood() }
    ),
    Demo(
        route = "announcement",
        title = "Announcements (liveRegion)",
        summary = "Async status changes are silent unless the text is a live region. " +
            "Listen for \"Loading… / Updated\" spoken vs silence.",
        bad = { AnnouncementBad() },
        good = { AnnouncementGood() }
    ),
    Demo(
        route = "form-validation",
        title = "Forms & validation",
        summary = "A field flagged only by a red border is silent; " +
            "error() + a liveRegion announce it on submit. " +
            "Listen for the error spoken on Submit vs nothing.",
        bad = { FormValidationBad() },
        good = { FormValidationGood() }
    ),
    Demo(
        route = "font-scaling",
        title = "Font scaling",
        summary = "Set system font to 200%: fixed-height layouts clip, fluid layouts grow. " +
            "Watch the label clip vs the button grow.",
        bad = { FontScalingBad() },
        good = { FontScalingGood() }
    ),
    Demo(
        route = "custom-action",
        title = "Custom actions for gestures",
        summary = "Swipe-to-delete is invisible to TalkBack; expose it as a custom action. " +
            "Listen for \"Delete\" in the actions menu.",
        bad = { CustomActionBad() },
        good = { CustomActionGood() }
    ),
    Demo(
        route = "touch-target",
        title = "Touch targets",
        summary = "Keep the icon small but guarantee a 48dp interactive area. " +
            "Compare hit areas with Accessibility Scanner.",
        bad = { TouchTargetBad() },
        good = { TouchTargetGood() }
    )
)
