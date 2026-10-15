package dev.devaxes.composeatlas.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object Instrument {
    val Background = Color(0xFF0C1715)
    val Surface = Color(0xFF12231F)
    val Outline = Color(0xFF2D433C)
    val Text = Color(0xFFEEF4F0)
    val Muted = Color(0xFFB0C3BB)
    val Lime = Color(0xFFB9EE77)
    val Cyan = Color(0xFF6FBDCB)
    val Amber = Color(0xFFE9B965)
    val Selected = Color(0xFF395630)
}

@Composable
fun InstrumentTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme =
            darkColorScheme(
                primary = Instrument.Lime,
                onPrimary = Instrument.Background,
                primaryContainer = Instrument.Selected,
                onPrimaryContainer = Instrument.Text,
                secondary = Instrument.Cyan,
                background = Instrument.Background,
                onBackground = Instrument.Text,
                surface = Instrument.Surface,
                onSurface = Instrument.Text,
                surfaceVariant = Instrument.Surface,
                onSurfaceVariant = Instrument.Muted,
                outline = Instrument.Outline,
            ),
        content = content,
    )
}
