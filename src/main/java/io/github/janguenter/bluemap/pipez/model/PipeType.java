/*
 * SPDX-License-Identifier: MIT
 */
package io.github.janguenter.bluemap.pipez.model;

import java.util.Arrays;
import java.util.Optional;

/** Closed five-block catalog installed by exact Pipez 1.2.31. */
public enum PipeType {
    ITEM("item"),
    FLUID("fluid"),
    ENERGY("energy"),
    UNIVERSAL("universal"),
    GAS("gas");

    private final String stem;

    PipeType(String stem) {
        this.stem = stem;
    }

    public String stem() {
        return stem;
    }

    public String blockId() {
        return "pipez:" + stem + "_pipe";
    }

    public String blockEntityId() {
        return blockId();
    }

    public String coreModel() {
        return "pipez:block/" + stem + "_pipe_core";
    }

    public String armModel() {
        return "pipez:block/" + stem + "_pipe_part";
    }

    public String extractorModel() {
        return "pipez:block/" + stem + "_pipe_extract";
    }

    public String texture() {
        return "pipez:block/" + stem + "_pipe";
    }

    public static Optional<PipeType> fromBlockId(String blockId) {
        return Arrays.stream(values()).filter(type -> type.blockId().equals(blockId)).findFirst();
    }
}
