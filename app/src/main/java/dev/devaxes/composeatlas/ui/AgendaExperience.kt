package dev.devaxes.composeatlas.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import dev.devaxes.composeatlas.data.AGENDA_END
import dev.devaxes.composeatlas.data.AGENDA_START
import dev.devaxes.composeatlas.data.AgendaEvent
import dev.devaxes.composeatlas.data.agendaEvents
import dev.devaxes.composeatlas.data.moveEvent
import dev.devaxes.composeatlas.data.placeEvents
import dev.devaxes.composeatlas.data.timeLabel
import kotlin.math.roundToInt

@Composable
internal fun AgendaExperience(onExit: () -> Unit) {
    var zoom by rememberSaveable { mutableStateOf(100f) }
    var overlap by rememberSaveable { mutableStateOf(false) }
    var offset by rememberSaveable { mutableStateOf(0) }
    var selected by rememberSaveable { mutableStateOf<Int?>(null) }
    val height by animateFloatAsState(zoom, label = "Timeline scale")
    val events =
        agendaEvents.map {
            when (it.id) {
                1 -> moveEvent(it, offset)
                2 -> if (overlap) it.copy(start = 9 * 60 + 30) else it
                else -> it
            }
        }
    ExperimentFrame(
        "Spatial agenda",
        "One day / synthetic sessions",
        onExit,
        explanation = {
            Lesson(
                "Time becomes geometry",
                "A custom Layout measures each event once. Its height comes from duration; its vertical position comes from minutes since 09:00. The hour grid is a separate Canvas using the same scale.",
            )
            Lesson(
                "Make overlaps explicit",
                "A pure interval algorithm groups overlapping events and assigns lanes. Events touching at one edge do not overlap. Every event in a group receives the same column width.",
            )
            Lesson(
                "Constraints and adaptation",
                "The timeline fills the available width. Below 360 dp or above 1.2 font scale, it presents a readable chronological list, including time and duration. Zoom is disabled in this list; in the timeline it changes hour height; Compose animates the scale using animateFloatAsState.",
            )
            Lesson(
                "Try it",
                "Turn Overlap on, zoom the timeline, then move Motion workshop in 15-minute steps. Moves clamp to 09:00–14:00. Tap any event to read its full time and title.",
            )
        },
    ) {
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val timelineVisible = maxWidth >= 360.dp && LocalDensity.current.fontScale <= 1.2f
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Thursday / Studio day", style = MaterialTheme.typography.headlineSmall)
                Text("09:00—14:00", color = Instrument.Muted)
                FilterChip(selected = overlap, onClick = {
                    overlap = !overlap
                }, label = { Text("Overlap sessions") }, modifier = Modifier.testTag("agenda-overlap"))
                Text("Timeline scale: ${zoom.toInt()} dp / hour", modifier = Modifier.testTag("agenda-scale"))
                Slider(
                    zoom,
                    { zoom = it },
                    valueRange = 80f..160f,
                    enabled = timelineVisible,
                    steps = 3,
                    modifier =
                        Modifier.testTag("agenda-zoom").semantics {
                            contentDescription =
                                "Timeline scale"
                        },
                )
                if (!timelineVisible) {
                    Text(
                        "Zoom is available in the timeline view.",
                        color = Instrument.Muted,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                Text("Move Motion workshop", style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { offset = moveEvent(events.first(), -15).start - AGENDA_START },
                        enabled = events.first().start > AGENDA_START,
                        modifier = Modifier.weight(1f).testTag("agenda-earlier"),
                    ) { Text("15 min earlier") }
                    Button(
                        onClick = { offset = moveEvent(events.first(), 15).start - AGENDA_START },
                        enabled = events.first().start + events.first().duration < AGENDA_END,
                        modifier = Modifier.weight(1f).testTag("agenda-later"),
                    ) { Text("15 min later") }
                }
                Text(
                    "Motion workshop: ${timeLabel(events.first().start)}",
                    modifier = Modifier.testTag("agenda-time"),
                    color = Instrument.Cyan,
                )
                if (!timelineVisible) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("List view / readable event details", color = Instrument.Muted, style = MaterialTheme.typography.bodySmall)
                        events.sortedBy { it.start }.forEach { event ->
                            EventBlock(event, { selected = event.id }, Modifier.fillMaxWidth())
                        }
                    }
                } else {
                    Timeline(events, height, onSelect = { selected = it })
                }
                TextButton(onClick = {
                    zoom = 100f
                    overlap = false
                    offset = 0
                }, modifier = Modifier.fillMaxWidth()) { Text("Reset agenda") }
            }
        }
    }

    selected?.let { id ->
        val event = events.first { it.id == id }
        AlertDialog(
            onDismissRequest = { selected = null },
            title = { Text(event.title) },
            text = {
                Text(
                    "${timeLabel(
                        event.start,
                    )}–${timeLabel(
                        event.start + event.duration,
                    )} · ${event.duration} minutes. This event is placed from time and duration, not a fixed card position.",
                )
            },
            confirmButton = { TextButton(onClick = { selected = null }) { Text("Close event") } },
        )
    }
}

@Composable
private fun Timeline(
    events: List<AgendaEvent>,
    hourHeight: Float,
    onSelect: (Int) -> Unit,
) {
    val placed = placeEvents(events)
    val animatedStarts = placed.map { key(it.event.id) { animateIntAsState(it.event.start, label = "Event position").value } }
    Row(Modifier.fillMaxWidth().height((hourHeight * 5).dp).testTag("agenda-timeline")) {
        Column(Modifier.width(48.dp)) {
            repeat(5) { hour ->
                Text(
                    "${9 + hour}:00",
                    modifier = Modifier.height(hourHeight.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = Instrument.Muted,
                )
            }
        }
        Box(Modifier.weight(1f)) {
            Canvas(Modifier.matchParentSize()) {
                repeat(5) { hour ->
                    val y = hour * hourHeight.dp.toPx()
                    drawLine(Instrument.Outline, Offset(0f, y), Offset(size.width, y), 1.dp.toPx())
                }
            }
            Layout(content = {
                placed.forEach { item ->
                    key(item.event.id) { EventBlock(item.event, { onSelect(item.event.id) }, Modifier) }
                }
            }, modifier = Modifier.fillMaxWidth()) { measurables, constraints ->
                val minute = hourHeight.dp.toPx() / 60f
                val gap = 4.dp.roundToPx()
                val children =
                    measurables.mapIndexed { index, measurable ->
                        val item = placed[index]
                        val width = (constraints.maxWidth / item.laneCount - gap).coerceAtLeast(1)
                        val height = (item.event.duration * minute).roundToInt().coerceAtLeast(1)
                        measurable.measure(Constraints.fixed(width, height))
                    }
                layout(constraints.maxWidth, (minute * (AGENDA_END - AGENDA_START)).roundToInt()) {
                    children.forEachIndexed { index, child ->
                        val item = placed[index]
                        child.placeRelative(
                            x = item.lane * (constraints.maxWidth / item.laneCount),
                            y = ((animatedStarts[index] - AGENDA_START) * minute).roundToInt(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EventBlock(
    event: AgendaEvent,
    onClick: () -> Unit,
    modifier: Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.testTag("event-${event.id}"),
        color = if (event.id == 1) Instrument.Selected else Instrument.Surface,
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(event.title, style = MaterialTheme.typography.labelLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(
                "${timeLabel(event.start)} · ${event.duration} min",
                color = Instrument.Muted,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
            )
        }
    }
}
