# Architecture

The release unit is one plain BlueMap add-on JAR for exact Pipez 1.2.31. It is
not a NeoForge mod and never loads Minecraft's client renderer.

```text
BlueMap add-on entrypoint
        |
exact BlueMap 5.23 feature-backport internal-ABI adapter
        |
exact Pipez artifact size/SHA-256 gate
        |
five exact block and block-entity IDs
        |
strict blockstate + six-byte-list snapshot
        |
stock installed core/arm models + installed extractor model
        |
commit-pinned transformed-face lighting source
        |
commit-pinned adapter API sources
        |
atomic original-resource fallback
```

## Activation and failure boundary

The single route begins inactive. It activates only when BlueMap's resource
roots contain exactly one JAR declaring `pipez` with the exact size and
SHA-256. The resource extension then requires the five multipart blockstates,
18 model keys, five texture keys, and one valid synthetic dispatch model.

Only while active are the five exact blockstate IDs redirected. Registry
collisions, an artifact mismatch, or an incomplete resource closure leave the
route inactive. A renderer invariant failure marks the route failed for the
rest of the process. A malformed individual block uses stock fallback without
poisoning other blocks.

## Rendering model

The six directional blockstate booleans are the persisted connection topology.
BlueMap's ordinary resource renderer draws each type's installed core and the
matching rotated arms. This preserves stock JSON semantics and avoids any
capability or neighboring-machine discovery during map rendering.

When `has_data=true`, BlueMap's registered block-entity projection reads only
`ExtractingSides` and `DisconnectedSides`. They are NBT lists of bytes indexed
`DOWN, UP, NORTH, SOUTH, WEST, EAST`. Pipez treats a missing or short list as
all false and any nonzero byte as true. `DisconnectedSides` is retained for
validation/diagnostics; visible arm topology remains authoritative in the
blockstate.

For each extracting side, the renderer emits the installed per-type
`*_pipe_extract` JSON model with the exact client rotations and a 0.001-block
outward offset. Extraction is intentionally independent of the corresponding
arm boolean because the exact client renderer has that behavior. BlueMap's
outer blockstate renderer supplies water for `waterlogged=true` after the
custom variant completes.

`FaceLighting` compiles from the exact pinned `bluemap-addon-render-core`
source tree. It rotates each requested face through the model variant and
samples the host and exposed neighbor for maximum sunlight, block light, and
model emission. Runtime identity checks, registry admission, the resource
extension factory, and synthetic dispatch validation compile from the exact
pinned `bluemap-addon-adapter-api` source tree. Neither module has an installed
runtime. Geometry, resource access, and fallback remain in this add-on.

Before custom output, the renderer records the tile-model and map-color start.
Any failed decode or emission resets partial geometry and invokes the raw
installed Pipez blockstate through BlueMap's stock renderer. No half-custom
block remains.

## Resource ownership

The production JAR owns only its entrypoint, adapter/renderer code, synthetic
dispatch blockstate, exact profile facts, and resource path/size/hash manifest.
It also contains one first-party MIT render-core source and four first-party
MIT adapter API sources compiled into the consumer. BlueMap and all
Minecraft/Pipez resources remain operator supplied. No module JAR is nested or
installed.
