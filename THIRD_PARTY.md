# Third-party components

| Component | Use | Exact identity | Declared license | Bundled |
| --- | --- | --- | --- | --- |
| BlueMap | Compile-time and runtime host API/internal ABI | Backport `5.22-agent.backport-5.22-mc1.21.1-2`, commit `9be321df995a1103808621d529eb72773e719d4d`; API commit `285c9a60eff3ac2b0cab308ce1058d1565be0971` | MIT | No |
| Pipez | Operator-supplied blockstates, models, textures, and persisted world data | `1.21.1-1.2.31`, 456,599 bytes, SHA-256 `9b37e922443ea3452daeacbfba4bcf69de07692183c4ee09f1d1e82c9fc5cc5f` | All rights reserved | No |
| JetBrains annotations | Compile-only transitive host dependency | `23.0.0` | Apache-2.0 | No |
| JUnit | Test framework | `5.11.4` | EPL-2.0 | No |
| Checkstyle | Source-style verification | `10.18.2` | LGPL-2.1-or-later | No |
| Gradle | Build tool used by CI | `9.4.0` | Apache-2.0 | No |

The packaged exact-resource manifest contains only paths, byte sizes, and
hashes of operator-supplied resources. It contains no third-party resource
bytes.
