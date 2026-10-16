"""Compose reproducible presentation images from unchanged native screenshots.

Usage: python tools/presentation/render.py (requires Pillow 12.3.0).
No UI is invented, retouched, cropped or generated. Screens are scaled uniformly.
"""
from __future__ import annotations

import hashlib
import json
from pathlib import Path

from PIL import Image, ImageDraw, ImageFilter, ImageFont, PngImagePlugin

ROOT = Path(__file__).resolve().parents[2]
OUTPUT = ROOT / "docs/presentation"
SOURCE = ROOT / "docs/images/redesign"
FONT = Path(__file__).parent / "fonts/Manrope.ttf"
INK = "#202436"
GROUND = "#F6F7FC"
SCREENS = [
    ("home", "Make it move.", ["Eight familiar app moments.", "Choose one. See Compose respond."], "home", "#E5E1FF", "Explore the gallery"),
    ("music", "Pocket DJ", ["Play a little. Fold it away.", "A player that moves with its state."], "music", "#FF9A86", "Animation · lifecycle · saved state"),
    ("shop", "Little shop", ["Open an object. Keep its identity.", "From collection to detail and bag."], "shop-detail", "#D6CBFF", "Shared elements · animated content"),
    ("delivery", "On its way", ["Send a parcel. Follow the journey.", "One route drives motion and status."], "delivery-arrived", "#C7E4FC", "Canvas · PathMeasure · Animatable"),
    ("energy", "Energy story", ["Touch a bar. Inspect a reading.", "A missing sample stays a gap."], "energy", "#C6EEDC", "Canvas · selection · semantics"),
    ("ticket", "Your ticket", ["Enter your details. Make a preview.", "A form becomes a golden ticket."], "ticket-confirmed", "#FFEAB0", "Focus · validation · custom Shape"),
    ("poster", "Poster studio", ["Pinch. Turn. Find another angle.", "Direct gestures with visible controls."], "photo-transformed", "#E7DBEF", "Gestures · graphicsLayer"),
    ("inbox", "Inbox zero", ["Swipe a message. Make some room.", "Change your mind with Undo."], "inbox-undo", "#FFEDBE", "LazyColumn · stable keys · undo"),
    ("agenda", "Make room", ["Move coffee. Watch the morning adjust.", "A custom layout resolves the overlap."], "agenda-moved", "#E1DEF4", "Layout · overlap lanes · keyed motion"),
    ("motion", "At your pace", ["Normal, slow motion or off.", "One preference for every experience."], "motion", "#E5E1FF", "Global motion · Android system scale"),
    ("how-it-works", "See how it works", ["Try the interaction, then look inside.", "Mechanism, production use and limits."], "explanation", "#C6EEDC", "A short explanation, when you want it"),
]


def font(size: int, weight: int = 600) -> ImageFont.FreeTypeFont:
    result = ImageFont.truetype(str(FONT), size)
    result.set_variation_by_axes([weight])
    return result


def screenshot(canvas: Image.Image, source: str, xy: tuple[int, int], height: int) -> None:
    image = Image.open(SOURCE / f"phone-{source}.png").convert("RGBA")
    width = round(image.width * height / image.height)
    image = image.resize((width, height), Image.Resampling.LANCZOS)
    # Only a soft presentation shadow is added outside the unchanged capture.
    shadow = Image.new("RGBA", canvas.size)
    ImageDraw.Draw(shadow).rectangle((xy[0], xy[1] + 12, xy[0] + width, xy[1] + height + 12), fill=(24, 36, 67, 35))
    canvas.alpha_composite(shadow.filter(ImageFilter.GaussianBlur(20)))
    canvas.alpha_composite(image, xy)


def save(canvas: Image.Image, target: Path, sources: list[str]) -> dict:
    target.parent.mkdir(parents=True, exist_ok=True)
    metadata = PngImagePlugin.PngInfo()
    metadata.add_text("Description", "Compose Atlas v0.3.0: composition of actual native Android screenshots; uniform scaling, no UI retouching.")
    metadata.add_text("Source", "; ".join(f"docs/images/redesign/phone-{name}.png, UI revision 888c913" for name in sources))
    metadata.add_text("Software", "Compose Atlas tools/presentation/render.py; Pillow 12.3.0")
    metadata.add_text("impeccable:prompt", "Origin: Compose Atlas native Android screenshots, UI source commit 888c913: " + "; ".join(f"docs/images/redesign/phone-{name}.png" for name in sources) + ". Composed by tools/presentation/render.py with uniform full-frame scaling, licensed Manrope typography and solid backgrounds; no UI retouching or generated screens.")
    canvas.convert("RGB").save(target, pnginfo=metadata, optimize=True)
    return {"file": str(target.relative_to(OUTPUT)), "pixels": list(canvas.size), "sha256": hashlib.sha256(target.read_bytes()).hexdigest(), "screenshots": [f"docs/images/redesign/phone-{name}.png" for name in sources]}


