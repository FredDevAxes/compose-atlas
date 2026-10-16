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

The eight-scene redesign was integrated into `main` by merge commit
[`34de0f3`](https://github.com/FredDevAxes/compose-atlas/commit/34de0f3ae6faf269a2014c564b62fecae7f44558), preserving the implementation,
design-documentation and publication-preparation commits.

The [integration workflow](https://github.com/FredDevAxes/compose-atlas/actions/runs/38906701386)
records hosted checks and API 36 x86_64 emulator tests for that merge. Later
documentation-only commits have their own entries in the [main-branch runs](https://github.com/FredDevAxes/compose-atlas/actions?query=branch%3Amain).
Read the revision and conclusion on each run; the local results above are recorded
separately. The `v0.3.0` tag identifies the published milestone.

The public FredDevAxes profile was updated in
[`3645185`](https://github.com/FredDevAxes/FredDevAxes/commit/364518586709f8f226fecd1bc39dea7f3b960420).
Its [dedicated Compose Atlas page](https://github.com/FredDevAxes/FredDevAxes/blob/master/labs/compose-atlas.md)
contains the cover and all eleven screen compositions. The home and Labs catalogue
link to that page; their titles retain the direct repository destination.
The public profile export validates 18 pages and 252 assets. Only the Compose Atlas
entry, dedicated page and its images changed; the other project pages are identical.

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
