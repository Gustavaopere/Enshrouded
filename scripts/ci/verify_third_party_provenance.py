#!/usr/bin/env python3
"""Fail-closed third-party provenance validation for Enshrouded releases."""

from __future__ import annotations

import json
import re
import sys
from pathlib import Path

MATERIAL_KINDS = {"vendored", "copied", "derived"}
MARKER_REQUIRED_KINDS = {"copied", "derived"}
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
NOTICE_ID = re.compile(r"^\s*-\s+ID\s+`([A-Za-z0-9_.-]+)`\s+—", re.MULTILINE)
GIT_IMMUTABLE_REF = re.compile(r"^git:[0-9a-fA-F]{40}(?:[0-9a-fA-F]{24})?$")
SHA256_IMMUTABLE_REF = re.compile(r"^sha256:[0-9a-fA-F]{64}$")
ARTIFACT_IMMUTABLE_REF = re.compile(
    r"^artifact:.+:(?:sha1:[0-9a-fA-F]{40}|sha256:[0-9a-fA-F]{64})$"
)
INTEGRATION_ROOT = Path("src/main/java/com/gustavaopere/enshrouded/integration")
RESOURCE_ROOT = Path("src/main/resources")


def _nonempty_string(value: object) -> bool:
    return isinstance(value, str) and bool(value.strip())


def _is_immutable_material_ref(value: object) -> bool:
    if not _nonempty_string(value):
        return False
    normalized = str(value).strip()
    return bool(
        GIT_IMMUTABLE_REF.fullmatch(normalized)
        or SHA256_IMMUTABLE_REF.fullmatch(normalized)
        or ARTIFACT_IMMUTABLE_REF.fullmatch(normalized)
    )


def _first_party_binary_files(document: dict) -> set[str]:
    values = document.get("first_party_binaries")
    if not isinstance(values, list):
        return set()
    return {str(value) for value in values if _nonempty_string(value)}


def validate_manifest_document(document: object) -> list[str]:
    errors: list[str] = []
    if not isinstance(document, dict):
        return ["manifest root must be an object"]
    if document.get("schema_version") != 1:
        errors.append("schema_version must be 1")

    first_party_values = document.get("first_party_binaries")
    first_party: set[str] = set()
    if not isinstance(first_party_values, list) or any(not _nonempty_string(path) for path in first_party_values):
        errors.append("first_party_binaries must be an array of non-empty repository paths")
    else:
        for path in first_party_values:
            normalized = str(path)
            if normalized in first_party:
                errors.append(f"duplicate first-party binary: {normalized}")
            first_party.add(normalized)

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
            immutable_ref = entry.get("immutable_ref")
            if not _nonempty_string(immutable_ref):
                errors.append(f"{entry_id}.immutable_ref is required for material provenance")
            elif not _is_immutable_material_ref(immutable_ref):
                errors.append(
                    f"{entry_id}.immutable_ref must be a pinned git commit or cryptographic artifact digest"
                )
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

    for local_path in sorted(first_party & material_files.keys()):
        errors.append(
            f"binary/resource is classified as both first-party and third-party material: {local_path}"
        )

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


def _is_scanned_binary_resource(local_path: str) -> bool:
    path = Path(local_path)
    if path.is_absolute() or ".." in path.parts or path.as_posix() != local_path:
        return False
    try:
        path.relative_to(RESOURCE_ROOT)
    except ValueError:
        return False
    return path.suffix.lower() in BINARY_RESOURCE_SUFFIXES


