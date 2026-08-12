/*
 * SPDX-License-Identifier: MIT
 */
package io.github.janguenter.bluemap.pipez.model;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/** Strict blockstate decoder with the exact 1.2.31 six-byte-list semantics. */
public final class PipeSnapshotDecoder {

    private static final Set<String> EXPECTED_PROPERTIES = Set.of(
            "down", "up", "north", "south", "west", "east",
            "has_data", "waterlogged"
    );

    public Result decode(
            String blockId,
            Map<String, String> properties,
            boolean blockEntityPresent,
            List<Byte> extractingSides,
            List<Byte> disconnectedSides
    ) {
        Objects.requireNonNull(blockId, "blockId");
        Objects.requireNonNull(properties, "properties");

        PipeType type = PipeType.fromBlockId(blockId).orElse(null);
        if (type == null) {
            return Result.invalid("unknown-block-id");
        }
        if (!EXPECTED_PROPERTIES.equals(properties.keySet())) {
            return Result.invalid("blockstate-property-set");
        }

        EnumSet<PipeDirection> connections = EnumSet.noneOf(PipeDirection.class);
        for (PipeDirection direction : PipeDirection.values()) {
            Boolean connected = strictBoolean(properties.get(direction.property()));
            if (connected == null) {
                return Result.invalid("blockstate-property-value");
            }
            if (connected) {
                connections.add(direction);
            }
        }

        Boolean hasData = strictBoolean(properties.get("has_data"));
        Boolean waterlogged = strictBoolean(properties.get("waterlogged"));
        if (hasData == null || waterlogged == null) {
            return Result.invalid("blockstate-property-value");
        }
        if (hasData && !blockEntityPresent) {
            return Result.invalid("block-entity-missing");
        }

        SideResult extracting = hasData
                ? decodeByteList(extractingSides)
                : SideResult.empty();
        SideResult disconnected = hasData
                ? decodeByteList(disconnectedSides)
                : SideResult.empty();
        if (!extracting.valid() || !disconnected.valid()) {
            return Result.invalid("block-entity-byte-list");
        }

        return Result.valid(new PipeSnapshot(
                type,
                connections,
                extracting.sides(),
                disconnected.sides(),
                hasData,
                waterlogged
        ));
    }

    private static SideResult decodeByteList(List<Byte> values) {
        if (values == null || values.size() < PipeDirection.values().length) {
            return SideResult.empty();
        }
        EnumSet<PipeDirection> result = EnumSet.noneOf(PipeDirection.class);
        for (PipeDirection direction : PipeDirection.values()) {
            Byte value = values.get(direction.nbtIndex());
            if (value == null) {
                return SideResult.invalid();
            }
            if (value != 0) {
                result.add(direction);
            }
        }
        return new SideResult(true, result);
    }

    private static Boolean strictBoolean(String value) {
        if ("true".equals(value)) {
            return Boolean.TRUE;
        }
        if ("false".equals(value)) {
            return Boolean.FALSE;
        }
        return null;
    }

    private record SideResult(boolean valid, Set<PipeDirection> sides) {

        private static SideResult empty() {
            return new SideResult(true, Set.of());
        }

        private static SideResult invalid() {
            return new SideResult(false, Set.of());
        }
    }

    public record Result(PipeSnapshot snapshot, String reason) {

        public Result {
            if ((snapshot == null) == (reason == null)) {
                throw new IllegalArgumentException("exactly one result branch must be present");
            }
        }

        public static Result valid(PipeSnapshot snapshot) {
            return new Result(Objects.requireNonNull(snapshot, "snapshot"), null);
        }

        public static Result invalid(String reason) {
            return new Result(null, Objects.requireNonNull(reason, "reason"));
        }

        public boolean valid() {
            return snapshot != null;
        }
    }
}
