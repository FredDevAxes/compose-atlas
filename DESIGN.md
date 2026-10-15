---
name: Compose Atlas
description: Native Android instruments for practical Compose experiments.
colors:
  lime: "#B9EE77"
  cyan: "#6FBDCB"
  amber: "#E9B965"
  background: "#0B1A19"
  surface: "#112523"
  outline: "#2D433C"
  text: "#EEF4F0"
  muted: "#B0C3BB"
  selected: "#395630"
typography:
  display-small:
    fontFamily: "Roboto, sans-serif"
    fontWeight: 600
  headline-small:
    fontFamily: "Roboto, sans-serif"
    fontWeight: 500
  title-large:
    fontFamily: "Roboto, sans-serif"
    fontWeight: 500
  title-medium:
    fontFamily: "Roboto, sans-serif"
    fontWeight: 500
  title-small:
    fontFamily: "Roboto, sans-serif"
    fontWeight: 500
  body-large:
    fontFamily: "Roboto, sans-serif"
    fontWeight: 400
  body-medium:
    fontFamily: "Roboto, sans-serif"
    fontWeight: 400
  body-small:
    fontFamily: "Roboto, sans-serif"
    fontWeight: 400
  label-large:
    fontFamily: "Roboto, sans-serif"
    fontWeight: 500
  label-medium:
    fontFamily: "Roboto, sans-serif"
    fontWeight: 500
  readout-time:
    fontFamily: "monospace"
  chart-axis:
    fontFamily: "sans-serif"
    fontSize: "11sp"
rounded:
  readout: "8dp"
  series: "10dp"
  chart-and-action: "12dp"
  gallery-card: "16dp"
  panel-top: "20dp"
spacing:
  step-4: "4dp"
  step-6: "6dp"
  step-8: "8dp"
  step-10: "10dp"
  step-12: "12dp"
  step-16: "16dp"
  step-20: "20dp"
  step-24: "24dp"
components:
  button-primary:
    backgroundColor: "{colors.lime}"
    textColor: "{colors.background}"
    rounded: "{rounded.chart-and-action}"
  choice-selected:
    backgroundColor: "{colors.selected}"
    textColor: "{colors.text}"
  choice-unselected:
    backgroundColor: "{colors.background}"
    textColor: "{colors.text}"
  gallery-card:
    backgroundColor: "{colors.surface}"
    rounded: "{rounded.gallery-card}"
    padding: "20dp"
  chart:
    backgroundColor: "{colors.background}"
    rounded: "{rounded.chart-and-action}"
    padding: "10dp"
  readout:
    backgroundColor: "{colors.surface}"
    rounded: "{rounded.readout}"
    padding: "10dp"
---

# Design System: Compose Atlas

## Overview

**Creative North Star: "Instrument"**

Instrument is a measuring surface where changes can be inspected, replayed and understood. Green-black fields hold clear measurements, solid lime and dashed cyan traces, and an amber selection cursor. Material 3 provides native Android interaction and text scaling.

This records the implemented dark-only system, extracted from InstrumentTheme.kt, AtlasApp.kt, ControlPanel.kt and TemperatureChart.kt under app/src/main/java/dev/devaxes/composeatlas/ui. PRODUCT.md establishes the offline, synthetic-data scope. The approved surface brief selects composition B with C explanations; it is not a requirement that future experiments repeat this exact screen.

**Key Characteristics:**
- Tonal green-black surfaces with restrained outlines.
- Series identity carried by color, line pattern and names.
- Native controls and readable, scalable text.
- One working experiment in an evolving gallery.

## Colors

The palette is a cool green-black instrument field with three functional accents. Exact values are in the frontmatter and come from InstrumentTheme.kt.

### Primary
- **Lime:** primary action and Studio trace; background is the on-primary color.
- **Selected green:** active segmented controls and primary container.

### Secondary
- **Cyan:** Gallery trace and supporting API text.

### Tertiary
- **Amber:** chart cursor, selected time and replay status. This is a direct Instrument token, not an override of Material's tertiary role.

### Neutral
- **Background:** screen and plot field.
- **Surface:** experiment card, readout and controls panel.
- **Text / Muted:** primary information / supporting copy and axis labels.
- **Outline:** container borders, dividers and chart grid.

**The Trace Identity Rule.** Keep Studio solid lime and Gallery dashed cyan wherever these series appear; pair the traces with explicit names.

## Typography

Roboto is supplied through the unchanged Material 3 typography scale. The source uses displaySmall, headlineSmall, titleLarge/Medium/Small, bodyLarge/Medium/Small and labelLarge/Medium; frontmatter records their observed weights without inventing custom size tokens. DisplaySmall is semibold on Explore; headlineSmall and titleLarge are explicitly medium. Native Material role sizes, line heights and spacing remain library-owned. The chart axis uses an explicit sp size converted through LocalDensity. Readout time uses the platform monospace face with labelMedium sizing.

