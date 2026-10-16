package dev.devaxes.composeatlas.ui.scenes

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import dev.devaxes.composeatlas.data.validateSession
import dev.devaxes.composeatlas.ui.LocalAtlasMotion

private val TicketInk = Color(0xFF392927)
private val TicketCoral = Color(0xFFFFA38C)
private val TicketGold = Color(0xFFFFD875)

/** A local booking task: validation is shared by the button and IME, confirmation sends nothing. */
@Composable
internal fun TicketScene() {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var attempted by rememberSaveable { mutableStateOf(false) }
    var confirmed by rememberSaveable { mutableStateOf(false) }
    val errors = validateSession(name, email)
    val emailFocus = remember { FocusRequester() }
    val nameFocus = remember { FocusRequester() }
    val focus = LocalFocusManager.current
    val motion = LocalAtlasMotion.current
    val submit: () -> Unit = {
        attempted = true
        if (errors.valid) {
            focus.clearFocus()
            confirmed = true
        } else if (errors.name != null) {
            nameFocus.requestFocus()
        } else {
            emailFocus.requestFocus()
        }
    }
    AnimatedContent(
        targetState = confirmed,
        label = "Ticket confirmation",
        transitionSpec = {
            (fadeIn(motion.tween(base = 240)) + slideInVertically(motion.tween(base = 420)) { it / 8 })
                .togetherWith(fadeOut(motion.tween(base = 120)))
                .using(SizeTransform { _, _ -> motion.spring() })
        },
    ) { complete ->
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            if (complete) {
                Text(
                    "Your preview is ready.",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                )
                AdmissionTicket(name.trim(), email.trim())
                Text(
                    "A night for bright ideas. One place with your name on it.",
                    style = MaterialTheme.typography.bodyLarge,
                )
                Button(
                    onClick = { confirmed = false },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp).testTag("edit-ticket"),
                ) { Text("Edit my details") }
            } else {
                EventArtwork(Modifier.fillMaxWidth())
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    singleLine = true,
                    isError = attempted && errors.name != null,
                    supportingText = if (attempted && errors.name != null) ({ Text(errors.name.orEmpty()) }) else null,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { emailFocus.requestFocus() }),
                    modifier = Modifier.fillMaxWidth().focusRequester(nameFocus).testTag("ticket-name"),
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    singleLine = true,
                    isError = attempted && errors.email != null,
                    supportingText = if (attempted && errors.email != null) ({ Text(errors.email.orEmpty()) }) else null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { submit() }),
                    modifier = Modifier.fillMaxWidth().focusRequester(emailFocus).testTag("ticket-email"),
                )
                Button(onClick = submit, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp).testTag("make-ticket")) {
                    Text("Make my ticket")
                }
                TextButton(
                    onClick = {
                        name = "Alex Morgan"
                        email = "alex@example.com"
                        attempted = false
                        focus.clearFocus()
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("Use example details") }
            }
        }
    }
}

