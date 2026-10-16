package dev.devaxes.composeatlas.ui.scenes

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.devaxes.composeatlas.ui.LocalAtlasMotion
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private val InboxYellow = Color(0xFFFFD968)
private val InboxInk = Color(0xFF182443)
private val InboxLilac = Color(0xFFE2DAFF)

private data class StudioMessage(
    val id: Int,
    val sender: String,
    val subject: String,
    val preview: String,
    val time: String,
)

private val studioMessages =
    listOf(
        StudioMessage(1, "Rae Chen", "The print samples are here", "Two colours. One very hard choice.", "10:42"),
        StudioMessage(2, "Noah Patel", "Lunch by the water?", "Same spot, a little later.", "09:18"),
        StudioMessage(3, "Studio Notes", "Friday, in good company", "An open studio and a fresh pot of coffee.", "Yesterday"),
        StudioMessage(4, "Ada Wilson", "One last little detail", "The blue version gets my vote.", "Yesterday"),
    )

/** Stable message IDs retain their identity through swipe removal, placement animation and undo. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun InboxScene() {
    var archived by rememberSaveable { mutableStateOf(listOf<Int>()) }
    var restoreRevision by rememberSaveable { mutableIntStateOf(0) }
    var revealMessageId by remember { mutableStateOf<Int?>(null) }
    val snackbar = remember { SnackbarHostState() }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var pendingUndo by remember { mutableStateOf<Job?>(null) }
    val motion = LocalAtlasMotion.current
    val visible = studioMessages.filterNot { it.id in archived }
    LaunchedEffect(restoreRevision) {
        val restoredId = revealMessageId ?: return@LaunchedEffect
        val restoredIndex = visible.indexOfFirst { it.id == restoredId }
        if (restoredIndex >= 0) {
            // Wait for the restored item provider to reach layout before overriding key anchoring.
            snapshotFlow { listState.layoutInfo.totalItemsCount }.first { it == visible.size }
            withFrameNanos { }
            listState.scrollToItem(restoredIndex)
        }
        revealMessageId = null
    }
    val archive: (StudioMessage) -> Unit = { message ->
        if (message.id !in archived) {
            archived = archived + message.id
            pendingUndo?.cancel()
            snackbar.currentSnackbarData?.dismiss()
            pendingUndo =
                scope.launch {
                    val result =
                        snackbar.showSnackbar(
                            message = "Archived ${message.sender}'s message",
                            actionLabel = "Undo",
                            withDismissAction = true,
                            duration = SnackbarDuration.Short,
                        )
                    if (result == SnackbarResult.ActionPerformed) {
                        revealMessageId = message.id
                        archived = archived - message.id
                        restoreRevision++
                    }
                }
        }
    }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(InboxYellow)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text("Make a little\nroom.", color = InboxInk, style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
            Text("Swipe left to archive. Undo if you change your mind.", color = InboxInk, style = MaterialTheme.typography.bodyMedium)
        }
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Your inbox", Modifier.weight(1f), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(
                "${visible.size} ${if (visible.size == 1) "message" else "messages"}",
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelLarge,
            )
        }
        // The finite viewport makes a lazy list safe inside the shell's scrolling lesson page.
        // At large font scales it grows, and rows remain individually scrollable without clipping.
        val listHeight = if (LocalDensity.current.fontScale > 1.3f) 440.dp else 300.dp
        Box(Modifier.fillMaxWidth()) {
            LazyColumn(
                Modifier.fillMaxWidth().height(if (visible.isEmpty()) 180.dp else listHeight).testTag("inbox-list"),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                items(visible, key = { it.id }) { message ->
                    // Undo must create a settled swipe state instead of reviving a saved dismissed value.
                    val dismiss = key(restoreRevision) { rememberSwipeToDismissBoxState() }
                    LaunchedEffect(dismiss.currentValue) {
                        if (dismiss.currentValue == SwipeToDismissBoxValue.EndToStart) archive(message)
                    }
                    SwipeToDismissBox(
                        state = dismiss,
                        enableDismissFromStartToEnd = false,
                        modifier =
                            Modifier
                                .animateItem(
                                    fadeInSpec = motion.tween(200),
                                    placementSpec = motion.spring(),
                                    fadeOutSpec = motion.tween(160),
                                ).clip(RoundedCornerShape(16.dp))
                                .semantics {
                                    customActions =
                                        listOf(
                                            CustomAccessibilityAction("Archive message from ${message.sender}") {
                                                archive(message)
                                                true
                                            },
                                        )
                                },
                        backgroundContent = {
                            Box(
                                Modifier.fillMaxSize().background(InboxInk).padding(horizontal = 20.dp),
                                contentAlignment = Alignment.CenterEnd,
                            ) {
                                Text("Archive", color = Color.White, style = MaterialTheme.typography.labelLarge)
                            }
                        },
                    ) {
                        MessageRow(message, onArchive = { archive(message) })
                    }
                }
                if (visible.isEmpty()) {
                    item(key = "empty-inbox") {
                        Column(
                            Modifier.fillMaxWidth().padding(vertical = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text("Room to breathe.", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            Text("All four messages are archived.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            TextButton(onClick = {
                                pendingUndo?.cancel()
                                snackbar.currentSnackbarData?.dismiss()
                                revealMessageId = studioMessages.first().id
                                archived = emptyList()
                                restoreRevision++
                            }) { Text("Bring them back") }
                        }
                    }
                }
            }
            SnackbarHost(snackbar, Modifier.align(Alignment.BottomCenter).testTag("inbox-undo"))
        }
        if (archived.isNotEmpty() && visible.isNotEmpty()) {
            TextButton(onClick = {
                pendingUndo?.cancel()
                snackbar.currentSnackbarData?.dismiss()
                revealMessageId = studioMessages.first().id
                archived = emptyList()
                restoreRevision++
            }) { Text("Restore all messages") }
        }
        Text(
            "A fictional inbox. Nothing is sent or deleted.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun MessageRow(
    message: StudioMessage,
    onArchive: () -> Unit,
) {
    val largeText = LocalDensity.current.fontScale > 1.3f
    Row(
        Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surfaceContainerLow,
            ).heightIn(min = 96.dp)
            .padding(start = 10.dp, top = 10.dp, bottom = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            Modifier.size(36.dp).background(
                if (message.id % 2 ==
                    0
                ) {
                    InboxYellow
                } else {
                    InboxLilac
                },
                CircleShape,
            ),
            contentAlignment = Alignment.Center,
        ) {
            Text(message.sender.take(1), color = InboxInk, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(message.sender, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Text(message.subject, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            Text(message.preview, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(message.time, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (largeText) {
                TextButton(onArchive, modifier = Modifier.heightIn(min = 48.dp).testTag("inbox-archive-${message.id}")) {
                    Text("Archive", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
        if (!largeText) {
            TextButton(onArchive, modifier = Modifier.heightIn(min = 48.dp).testTag("inbox-archive-${message.id}")) {
                Text("Archive", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
internal fun InboxPreview(modifier: Modifier = Modifier) {
    Canvas(modifier.clip(RoundedCornerShape(20.dp)).background(InboxYellow)) {
        val unit = size.minDimension
        val width = unit * 0.64f
        val height = unit * 0.43f
        val origin = Offset(center.x - width / 2, center.y - height / 2)
        drawRoundRect(
            Color(0xFFFFFCF1),
            origin,
            Size(width, height),
            androidx.compose.ui.geometry
                .CornerRadius(unit * 0.04f),
        )
        drawLine(
            InboxInk,
            origin + Offset(width * 0.06f, height * 0.10f),
            origin + Offset(width * 0.50f, height * 0.58f),
            unit * 0.025f,
            StrokeCap.Round,
        )
        drawLine(
            InboxInk,
            origin + Offset(width * 0.50f, height * 0.58f),
            origin + Offset(width * 0.94f, height * 0.10f),
            unit * 0.025f,
            StrokeCap.Round,
        )
        drawRoundRect(
            InboxInk,
            origin,
            Size(width, height),
            androidx.compose.ui.geometry
                .CornerRadius(unit * 0.04f),
            style =
                Stroke(unit * 0.025f),
        )
    }
}
