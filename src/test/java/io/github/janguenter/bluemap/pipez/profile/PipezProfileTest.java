/*
 * SPDX-License-Identifier: MIT
 */
package io.github.janguenter.bluemap.pipez.profile;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PipezProfileTest {

    @Test
    void exactIdentityAndResourceSetsAreClosed() {
        assertEquals(456_599L, Pipez1211231Profile.JAR_SIZE);
        assertEquals(5, Pipez1211231Profile.ROUTED_BLOCKS.size());
        assertEquals(5, Pipez1211231Profile.BLOCK_ENTITY_IDS.size());
        assertEquals(18, Pipez1211231Profile.REQUIRED_MODELS.size());
        assertEquals(5, Pipez1211231Profile.REQUIRED_TEXTURES.size());
        assertTrue(Pipez1211231Profile.acceptsArtifact(
                Pipez1211231Profile.JAR_SIZE,
                Pipez1211231Profile.JAR_SHA256
        ));
        assertFalse(Pipez1211231Profile.acceptsArtifact(
                Pipez1211231Profile.JAR_SIZE + 1,
                Pipez1211231Profile.JAR_SHA256
        ));
    }
}