@Composable
private fun EventArtwork(modifier: Modifier = Modifier) {
    Box(modifier.clip(RoundedCornerShape(20.dp)).background(TicketCoral)) {
        Canvas(Modifier.matchParentSize()) {
            val radius = size.width * .32f
            drawCircle(
                TicketInk.copy(alpha = .12f),
                radius,
                androidx.compose.ui.geometry
                    .Offset(size.width * .98f, size.height * .15f),
            )
            drawCircle(
                TicketInk.copy(alpha = .12f),
                radius * .70f,
                androidx.compose.ui.geometry.Offset(
                    size.width * .98f,
                    size.height * .15f,
                ),
            )
        }
        Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Colour\nafter hours.", style = MaterialTheme.typography.headlineLarge, color = TicketInk, fontWeight = FontWeight.Black)
            Text(
                "FRIDAY · 19:00   /   STUDIO 04",
                color = TicketInk,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun AdmissionTicket(
    name: String,
    email: String,
) {
    var ticketHeight by remember { mutableIntStateOf(0) }
    var footerHeight by remember { mutableIntStateOf(0) }
    val shape = AdmissionShape(if (ticketHeight > 0) (ticketHeight - footerHeight).toFloat() / ticketHeight else .76f)
    Column(
        Modifier
            .fillMaxWidth()
            .onSizeChanged { ticketHeight = it.height }
            .clip(shape)
            .background(TicketGold)
            .semantics(mergeDescendants = true) {
                contentDescription =
                    "Local ticket preview for $name. $email. Colour after hours, Friday at 19:00, Studio 04. This is not an issued admission ticket."
            },
    ) {
        Column(Modifier.padding(26.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    "ADMIT ONE",
                    color = TicketInk,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    "ATLAS / 0042",
                    color = TicketInk,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.End,
                )
            }
            Text("Colour\nafter hours.", color = TicketInk, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black)
            Text("FRIDAY · 19:00\nSTUDIO 04", color = TicketInk, style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(8.dp))
            Text(
                name,
                color = TicketInk,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.testTag("ticket-attendee"),
            )
            Text(email, color = TicketInk, style = MaterialTheme.typography.bodyMedium)
        }
        Canvas(Modifier.fillMaxWidth().height(2.dp).padding(horizontal = 24.dp)) {
            drawLine(
                TicketInk.copy(alpha = .50f),
                androidx.compose.ui.geometry.Offset.Zero,
                androidx.compose.ui.geometry
                    .Offset(size.width, 0f),
                1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(5.dp.toPx(), 5.dp.toPx())),
            )
        }
        Row(
            Modifier.fillMaxWidth().onSizeChanged { footerHeight = it.height }.padding(horizontal = 26.dp, vertical = 22.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(Modifier.weight(1f)) {
                Text("GOOD EVENINGS", color = TicketInk, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                Text("start with an idea.", color = TicketInk, style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(Modifier.width(12.dp))
            DecorativeBarcode(Modifier.width(64.dp).height(38.dp))
        }
    }
}

/** A true outline removes the side notches, revealing any parent surface behind the ticket. */
private class AdmissionShape(
    private val perforationFraction: Float = .76f,
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val corner = with(density) { 16.dp.toPx() }.coerceAtMost(size.width / 8)
        val notch = with(density) { 11.dp.toPx() }.coerceAtMost(size.width / 10)
        val split = size.height * perforationFraction
        val path =
            Path().apply {
                moveTo(corner, 0f)
                lineTo(size.width - corner, 0f)
                quadraticTo(size.width, 0f, size.width, corner)
                lineTo(size.width, split - notch)
                arcTo(Rect(size.width - notch, split - notch, size.width + notch, split + notch), -90f, -180f, false)
                lineTo(size.width, size.height - corner)
                quadraticTo(size.width, size.height, size.width - corner, size.height)
                lineTo(corner, size.height)
                quadraticTo(0f, size.height, 0f, size.height - corner)
                lineTo(0f, split + notch)
                arcTo(Rect(-notch, split - notch, notch, split + notch), 90f, -180f, false)
                lineTo(0f, corner)
                quadraticTo(0f, 0f, corner, 0f)
                close()
            }
        return Outline.Generic(path)
    }
}

@Composable
private fun DecorativeBarcode(modifier: Modifier = Modifier) {
    Canvas(modifier.semantics { contentDescription = "Decorative barcode. Not scannable." }) {
        val widths = listOf(2, 1, 3, 1, 1, 2, 1, 3, 2, 1, 1, 3, 1)
        val unit = size.width / (widths.sum() + widths.size)
        var x = 0f
        widths.forEachIndexed { index, width ->
            drawRect(
                TicketInk,
                androidx.compose.ui.geometry
                    .Offset(x, 0f),
                Size(
                    width * unit,
                    size.height * if (index % 3 == 0) 1f else .82f,
                ),
            )
            x += (width + 1) * unit
        }
    }
}

@Composable
internal fun TicketPreview(modifier: Modifier = Modifier) {
    Box(modifier.background(Color(0xFFFFEBC1)), contentAlignment = Alignment.Center) {
        Row(
            Modifier
                .fillMaxWidth(.82f)
                .height(88.dp)
                .clip(AdmissionShape())
                .background(TicketGold)
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                Box(
                    Modifier
                        .fillMaxWidth(.73f)
                        .height(7.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(TicketInk.copy(alpha = .65f)),
                )
                Box(
                    Modifier
                        .fillMaxWidth(.48f)
                        .height(7.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(TicketInk.copy(alpha = .40f)),
                )
            }
            DecorativeBarcode(Modifier.width(42.dp).height(35.dp))
        }
    }
}
