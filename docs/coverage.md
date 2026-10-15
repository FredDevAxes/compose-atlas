# Capability coverage

This table describes milestone scope, not all of Jetpack Compose.

| Capability | Observable example | Verification |
| --- | --- | --- |
| Canvas and paths | Two series, fixed axes, cursor and gaps | Pure coordinate and segmentation tests; emulator screenshots |
| Pointer input | Tap and horizontal drag select a sample | Instrumented edge selection |
| Animation | Tween / Spring trace reveal, replay, motion toggle | Controlled test-clock assertions |
| State and restoration | Period, selection, series and controls | Unit transitions; activity recreation |
| Material controls | Segments, sliders, switches, actions | Instrumented user flows |
| Semantics | Text readout, point actions, series names | Accessibility tree plus manual review scope in verification |
| Empty/incomplete content | Empty, Gaps and Long labels fixtures | Deterministic data and UI scenarios |
| Window adaptation | Short-height and enlarged-text scrolling | Captures listed in verification |
| Navigation | Explore → experiment → panel close → back | UI navigation test and saved detail state |

Not included: a complete component catalogue, custom Layout/Modifier nodes,
shared-element navigation, production data loading, View interoperability,
baseline profiles, hardware benchmarks, light theme, tablet-specific two-pane UI
or other gallery experiments. These are separate future milestones.
