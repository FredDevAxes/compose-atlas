package dev.devaxes.composeatlas.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private data class Experiment(
    val route: String,
    val title: String,
    val description: String,
    val features: String,
)

private val experiments =
    listOf(
        Experiment(
            "player",
            "Quiet frequency",
            "A fictional player. Open it, seek, switch tracks.",
            "AnimatedContent · gestures · effects",
        ),
        Experiment(
            "temperature",
            "Temperature study",
            "Inspect two series. Compare Tween and Spring.",
            "Canvas · pointer input · animation clock",
        ),
        Experiment(
            "collection",
            "Object collection",
            "Filter, reorder and collect small discoveries.",
            "Lazy layouts · stable keys · item motion",
        ),
        Experiment(
            "form",
            "Session builder",
            "Explore a form from first focus to confirmation.",
            "Text input · keyboard · validation",
        ),
        Experiment(
            "agenda",
            "Spatial agenda",
            "Place sessions on a timeline. Change its scale.",
            "Custom Layout · constraints · placement",
        ),
    )

@Composable
fun AtlasApp() {
    var route by rememberSaveable { mutableStateOf("explore") }
    val holder = rememberSaveableStateHolder()
    val exit = { route = "explore" }
    InstrumentTheme {
        Surface(Modifier.fillMaxSize(), color = Instrument.Background) {
            BackHandler(route != "explore", onBack = exit)
            holder.SaveableStateProvider(route) {
                when (route) {
                    "temperature" -> TemperatureExperience(exit)
                    "player" -> PlayerExperience(exit)
                    "collection" -> CollectionExperience(exit)
                    "form" -> FormExperience(exit)
                    "agenda" -> AgendaExperience(exit)
                    else -> ExperimentGallery { route = it }
                }
            }
        }
    }
}

@Composable
private fun ExperimentGallery(onOpen: (String) -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text("Compose Atlas", style = MaterialTheme.typography.titleLarge)
        Text("Explore", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.SemiBold)
        Text("Five interfaces. One place to take them apart.", color = Instrument.Muted)
        experiments.forEach { experiment ->
            Surface(
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Instrument.Outline),
                color = Instrument.Surface,
            ) {
                Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(experiment.title, style = MaterialTheme.typography.headlineSmall)
                    Text(experiment.description, color = Instrument.Muted)
                    Text(experiment.features, color = Instrument.Cyan, style = MaterialTheme.typography.bodySmall)
                    Button(
                        onClick = { onOpen(experiment.route) },
                        shape = RoundedCornerShape(12.dp),
                        modifier =
                            Modifier.fillMaxWidth().testTag(
                                if (experiment.route ==
                                    "temperature"
                                ) {
                                    "open-experiment"
                                } else {
                                    "open-${experiment.route}"
                                },
                            ),
                    ) { Text("Open experiment") }
                }
            }
        }
        Text(
            "Synthetic content. Fully offline. More capabilities will join the atlas over time.",
            color = Instrument.Muted,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
internal fun ExperimentFrame(
    title: String,
    subtitle: String,
    onExit: () -> Unit,
    explanation: @Composable ColumnScope.() -> Unit,
    scroll: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    var understand by rememberSaveable { mutableStateOf(false) }
    val experimentScroll = rememberScrollState()
    val explanationScroll = rememberScrollState()
    Column(Modifier.fillMaxSize().safeDrawingPadding().imePadding()) {
        Row(Modifier.fillMaxWidth().padding(end = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onExit, modifier = Modifier.testTag("back-to-explore")) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back to Explore")
            }
            Column(Modifier.weight(1f).padding(vertical = 8.dp)) {
                Text(title, style = MaterialTheme.typography.titleLarge)
                Text(subtitle, color = Instrument.Muted, style = MaterialTheme.typography.bodySmall)
            }
        }
        ChoiceRow(listOf("Experiment", "Understand"), if (understand) 1 else 0, {
            understand = it == 1
        }, Modifier.align(Alignment.CenterHorizontally).widthIn(max = 600.dp).padding(horizontal = 16.dp, vertical = 8.dp))
        Column(
            Modifier
                .weight(1f)
                .align(Alignment.CenterHorizontally)
                .widthIn(max = 600.dp)
                .fillMaxWidth()
                .then(
                    if (scroll ||
                        understand
                    ) {
                        Modifier.verticalScroll(if (understand) explanationScroll else experimentScroll)
                    } else {
                        Modifier
                    },
                ).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (understand) explanation() else content()
        }
    }
}

@Composable
internal fun Lesson(
    title: String,
    body: String,
) {
    Text(title, style = MaterialTheme.typography.titleMedium, color = Instrument.Lime)
    Text(body, style = MaterialTheme.typography.bodyMedium, color = Instrument.Muted)
}

@Composable
internal fun LabSurface(content: @Composable ColumnScope.() -> Unit) {
    Surface(shape = RoundedCornerShape(16.dp), color = Instrument.Surface, border = BorderStroke(1.dp, Instrument.Outline)) {
        Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp), content = content)
    }
}
