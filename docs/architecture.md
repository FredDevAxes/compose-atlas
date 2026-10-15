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


## A gallery with independent scene state

The root uses a saveable route and SaveableStateHolder. Each scene owns its state;
leaving a scene removes its effects from composition while retaining saveable
values for re-entry. This small local gallery does not need a navigation dependency
or a production back stack. Experiment and Understand keep independent scroll
positions. The new scene body is capped at 600 dp in wide windows for readable
copy and controls. Android Back returns to Explore; the chart first
closes its control panel, and native dialogs handle their own dismissal.

Quiet frequency uses a cancellable coroutine ticking every 100 ms. Its clock is
a simulation, not an audio clock or performance benchmark. A lifecycle observer
pauses on ON_STOP. Playback itself is transient; position and track are saveable.
Vertical dragging belongs only to the title, leaving normal page scrolling free.

The collection hoists favorite IDs out of lazy items. Both layouts use stable
keys and animateItem, so reordered items keep their identity. Search and category
filters combine with the saved-only filter. Its controls live inside the lazy
container, avoiding an inaccessible results pane with a short window or large font.

The form derives validation from input state and only displays errors after a
submission attempt. IME Next requests the email focus; Done runs the same submit
action as the button. Confirmation is local. Input and confirmation are saveable.

The agenda's pure overlap algorithm groups connected intervals, assigning the
first free lane and a consistent lane count within each group. Its custom Layout
measures each child exactly once from duration and lane width, then places it from
start time. The Canvas hour grid shares the same pixels-per-minute scale.
Narrow windows or font scale above 1.2 use a chronological list, preserving readable
event content. Timeline zoom is disabled in that list with a visible explanation. Time changes clamp the entire event within the displayed day.

References: [lazy lists and keys](https://developer.android.com/develop/ui/compose/lists),
[custom layouts](https://developer.android.com/develop/ui/compose/layouts/custom),
[text-field focus](https://developer.android.com/develop/ui/compose/touch-input/focus/focus-in-text-fields),
and [animation composables](https://developer.android.com/develop/ui/compose/animation/composables-modifiers).
