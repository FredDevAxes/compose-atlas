package dev.devaxes.composeatlas.ui.scenes

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.devaxes.composeatlas.ui.LocalAtlasMotion

private val ShopLavender = Color(0xFFD6CBFF)
private val ShopPeach = Color(0xFFFFC8AF)
private val ShopInk = Color(0xFF182443)
private val ShopIvory = Color(0xFFFFF5DC)

private data class ShopObject(
    val name: String,
    val price: Int,
    val description: String,
)

private val shopObjects =
    listOf(
        ShopObject("Arc lamp", 89, "A warm little pool of light. Sculptural shade, satisfyingly simple shape."),
        ShopObject("Loop stool", 65, "A playful perch for a plant, a book, or you. Small footprint, big personality."),
    )

/** A genuine shared element connects each object in the collection to its detail view. */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun ShopScene() {
    var selected by rememberSaveable { mutableStateOf<Int?>(null) }
    var quantity by rememberSaveable { mutableIntStateOf(1) }
    var bagCount by rememberSaveable { mutableIntStateOf(0) }
    var bagTotal by rememberSaveable { mutableIntStateOf(0) }
    val motion = LocalAtlasMotion.current
    BackHandler(enabled = selected != null) { selected = null }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Good objects.", Modifier.weight(1f), style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
            Text(
                "Bag ($bagCount)",
                style = MaterialTheme.typography.labelLarge,
                modifier =
                    Modifier.semantics {
                        liveRegion =
                            LiveRegionMode.Polite
                    },
            )
        }
        SharedTransitionLayout {
            AnimatedContent(
                targetState = selected,
                transitionSpec = {
                    (fadeIn(motion.tween(280)) togetherWith fadeOut(motion.tween(180))) using
                        SizeTransform(sizeAnimationSpec = { _, _ -> motion.spring() })
                },
                label = "Object collection to detail",
            ) { objectIndex ->
                if (objectIndex == null) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("Small things. A lot of character.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            shopObjects.forEachIndexed { index, item ->
                                Column(
                                    Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(20.dp))
                                        .clickable(role = Role.Button, onClickLabel = "View ${item.name}") {
                                            quantity = 1
                                            selected = index
                                        }.testTag("shop-object-$index"),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    ProductArtwork(
                                        index,
                                        Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(0.82f)
                                            .sharedElement(
                                                rememberSharedContentState("object-$index"),
                                                animatedVisibilityScope = this@AnimatedContent,
                                                boundsTransform = { _, _ -> motion.spring() },
                                            ),
                                    )
                                    Text(item.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                    Text(
                                        "€${item.price}  ·  View object",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }
                        Text("Pick an object. Watch it find its place.", style = MaterialTheme.typography.bodyMedium)
                    }
                } else {
                    val item = shopObjects[objectIndex]
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        TextButton(onClick = { selected = null }, modifier = Modifier.testTag("shop-back")) { Text("Back to objects") }
                        ProductArtwork(
                            objectIndex,
                            Modifier
                                .fillMaxWidth()
                                .height(205.dp)
                                .sharedElement(
                                    rememberSharedContentState("object-$objectIndex"),
                                    animatedVisibilityScope = this@AnimatedContent,
                                    boundsTransform = { _, _ -> motion.spring() },
                                ),
                        )
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                item.name,
                                Modifier.weight(1f),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                            )
                            Text("€${item.price}", style = MaterialTheme.typography.titleLarge)
                        }
                        Text(
                            item.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                QuantityButton(false, quantity > 1) { quantity-- }
                                Text(
                                    quantity.toString(),
                                    Modifier.padding(horizontal = 8.dp).semantics {
                                        contentDescription =
                                            "Quantity $quantity"
                                    },
                                    style = MaterialTheme.typography.titleMedium,
                                )
                                QuantityButton(true, quantity < 9) { quantity++ }
                            }
                            Button(
                                onClick = {
                                    bagCount += quantity
                                    bagTotal += quantity * item.price
                                },
                                modifier = Modifier.weight(1f).heightIn(min = 52.dp).testTag("shop-add"),
                            ) {
                                AnimatedContent(
                                    targetState = quantity * item.price,
                                    transitionSpec = { fadeIn(motion.tween(180)) togetherWith fadeOut(motion.tween(120)) },
                                    label = "Selected total",
                                ) { total -> Text("Add · €$total") }
                            }
                        }
                    }
                }
            }
        }
        AnimatedContent(
            targetState = bagTotal,
            transitionSpec = { fadeIn(motion.tween(200)) togetherWith fadeOut(motion.tween(120)) },
            modifier = Modifier.fillMaxWidth().semantics { liveRegion = LiveRegionMode.Polite },
            label = "Bag total",
        ) { total ->
            Text(
                if (total ==
                    0
                ) {
                    "A fictional shop, full of good shapes."
                } else {
                    "$bagCount ${if (bagCount == 1) "object" else "objects"} in your bag · €$total"
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun QuantityButton(
    add: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val tint = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
    IconButton(
        onClick,
        enabled = enabled,
        modifier =
            Modifier.size(48.dp).semantics {
                contentDescription =
                    if (add) "Increase quantity" else "Decrease quantity"
            },
    ) {
        Canvas(Modifier.size(18.dp)) {
            drawLine(tint, Offset(2.dp.toPx(), center.y), Offset(size.width - 2.dp.toPx(), center.y), 2.dp.toPx(), StrokeCap.Round)
            if (add) {
                drawLine(
                    tint,
                    Offset(center.x, 2.dp.toPx()),
                    Offset(center.x, size.height - 2.dp.toPx()),
                    2.dp.toPx(),
                    StrokeCap.Round,
                )
            }
        }
    }
}

/** Product silhouettes are original flat geometry, not simulated product photography. */
@Composable
private fun ProductArtwork(
    index: Int,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier.clip(RoundedCornerShape(20.dp)).background(if (index == 0) ShopLavender else ShopPeach)) {
        val unit = size.minDimension
        val cx = size.width * 0.5f
        val top = size.height * 0.5f - unit * 0.43f
        if (index == 0) {
            drawOval(ShopInk.copy(alpha = 0.12f), Offset(cx - unit * 0.33f, top + unit * 0.80f), Size(unit * 0.66f, unit * 0.08f))
            drawLine(ShopInk, Offset(cx, top + unit * 0.32f), Offset(cx, top + unit * 0.78f), unit * 0.065f, StrokeCap.Round)
            drawRoundRect(
                ShopInk,
                Offset(cx - unit * 0.23f, top + unit * 0.74f),
                Size(unit * 0.46f, unit * 0.07f),
                CornerRadius(unit * 0.035f),
            )
            drawArc(ShopInk, 180f, 180f, true, Offset(cx - unit * 0.36f, top + unit * 0.05f), Size(unit * 0.72f, unit * 0.66f))
            drawOval(ShopIvory, Offset(cx - unit * 0.36f, top + unit * 0.34f), Size(unit * 0.72f, unit * 0.085f))
            drawOval(ShopInk, Offset(cx - unit * 0.055f, top + unit * 0.345f), Size(unit * 0.11f, unit * 0.04f))
        } else {
            drawOval(ShopInk.copy(alpha = 0.12f), Offset(cx - unit * 0.35f, top + unit * 0.80f), Size(unit * 0.70f, unit * 0.08f))
            drawLine(
                ShopInk,
                Offset(cx - unit * 0.20f, top + unit * 0.32f),
                Offset(cx - unit * 0.28f, top + unit * 0.80f),
                unit * 0.075f,
                StrokeCap.Round,
            )
            drawLine(
                ShopInk,
                Offset(cx + unit * 0.20f, top + unit * 0.32f),
                Offset(cx + unit * 0.28f, top + unit * 0.80f),
                unit * 0.075f,
                StrokeCap.Round,
            )
            drawOval(ShopInk, Offset(cx - unit * 0.36f, top + unit * 0.19f), Size(unit * 0.72f, unit * 0.29f))
            drawOval(ShopIvory, Offset(cx - unit * 0.36f, top + unit * 0.14f), Size(unit * 0.72f, unit * 0.25f))
            drawOval(
                ShopInk,
                Offset(cx - unit * 0.12f, top + unit * 0.205f),
                Size(unit * 0.24f, unit * 0.095f),
                style =
                    Stroke(unit * 0.035f),
            )
        }
    }
}

@Composable
internal fun ShopPreview(modifier: Modifier = Modifier) {
    ProductArtwork(0, modifier)
}
