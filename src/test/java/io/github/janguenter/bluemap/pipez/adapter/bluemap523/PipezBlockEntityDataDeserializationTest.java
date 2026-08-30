/*
 * SPDX-License-Identifier: MIT
 */
package io.github.janguenter.bluemap.pipez.adapter.bluemap523;

import de.bluecolored.bluemap.core.world.mca.MCAUtil;
import de.bluecolored.bluenbt.BlueNBT;
import de.bluecolored.bluenbt.NBTWriter;
import de.bluecolored.bluenbt.TagType;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PipezBlockEntityDataDeserializationTest {

    @Test
    void exactUppercaseByteListsSurviveBlueMapNamingAndGenericDecoding()
            throws Exception {
        PipezBlockEntityData data = read(blockEntity(writer -> {
            byteList(writer, "ExtractingSides", (byte) 1, (byte) 0, (byte) 1,
                    (byte) 0, (byte) 1, (byte) 0);
            byteList(writer, "DisconnectedSides", (byte) 0, (byte) 1, (byte) 0,
                    (byte) 1, (byte) 0, (byte) 1);
        }));

        assertEquals(
                List.of((byte) 1, (byte) 0, (byte) 1, (byte) 0, (byte) 1, (byte) 0),
                data.extractingSides()
        );
        assertEquals(
                List.of((byte) 0, (byte) 1, (byte) 0, (byte) 1, (byte) 0, (byte) 1),
                data.disconnectedSides()
        );
    }

    @Test
    void absentListsRemainDistinguishableForExactAllFalseNormalization()
            throws Exception {
        PipezBlockEntityData data = read(blockEntity(writer -> {
        }));
        assertNull(data.extractingSides());
        assertNull(data.disconnectedSides());
    }

    private static PipezBlockEntityData read(byte[] nbt) throws IOException {
        return MCAUtil.addCommonNbtSettings(new BlueNBT()).read(
                new ByteArrayInputStream(nbt),
                PipezBlockEntityData.class
        );
    }

    private static byte[] blockEntity(WriterAction body) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (NBTWriter writer = new NBTWriter(bytes)) {
            writer.beginCompound();
            writer.name("id").value("pipez:item_pipe");
            writer.name("x").value(220);
            writer.name("y").value(100);
            writer.name("z").value(196);
            body.write(writer);
            writer.endCompound();
        }
        return bytes.toByteArray();
    }

    private static void byteList(NBTWriter writer, String name, byte... values)
            throws IOException {
        writer.name(name).beginList(values.length, TagType.BYTE);
        for (byte value : values) {
            writer.value(value);
        }
        writer.endList();
    }

    @FunctionalInterface
    private interface WriterAction {
        void write(NBTWriter writer) throws IOException;
    }
}
