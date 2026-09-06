#!/usr/bin/env python3
import copy
import sys
import tempfile
import unittest
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))

from verify_third_party_provenance import validate_manifest_document, validate_repository


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
    "notice_required": True,
    "notes": "Runtime-provided only; no source or assets are redistributed."
}


def manifest_with(entry):
    return {
        "schema_version": 1,
        "entries": [entry],
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

    def test_registered_material_file_is_not_reported_as_unregistered(self):
        with tempfile.TemporaryDirectory() as temp_dir:
            root = Path(temp_dir)
            material = root / "src/main/resources/assets/example/registered.json"
            material.parent.mkdir(parents=True)
            material.write_text("{}\n", encoding="utf-8")
            entry = copy.deepcopy(BASE_ENTRY)
            entry["usage_kind"] = "derived"
            entry["license"]["status"] = "approved"
            entry["files"] = ["src/main/resources/assets/example/registered.json"]
            errors = validate_repository(root, manifest_with(entry))
            self.assertFalse(any("registered.json" in error and "unregistered" in error.lower() for error in errors), errors)


if __name__ == "__main__":
    unittest.main()
