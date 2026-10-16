# Temperature study
Mode: Experience, with Operate controls and Read explanations.
> Historical v0.2.0 design record. The local redesign is governed by [the new direction contract](redesign/brief.md) and [DESIGN.md](../../DESIGN.md).

Approved comp: .impeccable/mocks/focused.png. Supporting explanation: .impeccable/mocks/guided.png.

## Direction contract
THESIS: A measuring surface where changes can be inspected, replayed and understood.
OWN-WORLD: Green-black surfaces, lime and dashed cyan traces, amber cursor; Material controls, clear measurement labels.
STORY: Open the scene, select a point, adjust an animation, then inspect the mechanism and source.
FIRST VIEWPORT: Compact header and period selector; chart occupies the upper two fifths; selection row below; a non-modal panel fills the lower half with Adjust / Understand, series controls, animation settings and Replay.
FORM: Instrument, selected from the oscilloscope challenger, seed 1ada881e. Composition B with C explanation content.
FINISH: unreviewed and undocumented is unfinished; this build ends with the finish review, the verdict, DESIGN.md, and every shipping raster carrying its provenance

## Native adaptation
Runtime charts and text are native Compose, never rasterized. System Roboto follows Android type conventions.
Content scrolls at large font scales and short heights. The chart remains separately inspectable.
Correct numeric coordinates replace inaccurate generated positions. No unavailable gallery entries.
