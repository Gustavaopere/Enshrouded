#!/usr/bin/env python3
"""Fail-closed third-party provenance validation for Enshrouded releases."""

from __future__ import annotations

import json
import re
import sys
from pathlib import Path

MATERIAL_KINDS = {"vendored", "copied", "derived"}
ALLOWED_KINDS = MATERIAL_KINDS | {
    "runtime_provided",
    "api_only",
    "reference_only",
    "inspected_only",
    "optional_absent",
    "first_party_sibling",
}
BLOCKED_MATERIAL_LICENSE_STATUSES = {
    "restricted",
    "unknown",
    "REVIEW_REQUIRED",
    "PERMISSION_REQUIRED",
}
REQUIRED_SOURCE_IDS = {
    "endnight-the-forest-alpha",
    "minecraft-dungeons",
    "enshrouded-game",
    "epic-fight",
    "ftb-chunks",
    "ftb-teams",
    "journeymap",
    "minecolonies",
    "geckolib",
    "ars-nouveau",
    "ars-zero",
    "irons-spellbooks",
    "goety",
    "malum",
    "eidolon-repraised",
}
BINARY_RESOURCE_SUFFIXES = {
    ".png", ".jpg", ".jpeg", ".gif", ".webp", ".ogg", ".wav", ".mp3",
    ".ttf", ".otf", ".zip", ".jar", ".class", ".bin", ".nbt",
}
TEXT_SUFFIXES = {
    ".java", ".kt", ".kts", ".json", ".toml", ".properties", ".mcmeta",
    ".txt", ".md", ".yml", ".yaml", ".gradle", ".xml",
}
DERIVED_MARKER = re.compile(r"UPSTREAM-DERIVED:\s*([A-Za-z0-9_.-]+)")
INTEGRATION_ROOT = Path("src/main/java/com/gustavaopere/enshrouded/integration")


def _nonempty_string(value: object) -> bool:
    return isinstance(value, str) and bool(value.strip())


def validate_manifest_document(document: object) -> list[str]:
    errors: list[str] = []
    if not isinstance(document, dict):
        return ["manifest root must be an object"]
    if document.get("schema_version") != 1:
        errors.append("schema_version must be 1")

    entries = document.get("entries")
    if not isinstance(entries, list):
        return errors + ["entries must be an array"]

    ids: set[str] = set()
    material_files: dict[str, str] = {}
    for index, entry in enumerate(entries):
        prefix = f"entries[{index}]"
        if not isinstance(entry, dict):
            errors.append(f"{prefix} must be an object")
            continue

        entry_id = entry.get("id")
        if not _nonempty_string(entry_id):
            errors.append(f"{prefix}.id is required")
            continue
        entry_id = str(entry_id)
        if entry_id in ids:
            errors.append(f"duplicate provenance id: {entry_id}")
        ids.add(entry_id)

        for field in ("name", "author", "source_url", "immutable_ref"):
            if not _nonempty_string(entry.get(field)):
                errors.append(f"{entry_id}.{field} is required")

        kind = entry.get("usage_kind")
        if kind not in ALLOWED_KINDS:
            errors.append(f"{entry_id}.usage_kind must be one of {sorted(ALLOWED_KINDS)}")

        license_info = entry.get("license")
        if not isinstance(license_info, dict):
            errors.append(f"{entry_id}.license must be an object")
            license_info = {}
        for field in ("name", "url", "status"):
            if not _nonempty_string(license_info.get(field)):
                errors.append(f"{entry_id}.license.{field} is required")

        files = entry.get("files")
        if not isinstance(files, list) or any(not _nonempty_string(path) for path in files):
            errors.append(f"{entry_id}.files must be an array of non-empty repository paths")
            files = []

        integration_paths = entry.get("integration_paths", [])
        if not isinstance(integration_paths, list) or any(not _nonempty_string(path) for path in integration_paths):
            errors.append(f"{entry_id}.integration_paths must be an array of non-empty repository paths")

        if not isinstance(entry.get("notice_required"), bool):
            errors.append(f"{entry_id}.notice_required must be boolean")

        if kind in MATERIAL_KINDS:
            if not files:
                errors.append(f"{entry_id}.files must map every {kind} local file")
            if not _nonempty_string(entry.get("immutable_ref")):
                errors.append(f"{entry_id}.immutable_ref is required for material provenance")
            status = license_info.get("status")
            if status in BLOCKED_MATERIAL_LICENSE_STATUSES:
                errors.append(f"{entry_id} material is blocked by license status {status}")
            if status != "approved":
                errors.append(f"{entry_id} material license status must be approved")
            for local_path in files:
                owner = material_files.get(local_path)
                if owner and owner != entry_id:
                    errors.append(f"material file {local_path} is mapped by both {owner} and {entry_id}")
                material_files[local_path] = entry_id
        elif files:
            errors.append(f"{entry_id} is {kind}; non-material entries must not claim redistributed files")

    missing = sorted(REQUIRED_SOURCE_IDS - ids)
    if missing:
        errors.append("mandatory provenance sources missing: " + ", ".join(missing))

    excluded = document.get("excluded_provider_ids")
    if not isinstance(excluded, list):
        errors.append("excluded_provider_ids must be an array")
    else:
        normalized = {str(value).strip().lower() for value in excluded}
        for required in ("spore", "infnexus"):
            if required not in normalized:
                errors.append(f"excluded_provider_ids must include {required}")

    return errors


