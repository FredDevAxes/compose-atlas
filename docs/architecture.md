# Architecture and decisions

## State drives the interface

A single Android application is split into data/math and small Compose surfaces.
The data layer has no Android dependency. `ExperimentState` is immutable; controls
request a new state, and the chart, readout and explanations consume it. A saveable
state holder is sufficient for this local, synchronous experiment. There is no
network repository, dependency-injection container or ViewModel with no work to do.

`rememberSaveable` preserves period, scenario, sample index, visible series, animation
settings, panel visibility and selected explanation tab through activity recreation.
Replay progress is transient and restarts on screen re-entry.

## Numeric integrity

All series use the fixed 18–26 °C domain. Hiding one series does not silently
rescale the other. Canvas, hit testing and markers use the same normalized
coordinates. Sample selection clamps at the edges and uses the closest supplied
sample; a single sample sits at the midpoint. Missing values split a path into
segments, including isolated points.

Day has 25 hourly samples from 00:00 through 24:00. The default sample at 14:00 is
21.4 °C for Studio and 23.1 °C for Gallery. Week has seven independent synthetic
daily values; Month has thirty synthetic daily values for September. The latter
fixtures are not inferred aggregations of Day. All fixture data is deterministic.

## What the animation means

The milestone animates a horizontal reveal of existing traces. A clipped Spring
progress can overshoot internally, but the revealed area is clamped and does not
change any temperature value. The cursor and readout appear once the selected animation settles, including
its overshoot and return. Beginning an inspection stops replay and reveals the complete chart
before displaying the selected value.

This intentionally replaces the proposed period morph with a trace reveal: the
fixtures have different cardinalities, and interpolating them without a defined
resampling rule would suggest measurements that do not exist. Period changes use
a new fixture and a fresh reveal, with correctly labeled axes from the start.
Continuous morphing remains a possible future experiment with its own semantics.

## Comparing motion and slowing time

Two `Animatable` values start in sibling coroutines on the same frame clock. Tween
uses explicit FastOutSlowIn easing and the selected duration. Spring uses the
selected damping and stiffness, with a normalized visibility threshold of 0.001.
The comparison lanes show raw animation position, including values above 100%;
the chart still clips its reveal at the plot boundary. Default damping is 0.45.
These markers are animation diagnostics, not temperature samples.

Playback speed is saved with experiment state. A local `MotionDurationScale`
divides the inherited system scale by 1, 0.5 or 0.25. It reads the inherited scale
dynamically, so system motion-off remains zero. This stretches time for both
animations without modifying stiffness, damping or easing. Replay starts both
from zero; inspecting the chart stops both and snaps them to their targets.
Changing the selected mode or speed replays immediately; slider settings apply on
Replay. There is no continuous loop or global Android-setting change.

References: [animation specifications](https://developer.android.com/develop/ui/compose/animation/customize)
and [MotionDurationScale](https://developer.android.com/reference/kotlin/androidx/compose/ui/MotionDurationScale).

## Native behavior

Instrument uses Android Material controls and system typography, a compact header,
a Canvas chart and a non-modal settings surface. Understand carries the explanation
from the approved guided composition. The panel is independently scrollable;
short windows and enlarged text switch to a page scroll. System Back closes the
panel before leaving the detail.

Selection can be changed through touch, explicit point buttons and custom
accessibility actions. Labels and dash patterns complement color. The readout
exposes the values as text; drawing pixels is not the only way to inspect data.
The project remains dark-only for this first Instrument milestone.

## Official references

Consulted on 15 October 2026:

- [Compose architecture](https://developer.android.com/develop/ui/compose/architecture)
- [Graphics in Compose](https://developer.android.com/develop/ui/compose/graphics/draw/overview)
- [Compose testing](https://developer.android.com/develop/ui/compose/testing)
- [Animation testing](https://developer.android.com/develop/ui/compose/animation/testing)

The implementation is original; reference examples were not copied into the app.
