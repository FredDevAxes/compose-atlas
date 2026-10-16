# Capability map

This map describes the eight reachable scenes in the local redesign. It connects
Compose patterns to a task and a source file; it does not claim every Compose API
is implemented. A capability present in source is distinct from verified behavior.
Current execution evidence is recorded in [verification](verification.md).

## Implemented in the local source

All scene links below point to
[`ui/scenes/`](../app/src/main/java/dev/devaxes/composeatlas/ui/scenes/).

| Scene | Mechanisms | What to inspect |
| --- | --- | --- |
| [Pocket DJ](../app/src/main/java/dev/devaxes/composeatlas/ui/scenes/MusicScene.kt) | `AnimatedContent`, `SizeTransform`, spring size animation, `Canvas`, `Slider`, `LaunchedEffect`, lifecycle observation | Fold the sleeve; change title; seek to the end; leave and restore paused |
| [Little shop](../app/src/main/java/dev/devaxes/composeatlas/ui/scenes/ShopScene.kt) | Experimental `SharedTransitionLayout`, `sharedElement`, stable content identity, animated totals, polite live regions | Open each object; return with Back; change quantity; add to a local bag |
| [On its way](../app/src/main/java/dev/devaxes/composeatlas/ui/scenes/DeliveryScene.kt) | `Canvas`, `Path`, `PathMeasure`, finite `Animatable`, derived status | Start, inspect intermediate stages, finish, replay and compare global motion modes |
| [Energy story](../app/src/main/java/dev/devaxes/composeatlas/ui/scenes/EnergyScene.kt) | `Canvas`, animated values, tap/drag input, text semantics and equivalent selection buttons | Select the peak and missing reading; compare days; use Earlier/Later at boundaries |
| [Your ticket](../app/src/main/java/dev/devaxes/composeatlas/ui/scenes/TicketScene.kt) | Text fields, `FocusRequester`, IME actions, error state, `AnimatedContent`, custom `Shape` | Submit empty input; Next changes focus; Done validates; edit a local confirmation |
| [Poster studio](../app/src/main/java/dev/devaxes/composeatlas/ui/scenes/PhotoScene.kt) | `transformable`, multi-touch, `graphicsLayer`, cancellable animation, custom accessibility actions | Zoom, rotate, pan and reset; interrupt a button animation with a gesture |
| [Inbox zero](../app/src/main/java/dev/devaxes/composeatlas/ui/scenes/InboxScene.kt) | `LazyColumn`, stable keys, `animateItem`, `SwipeToDismissBox`, snackbar undo | Swipe or press Archive; Undo; restore all; inspect the empty inbox |
| [Make room](../app/src/main/java/dev/devaxes/composeatlas/ui/scenes/AgendaScene.kt) | Custom `Layout`, one-pass child measurement, pure overlap lanes, animated offsets/widths, `Canvas` grid | Move coffee; compare overlap lanes; enlarge system text for the list adaptation |
| [Shared shell](../app/src/main/java/dev/devaxes/composeatlas/ui/ExperimentGallery.kt) | `rememberSaveable`, `SaveableStateHolder`, state-driven routes, `BackHandler`, responsive columns, insets and keyboard padding | Return from settings; recreate activity; revisit a scene; expand How it works |
| [Theme and motion](../app/src/main/java/dev/devaxes/composeatlas/ui/AtlasTheme.kt) | Material 3, system light/dark, `CompositionLocal`, tween/spring/snap policy | Compare themes and Normal/Slow/Off with Android's animation scale |

`SharedTransitionLayout` requires an experimental opt-in in pinned Compose 1.9.0.
The gallery illustrates patterns used in production interfaces; it supplies none
of the backend, business or operational guarantees of those products.

## Evidence boundaries

The test sources under [`app/src/test`](../app/src/test) and
[`app/src/androidTest`](../app/src/androidTest) describe automated scenarios.
Only recorded executions verify a revision; old test counts must not stand in for
the redesign. Native screenshots show specific visual states, not animation timing,
TalkBack traversal or frame performance.

Text labels, focus actions, content descriptions, custom actions and non-gesture
controls are implemented in relevant scenes. Full screen-reader testing and
accessibility certification require separate evidence. The large-text gallery and
agenda adaptations do not establish complete tablet, foldable or desktop coverage.

## Future scope

These areas are not implemented by the eight-scene gallery. They are candidates,
not disabled controls or promised roadmap commitments.

| Family | A useful next experiment | Evidence needed |
| --- | --- | --- |
| View interoperability | A meaningful `AndroidView` / `ComposeView` boundary | Lifecycle, disposal, input and saved-state behavior |
| Asynchronous data | Loading, retry, pagination and Flow-backed state | Controlled repository fixtures, cancellation and recovery tests |
| Production navigation | List/detail panes, deep links and multiple back stacks | Window profiles, route restoration and Back behavior |
| Rich text | Selection, links and reading order | Text fixtures, keyboard and TalkBack checks |
| Advanced drawing | Cached drawing, blending or custom modifier nodes | A bounded rendering task and visual correctness checks |
| System integration | Media3 playback, location or calendar access | Permissions, lifecycle, failure cases and actual service boundaries |
| Performance tooling | Tracing, Macrobenchmark and baseline profiles | Repeatable protocol, raw measurements and physical-device runs |
| Broader adaptation | Dedicated tablets/foldables, dynamic color and input devices | Explicit supported profiles and visual/interaction checks |

No hardware smoothness, battery use, audio quality, haptic behavior or real-world
service performance is inferred from emulator tests. Earlier Temperature study
experiments are documented in the [historical record](archive/verification-v0.2.0.md),
not counted as a ninth current scene.
