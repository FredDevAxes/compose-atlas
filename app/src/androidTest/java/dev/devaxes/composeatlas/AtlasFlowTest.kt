package dev.devaxes.composeatlas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.click
import androidx.compose.ui.test.doubleClick
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.pinch
import androidx.compose.ui.test.swipe
import androidx.compose.ui.test.swipeLeft
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** User paths for the objects that changed in the eight-scene playground. */
class AtlasFlowTest {
    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()

    private fun open(scene: String) = rule.onNodeWithTag("open-$scene").performScrollTo().performClick()

    private fun click(tag: String) = rule.onNodeWithTag(tag).performScrollTo().performClick()

    @Test
    fun musicTransportSeeksChangesTrackAndRestoresPaused() {
        open("music")
        rule.onNodeWithText("Sidewalk radio").assertExists()
        click("music-fold")
        rule.onNodeWithText("Expand").assertExists()
        rule.onNodeWithTag("music-seek").performSemanticsAction(SemanticsActions.SetProgress) { it(60f) }
        rule.onNodeWithText("1:00").assertExists()
        click("music-play")
        rule.onNodeWithText("Pause").assertExists()
        rule.activityRule.scenario.recreate()
        rule.onNodeWithText("Play").assertExists()
        rule.onNodeWithText("Expand").assertExists()
        assertTrue(
            rule
                .onNodeWithTag("music-seek")
                .fetchSemanticsNode()
                .config[SemanticsProperties.ProgressBarRangeInfo]
                .current >= 60f,
        )
        rule.onNodeWithContentDescription("Next track").performScrollTo().performClick()
        rule.onNodeWithText("After the rain").assertExists()
        rule.onNodeWithText("0:00").assertExists()
        rule.onNodeWithContentDescription("Previous track").performClick()
        rule.onNodeWithText("Sidewalk radio").assertExists()
    }

    @Test
    fun shopQuantityBagAndSharedElementBackSurviveRecreation() {
        open("shop")
        click("shop-object-0")
        rule.onNodeWithContentDescription("Decrease quantity").assertIsNotEnabled()
        rule.onNodeWithContentDescription("Increase quantity").performScrollTo().performClick()
        rule.onNodeWithText("Add · €178").assertExists()
        click("shop-add")
        rule.onNodeWithText("Bag (2)").assertExists()
        rule.activityRule.scenario.recreate()
        rule.onNodeWithText("Add · €178").assertExists()
        rule.onNodeWithText("2 objects in your bag · €178").assertExists()
        rule.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
        rule.onNodeWithTag("shop-object-0").assertExists()
        rule.onNodeWithTag("shop-object-1").assertExists()
        rule.onNodeWithText("Bag (2)").assertExists()
        click("shop-object-1")
        rule.onNodeWithText("Add · €65").assertExists()
        click("shop-back")
        rule.onNodeWithTag("shop-object-1").assertExists()
    }

    @Test
    fun ticketValidatesMovesImeFocusConfirmsAndRestores() {
        open("ticket")
        click("make-ticket")
        rule.onNodeWithText("Enter at least two characters.").assertExists()
        rule.onNodeWithText("Use an address such as alex@example.com.").assertExists()
        rule.onNodeWithTag("ticket-name").performScrollTo().performTextInput("Rae Chen")
        rule.onNodeWithTag("ticket-name").performImeAction()
        rule.onNodeWithTag("ticket-email").assertIsFocused()
        rule.onNodeWithTag("ticket-email").performTextInput("rae@example.com")
        rule.activityRule.scenario.recreate()
        rule.onNodeWithTag("ticket-name").assertTextContains("Rae Chen")
        rule
            .onNodeWithTag("ticket-email")
            .performScrollTo()
            .performClick()
            .performImeAction()
        rule.onNodeWithText("Your preview is ready.").assertExists()
        rule.onNodeWithTag("ticket-attendee", useUnmergedTree = true).assertTextEquals("Rae Chen")
        rule.activityRule.scenario.recreate()
        rule.onNodeWithText("Your preview is ready.").assertExists()
        click("edit-ticket")
        rule.onNodeWithTag("ticket-name").assertTextContains("Rae Chen")
        rule.onNodeWithTag("ticket-email").assertTextContains("rae@example.com")
    }

