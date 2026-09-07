import json
import struct
import unittest
import zlib
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
GUI = ROOT / "src/main/resources/assets/enshrouded/textures/gui"
OVERLAY = ROOT / "src/main/java/com/gustavaopere/enshrouded/client/hud/ShroudHudOverlay.java"
MODEL = ROOT / "src/main/java/com/gustavaopere/enshrouded/client/hud/ExposureHudModel.java"
CONTRACT = ROOT / "plans/10-visual-polish/10-07-hud-ui-art.md"
LANG = ROOT / "src/main/resources/assets/enshrouded/lang"

ATLAS = GUI / "shroud_hud_icons.png"
ATLAS_WIDTH = 384
ATLAS_HEIGHT = 64
FRAME_WIDTH = 160
FRAME_HEIGHT = 60
SYMBOL_START_X = 320
SYMBOL_SIZE = 16


def decode_rgba8_png(path: Path):
    data = path.read_bytes()
    if data[:8] != b"\x89PNG\r\n\x1a\n":
        raise AssertionError(f"{path} is not a PNG")
    cursor = 8
    width = height = None
    idat = bytearray()
    while cursor < len(data):
        length = struct.unpack(">I", data[cursor:cursor + 4])[0]
        chunk_type = data[cursor + 4:cursor + 8]
        payload = data[cursor + 8:cursor + 8 + length]
        cursor += 12 + length
        if chunk_type == b"IHDR":
            width, height, bit_depth, color_type, compression, filter_method, interlace = struct.unpack(
                ">IIBBBBB", payload
            )
            if (bit_depth, color_type, compression, filter_method, interlace) != (8, 6, 0, 0, 0):
                raise AssertionError(f"{path} must remain non-interlaced 8-bit RGBA")
        elif chunk_type == b"IDAT":
            idat.extend(payload)
        elif chunk_type == b"IEND":
            break
    if width is None or height is None or not idat:
        raise AssertionError(f"{path} is missing required PNG chunks")

    raw = zlib.decompress(bytes(idat))
    stride = width * 4
    if len(raw) != height * (stride + 1):
        raise AssertionError(f"{path} has an unexpected decoded length")

    rows = []
    previous = bytearray(stride)
    offset = 0
    for _ in range(height):
        filter_type = raw[offset]
        offset += 1
        encoded = raw[offset:offset + stride]
        offset += stride
        row = bytearray(stride)
        for index, value in enumerate(encoded):
            left = row[index - 4] if index >= 4 else 0
            up = previous[index]
            up_left = previous[index - 4] if index >= 4 else 0
            if filter_type == 0:
                reconstructed = value
            elif filter_type == 1:
                reconstructed = value + left
            elif filter_type == 2:
                reconstructed = value + up
            elif filter_type == 3:
                reconstructed = value + ((left + up) // 2)
            elif filter_type == 4:
                p = left + up - up_left
                pa, pb, pc = abs(p - left), abs(p - up), abs(p - up_left)
                predictor = left if pa <= pb and pa <= pc else up if pb <= pc else up_left
                reconstructed = value + predictor
            else:
                raise AssertionError(f"{path} uses unsupported PNG filter {filter_type}")
            row[index] = reconstructed & 0xFF
        rows.append(row)
        previous = row

    pixels = []
    for row in rows:
        pixels.append(tuple(tuple(row[i:i + 4]) for i in range(0, stride, 4)))
    return width, height, tuple(pixels)


class Stage10HudUiArtContractTest(unittest.TestCase):
    def test_combined_hud_atlas_is_bounded_and_frames_are_structurally_distinct(self):
        self.assertTrue(ATLAS.is_file(), "Stage 10.07 requires the authored HUD atlas")
        width, height, pixels = decode_rgba8_png(ATLAS)
        self.assertEqual((ATLAS_WIDTH, ATLAS_HEIGHT), (width, height))

        ordinary_alpha = tuple(
            pixels[y][x][3]
            for y in range(FRAME_HEIGHT)
            for x in range(FRAME_WIDTH)
        )
        deadly_alpha = tuple(
            pixels[y][x + FRAME_WIDTH][3]
            for y in range(FRAME_HEIGHT)
            for x in range(FRAME_WIDTH)
        )
        self.assertNotEqual(
            ordinary_alpha,
            deadly_alpha,
            "Ordinary and Deadly frames must differ by shape/pattern, not only color",
        )
        alpha_delta = sum(a != b for a, b in zip(ordinary_alpha, deadly_alpha))
        self.assertGreaterEqual(
            alpha_delta,
            400,
            "Ordinary/Deadly frame topology is too similar for non-color accessibility",
        )

    def test_combined_hud_atlas_contains_four_shape_distinct_symbols(self):
        width, height, pixels = decode_rgba8_png(ATLAS)
        self.assertEqual((ATLAS_WIDTH, ATLAS_HEIGHT), (width, height))
        masks = []
        for cell in range(4):
            start_x = SYMBOL_START_X + cell * SYMBOL_SIZE
            masks.append(tuple(
                pixels[y][start_x + x][3]
                for y in range(SYMBOL_SIZE)
                for x in range(SYMBOL_SIZE)
            ))
        self.assertEqual(4, len(set(masks)), "HUD symbols must be shape-distinct, not palette aliases")

    def test_overlay_remains_stage03_projection_and_has_full_plus_minimal_render_paths(self):
        source = OVERLAY.read_text(encoding="utf-8")
        self.assertIn("ClientExposureState.INSTANCE", source)
        self.assertIn("ExposureHudModel.fromSnapshot(state.snapshot(), 0)", source)
        self.assertIn("AccessibilityProfile.MINIMAL", source)
        self.assertIn("renderMinimalHud", source)
        self.assertIn("renderFullHud", source)
        self.assertIn("renderMadnessBar", source)
        self.assertIn("HUD_ATLAS_TEXTURE", source)
        self.assertIn("model.passageWarning()", source)
        for forbidden in ("SavedData", "PacketDistributor", "sendToServer", "System.nanoTime()"):
            self.assertNotIn(
                forbidden,
                source,
                f"HUD presentation must not acquire gameplay/network authority via {forbidden}",
            )

    def test_madness_bar_uses_only_server_authored_stage(self):
        source = MODEL.read_text(encoding="utf-8")
        self.assertIn("public int madnessSegments()", source)
        self.assertIn("return switch (madnessStage)", source)
        self.assertNotIn("madnessPercent", source)
        self.assertNotIn("remainingTicks /", source)

    def test_ptbr_and_english_hud_keys_remain_in_parity(self):
        en = json.loads((LANG / "en_us.json").read_text(encoding="utf-8"))
        pt = json.loads((LANG / "pt_br.json").read_text(encoding="utf-8"))
        en_hud = {key for key in en if key.startswith("hud.enshrouded.")}
        pt_hud = {key for key in pt if key.startswith("hud.enshrouded.")}
        self.assertEqual(en_hud, pt_hud)

    def test_contract_preserves_authority_accessibility_and_manual_gates(self):
        text = CONTRACT.read_text(encoding="utf-8").lower()
        for phrase in (
            "stage 03 remains authoritative",
            "presentation-only",
            "no authoritative recomputation",
            "no new network packets",
            "no new saveddata",
            "color is not the sole distinction",
            "minimal remains readable",
            "fusion is optional",
            "geckolib is not required",
            "shader is not required",
            "art approved remains open",
            "612-mod",
            "reduced-effects",
            "screenshots",
            "pending",
        ):
            self.assertIn(phrase, text)


if __name__ == "__main__":
    unittest.main()
