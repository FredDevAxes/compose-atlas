package dev.devaxes.composeatlas.ui

import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/** Every authored animation consumes this policy; Android still applies system scale. */
@Immutable
internal data class AtlasMotion(
    val mode: MotionMode = MotionMode.Normal,
    val systemEnabled: Boolean = true,
) {
    val enabled: Boolean get() = mode != MotionMode.Off && systemEnabled

    fun duration(base: Int = 400): Int =
        if (!enabled) {
            0
        } else {
            when (mode) {
                MotionMode.Normal -> base
                MotionMode.Slow -> base * 3
                MotionMode.Off -> 0
            }
        }

    fun <T> tween(base: Int = 400): TweenSpec<T> = tween(duration(base), easing = FastOutSlowInEasing)

    fun <T> spring(): FiniteAnimationSpec<T> =
        if (!enabled) {
            snap()
        } else {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = if (mode == MotionMode.Slow) 35f else 315f,
            )
        }
}

internal enum class MotionMode(
    val label: String,
) {
    Normal("Normal"),
    Slow("Slow motion"),
    Off("Off"),
}

internal val LocalAtlasMotion = staticCompositionLocalOf { AtlasMotion() }

private val Light =
    lightColorScheme(
        primary = Color(0xFF4F46C8),
        onPrimary = Color.White,
        primaryContainer = Color(0xFFE5E1FF),
        onPrimaryContainer = Color(0xFF252052),
        secondary = Color(0xFF536172),
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFE0E9F6),
        onSecondaryContainer = Color(0xFF172434),
        tertiary = Color(0xFF9A3D2B),
        onTertiary = Color.White,
        background = Color(0xFFF6F7FC),
        onBackground = Color(0xFF202436),
        surface = Color(0xFFF6F7FC),
        onSurface = Color(0xFF202436),
        surfaceContainer = Color(0xFFEBEDF5),
        surfaceContainerLow = Color(0xFFF0F1F8),
        surfaceContainerHigh = Color(0xFFE5E7F0),
        surfaceContainerHighest = Color(0xFFDFE1EA),
        onSurfaceVariant = Color(0xFF535869),
        outline = Color(0xFF767B8C),
        outlineVariant = Color(0xFFD1D4E1),
    )
private val Dark =
    darkColorScheme(
        primary = Color(0xFFC5BFFF),
        onPrimary = Color(0xFF282064),
        primaryContainer = Color(0xFF40358E),
        onPrimaryContainer = Color(0xFFE5E1FF),
        secondary = Color(0xFFBAC8DC),
        onSecondary = Color(0xFF243244),
        secondaryContainer = Color(0xFF39465A),
        onSecondaryContainer = Color(0xFFE0E9F6),
        tertiary = Color(0xFFFFB4A3),
        onTertiary = Color(0xFF5D170B),
        background = Color(0xFF141622),
        onBackground = Color(0xFFE6E7F3),
        surface = Color(0xFF141622),
        onSurface = Color(0xFFE6E7F3),
        surfaceContainer = Color(0xFF212431),
        surfaceContainerLow = Color(0xFF1B1E2A),
        surfaceContainerHigh = Color(0xFF2B2E3D),
        surfaceContainerHighest = Color(0xFF363949),
        onSurfaceVariant = Color(0xFFC2C5D6),
        outline = Color(0xFF8D92A5),
        outlineVariant = Color(0xFF424658),
    )

@Composable
internal fun AtlasTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (isSystemInDarkTheme()) Dark else Light, content = content)
}

/** Observe reduced motion, including changes made while the app stays in the foreground. */
@Composable
internal fun rememberSystemMotionEnabled(): Boolean {
    val resolver = LocalContext.current.contentResolver

    fun read() = Settings.Global.getFloat(resolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) > 0f
    var enabled by remember(resolver) { mutableStateOf(read()) }
    DisposableEffect(resolver) {
        val observer =
            object : ContentObserver(Handler(Looper.getMainLooper())) {
                override fun onChange(selfChange: Boolean) {
                    enabled = read()
                }
            }
        resolver.registerContentObserver(Settings.Global.getUriFor(Settings.Global.ANIMATOR_DURATION_SCALE), false, observer)
        onDispose { resolver.unregisterContentObserver(observer) }
    }
    return enabled
}