**The Native Type Rule.** Use Material typography roles and system font scaling; the timestamp alone uses monospace.

The system face is an explicit native Android adaptation. The current Explore display treatment is recorded as source evidence, not promoted into a custom brand display face for future surfaces.

## Layout

The current implementation is one vertical column. Explore uses the outer spacing step of 24dp; its card has 20dp content padding. The experiment header uses system safe-drawing insets. Period controls and panel content use 16dp horizontal padding; common control gaps are 8dp. The panel has 8dp vertical padding and rounded top corners.

Within the detail content area, height below 600dp or font scale above 1.25 selects a whole-content scroll with a 240dp chart card. Otherwise the chart card is 31% of available height, clamped to 210–290dp, and the remaining panel scrolls independently. These are height/font-scale conditions, not tablet breakpoints. Explore's chart preview is 200dp high. No tablet-specific two-pane layout is implemented.

**The Reachable Controls Rule.** Preserve the scroll fallback when the available height is short or the system font size is enlarged.

## Elevation & Depth

Custom surfaces are flat and separated by tone and 1dp outlines. Material components retain their library state treatments; there is no custom shadow vocabulary. The controls panel is a non-modal surface in the layout, with a close action, rather than a draggable sheet.

**The Tonal Separation Rule.** Separate the plot, readout and control surface with tone and fine outlines, without adding decorative shadows.

## Shapes

Rounded containers use the source radii in frontmatter: small readout, series buttons, chart/action, gallery card and panel top corners. The panel's bottom corners are square. Native segmented controls use Material item shapes; standard outlined/text/icon buttons, sliders and switches retain Material shape and interaction defaults. Icons are Material vector assets, not text glyphs.

## Components

- **Primary buttons:** lime on green-black. Open experiment and Replay transition use the chart-and-action radius. Replay is disabled without a selected sample; the panel-reopen button retains its Material default shape.
- **Secondary actions:** native outlined buttons for point inspection, scenarios and View source; a text button resets. Point buttons disable at data boundaries. Scenario selection changes its outline to lime and exposes selected semantics.
- **ChoiceRow:** Material single-choice segments for period, panel tab and transition mode. Selected green marks the active choice; both states use text and outline roles. No decorative leading check icon is supplied.
- **SeriesButton:** outlined controls with a checkbox, a miniature solid/dashed trace and a name. Shown/Hidden semantics describe their state. The last visible series cannot be hidden.
- **LabeledSlider and Switch:** Material inputs with text labels and current values. Slider tracks use outline for the inactive portion and a 48dp layout height. Duration, damping and stiffness appear according to transition mode. No text-entry field or independent chip component exists.
- **Gallery card:** one available experiment, a non-interactive native Canvas preview and a full-width opening action.
- **Chart and readout:** 2dp traces, dashed Gallery pattern 6dp/5dp, amber 1dp cursor with 5dp/4dp dashes, 5dp markers with 2dp background centers. Grid strokes are 1dp with 3dp/3dp dashes. The readout carries time and named series values; replay temporarily shows its status until the trace reveal completes. Empty data has an explicit text state. Touch and accessibility actions inspect the same sample.
- **Controls panel:** Adjust exposes series, animation and scenario controls. Understand explains the mechanism and provides a source link. System Back closes the panel before returning to Explore; the header back action returns directly to Explore.

**The Honest Measurement Rule.** A gap stays missing, never zero; replay reveals the existing trace instead of fabricating measurements.

Animation is the experiment: Animatable reveals a clipped trace using the selected Tween or Spring. Inspection completes the reveal before presenting a selected point. This is not a decorative motion token for unrelated UI.

The sidecar's HTML/CSS snippets are illustrative documentation previews of these native components, not runtime implementations or exact Android renderings. Native controls own pressed, disabled, focus and accessibility behavior.

Review limit: docs/design/native-adaptations.md retains the strict fixed-coordinate comparison at 71.03%, below its 72% gate. Native adaptations are reviewed separately; this document does not assert an automated visual pass. Emulator evidence does not establish hardware performance or accessibility certification.

## Do's and Don'ts

### Do:
- Do use native Material controls with semantic labels and visible state.
- Do retain text readouts and Previous point / Next point alongside chart gestures.
- Do preserve system insets and scalable text.
- Do present only implemented gallery experiments as available.

### Don't:
- Don't replace native text or charts with reference-image rasters.
- Don't rely on color alone to distinguish the two series.
- Don't imply light-theme, dynamic-color or tablet two-pane support exists in this milestone.
