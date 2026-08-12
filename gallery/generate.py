#!/usr/bin/env python3
# SPDX-License-Identifier: MIT
"""Generate the lean deterministic Pipez stable-optics review gallery."""

from __future__ import annotations

import argparse
from dataclasses import dataclass
import hashlib
import json
from pathlib import Path
import sys
from typing import Iterable


ROOT = Path(__file__).resolve().parent
DIRECTIONS = ("down", "up", "north", "south", "west", "east")
STEPS = {
    "down": (0, -1, 0),
    "up": (0, 1, 0),
    "north": (0, 0, -1),
    "south": (0, 0, 1),
    "west": (-1, 0, 0),
    "east": (1, 0, 0),
}
OPPOSITE = {
    "down": "up",
    "up": "down",
    "north": "south",
    "south": "north",
    "west": "east",
    "east": "west",
}
PIPE_TYPES = ("item", "fluid", "energy", "universal", "gas")


@dataclass(frozen=True, order=True)
class Position:
    x: int
    y: int
    z: int

    def offset(self, direction: str) -> "Position":
        dx, dy, dz = STEPS[direction]
        return Position(self.x + dx, self.y + dy, self.z + dz)

    def command(self) -> str:
        return f"{self.x} {self.y} {self.z}"

    def manifest(self) -> dict[str, int]:
        return {"x": self.x, "y": self.y, "z": self.z}


@dataclass(frozen=True)
class Support:
    position: Position
    block: str


@dataclass(frozen=True)
class Anchor:
    anchor_id: str
    case_id: str
    position: Position
    pipe_type: str
    connections: frozenset[str]
    extracting: frozenset[str] = frozenset()
    disconnected: frozenset[str] = frozenset()
    waterlogged: bool = False
    supports: tuple[Support, ...] = ()
    notes: str = ""

    @property
    def block_id(self) -> str:
        return f"pipez:{self.pipe_type}_pipe"

    @property
    def has_data(self) -> bool:
        return bool(self.extracting or self.disconnected)

    @property
    def properties(self) -> dict[str, str]:
        values = {
            direction: str(direction in self.connections).lower()
            for direction in DIRECTIONS
        }
        values["has_data"] = str(self.has_data).lower()
        values["waterlogged"] = str(self.waterlogged).lower()
        return values

    @property
    def block_spec(self) -> str:
        properties = ",".join(
            f"{key}={value}" for key, value in sorted(self.properties.items())
        )
        return f"{self.block_id}[{properties}]"

    def byte_list(self, selected: frozenset[str]) -> str:
        return "[" + ",".join(
            "1b" if direction in selected else "0b" for direction in DIRECTIONS
        ) + "]"

    @property
    def nbt_snbt(self) -> str:
        return (
            "{ExtractingSides:"
            + self.byte_list(self.extracting)
            + ",DisconnectedSides:"
            + self.byte_list(self.disconnected)
            + "}"
        )

    def manifest(self) -> dict[str, object]:
        stable = ["type-texture", "core", "connected-arms"]
        if self.extracting:
            stable.append("extractor-plates")
        if self.disconnected:
            stable.append("persisted-disconnection")
        if self.waterlogged:
            stable.append("stock-waterlogged-overlay")
        return {
            "anchor_id": self.anchor_id,
            "case_id": self.case_id,
            "position": self.position.manifest(),
            "block_id": self.block_id,
            "blockstate": dict(sorted(self.properties.items())),
            "block_entity_id": self.block_id if self.has_data else None,
            "nbt": {
                "ExtractingSides": [
                    1 if direction in self.extracting else 0
                    for direction in DIRECTIONS
                ],
                "DisconnectedSides": [
                    1 if direction in self.disconnected else 0
                    for direction in DIRECTIONS
                ],
            } if self.has_data else {},
            "expected_route": "custom",
            "stable_optics": stable,
            "notes": self.notes,
        }


