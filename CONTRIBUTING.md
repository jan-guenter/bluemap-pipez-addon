# Contributing

Open an issue before expanding the supported byte identity or visual scope.
Compatibility is exact-profile data, not a version range.

Contributions must be independently written under MIT. Do not copy or adapt
Pipez source, classes, models, textures, translations, captures, or meshes.
Private inspection of an operator-supplied artifact may establish factual
identifiers, persisted formats, resource paths, sizes, and hashes; third-party
bytes must not be committed or published.

Keep one fail-closed Pipez route. Malformed state must return the whole block
to BlueMap's original rendering. Transported contents, flow/activity,
upgrades, and timing remain outside scope, and the exact artifact has no
camouflage feature.

Submit one complete change and rely on the pull-request gate instead of
repeating full local builds after every edit. The authoritative command is in
[AGENTS.md](AGENTS.md). Clone with `--recurse-submodules`, or initialize an
existing checkout with:

```bash
git submodule update --init --recursive -- \
  tooling/bluemap-addon-toolkit modules/bluemap-addon-render-core
```
