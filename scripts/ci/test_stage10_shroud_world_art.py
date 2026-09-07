import json
import struct
import unittest
import zlib
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
ASSETS = ROOT / "src/main/resources/assets/enshrouded"
BLOCKSTATES = ASSETS / "blockstates"
MODELS = ASSETS / "models/block"
TEXTURES = ASSETS / "textures/block"
CONTRACT = ROOT / "plans/10-visual-polish/10-06-shroud-world-art-family.md"

BLOCK_FAMILIES = {
    "shroud_growth": "shroud_growth_ordinary_",
    "shroud_vein": "shroud_vein_ordinary_",
    "withered_growth": "withered_growth_deadly_",
}

REQUIRED_TEXTURES = [
    "shroud_growth_ordinary_a.png",
    "shroud_growth_ordinary_b.png",
    "shroud_growth_ordinary_c.png",
    "shroud_vein_ordinary_a.png",
    "shroud_vein_ordinary_b.png",
    "shroud_vein_ordinary_c.png",
    "shroud_membrane_ordinary.png",
    "shroud_crust_ordinary.png",
    "withered_growth_deadly_a.png",
    "withered_growth_deadly_b.png",
    "withered_growth_deadly_c.png",
    "shroud_membrane_deadly.png",
    "shroud_crust_deadly.png",
    "red_sludge_still.png",
    "red_sludge_flow.png",
]


def load_json(path: Path):
    return json.loads(path.read_text(encoding="utf-8"))


def png_dimensions(path: Path):
    data = path.read_bytes()
    if data[:8] != b"\x89PNG\r\n\x1a\n":
        raise AssertionError(f"{path} is not a PNG")
    if data[12:16] != b"IHDR":
        raise AssertionError(f"{path} has no IHDR at the canonical PNG position")
    return struct.unpack(">II", data[16:24])


def _paeth(a: int, b: int, c: int) -> int:
    p = a + b - c
    pa = abs(p - a)
    pb = abs(p - b)
    pc = abs(p - c)
    if pa <= pb and pa <= pc:
        return a
    if pb <= pc:
        return b
    return c


