/*
 * SPDX-License-Identifier: MIT
 */
package io.github.janguenter.bluemap.pipez.profile;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProfileDisablementTest {

    @Test
    void propertyAndEnvironmentValuesMergeCanonically() {
        ProfileDisablement disabled = ProfileDisablement.from(
                " Pipez,INVALID VALUE,pipez ",
                "future,"
        );
        assertEquals(Set.of("pipez", "future"), disabled.disabledProfiles());
        assertTrue(disabled.isDisabled("PIPEZ"));
        assertFalse(disabled.isDisabled("missing"));
    }
}
