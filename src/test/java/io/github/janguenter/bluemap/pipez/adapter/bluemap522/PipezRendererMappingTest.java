/*
 * SPDX-License-Identifier: MIT
 */
package io.github.janguenter.bluemap.pipez.adapter.bluemap522;

import de.bluecolored.bluemap.core.map.hires.ArrayTileModel;
import de.bluecolored.bluemap.core.map.hires.TileModelView;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.Multipart;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.Variant;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.VariantSet;
import de.bluecolored.bluemap.core.util.Key;
import de.bluecolored.bluemap.core.util.math.Color;
import de.bluecolored.bluemap.core.util.math.VectorM3f;
import io.github.janguenter.bluemap.pipez.model.PipeDirection;
import io.github.janguenter.bluemap.pipez.model.PipeType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PipezRendererMappingTest {

    @Test
    void everyTypeUsesItsOperatorInstalledExtractorModel() {
        for (PipeType type : PipeType.values()) {
            assertEquals(
                    "pipez:block/" + type.stem() + "_pipe_extract",
                    PipezRenderer.extractorModel(type).getFormatted()
            );
        }
    }

    @Test
    void everyExtractorFaceUsesTheBlueMapEquivalentOfTheExactClientRotation() {
        assertEquals(List.of(90F, 0F, 0F),
                PipezRenderer.extractorRotation(PipeDirection.DOWN));
        assertEquals(List.of(270F, 0F, 0F),
                PipezRenderer.extractorRotation(PipeDirection.UP));
        assertEquals(List.of(0F, 0F, 0F),
                PipezRenderer.extractorRotation(PipeDirection.NORTH));
        assertEquals(List.of(0F, 180F, 0F),
                PipezRenderer.extractorRotation(PipeDirection.SOUTH));
        assertEquals(List.of(0F, 270F, 0F),
                PipezRenderer.extractorRotation(PipeDirection.WEST));
        assertEquals(List.of(0F, 90F, 0F),
                PipezRenderer.extractorRotation(PipeDirection.EAST));
        assertEquals(0.001F, PipezRenderer.EXTRACTOR_OFFSET);
    }

    @Test
    void everyNativeNorthExtractorModelLandsOnItsRequestedWorldFace() {
        for (PipeDirection direction : PipeDirection.values()) {
            Variant transform = new Variant(
                    ResourcePack.MISSING_BLOCK_MODEL,
                    direction.extractorXRotation(),
                    direction.extractorYRotation(),
                    0F
            );
            VectorM3f center = new VectorM3f(0.5F, 0.5F, 0F)
                    .transform(transform.getTransformMatrix());

            assertEquals(0.5F + 0.5F * direction.stepX(), center.x, 0.00001F,
                    direction.property());
            assertEquals(0.5F + 0.5F * direction.stepY(), center.y, 0.00001F,
                    direction.property());
            assertEquals(0.5F + 0.5F * direction.stepZ(), center.z, 0.00001F,
                    direction.property());
        }
    }

    @Test
    void multipartVariantsEachReceiveAnIsolatedTailView() {
        Variant core = new Variant(ResourcePack.MISSING_BLOCK_MODEL);
        Variant north = new Variant(ResourcePack.MISSING_BLOCK_MODEL, 0F, 0F, 0F);
        Variant south = new Variant(ResourcePack.MISSING_BLOCK_MODEL, 0F, 180F, 0F);
        var resourceState = new de.bluecolored.bluemap.core.resources.pack.resourcepack
                .blockstate.BlockState(new Multipart(new VariantSet[]{
                        new VariantSet(core),
                        new VariantSet(north),
                        new VariantSet(south)
                }));
        var worldState = new de.bluecolored.bluemap.core.world.BlockState(
                Key.parse("pipez:item_pipe")
        );
        ArrayTileModel model = new ArrayTileModel(8);
        TileModelView target = new TileModelView(model);
        target.add(1);
        List<Integer> starts = new ArrayList<>();

        PipezRenderer.forEachIsolatedVariant(
                resourceState,
                worldState,
                10,
                20,
                30,
                target,
                ignored -> {
                    starts.add(target.getStart());
                    target.add(1);
                }
        );

        assertEquals(List.of(1, 2, 3), starts);
        assertEquals(4, model.size());
    }

    @Test
    void failedCustomGeometryIsRemovedBeforeStockFallbackRuns() {
        ArrayTileModel model = new ArrayTileModel(8);
        TileModelView target = new TileModelView(model);
        target.add(1);
        int stockStart = model.size();
        target.initialize();
        target.add(2);
        Color initial = new Color().set(0.2F, 0.3F, 0.4F, 0.5F, false);
        Color actual = new Color().set(1F, 0F, 0F, 1F, false);
        boolean[] stockRendered = {false};

        PipezRenderer.resetForStockFallback(
                target,
                stockStart,
                actual,
                initial,
                () -> {
                    assertEquals(stockStart, model.size());
                    assertEquals(stockStart, target.getStart());
                    assertEquals(0, target.getSize());
                    assertEquals(initial.getInt(), actual.getInt());
                    target.add(1);
                    stockRendered[0] = true;
                }
        );

        assertTrue(stockRendered[0]);
        assertEquals(stockStart + 1, model.size());
        assertEquals(initial.getInt(), actual.getInt());
    }
}
