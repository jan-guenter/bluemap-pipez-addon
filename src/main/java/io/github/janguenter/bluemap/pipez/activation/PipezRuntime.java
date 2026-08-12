/*
 * SPDX-License-Identifier: MIT
 */
package io.github.janguenter.bluemap.pipez.activation;

/** Shared activation state for the single exact Pipez route. */
public final class PipezRuntime {

    public static final String PIPEZ = "pipez";
    public static final PipezRuntime INSTANCE = new PipezRuntime();

    private final RouteActivation route = new RouteActivation(PIPEZ);

    private PipezRuntime() {
    }

    public RouteActivation route() {
        return route;
    }

    public void disable(String detail) {
        route.fail(detail);
    }
}
