/*
 * SPDX-License-Identifier: MIT
 */
package io.github.janguenter.bluemap.pipez.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PipeTypeTest {

    @Test
    void catalogContainsOnlyTheFiveInstalledWorldBlocks() {
        Set<String> ids = Arrays.stream(PipeType.values())
                .map(PipeType::blockId)
                .collect(Collectors.toSet());
        assertEquals(Set.of(
                "pipez:item_pipe",
                "pipez:fluid_pipe",
                "pipez:energy_pipe",
                "pipez:universal_pipe",
                "pipez:gas_pipe"
        ), ids);
        for (PipeType type : PipeType.values()) {
            assertEquals(type.blockId(), type.blockEntityId());
            assertTrue(type.extractorModel().endsWith("_pipe_extract"));
        }
    }
}
