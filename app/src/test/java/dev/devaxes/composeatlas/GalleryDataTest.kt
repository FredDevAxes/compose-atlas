package dev.devaxes.composeatlas

import dev.devaxes.composeatlas.data.AgendaEvent
import dev.devaxes.composeatlas.data.placeEvents
import dev.devaxes.composeatlas.data.validateSession
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GalleryDataTest {
    @Test
    fun validationRejectsIncompleteInputAndTrimsValidInput() {
        assertFalse(validateSession("", "").valid)
        assertFalse(validateSession("A", "a@b").valid)
        assertFalse(validateSession("Alex", "alex @example.com").valid)
        assertTrue(validateSession(" Alex ", " alex@example.com ").valid)
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
}
