---
name: Compose Atlas
description: A native Android exhibit of eight hands-on Compose experiences.
colors:
  light-primary: "#4F46C8"
  light-on-primary: "#FFFFFF"
  light-primary-container: "#E5E1FF"
  light-on-primary-container: "#252052"
  light-secondary: "#536172"
  light-on-secondary: "#FFFFFF"
  light-secondary-container: "#E0E9F6"
  light-on-secondary-container: "#172434"
  light-tertiary: "#9A3D2B"
  light-on-tertiary: "#FFFFFF"
  light-background: "#F6F7FC"
  light-on-background: "#202436"
  light-surface: "#F6F7FC"
  light-on-surface: "#202436"
  light-surface-container: "#EBEDF5"
  light-surface-container-low: "#F0F1F8"
  light-surface-container-high: "#E5E7F0"
  light-surface-container-highest: "#DFE1EA"
  light-on-surface-variant: "#535869"
  light-outline: "#767B8C"
  light-outline-variant: "#D1D4E1"
  dark-primary: "#C5BFFF"
  dark-on-primary: "#282064"
  dark-primary-container: "#40358E"
  dark-on-primary-container: "#E5E1FF"
  dark-secondary: "#BAC8DC"
  dark-on-secondary: "#243244"
  dark-secondary-container: "#39465A"
  dark-on-secondary-container: "#E0E9F6"
  dark-tertiary: "#FFB4A3"
  dark-on-tertiary: "#5D170B"
  dark-background: "#141622"
  dark-on-background: "#E6E7F3"
  dark-surface: "#141622"
  dark-on-surface: "#E6E7F3"
  dark-surface-container: "#212431"
  dark-surface-container-low: "#1B1E2A"
  dark-surface-container-high: "#2B2E3D"
  dark-surface-container-highest: "#363949"
  dark-on-surface-variant: "#C2C5D6"
  dark-outline: "#8D92A5"
  dark-outline-variant: "#424658"
  gallery-music: "#FF9A86"
  gallery-shop: "#E1DBFF"
  gallery-delivery: "#C7E4FC"
  gallery-energy: "#C6EEDC"
  gallery-ticket: "#FFEAB0"
  gallery-poster: "#E7DBEF"
  gallery-inbox: "#FFEDBE"
  gallery-agenda: "#E1DEF4"
  object-ink: "#182443"
  record-coral: "#FF8D79"
  record-cream: "#FFE7BC"
  shop-lavender: "#D6CBFF"
  shop-peach: "#FFC8AF"
  delivery-cyan: "#007A87"
  delivery-ground: "#E5F7F7"
  energy-ink: "#163D31"
  energy-bar: "#61A988"
  ticket-coral: "#FFA38C"
  ticket-gold: "#FFD875"
  inbox-yellow: "#FFD968"
  poster-violet: "#7754CF"
  poster-peach: "#FFC1AA"
  poster-ground: "#EDE6FF"
  agenda-review: "#DCD3FA"
  agenda-coffee: "#FFD6C5"
  agenda-build: "#C8E8DA"
