import re
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
CONTRACT = ROOT / "plans/10-visual-polish/10-08-advanced-vfx.md"
CUE = ROOT / "src/main/java/com/gustavaopere/enshrouded/presentation/AdvancedVfxCue.java"
CONTROLLER = ROOT / "src/main/java/com/gustavaopere/enshrouded/client/effects/AdvancedVfxController.java"
EMITTER = ROOT / "src/main/java/com/gustavaopere/enshrouded/presentation/AdvancedVfxServerEmitter.java"
NETWORKING = ROOT / "src/main/java/com/gustavaopere/enshrouded/network/ModNetworking.java"
BUILD = ROOT / "build.gradle"
MODS_TOML = ROOT / "src/main/resources/META-INF/neoforge.mods.toml"
CI = ROOT / ".github/workflows/ci.yml"
READINESS = ROOT / ".github/workflows/release-readiness.yml"


class Stage10AdvancedVfxContractTest(unittest.TestCase):
    def test_advanced_vfx_contract_exists_and_records_task_decision(self):
        self.assertTrue(CONTRACT.is_file(), "Stage 10.08 requires its canonical task/closeout contract")
        text = CONTRACT.read_text(encoding="utf-8").lower()
        for phrase in (
            "presentation-only",
            "stage 03 remains authoritative",
            "lodestone 1.8.2",
            "not adopted",
            "no lodestone compile/runtime dependency",
            "reduced_sensory",
            "minimal",
            "612-mod",
            "sodium",
            "manual",
            "art approved remains open",
        ):
            self.assertIn(phrase, text)

    def test_native_path_does_not_silently_promote_lodestone_to_dependency(self):
        build = BUILD.read_text(encoding="utf-8").lower()
        mods_toml = MODS_TOML.read_text(encoding="utf-8").lower()
        controller = CONTROLLER.read_text(encoding="utf-8").lower()
        self.assertNotIn("lodestone", build)
        self.assertNotIn('modid="lodestone"', mods_toml)
        self.assertNotIn("lodestone", controller)
        self.assertIn("modparticles", controller)
        self.assertIn("modsounds", controller)

    def test_discrete_server_cues_are_bounded_clientbound_presentation_only(self):
        emitter = EMITTER.read_text(encoding="utf-8")
        networking = NETWORKING.read_text(encoding="utf-8")
        self.assertIn("level.players()", emitter)
        self.assertIn("PacketDistributor.sendToPlayer", emitter)
        self.assertIn("AdvancedVfxPayload.TYPE", networking)
        self.assertIn("ClientAdvancedVfxState.INSTANCE.accept(payload)", networking)
        for forbidden in ("sendParticles(", "forceChunk", "getChunk(", "SavedData"):
            self.assertNotIn(forbidden, emitter)
        self.assertNotIn("playToServer(", networking)

    def test_every_declared_sequence_has_strict_hard_bounds(self):
        source = CUE.read_text(encoding="utf-8")
        declarations = re.findall(
            r"^\s*[A-Z_]+\(\"[^\"]+\",\s*(\d+),\s*(\d+),\s*([0-9.]+)D,\s*(\d+),\s*(?:true|false)\)",
            source,
            re.MULTILINE,
        )
        self.assertEqual(7, len(declarations), "Stage 10.08 must keep the seven authored cue families explicit")
        for particles, lifetime, distance, cooldown in declarations:
            self.assertLessEqual(int(particles), 24)
            self.assertLessEqual(int(lifetime), 40)
            self.assertLessEqual(float(distance), 48.0)
            self.assertGreaterEqual(int(cooldown), int(lifetime))

    def test_client_sequence_renderer_uses_synced_state_config_and_bounded_lifetimes(self):
        source = CONTROLLER.read_text(encoding="utf-8")
        for required in (
            "ClientExposureState.INSTANCE.lastSequence()",
            "ClientAdvancedVfxState.INSTANCE.drain()",
            "AdvancedVfxTransitionPlanner.plan",
            "EnshroudedClientConfig.particleSettings()",
            "AdvancedVfxSequenceBudget.targetEmitted",
            "cue.cooldownTicks()",
            "cue.lifetimeTicks()",
        ):
            self.assertIn(required, source)
        for forbidden in ("PacketDistributor", "sendToServer", "SavedData", "forceChunk"):
            self.assertNotIn(forbidden, source)

    def test_both_ci_workflows_execute_the_stage1008_contract(self):
        for workflow in (CI, READINESS):
            source = workflow.read_text(encoding="utf-8")
            self.assertIn("scripts/ci/test_stage10_advanced_vfx.py", source)


if __name__ == "__main__":
    unittest.main()
