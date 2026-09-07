import hashlib
import json
import struct
import unittest
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


class Stage10ShroudWorldArtContractTest(unittest.TestCase):
    def test_environment_blocks_use_bounded_weighted_visual_variants(self):
        for block_id, model_prefix in BLOCK_FAMILIES.items():
            payload = load_json(BLOCKSTATES / f"{block_id}.json")
            variants = payload.get("variants", {}).get("")
            self.assertIsInstance(variants, list, f"{block_id} must use weighted baked variants")
            self.assertGreaterEqual(len(variants), 3, f"{block_id} needs enough variation to break tiling")
            self.assertLessEqual(len(variants), 4, f"{block_id} variant count must stay bounded")
            models = [entry.get("model", "") for entry in variants]
            self.assertTrue(all(model.startswith(f"enshrouded:block/{model_prefix}") for model in models))
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

    def test_ordinary_and_deadly_materials_are_not_recolors_of_one_identical_file(self):
        ordinary = hashlib.sha256((TEXTURES / "shroud_membrane_ordinary.png").read_bytes()).digest()
        deadly = hashlib.sha256((TEXTURES / "shroud_membrane_deadly.png").read_bytes()).digest()
        self.assertNotEqual(ordinary, deadly)
        ordinary = hashlib.sha256((TEXTURES / "shroud_crust_ordinary.png").read_bytes()).digest()
        deadly = hashlib.sha256((TEXTURES / "shroud_crust_deadly.png").read_bytes()).digest()
        self.assertNotEqual(ordinary, deadly)

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