typography:
  display-small:
    fontFamily: "Android default sans-serif (Roboto on the reviewed emulator)"
    fontSize: "36sp"
    fontWeight: 400
    lineHeight: "44sp"
    letterSpacing: "0sp"
  headline-large:
    fontFamily: "Android default sans-serif (Roboto on the reviewed emulator)"
    fontSize: "32sp"
    fontWeight: 400
    lineHeight: "40sp"
    letterSpacing: "0sp"
  headline-medium:
    fontFamily: "Android default sans-serif (Roboto on the reviewed emulator)"
    fontSize: "28sp"
    fontWeight: 400
    lineHeight: "36sp"
    letterSpacing: "0sp"
  headline-small:
    fontFamily: "Android default sans-serif (Roboto on the reviewed emulator)"
    fontSize: "24sp"
    fontWeight: 400
    lineHeight: "32sp"
    letterSpacing: "0sp"
  title-large:
    fontFamily: "Android default sans-serif (Roboto on the reviewed emulator)"
    fontSize: "22sp"
    fontWeight: 400
    lineHeight: "28sp"
    letterSpacing: "0sp"
  title-medium:
    fontFamily: "Android default sans-serif (Roboto on the reviewed emulator)"
    fontSize: "16sp"
    fontWeight: 500
    lineHeight: "24sp"
    letterSpacing: "0.15sp"
  title-small:
    fontFamily: "Android default sans-serif (Roboto on the reviewed emulator)"
    fontSize: "14sp"
    fontWeight: 500
    lineHeight: "20sp"
    letterSpacing: "0.1sp"
  body-large:
    fontFamily: "Android default sans-serif (Roboto on the reviewed emulator)"
    fontSize: "16sp"
    fontWeight: 400
    lineHeight: "24sp"
    letterSpacing: "0.5sp"
  body-medium:
    fontFamily: "Android default sans-serif (Roboto on the reviewed emulator)"
    fontSize: "14sp"
    fontWeight: 400
    lineHeight: "20sp"
    letterSpacing: "0.25sp"
  body-small:
    fontFamily: "Android default sans-serif (Roboto on the reviewed emulator)"
    fontSize: "12sp"
    fontWeight: 400
    lineHeight: "16sp"
    letterSpacing: "0.4sp"
  label-large:
    fontFamily: "Android default sans-serif (Roboto on the reviewed emulator)"
    fontSize: "14sp"
    fontWeight: 500
    lineHeight: "20sp"
    letterSpacing: "0.1sp"
  label-medium:
    fontFamily: "Android default sans-serif (Roboto on the reviewed emulator)"
    fontSize: "12sp"
    fontWeight: 500
    lineHeight: "16sp"
    letterSpacing: "0.5sp"
  label-small:
    fontFamily: "Android default sans-serif (Roboto on the reviewed emulator)"
    fontSize: "11sp"
    fontWeight: 500
    lineHeight: "16sp"
    letterSpacing: "0.5sp"
rounded:
  event: "12dp"
  row-and-choice: "16dp"
  gallery-and-product: "20dp"
  working-object: "24dp"
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
  button-primary-light:
    backgroundColor: "{colors.light-primary}"
    textColor: "{colors.light-on-primary}"
    typography: "{typography.label-large}"
  button-primary-dark:
    backgroundColor: "{colors.dark-primary}"
    textColor: "{colors.dark-on-primary}"
    typography: "{typography.label-large}"
  gallery-card-music:
    backgroundColor: "{colors.gallery-music}"
    textColor: "{colors.light-on-surface}"
    rounded: "{rounded.gallery-and-product}"
    padding: "16dp"
  gallery-card-shop:
    backgroundColor: "{colors.gallery-shop}"
    textColor: "{colors.light-on-surface}"
    rounded: "{rounded.gallery-and-product}"
    padding: "16dp"
  motion-choice-selected-light:
    backgroundColor: "{colors.light-primary-container}"
    rounded: "{rounded.row-and-choice}"
    padding: "12dp"
  motion-choice-selected-dark:
    backgroundColor: "{colors.dark-primary-container}"
    rounded: "{rounded.row-and-choice}"
    padding: "12dp"
  energy-surface:
    backgroundColor: "{colors.gallery-energy}"
    textColor: "{colors.energy-ink}"
    rounded: "{rounded.working-object}"
    padding: "20dp"
  poster-stage:
    backgroundColor: "{colors.poster-ground}"
    rounded: "{rounded.working-object}"
    height: "294dp"
---

# Design System: Compose Atlas

## Overview

**Creative North Star: "Hands-on exhibit"**

Hands-on exhibit makes a familiar app object the first invitation: a record, a lamp, a parcel, a chart, a ticket, a poster, an envelope or a morning schedule. Broad pastel fields and clear dark silhouettes make each experience recognizable. The surrounding Android interface stays quiet, with indigo actions, native type and a direct route back to the gallery.

