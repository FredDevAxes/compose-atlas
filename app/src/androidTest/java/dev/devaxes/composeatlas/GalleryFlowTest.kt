package dev.devaxes.composeatlas

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import org.junit.Rule
import org.junit.Test

/** Shared navigation and one motion policy exercised through an observable finite journey. */
class GalleryFlowTest {
    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()

    private fun open(scene: String) {
        rule.onNodeWithTag("open-$scene").performScrollTo().performClick()
        rule.waitForIdle()
    }

    private fun setMotion(label: String) {
        rule.onNodeWithContentDescription("Motion settings").performClick()
        rule.onNodeWithText(label, substring = false).performScrollTo().performClick()
        selectedMotion(label)
        rule.onNodeWithTag("settings-back").performClick()
    }

    private fun selectedMotion(label: String) {
        rule.onNodeWithText(label, substring = false).assert(
            SemanticsMatcher.expectValue(SemanticsProperties.Selected, true),
        )
    }

    @Test
    fun everySceneCanExplainItsMechanismAndReturnToGallery() {
        listOf("music", "shop", "delivery", "energy", "ticket", "photo", "inbox", "agenda").forEach { scene ->
            open(scene)
            rule.onNodeWithTag("how-it-works").performScrollTo().performClick()
            rule.onNodeWithTag("explanation").assertExists()
            rule.onNodeWithText("In a real app").assertExists()
            rule.onNodeWithText("Source: ui/scenes/", substring = true).performScrollTo().assertIsDisplayed()
            rule.onNodeWithTag("how-it-works").performScrollTo().performClick()
            rule.onNodeWithTag("explanation").assertDoesNotExist()
            rule.onNodeWithTag("back-to-explore").performClick()
            rule.onNodeWithTag("open-$scene").assertExists()
        }
    }

    @Test
    fun deliveryFollowsControlledTimeAndCompletes() {
        open("delivery")
        rule.onNodeWithTag("send-parcel").performScrollTo()
        rule.mainClock.autoAdvance = false
        rule.onNodeWithTag("send-parcel").performClick()
        rule.mainClock.advanceTimeBy(32)
        rule.onNodeWithTag("send-parcel").assertIsNotEnabled()
        rule.onNodeWithContentDescription("Delivery status: Preparing").assertExists()
        rule.mainClock.advanceTimeBy(3200)
        rule.onNodeWithContentDescription("Delivery status: On the way").assertExists()
        rule.mainClock.advanceTimeBy(4000)
        rule.onNodeWithContentDescription("Delivery status: At your door").assertExists()
        rule.onNodeWithTag("send-parcel").assertIsEnabled()
        rule.onNodeWithText("Replay delivery").assertExists()
        rule.mainClock.autoAdvance = true
    }

    @Test
    fun globalSlowAndOffRetainAcrossRecreationAndApplyToScenes() {
        setMotion("Slow motion")
        rule.activityRule.scenario.recreate()
        rule.onNodeWithContentDescription("Motion settings").performClick()
        selectedMotion("Slow motion")
        rule.onNodeWithTag("settings-back").performClick()
        open("delivery")
        rule.onNodeWithTag("send-parcel").performScrollTo()
        rule.mainClock.autoAdvance = false
        rule.onNodeWithTag("send-parcel").performClick()
        rule.mainClock.advanceTimeBy(7000)
        rule.onNodeWithContentDescription("Delivery status: On the way").assertExists()
        rule.onNodeWithTag("send-parcel").assertIsNotEnabled()
        rule.mainClock.autoAdvance = true
        setMotion("Off")
        rule.onNodeWithContentDescription("Delivery status: At your door").assertExists()
        rule.onNodeWithTag("send-parcel").performScrollTo().performClick()
        rule.onNodeWithContentDescription("Delivery status: At your door").assertExists()
        rule.onNodeWithTag("send-parcel").assertIsEnabled()
        rule.activityRule.scenario.recreate()
        rule.onNodeWithContentDescription("Motion settings").performClick()
        selectedMotion("Off")
        rule.onNodeWithTag("settings-back").performClick()
        rule.onNodeWithTag("back-to-explore").performClick()
        open("photo")
        rule.mainClock.autoAdvance = false
        rule.onNodeWithText("Rotate", substring = false).performScrollTo().performClick()
        rule.mainClock.advanceTimeByFrame()
        rule
            .onNodeWithTag(
                "poster-canvas",
            ).assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Zoom 100 percent. Rotation 15 degrees."))
        rule.mainClock.autoAdvance = true
    }
}
