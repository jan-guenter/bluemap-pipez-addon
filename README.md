# BlueMap Pipez Add-on

[![CI](https://github.com/jan-guenter/bluemap-pipez-addon/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/jan-guenter/bluemap-pipez-addon/actions/workflows/ci.yml)

An exact-profile BlueMap 5.22 add-on for the stable world appearance of
Pipez connections and extractor plates.

## Status and compatibility

Version `0.1.0-alpha.1` is the owner-accepted prerelease for one environment:

- All the Mons `1.2.0`, Minecraft `1.21.1`, NeoForge `21.1.248`, Java `21`;
- BlueMap backport `5.22-agent.backport-5.22-mc1.21.1-2` at
  `9be321df995a1103808621d529eb72773e719d4d`;
- Pipez `1.21.1-1.2.31`, exact 456,599-byte JAR with SHA-256
  `9b37e922443ea3452daeacbfba4bcf69de07692183c4ee09f1d1e82c9fc5cc5f`.

The runtime activates only for those exact Pipez bytes. A same-named or
same-version file with a different size or SHA-256 remains on BlueMap's stock
resource path.

The accepted production JAR is 53,921 bytes with SHA-256
`e81dea280d08e19ea4602e5a0700f4ab7004ca74e3408bfba3a898cb745e67db`.
Its corrected extractor-face mapping passed pull-request CI, the single
37-anchor staging gate, and owner comparison between Minecraft and BlueMap on
2026-08-12.

## Visual scope

The exact artifact installs five world blocks: item, fluid, energy, universal,
and gas pipes. Their stable map appearance consists of:

- each type's core and six-direction connected arms from the installed
  multipart blockstate and JSON models;
- extractor plates selected by the persisted `ExtractingSides` six-byte list;
- stable disconnected topology already encoded in the six directional
  blockstate booleans;
- BlueMap's ordinary waterlogged overlay.

Stock BlueMap already renders cores and ordinary arms. It does not execute
Pipez's client block-entity renderer, so extractor plates disappear without
this add-on. The custom route keeps BlueMap's installed-resource rendering for
the core and arms and adds only the missing installed extractor model on each
persisted face.

Transported items, fluids, energy, gas, upgrade inventory/configuration,
activity, and transfer timing are excluded. Exact Pipez 1.2.31 has no cover or
camouflage system, so there is no camouflage scope to implement. Unknown or
malformed observations use the original stock path atomically.

See [coverage](docs/COVERAGE.md), [architecture](docs/ARCHITECTURE.md),
[compatibility](docs/COMPATIBILITY.md), [provenance](docs/PROVENANCE.md), and
the single [staging gate](docs/STAGING.md).

## Authoritative review gate

Use Java 21 and the exact sibling BlueMap checkout. Supply the exact
operator-downloaded Pipez JAR once:

```bash
gradle --no-daemon \
  -PpipezJar=/absolute/path/pipez-neoforge-1.21.1-1.2.31.jar \
  clean check build generatePomFileForAddonPublication \
  generateMetadataFileForAddonPublication verifyPinnedArtifacts
```

Pull-request CI reacquires the JAR ephemerally from its exact Modrinth version,
verifies every digest, metadata, Java level, and 28-path resource closure, and
discards it. The build never bundles or redistributes Pipez bytes.

Tagged releases publish production/source JARs, POM, module metadata, and
checksums on GitHub Releases and at Maven coordinates
`io.github.jan-guenter:bluemap-pipez-addon:<version>` on GitHub Packages. A
release tag must equal `v<addon_version>`.

## Installation

Place only the reviewed add-on JAR in BlueMap's `config/bluemap/packs`
directory and restart the JVM. It is not a NeoForge mod and does not belong in
the server's `mods` directory. It writes no world or player data.

## License

The add-on is independently written and released under the [MIT License](LICENSE).
Third-party software and resources are not bundled; see
[THIRD_PARTY.md](THIRD_PARTY.md) and [NOTICE.md](NOTICE.md).
