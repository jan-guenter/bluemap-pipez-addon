/*
 * SPDX-License-Identifier: MIT
 */
package io.github.janguenter.bluemap.pipez.model;

/** Exact Pipez 1.2.31 direction order and model rotations. */
public enum PipeDirection {
    DOWN("down", 0, 90F, 0F, 270F, 0F, 0, -1, 0),
    UP("up", 1, 270F, 0F, 90F, 0F, 0, 1, 0),
    NORTH("north", 2, 0F, 0F, 0F, 0F, 0, 0, -1),
    SOUTH("south", 3, 0F, 180F, 0F, 180F, 0, 0, 1),
    WEST("west", 4, 0F, 270F, 0F, 90F, -1, 0, 0),
    EAST("east", 5, 0F, 90F, 0F, 270F, 1, 0, 0);

    private final String property;
    private final int nbtIndex;
    private final float armXRotation;
    private final float armYRotation;
    private final float extractorXRotation;
    private final float extractorYRotation;
    private final int stepX;
    private final int stepY;
    private final int stepZ;

    PipeDirection(
            String property,
            int nbtIndex,
            float armXRotation,
            float armYRotation,
            float extractorXRotation,
            float extractorYRotation,
            int stepX,
            int stepY,
            int stepZ
    ) {
        this.property = property;
        this.nbtIndex = nbtIndex;
        this.armXRotation = armXRotation;
        this.armYRotation = armYRotation;
        this.extractorXRotation = extractorXRotation;
        this.extractorYRotation = extractorYRotation;
        this.stepX = stepX;
        this.stepY = stepY;
        this.stepZ = stepZ;
    }

    public String property() {
        return property;
    }

    public int nbtIndex() {
        return nbtIndex;
    }

    public float armXRotation() {
        return armXRotation;
    }

    public float armYRotation() {
        return armYRotation;
    }

    public float extractorXRotation() {
        return extractorXRotation;
    }

    public float extractorYRotation() {
        return extractorYRotation;
    }

    public int stepX() {
        return stepX;
    }

    public int stepY() {
        return stepY;
    }

    public int stepZ() {
        return stepZ;
    }
}
