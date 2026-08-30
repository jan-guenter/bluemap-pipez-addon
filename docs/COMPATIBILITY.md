# Compatibility

Compatibility is intentionally exact and evidence-locked.

| Component | Accepted identity |
| --- | --- |
| All the Mons | `1.2.0`, repository commit `c7bb230f21d14d26859d0b92548f089b3a493ad9` |
| Minecraft | `1.21.1` |
| NeoForge | `21.1.248` |
| Java | `21` |
| BlueMap | feature backport `5.22-feature.backport-5.23-stateless-java-web-server-46` at `7e07f4e74ec1e92a6ead9aa1e66054af3e133aac`, API commit `285c9a60eff3ac2b0cab308ce1058d1565be0971` |
| Pipez | `pipez-neoforge-1.21.1-1.2.31.jar`, 456,599 bytes, SHA-1 `a5671f7e8d38dfc092ace4091250e8f9e1245e1e`, SHA-256 `9b37e922443ea3452daeacbfba4bcf69de07692183c4ee09f1d1e82c9fc5cc5f` |

The All the Mons client manifest identifies CurseForge project/file
`443900/8351631` and fingerprint `411466094`. The correlated Modrinth
distribution is project/version `iRmWy6ga/BPGKb8pi`.

At runtime, the route activates only after the exact Pipez bytes are visible in
BlueMap's resource roots. Resource baking also requires every model and texture
used by the five-block profile.

This does not claim compatibility with later pack releases, other Pipez builds
sharing a semantic version, legacy All the Mons 1.1.1 inputs, or resource-pack
overrides. Every new byte identity requires a new profile and visual review.
