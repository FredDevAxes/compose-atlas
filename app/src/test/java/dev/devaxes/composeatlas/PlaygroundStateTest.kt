package dev.devaxes.composeatlas

import dev.devaxes.composeatlas.ui.AtlasMotion
import dev.devaxes.composeatlas.ui.MotionMode
import dev.devaxes.composeatlas.ui.scenes.energyIndex
import dev.devaxes.composeatlas.ui.scenes.todayEnergy
import dev.devaxes.composeatlas.ui.scenes.yesterdayEnergy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PlaygroundStateTest {
    @Test
    fun motionPolicyHasNormalSlowAndImmediateDurations() {
        assertEquals(400, AtlasMotion(MotionMode.Normal).duration())
        assertEquals(19200, AtlasMotion(MotionMode.Slow).duration(6400))
        assertEquals(0, AtlasMotion(MotionMode.Off).duration(6400))
        assertTrue(AtlasMotion(MotionMode.Slow).enabled)
        assertFalse(AtlasMotion(MotionMode.Off).enabled)
        listOf(MotionMode.Normal, MotionMode.Slow).forEach { mode ->
            val systemOff = AtlasMotion(mode, systemEnabled = false)
            assertEquals(0, systemOff.duration(6400))
            assertFalse(systemOff.enabled)
        }
    }

    @Test
    fun energySelectionUsesEveryBarCenterAndClampsOutsideChart() {
        (0..11).forEach { index -> assertEquals(index, energyIndex((index + .5f) / 12)) }
        assertEquals(0, energyIndex(-1f))
        assertEquals(0, energyIndex(0f))
        assertEquals(11, energyIndex(1f))
        assertEquals(11, energyIndex(2f))
    }

    @Test
    fun todayMissingReadingRemainsAbsentAndYesterdayIsAnIndependentFixture() {
        assertEquals(12, todayEnergy.size)
        assertEquals(12, yesterdayEnergy.size)
        assertNull(todayEnergy[6])
        assertEquals(.7f, yesterdayEnergy[6]!!, 0f)
        assertEquals(1, todayEnergy.count { it == null })
    }
}