The working object and ordinary action lead; technical explanations unfold afterward. Object continuity supplies the signature: a shop object keeps its identity as it moves from the collection into its detail view. Other transitions explain a concrete state change through folding, travel, placement, reveal or transformation. All artwork is original runtime Canvas or Compose geometry; no concept bitmap, photograph or remote artwork ships in the app.

This is a scan of the local implementation in `AtlasTheme.kt`, `ExperimentGallery.kt` and `ui/scenes/`, following `PRODUCT.md` and `docs/design/redesign/brief.md`. The generated concept in `.impeccable/mocks/playground-concept.png` is a composition reference selected by the orchestrator under the user's autonomous mandate. It is not a user-approved pixel contract. The former Instrument system is superseded for these eight scenes. This document records implementation, not publication, performance or accessibility certification.

**Key Characteristics:**
- Eight working scenes: Pocket DJ, Little shop, On its way, Energy story, Your ticket, Poster studio, Inbox zero and Make room.
- Light and dark native shells with scene artwork retaining its own purposeful palette.
- Material 3 controls, Android Back, scalable text, system insets and alternatives to gestures.
- One global authored-motion policy: Normal, Slow motion and Off, subordinate to Android animation settings.

## Colors

The shell moves between cool blue-white and midnight, while warm and cool scene fields preserve object identity in both themes. Frontmatter values are the extracted native tokens; Kotlin remains the runtime source of truth. Theme-prefixed names document the two explicit Material color schemes, not an in-app theme selector or dynamic-color support.

### Primary

- **Indigo / Pale indigo** (`light-primary`, `dark-primary`) carry primary actions, text actions and selected controls. Their matching on-primary colors provide the label contrast.
- **Soft indigo / Deep indigo containers** carry selected Motion choices, with Material retaining its native state treatment.

### Secondary

- **Slate blue** theme roles support tonal secondary actions such as the energy day switch.
- **Scene fields** identify records in coral, objects in lavender, delivery in blue, energy in mint, tickets in cream-gold, posters in lilac, inbox in warm cream and agenda in pale lavender. The actual working object may use a stronger related color than its gallery tile.

### Tertiary

- **Terracotta / Soft coral** are explicit Material tertiary roles. Strong coral, golden yellow, violet and cyan also belong to particular scene objects; they do not replace the shell's indigo actions.

### Neutral

- **Blue-white / Midnight** backgrounds and surfaces hold native reading and controls.
- **Ink / Pale text** are the corresponding foregrounds; surface-variant foregrounds carry supporting copy.
- **Tonal surface containers** separate inbox rows, agenda and motion choices. Outline roles serve fields, dividers and outlined controls.
- **Object ink** is paired with the fixed pastel art rather than inherited from dark-mode shell text. Energy, ticket and poster objects use their own explicit dark inks in code.

## Typography

**Display and body font:** Android's default sans-serif through the default Material 3 `Typography`; the reviewed emulator resolves it as Roboto. There is no bundled display family or custom type scale. The frontmatter records the library's default roles in scalable pixels. Local `Bold`, `Black` and `SemiBold` overrides strengthen scene headings and selected content without changing the role scale.

### Hierarchy

- **Display small** is the gallery invitation, inbox greeting and energy value. Prominent uses apply Bold.
- **Headline large / medium / small** introduce familiar tasks, track titles, event artwork and scene headings. Object lettering can use Black; ordinary headings use Bold.
- **Title large / medium / small** label navigation, gallery rows, products, weekdays and agenda events.
- **Body large / medium / small** carry tasks, descriptions, production explanations and the offline boundary copy.
- **Label large / medium / small** serve actions, times, status, form-adjacent information and compact object details. Uppercase is confined to intentional artwork labels such as SIDE A and ADMIT ONE.

**The Native Type Rule.** Use Material typography roles and Android font scaling; allow text to wrap and content to scroll instead of shrinking copy into an illustration.

## Layout

The app uses a one-level saved route stack: gallery, scene and global Motion settings, with system Back and an app-bar back action. Shop detail consumes Back before leaving its scene. `Scaffold` supplies app-bar insets; scene pages also account for the IME. The gallery scrolls as a page with a centered maximum width of 1000dp, 20dp horizontal padding, 12dp vertical padding and 20dp vertical groups. A full-width Pocket DJ feature leads the inventory. Other tiles use 12dp gutters.

