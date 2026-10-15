package dev.devaxes.composeatlas

import android.graphics.Bitmap
import android.os.SystemClock
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import java.io.File

class GalleryFlowTest {
    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()

    private fun open(route: String) {
        rule.onNodeWithTag("open-$route").performScrollTo().performClick()
    }

    private fun capture(name: String) {
        rule.waitForIdle()
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val directory = File(instrumentation.targetContext.getExternalFilesDir(null), "evidence")
        directory.mkdirs()
        instrumentation.waitForIdleSync()
        SystemClock.sleep(500)
        val bitmap = instrumentation.uiAutomation.takeScreenshot()
        File(directory, "$name.png").outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        bitmap.recycle()
    }

    @Test
    fun captureGalleryLayouts() {
        capture("layout-home")
        listOf("player", "collection", "form", "agenda").forEach { route ->
            open(route)
            capture("layout-$route")
            if (route == "agenda") {
                rule.onNodeWithTag("event-3").performScrollTo()
                capture("layout-agenda-timeline")
            }
            rule.onNodeWithText("Understand").performClick()
            rule
                .onNodeWithText(
                    when (route) {
                        "player" -> "A player without an audio engine"
                        "collection" -> "Identity survives movement"
                        "form" -> "State, then validation"
                        else -> "Time becomes geometry"
                    },
                ).assertIsDisplayed()
            capture("layout-$route-understand")
            rule.onNodeWithTag("back-to-explore").performClick()
        }
    }

    @Test
    fun playerSeeksStopsAtEndAndRestoresPausedState() {
        capture("gallery-home")
        open("player")
        capture("gallery-player")
        rule.onNodeWithTag("player-seek").performSemanticsAction(SemanticsActions.SetProgress) { it(95.9f) }
        rule.onNodeWithTag("player-toggle").performScrollTo().performClick()
        rule.waitUntil(4000) {
            rule
                .onAllNodes(
                    androidx.compose.ui.test
                        .hasText("Play again"),
                ).fetchSemanticsNodes()
                .isNotEmpty()
        }
        rule.onNodeWithTag("player-time").assertTextEquals("1:36 / 1:36")
        rule.onNodeWithTag("next-track").performScrollTo().performClick()
        rule.onNodeWithText("Glass & daylight").assertExists()
        rule.onNodeWithTag("player-time").assertTextEquals("0:00 / 2:04")
        rule.onNodeWithTag("player-expand").performScrollTo().performClick()
        rule.onNodeWithText("Expand player").assertExists()
        capture("gallery-player-compact")
        rule.activityRule.scenario.recreate()
        rule.onNodeWithText("Glass & daylight").assertExists()
        rule.onNodeWithText("Expand player").assertExists()
        rule.onNodeWithText("Play").assertExists()
        rule.onNodeWithText("Empty queue").performScrollTo().performClick()
        rule.onNodeWithText("The queue is quiet").assertExists()
        rule.onNodeWithText("Load demo tracks").performClick()
        rule.onNodeWithText("Understand").performClick()
        capture("gallery-player-understand")
        rule.onNodeWithText("Effects and restoration").performScrollTo().assertExists()
    }

    @Test
    fun playerGestureAndBackPreserveTrackButPausePlayback() {
        open("player")
        rule.onNodeWithTag("next-track").performScrollTo().performClick()
        rule.onNodeWithTag("player-gesture").performScrollTo().performTouchInput { swipeDown() }
        rule.onNodeWithText("Expand player").assertExists()
        rule.onNodeWithTag("player-toggle").performScrollTo().performClick()
        rule.onNodeWithText("Pause").assertExists()
        rule.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
        open("player")
        rule.onNodeWithText("Glass & daylight").assertExists()
        rule.onNodeWithText("Expand player").assertExists()
        rule.onNodeWithText("Play").assertExists()
    }

