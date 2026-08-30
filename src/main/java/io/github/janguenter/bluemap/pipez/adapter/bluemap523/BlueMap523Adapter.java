/*
 * SPDX-License-Identifier: MIT
 */
package io.github.janguenter.bluemap.pipez.adapter.bluemap523;

import de.bluecolored.bluemap.core.map.hires.block.BlockRendererType;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState;
import de.bluecolored.bluemap.core.world.mca.blockentity.BlockEntityType;
import io.github.janguenter.bluemap.addon.adapter.api.bluemap523.RegistryGuard;
import io.github.janguenter.bluemap.addon.adapter.api.bluemap523.ResourceExtensionType;
import io.github.janguenter.bluemap.addon.adapter.api.bluemap523.SyntheticDispatch;
import io.github.janguenter.bluemap.pipez.activation.PipezRuntime;
import io.github.janguenter.bluemap.pipez.profile.Pipez1211231Profile;

import java.util.ArrayList;
import java.util.List;

/** Exact BlueMap 5.23 feature-backport internal ABI boundary. */
public final class BlueMap523Adapter {

    private static final PipezRuntime RUNTIME = PipezRuntime.INSTANCE;
    private static final de.bluecolored.bluemap.core.util.Key RENDERER_KEY =
            de.bluecolored.bluemap.core.util.Key.parse("bluemap_pipez:pipe_shape");
    private static final BlockRendererType RENDERER = new BlockRendererType.Impl(
            RENDERER_KEY,
            (pack, gallery, settings) -> new PipezRenderer(pack, gallery, settings, RUNTIME)
    );
    private static final de.bluecolored.bluemap.core.util.Key EXTENSION_KEY =
            de.bluecolored.bluemap.core.util.Key.parse("bluemap_pipez:exact_profile");
    private static final ResourcePack.Extension<PipezResourceExtension> EXTENSION =
            new ResourceExtensionType<>(
                    EXTENSION_KEY,
                    pack -> new PipezResourceExtension(pack, RUNTIME)
            );

    private BlueMap523Adapter() {
    }

    public static synchronized boolean install() {
        List<BlockEntityType> entities = new ArrayList<>();
        Pipez1211231Profile.BLOCK_ENTITY_IDS.forEach(id -> entities.add(
                new BlockEntityType.Impl(
                        de.bluecolored.bluemap.core.util.Key.parse(id),
                        PipezBlockEntityData.class
                )
        ));

        if (!RegistryGuard.canRegister(BlockRendererType.REGISTRY, RENDERER)
                || !RegistryGuard.canRegister(ResourcePack.Extension.REGISTRY, EXTENSION)
                || entities.stream().anyMatch(
                        type -> !RegistryGuard.canRegister(BlockEntityType.REGISTRY, type)
                )) {
            RUNTIME.disable("registry-collision");
            return false;
        }
        if (!RegistryGuard.register(BlockRendererType.REGISTRY, RENDERER)
                || !RegistryGuard.register(ResourcePack.Extension.REGISTRY, EXTENSION)) {
            RUNTIME.disable("registry-collision");
            return false;
        }
        for (BlockEntityType entity : entities) {
            if (!RegistryGuard.register(BlockEntityType.REGISTRY, entity)) {
                RUNTIME.disable("block-entity-registry-collision");
                return false;
            }
        }
        return true;
    }

    static boolean isExpectedDispatch(BlockState state) {
        return SyntheticDispatch.matches(state, RENDERER);
    }

    static BlockRendererType renderer() {
        return RENDERER;
    }
}
