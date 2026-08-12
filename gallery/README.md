# Review gallery

This deterministic All the Mons 1.2.0 gallery contains 37 Pipez anchors:

- five isolated type cores;
- five three-block straight lines (15 anchors);
- one universal six-way fixture (seven anchors);
- six individual extractor-face fixtures;
- one three-face extractor fixture;
- one deliberately disconnected adjacent pair;
- one waterlogged item pipe.

It covers every installed type texture, all ordinary arm and extractor
rotations, multi-face extraction, stable disconnection, and waterlogging while
excluding contents, flow/activity, upgrades, and nonexistent camouflage.
Exhaustive five-type × 64-connection-mask coverage stays in unit tests rather
than inflating visual review.

Run `python3 generate.py` to regenerate tracked manifests/functions, or
`python3 generate.py --check` in CI. Package the datapack with
`./package.sh /absolute/output/pipez-gallery.zip`, then use
`function pipez_gallery:build` and `function pipez_gallery:verify` on staging.

The owner accepted the corrected extractor-face result on 2026-08-12 after
comparing Minecraft and BlueMap directly. The disposable world, rendered map,
screenshots, and logs may now be replaced by the next add-on cycle. Do not
commit third-party resources, world data, or client captures.
