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


if __name__ == "__main__":
    unittest.main()
