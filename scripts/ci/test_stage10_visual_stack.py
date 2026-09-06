import json
import pathlib
import re
import unittest


ROOT = pathlib.Path(__file__).resolve().parents[2]


class Stage10VisualStackContractTests(unittest.TestCase):
    def setUp(self):
        self.gradle_properties = (ROOT / "gradle.properties").read_text(encoding="utf-8")
        self.build_gradle = (ROOT / "build.gradle").read_text(encoding="utf-8")
        self.mods_toml = (ROOT / "src/main/resources/META-INF/neoforge.mods.toml").read_text(encoding="utf-8")
        self.provenance = json.loads((ROOT / "provenance/third-party-provenance.json").read_text(encoding="utf-8"))

    def property_value(self, name: str) -> str:
        match = re.search(rf"(?m)^{re.escape(name)}=(.+)$", self.gradle_properties)
        self.assertIsNotNone(match, f"missing Gradle property {name}")
        return match.group(1).strip()

    def test_geckolib_version_matches_current_pack_contract(self):
        self.assertEqual("4.9.2", self.property_value("geckolib_version"))
        self.assertEqual("[4.9.2,5.0.0)", self.property_value("geckolib_version_range"))

    def test_geckolib_is_main_runtime_dependency_and_fixture_reuses_same_version(self):
        dependency = 'software.bernie.geckolib:geckolib-neoforge-1.21.1:${geckolib_version}'
        self.assertIn(f'implementation "{dependency}"', self.build_gradle)
        self.assertIn(f'arsZeroCompatRuntime "{dependency}"', self.build_gradle)

    def test_mod_metadata_requires_geckolib_but_does_not_require_fusion(self):
        self.assertIn('modId="geckolib"', self.mods_toml)
        self.assertIn('versionRange="${geckolib_version_range}"', self.mods_toml)
        geckolib_block = self.mods_toml.split('modId="geckolib"', 1)[1]
        self.assertIn('type="required"', geckolib_block)
        self.assertNotIn('modId="fusion"', self.mods_toml)

    def test_fusion_remains_soft_and_uncompiled(self):
        self.assertNotIn("fusion-", self.build_gradle.lower())
        self.assertNotIn("curse.maven:fusion", self.build_gradle.lower())

    def test_geckolib_provenance_remains_external_and_unbundled(self):
        entries = {entry["id"]: entry for entry in self.provenance["entries"]}
        self.assertIn("geckolib", entries)
        geckolib = entries["geckolib"]
        self.assertEqual("runtime_provided", geckolib["usage_kind"])
        self.assertEqual("approved", geckolib["license"]["status"])
        self.assertEqual([], geckolib["files"])
        self.assertIn("4.9.2", geckolib["immutable_ref"])

    def test_flame_altar_uses_geckolib_block_entity_renderer_contract(self):
        block = (ROOT / "src/main/java/com/gustavaopere/enshrouded/flame/altar/FlameAltarBlock.java").read_text(encoding="utf-8")
        block_entity = (ROOT / "src/main/java/com/gustavaopere/enshrouded/flame/altar/FlameAltarBlockEntity.java").read_text(encoding="utf-8")
        client = (ROOT / "src/main/java/com/gustavaopere/enshrouded/client/EnshroudedClient.java").read_text(encoding="utf-8")

        self.assertIn("RenderShape.ENTITYBLOCK_ANIMATED", block)
        self.assertIn("GeoBlockEntity", block_entity)
        self.assertIn("GeckoLibUtil.createInstanceCache(this)", block_entity)
        self.assertIn("registerControllers(AnimatableManager.ControllerRegistrar", block_entity)
        self.assertIn(
            "registerBlockEntityRenderer(ModBlockEntities.FLAME_ALTAR.get(), FlameAltarRenderer::new)",
            client,
        )

    def test_flame_altar_renderer_uses_geo_model_and_emissive_layer(self):
        renderer_path = ROOT / "src/main/java/com/gustavaopere/enshrouded/client/render/flame/FlameAltarRenderer.java"
        model_path = ROOT / "src/main/java/com/gustavaopere/enshrouded/client/render/flame/FlameAltarGeoModel.java"
        self.assertTrue(renderer_path.is_file(), "missing FlameAltarRenderer")
        self.assertTrue(model_path.is_file(), "missing FlameAltarGeoModel")

        renderer = renderer_path.read_text(encoding="utf-8")
        model = model_path.read_text(encoding="utf-8")
        self.assertIn("GeoBlockRenderer<FlameAltarBlockEntity>", renderer)
        self.assertIn("AutoGlowingGeoLayer", renderer)
        self.assertIn("geo/flame_altar.geo.json", model)
        self.assertIn("textures/block/flame_altar.png", model)
        self.assertIn("animations/flame_altar.animation.json", model)

    def test_flame_altar_geo_and_animation_assets_have_required_content(self):
        geo_path = ROOT / "src/main/resources/assets/enshrouded/geo/flame_altar.geo.json"
        animation_path = ROOT / "src/main/resources/assets/enshrouded/animations/flame_altar.animation.json"
        self.assertTrue(geo_path.is_file(), "missing Flame Altar GeckoLib geo asset")
        self.assertTrue(animation_path.is_file(), "missing Flame Altar GeckoLib animation asset")

        geo = json.loads(geo_path.read_text(encoding="utf-8"))
        animation = json.loads(animation_path.read_text(encoding="utf-8"))
        geometry = geo["minecraft:geometry"][0]
        self.assertEqual("geometry.flame_altar", geometry["description"]["identifier"])
        bone_names = {bone["name"] for bone in geometry["bones"]}
        self.assertTrue(
            {"altar_root", "pedestal", "cradle", "runes", "flame", "halo"}.issubset(bone_names),
            bone_names,
        )
        required_clips = {
            "animation.flame_altar.idle",
            "animation.flame_altar.ritual_available",
            "animation.flame_altar.ritual_charge",
            "animation.flame_altar.ritual_success",
            "animation.flame_altar.level_transition",
            "animation.flame_altar.inactive",
        }
        self.assertTrue(required_clips.issubset(animation["animations"].keys()))

    def test_flame_altar_first_party_binary_assets_and_blockbench_source_are_tracked(self):
        texture = "src/main/resources/assets/enshrouded/textures/block/flame_altar.png"
        glowmask = "src/main/resources/assets/enshrouded/textures/block/flame_altar_glowmask.png"
        self.assertTrue((ROOT / texture).is_file(), "missing Flame Altar base texture")
        self.assertTrue((ROOT / glowmask).is_file(), "missing Flame Altar glowmask")
        self.assertIn(texture, self.provenance["first_party_binaries"])
        self.assertIn(glowmask, self.provenance["first_party_binaries"])

        source_path = ROOT / "art/blockbench/flame_altar.bbmodel"
        self.assertTrue(source_path.is_file(), "missing editable Blockbench source")
        source = json.loads(source_path.read_text(encoding="utf-8"))
        self.assertEqual("Flame Altar", source["name"])
        self.assertEqual("geometry.flame_altar", source["model_identifier"])

    def test_flame_altar_no_longer_uses_vanilla_placeholder_model(self):
        block_model = (ROOT / "src/main/resources/assets/enshrouded/models/block/flame_altar.json").read_text(encoding="utf-8")
        item_model = (ROOT / "src/main/resources/assets/enshrouded/models/item/flame_altar.json").read_text(encoding="utf-8")
        combined = block_model + "\n" + item_model
        self.assertNotIn("minecraft:block/polished_blackstone_bricks", combined)
        self.assertNotIn("minecraft:block/magma", combined)
        self.assertIn("enshrouded:block/flame_altar", combined)


if __name__ == "__main__":
    unittest.main()
