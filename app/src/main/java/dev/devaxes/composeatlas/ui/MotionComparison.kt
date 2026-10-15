package dev.devaxes.composeatlas.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.MotionDurationScale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.devaxes.composeatlas.data.ExperimentState
import dev.devaxes.composeatlas.data.MotionMode
import dev.devaxes.composeatlas.data.PlaybackSpeed
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext
import kotlin.math.roundToInt

internal class MotionPlayback {
    val tween = Animatable(1f)
    val spring = Animatable(1f, visibilityThreshold = 0.001f)

    suspend fun replay(state: ExperimentState) {
        tween.snapTo(0f)
        spring.snapTo(0f)
        val systemScale = coroutineContext[MotionDurationScale]
        // Read the system scale dynamically, including Remove animations (zero).
        val slowedClock =
            object : MotionDurationScale {
                override val scaleFactor: Float
                    get() = (systemScale?.scaleFactor ?: 1f) / state.speed.multiplier
            }
        withContext(slowedClock) {
            coroutineScope {
                launch { tween.animateTo(1f, tween(state.duration, easing = FastOutSlowInEasing)) }
                launch { spring.animateTo(1f, spring(state.damping, state.stiffness, visibilityThreshold = 0.001f)) }
            }
        }
    }

    suspend fun finish() {
        tween.stop()
        spring.stop()
        tween.snapTo(1f)
        spring.snapTo(1f)
    }
}

@Composable
internal fun MotionComparison(
    state: ExperimentState,
    tweenPosition: Float,
    springPosition: Float,
    onSpeed: (PlaybackSpeed) -> Unit,
    onReplay: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Compare motion", Modifier.weight(1f), style = MaterialTheme.typography.titleSmall)
            TextButton(onClick = onReplay, enabled = state.sample != null, modifier = Modifier.testTag("compare-replay")) {
                Text("Replay both")
            }
        }
        MotionLane("Tween", tweenPosition, state.mode == MotionMode.TWEEN)
        MotionLane("Spring", springPosition, state.mode == MotionMode.SPRING)
        Text("Target: 100% · room to overshoot", style = MaterialTheme.typography.bodySmall, color = Instrument.Muted)
        ChoiceRow(PlaybackSpeed.entries.map { it.label }, state.speed.ordinal, { onSpeed(PlaybackSpeed.entries[it]) })
    }
}

@Composable
private fun MotionLane(
    name: String,
    position: Float,
    selected: Boolean,
) {
    val color = if (selected) Instrument.Lime else Instrument.Text
    Row(
        Modifier.fillMaxWidth().testTag("motion-" + name.lowercase()).semantics(mergeDescendants = true) {
            contentDescription = "$name animation position; target 100 percent"
            progressBarRangeInfo = ProgressBarRangeInfo(position, 0f..1.5f)
        },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(name, Modifier.width(56.dp), style = MaterialTheme.typography.labelMedium, color = color)
        Canvas(Modifier.weight(1f).height(28.dp)) {
            val inset = 7.dp.toPx()
            val span = size.width - 2 * inset
            val target = inset + span / 1.5f
            val y = size.height / 2
            drawLine(Instrument.Outline, Offset(inset, y), Offset(size.width - inset, y), 2.dp.toPx())
            drawLine(Instrument.Amber, Offset(target, 2.dp.toPx()), Offset(target, size.height - 2.dp.toPx()), 1.dp.toPx())
            drawCircle(color, 5.dp.toPx(), Offset(inset + span * position / 1.5f, y))
        }
        Text(
            "${(position * 100).roundToInt()}%",
            Modifier.width(44.dp),
            style = MaterialTheme.typography.labelMedium,
            color = if (position > 1.005f) Instrument.Amber else color,
        )
    }
}
