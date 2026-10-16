# Verification record

## v0.3.0 redesign

Scope: the eight-scene application, with global Normal/Slow motion/Off, light/dark
themes and a scene explanation. The user approved the main-branch merge and GitHub
push on 16 October 2026. The application identifies itself as `0.3.0` (version code 3).
Publication checks are recorded in [the v0.3.0 record](publication-v0.3.0.md).
The earlier public v0.2.0 test results do not verify this source.

## Verified source and results

Application source: local commit `888c913`, verified on 16 October 2026. All of the
checks below passed before publication. These are local execution results; see
the publication record for checks of the tagged revision and GitHub Actions.

| Check | Result |
| --- | --- |
| `ktlintCheck` | Passed |
| `lintDebug` | Passed: “No issues found.” |
| `testDebugUnitTest` | 6 tests passed |
| `assembleDebug` | Passed |
| `assembleRelease` | Passed; minified APK is unsigned |
| `connectedDebugAndroidTest` | 11 passed, none skipped or failed: 10 behavior tests and 1 capture tour |
| Clean local clone of `888c913` | Offline `ktlintCheck lintDebug testDebugUnitTest assembleDebug` passed |
| Capture tour variants | All 20 capture steps completed separately in light, dark, font scale 1.3 and a wide window |
| Physical device, TalkBack and performance profiling | Not verified |
| GitHub Actions on the published revision | See [publication record](publication-v0.3.0.md) |

The six unit tests cover local input validation, overlap lanes, motion policy,
chart selection boundaries and missing-reading fixtures. The ten behavior tests
cover scene explanations/navigation, controlled delivery time, global motion and
recreation, player transport/restoration, shop quantity/bag/Back, ticket validation
and IME focus, chart selection, poster controls/pinch/reset, inbox swipe/undo and
agenda movement. The capture tour is counted separately because generating
screenshots is not equivalent to a behavior test.

The final combined run is recorded in the local execution log
`atlas-final-checks.log`; the clean-clone check is in `atlas-clean-checks.log`.
Generated reports are under `app/build/reports/`, with connected-device XML under
`app/build/outputs/androidTest-results/`. These build outputs and temporary logs
are local evidence, not committed artifacts or public CI links.

The clean clone used an already installed JDK/SDK and populated dependency cache.
It verifies the committed source with an offline build; it does not verify a
first-time internet download or a different host environment. Its checks did not
repeat connected tests or release assembly.

## Recorded environment

- Host: macOS on Apple Silicon; JDK 17.0.11.
- Gradle 8.13, Android Gradle Plugin 8.12.0, Kotlin 2.2.10.
- Compose UI/Foundation/Animation 1.9.0; Material 3 1.3.2; ktlint 1.7.1.
- Compile/target API 36, Build Tools 36.0.0; minimum API 26.
- Emulator: `ComposeAtlas_API_36`, Android 16/API 36, ARM64, target `emulator-5554`.
- Phone captures: 1080 × 2400 px at 420 dpi, normal font scale unless labeled.
- Additional tours: system dark theme, font scale 1.3, and 1600 × 1000 px at 160 dpi.
  The wide configuration is a window/density override on the same emulator,
  not a separate tablet or physical device.
- Intermediate animation tests use system Animator duration scale 1×. Global
  Slow motion and Off are exercised separately by behavior tests.

An initial Android Lint/UAST run encountered a host JDK 17.0.11 C1 compiler VM
crash. The recorded final and clean-clone checks passed with the local JVM
workaround below. This is an environment workaround, not evidence that normal
commands always fail or an application crash.

## Reproduce the checks

From the repository root, with JDK 17 and the API 36 SDK packages installed:

```sh
./gradlew ktlintCheck lintDebug testDebugUnitTest assembleDebug
./gradlew connectedDebugAndroidTest
./gradlew assembleRelease
```

The combined verification command used on this host was:

```sh
./gradlew --no-daemon --max-workers=2 \
  '-Dorg.gradle.jvmargs=-Xmx3g -XX:TieredStopAtLevel=1 -Dfile.encoding=UTF-8' \
  ktlintCheck lintDebug testDebugUnitTest assembleDebug assembleRelease connectedDebugAndroidTest
```

The clean local clone used the same JVM options with `--offline` and the tasks
`ktlintCheck lintDebug testDebugUnitTest assembleDebug`.

Run formatting separately before checks if needed:

```sh
./gradlew ktlintFormat
```

The instrumented command needs one running emulator. Tests that inspect intermediate
frames need Android Animator duration scale at 1×; global and system motion-off
behavior should be recorded separately. Reports are under `app/build/reports/`;
connected-device result XML is under `app/build/outputs/androidTest-results/`.

The [workflow](../.github/workflows/android.yml) configures static checks, unit tests
and debug assembly on every push and PR, plus a separate API 36 Google APIs x86_64
Pixel 6 emulator job for instrumented tests. Actions are pinned to commit SHAs.
Configured coverage is not an execution result for an unpushed branch.

## Additional review protocol

For further runs, record the emulator image, Android API, architecture, graphics
mode, viewport, density, font scale, theme and system animation scale. Also record JDK, SDK,
Gradle and dependency versions when comparing runs. Gradle locks app dependencies;
it does not lock the emulator image or hosted runner.

- Open each of the eight scenes, perform its main task, expand How it works and
  return using both the app bar and system Back where relevant.
- Check shop detail Back, form IME focus and errors, inbox archive/undo/empty state,
  missing chart readings, player background pause and saved scene state.
- Compare Normal, Slow motion and Off through a finite delivery journey; separately
  inspect Android system animation scale zero. These modes control authored motion,
  not every Material internal animation or the simulated media clock.
- Check light/dark, enlarged text and short/wide windows for reachable controls and
  readable content. List the scenes actually checked instead of generalizing a pass.
- Test screen-reader traversal, announcements and alternatives to gestures in a
  dedicated session before reporting TalkBack coverage.

## Captures and provenance

The [visual tour](visual-tour.md) references native screenshots of this local
redesign under `docs/images/redesign/`. They show UI states, not design references.
The generated composition reference under `.impeccable/mocks/` is an autonomously
selected design input; no generated bitmap ships in the APK and no user approval
of that composition is claimed. Runtime illustrations use original Compose/Canvas
geometry. Captures correspond to source `888c913` and the emulator/configurations above.
Each configuration's tour completes the same 20 steps, for 80 raw captures across
four configurations. Documented selections and an overview are under
`docs/images/redesign/`. Native captures demonstrate these visual states; they
do not establish complete behavior, contrast or accessibility compliance in every
configuration.

## Limits and historical evidence

Synthetic data and local state exercise UI patterns; they do not verify payments,
audio, dispatch, email delivery, issued tickets or production backend behavior.
Emulator tests do not establish frame smoothness, battery consumption, haptics or
performance on physical devices. No comprehensive accessibility certification,
API 26 device run or dedicated tablet/foldable test is implied.

The published five-scene gallery and prior Temperature study have a separate
[archived verification record](archive/verification-v0.2.0.md), including their real
tags, runs, captures and limitations. Those historical results remain attributable
to their earlier revisions only.
