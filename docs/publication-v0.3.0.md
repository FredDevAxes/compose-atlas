# v0.3.0 publication record

The user approved merging the eight-scene redesign into `main` and pushing it to
GitHub on 16 October 2026. This replaces the initial local-only delivery restriction.
The design and interaction source is `888c913`; design documentation and raw
captures were recorded in `308563f`. The publication update changes the version
marker from `0.3.0-local` to `0.3.0` and adds reproducible presentation assets.

## Local checks

The v0.3.0 packaging passed `ktlintCheck lintDebug testDebugUnitTest assembleDebug
assembleRelease connectedDebugAndroidTest` on 16 October 2026: six unit tests,
eleven instrumented tests, no lint issues, debug and unsigned minified release APKs.
The local JDK workaround and environment are documented in [verification](verification.md).
The publication check log is retained locally as `atlas-publication-checks.log`.
The twelve presentation images passed dimensions, source/hash checks and embedded
provenance scanning. Gitleaks found no secret in the two redesign commits or the
prepared publication assets. The final publication commit is scanned before push.

## GitHub state

Main-branch integration, tag and GitHub Actions results are pending in this preparation
commit. This section will be updated from the actual remote state.

## Visuals

The [presentation gallery](presentation/README.md) contains eleven screen visuals
and one cover, rendered from unchanged native captures. Sources, dimensions and
hashes are retained in its [manifest](presentation/manifest.json), together with a
reproducible renderer and the font's license. No invented UI is used.

The independent publication-visual review returned **ship** for the cover, eleven
compositions and profile gallery. It confirmed complete screen inventory, whole
scaled captures, clear hierarchy and explicit simulation limits. No pixel-gate,
catalog quality-card or native accessibility certification is implied.

## Limits

An offline Android lab with fictional data. No real audio, payment, parcel dispatch
or issued ticket. API 36 emulator tests do not establish physical-device smoothness,
API 26 device compatibility, TalkBack quality or comprehensive accessibility compliance.
