# Provenance

The evidence lock is machine-readable in
`src/main/resources/bluemap-pipez/profiles/exact-artifacts.json`, the exact
profile beside it, and `provenance/upstreams.json`.
`provenance/release.json` separately binds the promoted consumer version to
its exact publication files, accepted baseline, and source-module migration.

The All the Mons 1.2.0 client export manifest identifies CurseForge
project/file `443900/8351631`. Its runtime ledger names
`pipez-neoforge-1.21.1-1.2.31.jar`, gives fingerprint `411466094`, and records
SHA-1 `a5671f7e8d38dfc092ace4091250e8f9e1245e1e`. The exact official server
archive member and the disposable 1.2.0 staging installation were independently
measured as 456,599 bytes with SHA-256
`9b37e922443ea3452daeacbfba4bcf69de07692183c4ee09f1d1e82c9fc5cc5f`.
Modrinth project/version `iRmWy6ga/BPGKb8pi` publishes those same bytes.

The exact JAR is authoritative. The verifier checks filename, size, SHA-1,
SHA-256, SHA-512, NeoForge metadata and dependency closure, Java 21 class
identity, selected audited classes, and all 28 blockstate/model/texture paths
with their individual sizes and SHA-256 values.

Public branch `1.21.1` commit
`91a01deda19e5beb19fe7840aa14daaa312aa212` changes the source version to the
exact runtime version and has a byte-identical resource closure after
line-ending normalization. There is no immutable source tag, build attestation,
or reproducible-binary proof, so this is release correlation—not exact binary
source proof. The repository and runtime artifact are All rights reserved and
are used only for factual inspection.

The implementation is clean-room MIT. No upstream source, compiled class,
model, texture, translation, capture, or mesh is committed. The historical All
the Mons 1.1.1 knowledge snapshot was a discovery hint only and is not evidence
for this 1.2.0 profile.

Repository/build/release mechanics, the BlueMap adapter boundary, and the
ordinary JSON emitter were seeded from this owner's independently authored MIT
`bluemap-sophisticated-addon` commit
`a75b1d82c3987fa9360a1e8a5910eedf90aca7cb`. No Sophisticated profile,
family-specific decoder/renderer/catalog, resource fact, or observation was
carried into Pipez.

Version `0.1.0-alpha.3` source-bundles `FaceLighting` from the first-party MIT
`bluemap-addon-render-core` `0.1.0-alpha.2` release at commit
`24b84efdc8235f3f1323e1a8e9fd033080e3a79e`, source tree
`424040931680fb82d37693f893ca887c0ed48eae`. That release moves the helper to
the 5.23-only package without changing its behavior. The build also compiles
the four bootstrap helpers from `bluemap-addon-adapter-api` `0.1.0-alpha.2` at
commit `e81f08bc4bfbf02d810ec8949a019130e2e61634`, source tree
`2f974c9bb2ba13888d69682f86f30f58922d30eb`. Neither module JAR is installed
or nested. Pipez profile facts, decoding, geometry, routing, and fallback
remain local.
