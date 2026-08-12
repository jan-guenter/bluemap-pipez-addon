/*
 * SPDX-License-Identifier: MIT
 */
package io.github.janguenter.bluemap.pipez.activation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RouteActivationTest {

    @Test
    void failureIsTerminalAndFailClosed() {
        RouteActivation route = new RouteActivation("pipez");
        assertFalse(route.isActive());
        route.activate();
        assertTrue(route.isActive());
        route.fail("render-failed");
        assertEquals(RouteActivation.State.FAILED, route.snapshot().state());
        route.activate();
        route.inactive("operator-disabled");
        assertEquals(RouteActivation.State.FAILED, route.snapshot().state());
        assertEquals("render-failed", route.snapshot().detail());
    }
}
