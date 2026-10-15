# Capability coverage

Compose Atlas grows through working interfaces. This is a coverage map, not a claim
to implement every Compose API. All five experiments are available offline.

| Experience | Compose capabilities | Reproducible checks |
| --- | --- | --- |
| Temperature study | Canvas, paths, pointer input, Animatable, Tween/Spring, MotionDurationScale, custom semantics | Select an edge; inspect a gap; compare overshoot at 0.25×; restore settings |
| Quiet frequency | AnimatedContent, AnimatedVisibility, animateContentSize, vertical drag, Slider, LaunchedEffect, DisposableEffect | Seek to the end; change track; compact/expand; empty queue; restore paused |
| Object collection | LazyColumn, adaptive LazyVerticalGrid, stable keys, animateItem, FlowRow, dialog | Save an object; reverse; filter; change layout; inspect empty results; recreate |
| Session builder | TextField state, FocusRequester, IME actions, keyboard type, error semantics, animated confirmation | Submit empty input; Next moves focus; Done validates; edit confirmation; recreate |
| Spatial agenda | Custom Layout, one-pass measurement, Canvas grid, BoxWithConstraints, animated size/placement | Overlap sessions; zoom; move by 15 minutes; clamp to day; open event; recreate |
| Shared shell | State hoisting, rememberSaveable, SaveableStateHolder, Material theme, insets, system Back | Leave/re-enter a scene; scroll with large text or a short window; inspect Understand |

## Evidence and source

- [Chart state/math](../app/src/main/java/dev/devaxes/composeatlas/data/TemperatureData.kt)
  and [chart tests](../app/src/test/java/dev/devaxes/composeatlas/TemperatureDataTest.kt).
- [Gallery fixtures, filtering, validation and interval layout](../app/src/main/java/dev/devaxes/composeatlas/data/GalleryData.kt)
  and [pure tests](../app/src/test/java/dev/devaxes/composeatlas/GalleryDataTest.kt).
- [Chart UI flows](../app/src/androidTest/java/dev/devaxes/composeatlas/AtlasFlowTest.kt)
  and [gallery UI flows](../app/src/androidTest/java/dev/devaxes/composeatlas/GalleryFlowTest.kt).
- [Recorded runs and native captures](verification.md). A listed test describes its
  scope; screenshots and emulator tests do not establish hardware performance.

## Next experiments

These are candidates for later milestones, not inactive buttons in the app.

| Candidate | What it would add | Boundary to define |
| --- | --- | --- |
| Reading room | Rich text, selection, links, typography, accessibility traversal | Text fixtures and manual TalkBack scenarios |
| Adaptive inbox | Navigation back stack, list/detail panes, window classes, shared transitions | Phone, tablet and foldable emulator profiles |
| Gesture studio | Draggable anchors, nested scroll, transforms, multi-touch, fling | Pointer arbitration and equivalent accessible actions |
| Theme workshop | Light/dark, dynamic color, contrast, composition locals | Explicit color policy and screenshot variants |
| Drawing desk | drawWithCache, graphicsLayer, blend modes, custom modifiers | Export behavior and bounded rendering workload |
| Interop bench | AndroidView, ComposeView, lifecycle boundaries | A small View-based control with a meaningful comparison |
| Performance bench | Recomposition observation, stability, tracing, Macrobenchmark, baseline profiles | A repeatable protocol; hardware for performance claims |

Loading, pagination, Flow/ViewModel integration and asynchronous error recovery
need a separate data-driven experiment. Media3 audio, services and permissions are
Android integrations, not capabilities demonstrated by the fictional player.
No shared-element, custom Modifier.Node, production navigation, haptic, audio,
tablet-specific or light-theme coverage is claimed in the current build.
