/*
 * SPDX-License-Identifier: MIT
 */
package io.github.janguenter.bluemap.pipez.adapter.bluemap523;

import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.Variant;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.VariantSet;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.Variants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdapterBoundaryTest {

    @Test
    void sharedDispatchAcceptsOnlyTheExactLocalFixtureShape() {
        Variant variant = new Variant(ResourcePack.MISSING_BLOCK_MODEL);
        BlockState wrongRenderer = new BlockState(new Variants(
                new VariantSet[0], new VariantSet(variant)
        ));
        assertFalse(BlueMap523Adapter.isExpectedDispatch(wrongRenderer));

        variant.setRenderer(BlueMap523Adapter.renderer());
        BlockState exact = new BlockState(new Variants(
                new VariantSet[0], new VariantSet(variant)
        ));
        assertTrue(BlueMap523Adapter.isExpectedDispatch(exact));
    }
}
