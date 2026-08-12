/*
 * SPDX-License-Identifier: MIT
 */
package io.github.janguenter.bluemap.pipez.adapter.bluemap522;

import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePackExtension;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.Variant;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.VariantSet;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.Variants;
import de.bluecolored.bluemap.core.util.Key;
import de.bluecolored.bluemap.core.world.BlockProperties;
import de.bluecolored.bluemap.core.world.BlockState;
import io.github.janguenter.bluemap.pipez.activation.PipezRuntime;
import io.github.janguenter.bluemap.pipez.profile.ExactModArtifactDetector;
import io.github.janguenter.bluemap.pipez.profile.Pipez1211231Profile;
import io.github.janguenter.bluemap.pipez.profile.ProfileDisablement;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

/** Exact artifact activation and renderer routing for the Pipez profile. */
final class PipezResourceExtension implements ResourcePackExtension {

    private static final Key SYNTHETIC = Key.parse("bluemap_pipez:pipe_shape");

    private final ResourcePack resourcePack;
    private final PipezRuntime runtime;

    PipezResourceExtension(ResourcePack resourcePack, PipezRuntime runtime) {
        this.resourcePack = resourcePack;
        this.runtime = runtime;
    }

    @Override
    public void loadResources(Iterable<Path> roots) throws IOException {
        ProfileDisablement disabled = ProfileDisablement.current();
        if (disabled.isDisabled(Pipez1211231Profile.PROFILE_ID)) {
            runtime.route().inactive("operator-disabled");
            return;
        }
        if (!ExactModArtifactDetector.matches(
                roots,
                Pipez1211231Profile.JAR_SHA256,
                Pipez1211231Profile.JAR_SIZE
        )) {
            runtime.route().inactive("exact-artifact-missing");
            return;
        }
        runtime.route().activate();

        de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState dispatch =
                resourcePack.getBlockStates().get(SYNTHETIC);
        if (!validDispatch(dispatch)) {
            runtime.route().inactive("synthetic-dispatch-invalid");
        }
    }

    @Override
    public Set<Key> collectUsedTextureKeys() {
        return runtime.route().isActive()
                ? Pipez1211231Profile.REQUIRED_TEXTURES
                : Set.of();
    }

    @Override
    public void bake() {
        if (!runtime.route().isActive()) {
            return;
        }
        for (String blockId : Pipez1211231Profile.ROUTED_BLOCKS) {
            de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState state =
                    resourcePack.getBlockStates().get(Key.parse(blockId));
            if (state == null || state.getMultipart() == null) {
                runtime.route().inactive("required-blockstate-missing");
                return;
            }
        }
        for (Key model : Pipez1211231Profile.REQUIRED_MODELS) {
            if (resourcePack.getModels().get(model) == null) {
                runtime.route().inactive("required-model-missing");
                return;
            }
        }
        for (Key texture : Pipez1211231Profile.REQUIRED_TEXTURES) {
            if (resourcePack.getTextures().get(texture) == null) {
                runtime.route().inactive("required-texture-missing");
                return;
            }
        }
    }

    @Override
    public Key getBlockStateKey(Key key) {
        return runtime.route().isActive()
                && Pipez1211231Profile.ROUTED_BLOCKS.contains(key.getFormatted())
                ? SYNTHETIC
                : key;
    }

    @Override
    public void getBlockProperties(BlockState blockState, BlockProperties.Builder builder) {
        if (runtime.route().isActive()
                && Pipez1211231Profile.ROUTED_BLOCKS.contains(
                        blockState.getId().getFormatted()
                )) {
            builder.culling(false).occluding(false).cullingIdentical(false);
        }
    }

    private static boolean validDispatch(
            de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState state
    ) {
        if (state == null || state.getMultipart() != null) {
            return false;
        }
        Variants variants = state.getVariants();
        if (variants == null || variants.getDefaultVariant() == null) {
            return false;
        }
        VariantSet set = variants.getDefaultVariant();
        if (set.getVariants().length != 1) {
            return false;
        }
        Variant variant = set.getVariants()[0];
        return BlueMap522Adapter.isExpectedDispatch(variant);
    }
}
