# Architecture and reuse

Compose Atlas is one Android application, with eight local scenes and a small shared
shell. A visitor acts first and can open an explanation afterward. The architecture
follows that boundary: each scene owns a comprehensible interaction; the shell owns
navigation, saved scene scopes, theme and the global motion preference.

## Find the mechanism

| File | Responsibility |
| --- | --- |
| `data/AtlasScene.kt` | Scene catalog, UI copy, mechanism, production use and boundaries |
| `ui/ExperimentGallery.kt` | Gallery, one-level routes, settings, explanation and scene host |
| `ui/AtlasTheme.kt` | System light/dark theme and `LocalAtlasMotion` |
| `ui/scenes/MusicScene.kt` | Simulated transport, coordinated title and sleeve motion |
| `ui/scenes/ShopScene.kt` | Shared-element catalog/detail journey and local bag totals |
| `ui/scenes/DeliveryScene.kt` | Finite journey, path geometry and status |
| `ui/scenes/EnergyScene.kt` | Deterministic readings, selection and chart drawing |
| `ui/scenes/TicketScene.kt` | Input, focus, local validation and shaped confirmation |
| `ui/scenes/PhotoScene.kt` | Direct transforms, button-driven transforms and artwork |
| `ui/scenes/InboxScene.kt` | Stable message identity, archive, undo and empty state |
| `ui/scenes/AgendaScene.kt` | Time-driven layout and list adaptation for large text |
| `data/GalleryData.kt` | Pure validation and interval-placement helpers |

Paths above are relative to
[`app/src/main/java/dev/devaxes/composeatlas/`](../app/src/main/java/dev/devaxes/composeatlas/).
Earlier temperature/chart implementations and unused collection helpers have been
removed from the active application. Historical chart behavior and its verification
belong to [v0.2.0](archive/verification-v0.2.0.md).

## State and lifetime

`AtlasApp` stores the current route, return route for settings and motion preference
with `rememberSaveable`. `SaveableStateHolder` gives each route its own saved scope.
Leaving a scene removes its composition and cancels composition-scoped work; saved
input and choices can return with it. These values support activity recreation,
not durable storage across a fresh application installation or a force stop.

Scene state is local and changes through explicit events. Values such as a selected
product, attendee input, archived IDs and chart selection are saveable. Playback
itself is deliberately transient: Pocket DJ restores its position paused. Its
`LaunchedEffect` advances a simulated clock and `DisposableEffect` attaches a
lifecycle observer that pauses playback on `ON_STOP`. This clock is not an audio
engine or a timing benchmark.

The delivery stores completed progress and resumes a finite journey when its scene
is composed again. Inbox undo uses a composition-scoped coroutine and a Material
snackbar; a later archive replaces the pending undo. Restoring an item resets its
swipe state so the restored row does not immediately dismiss itself again.

A ViewModel, asynchronous repository and dependency-injection container would add
indirection without owning meaningful external work here. A real application with
network requests, durable data or cross-screen business state should introduce
those boundaries for that work; the lab does not demonstrate them.

## Navigation and native layout

The gallery has one level of scene routes and a settings return route. System Back
returns from settings to its originating screen and from a scene to the gallery.
Little shop handles Back from product detail before the scene exits. This is a
small explicit state machine, not a demonstration of a general navigation library,
multiple back stacks, deep links or predictive Back transitions.

Material 3 app bars, buttons, fields, sliders and snackbars supply familiar Android
interaction. Scaffold insets protect system bars; `imePadding` keeps form content
within the keyboard-adjusted scrollable page. Scene content is capped at 640 dp.
The gallery varies its column count with width and font scale; the agenda uses a
chronological list above font scale 1.2. These are local adaptations, not a complete
foldable or multi-pane tablet design. Theme palettes follow system light/dark;
dynamic color is not implemented.

## One motion policy

`LocalAtlasMotion` carries Normal, Slow motion or Off. Authored tweens use a shared
easing and triple their base duration in Slow motion. Authored springs reduce
stiffness from 315 to 35 with the same damping; this makes them slower, but it is
not a uniform time-scale transform. Off uses zero-duration tweens and snapped
spring targets. A lifecycle-scoped `ContentObserver` observes Android animator scale
changes, including scale zero while the app stays in the foreground. Compose also
applies the nonzero system scale.

The policy controls authored transitions, not elapsed media time, direct gesture
tracking or every animation internal to a Material component. Scene controls alter
the task itself: Play, Fold, Move coffee or Archive. There are no damping sliders,
per-scene speed controls or separate technical control panels.

## Geometry and identity

Little shop uses `SharedTransitionLayout` and stable shared-content keys to carry
the same Canvas object between its collection and detail. Surrounding content uses
`AnimatedContent`. Shared transitions are experimental in Compose 1.9.0; extracting
this pattern requires retaining the opt-in and checking its API on an upgrade.

On its way draws a route, measures it with `PathMeasure` and derives both the
revealed trail and courier position from distance along that same path. Its map
is original geometry, with no mapping SDK or location permission.

Energy story shares a fixed 0–3 kWh scale between bars and uses the same slot
calculation for tap and drag selection. Text inspection always reports fixture
values, not intermediate animated bar heights. The missing noon reading is a
visible gap and reads “No reading”; it is never presented as zero. Today and
yesterday are separate illustrative fixtures, not imported meter records.

Poster studio updates scale, rotation and translation from `transformable`.
`graphicsLayer` applies the visual transform without changing the artwork's layout
size. Button animations are cancellable when a new gesture takes over. Zoom,
rotation and reset also have explicit controls and accessibility actions; directional
custom actions expose panning without a drag.

Inbox keeps archived state by message ID rather than row position. Stable lazy
keys preserve identity while `animateItem` moves neighboring rows; undo restores
the original order. The agenda computes overlap lanes in pure data code, measures
each event once in `Layout` and positions it from time and duration. Its grid and
event positions share the same displayed morning range.

## Copy a pattern with its boundaries

Start with the scene file, then inspect its imports and the helpers above. Replace
synthetic fixtures and the shared motion provider deliberately. Extract state and
event parameters when a scene needs to participate in a larger application; do
not copy a simulated transport or local bag and imply it performs backend work.

Canvas illustrations, ticket outlines and thumbnails are original runtime geometry.
No generated bitmap, remote artwork, custom font or photo-library permission is
needed. Ticket validation is local format validation; its decorative barcode is
not scannable admission proof. No scene uses analytics, payment, dispatch, email,
audio decoding or a remote service.

See [coverage](coverage.md) for implemented APIs and deferred families, and
[verification](verification.md) for evidence rather than inferred guarantees.
