package dev.devaxes.composeatlas.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.devaxes.composeatlas.data.ExperimentState
import dev.devaxes.composeatlas.data.MotionMode
import dev.devaxes.composeatlas.data.Scenario
import dev.devaxes.composeatlas.data.Series
import kotlin.math.roundToInt

@Composable
fun ChoiceRow(
    labels: List<String>,
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    SingleChoiceSegmentedButtonRow(modifier.fillMaxWidth()) {
        labels.forEachIndexed { index, label ->
            SegmentedButton(
                selected = index == selected,
                onClick = { onSelect(index) },
                shape = SegmentedButtonDefaults.itemShape(index, labels.size),
                icon = {},
                colors =
                    SegmentedButtonDefaults.colors(
                        activeContainerColor = Instrument.Selected,
                        activeContentColor = Instrument.Text,
                        inactiveContainerColor = Instrument.Background,
                        inactiveContentColor = Instrument.Text,
                        activeBorderColor = Instrument.Outline,
                        inactiveBorderColor = Instrument.Outline,
                    ),
            ) { Text(label, style = MaterialTheme.typography.labelLarge) }
        }
    }
}

@Composable
fun ControlPanel(
    state: ExperimentState,
    understand: Boolean,
    onUnderstand: (Boolean) -> Unit,
    onState: (ExperimentState) -> Unit,
    onClose: () -> Unit,
    onReplay: () -> Unit,
    onReset: () -> Unit,
    onInspect: () -> Unit,
    modifier: Modifier = Modifier,
    scroll: Boolean = true,
) {
    Surface(
        modifier.fillMaxWidth(),
        color = Instrument.Surface,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        border = BorderStroke(1.dp, Instrument.Outline),
    ) {
        Column(
            Modifier
                .then(
                    if (scroll) Modifier.verticalScroll(rememberScrollState()) else Modifier,
                ).testTag("controls-scroll")
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Experiment controls",
                    Modifier.weight(1f),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium,
                )
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.testTag("close-panel"),
                ) { Icon(Icons.Default.Close, "Close experiment controls") }
            }
            ChoiceRow(listOf("Adjust", "Understand"), if (understand) 1 else 0, { onUnderstand(it == 1) })
            if (understand) {
                UnderstandContent(state, onState, onInspect)
            } else {
                Text("Visible series", style = MaterialTheme.typography.labelLarge, color = Instrument.Muted)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SeriesButton(
                        "Studio",
                        state.studio,
                        state.gallery,
                        false,
                        { onState(state.toggle(Series.STUDIO)) },
                        Modifier.weight(1f),
                    )
                    SeriesButton(
                        "Gallery",
                        state.gallery,
                        state.studio,
                        true,
                        { onState(state.toggle(Series.GALLERY)) },
                        Modifier.weight(1f),
                    )
                }
                Text("Transition", style = MaterialTheme.typography.labelLarge, color = Instrument.Muted)
                ChoiceRow(listOf("Tween", "Spring"), state.mode.ordinal, { onState(state.copy(mode = MotionMode.entries[it])) })
                if (state.mode == MotionMode.TWEEN) {
                    LabeledSlider("Duration", state.duration.toString() + " ms", state.duration.toFloat(), 200f..1600f) {
                        onState(state.copy(duration = ((it / 50).roundToInt() * 50)))
                    }
                } else {
                    LabeledSlider(
                        "Damping",
                        "%.1f".format(java.util.Locale.US, state.damping),
                        state.damping,
                        0.3f..1f,
                    ) { onState(state.copy(damping = it)) }
                    LabeledSlider(
                        "Stiffness",
                        state.stiffness.roundToInt().toString(),
                        state.stiffness,
                        80f..500f,
                    ) { onState(state.copy(stiffness = it)) }
                }
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Animate changes", Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                    Switch(
                        checked = state.animate,
                        onCheckedChange = { onState(state.copy(animate = it)) },
                        modifier =
                            Modifier
                                .semantics {
                                    contentDescription =
                                        "Animate changes"
                                }.testTag("animate-switch"),
                    )
                }
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(onClick = onReset, modifier = Modifier.testTag("reset")) { Text("Reset") }
                    Button(
                        onClick = onReplay,
                        enabled = state.sample != null,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("replay"),
                    ) {
                        Icon(Icons.Default.PlayArrow, null, Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Replay transition", style = MaterialTheme.typography.labelLarge)
                    }
                }
                HorizontalDivider(color = Instrument.Outline)
                PointControls(state, {
                    onInspect()
                    onState(state.select(it))
                })
                ScenarioControls(state, onState)
            }
        }
    }
}

