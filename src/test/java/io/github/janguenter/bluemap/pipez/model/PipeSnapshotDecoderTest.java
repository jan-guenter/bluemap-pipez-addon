/*
 * SPDX-License-Identifier: MIT
 */
package io.github.janguenter.bluemap.pipez.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PipeSnapshotDecoderTest {

    private final PipeSnapshotDecoder decoder = new PipeSnapshotDecoder();

    @Test
    void everyTypeAndEveryConnectionMaskDecodeWithoutCapabilityDiscovery() {
        for (PipeType type : PipeType.values()) {
            for (int mask = 0; mask < 64; mask++) {
                Map<String, String> properties = properties(false);
                EnumSet<PipeDirection> expected = EnumSet.noneOf(PipeDirection.class);
                for (PipeDirection direction : PipeDirection.values()) {
                    boolean connected = (mask & (1 << direction.nbtIndex())) != 0;
                    properties.put(direction.property(), Boolean.toString(connected));
                    if (connected) {
                        expected.add(direction);
                    }
                }

                PipeSnapshotDecoder.Result result = decoder.decode(
                        type.blockId(), properties, false, null, null
                );
                assertTrue(result.valid(), type + " mask " + mask);
                assertEquals(expected, result.snapshot().connections());
                assertTrue(result.snapshot().extracting().isEmpty());
            }
        }
    }

    @Test
    void exactByteListOrderAndNonzeroSemanticsArePreserved() {
        Map<String, String> properties = properties(true);
        List<Byte> extracting = List.of((byte) 1, (byte) 0, (byte) -1,
                (byte) 0, (byte) 2, (byte) 0);
        List<Byte> disconnected = List.of((byte) 0, (byte) 1, (byte) 0,
                (byte) 1, (byte) 0, (byte) 1);
        PipeSnapshotDecoder.Result result = decoder.decode(
                PipeType.ITEM.blockId(), properties, true, extracting, disconnected
        );

        assertTrue(result.valid());
        assertEquals(
                EnumSet.of(PipeDirection.DOWN, PipeDirection.NORTH, PipeDirection.WEST),
                result.snapshot().extracting()
        );
        assertEquals(
                EnumSet.of(PipeDirection.UP, PipeDirection.SOUTH, PipeDirection.EAST),
                result.snapshot().disconnected()
        );
    }

    @Test
    void missingOrShortListsMatchPipezAllFalseLoadBehavior() {
        Map<String, String> properties = properties(true);
        for (List<Byte> list : List.of(List.<Byte>of(), List.of((byte) 1))) {
            PipeSnapshotDecoder.Result result = decoder.decode(
                    PipeType.FLUID.blockId(), properties, true, list, list
            );
            assertTrue(result.valid());
            assertTrue(result.snapshot().extracting().isEmpty());
            assertTrue(result.snapshot().disconnected().isEmpty());
        }
        PipeSnapshotDecoder.Result missing = decoder.decode(
                PipeType.FLUID.blockId(), properties, true, null, null
        );
        assertTrue(missing.valid());
        assertTrue(missing.snapshot().extracting().isEmpty());
    }

    @Test
    void extractorIsIndependentFromConnectionBooleanLikeTheExactBer() {
        Map<String, String> properties = properties(true);
        properties.put("north", "false");
        PipeSnapshotDecoder.Result result = decoder.decode(
                PipeType.ENERGY.blockId(),
                properties,
                true,
                List.of((byte) 0, (byte) 0, (byte) 1, (byte) 0, (byte) 0, (byte) 0),
                List.of()
        );
        assertTrue(result.valid());
        assertFalse(result.snapshot().connections().contains(PipeDirection.NORTH));
        assertTrue(result.snapshot().extracting().contains(PipeDirection.NORTH));
    }

    @Test
    void noDataStateNeverTrustsStaleBlockEntityLists() {
        PipeSnapshotDecoder.Result result = decoder.decode(
                PipeType.GAS.blockId(),
                properties(false),
                true,
                List.of((byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 1),
                List.of((byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 1)
        );
        assertTrue(result.valid());
        assertTrue(result.snapshot().extracting().isEmpty());
        assertTrue(result.snapshot().disconnected().isEmpty());
    }

    @Test
    void malformedObservationReturnsWholeBlockFallback() {
        Map<String, String> missingProperty = properties(false);
        missingProperty.remove("east");
        assertEquals(
                "blockstate-property-set",
                decoder.decode(PipeType.ITEM.blockId(), missingProperty, false, null, null)
                        .reason()
        );

        Map<String, String> badValue = properties(false);
        badValue.put("up", "TRUE");
        assertEquals(
                "blockstate-property-value",
                decoder.decode(PipeType.ITEM.blockId(), badValue, false, null, null)
                        .reason()
        );
        assertEquals(
                "block-entity-missing",
                decoder.decode(PipeType.ITEM.blockId(), properties(true), false, null, null)
                        .reason()
        );

        List<Byte> malformed = new ArrayList<>(
                List.of((byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0)
        );
        malformed.set(2, null);
        assertEquals(
                "block-entity-byte-list",
                decoder.decode(
                        PipeType.ITEM.blockId(), properties(true), true, malformed, List.of()
                ).reason()
        );
        assertEquals(
                "unknown-block-id",
                decoder.decode("pipez:future_pipe", properties(false), false, null, null)
                        .reason()
        );
    }

    @Test
    void directionTablesMatchExactMultipartAndBlueMapEquivalentRotations() {
        assertDirection(PipeDirection.DOWN, 0, 90F, 0F, 90F, 0F);
        assertDirection(PipeDirection.UP, 1, 270F, 0F, 270F, 0F);
        assertDirection(PipeDirection.NORTH, 2, 0F, 0F, 0F, 0F);
        assertDirection(PipeDirection.SOUTH, 3, 0F, 180F, 0F, 180F);
        assertDirection(PipeDirection.WEST, 4, 0F, 270F, 0F, 270F);
        assertDirection(PipeDirection.EAST, 5, 0F, 90F, 0F, 90F);
    }

    private static Map<String, String> properties(boolean hasData) {
        Map<String, String> result = new HashMap<>();
        for (PipeDirection direction : PipeDirection.values()) {
            result.put(direction.property(), "false");
        }
        result.put("has_data", Boolean.toString(hasData));
        result.put("waterlogged", "false");
        return result;
    }

    private static void assertDirection(
            PipeDirection direction,
            int index,
            float armX,
            float armY,
            float extractorX,
            float extractorY
    ) {
        assertEquals(index, direction.nbtIndex());
        assertEquals(armX, direction.armXRotation());
        assertEquals(armY, direction.armYRotation());
        assertEquals(extractorX, direction.extractorXRotation());
        assertEquals(extractorY, direction.extractorYRotation());
    }
}
