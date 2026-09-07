import json
import pathlib
import re
import unittest


ROOT = pathlib.Path(__file__).resolve().parents[2]


class Stage10SanctuaryPresentationContractTests(unittest.TestCase):
    def test_sanctuary_presentation_reads_canonical_client_sample_only(self):
        path = ROOT / "src/main/java/com/gustavaopere/enshrouded/client/effects/SanctuaryPresentationController.java"
        self.assertTrue(path.is_file(), "missing client-only Sanctuary presentation controller")
        controller = path.read_text(encoding="utf-8")
        self.assertIn("ClientShroudState.INSTANCE.sample()", controller)
        self.assertIn("sanctuarySuppressed()", controller)
        self.assertIn("sample.intensity()", controller)
        for forbidden in (
            "FlameWardRuntime",
            "FlameWardRuntimeBindings",
            "ShroudSavedData",
            "ShroudWorldState",
            "sendToServer",
        ):
            self.assertNotIn(forbidden, controller)

    def test_sanctuary_motes_are_registered_and_hard_bounded(self):
        particles = (ROOT / "src/main/java/com/gustavaopere/enshrouded/registry/ModParticles.java").read_text(encoding="utf-8")
        client_particles = (ROOT / "src/main/java/com/gustavaopere/enshrouded/client/effects/ShroudParticleController.java").read_text(encoding="utf-8")
        controller = (ROOT / "src/main/java/com/gustavaopere/enshrouded/client/effects/SanctuaryPresentationController.java").read_text(encoding="utf-8")
        client = (ROOT / "src/main/java/com/gustavaopere/enshrouded/client/EnshroudedClient.java").read_text(encoding="utf-8")

        self.assertIn('SANCTUARY_MOTE = register("sanctuary_mote")', particles)
        self.assertIn("event.registerSpriteSet(ModParticles.SANCTUARY_MOTE.get()", client_particles)
        self.assertIn("SanctuaryPresentationController.register(NeoForge.EVENT_BUS)", client)

        max_motes = re.search(r"MAX_MOTES_PER_PULSE\s*=\s*(\d+)", controller)
        interval = re.search(r"PULSE_INTERVAL_TICKS\s*=\s*(\d+)", controller)
        self.assertIsNotNone(max_motes)
        self.assertIsNotNone(interval)
        self.assertLessEqual(int(max_motes.group(1)), 8)
        self.assertGreaterEqual(int(interval.group(1)), 4)
        self.assertIn("EnshroudedClientConfig.particleSettings()", controller)
        self.assertIn("ModParticles.SANCTUARY_MOTE.get()", controller)
        self.assertNotIn("getChunk(", controller)
        self.assertNotIn("forceChunk", controller)

    def test_flame_altar_carries_the_presentation_only_ward_focus_asset(self):
        geo_path = ROOT / "src/main/resources/assets/enshrouded/geo/flame_altar.geo.json"
        animation_path = ROOT / "src/main/resources/assets/enshrouded/animations/flame_altar.animation.json"
        source_path = ROOT / "art/blockbench/flame_altar.bbmodel"
        block_entity_path = ROOT / "src/main/java/com/gustavaopere/enshrouded/flame/altar/FlameAltarBlockEntity.java"

        geo = json.loads(geo_path.read_text(encoding="utf-8"))
        animation = json.loads(animation_path.read_text(encoding="utf-8"))
        source = json.loads(source_path.read_text(encoding="utf-8"))
        block_entity = block_entity_path.read_text(encoding="utf-8")

        bone_names = {bone["name"] for bone in geo["minecraft:geometry"][0]["bones"]}
        self.assertTrue(
            {"ward_focus", "ward_ring", "purification_aperture", "ward_fragments"}.issubset(bone_names),
            bone_names,
        )
        clips = animation["animations"]
        self.assertIn("animation.flame_altar.sanctuary_active", clips)
        self.assertIn("animation.flame_altar.purification_release", clips)
        self.assertIn('new AnimationController<>(this, "sanctuary_ward"', block_entity)
        self.assertIn("SANCTUARY_ACTIVE", block_entity)
        self.assertIn('triggerableAnim("purification_release", PURIFICATION_RELEASE)', block_entity)

        element_names = {element["name"] for element in source["elements"]}
        self.assertTrue(
            {"ward_ring_north", "ward_ring_south", "purification_aperture", "ward_fragment_west"}.issubset(element_names),
            element_names,
        )

    def test_purification_release_is_event_bounded_and_downstream_of_authoritative_transition(self):
        runtime = (ROOT / "src/main/java/com/gustavaopere/enshrouded/shroud/purification/ShroudPurificationRuntime.java").read_text(encoding="utf-8")
        presentation_path = ROOT / "src/main/java/com/gustavaopere/enshrouded/shroud/purification/ShroudPurificationPresentation.java"
        self.assertTrue(presentation_path.is_file(), "missing authoritative-transition presentation bridge")
        presentation = presentation_path.read_text(encoding="utf-8")

        call = "ShroudPurificationPresentation.onPurified(level, core);"
        self.assertIn(call, runtime)
        self.assertGreater(runtime.index(call), runtime.index("updated.lifecycleState() == CoreLifecycleState.PURIFIED"))

        max_particles = re.search(r"MAX_RELEASE_PARTICLES\s*=\s*(\d+)", presentation)
        self.assertIsNotNone(max_particles)
        self.assertLessEqual(int(max_particles.group(1)), 32)
        self.assertRegex(
            presentation,
            r"sendParticles\s*\(\s*ModParticles\.SANCTUARY_MOTE\.get\(\)",
        )
        self.assertNotIn("forceChunk", presentation)
        self.assertNotIn("getChunk(", presentation)
        for forbidden in ("savedData.replace", "ShroudCoreService", "FlameWardRuntime.onAltarLoaded"):
            self.assertNotIn(forbidden, presentation)

    def test_stage_10_05_contract_documents_latent_shroud_and_defers_multiblock(self):
        path = ROOT / "plans/10-visual-polish/10-05-sanctuary-purification.md"
        self.assertTrue(path.is_file(), "missing Stage 10.05 implementation contract")
        document = path.read_text(encoding="utf-8")
        self.assertIn("FlameWardRuntime", document)
        self.assertIn("FlameWardService", document)
        self.assertIn("sanctuarySuppressed", document)
        self.assertIn("latent", document.lower())
        self.assertIn("10.09", document)
        self.assertIn("DEFERIDO", document)
        self.assertIn("does not create", document.lower())


if __name__ == "__main__":
    unittest.main()
