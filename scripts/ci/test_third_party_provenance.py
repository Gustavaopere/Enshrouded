#!/usr/bin/env python3
import copy
import json
import sys
import tempfile
import unittest
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))

from verify_third_party_provenance import (
    load_manifest,
    validate_manifest_document,
    validate_repository,
)


BASE_ENTRY = {
    "id": "example-runtime",
    "name": "Example Runtime",
    "author": "Example Author",
    "source_url": "https://example.invalid/project",
    "immutable_ref": "artifact:example-runtime:1.2.3:sha256:0123456789abcdef",
    "license": {
        "name": "Example-Proprietary",
        "url": "https://example.invalid/license",
        "status": "restricted"
    },
    "usage_kind": "runtime_provided",
    "files": [],
    "integration_paths": [],
    "notice_required": True,
    "notes": "Runtime-provided only; no source or assets are redistributed."
}


def manifest_with(entry):
    required_entries = []
    for required_id in (
        "endnight-the-forest-alpha", "minecraft-dungeons", "enshrouded-game",
        "epic-fight", "ftb-chunks", "ftb-teams", "journeymap", "minecolonies",
        "geckolib", "ars-nouveau", "ars-zero", "irons-spellbooks", "goety",
        "malum", "eidolon-repraised",
    ):
        required = copy.deepcopy(BASE_ENTRY)
        required["id"] = required_id
        required["name"] = required_id
        required_entries.append(required)
    return {
        "schema_version": 1,
        "first_party_binaries": [],
        "entries": required_entries + [entry],
        "excluded_provider_ids": ["spore", "infnexus"]
    }


class ProvenanceManifestContractTests(unittest.TestCase):
    def test_runtime_provided_restricted_license_is_allowed_without_vendored_files(self):
        errors = validate_manifest_document(manifest_with(copy.deepcopy(BASE_ENTRY)))
        self.assertEqual([], errors)

    def test_material_entry_without_immutable_ref_is_rejected(self):
        entry = copy.deepcopy(BASE_ENTRY)
        entry["usage_kind"] = "derived"
        entry["license"]["status"] = "approved"
        entry["files"] = ["src/main/java/example/Derived.java"]
        entry["immutable_ref"] = ""
        errors = validate_manifest_document(manifest_with(entry))
        self.assertTrue(any("immutable_ref" in error for error in errors), errors)

    def test_material_entry_with_restricted_license_is_rejected(self):
        entry = copy.deepcopy(BASE_ENTRY)
        entry["usage_kind"] = "copied"
        entry["files"] = ["src/main/resources/assets/example/copied.json"]
        errors = validate_manifest_document(manifest_with(entry))
        self.assertTrue(any("restricted" in error.lower() for error in errors), errors)

    def test_review_required_material_is_rejected(self):
        entry = copy.deepcopy(BASE_ENTRY)
        entry["usage_kind"] = "vendored"
        entry["license"]["status"] = "REVIEW_REQUIRED"
        entry["files"] = ["src/main/resources/assets/example/vendor.bin"]
        errors = validate_manifest_document(manifest_with(entry))
        self.assertTrue(any("REVIEW_REQUIRED" in error for error in errors), errors)

    def test_material_entry_requires_file_mapping(self):
        entry = copy.deepcopy(BASE_ENTRY)
        entry["usage_kind"] = "derived"
        entry["license"]["status"] = "approved"
        errors = validate_manifest_document(manifest_with(entry))
        self.assertTrue(any("files" in error for error in errors), errors)

    def test_first_party_binaries_field_is_required_and_must_be_string_array(self):
        document = manifest_with(copy.deepcopy(BASE_ENTRY))
        del document["first_party_binaries"]
        errors = validate_manifest_document(document)
        self.assertTrue(any("first_party_binaries" in error for error in errors), errors)

        document = manifest_with(copy.deepcopy(BASE_ENTRY))
        document["first_party_binaries"] = ["src/main/resources/assets/example/a.ogg", 42]
        errors = validate_manifest_document(document)
        self.assertTrue(any("first_party_binaries" in error for error in errors), errors)

    def test_duplicate_first_party_binary_is_rejected(self):
        document = manifest_with(copy.deepcopy(BASE_ENTRY))
        path = "src/main/resources/assets/example/a.ogg"
        document["first_party_binaries"] = [path, path]
        errors = validate_manifest_document(document)
        self.assertTrue(any("duplicate first-party binary" in error for error in errors), errors)

    def test_first_party_and_third_party_material_overlap_is_rejected(self):
        path = "src/main/resources/assets/example/shared.ogg"
        entry = copy.deepcopy(BASE_ENTRY)
        entry["usage_kind"] = "derived"
        entry["license"]["status"] = "approved"
        entry["files"] = [path]
        document = manifest_with(entry)
        document["first_party_binaries"] = [path]
        errors = validate_manifest_document(document)
        self.assertTrue(any("both first-party and third-party" in error for error in errors), errors)


