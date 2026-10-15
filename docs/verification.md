# Milestone 1 verification

This record belongs to the Instrument milestone. The screenshots show the native
Android build, not the generated design references. All temperatures are synthetic.

## Environment

- Local host: macOS, Apple Silicon.
- Java: Oracle JDK 17.0.11+7-LTS-207.
- Gradle 8.13, Android Gradle Plugin 8.12.0, Kotlin 2.2.10.
- Compile / target API 36, Build Tools 36.0.0; minimum API 26.
- Emulator: ComposeAtlas_API_36, Pixel 6 profile, Android 16 / API 36,
  Google APIs ARM64 image revision 7, emulator 35.6.11 (build 13610412),
  software graphics.
- Normal viewport: 1080 × 2400 px, density 420 dpi, font scale 1.0.
- Additional configurations: 1.3 font scale and a 2400 × 1080 px landscape window.
  Landscape captures use a window-size override, not a sensor-rotation test. The design comparison
  uses 851 × 1848 px at 320 dpi. These are different configurations of one emulator,
  not evidence from multiple physical devices.

## Automated checks

Run the commands from [the README](../README.md). Seven JVM tests cover fixtures,
coordinate mapping, selection boundaries, null-gap segmentation and state changes.
Six instrumented tests cover the main user flows, recreation, selection, series
visibility, empty/incomplete data and deterministic replay with the Compose test clock.

The animation test advances 32 ms into replay, then beyond its duration, and checks
that disabling motion makes the readout immediately available. It does not measure
frame timing or rendering performance. Activity recreation is tested; OS process
termination and cold-launch restoration are not claimed.

Captures are produced by the instrumented test helper after Compose settles and
the emulator compositor presents the frame. Gradle may uninstall test applications
after connected tests, so collect capture files before uninstalling when reproducing.

## Scope and limits

- No physical-device smoothness, energy or haptic claim.
- API 26 is the declared minimum and lint compatibility target, not a tested image.
- The app provides readable selected values and previous/next semantics. A complete
  manual TalkBack session and accessibility certification have not been performed.
- English and the Instrument dark theme are the first milestone's supported content.
- Landscape and enlarged text scroll vertically; no tablet-specific two-pane claim.
- Distinct day, week and month fixtures are authored examples, not statistical
  aggregations of one underlying measurement series.
- Empty and gaps are local scenarios. There is no simulated network loading or
  network-error state because this experiment has no remote data dependency.
- Release output is unsigned and has not been distributed through a store.

## Recorded local results — 2026-10-15

Application source: `303509b614298f83fbed57ce977fca23d04bc100`. The following
build-script change excludes only the `OldTargetApi` SDK-update notice from the
pinned API 36 milestone; functional source and captures are unchanged.

| Check | Result |
| --- | --- |
| ktlint, Android Lint, debug assembly | Passed |
| JVM unit tests | 7 passed |
| API 36 ARM64 instrumented suite | 6 passed |
| Enlarged-text and landscape-window navigation checks | Passed |
| Minified release assembly (R8) | Passed; unsigned APK |

