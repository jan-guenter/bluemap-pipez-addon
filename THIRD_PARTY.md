# Third-party components

| Component | Use | Exact identity | Declared license | Bundled |
| --- | --- | --- | --- | --- |
| BlueMap | Compile-time and runtime host API/internal ABI | Feature backport `5.22-feature.backport-5.23-stateless-java-web-server-46`, commit `7e07f4e74ec1e92a6ead9aa1e66054af3e133aac`; API commit `285c9a60eff3ac2b0cab308ce1058d1565be0971` | MIT | No |
| Pipez | Operator-supplied blockstates, models, textures, and persisted world data | `1.21.1-1.2.31`, 456,599 bytes, SHA-256 `9b37e922443ea3452daeacbfba4bcf69de07692183c4ee09f1d1e82c9fc5cc5f` | All rights reserved | No |
| BlueMap Add-on Render Core | First-party transformed-face lighting source | `0.1.0-alpha.2`, tag `v0.1.0-alpha.2`, commit `24b84efdc8235f3f1323e1a8e9fd033080e3a79e`, source tree `424040931680fb82d37693f893ca887c0ed48eae` | MIT | One source compiles into this add-on; no module JAR |
| BlueMap Add-on Adapter API | First-party runtime, registry, extension, and dispatch sources | `0.1.0-alpha.2`, tag `v0.1.0-alpha.2`, commit `e81f08bc4bfbf02d810ec8949a019130e2e61634`, source tree `2f974c9bb2ba13888d69682f86f30f58922d30eb` | MIT | Four sources compile into this add-on; no module JAR |
| JetBrains annotations | Compile-only transitive host dependency | `23.0.0` | Apache-2.0 | No |
| JUnit | Test framework | `5.11.4` | EPL-2.0 | No |
| Checkstyle | Source-style verification | `10.18.2` | LGPL-2.1-or-later | No |
| Gradle | Build tool used by CI | `9.4.0` | Apache-2.0 | No |

The packaged exact-resource manifest contains only paths, byte sizes, and
hashes of operator-supplied resources. It contains no third-party resource
bytes.
