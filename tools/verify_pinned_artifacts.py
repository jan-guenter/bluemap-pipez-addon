#!/usr/bin/env python3
# SPDX-License-Identifier: MIT
"""Verify the exact All the Mons 1.2.0 Pipez renderer input."""

from __future__ import annotations

import argparse
from dataclasses import dataclass
import hashlib
import json
from pathlib import Path, PurePosixPath
import re
import struct
import sys
import tomllib
from typing import Mapping
import zipfile


PROFILE_ROOT = Path("src/main/resources/bluemap-pipez/profiles")
CATALOG_PATH = PROFILE_ROOT / "exact-artifacts.json"
PROFILE_PATH = PROFILE_ROOT / "pipez/1.21.1-1.2.31/profile.json"
MANIFEST_PATH = PROFILE_PATH.with_name("required-resources.tsv")
CATALOG_SHA256 = "89d4608bc10fd1e3006f5c5dd2643e5d9b0bda2b62c4ae14288e5f61524d0fb8"
PROFILE_SHA256 = "ba56ac821f558a6eac61715f83dc007b5fccf7896d50c8a1307a28cca3c01a8c"
MANIFEST_SHA256 = "3b1776ff9eb80dbbf3d20fa132793ef6fcf3cdb871ba3a3d400bf8218b44336c"
LOWER_SHA256 = re.compile(r"[0-9a-f]{64}")


@dataclass(frozen=True)
class ArtifactIdentity:
    mod_id: str
    metadata_version: str
    filename: str
    size: int
    sha1: str
    sha256: str
    sha512: str
    project_id: int
    file_id: int
    fingerprint: int
    modrinth_project_id: str
    modrinth_version_id: str
    class_path: str
    dependencies: Mapping[str, tuple[str, str, str, str]]


@dataclass(frozen=True)
class ResourceRow:
    path: str
    size: int
    sha256: str


PIPEZ = ArtifactIdentity(
    mod_id="pipez",
    metadata_version="1.21.1-1.2.31",
    filename="pipez-neoforge-1.21.1-1.2.31.jar",
    size=456_599,
    sha1="a5671f7e8d38dfc092ace4091250e8f9e1245e1e",
    sha256="9b37e922443ea3452daeacbfba4bcf69de07692183c4ee09f1d1e82c9fc5cc5f",
    sha512=(
        "7291230b62104b73b04564d6d39ba18d11134a5713ee79242c7ae8885d07c4ef"
        "b5f5d0ab75b8ff9aba3119ed8163d0c5488ad603a2db21b92dc35715b723ce5e"
    ),
    project_id=443_900,
    file_id=8_351_631,
    fingerprint=411_466_094,
    modrinth_project_id="iRmWy6ga",
    modrinth_version_id="BPGKb8pi",
    class_path="de/maxhenkel/pipez/Main.class",
    dependencies={
        "neoforge": ("required", "[21.0.8-beta,)", "NONE", "BOTH"),
        "minecraft": ("required", "[1.21,1.21.1]", "NONE", "BOTH"),
        "mekanism": ("optional", "[1.21.1-10.7.3.59,)", "AFTER", "BOTH"),
        "jei": ("optional", "*", "NONE", "BOTH"),
        "theoneprobe": ("optional", "*", "NONE", "BOTH"),
        "jade": ("optional", "*", "NONE", "BOTH"),
    },
)

BLOCK_IDS = {
    "pipez:energy_pipe",
    "pipez:fluid_pipe",
    "pipez:gas_pipe",
    "pipez:item_pipe",
    "pipez:universal_pipe",
}


def digest(path: Path, algorithm: str) -> str:
    value = hashlib.new(algorithm)
    with path.open("rb") as source:
        for chunk in iter(lambda: source.read(64 * 1024), b""):
            value.update(chunk)
    return value.hexdigest()


def verify_hash(label: str, actual: str, expected: str) -> None:
    if actual != expected:
        raise ValueError(f"{label} changed: got {actual}, expected {expected}")


def verify_exact_identity(path: Path, identity: ArtifactIdentity) -> None:
    if not path.is_file():
        raise ValueError(f"{identity.mod_id} artifact is not a regular file: {path}")
    if path.name != identity.filename:
        raise ValueError(
            f"{identity.mod_id} filename changed: got {path.name}, "
            f"expected {identity.filename}"
        )
    if path.stat().st_size != identity.size:
        raise ValueError(
            f"{identity.mod_id} size changed: got {path.stat().st_size}, "
            f"expected {identity.size}"
        )
    verify_hash(f"{identity.mod_id} SHA-1", digest(path, "sha1"), identity.sha1)
    verify_hash(
        f"{identity.mod_id} SHA-256", digest(path, "sha256"), identity.sha256
    )
    verify_hash(
        f"{identity.mod_id} SHA-512", digest(path, "sha512"), identity.sha512
    )


