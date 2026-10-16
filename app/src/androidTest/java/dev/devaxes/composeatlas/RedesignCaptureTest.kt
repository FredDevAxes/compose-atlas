package dev.devaxes.composeatlas

import android.graphics.Bitmap
import android.os.SystemClock
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import java.io.File

/** Native evidence only: run separately in each desired theme, font scale and window configuration. */
class RedesignCaptureTest {
    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()

    private fun open(scene: String) = rule.onNodeWithTag("open-$scene").performScrollTo().performClick()

    private fun click(tag: String) = rule.onNodeWithTag(tag).performScrollTo().performClick()

    private fun back() = rule.onNodeWithTag("back-to-explore").performClick()

    private fun capture(name: String) {
        rule.waitForIdle()
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val label = InstrumentationRegistry.getArguments().getString("evidenceLabel", "tour")
        val directory = File(instrumentation.targetContext.getExternalFilesDir(null), "evidence/$label")
        check(directory.mkdirs() || directory.isDirectory)
        instrumentation.waitForIdleSync()
        // One bounded compositor wait, only for evidence pixels after Compose has settled.
        SystemClock.sleep(250)
        val screenshot = checkNotNull(instrumentation.uiAutomation.takeScreenshot())
        File(directory, "$name.png").outputStream().use { screenshot.compress(Bitmap.CompressFormat.PNG, 100, it) }
        screenshot.recycle()
    }

    @Test
    fun captureRedesignTour() {
        capture("00-home")
        open("music")
        capture("01-music")
        click("music-fold")
        rule.onNodeWithContentDescription("Next track").performScrollTo().performClick()
        capture("02-music-folded-next-track")
        back()
        open("shop")
        capture("03-shop")
        click("shop-object-0")
        rule.onNodeWithContentDescription("Increase quantity").performScrollTo().performClick()
        click("shop-add")
        capture("04-shop-bag")
        back()
        open("delivery")
        capture("05-delivery")
        rule.onNodeWithTag("send-parcel").performScrollTo()
        rule.mainClock.autoAdvance = false
        rule.onNodeWithTag("send-parcel").performClick()
        rule.mainClock.advanceTimeBy(7200)
        rule.mainClock.autoAdvance = true
        capture("06-delivery-arrived")
        back()
        open("energy")
        capture("07-energy")
        repeat(3) { click("energy-earlier") }
        rule.onNodeWithTag("energy-chart").performScrollTo()
        capture("08-energy-missing-reading")
        back()
        open("ticket")
        capture("09-ticket")
        rule.onNodeWithText("Use example details").performScrollTo().performClick()
        click("make-ticket")
        capture("10-ticket-ready")
        back()
        open("photo")
        capture("11-poster")
        rule.onNodeWithText("Zoom +").performScrollTo().performClick()
        rule.onNodeWithText("Rotate", substring = false).performClick()
        rule.onNodeWithTag("poster-canvas").performScrollTo()
        capture("12-poster-transformed")
        back()
        open("inbox")
        capture("13-inbox")
        click("inbox-archive-1")
        capture("14-inbox-undo")
        rule.onNodeWithText("Undo").performClick()
        capture("15-inbox-restored")
        back()
        open("agenda")
        capture("16-agenda")
        click("agenda-move")
        capture("17-agenda-moved")
        rule.onNodeWithTag("how-it-works").performScrollTo().performClick()
        rule.onNodeWithText("In a real app").performScrollTo()
        capture("18-explanation")
        rule.onNodeWithContentDescription("Motion settings").performClick()
        capture("19-global-motion")
    }
}
