package dev.devaxes.composeatlas.ui.scenes

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import dev.devaxes.composeatlas.data.AgendaEvent
import dev.devaxes.composeatlas.data.placeEvents
import dev.devaxes.composeatlas.ui.LocalAtlasMotion
import java.util.Locale
import kotlin.math.roundToInt

private fun clockLabel(minute: Int): String = "%02d:%02d".format(Locale.ROOT, minute / 60, minute % 60)

/** Time and overlap lanes are the layout inputs; every child is measured exactly once. */
@Composable
internal fun AgendaScene() {
    var moved by rememberSaveable { mutableStateOf(false) }
    val events =
        listOf(
            AgendaEvent(1, "Design review", 540, 60),
            AgendaEvent(2, "Coffee with Sam", if (moved) 660 else 570, 45),
            AgendaEvent(3, "Build together", 615, 45),
        )
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("A little room to breathe", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("Move coffee. Watch the whole morning adjust.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Surface(shape = RoundedCornerShape(24.dp), color = MaterialTheme.colorScheme.surfaceContainerLow) {
            Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Monday", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(
                        if (moved) "Room for everyone" else "One overlap",
                        modifier = Modifier.testTag("agenda-status"),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                if (LocalDensity.current.fontScale > 1.2f) {
                    events.sortedBy { it.start }.forEach { EventBlock(it, Modifier.fillMaxWidth()) }
                } else {
                    Timeline(events)
                }
            }
        }
        Button({
            moved = !moved
        }, Modifier.fillMaxWidth().testTag("agenda-move")) { Text(if (moved) "Put coffee back" else "Move coffee to 11:00") }
        Text(
            if (moved) {
                "Coffee now starts at 11:00. No meetings overlap."
            } else {
                "Coffee and the design review share the same time. " +
                    "Each keeps its own lane."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun Timeline(events: List<AgendaEvent>) {
    val density = LocalDensity.current
    val motion = LocalAtlasMotion.current
    val placements = placeEvents(events)
    val line = MaterialTheme.colorScheme.outlineVariant
    Row(Modifier.fillMaxWidth().height(330.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Column(Modifier.width(42.dp).fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween) {
            listOf("09:00", "10:00", "11:00", "12:00").forEach {
                Text(it, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        BoxWithConstraints(Modifier.weight(1f).fillMaxHeight()) {
            val widthPx = with(density) { maxWidth.roundToPx() }
            val heightPx = with(density) { 324.dp.toPx() }
            val gap = with(density) { 6.dp.roundToPx() }
            val offsets =
                placements.map { placed ->
                    key("position", placed.event.id) {
                        val laneWidth = widthPx / placed.laneCount
                        animateIntOffsetAsState(
                            IntOffset(placed.lane * laneWidth, ((placed.event.start - 540) / 180f * heightPx).roundToInt()),
                            motion.tween(700),
                            label = "event position",
                        ).value
                    }
                }
            val widths =
                placements.map {
                    key("width", it.event.id) {
                        animateIntAsState((widthPx / it.laneCount - gap).coerceAtLeast(1), motion.tween(700), label = "event width").value
                    }
                }
            Canvas(Modifier.matchParentSize()) {
                repeat(4) { i -> drawLine(line, Offset(0f, i * size.height / 3), Offset(size.width, i * size.height / 3), 1.dp.toPx()) }
            }
            Layout(
                content = { placements.forEach { key(it.event.id) { EventBlock(it.event, Modifier) } } },
                modifier = Modifier.fillMaxSize(),
            ) { measurables, constraints ->
                val children =
                    measurables.mapIndexed { i, child ->
                        val h = (placements[i].event.duration / 180f * heightPx).roundToInt() - gap
                        child.measure(Constraints.fixed(widths[i], h.coerceAtLeast(1)))
                    }
                layout(constraints.maxWidth, constraints.maxHeight) {
                    children.forEachIndexed {
                        i,
                        child,
                        ->
                        child.placeRelative(offsets[i])
                    }
                }
            }
        }
    }
}

@Composable
private fun EventBlock(
    event: AgendaEvent,
    modifier: Modifier,
) {
    val color =
        when (event.id) {
            1 -> Color(0xFFDCD3FA)
            2 -> Color(0xFFFFD6C5)
            else -> Color(0xFFC8E8DA)
        }
    Surface(modifier.testTag("event-${event.id}"), shape = RoundedCornerShape(12.dp), color = color, contentColor = Color(0xFF26253B)) {
        Column(Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(event.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Text("${clockLabel(event.start)} – ${clockLabel(event.start + event.duration)}", style = MaterialTheme.typography.labelSmall)
        }
    }
}
