/*
 * SPDX-License-Identifier: MIT
 */
package io.github.janguenter.bluemap.pipez.model;

import java.util.Objects;
import java.util.Set;

/** Immutable stable visual projection for one exact Pipez world block. */
public record PipeSnapshot(
        PipeType type,
        Set<PipeDirection> connections,
        Set<PipeDirection> extracting,
        Set<PipeDirection> disconnected,
        boolean hasData,
        boolean waterlogged
) {

    public PipeSnapshot {
        Objects.requireNonNull(type, "type");
        connections = Set.copyOf(connections);
        extracting = Set.copyOf(extracting);
        disconnected = Set.copyOf(disconnected);
    }
}