def anchors() -> list[Anchor]:
    result: list[Anchor] = []

    for index, pipe_type in enumerate(PIPE_TYPES):
        result.append(Anchor(
            f"roster-{pipe_type}",
            "type-roster",
            Position(196 + index * 6, 100, 196),
            pipe_type,
            frozenset(),
            notes="isolated exact type texture and core",
        ))

    for index, pipe_type in enumerate(PIPE_TYPES):
        x = 196 + index * 6
        line = (
            ("north-end", 204, frozenset({"south"})),
            ("middle", 205, frozenset({"north", "south"})),
            ("south-end", 206, frozenset({"north"})),
        )
        for role, z, connections in line:
            result.append(Anchor(
                f"straight-{pipe_type}-{role}",
                "straight-topology",
                Position(x, 100, z),
                pipe_type,
                connections,
                notes="same-type persisted north/south topology",
            ))

    center = Position(232, 102, 210)
    result.append(Anchor(
        "six-way-center",
        "six-way-topology",
        center,
        "universal",
        frozenset(DIRECTIONS),
        notes="all six ordinary arm rotations on one center",
    ))
    for direction in DIRECTIONS:
        result.append(Anchor(
            f"six-way-arm-{direction}",
            "six-way-topology",
            center.offset(direction),
            "universal",
            frozenset({OPPOSITE[direction]}),
            notes=f"universal {direction} arm endpoint",
        ))

    for index, direction in enumerate(DIRECTIONS):
        position = Position(196 + index * 8, 102, 224)
        result.append(Anchor(
            f"extractor-{direction}",
            "extractor-faces",
            position,
            "item",
            frozenset({direction}),
            extracting=frozenset({direction}),
            supports=(Support(position.offset(direction), "minecraft:barrel"),),
            notes=f"one persisted extractor plate on the {direction} face",
        ))

    multi_position = Position(196, 102, 236)
    multi_directions = frozenset({"up", "north", "east"})
    result.append(Anchor(
        "extractor-multi",
        "multi-extractor",
        multi_position,
        "universal",
        multi_directions,
        extracting=multi_directions,
        supports=tuple(
            Support(multi_position.offset(direction), "minecraft:barrel")
            for direction in sorted(multi_directions)
        ),
        notes="three stable extractor plates compose without transient flow",
    ))

    result.extend((
        Anchor(
            "disconnected-west",
            "persisted-disconnection",
            Position(210, 102, 236),
            "item",
            frozenset(),
            disconnected=frozenset({"east"}),
            notes="adjacent same-type pair with east connection disabled",
        ),
        Anchor(
            "disconnected-east",
            "persisted-disconnection",
            Position(211, 102, 236),
            "item",
            frozenset(),
            disconnected=frozenset({"west"}),
            notes="adjacent same-type pair with west connection disabled",
        ),
    ))

    result.append(Anchor(
        "waterlogged-item",
        "waterlogged",
        Position(224, 102, 236),
        "item",
        frozenset(),
        waterlogged=True,
        notes="BlueMap outer renderer supplies the stock water overlay",
    ))

    if len(result) != 37:
        raise AssertionError(f"gallery has {len(result)} anchors, expected 37")
    positions = [anchor.position for anchor in result]
    if len(positions) != len(set(positions)):
        raise AssertionError("gallery has duplicate anchor positions")
    return result


def build_function(cases: Iterable[Anchor]) -> str:
    lines = [
        "# Generated by gallery/generate.py; do not edit.",
        "function pipez_gallery:clear",
        "fill 190 98 190 246 98 250 minecraft:stone",
        "scoreboard players set #anchors pipez_gallery 0",
    ]
    case_list = list(cases)
    supports: dict[Position, str] = {}
    for anchor in case_list:
        for support in anchor.supports:
            previous = supports.setdefault(support.position, support.block)
            if previous != support.block:
                raise AssertionError(f"conflicting support at {support.position}")
    for position, block in sorted(supports.items()):
        lines.append(f"setblock {position.command()} {block}")

    for anchor in case_list:
        if anchor.waterlogged:
            lines.append(
                f"setblock {anchor.position.command()} minecraft:water"
            )
        lines.append(
            f"setblock {anchor.position.command()} {anchor.block_spec}"
        )
        if anchor.has_data:
            lines.append(
                f"data merge block {anchor.position.command()} {anchor.nbt_snbt}"
            )
        lines.append("scoreboard players add #anchors pipez_gallery 1")

    lines.append("# Reassert exact states after all neighbor updates and NBT writes.")
    for anchor in case_list:
        lines.append(
            f"setblock {anchor.position.command()} {anchor.block_spec}"
        )
        if anchor.has_data:
            lines.append(
                f"data merge block {anchor.position.command()} {anchor.nbt_snbt}"
            )
    lines.extend((
        "function pipez_gallery:verify",
        "tellraw @a [{\"text\":\"Pipez gallery built: \"},{\"score\":{\"name\":\"#anchors\",\"objective\":\"pipez_gallery\"}},{\"text\":\" anchors, \"},{\"score\":{\"name\":\"#failures\",\"objective\":\"pipez_gallery\"}},{\"text\":\" verification failures\"}]",
    ))
    return "\n".join(lines) + "\n"


def verify_function(cases: Iterable[Anchor]) -> str:
    lines = [
        "# Generated by gallery/generate.py; do not edit.",
        "scoreboard players set #failures pipez_gallery 0",
        "scoreboard players set #checked pipez_gallery 0",
    ]
    for anchor in cases:
        position = anchor.position.command()
        lines.append(
            f"execute unless block {position} {anchor.block_spec} run "
            "scoreboard players add #failures pipez_gallery 1"
        )
        if anchor.has_data:
            lines.append(
                f"execute unless data block {position} {{id:\"{anchor.block_id}\"}} run "
                "scoreboard players add #failures pipez_gallery 1"
            )
            lines.append(
                f"execute unless data block {position} {anchor.nbt_snbt} run "
                "scoreboard players add #failures pipez_gallery 1"
            )
        lines.append("scoreboard players add #checked pipez_gallery 1")
    lines.append(
        "execute unless score #checked pipez_gallery matches 37 run "
        "scoreboard players add #failures pipez_gallery 1"
    )
    return "\n".join(lines) + "\n"


