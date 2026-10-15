package dev.devaxes.composeatlas.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.devaxes.composeatlas.data.ChartMath
import dev.devaxes.composeatlas.data.ExperimentState
import dev.devaxes.composeatlas.data.MotionMode
import dev.devaxes.composeatlas.data.Period
import dev.devaxes.composeatlas.data.Scenario
import kotlinx.coroutines.launch

private val experimentSaver =
    listSaver<ExperimentState, Any>(
        save = {
            listOf(
                it.period.ordinal,
                it.scenario.ordinal,
                it.selected,
                it.studio,
                it.gallery,
                it.mode.ordinal,
                it.duration,
                it.damping,
                it.stiffness,
                it.animate,
            )
        },
        restore = {
            ExperimentState(
                Period.entries[it[0] as Int],
                Scenario.entries[it[1] as Int],
                it[2] as Int,
                it[3] as Boolean,
                it[4] as Boolean,
                MotionMode.entries[it[5] as Int],
                it[6] as Int,
                it[7] as Float,
                it[8] as Float,
                it[9] as Boolean,
            )
        },
    )

@Composable
fun AtlasApp() {
    var detail by rememberSaveable { mutableStateOf(false) }
    var state by rememberSaveable(stateSaver = experimentSaver) { mutableStateOf(ExperimentState()) }
    var panelOpen by rememberSaveable { mutableStateOf(true) }
    var understand by rememberSaveable { mutableStateOf(false) }
    val progress = remember { Animatable(1f) }
    var replay by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(detail, replay, state.period, state.scenario, state.animate) {
        if (detail && state.animate && state.data.samples.isNotEmpty()) {
            progress.snapTo(0f)
            progress.animateTo(1f, if (state.mode == MotionMode.TWEEN) tween(state.duration) else spring(state.damping, state.stiffness))
        } else {
            progress.snapTo(1f)
        }
    }
    val inspect = {
        scope.launch {
            progress.stop()
            progress.snapTo(1f)
        }
        Unit
    }
    BackHandler(detail) {
        if (panelOpen) panelOpen = false else detail = false
    }
    InstrumentTheme {
        Surface(color = Instrument.Background, modifier = Modifier.fillMaxSize()) {
            if (!detail) {
                ExploreScreen {
                    panelOpen = true
                    detail = true
                }
            } else {
                Column(Modifier.safeDrawingPadding().fillMaxSize()) {
                    Row(Modifier.fillMaxWidth().padding(start = 4.dp, end = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { detail = false }, modifier = Modifier.testTag("back-to-explore")) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back to Explore")
                        }
                        Column(Modifier.weight(1f).padding(vertical = 8.dp)) {
                            Text("Temperature study", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Medium)
                            Text("Synthetic data", style = MaterialTheme.typography.bodySmall, color = Instrument.Muted)
                        }
                    }
                    BoxWithConstraints(Modifier.weight(1f)) {
                        val compactHeight = maxHeight < 600.dp || LocalDensity.current.fontScale > 1.25f
                        val chartHeight = if (compactHeight) 240.dp else (maxHeight * 0.31f).coerceIn(210.dp, 290.dp)
                        Column(
                            Modifier.fillMaxSize().then(if (compactHeight) Modifier.verticalScroll(rememberScrollState()) else Modifier),
                        ) {
                            ChoiceRow(
                                Period.entries.map { it.label },
                                state.period.ordinal,
                                {
                                    state =
                                        state.changePeriod(Period.entries[it])
                                },
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            )
                            ChartCard(
                                state,
                                progress.value,
                                { state = state.select(it) },
                                inspect,
                                Modifier.padding(horizontal = 10.dp).height(chartHeight),
                            )
                            if (panelOpen) {
                                ControlPanel(
                                    state = state,
                                    understand = understand,
                                    onUnderstand = { understand = it },
                                    onState = { state = it },
                                    onClose = { panelOpen = false },
                                    onReplay = { replay++ },
                                    onReset = {
                                        state = ExperimentState()
                                        replay++
                                    },
                                    onInspect = inspect,
                                    modifier =
                                        if (compactHeight) {
                                            Modifier.padding(
                                                top = 12.dp,
                                            )
                                        } else {
                                            Modifier.weight(1f).padding(top = 12.dp)
                                        },
                                    scroll = !compactHeight,
                                )
                            } else {
                                Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    PointControls(state, {
                                        inspect()
                                        state = state.select(it)
                                    })
                                    Button(
                                        onClick = { panelOpen = true },
                                        modifier = Modifier.fillMaxWidth(),
                                    ) { Text("Experiment controls") }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExploreScreen(onOpen: () -> Unit) {
    Column(
        Modifier
            .safeDrawingPadding()
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Text("Compose Atlas", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Medium)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Explore", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.SemiBold)
            Text("Try an interface. Explore how it works.", style = MaterialTheme.typography.bodyLarge, color = Instrument.Muted)
        }
        Card(
            colors = CardDefaults.cardColors(containerColor = Instrument.Surface),
            border = BorderStroke(1.dp, Instrument.Outline),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Temperature study", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Medium)
                Text("Inspect two series. Change the timeline.", color = Instrument.Muted)
                Box(Modifier.fillMaxWidth().height(200.dp)) {
                    TemperatureChart(ExperimentState(), interactive = false)
                }
                Button(onClick = onOpen, modifier = Modifier.fillMaxWidth().testTag("open-experiment"), shape = RoundedCornerShape(12.dp)) {
                    Text("Open experiment")
                    Spacer(Modifier.width(12.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, null, Modifier.size(18.dp))
                }
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Inside this experiment", style = MaterialTheme.typography.titleMedium)
            Text("Canvas drawing · touch inspection · controlled animation · accessible values", color = Instrument.Muted)
            Text("All values are synthetic. This lab runs offline.", color = Instrument.Muted, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun ChartCard(
    state: ExperimentState,
    progress: Float,
    onSelect: (Int) -> Unit,
    onInspect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Instrument.Background,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Instrument.Outline),
    ) {
        Column(Modifier.padding(10.dp)) {
            if (state.data.samples.isEmpty()) {
                Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("No samples to display", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Choose Normal or Reset to restore the data.",
                            color = Instrument.Muted,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            } else {
                TemperatureChart(state, modifier = Modifier.weight(1f), progress = progress, onSelect = onSelect, onInspect = onInspect)
            }
            Surface(
                color = Instrument.Surface,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().testTag("selection-readout"),
            ) {
                if (progress < 0.999f && state.data.samples.isNotEmpty()) {
                    Text("Replaying trace…", Modifier.padding(12.dp), color = Instrument.Amber, style = MaterialTheme.typography.bodySmall)
                } else {
                    Row(
                        Modifier.padding(horizontal = 10.dp, vertical = 10.dp).semantics(mergeDescendants = true) {
                            contentDescription = state.description()
                            liveRegion = LiveRegionMode.Polite
                        },
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            state.sample?.label ?: "No data",
                            Modifier.weight(0.8f),
                            color = Instrument.Amber,
                            fontFamily = FontFamily.Monospace,
                            style = MaterialTheme.typography.labelMedium,
                        )
                        if (state.studio) {
                            Text(
                                "Studio " + ChartMath.format(state.sample?.studio),
                                Modifier.weight(1f),
                                color = Instrument.Lime,
                                style = MaterialTheme.typography.labelMedium,
                            )
                        }
                        if (state.gallery) {
                            Text(
                                "Gallery " + ChartMath.format(state.sample?.gallery),
                                Modifier.weight(1f),
                                color = Instrument.Cyan,
                                style = MaterialTheme.typography.labelMedium,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PointControls(
    state: ExperimentState,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedButton(onClick = {
            onSelect(state.selected - 1)
        }, enabled = state.selected > 0 && state.sample != null, modifier = Modifier.weight(1f).testTag("previous-point")) {
            Text("Previous point", style = MaterialTheme.typography.labelMedium)
        }
        OutlinedButton(onClick = {
            onSelect(state.selected + 1)
        }, enabled = state.selected < state.data.samples.lastIndex, modifier = Modifier.weight(1f).testTag("next-point")) {
            Text("Next point", style = MaterialTheme.typography.labelMedium)
        }
    }
}