def decode_rgba8_png(path: Path):
    """Decode the first-party 8-bit RGBA PNGs with stdlib only for rendered-pixel assertions."""
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
                raise AssertionError(
                    f"{path} must remain non-interlaced 8-bit RGBA for deterministic CI decoding"
                )
        elif chunk_type == b"IDAT":
            idat.extend(payload)
        elif chunk_type == b"IEND":
            break

    if width is None or height is None or not idat:
        raise AssertionError(f"{path} is missing required PNG chunks")

    raw = zlib.decompress(bytes(idat))
    bytes_per_pixel = 4
    stride = width * bytes_per_pixel
    expected = height * (stride + 1)
    if len(raw) != expected:
        raise AssertionError(f"{path} decoded byte count {len(raw)} != expected {expected}")

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
            left = row[index - bytes_per_pixel] if index >= bytes_per_pixel else 0
            up = previous[index]
            up_left = previous[index - bytes_per_pixel] if index >= bytes_per_pixel else 0
            if filter_type == 0:
                reconstructed = value
            elif filter_type == 1:
                reconstructed = value + left
            elif filter_type == 2:
                reconstructed = value + up
            elif filter_type == 3:
                reconstructed = value + ((left + up) // 2)
            elif filter_type == 4:
                reconstructed = value + _paeth(left, up, up_left)
            else:
                raise AssertionError(f"{path} uses unsupported PNG filter {filter_type}")
            row[index] = reconstructed & 0xFF
        rows.append(row)
        previous = row

    pixels = []
    for row in rows:
        pixels.extend(tuple(row[index:index + 4]) for index in range(0, stride, 4))
    return width, height, tuple(pixels)


class Stage10ShroudWorldArtContractTest(unittest.TestCase):
    def test_environment_blocks_use_exact_distinct_weighted_visual_variants(self):
        for block_id, model_prefix in BLOCK_FAMILIES.items():
            payload = load_json(BLOCKSTATES / f"{block_id}.json")
            variants = payload.get("variants", {}).get("")
            self.assertIsInstance(variants, list, f"{block_id} must use weighted baked variants")
            self.assertEqual(3, len(variants), f"{block_id} must use exactly the authored a/b/c variant set")
            models = [entry.get("model", "") for entry in variants]
            expected = {f"enshrouded:block/{model_prefix}{suffix}" for suffix in "abc"}
            self.assertEqual(expected, set(models), f"{block_id} must reference each authored model exactly once")
            self.assertEqual(len(models), len(set(models)), f"{block_id} cannot repeat one weighted model")
            for model in models:
                model_path = MODELS / f"{model.removeprefix('enshrouded:block/')}.json"
                self.assertTrue(model_path.is_file(), f"{block_id} references missing model {model}")
            self.assertTrue(all(int(entry.get("weight", 1)) >= 1 for entry in variants))

    def test_world_art_models_are_authored_geometry_not_single_cross_placeholders(self):
        expected_models = [
            *(f"shroud_growth_ordinary_{suffix}.json" for suffix in "abc"),
            *(f"shroud_vein_ordinary_{suffix}.json" for suffix in "abc"),
            *(f"withered_growth_deadly_{suffix}.json" for suffix in "abc"),
        ]
        for filename in expected_models:
            payload = load_json(MODELS / filename)
            self.assertGreaterEqual(len(payload.get("elements", [])), 3, f"{filename} needs layered authored geometry")
            self.assertNotEqual(payload.get("parent"), "minecraft:block/cross")
            textures = "\n".join(str(value) for value in payload.get("textures", {}).values())
            self.assertRegex(textures, r"enshrouded:block/shroud_(growth|vein|membrane|crust)|enshrouded:block/withered_growth")

    def test_material_family_textures_are_real_sized_and_bounded(self):
        for filename in REQUIRED_TEXTURES:
            path = TEXTURES / filename
            self.assertTrue(path.is_file(), f"missing Stage 10.06 material texture {filename}")
            width, height = png_dimensions(path)
            self.assertGreaterEqual(width, 32, f"{filename} remains below the Stage 10 art baseline")
            self.assertGreaterEqual(height, 32, f"{filename} remains below the Stage 10 art baseline")
            self.assertLessEqual(width, 64, f"{filename} exceeds the bounded texture budget")
            self.assertLessEqual(height, 64, f"{filename} exceeds the bounded texture budget")

    def test_ordinary_and_deadly_materials_have_distinct_rendered_pixel_structure(self):
        for material in ("membrane", "crust"):
            ordinary_path = TEXTURES / f"shroud_{material}_ordinary.png"
            deadly_path = TEXTURES / f"shroud_{material}_deadly.png"
            ordinary_width, ordinary_height, ordinary_pixels = decode_rgba8_png(ordinary_path)
            deadly_width, deadly_height, deadly_pixels = decode_rgba8_png(deadly_path)
            self.assertEqual((ordinary_width, ordinary_height), (deadly_width, deadly_height))
            self.assertNotEqual(
                ordinary_pixels,
                deadly_pixels,
                f"{material} Ordinary/Deadly cannot be the same rendered pixels under different PNG encoding",
            )
            ordinary_alpha = tuple(pixel[3] for pixel in ordinary_pixels)
            deadly_alpha = tuple(pixel[3] for pixel in deadly_pixels)
            self.assertNotEqual(
                ordinary_alpha,
                deadly_alpha,
                f"{material} Ordinary/Deadly must change material structure, not only RGB palette",
            )
            alpha_delta = sum(a != b for a, b in zip(ordinary_alpha, deadly_alpha))
            self.assertGreaterEqual(
                alpha_delta,
                len(ordinary_alpha) // 8,
                f"{material} Ordinary/Deadly alpha topology is too similar to prove structural distinction",
            )

    def test_contract_preserves_stage02_authority_and_purification_semantics(self):
        text = CONTRACT.read_text(encoding="utf-8").lower()
        for phrase in (
            "stage 02",
            "no parallel visual spread",
            "withered_growth is deadly/red",
            "not purification",
            "no persistent purified residue",
            "fusion is optional",
            "vanilla weighted blockstate",
            "art approved remains open",
        ):
            self.assertIn(phrase, text)

    def test_contract_keeps_manual_pack_and_reduced_effects_gates_open(self):
        text = CONTRACT.read_text(encoding="utf-8").lower()
        self.assertIn("612-mod", text)
        self.assertIn("reduced-effects", text)
        self.assertIn("screenshots", text)
        self.assertIn("pending", text)


if __name__ == "__main__":
    unittest.main()
