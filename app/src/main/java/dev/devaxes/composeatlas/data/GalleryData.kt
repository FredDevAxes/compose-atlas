package dev.devaxes.composeatlas.data

data class DemoTrack(
    val title: String,
    val artist: String,
    val seconds: Int,
    val seed: Int,
)

val demoTracks =
    listOf(
        DemoTrack("Quiet frequency", "Atlas sessions / 01", 96, 3),
        DemoTrack("Glass & daylight", "Atlas sessions / 02", 124, 7),
        DemoTrack("After the rain", "Atlas sessions / 03", 83, 11),
    )

fun timeLabel(seconds: Int): String = "${seconds.coerceAtLeast(0) / 60}:${(seconds.coerceAtLeast(0) % 60).toString().padStart(2, '0')}"

data class GalleryObject(
    val id: Int,
    val title: String,
    val category: String,
)

val galleryObjects =
    listOf(
        GalleryObject(1, "Arc lamp", "Light"),
        GalleryObject(2, "Folded shade", "Light"),
        GalleryObject(3, "Pebble vessel", "Clay"),
        GalleryObject(4, "Tall carafe", "Glass"),
        GalleryObject(5, "Orbit pendant", "Light"),
        GalleryObject(6, "Low bowl", "Clay"),
        GalleryObject(7, "Ripple tumbler", "Glass"),
        GalleryObject(8, "Loop handle", "Clay"),
        GalleryObject(9, "Prism study", "Glass"),
        GalleryObject(10, "Paper lantern", "Light"),
        GalleryObject(11, "Stacked cups", "Clay"),
        GalleryObject(12, "Smoke vase", "Glass"),
    )

fun filterObjects(
    query: String,
    category: String,
    favoritesOnly: Boolean,
    favorites: Set<Int>,
    reverse: Boolean,
): List<GalleryObject> =
    galleryObjects
        .filter {
            (category == "All" || it.category == category) &&
                it.title.contains(query.trim(), ignoreCase = true) &&
                (!favoritesOnly || it.id in favorites)
        }.let { if (reverse) it.reversed() else it }

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

val agendaEvents =
    listOf(
        AgendaEvent(1, "Motion workshop", 9 * 60, 60),
        AgendaEvent(2, "Color & contrast", 10 * 60 + 15, 45),
        AgendaEvent(3, "Layout studio", 11 * 60 + 15, 60),
    )
const val AGENDA_START = 9 * 60
const val AGENDA_END = 14 * 60

fun moveEvent(
    event: AgendaEvent,
    delta: Int,
): AgendaEvent = event.copy(start = (event.start + delta).coerceIn(AGENDA_START, AGENDA_END - event.duration))

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
