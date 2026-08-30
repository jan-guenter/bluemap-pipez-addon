# Single staging gate

Use the existing disposable Minecraft server and PVC. Evidence from an already
accepted add-on may be replaced; preserve only this add-on's current review
inputs until owner acceptance.

## Server baseline

Apply the requested low-cost staging settings before startup:

```ini
advance_time=false
advance_weather=false
random_tick_speed=0
spread_vines=false
spawn_mobs=false
spawn_monsters=false
spawn_patrols=false
spawn_phantoms=false
spawn_wandering_traders=false
spawn_wardens=false
spawner_blocks_work=false
pvp=false
player_movement_check=false
freeze_damage=false
fire_damage=false
fall_damage=false
drowning_damage=false
raids=false
global_sound_events=false
```

Keep the exact All the Mons 1.2.0 Pipez JAR in `mods`. Install only the
candidate `bluemap-pipez-addon` JAR in BlueMap's packs directory. Name the
bounded review map `pipez_staging`.

## One enabled pass

1. Package/install the generated gallery datapack, start once, and run
   `function pipez_gallery:build` followed by `function pipez_gallery:verify`.
   The scoreboard must report 37 checked anchors and zero failures.
2. Save the world, restart once, rerun only the verify function, and require the
   same persisted result. This is the one state-persistence check.
3. Confirm the exact profile activated with no adapter, registry, resource,
   decoder, or render failure; purge/render only the bounded map.
4. Run the existing rendered-block-model analyzer census and require all 37
   Pipez anchors to be nonempty. Treat stock support barrels and the platform as
   controls, not routed anchors.
5. Open the exact external `#pipez_staging` link in the agent browser for a
   quick sanity check against blank, black, missing, or grossly broken output.
   Then present that link for owner inspection.

This enabled pass is the release staging gate. A separate stock comparison,
rollback lifecycle, transient-flow gallery, or legacy 1.1.1 run is not
required.

## Accepted result

The owner accepted the corrected `0.1.0-alpha.1` result on 2026-08-12. The
exact staged production JAR was 53,921 bytes with SHA-256
`e81dea280d08e19ea4602e5a0700f4ab7004ca74e3408bfba3a898cb745e67db`.
It loaded exactly once without a targeted fault, the deterministic gallery
verified 37/37 anchors, and the bounded purge/render completed. A subsequent
clean exit-0 restart again loaded exactly one add-on and reached BlueMap's
loaded state without a targeted fault. The agent browser sanity check passed
before owner inspection. The owner then compared the corrected extractor
faces in Minecraft and BlueMap and approved the visual result.

The disposable world, map, screenshots, and runtime logs may now be replaced
by the next accepted add-on cycle under the shared staging policy.

## Accepted BlueMap 5.23 visual result

The owner accepted the `0.1.0-alpha.3` visual result on 2026-08-30 after
reviewing the combined All the Mons 1.2.0 BlueMap gallery. The base candidate
came from commit `e7af5cf372face707187d8d9544a345dea1ed585`, tree
`7c46a66dcd24b0ee37a667fba3fcd9fcf3b87e45`. Its production JAR was 57,846
bytes with SHA-256
`6e4d71baf9f7acc199ff94fe8fc887678bcde9224fb6ae49d3d228f3481e1d2d`.

The integration builder replaced `BlueMapPipezAddon.class` to add install
validation and an activation marker. The gallery therefore ran a 57,893-byte
instrumented overlay with SHA-256
`70ffe682fb5319aadb5519a5846804a50cabbd0d86436542b4d2f3adbb75f149`,
not the exact production JAR. The exact production artifact still needs the
runtime portion of this release gate.

The combined suite passed all 51 add-ons on two distinct boots. Pipez passed
56 checks with zero failures. The bounded render produced 589 fresh tiles
across four regions. `provenance/release.json` records both artifact identities
and the exact evidence-file, runtime, candidate-manifest, and rendered-tile
hashes.