def case_tsv(cases: Iterable[Anchor]) -> str:
    header = (
        "anchor_id\tcase_id\tx\ty\tz\tblock_id\tblockstate\t"
        "block_entity_id\textracting\tdisconnected\tnotes"
    )
    rows = [header]
    for anchor in cases:
        rows.append("\t".join((
            anchor.anchor_id,
            anchor.case_id,
            str(anchor.position.x),
            str(anchor.position.y),
            str(anchor.position.z),
            anchor.block_id,
            json.dumps(dict(sorted(anchor.properties.items())), separators=(",", ":")),
            anchor.block_id if anchor.has_data else "",
            ",".join(sorted(anchor.extracting)),
            ",".join(sorted(anchor.disconnected)),
            anchor.notes,
        )))
    return "\n".join(rows) + "\n"


def rendered_files(cases: list[Anchor]) -> dict[Path, bytes]:
    files: dict[Path, bytes] = {
        Path("cases.json"): (
            json.dumps(
                {
                    "schema_version": 1,
                    "baseline": {
                        "pack": "All the Mons 1.2.0",
                        "minecraft": "1.21.1",
                        "pipez": "1.21.1-1.2.31",
                    },
                    "anchor_count": len(cases),
                    "anchors": [anchor.manifest() for anchor in cases],
                },
                indent=2,
                sort_keys=True,
            ) + "\n"
        ).encode("utf-8"),
        Path("cases.tsv"): case_tsv(cases).encode("utf-8"),
        Path("datapack/pack.mcmeta"): (
            json.dumps(
                {
                    "pack": {
                        "description": (
                            "ATM 1.2.0 Pipez stable-optics BlueMap review gallery"
                        ),
                        "pack_format": 48,
                    }
                },
                indent=2,
            ) + "\n"
        ).encode("utf-8"),
        Path("datapack/data/minecraft/tags/function/load.json"): (
            json.dumps({"values": ["pipez_gallery:load"]}, indent=2) + "\n"
        ).encode("utf-8"),
        Path("datapack/data/pipez_gallery/function/load.mcfunction"): (
            "# Generated by gallery/generate.py; do not edit.\n"
            "scoreboard objectives add pipez_gallery dummy\n"
            "forceload add 190 190 246 250\n"
        ).encode("utf-8"),
        Path("datapack/data/pipez_gallery/function/build.mcfunction"):
            build_function(cases).encode("utf-8"),
        Path("datapack/data/pipez_gallery/function/verify.mcfunction"):
            verify_function(cases).encode("utf-8"),
        Path("datapack/data/pipez_gallery/function/clear.mcfunction"): (
            "# Generated by gallery/generate.py; do not edit.\n"
            "fill 190 98 190 246 110 220 minecraft:air\n"
            "fill 190 98 221 246 110 250 minecraft:air\n"
        ).encode("utf-8"),
        Path("datapack/data/pipez_gallery/function/pose.mcfunction"): (
            "# Generated by gallery/generate.py; do not edit.\n"
            "tp @s 218.5 122 182.5 0 28\n"
        ).encode("utf-8"),
        Path("datapack/data/pipez_gallery/function/release.mcfunction"): (
            "# Generated by gallery/generate.py; do not edit.\n"
            "forceload remove 190 190 246 250\n"
        ).encode("utf-8"),
    }
    checksum_lines = [
        f"{hashlib.sha256(content).hexdigest()}  {path.as_posix()}"
        for path, content in sorted(files.items(), key=lambda entry: entry[0].as_posix())
    ]
    files[Path("SHA256SUMS")] = ("\n".join(checksum_lines) + "\n").encode("ascii")
    return files


def write_or_check(files: dict[Path, bytes], check: bool) -> int:
    differences: list[str] = []
    for relative, expected in files.items():
        path = ROOT / relative
        if check:
            actual = path.read_bytes() if path.is_file() else None
            if actual != expected:
                differences.append(relative.as_posix())
        else:
            path.parent.mkdir(parents=True, exist_ok=True)
            path.write_bytes(expected)
    if differences:
        print("generated gallery files differ:", file=sys.stderr)
        for path in differences:
            print(f"  {path}", file=sys.stderr)
        return 1
    return 0


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--check", action="store_true")
    args = parser.parse_args()
    return write_or_check(rendered_files(anchors()), args.check)


if __name__ == "__main__":
    sys.exit(main())