def _is_safe_resource_path(value: str) -> bool:
    if not value or "\\" in value or value.startswith("/"):
        return False
    path = PurePosixPath(value)
    return not path.is_absolute() and all(
        part not in {"", ".", ".."} for part in path.parts
    )


def parse_resource_manifest(path: Path) -> tuple[ResourceRow, ...]:
    raw = path.read_bytes()
    if raw and not raw.endswith(b"\n"):
        raise ValueError(f"{path} is not LF-terminated")
    try:
        text = raw.decode("ascii")
    except UnicodeDecodeError as error:
        raise ValueError(f"{path} is not ASCII") from error
    rows: list[ResourceRow] = []
    for line_number, line in enumerate(text.splitlines(), start=1):
        parts = line.split("\t")
        if len(parts) != 3:
            raise ValueError(
                f"{path}:{line_number} is not a canonical three-field row"
            )
        resource, size_text, sha256 = parts
        if not _is_safe_resource_path(resource):
            raise ValueError(
                f"{path}:{line_number} has unsafe resource path {resource!r}"
            )
        if not size_text.isdecimal() or str(int(size_text)) != size_text:
            raise ValueError(f"{path}:{line_number} has noncanonical byte size")
        if LOWER_SHA256.fullmatch(sha256) is None:
            raise ValueError(f"{path}:{line_number} has noncanonical SHA-256")
        rows.append(ResourceRow(resource, int(size_text), sha256))
    names = [row.path for row in rows]
    if names != sorted(names):
        raise ValueError(f"{path} resource paths are not sorted")
    if len(names) != len(set(names)):
        raise ValueError(f"{path} contains duplicate resource paths")
    return tuple(rows)


def verify_resource_manifest(
    archive: zipfile.ZipFile,
    manifest_path: Path,
    expected_rows: int,
) -> tuple[ResourceRow, ...]:
    rows = parse_resource_manifest(manifest_path)
    if len(rows) != expected_rows:
        raise ValueError(
            f"{manifest_path} row count changed: got {len(rows)}, "
            f"expected {expected_rows}"
        )
    archive_names = set(archive.namelist())
    for row in rows:
        if row.path not in archive_names:
            raise ValueError(f"artifact is missing required resource {row.path}")
        raw = archive.read(row.path)
        if len(raw) != row.size:
            raise ValueError(
                f"{row.path} byte size changed: got {len(raw)}, expected {row.size}"
            )
        verify_hash(
            f"{row.path} SHA-256", hashlib.sha256(raw).hexdigest(), row.sha256
        )
    return rows


def _catalog_record(identity: ArtifactIdentity) -> dict[str, object]:
    return {
        "modId": identity.mod_id,
        "metadataVersion": identity.metadata_version,
        "filename": identity.filename,
        "sizeBytes": identity.size,
        "sha1": identity.sha1,
        "sha256": identity.sha256,
        "sha512": identity.sha512,
        "license": "All rights reserved",
        "curseForgeProjectId": identity.project_id,
        "curseForgeFileId": identity.file_id,
        "curseForgeFingerprint": identity.fingerprint,
        "modrinthProjectId": identity.modrinth_project_id,
        "modrinthVersionId": identity.modrinth_version_id,
        "verificationRole": "required-static-render-input",
    }


