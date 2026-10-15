package dev.devaxes.composeatlas.data

import java.util.Locale
import kotlin.math.roundToInt
import kotlin.math.sin

enum class Period(
    val label: String,
) {
    DAY("Day"),
    WEEK("Week"),
    MONTH("Month"),
}

enum class Scenario(
    val label: String,
) {
    NORMAL("Normal"),
    EMPTY("Empty"),
    GAPS("Gaps"),
    LONG_LABELS("Long labels"),
}

enum class MotionMode { TWEEN, SPRING }

enum class PlaybackSpeed(
    val label: String,
    val multiplier: Float,
) {
    NORMAL("1×", 1f),
    HALF("0.5×", 0.5f),
    QUARTER("0.25×", 0.25f),
}

enum class Series { STUDIO, GALLERY }

data class Sample(
    val label: String,
    val studio: Float?,
    val gallery: Float?,
)

data class TemperatureSet(
    val samples: List<Sample>,
    val description: String,
)

object TemperatureData {
    private val day =
        listOf(
            19.2f,
            19.3f,
            19.6f,
            19.5f,
            19.6f,
            19.8f,
            20.0f,
            20.2f,
            20.4f,
            20.6f,
            20.8f,
            21.0f,
            21.2f,
            21.3f,
            21.4f,
            21.4f,
            21.2f,
            20.9f,
            20.6f,
            20.4f,
            20.2f,
            20.0f,
            19.8f,
            19.7f,
            19.5f,
        )

    fun load(
        period: Period,
        scenario: Scenario,
    ): TemperatureSet {
        val samples =
            when (period) {
                Period.DAY ->
                    day.mapIndexed { hour, value ->
                        Sample("%02d:00".format(Locale.US, hour), value, value + 1.7f)
                    }
                Period.WEEK ->
                    listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                        .mapIndexed { i, label ->
                            val value = listOf(20.2f, 20.8f, 21.5f, 20.9f, 19.8f, 20.4f, 21.1f)[i]
                            Sample(label, value, value + 1.3f + i % 3 * 0.2f)
                        }
                Period.MONTH ->
                    (1..30).map { date ->
                        val value = 20.8f + sin(date * 0.35f) * 1.2f
                        Sample("Sep %02d".format(Locale.US, date), value, value + 1.6f)
                    }
            }
        val result =
            when (scenario) {
                Scenario.EMPTY -> emptyList()
                Scenario.GAPS ->
                    samples.mapIndexed { i, sample ->
                        sample.copy(
                            studio = sample.studio.takeUnless { i in samples.size / 3..samples.size / 2 },
                            gallery = sample.gallery.takeUnless { i in samples.size / 2..samples.size * 2 / 3 },
                        )
                    }
                Scenario.LONG_LABELS -> samples.map { it.copy(label = "Synthetic observation at " + it.label) }
                Scenario.NORMAL -> samples
            }
        return TemperatureSet(
            result,
            when (period) {
                Period.DAY -> "25 synthetic hourly samples. No sensor or live feed."
                Period.WEEK -> "7 independent synthetic daily values. Not an aggregation of Day."
                Period.MONTH -> "30 independent synthetic daily values for September. Not an aggregation of Week."
            },
        )
    }
}

object ChartMath {
    const val MIN = 18f
    const val MAX = 26f

    fun xFraction(
        index: Int,
        count: Int,
    ): Float = if (count <= 1) 0.5f else index.toFloat() / (count - 1)

    fun yFraction(value: Float): Float = 1f - (value - MIN) / (MAX - MIN)

    fun nearestIndex(
        fraction: Float,
        count: Int,
    ): Int? = if (count == 0) null else (fraction.coerceIn(0f, 1f) * (count - 1)).roundToInt()

    fun segments(values: List<Float?>): List<List<Pair<Int, Float>>> {
        val segments = mutableListOf<List<Pair<Int, Float>>>()
        var current = mutableListOf<Pair<Int, Float>>()
        for ((index, value) in values.withIndex()) {
            if (value == null) {
                if (current.isNotEmpty()) segments.add(current)
                current = mutableListOf()
            } else {
                current.add(index to value)
            }
        }
        if (current.isNotEmpty()) segments.add(current)
        return segments
    }

    fun closestTime(
        previousIndex: Int,
        previousCount: Int,
        newCount: Int,
    ): Int = nearestIndex(xFraction(previousIndex, previousCount), newCount) ?: 0

    fun format(value: Float?): String = value?.let { "%.1f °C".format(Locale.US, it) } ?: "No sample"
}

data class ExperimentState(
    val period: Period = Period.DAY,
    val scenario: Scenario = Scenario.NORMAL,
    val selected: Int = 14,
    val studio: Boolean = true,
    val gallery: Boolean = true,
    val mode: MotionMode = MotionMode.TWEEN,
    val duration: Int = 600,
    val damping: Float = 0.45f,
    val stiffness: Float = 200f,
    val animate: Boolean = true,
    val speed: PlaybackSpeed = PlaybackSpeed.NORMAL,
) {
    val data: TemperatureSet get() = TemperatureData.load(period, scenario)
    val sample: Sample? get() = data.samples.getOrNull(selected)

    fun changePeriod(next: Period): ExperimentState {
        val count = TemperatureData.load(next, scenario).samples.size
        return copy(period = next, selected = ChartMath.closestTime(selected, data.samples.size, count))
    }

    fun changeScenario(next: Scenario): ExperimentState =
        copy(scenario = next, selected = selected.coerceIn(0, (TemperatureData.load(period, next).samples.size - 1).coerceAtLeast(0)))

    fun select(index: Int): ExperimentState = copy(selected = index.coerceIn(0, (data.samples.size - 1).coerceAtLeast(0)))

    fun toggle(series: Series): ExperimentState =
        when (series) {
            Series.STUDIO -> if (gallery) copy(studio = !studio) else this
            Series.GALLERY -> if (studio) copy(gallery = !gallery) else this
        }

    fun description(): String =
        sample?.let {
            buildList {
                add(it.label)
                if (studio) add("Studio: " + ChartMath.format(it.studio))
                if (gallery) add("Gallery: " + ChartMath.format(it.gallery))
            }.joinToString(". ")
        } ?: "No samples in this scenario"
}