The API 36 x86_64 emulator completed all six tests successfully in
[run 38663599253](https://github.com/FredDevAxes/compose-atlas/actions/runs/38663599253).
That run's separate checks job failed on the `OldTargetApi` notice; it is not an
overall CI pass. [Run 38664049394](https://github.com/FredDevAxes/compose-atlas/actions/runs/38664049394)
checks the corrected build configuration. Its GitHub status is the authoritative
result; later documentation-only commits use the same application source.

## Native visual evidence

[Explore](images/phone-home.png), [Adjust](images/phone-controls.png),
[Understand](images/phone-understand.png), [empty data](images/phone-empty.png),
[gaps](images/phone-gaps.png), [long labels](images/phone-long-labels.png),
[large text](images/large-controls.png), [large-text explanation](images/large-understand.png),
[landscape chart](images/landscape-controls.png) and
[landscape panel](images/landscape-understand.png) are native captures with embedded
origin metadata. Some controls require scrolling in the short-window layout.

The independent review resolved its native visual and semantic corrections and
found no material regression in this capture set. The strict reference-image
comparison remains **71.03%**, below its **72%** threshold, and the final review
disposition remains **fix** for that open gate. See the
[native adaptation record](design/native-adaptations.md). This milestone does not
claim a full visual approval or exact reproduction of the generated reference.


## Motion comparison follow-up

The 0.1.0 screenshots above are historical milestone evidence. The motion follow-up
adds two diagnostic lanes and 1×/0.5×/0.25× playback. Additional instrumented tests
check visible spring overshoot while the selected chart still reports replay,
normal/quarter-speed curve equivalence, completion, speed restoration and reset.
App-level motion-off also checks that both markers finish at 100%.

Local verification on 15 October 2026: ktlint, Android Lint, debug assembly,
7 JVM tests and all 8 instrumented tests passed on API 36 ARM64. This follow-up
uses the same AVD with host GPU rendering. A preliminary software-rendered run
was interrupted after device timeouts; an old replay test was also corrected to
scroll before freezing the test clock. Neither interrupted run is counted as a pass.

Confirmation captures: [quarter-speed overshoot](images/motion-quarter-overshoot.png),
[Understand](images/motion-understand.png), [large text](images/motion-large-understand.png),
and [landscape window](images/motion-landscape-understand.png). The selected flow
passed at normal size, font scale 1.3 and the 2400 × 1080 window override.


## Gallery milestone — 0.2.0

The gallery adds Quiet frequency, Object collection, Session builder and Spatial
agenda alongside Temperature study. The native captures below belong to the
0.2.0 source. Earlier chart captures remain historical evidence for their sections.

Local verification on 15 October 2026 used the same API 36 Google APIs ARM64 AVD,
1080 × 2400 at density 420, host GPU rendering, JDK 17.0.11 and the pinned toolchain.

| Check | Result |
| --- | --- |
| ktlint, Android Lint, debug app and test APK assembly | Passed |
| Minified release assembly (R8) | Passed; unsigned APK |
| JVM unit tests | 13 passed |
| Complete instrumented suite | 14 passed, none skipped |
| New-scene navigation and Understand at font scale 1.3 | Passed |
| New-scene navigation and Understand at 2400 × 1080 window override | Passed |
| Public image origin metadata | 24 rasters, none missing |
| Secret scan | Passed, no findings |

The six gallery tests include four functional scene flows, a player gesture/back
flow and a capture/navigation flow. They check playback completion and seeking,
paused restoration, empty queue recovery, favorite identity through filtering and
layout changes, empty search, IME focus and submission, local notes confirmation,
agenda time changes, overlap, zoom and recreation. The eight existing chart tests
also pass. Pure tests cover filtering, validation, interval lanes, clamped movement,
clock labels and existing chart math.

The first gallery run exposed a missing IME action in collection search. The
corrected search declares Done and clears focus. The visual pass also corrected
tab scroll sharing and clarified disabled timeline zoom in list mode. Failed
preliminary runs are not counted as successful verification.

### Native evidence

- [Gallery](images/gallery-home.png)
- [Player](images/gallery-player.png) and [compact player](images/gallery-player-compact.png)
- [Collection](images/gallery-collection.png)
- [Form](images/gallery-form.png) and [local confirmation](images/gallery-form-confirmation.png)
- [Overlapping agenda](images/gallery-agenda.png)
- [Understand](images/gallery-understand.png)
- [Large-text agenda list](images/gallery-large-agenda.png)
- [Landscape Understand](images/gallery-landscape-understand.png)

The large-text and landscape runs exercise scene navigation, scrolling and opening
Understand. They are not repeats of every functional scenario at every size.
The landscape evidence uses a window-size override, not a sensor-rotation test.
The displayed objects, waveform artwork, sessions and chart values are synthetic.

The independent extension review found the native visual direction consistent
with Instrument. Its sole material finding was stale design-system documentation.
The documenter updated DESIGN.md and its sidecar; the reviewer scored that fix
**resolved**, with disposition **ship** for that persistence fix. This is not a new
full-surface or hardware validation. The historical chart reproduction gate
(71.03% versus 72%) remains unchanged and outside this extension.

No real audio, network delivery, hardware performance, complete TalkBack session,
tablet-specific layout, light theme or exhaustive Compose API coverage is claimed.
The remaining experiments are tracked in [capability coverage](coverage.md).
