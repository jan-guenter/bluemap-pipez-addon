# Visual coverage

The exact closed catalog contains five blocks:

- `pipez:item_pipe`;
- `pipez:fluid_pipe`;
- `pipez:energy_pipe`;
- `pipez:universal_pipe`;
- `pipez:gas_pipe`.

## Included stable optics

- exact per-type texture and central core;
- each of the six installed connection arms from directional blockstate;
- persisted extractor plates on all six faces and multi-face combinations;
- stable deliberate disconnection as represented by absent blockstate arms;
- stock BlueMap waterlogging around a pipe.

## Deliberately excluded

- transported items, fluid amount/type, energy, gas, or transfer progress;
- transfer activity, timing, rates, particles, and sound;
- upgrade inventory, filters, redstone configuration, and GUI-only state;
- capability probing of neighboring machines during map rendering;
- covers or camouflage, because exact Pipez 1.2.31 implements neither.

The gallery uses 37 Pipez anchors: five isolated types, five three-block
straight lines, one seven-block universal six-way fixture, six individual
extractor faces, one multi-extractor, one disconnected pair, and one
waterlogged representative. Unit tests cover all five types across all 64
connection masks without expanding the owner-facing gallery.

Malformed observations use atomic stock fallback. No partial extractor
geometry remains in the tile model.

The owner accepted this bounded stable-optics scope on 2026-08-12 after
comparing the corrected extractor-face result in Minecraft and BlueMap. That
acceptance does not extend to the deliberately excluded transient state.
