/*
 * SPDX-License-Identifier: MIT
 */
package io.github.janguenter.bluemap.pipez.adapter.bluemap523;

import de.bluecolored.bluemap.core.world.mca.blockentity.MCABlockEntity;
import de.bluecolored.bluenbt.NBTName;

import java.util.List;

/** BlueNBT projection of the two stable six-byte Pipez side lists. */
public final class PipezBlockEntityData extends MCABlockEntity {

    @NBTName("ExtractingSides")
    private List<Byte> extractingSides;

    @NBTName("DisconnectedSides")
    private List<Byte> disconnectedSides;

    public PipezBlockEntityData() {
    }

    List<Byte> extractingSides() {
        return extractingSides;
    }

    List<Byte> disconnectedSides() {
        return disconnectedSides;
    }
}