def _entry_map(document: dict) -> dict[str, dict]:
    return {
        str(entry["id"]): entry
        for entry in document.get("entries", [])
        if isinstance(entry, dict) and _nonempty_string(entry.get("id"))
    }


def _registered_material_files(document: dict) -> dict[str, str]:
    registered: dict[str, str] = {}
    for entry in document.get("entries", []):
        if not isinstance(entry, dict) or entry.get("usage_kind") not in MATERIAL_KINDS:
            continue
        for path in entry.get("files", []):
            if _nonempty_string(path):
                registered[str(path)] = str(entry.get("id", "<unknown>"))
    return registered


def validate_repository(root: Path, document: dict) -> list[str]:
    errors: list[str] = []
    entries = _entry_map(document)
    registered_material = _registered_material_files(document)

    for local_path, provenance_id in sorted(registered_material.items()):
        if not (root / local_path).is_file():
            errors.append(f"registered material file does not exist: {local_path} ({provenance_id})")

    resource_root = root / "src/main/resources"
    if resource_root.exists():
        for path in resource_root.rglob("*"):
            if not path.is_file() or path.suffix.lower() not in BINARY_RESOURCE_SUFFIXES:
                continue
            relative = path.relative_to(root).as_posix()
            if relative not in registered_material:
                errors.append(f"unregistered distributable binary/resource: {relative}")

    production_root = root / "src/main"
    forbidden = [str(value).strip().lower() for value in document.get("excluded_provider_ids", [])]
    if production_root.exists():
        for path in production_root.rglob("*"):
            if not path.is_file() or path.suffix.lower() not in TEXT_SUFFIXES:
                continue
            try:
                text = path.read_text(encoding="utf-8")
            except UnicodeDecodeError:
                continue
            relative = path.relative_to(root).as_posix()
            lowered = text.lower()
            for provider_id in forbidden:
                if re.search(rf"(?<![a-z0-9_]){re.escape(provider_id)}(?![a-z0-9_])", lowered):
                    errors.append(f"forbidden provider reference {provider_id} in production file {relative}")
            for marker_id in DERIVED_MARKER.findall(text):
                entry = entries.get(marker_id)
                if entry is None:
                    errors.append(f"{relative} references unknown UPSTREAM-DERIVED id {marker_id}")
                    continue
                if entry.get("usage_kind") not in MATERIAL_KINDS:
                    errors.append(f"{relative} marks {marker_id} as derived but ledger kind is {entry.get('usage_kind')}")
                if relative not in entry.get("files", []):
                    errors.append(f"{relative} has UPSTREAM-DERIVED:{marker_id} but is not mapped in that ledger entry")

    integration_dir = root / INTEGRATION_ROOT
    covered_paths: list[str] = []
    for entry in document.get("entries", []):
        if isinstance(entry, dict):
            covered_paths.extend(str(path).rstrip("/") for path in entry.get("integration_paths", []))
    if integration_dir.exists():
        for child in integration_dir.iterdir():
            if not child.is_dir():
                continue
            relative = child.relative_to(root).as_posix()
            if not any(relative == prefix or relative.startswith(prefix + "/") for prefix in covered_paths):
                errors.append(f"integration/provider path lacks provenance decision: {relative}")

    for required in ("LICENSE", "THIRD_PARTY_NOTICES.md"):
        if root.resolve() != Path(root.anchor).resolve() and not (root / required).is_file():
            # Synthetic unit-test roots intentionally omit release notices.
            if (root / ".git").exists() or (root / "build.gradle").exists():
                errors.append(f"release notice missing: {required}")

    return errors


def load_manifest(path: Path) -> dict:
    with path.open("r", encoding="utf-8") as handle:
        document = json.load(handle)
    if not isinstance(document, dict):
        raise ValueError("manifest root must be an object")
    return document


def main(argv: list[str] | None = None) -> int:
    argv = list(sys.argv[1:] if argv is None else argv)
    root = Path(argv[0]).resolve() if argv else Path(__file__).resolve().parents[2]
    manifest_path = root / "provenance/third-party-provenance.json"
    if not manifest_path.is_file():
        print(f"ERROR: provenance manifest missing: {manifest_path}")
        return 1
    try:
        document = load_manifest(manifest_path)
    except (OSError, json.JSONDecodeError, ValueError) as exc:
        print(f"ERROR: cannot load provenance manifest: {exc}")
        return 1

    errors = validate_manifest_document(document)
    errors.extend(validate_repository(root, document))
    if errors:
        for error in errors:
            print(f"ERROR: {error}")
        return 1
    print(f"Third-party provenance verified: {len(document.get('entries', []))} entries; no blocked material detected.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