@Composable
private fun SeriesButton(
    label: String,
    checked: Boolean,
    canToggle: Boolean,
    dashed: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.testTag("series-" + label.lowercase()),
        enabled = canToggle || !checked,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Instrument.Outline),
    ) {
        Checkbox(checked = checked, onCheckedChange = null, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(6.dp))
        Canvas(Modifier.width(20.dp).height(10.dp)) {
            drawLine(
                if (dashed) Instrument.Cyan else Instrument.Lime,
                Offset(0f, size.height / 2),
                Offset(size.width, size.height / 2),
                2.dp.toPx(),
                pathEffect = if (dashed) PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 3.dp.toPx())) else null,
            )
        }
        Spacer(Modifier.width(6.dp))
        Text(label, color = Instrument.Text, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun LabeledSlider(
    label: String,
    valueLabel: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onChange: (Float) -> Unit,
) {
    Column {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = Instrument.Muted)
            Text(valueLabel, style = MaterialTheme.typography.bodySmall)
        }
        Slider(
            value = value,
            onValueChange = onChange,
            valueRange = range,
            colors = SliderDefaults.colors(inactiveTrackColor = Instrument.Outline),
            modifier =
                Modifier.height(48.dp).semantics {
                    contentDescription =
                        label
                },
        )
    }
}

@Composable
private fun ScenarioControls(
    state: ExperimentState,
    onState: (ExperimentState) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Try a scenario", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Scenario.entries.take(2).forEach { scenario ->
                OutlinedButton(
                    onClick = { onState(state.changeScenario(scenario)) },
                    modifier =
                        Modifier.weight(1f).testTag("scenario-" + scenario.name.lowercase()),
                    border =
                        BorderStroke(
                            1.dp,
                            if (scenario ==
                                state.scenario
                            ) {
                                Instrument.Lime
                            } else {
                                Instrument.Outline
                            },
                        ),
                ) { Text(scenario.label) }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Scenario.entries.drop(2).forEach { scenario ->
                OutlinedButton(
                    onClick = { onState(state.changeScenario(scenario)) },
                    modifier =
                        Modifier.weight(1f).testTag("scenario-" + scenario.name.lowercase()),
                    border =
                        BorderStroke(
                            1.dp,
                            if (scenario ==
                                state.scenario
                            ) {
                                Instrument.Lime
                            } else {
                                Instrument.Outline
                            },
                        ),
                ) { Text(scenario.label) }
            }
        }
        Text(state.data.description, style = MaterialTheme.typography.bodySmall, color = Instrument.Muted)
    }
}

@Composable
private fun UnderstandContent(
    state: ExperimentState,
    onState: (ExperimentState) -> Unit,
    onInspect: () -> Unit,
) {
    val uri = LocalUriHandler.current
    Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(top = 8.dp)) {
        PointControls(state, {
            onInspect()
            onState(state.select(it))
        })
        Text("From touch to value", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Medium)
        Explanation("Find the nearest time", "Touch maps to a horizontal fraction, then to the closest supplied sample.")
        Explanation("Read both series", "The cursor and values share the same sample index. A gap stays missing, never zero.")
        Explanation("Move the cursor", "Drag the chart or use Previous point and Next point. Values remain available as text.")
        HorizontalDivider(color = Instrument.Outline)
        Text("What moves?", style = MaterialTheme.typography.titleMedium)
        Text(
            "Tween or Spring reveals the existing trace. Values do not morph into invented measurements. During replay the readout waits; inspecting completes the reveal.",
            color = Instrument.Muted,
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            "Canvas · drawPath · pointerInput · Animatable · rememberSaveable",
            color = Instrument.Cyan,
            style = MaterialTheme.typography.bodySmall,
        )
        ScenarioControls(state, onState)
        OutlinedButton(onClick = {
            uri.openUri(
                "https://github.com/FredDevAxes/compose-atlas/blob/main/app/src/main/java/dev/devaxes/composeatlas/ui/TemperatureChart.kt",
            )
        }, modifier = Modifier.fillMaxWidth()) { Text("View source") }
    }
}

@Composable
private fun Explanation(
    title: String,
    body: String,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(title, style = MaterialTheme.typography.titleSmall)
        Text(body, style = MaterialTheme.typography.bodyMedium, color = Instrument.Muted)
    }
}
