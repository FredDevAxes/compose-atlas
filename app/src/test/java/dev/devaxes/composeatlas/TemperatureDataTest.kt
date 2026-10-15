package dev.devaxes.composeatlas

import dev.devaxes.composeatlas.data.ChartMath
import dev.devaxes.composeatlas.data.ExperimentState
import dev.devaxes.composeatlas.data.Period
import dev.devaxes.composeatlas.data.Scenario
import dev.devaxes.composeatlas.data.Series
import dev.devaxes.composeatlas.data.TemperatureData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TemperatureDataTest {
    @Test fun selectionMatchesTheSameSampleAndScale() {
        val state = ExperimentState()
        assertEquals("14:00", state.sample!!.label)
        assertEquals(21.4f, state.sample!!.studio!!, 0.001f)
        assertEquals(23.1f, state.sample!!.gallery!!, 0.001f)
        assertTrue(ChartMath.yFraction(state.sample!!.gallery!!) < ChartMath.yFraction(state.sample!!.studio!!))
        assertEquals(0f, ChartMath.yFraction(26f), 0.001f)
        assertEquals(1f, ChartMath.yFraction(18f), 0.001f)
    }

    @Test fun nearestPointClampsAndHandlesEmptyAndSingleData() {
        assertNull(ChartMath.nearestIndex(0.5f, 0))
        assertEquals(0, ChartMath.nearestIndex(-2f, 25))
        assertEquals(24, ChartMath.nearestIndex(2f, 25))
        assertEquals(14, ChartMath.nearestIndex(14f / 24f, 25))
        assertEquals(0, ChartMath.nearestIndex(1f, 1))
    }

    @Test fun missingDataSplitsPathsAndPreservesSingletons() {
        assertEquals(
            listOf(listOf(0 to 1f, 1 to 2f), listOf(3 to 4f), listOf(5 to 6f)),
            ChartMath.segments(listOf(1f, 2f, null, 4f, null, 6f)),
        )
        assertTrue(ChartMath.segments(listOf(null, null)).isEmpty())
    }

    @Test fun fixturesAreDeterministicAndDistinct() {
        val day = TemperatureData.load(Period.DAY, Scenario.NORMAL)
        assertEquals(day, TemperatureData.load(Period.DAY, Scenario.NORMAL))
        assertEquals(25, day.samples.size)
        assertEquals(7, TemperatureData.load(Period.WEEK, Scenario.NORMAL).samples.size)
        assertEquals(30, TemperatureData.load(Period.MONTH, Scenario.NORMAL).samples.size)
        for (period in Period.entries) {
            for (s in TemperatureData.load(period, Scenario.NORMAL).samples) {
                assertTrue(s.studio!! in ChartMath.MIN..ChartMath.MAX)
                assertTrue(s.gallery!! in ChartMath.MIN..ChartMath.MAX)
            }
        }
    }

    @Test fun emptyAndGapAreNeverZero() {
        assertNull(ExperimentState().changeScenario(Scenario.EMPTY).sample)
        val gaps = TemperatureData.load(Period.DAY, Scenario.GAPS).samples
        assertNull(gaps[10].studio)
        assertNotNull(gaps[10].gallery)
        assertEquals("No sample", ChartMath.format(gaps[10].studio))
    }

    @Test fun atLeastOneSeriesStaysVisible() {
        val state = ExperimentState().toggle(Series.STUDIO)
        assertFalse(state.studio)
        assertEquals(state, state.toggle(Series.GALLERY))
        assertTrue(state.toggle(Series.STUDIO).studio)
    }

    @Test fun periodAndScenarioChangesKeepSelectionInBounds() {
        val state = ExperimentState(selected = 24).changePeriod(Period.WEEK)
        assertEquals(6, state.selected)
        assertEquals("Sun", state.sample!!.label)
        assertEquals(0, state.select(-1).selected)
        assertEquals(6, state.select(90).selected)
        assertEquals(0, state.changeScenario(Scenario.EMPTY).selected)
        assertNotNull(state.changeScenario(Scenario.EMPTY).changeScenario(Scenario.NORMAL).sample)
    }
}
