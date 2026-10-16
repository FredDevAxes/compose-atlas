package dev.devaxes.composeatlas.ui.scenes

import androidx.compose.animation.core.animate
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.devaxes.composeatlas.ui.LocalAtlasMotion
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private val PosterInk = Color(0xFF302451)
private val PosterViolet = Color(0xFF7754CF)
private val PosterPeach = Color(0xFFFFC1AA)

/** The artwork is original geometry. Gestures update it directly; buttons and semantic actions provide alternatives. */
@Composable
internal fun PhotoScene() {
    var scale by rememberSaveable { mutableFloatStateOf(1f) }
    var rotation by rememberSaveable { mutableFloatStateOf(0f) }
    var panX by rememberSaveable { mutableFloatStateOf(0f) }
    var panY by rememberSaveable { mutableFloatStateOf(0f) }
    var actionJob by remember { mutableStateOf<Job?>(null) }
    val scope = rememberCoroutineScope()
    val motion = LocalAtlasMotion.current
    val panLimit = with(LocalDensity.current) { 160.dp.toPx() }

    fun moveArtwork(
        targetScale: Float,
        targetRotation: Float,
        resetPan: Boolean = false,
    ) {
        actionJob?.cancel()
        val fromScale = scale
        val fromRotation = rotation
        val fromX = panX
        val fromY = panY
        actionJob =
            scope.launch {
                animate(0f, 1f, animationSpec = motion.tween(base = 360)) { fraction, _ ->
                    scale = fromScale + (targetScale - fromScale) * fraction
                    rotation = fromRotation + (targetRotation - fromRotation) * fraction
                    if (resetPan) {
                        panX = fromX * (1f - fraction)
                        panY = fromY * (1f - fraction)
                    }
                }
            }
    }
    val transform =
        rememberTransformableState { zoom, pan, turn ->
            actionJob?.cancel()
            scale = (scale * zoom).coerceIn(.65f, 3f)
            rotation = (rotation + turn) % 360f
            panX = (panX + pan.x).coerceIn(-panLimit, panLimit)
            panY = (panY + pan.y).coerceIn(-panLimit, panLimit)
        }
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Your angle. Your artwork.",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
            )
            TextButton(onClick = { moveArtwork(1f, 0f, resetPan = true) }) { Text("Reset") }
        }
        BoxWithConstraints(
            Modifier
                .fillMaxWidth()
                .height(294.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFFEDE6FF))
                .testTag("poster-canvas")
                .semantics {
                    contentDescription =
                        "Poster studio. Original violet, peach and golden geometric artwork. Pinch to zoom, use two fingers to rotate, or drag to move. Double tap to reset."
                    stateDescription = "Zoom ${(scale * 100).roundToInt()} percent. Rotation ${rotation.roundToInt()} degrees."
                    customActions =
                        listOf(
                            CustomAccessibilityAction("Zoom in") {
                                moveArtwork((scale + .25f).coerceAtMost(3f), rotation)
                                true
                            },
                            CustomAccessibilityAction("Zoom out") {
                                moveArtwork((scale - .25f).coerceAtLeast(.65f), rotation)
                                true
                            },
                            CustomAccessibilityAction("Rotate clockwise") {
                                moveArtwork(scale, rotation + 15f)
                                true
                            },
                            CustomAccessibilityAction("Move left") {
                                actionJob?.cancel()
                                panX = (panX - panLimit / 4f).coerceIn(-panLimit, panLimit)
                                true
                            },
                            CustomAccessibilityAction("Move right") {
                                actionJob?.cancel()
                                panX = (panX + panLimit / 4f).coerceIn(-panLimit, panLimit)
                                true
                            },
                            CustomAccessibilityAction("Move up") {
                                actionJob?.cancel()
                                panY = (panY - panLimit / 4f).coerceIn(-panLimit, panLimit)
                                true
                            },
                            CustomAccessibilityAction("Move down") {
                                actionJob?.cancel()
                                panY = (panY + panLimit / 4f).coerceIn(-panLimit, panLimit)
                                true
                            },
                            CustomAccessibilityAction("Reset artwork") {
                                moveArtwork(1f, 0f, resetPan = true)
                                true
                            },
                        )
                }.pointerInput(motion) {
                    detectTapGestures(onDoubleTap = { moveArtwork(1f, 0f, resetPan = true) })
                }.transformable(transform),
            contentAlignment = Alignment.Center,
        ) {
            val posterWidth = minOf(maxWidth * .66f, 210.dp)
            Box(
                Modifier
                    .size(posterWidth, posterWidth * 1.22f)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        rotationZ = rotation
                        translationX = panX
                        translationY = panY
                        shadowElevation = 12.dp.toPx()
                    }.background(Color(0xFFFFF8E9)),
            ) {
                PosterArtwork(Modifier.fillMaxSize())
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = { moveArtwork((scale - .25f).coerceAtLeast(.65f), rotation) },
                enabled = scale > .65f,
                modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                contentPadding = PaddingValues(horizontal = 8.dp),
            ) { Text("Zoom −") }
            OutlinedButton(
                onClick = { moveArtwork((scale + .25f).coerceAtMost(3f), rotation) },
                enabled = scale < 3f,
                modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                contentPadding = PaddingValues(horizontal = 8.dp),
            ) { Text("Zoom +") }
            OutlinedButton(
                onClick = { moveArtwork(scale, rotation + 15f) },
                modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                contentPadding = PaddingValues(horizontal = 8.dp),
            ) { Text("Rotate") }
        }
        Text(
            "Pinch, turn or drag. Double tap to start fresh.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun PosterArtwork(
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    Box(modifier) {
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            drawRect(PosterPeach, Offset(0f, h * .34f), Size(w, h * .66f))
            // A precise arch and orbit are the subject of this poster, not a substitute photograph.
            val arch =
                Path().apply {
                    moveTo(w * .17f, h * .82f)
                    lineTo(w * .17f, h * .41f)
                    cubicTo(w * .17f, h * .08f, w * .83f, h * .08f, w * .83f, h * .41f)
                    lineTo(w * .83f, h * .82f)
                    close()
                }
            drawPath(arch, PosterViolet)
            drawCircle(Color(0xFFF6C94E), w * .19f, Offset(w * .50f, h * .40f))
            drawCircle(PosterInk, w * .31f, Offset(w * .50f, h * .49f), style = Stroke(w * .022f))
            drawCircle(PosterInk, w * .39f, Offset(w * .50f, h * .49f), style = Stroke(w * .01f))
            drawRect(Color(0xFFFFF8E9), Offset(w * .17f, h * .67f), Size(w * .66f, h * .15f))
            drawLine(PosterInk, Offset(w * .17f, h * .82f), Offset(w * .83f, h * .82f), w * .012f)
        }
        if (!compact) {
            Column(Modifier.align(Alignment.BottomStart).padding(16.dp)) {
                Text("FIND YOUR", color = PosterInk, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                Text("own orbit.", color = PosterInk, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
internal fun PhotoPreview(modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier.clipToBounds().background(Color(0xFFEDE6FF)), contentAlignment = Alignment.Center) {
        // Fit both axes, including the rotated corners, at phone and expanded gallery widths.
        val artHeight = minOf(maxHeight * .82f, maxWidth * .72f)
        PosterArtwork(
            Modifier
                .size(artHeight * .82f, artHeight)
                .graphicsLayer { rotationZ = -9f }
                .background(Color(0xFFFFF8E9)),
            compact = true,
        )
    }
}
