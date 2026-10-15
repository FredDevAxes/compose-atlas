package dev.devaxes.composeatlas.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.devaxes.composeatlas.data.GalleryObject
import dev.devaxes.composeatlas.data.filterObjects
import dev.devaxes.composeatlas.data.galleryObjects

@Composable
internal fun CollectionExperience(onExit: () -> Unit) {
    var query by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("All") }
    var favorites by rememberSaveable { mutableStateOf(emptyList<Int>()) }
    var favoritesOnly by rememberSaveable { mutableStateOf(false) }
    var reverse by rememberSaveable { mutableStateOf(false) }
    var grid by rememberSaveable { mutableStateOf(true) }
    var selected by rememberSaveable { mutableStateOf<Int?>(null) }
    val objects = filterObjects(query, category, favoritesOnly, favorites.toSet(), reverse)
    val toggle: (Int) -> Unit = { id -> favorites = if (id in favorites) favorites - id else favorites + id }
    ExperimentFrame(
        "Object collection",
        "Twelve fictional objects",
        onExit,
        scroll = false,
        explanation = {
            Lesson(
                "Identity survives movement",
                "LazyColumn and LazyVerticalGrid use each object’s stable ID as a key. animateItem moves existing items when the order changes and animates insertions and removals after filtering.",
            )
            Lesson(
                "State belongs to the object",
                "Favorites are saved by ID outside the item composition. An item keeps its favorite state when scrolled away, filtered out, moved or shown in another layout.",
            )
            Lesson(
                "Two layouts, one dataset",
                "AnimatedContent switches list and grid. GridCells.Adaptive selects the column count from available width. FlowRow wraps category chips when text or window size grows.",
            )
            Lesson(
                "Try it",
                "Save Arc lamp, reverse the order, switch to List, then show Saved only. Search for a nonexistent object to see the empty state. Open Details for a native dialog.",
            )
        },
    ) {
        // A single lazy container keeps controls reachable with large text and short windows.
        AnimatedContent(targetState = grid, modifier = Modifier.weight(1f), label = "Collection layout") { asGrid ->
            if (asGrid) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(144.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.testTag("object-grid"),
                ) {
                    item(span = {
                        androidx.compose.foundation.lazy.grid
                            .GridItemSpan(maxLineSpan)
                    }) {
                        CollectionFilters(query, { query = it }, category, { category = it }, favoritesOnly, {
                            favoritesOnly =
                                !favoritesOnly
                        }, reverse, { reverse = !reverse }, grid, { grid = it }, objects.size)
                    }
                    if (objects.isEmpty()) {
                        item(span = {
                            androidx.compose.foundation.lazy.grid
                                .GridItemSpan(maxLineSpan)
                        }) {
                            EmptyCollection {
                                query = ""
                                category = "All"
                                favoritesOnly = false
                            }
                        }
                    }
                    items(objects, key = { it.id }) { item ->
                        ObjectTile(item, item.id in favorites, { toggle(item.id) }, { selected = item.id }, Modifier.animateItem())
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.testTag("object-list")) {
                    item {
                        CollectionFilters(query, { query = it }, category, { category = it }, favoritesOnly, {
                            favoritesOnly =
                                !favoritesOnly
                        }, reverse, { reverse = !reverse }, grid, { grid = it }, objects.size)
                    }
                    if (objects.isEmpty()) {
                        item {
                            EmptyCollection {
                                query = ""
                                category = "All"
                                favoritesOnly = false
                            }
                        }
                    }
                    items(objects, key = { it.id }) { item ->
                        ObjectTile(
                            item,
                            item.id in favorites,
                            { toggle(item.id) },
                            { selected = item.id },
                            Modifier.animateItem(),
                            compact = true,
                        )
                    }
                }
            }
        }
    }
    selected?.let { id ->
        val item = galleryObjects.first { it.id == id }
        AlertDialog(
            onDismissRequest = { selected = null },
            title = { Text(item.title) },
            text = {
                Text(
                    "A fictional ${item.category.lowercase()} object, drawn with Canvas. Its stable key is object ${item.id}. Saved state follows this identity across layouts.",
                )
            },
            confirmButton = { TextButton(onClick = { selected = null }) { Text("Close details") } },
        )
    }
}

@Composable
private fun CollectionFilters(
    query: String,
    onQuery: (String) -> Unit,
    category: String,
    onCategory: (String) -> Unit,
    favoritesOnly: Boolean,
    onFavorites: () -> Unit,
    reverse: Boolean,
    onReverse: () -> Unit,
    grid: Boolean,
    onGrid: (Boolean) -> Unit,
    count: Int,
) {
    val focus = LocalFocusManager.current
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            query,
            onQuery,
            label = {
                Text("Search objects")
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions =
                KeyboardActions(onDone = {
                    focus.clearFocus()
                }),
            modifier = Modifier.fillMaxWidth().testTag("object-search"),
        )
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("All", "Light", "Clay", "Glass").forEach {
                FilterChip(selected = category == it, onClick = { onCategory(it) }, label = { Text(it) })
            }
        }
        ChoiceRow(listOf("Grid", "List"), if (grid) 0 else 1, { onGrid(it == 0) })
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = favoritesOnly, onClick = onFavorites, label = { Text("Saved only") })
            TextButton(onClick = onReverse, modifier = Modifier.testTag("reverse-objects")) {
                Text(if (reverse) "Order: reversed" else "Order: original")
            }
        }
        Text(if (count == 1) "1 object" else "$count objects", color = Instrument.Muted, modifier = Modifier.testTag("object-count"))
    }
}

@Composable
private fun EmptyCollection(onReset: () -> Unit) {
    Column {
        Text("No objects match", style = MaterialTheme.typography.titleMedium)
        TextButton(onClick = onReset) { Text("Clear filters") }
    }
}

@Composable
private fun ObjectTile(
    item: GalleryObject,
    saved: Boolean,
    onSave: () -> Unit,
    onDetails: () -> Unit,
    modifier: Modifier,
    compact: Boolean = false,
) {
    Column(modifier.testTag("object-${item.id}")) {
        LabSurface {
            if (!compact) {
                Canvas(Modifier.fillMaxWidth().height(72.dp)) {
                    val color =
                        when (item.category) {
                            "Light" -> Instrument.Lime
                            "Glass" -> Instrument.Cyan
                            else -> Instrument.Amber
                        }
                    val radius = size.height * 0.32f
                    drawCircle(color.copy(alpha = 0.12f), radius * 1.5f)
                    drawCircle(color, radius, style = Stroke(3.dp.toPx()))
                    drawLine(color, Offset(center.x - radius, center.y), Offset(center.x + radius, center.y), 2.dp.toPx())
                }
            }
            Text(item.title, style = MaterialTheme.typography.titleMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text(item.category, color = Instrument.Muted, style = MaterialTheme.typography.bodySmall)
            TextButton(
                onClick = onSave,
                modifier =
                    Modifier.fillMaxWidth().testTag("save-${item.id}").semantics {
                        contentDescription = "Save ${item.title}"
                        stateDescription = if (saved) "Saved" else "Not saved"
                    },
            ) { Text(if (saved) "Saved" else "Save") }
            TextButton(onClick = onDetails, modifier = Modifier.fillMaxWidth().testTag("details-${item.id}")) { Text("Details") }
        }
    }
}
