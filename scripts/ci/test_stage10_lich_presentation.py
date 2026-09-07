import json
import pathlib
import re
import unittest


ROOT = pathlib.Path(__file__).resolve().parents[2]


class Stage10LichPresentationContractTests(unittest.TestCase):
    def setUp(self):
        self.provenance = json.loads(
            (ROOT / "provenance/third-party-provenance.json").read_text(encoding="utf-8")
        )

    def test_lich_skull_replaces_vanilla_placeholder_with_gecko_item_renderer(self):
        item = (ROOT / "src/main/java/com/gustavaopere/enshrouded/story/reward/LichSkullItem.java").read_text(encoding="utf-8")
        renderer_path = ROOT / "src/main/java/com/gustavaopere/enshrouded/client/render/story/LichSkullRenderer.java"
        model_path = ROOT / "src/main/java/com/gustavaopere/enshrouded/client/render/story/LichSkullGeoModel.java"
        item_model_path = ROOT / "src/main/resources/assets/enshrouded/models/item/lich_skull_manifestation_1.json"

        self.assertIn("implements GeoItem", item)
        self.assertNotIn("StandingAndWallBlockItem", item)
        self.assertIn("GeckoLibUtil.createInstanceCache(this)", item)
        self.assertIn("createGeoRenderer(Consumer<GeoRenderProvider>", item)
        self.assertTrue(renderer_path.is_file(), "missing LichSkullRenderer")
        self.assertTrue(model_path.is_file(), "missing LichSkullGeoModel")

        renderer = renderer_path.read_text(encoding="utf-8")
        model = model_path.read_text(encoding="utf-8")
        self.assertIn("GeoItemRenderer<LichSkullItem>", renderer)
        self.assertIn("AutoGlowingGeoLayer", renderer)
        self.assertIn("geo/lich_skull_manifestation_1.geo.json", model)
        self.assertIn("textures/item/lich_skull_manifestation_1.png", model)
        self.assertIn("animations/lich_skull_manifestation_1.animation.json", model)

        item_model = json.loads(item_model_path.read_text(encoding="utf-8"))
        self.assertEqual("builtin/entity", item_model["parent"])
        self.assertNotIn("wither_skeleton_skull", item_model_path.read_text(encoding="utf-8"))

    def test_lich_skull_geo_animation_and_item_transforms_are_authored(self):
        geo_path = ROOT / "src/main/resources/assets/enshrouded/geo/lich_skull_manifestation_1.geo.json"
        animation_path = ROOT / "src/main/resources/assets/enshrouded/animations/lich_skull_manifestation_1.animation.json"
        item_model_path = ROOT / "src/main/resources/assets/enshrouded/models/item/lich_skull_manifestation_1.json"
        self.assertTrue(geo_path.is_file(), "missing Lich Skull geo asset")
        self.assertTrue(animation_path.is_file(), "missing Lich Skull animation asset")

        geo = json.loads(geo_path.read_text(encoding="utf-8"))
        animation = json.loads(animation_path.read_text(encoding="utf-8"))
        geometry = geo["minecraft:geometry"][0]
        self.assertEqual("geometry.lich_skull_manifestation_1", geometry["description"]["identifier"])
        bone_names = {bone["name"] for bone in geometry["bones"]}
        self.assertTrue(
            {"skull_root", "bone_mask", "broken_crown", "halo", "fragments", "fractured_arcana"}.issubset(bone_names),
            bone_names,
        )
        self.assertTrue(
            {
                "animation.lich_skull.idle",
                "animation.lich_skull.ritual_resonance",
            }.issubset(animation["animations"].keys())
        )

        display = json.loads(item_model_path.read_text(encoding="utf-8"))["display"]
        for transform in ("gui", "ground", "firstperson_righthand", "thirdperson_righthand"):
            self.assertIn(transform, display, f"missing item transform: {transform}")

    def test_lich_skull_first_party_assets_and_editable_source_are_tracked(self):
        texture = "src/main/resources/assets/enshrouded/textures/item/lich_skull_manifestation_1.png"
        glowmask = "src/main/resources/assets/enshrouded/textures/item/lich_skull_manifestation_1_glowmask.png"
        self.assertTrue((ROOT / texture).is_file(), "missing Lich Skull base texture")
        self.assertTrue((ROOT / glowmask).is_file(), "missing Lich Skull glowmask")
        self.assertIn(texture, self.provenance["first_party_binaries"])
        self.assertIn(glowmask, self.provenance["first_party_binaries"])

        source_path = ROOT / "art/blockbench/lich_skull_manifestation_1.bbmodel"
        self.assertTrue(source_path.is_file(), "missing editable Lich Skull Blockbench source")
        source = json.loads(source_path.read_text(encoding="utf-8"))
        self.assertEqual("Lich Skull — Manifestation I", source["name"])
        self.assertEqual("geometry.lich_skull_manifestation_1", source["model_identifier"])
        self.assertGreaterEqual(len(source["elements"]), 12)
        element_names = {element["name"] for element in source["elements"]}
        self.assertTrue({"mask_core", "crown_left", "crown_right", "halo_left", "fragment_front"}.issubset(element_names))
        texture_names = {entry["name"] for entry in source["textures"]}
        self.assertEqual(
            {"lich_skull_manifestation_1.png", "lich_skull_manifestation_1_glowmask.png"},
            texture_names,
        )

    def test_manifestation_vfx_uses_project_particle_and_hard_budgets(self):
        particles = (ROOT / "src/main/java/com/gustavaopere/enshrouded/registry/ModParticles.java").read_text(encoding="utf-8")
        client_particles = (ROOT / "src/main/java/com/gustavaopere/enshrouded/client/effects/ShroudParticleController.java").read_text(encoding="utf-8")
        presentation_path = ROOT / "src/main/java/com/gustavaopere/enshrouded/story/manifestation/LichManifestationPresentation.java"
        self.assertIn('LICH_ARCANA = register("lich_arcana")', particles)
        self.assertIn("event.registerSpriteSet(ModParticles.LICH_ARCANA.get()", client_particles)
        self.assertTrue(presentation_path.is_file(), "missing bounded Lich manifestation presentation bridge")

        presentation = presentation_path.read_text(encoding="utf-8")
        spawn_count = re.search(r"MAX_SPAWN_PARTICLES\s*=\s*(\d+)", presentation)
        defeat_count = re.search(r"MAX_DEFEAT_PARTICLES\s*=\s*(\d+)", presentation)
        max_distance = re.search(r"MAX_AUDIBLE_DISTANCE\s*=\s*([0-9.]+)", presentation)
        self.assertIsNotNone(spawn_count)
        self.assertIsNotNone(defeat_count)
        self.assertIsNotNone(max_distance)
        self.assertLessEqual(int(spawn_count.group(1)), 32)
        self.assertLessEqual(int(defeat_count.group(1)), 40)
        self.assertLessEqual(float(max_distance.group(1)), 48.0)
        self.assertIn("sendParticles(ModParticles.LICH_ARCANA.get()", presentation)
        self.assertNotIn("forceChunk", presentation)
        self.assertNotIn("getChunk(", presentation)

    def test_manifestation_presentation_is_downstream_of_authoritative_start_and_defeat(self):
        service = (ROOT / "src/main/java/com/gustavaopere/enshrouded/story/manifestation/ManifestationEncounterService.java").read_text(encoding="utf-8")
        spawn_call = "LichManifestationPresentation.onSpawned(level, manifestation.entity(), encounterId);"
        defeat_call = "LichManifestationPresentation.onDefeated(level, actor, encounterId);"
        self.assertIn(spawn_call, service)
        self.assertIn(defeat_call, service)
        self.assertGreater(service.index(spawn_call), service.index("if (!activated)"))
        self.assertGreater(service.index(spawn_call), service.index("if (arenaRule != null && !arenaRule.activate"))
        self.assertGreater(service.index(defeat_call), service.index("if (!savedData.defeatEncounter(encounterId))"))
        self.assertLess(service.index(spawn_call), service.index("return Optional.of(new ActiveEncounter"))
        self.assertLess(service.index(defeat_call), service.index("return Optional.of(result)"))

    def test_manifestation_presentation_cannot_become_provider_or_story_authority(self):
        presentation_path = ROOT / "src/main/java/com/gustavaopere/enshrouded/story/manifestation/LichManifestationPresentation.java"
        self.assertTrue(presentation_path.is_file())
        presentation = presentation_path.read_text(encoding="utf-8")
        for forbidden in (
            "LichManifestationProvider",
            "StorySavedData",
            "createEncounter(",
            "activateEncounter(",
            "defeatEncounter(",
            "addFreshEntity(",
        ):
            self.assertNotIn(forbidden, presentation)

        director = (ROOT / "src/main/java/com/gustavaopere/enshrouded/story/boss/ManifestationDirector.java").read_text(encoding="utf-8")
        self.assertIn("provider.spawn(level, context)", director)


if __name__ == "__main__":
    unittest.main()