def main() -> None:
    files = []
    for slug, title, lines, source, color, mechanism in SCREENS:
        canvas = Image.new("RGBA", (1600, 1200), GROUND)
        draw = ImageDraw.Draw(canvas)
        draw.rectangle((930, 0, 1600, 1200), fill=color)
        draw.text((96, 130), "Compose Atlas", font=font(32, 650), fill=INK)
        draw.text((96, 325), title, font=font(68, 750), fill=INK)
        for index, line in enumerate(lines):
            draw.text((100, 455 + index * 48), line, font=font(29, 450), fill=INK)
        draw.line((100, 626, 790, 626), fill="#D1D4E1", width=2)
        draw.text((100, 662), mechanism, font=font(25, 600), fill="#4F46C8")
        draw.text((100, 1055), "Jetpack Compose · Android · v0.3.0", font=font(24, 550), fill="#535869")
        screenshot(canvas, source, (1050, 130), 940)
        files.append(save(canvas, OUTPUT / f"screens/{slug}.png", [source]))

    canvas = Image.new("RGBA", (2400, 1260), GROUND)
    draw = ImageDraw.Draw(canvas)
    draw.text((100, 100), "Compose", font=font(108, 800), fill=INK)
    draw.text((100, 220), "Atlas", font=font(108, 800), fill=INK)
    draw.text((106, 398), "Make it move.", font=font(57, 750), fill="#4F46C8")
    for index, line in enumerate(["Eight hands-on", "Jetpack Compose", "experiences."]):
        draw.text((108, 524 + index * 61), line, font=font(44, 500), fill=INK)
    draw.text((108, 1030), "Kotlin · Android · Material 3", font=font(30, 600), fill=INK)
    draw.text((108, 1080), "v0.3.0", font=font(28, 500), fill="#535869")
    for index, (source, color, label) in enumerate([("music", "#FF9A86", "Play"), ("shop-detail", "#D6CBFF", "Explore"), ("delivery", "#C7E4FC", "Follow"), ("ticket-confirmed", "#FFEAB0", "Create")]):
        x = 825 + index * 377
        draw.rectangle((x - 17, 100, x + 342, 1160), fill=color)
        screenshot(canvas, source, (x, 200), 720)
        draw.text((x + 10, 1025), label, font=font(35, 700), fill=INK)
    files.append(save(canvas, OUTPUT / "cover.png", ["music", "shop-detail", "delivery", "ticket-confirmed"]))
    manifest = {"version": "0.3.0", "ui_source_commit": "888c913", "capture_manifest": "../images/redesign/manifest.json", "method": "Native screenshots uniformly scaled and placed on presentation backgrounds. Screenshot pixels are not retouched or cropped.", "font": "Manrope, SIL OFL 1.1; tools/presentation/fonts/OFL.txt", "files": files}
    (OUTPUT / "manifest.json").write_text(json.dumps(manifest, indent=2) + "\n")
    entries = ["# Compose Atlas — the screen gallery", "", "![Compose Atlas v0.3.0: four native screens](cover.png)", "", "Eleven views of the eight-scene Android lab, composed from actual emulator screenshots.", "The [visual tour](../visual-tour.md) explains actions to try; the [raw captures](../images/redesign/manifest.json) retain their source and environment.", ""]
    for slug, title, lines, source, color, mechanism in SCREENS:
        entries.extend([f"## {title}", "", " ".join(lines), "", f"![{title}: {mechanism}](screens/{slug}.png)", ""])
    entries.extend(["## Reproduce the images", "", "From the repository root:", "", "```sh", "python3 -m venv .venv-presentation", ". .venv-presentation/bin/activate", "python -m pip install -r tools/presentation/requirements.txt", "python tools/presentation/render.py", "```", "", "The renderer scales whole screenshots without cropping or retouching their UI. It adds a background, typography and an external shadow. The font is retained with its SIL Open Font License. [Manifest](manifest.json) records source files and hashes. The same capture set was verified in light, dark, enlarged-text and wide-window configurations; these presentation compositions use the light phone set.", "", "This offline lab uses fictional data. The player is silent; no order, parcel or issued ticket is created. Emulator images do not establish physical-device performance or accessibility certification.", ""])
    (OUTPUT / "README.md").write_text("\n".join(entries))
    print(f"Rendered {len(files)} presentation visuals from native captures.")


if __name__ == "__main__":
    main()
