package dev.devaxes.composeatlas.ui.scenes

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import dev.devaxes.composeatlas.ui.LocalAtlasMotion
import kotlinx.coroutines.delay

private val RecordCoral = Color(0xFFFF8D79)
private val RecordCream = Color(0xFFFFE7BC)
private val RecordInk = Color(0xFF182443)

private data class RecordTrack(
    val title: String,
    val artist: String,
    val seconds: Int,
)

private val recordTracks =
    listOf(
        RecordTrack("Sidewalk radio", "Mira & The Satellites", 184),
        RecordTrack("After the rain", "The Paper Planes", 212),
        RecordTrack("A little further", "Mira & The Satellites", 167),
    )

/** A local transport simulation: the clock is cancelled on exit and pauses on backgrounding. */
@Composable
internal fun MusicScene() {
    var trackIndex by rememberSaveable { mutableIntStateOf(0) }
    var position by rememberSaveable { mutableFloatStateOf(0f) }
    var compact by rememberSaveable { mutableStateOf(false) }
    var playing by remember { mutableStateOf(false) }
    val track = recordTracks[trackIndex]
    val motion = LocalAtlasMotion.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_STOP) playing = false
            }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }
    LaunchedEffect(playing, trackIndex) {
        while (playing) {
            delay(250)
            position = (position + 0.25f).coerceAtMost(track.seconds.toFloat())
            if (position >= track.seconds) playing = false
        }
    }
    val artHeight by animateDpAsState(
        if (compact) 100.dp else 230.dp,
        motion.spring(),
        label = "Record sleeve folds",
    )
    val rotation by animateFloatAsState(if (motion.enabled) position * 5f else 0f, motion.tween(250), label = "Record turns")
    val ink = MaterialTheme.colorScheme.onSurface
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("On repeat.", Modifier.weight(1f), style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
            TextButton(onClick = { compact = !compact }, modifier = Modifier.testTag("music-fold")) {
                Text(if (compact) "Expand" else "Fold")
            }
        }
        Box(
            Modifier
                .fillMaxWidth()
                .height(artHeight)
                .clip(RoundedCornerShape(24.dp))
                .background(RecordCoral),
            contentAlignment = Alignment.Center,
        ) {
            VinylArtwork(rotation, Modifier.fillMaxSize().padding(if (compact) 4.dp else 12.dp))
            Text(
                "SIDE A",
                Modifier.align(Alignment.TopStart).padding(18.dp),
                color = RecordInk,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Black,
            )
        }
        AnimatedContent(
            targetState = trackIndex,
            transitionSpec = {
                (fadeIn(motion.tween(220)) + slideInVertically(motion.tween(320)) { it / 3 }) togetherWith
                    (fadeOut(motion.tween(160)) + slideOutVertically(motion.tween(240)) { -it / 3 }) using
                    SizeTransform(sizeAnimationSpec = { _, _ -> motion.spring() })
            },
            label = "New track",
        ) { selected ->
            Column(Modifier.fillMaxWidth()) {
                Text(recordTracks[selected].title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text(
                    recordTracks[selected].artist,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Column {
            Slider(
                value = position,
                onValueChange = { position = it },
                valueRange = 0f..track.seconds.toFloat(),
                colors = SliderDefaults.colors(activeTrackColor = ink, thumbColor = ink),
                modifier = Modifier.fillMaxWidth().testTag("music-seek").semantics { contentDescription = "Track position" },
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(recordTime(position.toInt()), style = MaterialTheme.typography.labelMedium)
                Text(recordTime(track.seconds), style = MaterialTheme.typography.labelMedium)
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = {
                    trackIndex = (trackIndex + recordTracks.size - 1) % recordTracks.size
                    position = 0f
                },
                modifier = Modifier.size(56.dp).semantics { contentDescription = "Previous track" },
            ) { TransportGlyph("previous", ink) }
            Button(
                onClick = {
                    if (position >= track.seconds) position = 0f
                    playing = !playing
                },
                modifier = Modifier.height(56.dp).padding(horizontal = 12.dp).testTag("music-play"),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = ink, contentColor = MaterialTheme.colorScheme.surface),
            ) {
                TransportGlyph(if (playing) "pause" else "play", MaterialTheme.colorScheme.surface)
                Text(if (playing) "Pause" else "Play", Modifier.padding(start = 8.dp), style = MaterialTheme.typography.titleMedium)
            }
            IconButton(
                onClick = {
                    trackIndex = (trackIndex + 1) % recordTracks.size
                    position = 0f
                },
                modifier = Modifier.size(56.dp).semantics { contentDescription = "Next track" },
            ) { TransportGlyph("next", ink) }
        }
        Text(
            "A make-believe record. Real controls, no sound.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/** The sleeve is deliberately graphic geometry, so it needs no remote artwork or image claim. */
@Composable
private fun VinylArtwork(
    rotation: Float,
    modifier: Modifier = Modifier,
) {
    val ink = RecordInk
    Canvas(modifier) {
        val radius = size.minDimension * 0.46f
        val center = Offset(size.width * 0.55f, size.height * 0.5f)
        drawCircle(ink, radius, center)
        repeat(6) { groove ->
            drawCircle(RecordCoral.copy(alpha = 0.25f), radius * (0.46f + groove * 0.075f), center, style = Stroke(1.dp.toPx()))
        }
        rotate(rotation, center) {
            drawArc(
                RecordCream.copy(alpha = 0.35f),
                -40f,
                52f,
                false,
                Offset(center.x - radius * 0.91f, center.y - radius * 0.91f),
                Size(radius * 1.82f, radius * 1.82f),
                style = Stroke(radius * 0.10f),
            )
            drawCircle(RecordCream, radius * 0.31f, center)
            drawLine(
                RecordCoral,
                Offset(center.x - radius * 0.20f, center.y + radius * 0.07f),
                Offset(
                    center.x + radius * 0.20f,
                    center.y - radius * 0.07f,
                ),
                radius * 0.13f,
            )
        }
        drawCircle(ink, radius * 0.045f, center)
    }
}

@Composable
private fun TransportGlyph(
    kind: String,
    tint: Color,
) {
    Canvas(Modifier.size(24.dp)) {
        val w = size.width
        val h = size.height
        if (kind == "pause") {
            drawLine(tint, Offset(w * 0.34f, h * 0.22f), Offset(w * 0.34f, h * 0.78f), w * 0.16f, StrokeCap.Round)
            drawLine(tint, Offset(w * 0.67f, h * 0.22f), Offset(w * 0.67f, h * 0.78f), w * 0.16f, StrokeCap.Round)
        } else {
            val reverse = kind == "previous"
            val path =
                Path().apply {
                    moveTo(w * (if (reverse) 0.72f else 0.28f), h * 0.18f)
                    lineTo(w * (if (reverse) 0.28f else 0.78f), h * 0.5f)
                    lineTo(w * (if (reverse) 0.72f else 0.28f), h * 0.82f)
                    close()
                }
            drawPath(path, tint)
            if (kind != "play") {
                val x = w * if (reverse) 0.20f else 0.84f
                drawLine(tint, Offset(x, h * 0.2f), Offset(x, h * 0.8f), w * 0.09f, StrokeCap.Round)
            }
        }
    }
}

private fun recordTime(seconds: Int): String = "${seconds / 60}:${(seconds % 60).toString().padStart(2, '0')}"

@Composable
internal fun MusicPreview(modifier: Modifier = Modifier) {
    VinylArtwork(18f, modifier.padding(4.dp))
}
