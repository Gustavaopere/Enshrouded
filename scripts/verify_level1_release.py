#!/usr/bin/env python3
import json
import re
import sys
from pathlib import Path

REQUIRED_ROOT = (
    "LICENSE",
    "SOURCES.md",
    "THIRD_PARTY_NOTICES.md",
    "provenance/third-party-provenance.json",
)

PREREQUISITES = {
    "09.01": "plans/09-hardening/✅-01-test-matrix.md",
    "09.02": "plans/09-hardening/✅-02-performance.md",
    "09.03": "plans/09-hardening/✅-03-world-upgrade.md",
    "09.04": "plans/09-hardening/✅-04-release-checklist.md",
    "09.05": "plans/09-hardening/✅-05-third-party-licenses-provenance.md",
}

PACK_VERSIONS = {
    "Ars Nouveau": "5.13.1",
    "Ars Zero": "2.0.2",
    "Iron's Spells": "3.16.3",
    "Epic Fight": "21.17.3.1",
    "Goety": "3.1.4",
    "Malum": "1.8.2",
    "Eidolon: Repraised": "0.5.0.2",
    "FTB Chunks": "2101.1.22",
    "FTB Teams": "2101.1.11",
    "JourneyMap": "6.0.7",
    "MineColonies": "1.1.1376",
    "GeckoLib": "4.9.2",
}

RELEASE_DOCS = (
    "docs/release/level1-checklist.md",
    "docs/release/level1-release-notes.md",
)

BLOCKER_MARKERS = (
    "REVIEW_REQUIRED",
    "PERMISSION_REQUIRED",
    "OPEN_P0",
    "OPEN_P1",
    "UNRESOLVED_RELEASE_BLOCKER",
)


def read(path: Path) -> str:
    return path.read_text(encoding="utf-8")


def has_name_version_line(text: str, name: str, version: str) -> bool:
    pattern = re.compile(rf"^{re.escape(name)}.*{re.escape(version)}$|^.*{re.escape(name)}.*{re.escape(version)}.*$", re.MULTILINE)
    return bool(pattern.search(text))


def validate_repository(root: Path) -> list[str]:
    root = Path(root)
    errors: list[str] = []

    for rel in REQUIRED_ROOT:
        if not (root / rel).is_file():
            errors.append(f"missing required release root file: {rel}")

    for stage, rel in PREREQUISITES.items():
        if not (root / rel).is_file():
            errors.append(f"missing prerequisite closeout {stage}: {rel}")

    for rel in RELEASE_DOCS:
        if not (root / rel).is_file():
            label = "release checklist" if rel.endswith("checklist.md") else "release notes"
            errors.append(f"missing {label}: {rel}")

    checklist_path = root / "docs/release/level1-checklist.md"
    if checklist_path.is_file():
        checklist = read(checklist_path)
        for token in ("NeoForge 1.21.1", "Java 21", "MANUAL_CURRENT_PACK_SMOKE_REQUIRED"):
            if token not in checklist:
                errors.append(f"release checklist missing required token: {token}")

    notes_path = root / "docs/release/level1-release-notes.md"
    if notes_path.is_file():
        notes = read(notes_path)
        for token in ("Level 1", "NeoForge 1.21.1", "Java 21"):
            if token not in notes:
                errors.append(f"release notes missing required token: {token}")

    compat_path = root / "docs/compat/current-pack-2026-09-06.md"
    if not compat_path.is_file():
        errors.append("missing current-pack compatibility profile: docs/compat/current-pack-2026-09-06.md")
    else:
        compat = read(compat_path)
        for name, version in PACK_VERSIONS.items():
            if not has_name_version_line(compat, name, version):
                errors.append(f"current-pack compatibility profile missing: {name} {version}")
        if "Spore/Infnexus: unsupported integration" not in compat:
            errors.append("current-pack profile must state Spore/Infnexus: unsupported integration")

    gradle_path = root / "gradle.properties"
    if not gradle_path.is_file():
        errors.append("missing gradle.properties")
    else:
        gradle = read(gradle_path)
        for token in (
            "minecraft_version=1.21.1",
            "neo_version=21.1.248",
            "mod_license=BSD-2-Clause",
            "mod_version=1.0.0",
        ):
            if token not in gradle:
                errors.append(f"release metadata missing required value: {token}")

    mods_toml = root / "src/main/resources/META-INF/neoforge.mods.toml"
    if not mods_toml.is_file():
        errors.append("missing NeoForge mod manifest")
    else:
        manifest = read(mods_toml)
        for token in ('modLoader="javafml"', 'modId="${mod_id}"'):
            if token not in manifest:
                errors.append(f"NeoForge mod manifest missing: {token}")

    lang_dir = root / "src/main/resources/assets/enshrouded/lang"
    en_path, pt_path = lang_dir / "en_us.json", lang_dir / "pt_br.json"
    if not en_path.is_file() or not pt_path.is_file():
        errors.append("release requires both en_us.json and pt_br.json")
    else:
        try:
            en = json.loads(read(en_path))
            pt = json.loads(read(pt_path))
            if set(en) != set(pt):
                missing_pt = sorted(set(en) - set(pt))
                missing_en = sorted(set(pt) - set(en))
                errors.append(
                    "language key parity mismatch: "
                    f"missing pt_br={missing_pt}; missing en_us={missing_en}"
                )
        except (json.JSONDecodeError, OSError) as exc:
            errors.append(f"invalid language JSON: {exc}")

    for rel in RELEASE_DOCS + ("docs/compat/current-pack-2026-09-06.md",):
        path = root / rel
        if path.is_file():
            text = read(path)
            for marker in BLOCKER_MARKERS:
                if marker in text:
                    errors.append(f"unresolved release blocker marker {marker} in {rel}")

    return errors


def main() -> int:
    root = Path(__file__).resolve().parents[1]
    errors = validate_repository(root)
    if errors:
        print("Level 1 release-readiness validation FAILED:")
        for error in errors:
            print(f"- {error}")
        return 1
    print("Level 1 release-readiness validation PASSED")
    return 0


if __name__ == "__main__":
    sys.exit(main())
