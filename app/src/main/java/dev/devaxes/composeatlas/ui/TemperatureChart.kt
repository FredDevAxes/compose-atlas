package dev.devaxes.composeatlas.ui

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.devaxes.composeatlas.data.ChartMath
import dev.devaxes.composeatlas.data.ExperimentState

@Composable
fun TemperatureChart(
    state: ExperimentState,
    modifier: Modifier = Modifier,
    progress: Float = 1f,
    replaying: Boolean = false,
    onSelect: (Int) -> Unit = {},
    onInspect: () -> Unit = {},
    interactive: Boolean = true,
) {
    val samples = state.data.samples
    val density = LocalDensity.current
    val left = with(density) { 28.dp.toPx() }
    val right = with(density) { 12.dp.toPx() }
    val textSize = with(density) { 11.sp.toPx() }
    val chartModifier =
        if (interactive) {
            Modifier
                .semantics {
                    contentDescription = "Temperature chart. " + state.description()
                    customActions =
                        listOf(
                            CustomAccessibilityAction("Previous point") {
                                onInspect()
                                onSelect(state.selected - 1)
                                true
                            },
                            CustomAccessibilityAction("Next point") {
                                onInspect()
                                onSelect(state.selected + 1)
                                true
                            },
                        )
                }.pointerInput(samples.size) {
                    detectTapGestures { point ->
                        onInspect()
                        ChartMath.nearestIndex((point.x - left) / (size.width - left - right), samples.size)?.let(onSelect)
                    }
                }.pointerInput(samples.size) {
                    detectDragGestures(
                        onDragStart = { point ->
                            onInspect()
                            ChartMath.nearestIndex((point.x - left) / (size.width - left - right), samples.size)?.let(onSelect)
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            ChartMath.nearestIndex((change.position.x - left) / (size.width - left - right), samples.size)?.let(onSelect)
                        },
                    )
                }
        } else {
            Modifier
        }
    Canvas(modifier.then(chartModifier).fillMaxSize().testTag("temperature-chart")) {
        val top = 22.dp.toPx()
        val bottom = size.height - 28.dp.toPx()
        val end = size.width - right
        val plotWidth = (end - left).coerceAtLeast(1f)
        val plotHeight = (bottom - top).coerceAtLeast(1f)
        val paint =
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Instrument.Muted.toArgb()
                this.textSize = textSize
            }

        fun x(index: Int) = left + ChartMath.xFraction(index, samples.size) * plotWidth

        fun y(value: Float) = top + ChartMath.yFraction(value) * plotHeight
        drawContext.canvas.nativeCanvas.drawText("°C", 2.dp.toPx(), 14.dp.toPx(), paint)
        for (value in 18..26 step 2) {
            val yy = y(value.toFloat())
            drawLine(
                Instrument.Outline,
                Offset(left, yy),
                Offset(end, yy),
                1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(3.dp.toPx(), 3.dp.toPx())),
            )
            if (value % 4 == 2) drawContext.canvas.nativeCanvas.drawText(value.toString(), 0f, yy + textSize / 3, paint)
        }
        for (i in 0..6) {
            val xx = left + plotWidth * i / 6
            drawLine(
                Instrument.Outline.copy(alpha = 0.7f),
                Offset(xx, top),
                Offset(xx, bottom),
                1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(3.dp.toPx(), 3.dp.toPx())),
            )
        }
        drawLine(Instrument.Muted.copy(alpha = 0.5f), Offset(left, top), Offset(left, bottom), 1.dp.toPx())
        drawLine(Instrument.Muted.copy(alpha = 0.5f), Offset(left, bottom), Offset(end, bottom), 1.dp.toPx())
        if (samples.isEmpty()) return@Canvas
        listOf(0, samples.lastIndex / 2, samples.lastIndex).distinct().forEach { index ->
            val raw = samples[index].label
            val label = if (raw.startsWith("Synthetic")) raw.takeLast(5) else raw
            val width = paint.measureText(label)
            drawContext.canvas.nativeCanvas.drawText(
                label,
                (x(index) - width / 2).coerceIn(0f, size.width - width),
                size.height - 5.dp.toPx(),
                paint,
            )
        }
        clipRect(
            left = left - 4.dp.toPx(),
            top = top - 4.dp.toPx(),
            right = left + plotWidth * progress.coerceIn(0f, 1f) + 4.dp.toPx(),
            bottom =
                bottom + 4.dp.toPx(),
        ) {
            fun trace(
                values: List<Float?>,
                color: androidx.compose.ui.graphics.Color,
                dashed: Boolean,
            ) {
                for (segment in ChartMath.segments(values)) {
                    if (segment.size == 1) {
                        drawCircle(color, 2.dp.toPx(), Offset(x(segment[0].first), y(segment[0].second)))
                    } else {
                        val path = Path()
                        segment.forEachIndexed { index, pair ->
                            if (index == 0) path.moveTo(x(pair.first), y(pair.second)) else path.lineTo(x(pair.first), y(pair.second))
                        }
                        drawPath(
                            path,
                            color,
                            style =
                                Stroke(
                                    2.dp.toPx(),
                                    pathEffect = if (dashed) PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 5.dp.toPx())) else null,
                                ),
                        )
                    }
                }
            }
            if (state.studio) trace(samples.map { it.studio }, Instrument.Lime, false)
            if (state.gallery) trace(samples.map { it.gallery }, Instrument.Cyan, true)
        }
        if (!replaying) {
            val sample = samples.getOrNull(state.selected) ?: return@Canvas
            drawLine(
                Instrument.Amber,
                Offset(x(state.selected), top),
                Offset(x(state.selected), bottom),
                1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(5.dp.toPx(), 4.dp.toPx())),
            )

            fun marker(
                value: Float?,
                color: androidx.compose.ui.graphics.Color,
            ) {
                value ?: return
                drawCircle(color, 5.dp.toPx(), Offset(x(state.selected), y(value)))
                drawCircle(Instrument.Background, 2.dp.toPx(), Offset(x(state.selected), y(value)))
            }
            if (state.studio) marker(sample.studio, Instrument.Lime)
            if (state.gallery) marker(sample.gallery, Instrument.Cyan)
        }
    }
}
