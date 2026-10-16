package dev.devaxes.composeatlas.ui.scenes

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.devaxes.composeatlas.ui.LocalAtlasMotion
import java.util.Locale
import kotlin.math.roundToInt

internal val todayEnergy = listOf(.6f, .4f, .3f, .5f, 1.4f, 1.1f, null, .8f, 1.3f, 2.8f, 1.9f, .7f)
internal val yesterdayEnergy = listOf(.5f, .4f, .4f, .8f, 1.6f, .9f, .7f, .9f, 1.1f, 2.2f, 1.4f, .6f)

internal fun energyIndex(fraction: Float): Int = (fraction * 12 - .5f).roundToInt().coerceIn(0, 11)

/** Missing samples have a visible gap and no animated surrogate value. */
@Composable
internal fun EnergyScene() {
    var yesterday by rememberSaveable { mutableStateOf(false) }
    var selected by rememberSaveable { mutableIntStateOf(9) }
    val readings = if (yesterday) yesterdayEnergy else todayEnergy
    val motion = LocalAtlasMotion.current
    val heights = readings.map { animateFloatAsState(it ?: 0f, motion.tween(650), label = "energy bar").value }
    val value = readings[selected]
    val time = "%02d:00".format(Locale.ROOT, selected * 2)
    val ink = Color(0xFF163D31)
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("A day in your home", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("Touch the chart to find the busiest hours.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Surface(color = Color(0xFFC6EEDC), contentColor = ink, shape = RoundedCornerShape(24.dp)) {
            Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(if (yesterday) "Yesterday" else "Today", style = MaterialTheme.typography.titleMedium)
                Text(
                    if (value ==
                        null
                    ) {
                        "No reading"
                    } else {
                        "%.1f kWh".format(Locale.ROOT, value)
                    },
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.testTag("energy-value"),
                )
                Text(time, modifier = Modifier.testTag("energy-time"), style = MaterialTheme.typography.titleMedium)
                Canvas(
                    Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                        .testTag("energy-chart")
                        .semantics {
                            contentDescription =
                                "Energy chart. $time: ${value?.let {
                                    "$it kilowatt hours"
                                } ?: "no reading"}. Use Earlier or Later to inspect."
                        }.pointerInput(Unit) { detectTapGestures { selected = energyIndex(it.x / size.width) } }
                        .pointerInput(Unit) {
                            detectDragGestures {
                                change,
                                _,
                                ->
                                selected = energyIndex(change.position.x / size.width)
                                change.consume()
                            }
                        },
                ) {
                    val chartHeight = size.height - 12.dp.toPx()
                    val slot = size.width / 12
                    repeat(3) { i ->
                        val y = chartHeight * (1 - i / 3f)
                        drawLine(ink.copy(alpha = .14f), Offset(0f, y), Offset(size.width, y), 1.dp.toPx())
                    }
                    readings.forEachIndexed { i, reading ->
                        val x = slot * (i + .14f)
                        val width = slot * .72f
                        if (reading != null) {
                            val bar = heights[i] / 3f * chartHeight
                            drawRoundRect(
                                if (i ==
                                    selected
                                ) {
                                    ink
                                } else {
                                    Color(0xFF61A988)
                                },
                                Offset(x, chartHeight - bar),
                                Size(width, bar),
                                CornerRadius(4.dp.toPx()),
                            )
                        } else {
                            drawLine(
                                ink,
                                Offset(x, chartHeight),
                                Offset(x + width, chartHeight),
                                2.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(3.dp.toPx(), 3.dp.toPx())),
                            )
                        }
                        if (i == selected) drawCircle(ink, 3.dp.toPx(), Offset(slot * (i + .5f), size.height - 3.dp.toPx()))
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("00:00")
                    Text("12:00")
                    Text("22:00")
                }
                Text(
                    if (selected == 6 &&
                        !yesterday
                    ) {
                        "The meter missed this reading."
                    } else if (selected ==
                        9
                    ) {
                        "Dinner time is the busiest part of the day."
                    } else {
                        "Each bar is one two-hour reading."
                    },
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedButton({ selected-- }, Modifier.weight(1f).testTag("energy-earlier"), enabled = selected > 0) { Text("Earlier") }
            OutlinedButton({ selected++ }, Modifier.weight(1f).testTag("energy-later"), enabled = selected < 11) { Text("Later") }
        }
        FilledTonalButton({
            yesterday = !yesterday
        }, Modifier.fillMaxWidth().testTag("energy-day")) { Text(if (yesterday) "Back to today" else "See yesterday") }
    }
}