def verify_profile_set(project: Path) -> tuple[ResourceRow, ...]:
    catalog_path = project / CATALOG_PATH
    profile_path = project / PROFILE_PATH
    manifest_path = project / MANIFEST_PATH
    verify_hash(
        "exact-artifacts catalog SHA-256",
        digest(catalog_path, "sha256"),
        CATALOG_SHA256,
    )
    verify_hash(
        "Pipez profile SHA-256", digest(profile_path, "sha256"), PROFILE_SHA256
    )
    verify_hash(
        "Pipez resource manifest SHA-256",
        digest(manifest_path, "sha256"),
        MANIFEST_SHA256,
    )

    catalog = json.loads(catalog_path.read_text(encoding="utf-8"))
    if catalog.get("schemaVersion") != 1:
        raise ValueError("exact-artifacts catalog schema changed")
    baseline = catalog.get("baseline")
    if not isinstance(baseline, dict) or {
        "packVersion": baseline.get("packVersion"),
        "packRepositoryCommit": baseline.get("packRepositoryCommit"),
        "minecraft": baseline.get("minecraft"),
        "neoforge": baseline.get("neoforge"),
        "java": baseline.get("java"),
    } != {
        "packVersion": "1.2.0",
        "packRepositoryCommit": "c7bb230f21d14d26859d0b92548f089b3a493ad9",
        "minecraft": "1.21.1",
        "neoforge": "21.1.248",
        "java": 21,
    }:
        raise ValueError("exact-artifacts baseline changed")
    if catalog.get("requiredForStaticRendering") != ["pipez"]:
        raise ValueError("required static-render artifact set changed")
    records = catalog.get("artifacts")
    if not isinstance(records, list) or len(records) != 1:
        raise ValueError("exact-artifacts catalog must contain one record")
    record = records[0]
    if not isinstance(record, dict):
        raise ValueError("exact-artifacts record is malformed")
    expected_record = _catalog_record(PIPEZ)
    if {field: record.get(field) for field in expected_record} != expected_record:
        raise ValueError("exact-artifacts Pipez record changed")
    source = record.get("sourceCorrelation")
    if not isinstance(source, dict) or {
        "status": source.get("status"),
        "commit": source.get("commit"),
    } != {
        "status": "release-correlated-not-attested",
        "commit": "91a01deda19e5beb19fe7840aa14daaa312aa212",
    }:
        raise ValueError("source correlation record changed")

    profile = json.loads(profile_path.read_text(encoding="utf-8"))
    expected_identity = {
        "schemaVersion": 1,
        "profileId": "pipez",
        "modId": PIPEZ.mod_id,
        "version": PIPEZ.metadata_version,
        "artifact": PIPEZ.filename,
        "sizeBytes": PIPEZ.size,
        "sha1": PIPEZ.sha1,
        "sha256": PIPEZ.sha256,
        "sha512": PIPEZ.sha512,
        "minecraft": "1.21.1",
        "neoforge": "21.1.248",
    }
    if {field: profile.get(field) for field in expected_identity} != expected_identity:
        raise ValueError("Pipez profile identity changed")
    coverage = profile.get("coverage")
    if not isinstance(coverage, dict):
        raise ValueError("Pipez profile coverage is missing")
    if coverage.get("blockCount") != 5 or set(
        coverage.get("supportedBlocks", [])
    ) != BLOCK_IDS:
        raise ValueError("Pipez profile block catalog changed")
    if coverage.get("camouflage") != "not implemented by the exact Pipez artifact":
        raise ValueError("Pipez camouflage scope changed")
    block_entity = profile.get("blockEntity")
    if not isinstance(block_entity, dict) or block_entity.get("ignoredFields") != [
        "Upgrades"
    ]:
        raise ValueError("Pipez block-entity scope changed")
    closure = profile.get("resourceClosure")
    if not isinstance(closure, dict) or closure != {
        "manifest": "required-resources.tsv",
        "pathCount": 28,
        "resourceBytes": 9737,
        "manifestSha256": MANIFEST_SHA256,
    }:
        raise ValueError("Pipez resource closure changed")

    rows = parse_resource_manifest(manifest_path)
    if len(rows) != 28 or sum(row.size for row in rows) != 9737:
        raise ValueError("Pipez resource-manifest count or byte total changed")
    return rows


def dependency_by_id(
    metadata: dict[str, object], owner_mod_id: str, dependency_mod_id: str
) -> dict[str, object]:
    dependencies = metadata.get("dependencies")
    if not isinstance(dependencies, dict):
        raise ValueError("NeoForge metadata has no dependency table")
    owner_dependencies = dependencies.get(owner_mod_id)
    if not isinstance(owner_dependencies, list):
        raise ValueError(f"NeoForge metadata has no {owner_mod_id} dependency list")
    matches = [
        dependency
        for dependency in owner_dependencies
        if isinstance(dependency, dict)
        and dependency.get("modId") == dependency_mod_id
    ]
    if len(matches) != 1:
        raise ValueError(
            f"expected one {dependency_mod_id} dependency for {owner_mod_id}, "
            f"got {len(matches)}"
        )
    return matches[0]


