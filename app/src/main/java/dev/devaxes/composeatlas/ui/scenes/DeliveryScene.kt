package dev.devaxes.composeatlas.ui.scenes

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.devaxes.composeatlas.ui.LocalAtlasMotion
import kotlin.math.atan2

private val DeliveryInk = Color(0xFF17223C)
private val DeliveryCyan = Color(0xFF007A87)
private val DeliveryGround = Color(0xFFE5F7F7)

/** One finite journey owns the route and the status; leaving composition cancels its clock. */
@Composable
internal fun DeliveryScene() {
    var sent by rememberSaveable { mutableStateOf(false) }
    var savedProgress by rememberSaveable { mutableFloatStateOf(0f) }
    var journey by rememberSaveable { mutableIntStateOf(0) }
    val progress = remember { Animatable(savedProgress) }
    val motion = LocalAtlasMotion.current
    LaunchedEffect(sent, journey, motion) {
        progress.snapTo(savedProgress)
        if (sent && savedProgress < 1f) {
            progress.animateTo(1f, motion.tween(base = (6400 * (1f - savedProgress)).toInt())) {
                savedProgress = value
            }
            savedProgress = 1f
        }
    }
    val stage =
        when {
            !sent || progress.value < 0.12f -> 0
            progress.value < 0.94f -> 1
            else -> 2
        }
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Good things, en route.", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Studio → your door", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text("#AT-042", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        DeliveryMap(progress.value, Modifier.fillMaxWidth().height(258.dp))
        Row(
            Modifier.fillMaxWidth().semantics(mergeDescendants = true) {
                liveRegion = LiveRegionMode.Polite
                contentDescription = "Delivery status: ${listOf("Preparing", "On the way", "At your door")[stage]}"
            },
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            listOf("Preparing", "On the way", "At your door").forEachIndexed { index, label ->
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(if (index <= stage) DeliveryCyan else Color(0xFFDCE4E9)),
                    )
                    Text(
                        label,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (index == stage) FontWeight.Bold else FontWeight.Normal,
                        color = if (index <= stage) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
        Button(
            onClick = {
                savedProgress = 0f
                sent = true
                journey += 1
            },
            enabled = !sent || savedProgress >= 1f,
            modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp).testTag("send-parcel"),
        ) {
            Text(
                if (!sent) {
                    "Send parcel"
                } else if (savedProgress < 1f) {
                    "Delivery in progress"
                } else {
                    "Replay delivery"
                },
            )
        }
    }
}

/** Both the revealed trail and the courier use distance along this same curved path. */
@Composable
private fun DeliveryMap(
    progress: Float,
    modifier: Modifier = Modifier,
    showLabels: Boolean = true,
) {
    Box(modifier.clip(RoundedCornerShape(24.dp)).background(DeliveryGround)) {
        Canvas(
            Modifier.fillMaxSize().semantics {
                contentDescription =
                    "Illustrated neighborhood. A parcel travels from the studio in the southwest to your door in the northeast."
            },
        ) {
            val w = size.width
            val h = size.height
            // The streets and blocks are deliberate map geometry, not a map-service image.
            val road = Color.White
            val roadWidth = 18.dp.toPx()
            val horizontal =
                Path().apply {
                    moveTo(-w * 0.1f, h * 0.38f)
                    cubicTo(w * 0.28f, h * 0.2f, w * 0.58f, h * 0.62f, w * 1.1f, h * 0.4f)
                }
            val vertical =
                Path().apply {
                    moveTo(w * 0.37f, -h * 0.1f)
                    cubicTo(w * 0.3f, h * 0.4f, w * 0.75f, h * 0.62f, w * 0.62f, h * 1.1f)
                }
            drawPath(horizontal, road, style = Stroke(roadWidth))
            drawPath(vertical, road, style = Stroke(roadWidth))
            drawLine(road, Offset(0f, h * 0.82f), Offset(w, h * 0.82f), roadWidth)
            val blocks =
                listOf(
                    floatArrayOf(.06f, .10f, .20f, .16f),
                    floatArrayOf(.47f, .06f, .18f, .17f),
                    floatArrayOf(.72f, .50f, .21f, .17f),
                    floatArrayOf(.08f, .48f, .17f, .14f),
                    floatArrayOf(.36f, .88f, .14f, .16f),
                )
            blocks.forEachIndexed { index, block ->
                drawRoundRect(
                    if (index % 2 == 0) Color(0xFFC9E8E4) else Color(0xFFD6E9E9),
                    Offset(w * block[0], h * block[1]),
                    Size(w * block[2], h * block[3]),
                    CornerRadius(9.dp.toPx()),
                )
            }
            val route =
                Path().apply {
                    moveTo(w * .14f, h * .76f)
                    cubicTo(w * .25f, h * .76f, w * .39f, h * .81f, w * .43f, h * .65f)
                    cubicTo(w * .48f, h * .44f, w * .42f, h * .34f, w * .62f, h * .35f)
                    cubicTo(w * .79f, h * .37f, w * .83f, h * .34f, w * .83f, h * .20f)
                }
            val measure = PathMeasure().apply { setPath(route, false) }
            drawPath(route, Color(0xFF9ACCCF), style = Stroke(5.dp.toPx(), cap = StrokeCap.Round))
            val travelled = Path()
            measure.getSegment(0f, measure.length * progress.coerceIn(0f, 1f), travelled, true)
            drawPath(travelled, DeliveryCyan, style = Stroke(5.dp.toPx(), cap = StrokeCap.Round))
            val start = measure.getPosition(0f)
            val end = measure.getPosition(measure.length)
            drawCircle(Color.White, 9.dp.toPx(), start)
            drawCircle(DeliveryCyan, 4.dp.toPx(), start)
            drawCircle(Color.White, 16.dp.toPx(), end)
            val house =
                Path().apply {
                    moveTo(end.x - 8.dp.toPx(), end.y)
                    lineTo(end.x, end.y - 7.dp.toPx())
                    lineTo(end.x + 8.dp.toPx(), end.y)
                    moveTo(end.x - 6.dp.toPx(), end.y - 1.dp.toPx())
                    lineTo(end.x - 6.dp.toPx(), end.y + 7.dp.toPx())
                    lineTo(end.x + 6.dp.toPx(), end.y + 7.dp.toPx())
                    lineTo(end.x + 6.dp.toPx(), end.y - 1.dp.toPx())
                }
            drawPath(house, DeliveryInk, style = Stroke(2.dp.toPx(), cap = StrokeCap.Round))
            val courier = measure.getPosition(measure.length * progress.coerceIn(0f, 1f))
            val tangent = measure.getTangent(measure.length * progress.coerceIn(0f, 1f))
            val angle = atan2(tangent.y, tangent.x) * 180f / Math.PI.toFloat()
            drawCircle(DeliveryInk.copy(alpha = .10f), 23.dp.toPx(), courier + Offset(0f, 3.dp.toPx()))
            drawCircle(DeliveryInk, 21.dp.toPx(), courier)
            rotate(angle, courier) {
                val parcelSize = 19.dp.toPx()
                drawRoundRect(
                    Color(0xFF91E2E2),
                    courier - Offset(parcelSize / 2, parcelSize / 2),
                    Size(parcelSize, parcelSize),
                    CornerRadius(3.dp.toPx()),
                )
                drawLine(DeliveryInk, courier - Offset(0f, parcelSize / 2), courier + Offset(0f, parcelSize / 2), 2.dp.toPx())
                drawLine(DeliveryInk, courier - Offset(4.dp.toPx(), 2.dp.toPx()), courier + Offset(4.dp.toPx(), -2.dp.toPx()), 2.dp.toPx())
            }
        }
        if (showLabels) {
            Text(
                "STUDIO",
                modifier = Modifier.align(Alignment.BottomStart).padding(start = 20.dp, bottom = 18.dp),
                style = MaterialTheme.typography.labelSmall,
                color = DeliveryCyan,
            )
            Text(
                "YOUR DOOR",
                modifier = Modifier.align(Alignment.TopEnd).padding(top = 14.dp, end = 15.dp),
                style = MaterialTheme.typography.labelSmall,
                color = DeliveryCyan,
            )
        }
    }
}

@Composable
internal fun DeliveryPreview(modifier: Modifier = Modifier) {
    DeliveryMap(.58f, modifier, showLabels = false)
}
