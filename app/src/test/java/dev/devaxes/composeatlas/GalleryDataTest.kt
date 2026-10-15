package dev.devaxes.composeatlas

import dev.devaxes.composeatlas.data.AGENDA_END
import dev.devaxes.composeatlas.data.AGENDA_START
import dev.devaxes.composeatlas.data.AgendaEvent
import dev.devaxes.composeatlas.data.agendaEvents
import dev.devaxes.composeatlas.data.filterObjects
import dev.devaxes.composeatlas.data.moveEvent
import dev.devaxes.composeatlas.data.placeEvents
import dev.devaxes.composeatlas.data.timeLabel
import dev.devaxes.composeatlas.data.validateSession
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GalleryDataTest {
    @Test
    fun searchCombinesCategoryAndSavedIdentityWithoutChangingOrder() {
        assertEquals(listOf(1), filterObjects(" ARC ", "Light", true, setOf(1, 4), false).map { it.id })
        assertEquals(listOf(4, 1), filterObjects("", "All", true, setOf(1, 4), true).map { it.id })
        assertTrue(filterObjects("absent", "All", false, emptySet(), false).isEmpty())
        assertTrue(filterObjects("", "Clay", true, setOf(1), false).isEmpty())
    }

    @Test
    fun validationRejectsIncompleteInputAndTrimsValidInput() {
        assertFalse(validateSession("", "").valid)
        assertFalse(validateSession("A", "a@b").valid)
        assertFalse(validateSession("Alex", "alex @example.com").valid)
        assertTrue(validateSession(" Alex ", " alex@example.com ").valid)
    }

    @Test
    fun moveClampsTheWholeEventToVisibleHours() {
        val event = agendaEvents.first()
        assertEquals(AGENDA_START, moveEvent(event, -999).start)
        assertEquals(AGENDA_END - event.duration, moveEvent(event, 999).start)
        assertEquals(event.start + 15, moveEvent(event, 15).start)
    }

    @Test
    fun adjacentEventsShareOneLaneButOverlappingGroupsShareTheirWidth() {
        val events =
            listOf(
                AgendaEvent(1, "A", 540, 60),
                AgendaEvent(2, "B", 570, 60),
                AgendaEvent(3, "C", 600, 60),
                AgendaEvent(4, "D", 660, 30),
            )
        val placed = placeEvents(events)
        assertEquals(listOf(0, 1, 0, 0), placed.map { it.lane })
        assertEquals(listOf(2, 2, 2, 1), placed.map { it.laneCount })
        assertEquals(listOf(1, 2, 3, 4), placed.map { it.event.id })
    }

    @Test
    fun nestedEventsDoNotCollideAndEmptyAgendaIsValid() {
        val events = listOf(AgendaEvent(1, "A", 540, 120), AgendaEvent(2, "B", 555, 60), AgendaEvent(3, "C", 570, 30))
        val placed = placeEvents(events.reversed())
        assertEquals(listOf(0, 1, 2), placed.map { it.lane })
        assertTrue(placed.all { it.laneCount == 3 })
        assertTrue(placeEvents(emptyList()).isEmpty())
    }

    @Test
    fun clockFormatsMinutesAndClampsNegativeValues() {
        assertEquals("0:00", timeLabel(-1))
        assertEquals("1:36", timeLabel(96))
        assertEquals("9:15", timeLabel(555))
    }
}
