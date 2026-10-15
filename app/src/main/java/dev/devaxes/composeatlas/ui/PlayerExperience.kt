package dev.devaxes.composeatlas.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import dev.devaxes.composeatlas.data.demoTracks
import dev.devaxes.composeatlas.data.timeLabel
import kotlinx.coroutines.delay
import kotlin.math.sin

@Composable
internal fun PlayerExperience(onExit: () -> Unit) {
    var index by rememberSaveable { mutableStateOf(0) }
    var position by rememberSaveable { mutableFloatStateOf(0f) }
    var expanded by rememberSaveable { mutableStateOf(true) }
    var playing by remember { mutableStateOf(false) }
    var empty by rememberSaveable { mutableStateOf(false) }
    val track = demoTracks[index]
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event -> if (event == Lifecycle.Event.ON_STOP) playing = false }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }
    LaunchedEffect(playing, index, empty) {
        while (playing && !empty) {
            delay(100)
            position = (position + 0.1f).coerceAtMost(track.seconds.toFloat())
            if (position >= track.seconds) playing = false
        }
    }
    val select: (Int) -> Unit = {
        index = (it + demoTracks.size) % demoTracks.size
        position = 0f
    }
    ExperimentFrame(
        "Quiet frequency",
        "Fictional audio / no sound",
        onExit,
        explanation = {
            Lesson(
                "A player without an audio engine",
                "The clock advances local state every 100 ms. No audio is decoded, streamed or played. Leaving the screen or putting the app in the background pauses playback.",
            )
            Lesson(
                "Size follows content",
                "animateContentSize resizes the player. AnimatedVisibility reveals its controls; AnimatedContent transitions between track titles. These finite animations follow Android’s animation scale.",
            )
            Lesson(
                "Gestures have an alternative",
                "Swipe the track title up to expand or down to compact. The Expand / Compact button provides the same action. The native slider supports touch and accessibility adjustment.",
            )
            Lesson(
                "Effects and restoration",
                "LaunchedEffect cancels the clock when playback stops. Track, position and expanded state survive recreation; playback deliberately returns paused. A real player would need Media3, an audio session and lifecycle-aware service work.",
            )
            Lesson(
                "Try it",
                "Seek near the end and press Play: playback stops at the track duration. Switch tracks, compact the player, or load the empty queue. Restore the queue to start again.",
            )
        },
    ) {
        ChoiceRow(listOf("Three tracks", "Empty queue"), if (empty) 1 else 0, {
            empty = it == 1
            playing = false
            position = 0f
        })
        if (empty) {
            LabSurface {
                Text("The queue is quiet", style = MaterialTheme.typography.headlineSmall)
                Text("Load the fictional tracks to explore playback.", color = Instrument.Muted)
                Button(onClick = { empty = false }) { Text("Load demo tracks") }
            }
        } else {
            Column(Modifier.animateContentSize()) {
                LabSurface {
                    AnimatedContent(
                        targetState = index,
                        modifier =
                            Modifier.testTag("player-gesture").pointerInput(Unit) {
                                var drag = 0f
                                detectVerticalDragGestures(
                                    onDragStart = { drag = 0f },
                                    onVerticalDrag = { change, amount ->
                                        change.consume()
                                        drag += amount
                                    },
                                    onDragEnd = {
                                        if (drag < -40.dp.toPx()) expanded = true
                                        if (drag > 40.dp.toPx()) expanded = false
                                    },
                                )
                            },
                        label = "Track title",
                    ) { selected ->
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(demoTracks[selected].title, style = MaterialTheme.typography.headlineMedium)
                            Text(demoTracks[selected].artist, color = Instrument.Muted)
                        }
                    }
                    AnimatedVisibility(visible = expanded) {
                        Canvas(Modifier.fillMaxWidth().height(112.dp)) {
                            val bars = 44
                            val step = size.width / bars
                            repeat(bars) { bar ->
                                val amplitude = (0.2f + 0.8f * kotlin.math.abs(sin((bar + track.seed) * 0.71f))) * size.height * 0.44f
                                val x = (bar + 0.5f) * step
                                drawLine(
                                    if ((bar + 0.5f) / bars < position / track.seconds) Instrument.Lime else Instrument.Outline,
                                    Offset(x, size.height / 2 - amplitude),
                                    Offset(x, size.height / 2 + amplitude),
                                    strokeWidth = step * 0.46f,
                                    cap = StrokeCap.Round,
                                )
                            }
                        }
                    }
                    Text(
                        "${timeLabel(position.toInt())} / ${timeLabel(track.seconds)}",
                        modifier = Modifier.testTag("player-time"),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Slider(
                        value = position,
                        onValueChange = { position = it },
                        valueRange = 0f..track.seconds.toFloat(),
                        modifier = Modifier.testTag("player-seek").semantics { contentDescription = "Track position" },
                    )
                    Button(
                        onClick = {
                            if (position >= track.seconds) position = 0f
                            playing = !playing
                        },
                        modifier = Modifier.fillMaxWidth().testTag("player-toggle"),
                    ) {
                        Text(
                            if (playing) {
                                "Pause"
                            } else if (position >= track.seconds) {
                                "Play again"
                            } else {
                                "Play"
                            },
                        )
                    }
                    AnimatedVisibility(visible = expanded) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { select(index - 1) },
                                modifier = Modifier.weight(1f).testTag("previous-track"),
                            ) { Text("Previous") }
                            OutlinedButton(
                                onClick = { select(index + 1) },
                                modifier = Modifier.weight(1f).testTag("next-track"),
                            ) { Text("Next") }
                        }
                    }
                    TextButton(onClick = { expanded = !expanded }, modifier = Modifier.fillMaxWidth().testTag("player-expand")) {
                        Text(if (expanded) "Compact player" else "Expand player")
                    }
                }
            }
            Text("Generated waveform artwork, not audio analysis.", color = Instrument.Muted, style = MaterialTheme.typography.bodySmall)
            Text("Swipe the track title up or down, or use its button.", color = Instrument.Muted)
        }
    }
}
