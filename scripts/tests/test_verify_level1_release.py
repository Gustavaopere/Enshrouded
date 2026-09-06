import json
import tempfile
import unittest
from pathlib import Path

import scripts.verify_level1_release as release


class Level1ReleaseContractTest(unittest.TestCase):
    def make_repo(self):
        root = Path(tempfile.mkdtemp())
        (root / "plans/09-hardening").mkdir(parents=True)
        (root / "docs/release").mkdir(parents=True)
        (root / "docs/compat").mkdir(parents=True)
        (root / "provenance").mkdir(parents=True)
        (root / "src/main/resources/assets/enshrouded/lang").mkdir(parents=True)
        (root / "src/main/resources/META-INF").mkdir(parents=True)
        for name in ("LICENSE", "SOURCES.md", "THIRD_PARTY_NOTICES.md"):
            (root / name).write_text("ok\n", encoding="utf-8")
        (root / "provenance/third-party-provenance.json").write_text("{}\n", encoding="utf-8")
        for name in ("✅-01-test-matrix.md", "✅-02-performance.md", "✅-03-world-upgrade.md", "✅-05-third-party-licenses-provenance.md"):
            (root / "plans/09-hardening" / name).write_text("closed\n", encoding="utf-8")
        (root / "docs/release/level1-checklist.md").write_text(
            "NeoForge 1.21.1\nJava 21\nMANUAL_CURRENT_PACK_SMOKE_REQUIRED\nNo unresolved P0/P1 release blockers\n",
            encoding="utf-8",
        )
        (root / "docs/release/level1-release-notes.md").write_text(
            "Level 1\nNeoForge 1.21.1\nJava 21\n",
            encoding="utf-8",
        )
        (root / "docs/compat/current-pack-2026-09-06.md").write_text(
            "| Target | Version |\n"
            "| --- | --- |\n"
            "| Ars Nouveau | 5.13.1 |\n"
            "| Ars Zero | 2.0.2 |\n"
            "| Iron's Spells | 3.16.3 |\n"
            "| Epic Fight | 21.17.3.1 |\n"
            "| Goety | 3.1.4 |\n"
            "| Malum | 1.8.2 |\n"
            "| Eidolon: Repraised | 0.5.0.2 |\n"
            "| FTB Chunks | 2101.1.22 |\n"
            "| FTB Teams | 2101.1.11 |\n"
            "| JourneyMap | 6.0.7 |\n"
            "| MineColonies | 1.1.1376 |\n"
            "| GeckoLib | 4.9.2 |\n"
            "Spore/Infnexus: unsupported integration\n",
            encoding="utf-8",
        )
        (root / "gradle.properties").write_text(
            "minecraft_version=1.21.1\nneo_version=21.1.248\nmod_license=BSD-2-Clause\nmod_version=1.0.0\n",
            encoding="utf-8",
        )
        (root / "src/main/resources/META-INF/neoforge.mods.toml").write_text(
            'modLoader="javafml"\nmodId="${mod_id}"\n', encoding="utf-8"
        )
        lang = {"mod.enshrouded.name": "Enshrouded", "hud.enshrouded.shroud": "Shroud"}
        (root / "src/main/resources/assets/enshrouded/lang/en_us.json").write_text(json.dumps(lang), encoding="utf-8")
        (root / "src/main/resources/assets/enshrouded/lang/pt_br.json").write_text(
            json.dumps({**lang, "hud.enshrouded.shroud": "Mortalha"}), encoding="utf-8"
        )
        return root

    def test_valid_release_contract_passes(self):
        self.assertEqual([], release.validate_repository(self.make_repo()))

    def test_missing_release_doc_fails_closed(self):
        root = self.make_repo()
        (root / "docs/release/level1-checklist.md").unlink()
        self.assertTrue(any("release checklist" in e.lower() for e in release.validate_repository(root)))

    def test_missing_prerequisite_closeout_fails_closed(self):
        root = self.make_repo()
        (root / "plans/09-hardening/✅-05-third-party-licenses-provenance.md").unlink()
        self.assertTrue(any("09.05" in e for e in release.validate_repository(root)))

    def test_current_pack_versions_are_required(self):
        root = self.make_repo()
        p = root / "docs/compat/current-pack-2026-09-06.md"
        p.write_text(p.read_text(encoding="utf-8").replace("| MineColonies | 1.1.1376 |", "| MineColonies | 1.1.1375 |"), encoding="utf-8")
        self.assertTrue(any("MineColonies 1.1.1376" in e for e in release.validate_repository(root)))

    def test_language_key_parity_is_required(self):
        root = self.make_repo()
        p = root / "src/main/resources/assets/enshrouded/lang/pt_br.json"
        data = json.loads(p.read_text(encoding="utf-8"))
        data.pop("hud.enshrouded.shroud")
        p.write_text(json.dumps(data), encoding="utf-8")
        self.assertTrue(any("language key parity" in e.lower() for e in release.validate_repository(root)))

    def test_unresolved_release_blocker_fails_closed(self):
        root = self.make_repo()
        (root / "docs/release/level1-release-notes.md").write_text("REVIEW_REQUIRED\n", encoding="utf-8")
        self.assertTrue(any("REVIEW_REQUIRED" in e for e in release.validate_repository(root)))

    def test_manual_full_pack_smoke_must_be_explicit(self):
        root = self.make_repo()
        p = root / "docs/release/level1-checklist.md"
        p.write_text(p.read_text(encoding="utf-8").replace("MANUAL_CURRENT_PACK_SMOKE_REQUIRED", "automated"), encoding="utf-8")
        self.assertTrue(any("MANUAL_CURRENT_PACK_SMOKE_REQUIRED" in e for e in release.validate_repository(root)))

    def test_release_version_must_not_be_dev(self):
        root = self.make_repo()
        p = root / "gradle.properties"
        p.write_text(p.read_text(encoding="utf-8").replace("mod_version=1.0.0", "mod_version=0.1.0-dev"), encoding="utf-8")
        self.assertTrue(any("mod_version=1.0.0" in e for e in release.validate_repository(root)))


if __name__ == "__main__":
    unittest.main()