    @Test
    fun collectionKeepsFavoritesThroughReorderFilterAndLayout() {
        open("collection")
        capture("gallery-collection")
        rule.onNodeWithTag("object-grid").performScrollToNode(hasTestTag("save-1"))
        rule.onNodeWithTag("save-1").performClick()
        rule.onNodeWithTag("object-grid").performScrollToNode(hasTestTag("reverse-objects"))
        rule.onNodeWithTag("reverse-objects").performClick()
        rule.onNodeWithText("Saved only").performClick()
        rule.onNodeWithTag("object-count").assertTextEquals("1 object")
        rule.onNodeWithText("List").performClick()
        rule.onNodeWithTag("object-list").performScrollToNode(hasTestTag("save-1"))
        rule.onNodeWithText("Saved").assertExists()
        rule.onNodeWithTag("details-1").performClick()
        rule.onNodeWithText("Close details").performClick()
        rule.activityRule.scenario.recreate()
        rule.onNodeWithTag("object-list").performScrollToNode(hasTestTag("object-search"))
        rule.onNodeWithTag("object-search").performTextInput("no match")
        rule.onNodeWithTag("object-search").performImeAction()
        rule.onNodeWithTag("object-list").performScrollToNode(
            androidx.compose.ui.test
                .hasText("Clear filters"),
        )
        rule.onNodeWithText("No objects match").assertExists()
        rule.onNodeWithText("Clear filters").performClick()
        rule.onNodeWithTag("object-count").assertTextEquals("12 objects")
        rule.onNodeWithText("Understand").performClick()
        capture("gallery-collection-understand")
    }

    @Test
    fun formValidatesMovesFocusAndConfirmsLocally() {
        open("form")
        rule.onNodeWithTag("submit-session").performScrollTo().performClick()
        rule.onNodeWithText("Enter at least two characters.").assertExists()
        capture("gallery-form-errors")
        rule.onNodeWithContentDescription("Include notes preview").performScrollTo().performClick()
        rule.onNodeWithTag("session-name").performScrollTo().performTextInput("Alex")
        rule.onNodeWithTag("session-name").performImeAction()
        rule.onNodeWithTag("session-email").performTextInput("alex@example.com")
        rule.onNodeWithTag("session-email").performImeAction()
        rule.onNodeWithText("Session prepared").assertExists()
        rule.onNodeWithTag("confirmed-email").assertTextEquals("alex@example.com")
        rule.onNodeWithText("Notes preview").assertExists()
        capture("gallery-form-confirmation")
        rule.activityRule.scenario.recreate()
        rule.onNodeWithText("Session prepared").assertExists()
        rule.onNodeWithText("Edit session").performScrollTo().performClick()
        rule.onNodeWithTag("session-name").assertTextContains("Alex")
        rule.onNodeWithText("Understand").performClick()
        capture("gallery-form-understand")
    }

    @Test
    fun agendaMovesClampsAndRestoresEvents() {
        open("agenda")
        rule.onNodeWithTag("agenda-later").performScrollTo().performClick()
        rule.onNodeWithTag("agenda-time").assertTextEquals("Motion workshop: 9:15")
        rule.onNodeWithTag("agenda-overlap").performClick()
        rule.onNodeWithTag("agenda-zoom").performSemanticsAction(SemanticsActions.SetProgress) { it(160f) }
        rule.onNodeWithTag("agenda-scale").assertTextEquals("Timeline scale: 160 dp / hour")
        rule.onNodeWithTag("event-1").performScrollTo()
        capture("gallery-agenda")
        rule.onNodeWithTag("event-1").performClick()
        rule.onNodeWithText("Close event").performClick()
        rule.activityRule.scenario.recreate()
        rule.onNodeWithTag("agenda-time").assertTextEquals("Motion workshop: 9:15")
        rule.onNodeWithTag("agenda-overlap").assertIsSelected()
        rule.onNodeWithText("Reset agenda").performScrollTo().performClick()
        rule.onNodeWithTag("agenda-time").assertTextEquals("Motion workshop: 9:00")
        rule.onNodeWithText("Understand").performClick()
        capture("gallery-agenda-understand")
    }
}