def verify_metadata(raw: bytes, identity: ArtifactIdentity) -> None:
    metadata = tomllib.loads(raw.decode("utf-8"))
    if metadata.get("license") != "All rights reserved":
        raise ValueError("Pipez metadata license changed")
    if metadata.get("modLoader") != "javafml" or metadata.get("loaderVersion") != "*":
        raise ValueError("Pipez loader identity changed")
    mods = metadata.get("mods")
    if not isinstance(mods, list) or len(mods) != 1 or not isinstance(mods[0], dict):
        raise ValueError("Pipez metadata must declare exactly one mod")
    mod = mods[0]
    if mod.get("modId") != identity.mod_id or mod.get("version") != identity.metadata_version:
        raise ValueError("Pipez metadata identity changed")
    dependencies = metadata.get("dependencies", {}).get(identity.mod_id, [])
    if len(dependencies) != len(identity.dependencies):
        raise ValueError("Pipez dependency set changed")
    for dependency_mod_id, expected_values in identity.dependencies.items():
        dependency = dependency_by_id(metadata, identity.mod_id, dependency_mod_id)
        expected = dict(
            zip(("type", "versionRange", "ordering", "side"), expected_values)
        )
        if {field: dependency.get(field) for field in expected} != expected:
            raise ValueError(f"Pipez {dependency_mod_id} dependency contract changed")


def parse_jar_manifest(raw: bytes) -> dict[str, str]:
    text = raw.decode("utf-8").replace("\r\n", "\n")
    unfolded: list[str] = []
    for line in text.split("\n"):
        if line.startswith(" "):
            if not unfolded:
                raise ValueError("JAR manifest starts with a continuation line")
            unfolded[-1] += line[1:]
        elif line:
            unfolded.append(line)
    values: dict[str, str] = {}
    for line in unfolded:
        if ": " not in line:
            raise ValueError("JAR manifest contains a malformed header")
        key, value = line.split(": ", 1)
        if key in values:
            raise ValueError(f"JAR manifest repeats {key}")
        values[key] = value
    return values


def verify_archive(
    jar: Path,
    identity: ArtifactIdentity,
    resource_rows: tuple[ResourceRow, ...],
    manifest_path: Path,
) -> None:
    with zipfile.ZipFile(jar) as archive:
        names = archive.namelist()
        if len(names) != len(set(names)):
            raise ValueError("Pipez contains duplicate ZIP entry names")
        verify_metadata(archive.read("META-INF/neoforge.mods.toml"), identity)
        manifest = parse_jar_manifest(archive.read("META-INF/MANIFEST.MF"))
        if manifest.get("Class-Path") != "corelib-1.21.1-2.1.4-api.jar":
            raise ValueError("Pipez manifest Class-Path changed")
        header = archive.read(identity.class_path)[:8]
        if len(header) != 8 or header[:4] != b"\xca\xfe\xba\xbe":
            raise ValueError(f"{identity.class_path} has an invalid class-file header")
        class_major = struct.unpack(">H", header[6:8])[0]
        if class_major != 65:
            raise ValueError(
                f"{identity.class_path} major is {class_major}, expected Java 21 (65)"
            )
        required_classes = {
            "de/maxhenkel/pipez/blocks/ModBlocks.class",
            "de/maxhenkel/pipez/blocks/PipeBlock.class",
            "de/maxhenkel/pipez/blocks/tileentity/PipeTileEntity.class",
            "de/maxhenkel/pipez/blocks/tileentity/render/PipeRenderer.class",
        }
        missing_classes = required_classes.difference(names)
        if missing_classes:
            raise ValueError(f"Pipez is missing audited classes: {missing_classes}")
        verify_resource_manifest(archive, manifest_path, len(resource_rows))


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--pipez", required=True, type=Path)
    args = parser.parse_args()

    project = Path(__file__).resolve().parents[1]
    resources = verify_profile_set(project)
    verify_exact_identity(args.pipez, PIPEZ)
    verify_archive(args.pipez, PIPEZ, resources, project / MANIFEST_PATH)
    print(
        "Verified exact All the Mons 1.2.0 Pipez 1.21.1-1.2.31 artifact, "
        "metadata, Java 21 classes, 28 stable exterior resources and five "
        "supported world blocks."
    )
    return 0


if __name__ == "__main__":
    try:
        sys.exit(main())
    except (
        json.JSONDecodeError,
        KeyError,
        OSError,
        tomllib.TOMLDecodeError,
        UnicodeDecodeError,
        ValueError,
        zipfile.BadZipFile,
    ) as error:
        print(f"verification failed: {error}", file=sys.stderr)
        sys.exit(1)
