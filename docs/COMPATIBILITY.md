# Compatibility

Compatibility is intentionally exact and evidence-locked.

| Component | Accepted identity |
| --- | --- |
| All the Mons | `1.2.0`, repository commit `c7bb230f21d14d26859d0b92548f089b3a493ad9` |
| Minecraft | `1.21.1` |
| NeoForge | `21.1.248` |
| Java | `21` |
| BlueMap | upstream `5.22` at `fe5115d5548a30d34175b8e0449aaca280af199f`, or exact ATM backport at `9be321df995a1103808621d529eb72773e719d4d` |
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
