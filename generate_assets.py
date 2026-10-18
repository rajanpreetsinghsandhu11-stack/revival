#!/usr/bin/env python3
import os
import random
import subprocess

from PIL import Image, ImageDraw

BASE = "src/main/resources/assets/revival"
os.makedirs(f"{BASE}/textures/entity", exist_ok=True)
os.makedirs(f"{BASE}/textures/item", exist_ok=True)
os.makedirs(f"{BASE}/sounds", exist_ok=True)

rng = random.Random(666)
skin = Image.new("RGBA", (64, 64))
pixels = skin.load()
for y in range(64):
    for x in range(64):
        v = rng.randint(22, 44)
        if rng.random() < 0.06:
            pixels[x, y] = (96, 16, 16, 255)
        else:
            pixels[x, y] = (v, v + rng.randint(4, 12), v - 4, 255)
skin.save(f"{BASE}/textures/entity/the_revived.png")

icon = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
draw = ImageDraw.Draw(icon)
draw.polygon([(8, 1), (13, 7), (9, 15), (6, 15), (3, 7)],
             fill=(70, 30, 100, 255), outline=(20, 8, 30, 255))
draw.polygon([(8, 4), (10, 7), (8, 12), (6, 7)], fill=(170, 110, 210, 255))
icon.save(f"{BASE}/textures/item/fragment_of_revival.png")

try:
    subprocess.run([
        "ffmpeg", "-y",
        "-f", "lavfi", "-i", "sine=frequency=55:duration=4",
        "-af", "tremolo=f=0.5:d=0.8,volume=0.6",
        "-c:a", "libvorbis",
        f"{BASE}/sounds/fragment_use.ogg",
    ], check=True)
except Exception:
    print("Warning: ffmpeg not found. Sound file skipped, but PNG textures were created successfully.")

print("Assets generated.")