The gallery chooses three columns at available widths of at least 840dp. Below that threshold, it uses one column when the width is below 340dp or font scale exceeds 1.35; otherwise it uses two. The expanded-width branch takes precedence over font scale. Feature artwork is 150dp high; other thumbnails are 112dp high. Cards use 16dp padding and a 6dp inner gap.

Scene bodies scroll vertically and are centered at a maximum width of 640dp, with 20dp horizontal and 12dp vertical outer padding. Main content groups use 16dp gaps; local scenes also use 8dp, 10dp, 12dp and 20dp where the object or controls require them. Motion settings share the 640dp maximum with 24dp padding and 16dp groups. Wide screens preserve these reading widths instead of stretching controls across the device.

Agenda uses a 330dp timeline and a measured event layout at ordinary font scale; above 1.2 it presents chronological full-width event blocks. Inbox uses a finite lazy-list viewport inside the scrolling scene: 300dp normally, 440dp above font scale 1.3, and 180dp when empty. At larger text sizes its Archive action moves below message text. Undo waits for the restored provider to reach layout and scrolls the restored message into view.

Poster stage height is 294dp. Its working paper is limited by both width and a 210dp cap. The thumbnail fits its rotated geometry within both axes and clips to its preview bounds. Delivery thumbnail labels are omitted to preserve a clean small-scale route; the full working map retains endpoint labels.

## Elevation & Depth

The interface is flat at rest: tone, spacing and clipping distinguish objects and controls. There is no app-wide card-shadow vocabulary. The transformed poster is an intentional exception, using native `graphicsLayer` shadow elevation (12dp) to lift the paper above its lilac stage. Small drawn shadows under shop silhouettes and the parcel marker belong to the Canvas artwork, not to reusable UI elevation. Material controls retain their library-provided state, ripple, focus and elevation behavior.

## Shapes

Large working stages use softly rounded corners (24dp); gallery tiles and shop artwork use 20dp. Motion choices and swipe rows use 16dp, while agenda blocks use 12dp. These explicit native radii are in the frontmatter. Pill buttons, radio buttons, sliders, fields and native feedback keep Material component defaults unless code supplies an override. Record transport uses a circular/pill silhouette.

The admission ticket uses a true custom outline with rounded outer corners (up to 16dp) and side cutouts (up to 11dp) aligned to the measured footer split. The notches reveal whichever parent surface is behind them in either theme; they are not painted background-colored circles. Perforation and barcode are decorative geometry; the barcode is explicitly not scannable.

## Components

### Actions and native controls

Primary `Button`, `OutlinedButton`, `FilledTonalButton`, `TextButton`, `IconButton`, `Slider` and `RadioButton` retain native Material semantics and feedback. Main transactional demonstrations commonly use a minimum 52dp action height; poster and quantity alternatives use at least 48dp, and music transport uses 56dp. Preserve the local constraints in code rather than imposing one new global button size. Music transport uses shell foreground/background colors instead of indigo, giving Play a strong dark silhouette in light mode and the corresponding reversed pairing in dark mode.

### Outlined fields

Your ticket uses native single-line Name and Email fields with persistent labels, IME Next/Done, supporting validation errors and focus on the first invalid field. The keyboard and button share the same validation. Successful submission creates a local preview; Edit my details returns to the retained form. Error styling comes from Material, including roles not explicitly overridden by AtlasTheme.

### Gallery cards and navigation

Entire tiles are clickable rounded `Surface`s with a fixed art field, title and forward arrow. The featured tile includes a short invitation; smaller tiles rely on recognizable art and their title. The app bar uses standard Android icon buttons for back and settings. Do not introduce a bottom navigation bar or resurrect the former experiment-tab shell.

### Working objects and explanations

**The Object and Action Rule.** Keep the working object recognizable and its main action available before the optional implementation explanation.

