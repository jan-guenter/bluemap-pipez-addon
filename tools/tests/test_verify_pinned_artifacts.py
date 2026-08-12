# SPDX-License-Identifier: MIT

from dataclasses import replace
import hashlib
import sys
import tempfile
import unittest
from pathlib import Path
import zipfile


ROOT = Path(__file__).resolve().parents[2]
sys.path.insert(0, str(ROOT / "tools"))

from verify_pinned_artifacts import (  # noqa: E402
    BLOCK_IDS,
    MANIFEST_PATH,
    PIPEZ,
    ArtifactIdentity,
    parse_resource_manifest,
    verify_archive,
    verify_exact_identity,
    verify_profile_set,
    verify_resource_manifest,
)


class VerifyPinnedArtifactsTest(unittest.TestCase):
    def test_exact_artifact_and_catalog_are_closed(self):
        self.assertEqual("pipez", PIPEZ.mod_id)
        self.assertEqual(456_599, PIPEZ.size)
        self.assertEqual(5, len(BLOCK_IDS))
        self.assertEqual("iRmWy6ga", PIPEZ.modrinth_project_id)
        self.assertEqual("BPGKb8pi", PIPEZ.modrinth_version_id)

    def test_profile_and_resource_manifest_are_exact(self):
        resources = verify_profile_set(ROOT)
        self.assertEqual(28, len(resources))
        self.assertEqual(9737, sum(row.size for row in resources))
        self.assertEqual(5, len([row for row in resources if "/blockstates/" in row.path]))
        self.assertEqual(5, len([row for row in resources if "/textures/" in row.path]))

    def test_identity_guard_checks_every_digest(self):
        with tempfile.TemporaryDirectory() as temporary:
            path = Path(temporary) / "fixture.jar"
            path.write_bytes(b"exact fixture")
            raw = path.read_bytes()
            identity = ArtifactIdentity(
                "fixture",
                "1.0.0",
                path.name,
                len(raw),
                hashlib.sha1(raw).hexdigest(),
                hashlib.sha256(raw).hexdigest(),
                hashlib.sha512(raw).hexdigest(),
                1,
                2,
                3,
                "project",
                "version",
                "fixture/Main.class",
                {},
            )
            verify_exact_identity(path, identity)
            path.write_bytes(b"wrong fixture")
            with self.assertRaisesRegex(ValueError, "changed"):
                verify_exact_identity(path, identity)
            with self.assertRaisesRegex(ValueError, "filename changed"):
                verify_exact_identity(path, replace(identity, filename="different.jar"))

    def test_manifest_parser_rejects_noncanonical_rows(self):
        with tempfile.TemporaryDirectory() as temporary:
            path = Path(temporary) / "manifest.tsv"
            sha256 = hashlib.sha256(b"x").hexdigest()
            path.write_text(
                f"assets/test/z.txt\t1\t{sha256}\n"
                f"assets/test/a.txt\t1\t{sha256}\n",
                encoding="ascii",
            )
            with self.assertRaisesRegex(ValueError, "not sorted"):
                parse_resource_manifest(path)
            path.write_text(f"../escape\t1\t{sha256}\n", encoding="ascii")
            with self.assertRaisesRegex(ValueError, "unsafe"):
                parse_resource_manifest(path)
            path.write_text(
                f"assets/test/a.txt\t1\t{sha256}\n"
                f"assets/test/a.txt\t1\t{sha256}\n",
                encoding="ascii",
            )
            with self.assertRaisesRegex(ValueError, "duplicate"):
                parse_resource_manifest(path)

    def test_resource_rows_are_verified_against_archive_bytes(self):
        with tempfile.TemporaryDirectory() as temporary:
            root = Path(temporary)
            archive_path = root / "fixture.jar"
            manifest_path = root / "manifest.tsv"
            resource = "assets/test/value.txt"
            raw = b"verified stable exterior resource"
            with zipfile.ZipFile(archive_path, "w") as archive:
                archive.writestr(resource, raw)
            manifest_path.write_text(
                f"{resource}\t{len(raw)}\t{hashlib.sha256(raw).hexdigest()}\n",
                encoding="ascii",
            )
            with zipfile.ZipFile(archive_path) as archive:
                verify_resource_manifest(archive, manifest_path, 1)
                manifest_path.write_text(
                    f"{resource}\t{len(raw)}\t{'0' * 64}\n",
                    encoding="ascii",
                )
                with self.assertRaisesRegex(ValueError, "SHA-256.*changed"):
                    verify_resource_manifest(archive, manifest_path, 1)

    def test_exact_archive_passes_complete_metadata_and_resource_gate(self):
        exact = Path("/tmp/pipez-jar-audit.EqpVYb/pipez-neoforge-1.21.1-1.2.31.jar")
        if not exact.is_file():
            self.skipTest("private exact artifact is not present")
        resources = verify_profile_set(ROOT)
        verify_archive(exact, PIPEZ, resources, ROOT / MANIFEST_PATH)


if __name__ == "__main__":
    unittest.main()
