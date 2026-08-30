/*
 * SPDX-License-Identifier: MIT
 */
package io.github.janguenter.bluemap.pipez.adapter.bluemap523;

import de.bluecolored.bluemap.core.map.TextureGallery;
import de.bluecolored.bluemap.core.map.hires.MaxCapacityReachedException;
import de.bluecolored.bluemap.core.map.hires.RenderSettings;
import de.bluecolored.bluemap.core.map.hires.TileModelView;
import de.bluecolored.bluemap.core.map.hires.block.BlockRenderer;
import de.bluecolored.bluemap.core.map.hires.block.ResourceModelRenderer;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.Variant;
import de.bluecolored.bluemap.core.util.Key;
import de.bluecolored.bluemap.core.util.math.Color;
import de.bluecolored.bluemap.core.world.block.BlockNeighborhood;
import io.github.janguenter.bluemap.pipez.activation.PipezRuntime;
import io.github.janguenter.bluemap.pipez.model.PipeDirection;
import io.github.janguenter.bluemap.pipez.model.PipeSnapshot;
import io.github.janguenter.bluemap.pipez.model.PipeSnapshotDecoder;
import io.github.janguenter.bluemap.pipez.model.PipeType;

import java.util.List;
import java.util.function.Consumer;

/** Stable Pipez renderer: stock core/arms plus persisted extractor plates. */
final class PipezRenderer implements BlockRenderer {

    static final float EXTRACTOR_OFFSET = 0.001F;

    private final ResourcePack resourcePack;
    private final PipezRuntime runtime;
    private final ResourceModelRenderer stock;
    private final JsonModelEmitter json;
    private final PipeSnapshotDecoder decoder = new PipeSnapshotDecoder();
    private final BoundedDiagnostics diagnostics = new BoundedDiagnostics();

    PipezRenderer(
            ResourcePack resourcePack,
            TextureGallery textureGallery,
            RenderSettings renderSettings,
            PipezRuntime runtime
    ) {
        this.resourcePack = resourcePack;
        this.runtime = runtime;
        this.stock = new ResourceModelRenderer(resourcePack, textureGallery, renderSettings);
        this.json = new JsonModelEmitter(resourcePack, textureGallery);
    }

    @Override
    public void render(
            BlockNeighborhood block,
            Variant original,
            TileModelView target,
            Color mapColor
    ) {
        int start = target.getStart();
        Color initialMapColor = new Color().set(mapColor);
        if (!runtime.route().isActive()) {
            renderStock(block, target, mapColor);
            return;
        }

        try {
            PipezBlockEntityData data = block.getBlockEntity()
                    instanceof PipezBlockEntityData found ? found : null;
            PipeSnapshotDecoder.Result decoded = decoder.decode(
                    block.getBlockState().getId().getFormatted(),
                    block.getBlockState().getProperties(),
                    data != null,
                    data == null ? null : data.extractingSides(),
                    data == null ? null : data.disconnectedSides()
            );
            if (!decoded.valid()) {
                diagnostics.report(decoded.reason());
                renderStock(block, target, mapColor);
                return;
            }

            PipeSnapshot snapshot = decoded.snapshot();
            renderStock(block, target, mapColor);
            if (!snapshot.extracting().isEmpty()
                    && !renderExtractors(snapshot, block, target)) {
                runtime.route().fail("extractor-render-failed");
                diagnostics.report("extractor-render-failed");
                resetAndRenderStock(block, target, start, mapColor, initialMapColor);
            }
        } catch (MaxCapacityReachedException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            runtime.route().fail("render-failed");
            diagnostics.report("render-failed");
            resetAndRenderStock(block, target, start, mapColor, initialMapColor);
        }
    }

    private boolean renderExtractors(
            PipeSnapshot snapshot,
            BlockNeighborhood block,
            TileModelView target
    ) {
        Key model = extractorModel(snapshot.type());
        for (PipeDirection direction : PipeDirection.values()) {
            if (!snapshot.extracting().contains(direction)) {
                continue;
            }
            int start = target.getTileModel().size();
            if (!json.emit(
                    model,
                    block,
                    target,
                    direction.extractorXRotation(),
                    direction.extractorYRotation(),
                    0F
            )) {
                return false;
            }
            target.initialize(start).translate(
                    direction.stepX() * EXTRACTOR_OFFSET,
                    direction.stepY() * EXTRACTOR_OFFSET,
                    direction.stepZ() * EXTRACTOR_OFFSET
            );
        }
        return true;
    }

    private void resetAndRenderStock(
            BlockNeighborhood block,
            TileModelView target,
            int start,
            Color mapColor,
            Color initialMapColor
    ) {
        resetForStockFallback(
                target,
                start,
                mapColor,
                initialMapColor,
                () -> renderStock(block, target, mapColor)
        );
    }

    static void resetForStockFallback(
            TileModelView target,
            int start,
            Color mapColor,
            Color initialMapColor,
            Runnable stockRenderer
    ) {
        target.getTileModel().reset(start);
        target.initialize(start);
        mapColor.set(initialMapColor);
        stockRenderer.run();
    }

    private void renderStock(
            BlockNeighborhood block,
            TileModelView target,
            Color mapColor
    ) {
        de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState state =
                resourcePack.getBlockStates().get(block.getBlockState().getId());
        if (state == null) {
            return;
        }
        forEachIsolatedVariant(
                state,
                block.getBlockState(),
                block.getX(),
                block.getY(),
                block.getZ(),
                target,
                variant -> stock.render(block, variant, target, mapColor)
        );
    }

    static void forEachIsolatedVariant(
            de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState state,
            de.bluecolored.bluemap.core.world.BlockState worldState,
            int x,
            int y,
            int z,
            TileModelView target,
            Consumer<Variant> renderer
    ) {
        state.forEach(worldState, x, y, z, variant -> {
            target.initialize();
            renderer.accept(variant);
        });
    }

    static Key extractorModel(PipeType type) {
        return Key.parse(type.extractorModel());
    }

    static List<Float> extractorRotation(PipeDirection direction) {
        return List.of(direction.extractorXRotation(), direction.extractorYRotation(), 0F);
    }
}