    @Test
    fun energyInspectionPreservesMissingReadingAcrossDaysAndRecreation() {
        open("energy")
        rule.onNodeWithTag("energy-value").assertTextEquals("2.8 kWh")
        repeat(3) { click("energy-earlier") }
        rule.onNodeWithTag("energy-time").assertTextEquals("12:00")
        rule.onNodeWithTag("energy-value").assertTextEquals("No reading")
        rule.onNodeWithContentDescription("Energy chart. 12:00: no reading. Use Earlier or Later to inspect.").assertExists()
        click("energy-day")
        rule.onNodeWithTag("energy-time").assertTextEquals("12:00")
        rule.onNodeWithTag("energy-value").assertTextEquals("0.7 kWh")
        click("energy-day")
        rule.onNodeWithTag("energy-value").assertTextEquals("No reading")
        rule.activityRule.scenario.recreate()
        rule.onNodeWithTag("energy-value").assertTextEquals("No reading")
        rule.onNodeWithTag("energy-chart").performScrollTo().performTouchInput { click(Offset(1f, height / 2f)) }
        rule.onNodeWithTag("energy-time").assertTextEquals("00:00")
        rule.onNodeWithTag("energy-earlier").assertIsNotEnabled()
        rule.onNodeWithTag("energy-chart").performTouchInput { swipe(Offset(width * .2f, height / 2f), Offset(width - 1f, height / 2f)) }
        rule.onNodeWithTag("energy-time").assertTextEquals("22:00")
        rule.onNodeWithTag("energy-later").assertIsNotEnabled()
    }

    @Test
    fun posterButtonsAndPinchHaveResetAndRestoration() {
        open("photo")
        rule.onNodeWithText("Zoom +").performScrollTo().performClick()
        rule.onNodeWithText("Rotate", substring = false).performClick()
        posterState("Zoom 125 percent. Rotation 15 degrees.")
        rule.activityRule.scenario.recreate()
        posterState("Zoom 125 percent. Rotation 15 degrees.")
        rule.onNodeWithText("Zoom −").performScrollTo().performClick()
        posterState("Zoom 100 percent. Rotation 15 degrees.")
        rule.onNodeWithTag("poster-canvas").performScrollTo().performTouchInput {
            pinch(
                Offset(width * .4f, height * .5f),
                Offset(width * .6f, height * .5f),
                Offset(width * .2f, height * .5f),
                Offset(width * .8f, height * .5f),
            )
        }
        val zoomed = rule.onNodeWithTag("poster-canvas").fetchSemanticsNode().config[SemanticsProperties.StateDescription]
        assertTrue(zoomed.substringAfter("Zoom ").substringBefore(" percent").toInt() > 100)
        rule.onNodeWithTag("poster-canvas").performTouchInput { doubleClick(center) }
        posterState("Zoom 100 percent. Rotation 0 degrees.")
        rule.onNodeWithText("Rotate", substring = false).performScrollTo().performClick()
        rule.onNodeWithText("Reset", substring = false).performScrollTo().performClick()
        posterState("Zoom 100 percent. Rotation 0 degrees.")
    }

    private fun posterState(expected: String) =
        rule.onNodeWithTag("poster-canvas").assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, expected))

    @Test
    fun inboxSwipeUndoRestoresSettledRowThenCanArchiveAgain() {
        open("inbox")
        val first =
            rule.onNode(
                SemanticsMatcher.keyIsDefined(SemanticsActions.CustomActions) and hasAnyDescendant(hasText("Rae Chen")),
            )
        first.performScrollTo().performTouchInput { swipeLeft() }
        rule.onNodeWithText("3 messages").assertExists()
        rule.onNodeWithText("Rae Chen").assertDoesNotExist()
        rule.onNodeWithText("Undo").performClick()
        rule.onNodeWithText("4 messages").assertExists()
        rule.onNodeWithText("Rae Chen").assertIsDisplayed()
        // Advancing beyond the dismissal animation catches a restored row re-archiving itself.
        rule.mainClock.advanceTimeBy(1200)
        rule.onNodeWithText("4 messages").assertExists()
        rule.onNodeWithText("Rae Chen").assertIsDisplayed()
        click("inbox-archive-1")
        rule.onNodeWithText("3 messages").assertExists()
        rule.activityRule.scenario.recreate()
        rule.onNodeWithText("Rae Chen").assertDoesNotExist()
        rule.onNodeWithText("Restore all messages").performScrollTo().performClick()
        rule.onNodeWithText("4 messages").assertExists()
    }

    @Test
    fun agendaMovesCoffeeAndRetainsTheChangedSchedule() {
        open("agenda")
        rule.onNodeWithTag("agenda-status").assertTextEquals("One overlap")
        click("agenda-move")
        rule.onNodeWithTag("agenda-status").assertTextEquals("Room for everyone")
        rule.onNodeWithText("11:00 – 11:45").assertExists()
        rule.activityRule.scenario.recreate()
        rule.onNodeWithTag("agenda-status").assertTextEquals("Room for everyone")
        rule.onNodeWithTag("back-to-explore").performClick()
        open("agenda")
        rule.onNodeWithTag("agenda-status").assertTextEquals("Room for everyone")
        click("agenda-move")
        rule.onNodeWithTag("agenda-status").assertTextEquals("One overlap")
        rule.onNodeWithText("09:30 – 10:15").assertExists()
    }
}
