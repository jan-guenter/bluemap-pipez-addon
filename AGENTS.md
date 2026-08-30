# Agent guide for BlueMap Pipez Add-on

Read `/root/work/allthemons/AGENTS.md` and this file before changing this
repository. This is a standalone public MIT project, not a NeoForge mod and
not part of the root orchestration repository.

## Exact baseline

| Component | Identity |
| --- | --- |
| All the Mons | `1.2.0`, pack commit `c7bb230f21d14d26859d0b92548f089b3a493ad9` |
| Minecraft / NeoForge / Java | `1.21.1` / `21.1.248` / `21` |
| BlueMap | feature backport `5.22-feature.backport-5.23-stateless-java-web-server-46`, commit `7e07f4e74ec1e92a6ead9aa1e66054af3e133aac` |
| Pipez | `pipez-neoforge-1.21.1-1.2.31.jar`, 456,599 bytes, SHA-256 `9b37e922443ea3452daeacbfba4bcf69de07692183c4ee09f1d1e82c9fc5cc5f` |
| Render Core | `0.1.0-alpha.2`, commit `24b84efdc8235f3f1323e1a8e9fd033080e3a79e`, source tree `424040931680fb82d37693f893ca887c0ed48eae` |
| Adapter API | `0.1.0-alpha.2`, commit `e81f08bc4bfbf02d810ec8949a019130e2e61634`, source tree `2f974c9bb2ba13888d69682f86f30f58922d30eb` |

Do not treat the version string alone as compatibility proof. A new pack or
mod file is a fresh evidence, implementation, and review task.

## Project boundaries

- The production JAR is a plain BlueMap add-on loaded from BlueMap's packs
  directory. It contains no NeoForge metadata, Mixins, nested JARs, client
  bootstrap, or third-party classes/assets. It compiles the exact pinned
  first-party render-core and adapter API sources into the add-on instead of
  nesting or installing either module JAR.
- The exact five-block catalog is `item_pipe`, `fluid_pipe`, `energy_pipe`,
  `universal_pipe`, and `gas_pipe` in the `pipez` namespace.
- Preserve stable cores, connected arms, persisted extractor plates,
  disconnection topology, and stock waterlogging.
- Ignore transported contents, transfer flow/activity, upgrades, and timing.
  Exact 1.2.31 has no cover/camouflage feature.
- Missing, malformed, unsupported, or mismatched observations use BlueMap's
  original blockstate/model path atomically.
- The implementation is clean-room MIT. Never copy/adapt upstream Pipez
  source or package its classes, resources, captures, or derived meshes.

## Validation cadence

Develop in one coherent tranche. Pull-request CI is the authoritative full
build and exact-input gate; do not repeat it locally after small edits:

```bash
git submodule update --init --recursive -- \
  tooling/bluemap-addon-toolkit modules/bluemap-addon-render-core \
  modules/bluemap-addon-adapter-api
gradle --no-daemon \
  -PpipezJar=/absolute/path/pipez-neoforge-1.21.1-1.2.31.jar \
  clean check build generatePomFileForAddonPublication \
  generateMetadataFileForAddonPublication verifyPinnedArtifacts
```

After CI, use the single enabled staging pass in [docs/STAGING.md](docs/STAGING.md).
The disposable gallery/server/PVC may replace evidence from an already
accepted add-on. Before presenting a BlueMap link, open that exact view in the
agent browser and perform the required quick blank/black/gross-breakage sanity
check. Do not claim CI, runtime, persistence, visual acceptance, publication,
or deployment until that exact result was observed.