def _validate_notice_reconciliation(root: Path, document: dict) -> list[str]:
    errors: list[str] = []
    required_ids = {
        str(entry["id"])
        for entry in document.get("entries", [])
        if isinstance(entry, dict)
        and _nonempty_string(entry.get("id"))
        and entry.get("notice_required") is True
    }
    notice_path = root / "THIRD_PARTY_NOTICES.md"
    if not notice_path.is_file():
        if required_ids:
            errors.append(
                "THIRD_PARTY_NOTICES.md is required because notice_required entries exist: "
                + ", ".join(sorted(required_ids))
            )
        return errors

    try:
        notice_text = notice_path.read_text(encoding="utf-8")
    except UnicodeDecodeError:
        return ["THIRD_PARTY_NOTICES.md must be UTF-8 text"]

    notice_ids_list = NOTICE_ID.findall(notice_text)
    notice_ids = set(notice_ids_list)
    duplicates = sorted({notice_id for notice_id in notice_ids_list if notice_ids_list.count(notice_id) > 1})
    for notice_id in duplicates:
        errors.append(f"duplicate notice provenance id: {notice_id}")

    for entry_id in sorted(required_ids - notice_ids):
        errors.append(f"notice_required ledger entry is missing from THIRD_PARTY_NOTICES.md: {entry_id}")
    for notice_id in sorted(notice_ids - required_ids):
        errors.append(f"THIRD_PARTY_NOTICES notice has no notice_required ledger entry: {notice_id}")
    return errors


def _validate_required_derivation_markers(root: Path, document: dict) -> list[str]:
    errors: list[str] = []
    for entry in document.get("entries", []):
        if not isinstance(entry, dict) or entry.get("usage_kind") not in MARKER_REQUIRED_KINDS:
            continue
        entry_id = str(entry.get("id", "<unknown>"))
        for local_path in entry.get("files", []):
            if not _nonempty_string(local_path):
                continue
            relative = str(local_path)
            path = Path(relative)
            if path.suffix.lower() != ".java":
                continue
            try:
                path.relative_to(Path("src/main/java"))
            except ValueError:
                continue
            absolute = root / path
            if not absolute.is_file():
                continue
            try:
                text = absolute.read_text(encoding="utf-8")
            except UnicodeDecodeError:
                errors.append(f"registered source material must be UTF-8 text: {relative}")
                continue
            markers = DERIVED_MARKER.findall(text)
            if entry_id not in markers:
                errors.append(
                    f"{relative} is mapped as {entry.get('usage_kind')} material for {entry_id} "
                    f"but lacks // UPSTREAM-DERIVED: {entry_id}"
                )
    return errors


def validate_repository(root: Path, document: dict) -> list[str]:
    errors: list[str] = []
    entries = _entry_map(document)
    registered_material = _registered_material_files(document)
    first_party = _first_party_binary_files(document)

    for local_path, provenance_id in sorted(registered_material.items()):
        if not (root / local_path).is_file():
            errors.append(f"registered material file does not exist: {local_path} ({provenance_id})")

    for local_path in sorted(first_party):
        if not _is_scanned_binary_resource(local_path):
            errors.append(f"first-party declaration is not a scanned distributable binary/resource: {local_path}")
            continue
        if not (root / local_path).is_file():
            errors.append(f"first-party binary does not exist: {local_path}")

    resource_root = root / RESOURCE_ROOT
    if resource_root.exists():
        for path in resource_root.rglob("*"):
            if not path.is_file() or path.suffix.lower() not in BINARY_RESOURCE_SUFFIXES:
                continue
            relative = path.relative_to(root).as_posix()
            classifications = int(relative in first_party) + int(relative in registered_material)
            if classifications == 0:
                errors.append(f"unregistered distributable binary/resource: {relative}")
            elif classifications > 1:
                errors.append(f"binary/resource has multiple provenance classifications: {relative}")

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

    errors.extend(_validate_required_derivation_markers(root, document))
    errors.extend(_validate_notice_reconciliation(root, document))

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

    if not (root / "LICENSE").is_file():
        # Synthetic unit-test roots intentionally omit repository release files.
        if (root / ".git").exists() or (root / "build.gradle").exists():
            errors.append("release notice missing: LICENSE")

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
    print(
        "Third-party provenance verified: "
        f"{len(document.get('entries', []))} entries; "
        f"{len(document.get('first_party_binaries', []))} first-party binaries; "
        "no blocked material detected."
    )
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