- **Pocket DJ:** a coral sleeve holds original vinyl geometry. Fold/Expand springs its artwork height between 230dp and 100dp. Track changes animate title content; the native seek slider and transport update a local, silent simulation. Playback stops when the app backgrounds; leaving the scene cancels its clock.
- **Little shop:** original lamp and stool silhouettes share their actual artwork element between collection and detail using `SharedTransitionLayout`. Quantity and bag totals are functional local state, with polite announcements; no checkout occurs.
- **On its way:** the courier and revealed trail use distance along one curved `PathMeasure` path. Preparing, On the way and At your door are explicit text states with a polite description. Send becomes a disabled progress action, then Replay delivery.
- **Energy story:** bar heights animate between synthetic day datasets, with a selected dot and readout. Tap/drag and Earlier/Later inspect the same indexed values. The day switch is a tonal action.
- **Your ticket:** event artwork becomes a measured, notched ticket preview with retained attendee details and decorative barcode. The preview semantics state that it is not an issued admission ticket.
- **Poster studio:** pinch, turn and drag directly manipulate original paper artwork. Visible Zoom −, Zoom +, Rotate and Reset actions plus custom accessibility actions provide alternatives. Programmatic changes animate; direct gestures track the fingers. Pan is bounded, zoom is clamped and reset restores the full composition.
- **Inbox zero:** stable message IDs drive swipe archive and placement animation. A visible Archive action and custom accessibility action provide alternatives; snackbar Undo restores a settled row and brings it into view. Restore all / Bring them back recovers all synthetic messages.
- **Make room:** one button moves coffee and reflows the morning. Event identity keys preserve position and width animation across sorting. The enlarged-text fallback retains event names, times and the same move action.

Every scene ends with a divider and a full-width How it works text action. Its expanding/fading explanation contains a mechanism heading, concise implementation explanation, an In a real app use case, limitations and a source-file pointer. It is initially closed and saved with the route state.

**The Honest Reading Rule.** A missing energy sample remains a visible gap with a No reading readout and a textual explanation; it never becomes a measured zero.

### Global motion

**The System Motion Rule.** Every authored transition consumes AtlasMotion. Off and Android animation scale zero show final state immediately; observe system changes while the app remains open.

`AtlasMotion` is supplied once through a composition local. Normal uses authored durations; Slow motion multiplies tween durations by three; Off uses zero-duration tweens and snapped springs. Default tweens use 400ms with `FastOutSlowInEasing`; scenes select smaller or longer finite durations for the event they illustrate. Springs use medium-bouncy damping and stiffness 315 normally, 35 in Slow mode. Spring timing is physics-based, so the three-times description is approximate for these transitions. Delivery uses a finite 6400ms normal journey scaled to remaining progress; chart bars use 650ms and agenda positions/widths use 700ms before policy scaling.

A `ContentObserver` watches `Settings.Global.ANIMATOR_DURATION_SCALE` and updates the policy while the app is in the foreground. A zero system value disables authored motion even when the saved preference is Normal or Slow. Nonzero Android duration scaling is still applied by Compose. Native component interactions keep their platform motion behavior. Motion preferences are global and saved; there are no per-scene technical tuning panels.

## Do's and Don'ts

### Do:
- Do build on native Material 3 controls, semantic labels, visible state and Android Back.
- Do use the current light/dark role pairs for shell text and actions; keep scene artwork colors paired with their explicit inks.
- Do retain touch-independent chart inspection, poster actions, visible Archive and undo/restore paths.
- Do keep the working object, explanatory copy and global motion controls reachable with enlarged text.
- Do render original artwork as runtime geometry and document only implemented behavior.

### Don't:
- Don't ship the generated concept image as a screen, thumbnail or functional UI.
- Don't restore the obsolete dark-only Instrument palette, per-scene tuning panels or Experiment / Understand tabs.
- Don't rely on color or a gesture alone to communicate a state change or permit an action.
- Don't claim real playback, purchases, dispatch, issued admission tickets or real-device performance from this offline lab.
- Don't treat the concept reference, documentation previews or emulator captures as user-approved pixel equivalence or accessibility certification.
