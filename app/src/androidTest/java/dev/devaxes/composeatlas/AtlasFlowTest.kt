package dev.devaxes.composeatlas

import android.graphics.Bitmap
import android.os.SystemClock
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.File

class AtlasFlowTest {
    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()

    private fun open() {
        rule.onNodeWithTag("open-experiment").performScrollTo().performClick()
        rule.waitForIdle()
    }

    private fun capture(name: String) {
        rule.waitForIdle()
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val directory = File(instrumentation.targetContext.getExternalFilesDir(null), "evidence")
        directory.mkdirs()
        // Let the emulator compositor present the settled Compose frame.
        instrumentation.waitForIdleSync()
        SystemClock.sleep(500)
        val screenshot = instrumentation.uiAutomation.takeScreenshot()
        File(directory, name + ".png").outputStream().use { screenshot.compress(Bitmap.CompressFormat.PNG, 100, it) }
        screenshot.recycle()
    }

    @Test
    fun exploreAdjustUnderstandAndRestoreSelection() {
        capture("phone-home")
        open()
        capture("phone-controls")
        rule.onNodeWithText("Understand").performScrollTo().performClick()
        capture("phone-understand")
        rule.onNodeWithText("From touch to value").performScrollTo()
        capture("phone-explanation")
        rule.onNodeWithTag("next-point").performScrollTo().performClick()
        rule.onNodeWithText("15:00").assertExists()
        rule.activityRule.scenario.recreate()
        rule.onNodeWithText("15:00").assertExists()
        rule.onNodeWithText("Understand").assertExists()
        rule.onNodeWithTag("back-to-explore").performClick()
        rule.onNodeWithTag("open-experiment").assertExists()
    }

    @Test
    fun emptyAndGapsCanBeInspectedAndReset() {
        open()
        rule.onNodeWithText("Understand").performScrollTo().performClick()
        rule.onNodeWithTag("scenario-empty").performScrollTo().performClick()
        rule.onNodeWithText("No samples to display").assertExists()
        rule.onNodeWithTag("scenario-empty").assertIsSelected()
        capture("phone-empty")
        rule.onNodeWithTag("scenario-gaps").performScrollTo().performClick()
        capture("phone-gaps")
        rule.onNodeWithText("Adjust").performScrollTo().performClick()
        rule.onNodeWithTag("reset").performScrollTo().performClick()
        rule.onNodeWithText("14:00").assertExists()
        rule.onNodeWithText("Studio 21.4 °C").assertExists()
    }

    @Test
    fun lastVisibleSeriesCannotBeHidden() {
        open()
        rule.onNodeWithTag("series-studio").performScrollTo().performClick()
        rule.onNodeWithTag("series-gallery").assertIsNotEnabled()
        rule.onNodeWithTag("series-studio").assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Hidden"))
        rule.onNodeWithTag("series-gallery").assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Shown"))
        rule.onNodeWithText("Studio 21.4 °C").assertDoesNotExist()
        rule.onNodeWithTag("series-studio").performScrollTo().performClick()
        rule.onNodeWithText("Studio 21.4 °C").assertExists()
    }

    @Test
    fun dragClampsSelectionAndPeriodChangesUseDifferentFixtures() {
        open()
        rule.onNodeWithTag("temperature-chart").performTouchInput {
            swipe(Offset(width * 0.25f, height * 0.5f), Offset(width.toFloat(), height * 0.5f))
        }
        rule.onNodeWithText("24:00").assertExists()
        rule.onNodeWithText("Week").performClick()
        rule.onNodeWithText("Sun").assertExists()
        rule.onNodeWithText("Month").performClick()
        rule.onNodeWithText("Sep 30").assertExists()
    }

    @Test
    fun replayUsesTestClockAndMotionCanBeDisabled() {
        open()
        rule.onNodeWithTag("replay").performScrollTo()
        rule.mainClock.autoAdvance = false
        rule.onNodeWithTag("replay").performClick()
        rule.mainClock.advanceTimeBy(32)
        rule.onNodeWithText("Replaying trace…").assertExists()
        rule.mainClock.advanceTimeBy(1000)
        rule.onNodeWithText("14:00").assertExists()
        rule.mainClock.autoAdvance = true
        rule.onNodeWithTag("animate-switch").performScrollTo().performClick()
        rule.onNodeWithTag("replay").performScrollTo().performClick()
        rule.onNodeWithText("Replaying trace…").assertDoesNotExist()
        rule.onNodeWithText("14:00").assertExists()
        assertEquals(1f, position("tween"), 0.001f)
        assertEquals(1f, position("spring"), 0.001f)
    }

    private fun position(mode: String): Float =
        rule
            .onNodeWithTag("motion-" + mode)
            .fetchSemanticsNode()
            .config[SemanticsProperties.ProgressBarRangeInfo]
            .current

    @Test
    fun slowMotionShowsOvershootAndRestoresSpeed() {
        open()
        rule.onNode(hasText("Spring") and hasClickAction()).performScrollTo().performClick()
        rule.onNodeWithText("0.25×").performClick()
        rule.mainClock.autoAdvance = false
        rule.onNodeWithTag("compare-replay").performClick()
        rule.mainClock.advanceTimeBy(32)
        rule.mainClock.advanceTimeBy(960)
        assertTrue("Spring crosses its target", position("spring") > 1.1f)
        assertTrue("Quarter-speed Tween is still approaching", position("tween") in 0.1f..0.9f)
        rule.onNodeWithText("Replaying trace…").assertExists()
        capture("motion-quarter-overshoot")
        rule.mainClock.advanceTimeBy(5000)
        assertEquals(1f, position("tween"), 0.001f)
        assertEquals(1f, position("spring"), 0.001f)
        rule.onNodeWithText("14:00").assertExists()
        rule.mainClock.autoAdvance = true
        rule.activityRule.scenario.recreate()
        rule.onNodeWithText("0.25×").assertIsSelected()
        rule.onNodeWithTag("reset").performScrollTo().performClick()
        rule.onNodeWithText("1×").assertIsSelected()
        capture("motion-controls")
    }

    @Test
    fun normalAndQuarterSpeedFollowTheSameCurve() {
        open()
        rule.mainClock.autoAdvance = false
        rule.onNodeWithTag("compare-replay").performClick()
        rule.mainClock.advanceTimeBy(32)
        rule.mainClock.advanceTimeBy(160)
        val normalTween = position("tween")
        val normalSpring = position("spring")
        rule.mainClock.autoAdvance = true
        rule.onNodeWithText("0.25×").performClick()
        rule.mainClock.autoAdvance = false
        rule.onNodeWithTag("compare-replay").performClick()
        rule.mainClock.advanceTimeBy(32)
        rule.mainClock.advanceTimeBy(640)
        assertEquals(normalTween, position("tween"), 0.07f)
        assertEquals(normalSpring, position("spring"), 0.09f)
        rule.mainClock.autoAdvance = true
    }

    @Test
    fun longLabelsAndSystemBackRemainUsable() {
        open()
        rule.onNodeWithText("Understand").performScrollTo().performClick()
        rule.onNodeWithTag("scenario-long_labels").performScrollTo().performClick()
        capture("phone-long-labels")
        rule.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
        rule.waitForIdle()
        rule.onNodeWithTag("close-panel").assertDoesNotExist()
        rule.onNodeWithText("Experiment controls").performClick()
        rule.onNodeWithTag("close-panel").assertExists()
        rule.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
        rule.waitForIdle()
        rule.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
        rule.waitForIdle()
        rule.onNodeWithTag("open-experiment").assertExists()
    }
}
