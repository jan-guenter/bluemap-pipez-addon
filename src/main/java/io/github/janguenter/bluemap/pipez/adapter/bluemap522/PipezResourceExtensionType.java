/*
 * SPDX-License-Identifier: MIT
 */
package io.github.janguenter.bluemap.pipez.adapter.bluemap522;

import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.util.Key;
import io.github.janguenter.bluemap.pipez.activation.PipezRuntime;

/** Resource-pack extension factory registered before resource loading begins. */
final class PipezResourceExtensionType
        implements ResourcePack.Extension<PipezResourceExtension> {

    static final Key KEY = Key.parse("bluemap_pipez:exact_profile");

    private final PipezRuntime runtime;

    PipezResourceExtensionType(PipezRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public Key getKey() {
        return KEY;
    }

    @Override
    public PipezResourceExtension create(ResourcePack pack) {
        return new PipezResourceExtension(pack, runtime);
    }
}