class ProvenanceRepositoryContractTests(unittest.TestCase):
    def test_forbidden_spore_or_infnexus_reference_in_production_tree_is_rejected(self):
        with tempfile.TemporaryDirectory() as temp_dir:
            root = Path(temp_dir)
            source = root / "src/main/java/example/BadIntegration.java"
            source.parent.mkdir(parents=True)
            source.write_text('class BadIntegration { String provider = "spore"; }\n', encoding="utf-8")
            errors = validate_repository(root, manifest_with(copy.deepcopy(BASE_ENTRY)))
            self.assertTrue(any("spore" in error.lower() for error in errors), errors)

    def test_unregistered_binary_under_distributable_resources_is_rejected(self):
        with tempfile.TemporaryDirectory() as temp_dir:
            root = Path(temp_dir)
            binary = root / "src/main/resources/assets/example/untracked.ogg"
            binary.parent.mkdir(parents=True)
            binary.write_bytes(b"not-real-audio")
            errors = validate_repository(root, manifest_with(copy.deepcopy(BASE_ENTRY)))
            self.assertTrue(any("untracked.ogg" in error for error in errors), errors)

    def test_registered_first_party_binary_is_accepted(self):
        with tempfile.TemporaryDirectory() as temp_dir:
            root = Path(temp_dir)
            relative = "src/main/resources/assets/example/owned.ogg"
            binary = root / relative
            binary.parent.mkdir(parents=True)
            binary.write_bytes(b"first-party-audio")
            document = manifest_with(copy.deepcopy(BASE_ENTRY))
            document["first_party_binaries"] = [relative]
            errors = validate_manifest_document(document) + validate_repository(root, document)
            self.assertEqual([], errors)

    def test_stale_first_party_binary_declaration_is_rejected(self):
        with tempfile.TemporaryDirectory() as temp_dir:
            root = Path(temp_dir)
            document = manifest_with(copy.deepcopy(BASE_ENTRY))
            document["first_party_binaries"] = ["src/main/resources/assets/example/missing.ogg"]
            errors = validate_repository(root, document)
            self.assertTrue(any("first-party binary does not exist" in error for error in errors), errors)

    def test_first_party_declaration_must_point_to_scanned_binary_resource(self):
        with tempfile.TemporaryDirectory() as temp_dir:
            root = Path(temp_dir)
            relative = "src/main/resources/assets/example/not-binary.json"
            path = root / relative
            path.parent.mkdir(parents=True)
            path.write_text("{}\n", encoding="utf-8")
            document = manifest_with(copy.deepcopy(BASE_ENTRY))
            document["first_party_binaries"] = [relative]
            errors = validate_repository(root, document)
            self.assertTrue(any("not a scanned distributable binary/resource" in error for error in errors), errors)

    def test_registered_material_binary_is_not_reported_as_unregistered(self):
        with tempfile.TemporaryDirectory() as temp_dir:
            root = Path(temp_dir)
            relative = "src/main/resources/assets/example/registered.ogg"
            material = root / relative
            material.parent.mkdir(parents=True)
            material.write_bytes(b"third-party-audio")
            entry = copy.deepcopy(BASE_ENTRY)
            entry["usage_kind"] = "derived"
            entry["license"]["status"] = "approved"
            entry["files"] = [relative]
            errors = validate_repository(root, manifest_with(entry))
            self.assertFalse(any("registered.ogg" in error and "unregistered" in error.lower() for error in errors), errors)

    def test_actual_repository_ledger_passes(self):
        root = Path(__file__).resolve().parents[2]
        document = load_manifest(root / "provenance/third-party-provenance.json")
        errors = validate_manifest_document(document) + validate_repository(root, document)
        self.assertEqual([], errors)


if __name__ == "__main__":
    unittest.main()
