package dev.devaxes.composeatlas.data

data class SessionErrors(
    val name: String? = null,
    val email: String? = null,
) {
    val valid: Boolean get() = name == null && email == null
}

// Deliberately small local validation, not email deliverability verification.
fun validateSession(
    name: String,
    email: String,
): SessionErrors =
    SessionErrors(
        name = if (name.trim().length < 2) "Enter at least two characters." else null,
        email = if (!Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$").matches(email.trim())) "Use an address such as alex@example.com." else null,
    )

data class AgendaEvent(
    val id: Int,
    val title: String,
    val start: Int,
    val duration: Int,
)

data class PlacedEvent(
    val event: AgendaEvent,
    val lane: Int,
    val laneCount: Int,
)

// Each connected overlap group shares the same lane count; touching edges do not overlap.
fun placeEvents(events: List<AgendaEvent>): List<PlacedEvent> {
    val result = mutableListOf<PlacedEvent>()
    var group = mutableListOf<Pair<AgendaEvent, Int>>()
    val laneEnds = mutableListOf<Int>()

    fun flush() {
        result += group.map { (event, lane) -> PlacedEvent(event, lane, laneEnds.size) }
        group = mutableListOf()
        laneEnds.clear()
    }
    events.sortedWith(compareBy<AgendaEvent> { it.start }.thenBy { it.id }).forEach { event ->
        if (group.isNotEmpty() && event.start >= laneEnds.max()) flush()
        var lane = laneEnds.indexOfFirst { it <= event.start }
        if (lane == -1) {
            lane = laneEnds.size
            laneEnds += event.start + event.duration
        } else {
            laneEnds[lane] = event.start + event.duration
        }
        group += event to lane
    }
    if (group.isNotEmpty()) flush()
    return result
}
