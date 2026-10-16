package dev.devaxes.composeatlas.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.devaxes.composeatlas.data.AtlasScene
import dev.devaxes.composeatlas.ui.scenes.AgendaScene
import dev.devaxes.composeatlas.ui.scenes.DeliveryPreview
import dev.devaxes.composeatlas.ui.scenes.DeliveryScene
import dev.devaxes.composeatlas.ui.scenes.EnergyScene
import dev.devaxes.composeatlas.ui.scenes.InboxPreview
import dev.devaxes.composeatlas.ui.scenes.InboxScene
import dev.devaxes.composeatlas.ui.scenes.MusicPreview
import dev.devaxes.composeatlas.ui.scenes.MusicScene
import dev.devaxes.composeatlas.ui.scenes.PhotoPreview
import dev.devaxes.composeatlas.ui.scenes.PhotoScene
import dev.devaxes.composeatlas.ui.scenes.ShopPreview
import dev.devaxes.composeatlas.ui.scenes.ShopScene
import dev.devaxes.composeatlas.ui.scenes.TicketPreview
import dev.devaxes.composeatlas.ui.scenes.TicketScene

/** A small saved back stack is sufficient for this offline gallery's one-level routes. */
@Composable
fun AtlasApp() {
    var route by rememberSaveable { mutableStateOf("home") }
    var previous by rememberSaveable { mutableStateOf("home") }
    var motionName by rememberSaveable { mutableStateOf(MotionMode.Normal.name) }
    val holder = rememberSaveableStateHolder()
    val motion = AtlasMotion(MotionMode.valueOf(motionName), rememberSystemMotionEnabled())
    val back = { route = if (route == "settings") previous else "home" }
    AtlasTheme {
        CompositionLocalProvider(LocalAtlasMotion provides motion) {
            Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                BackHandler(route != "home", onBack = back)
                holder.SaveableStateProvider(route) {
                    when (route) {
                        "home" ->
                            GalleryHome(
                                onOpen = { route = it.name },
                                onSettings = {
                                    previous = route
                                    route = "settings"
                                },
                            )
                        "settings" -> MotionSettings(motion.mode, { motionName = it.name }, back)
                        else ->
                            SceneScreen(
                                scene = AtlasScene.valueOf(route),
                                onBack = back,
                                onSettings = {
                                    previous = route
                                    route = "settings"
                                },
                            )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GalleryHome(
    onOpen: (AtlasScene) -> Unit,
    onSettings: () -> Unit,
) {
    Scaffold(topBar = {
        TopAppBar(title = { Text("Compose Atlas", fontWeight = FontWeight.Bold) }, actions = {
            IconButton(onSettings, Modifier.testTag("open-settings")) { Icon(Icons.Default.Settings, "Motion settings") }
        })
    }) { insets ->
        BoxWithConstraints(Modifier.fillMaxSize().padding(insets)) {
            val columns =
                if (maxWidth >= 840.dp) {
                    3
                } else if (maxWidth < 340.dp || LocalDensity.current.fontScale > 1.35f) {
                    1
                } else {
                    2
                }
            Column(
                Modifier
                    .align(Alignment.TopCenter)
                    .widthIn(max = 1000.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Make it move.", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
                    Text("Eight little moments. Try one.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                SceneTile(AtlasScene.Music, featured = true, modifier = Modifier.fillMaxWidth(), onOpen)
                Text(
                    "Try something else",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier =
                        Modifier
                            .semantics {
                                heading()
                            },
                )
                AtlasScene.entries.drop(1).chunked(columns).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                        row.forEach { scene -> SceneTile(scene, false, Modifier.weight(1f), onOpen) }
                        repeat(columns - row.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
                Text(
                    "Made to explore. All examples run offline with fictional data.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun SceneTile(
    scene: AtlasScene,
    featured: Boolean,
    modifier: Modifier,
    onOpen: (AtlasScene) -> Unit,
) {
    val color = sceneColor(scene)
    Surface(
        onClick = { onOpen(scene) },
        modifier = modifier.testTag("open-${scene.name.lowercase()}"),
        shape = RoundedCornerShape(20.dp),
        color = color,
        contentColor = Color(0xFF202436),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            SceneArtwork(scene, Modifier.fillMaxWidth().height(if (featured) 150.dp else 112.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    scene.title,
                    Modifier.weight(1f),
                    style = if (featured) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Icon(Icons.AutoMirrored.Filled.ArrowForward, null, Modifier.size(20.dp))
            }
            if (featured) Text(scene.invitation, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

/** Thumbnails depict the actual interaction objects; they are original runtime geometry. */
@Composable
private fun SceneArtwork(
    scene: AtlasScene,
    modifier: Modifier,
) {
    when (scene) {
        AtlasScene.Music -> MusicPreview(modifier)
        AtlasScene.Shop -> ShopPreview(modifier)
        AtlasScene.Delivery -> DeliveryPreview(modifier)
        AtlasScene.Ticket -> TicketPreview(modifier)
        AtlasScene.Photo -> PhotoPreview(modifier)
        AtlasScene.Inbox -> InboxPreview(modifier)
        else ->
            Canvas(modifier) {
                val w = size.width
                val h = size.height
                if (scene == AtlasScene.Energy) {
                    listOf(.24f, .44f, .32f, .75f, .53f).forEachIndexed { i, value ->
                        drawRoundRect(
                            Color(0xFF27845D),
                            Offset(w * (.1f + i * .17f), h * (.9f - value)),
                            Size(w * .12f, h * value),
                            CornerRadius(4.dp.toPx()),
                        )
                    }
                } else {
                    repeat(4) { i ->
                        drawLine(
                            Color(0xFFB2ABCE),
                            Offset(w * .08f, h * (.15f + i * .23f)),
                            Offset(w * .92f, h * (.15f + i * .23f)),
                            1.dp.toPx(),
                        )
                    }
                    drawRoundRect(Color(0xFF7862BA), Offset(w * .16f, h * .16f), Size(w * .37f, h * .43f), CornerRadius(6.dp.toPx()))
                    drawRoundRect(Color(0xFFF3A58A), Offset(w * .57f, h * .33f), Size(w * .3f, h * .42f), CornerRadius(6.dp.toPx()))
                }
            }
    }
}

internal fun sceneColor(scene: AtlasScene): Color =
    when (scene) {
        AtlasScene.Music -> Color(0xFFFF9A86)
        AtlasScene.Shop -> Color(0xFFE1DBFF)
        AtlasScene.Delivery -> Color(0xFFC7E4FC)
        AtlasScene.Energy -> Color(0xFFC6EEDC)
        AtlasScene.Ticket -> Color(0xFFFFEAB0)
        AtlasScene.Photo -> Color(0xFFE7DBEF)
        AtlasScene.Inbox -> Color(0xFFFFEDBE)
        AtlasScene.Agenda -> Color(0xFFE1DEF4)
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SceneScreen(
    scene: AtlasScene,
    onBack: () -> Unit,
    onSettings: () -> Unit,
) {
    var explain by rememberSaveable { mutableStateOf(false) }
    val motion = LocalAtlasMotion.current
    Scaffold(topBar = {
        TopAppBar(title = { Text(scene.title, style = MaterialTheme.typography.titleLarge) }, navigationIcon = {
            IconButton(onBack, Modifier.testTag("back-to-explore")) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back to gallery") }
        }, actions = { IconButton(onSettings) { Icon(Icons.Default.Settings, "Motion settings") } })
    }) { insets ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(insets)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(Modifier.widthIn(max = 640.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                when (scene) {
                    AtlasScene.Music -> MusicScene()
                    AtlasScene.Shop -> ShopScene()
                    AtlasScene.Delivery -> DeliveryScene()
                    AtlasScene.Energy -> EnergyScene()
                    AtlasScene.Ticket -> TicketScene()
                    AtlasScene.Photo -> PhotoScene()
                    AtlasScene.Inbox -> InboxScene()
                    AtlasScene.Agenda -> AgendaScene()
                }
                HorizontalDivider(Modifier.padding(top = 8.dp))
                TextButton(onClick = { explain = !explain }, modifier = Modifier.testTag("how-it-works").fillMaxWidth()) {
                    Text(if (explain) "Close explanation" else "How it works")
                }
                AnimatedVisibility(
                    explain,
                    enter = expandVertically(motion.tween()) + fadeIn(motion.tween()),
                    exit =
                        shrinkVertically(motion.tween()) + fadeOut(motion.tween()),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.testTag("explanation")) {
                        Text(scene.mechanism, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(scene.explanation)
                        Text("In a real app", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(scene.production)
                        Text(scene.boundary, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                        Text(
                            "Source: ui/scenes/${scene.source}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MotionSettings(
    mode: MotionMode,
    onSelect: (MotionMode) -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(topBar = {
        TopAppBar(title = { Text("Motion") }, navigationIcon = {
            IconButton(onBack, Modifier.testTag("settings-back")) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
        })
    }) { insets ->
        Box(Modifier.fillMaxSize().padding(insets), contentAlignment = Alignment.TopCenter) {
            Column(
                Modifier
                    .widthIn(max = 640.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text("Watch at your pace.", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text("One setting for every experience.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                MotionMode.entries.forEach { item ->
                    Surface(
                        onClick = { onSelect(item) },
                        modifier =
                            Modifier.testTag("motion-${item.name.lowercase()}").semantics {
                                selected = mode == item
                                role =
                                    Role.RadioButton
                            },
                        shape = RoundedCornerShape(16.dp),
                        color =
                            if (mode ==
                                item
                            ) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceContainer
                            },
                    ) {
                        Row(Modifier.fillMaxWidth().padding(12.dp).heightIn(min = 48.dp), verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = mode == item, onClick = null)
                            Text(item.label, Modifier.padding(start = 12.dp), style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
                Text(
                    "Slow motion stretches authored transitions to about three times their normal duration. Off shows the result immediately. Android's animation settings still apply.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    "All scenes use fictional, local data. Nothing is sent or purchased.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
