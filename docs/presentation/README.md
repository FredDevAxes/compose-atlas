# Compose Atlas — the screen gallery

![Compose Atlas v0.3.0: four native screens](cover.png)

Eleven views of the eight-scene Android lab, composed from actual emulator screenshots.
The [visual tour](../visual-tour.md) explains actions to try; the [raw captures](../images/redesign/manifest.json) retain their source and environment.

## Make it move.

Eight familiar app moments. Choose one. See Compose respond.

![Make it move.: Explore the gallery](screens/home.png)

## Pocket DJ

Play a little. Fold it away. A player that moves with its state.

![Pocket DJ: Animation · lifecycle · saved state](screens/music.png)

## Little shop

Open an object. Keep its identity. From collection to detail and bag.

![Little shop: Shared elements · animated content](screens/shop.png)

## On its way

Send a parcel. Follow the journey. One route drives motion and status.

![On its way: Canvas · PathMeasure · Animatable](screens/delivery.png)

## Energy story

Touch a bar. Inspect a reading. A missing sample stays a gap.

![Energy story: Canvas · selection · semantics](screens/energy.png)

## Your ticket

Enter your details. Make a preview. A form becomes a golden ticket.

![Your ticket: Focus · validation · custom Shape](screens/ticket.png)

## Poster studio

Pinch. Turn. Find another angle. Direct gestures with visible controls.

![Poster studio: Gestures · graphicsLayer](screens/poster.png)

## Inbox zero

Swipe a message. Make some room. Change your mind with Undo.

![Inbox zero: LazyColumn · stable keys · undo](screens/inbox.png)

## Make room

Move coffee. Watch the morning adjust. A custom layout resolves the overlap.

![Make room: Layout · overlap lanes · keyed motion](screens/agenda.png)

## At your pace

Normal, slow motion or off. One preference for every experience.

![At your pace: Global motion · Android system scale](screens/motion.png)

## See how it works

Try the interaction, then look inside. Mechanism, production use and limits.

![See how it works: A short explanation, when you want it](screens/how-it-works.png)

## Reproduce the images

From the repository root:

```sh
python3 -m venv .venv-presentation
. .venv-presentation/bin/activate
python -m pip install -r tools/presentation/requirements.txt
python tools/presentation/render.py
```

The renderer scales whole screenshots without cropping or retouching their UI. It adds a background, typography and an external shadow. The font is retained with its SIL Open Font License. [Manifest](manifest.json) records source files and hashes. The same capture set was verified in light, dark, enlarged-text and wide-window configurations; these presentation compositions use the light phone set.

This offline lab uses fictional data. The player is silent; no order, parcel or issued ticket is created. Emulator images do not establish physical-device performance or accessibility certification.
