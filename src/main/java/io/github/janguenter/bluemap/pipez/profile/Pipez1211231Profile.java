/*
 * SPDX-License-Identifier: MIT
 */
package io.github.janguenter.bluemap.pipez.profile;

import de.bluecolored.bluemap.core.util.Key;
import io.github.janguenter.bluemap.pipez.model.PipeType;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/** Exact All the Mons 1.2.0 profile for Pipez 1.21.1-1.2.31. */
public final class Pipez1211231Profile {

    public static final String PROFILE_ID = "pipez";
    public static final String MOD_ID = "pipez";
    public static final String VERSION = "1.21.1-1.2.31";
    public static final String ARTIFACT = "pipez-neoforge-1.21.1-1.2.31.jar";
    public static final long JAR_SIZE = 456_599L;
    public static final String JAR_SHA1 = "a5671f7e8d38dfc092ace4091250e8f9e1245e1e";
    public static final String JAR_SHA256 =
            "9b37e922443ea3452daeacbfba4bcf69de07692183c4ee09f1d1e82c9fc5cc5f";
    public static final String JAR_SHA512 =
            "7291230b62104b73b04564d6d39ba18d11134a5713ee79242c7ae8885d07c4ef"
                    + "b5f5d0ab75b8ff9aba3119ed8163d0c5488ad603a2db21b92dc35715b723ce5e";
    public static final int CURSEFORGE_PROJECT_ID = 443_900;
    public static final int CURSEFORGE_FILE_ID = 8_351_631;
    public static final long CURSEFORGE_FINGERPRINT = 411_466_094L;
    public static final String MODRINTH_PROJECT_ID = "iRmWy6ga";
    public static final String MODRINTH_VERSION_ID = "BPGKb8pi";
    public static final String SOURCE_COMMIT =
            "91a01deda19e5beb19fe7840aa14daaa312aa212";

    public static final Set<String> ROUTED_BLOCKS = Arrays.stream(PipeType.values())
            .map(PipeType::blockId)
            .collect(Collectors.toUnmodifiableSet());
    public static final Set<String> BLOCK_ENTITY_IDS = Arrays.stream(PipeType.values())
            .map(PipeType::blockEntityId)
            .collect(Collectors.toUnmodifiableSet());
    public static final Set<Key> REQUIRED_MODELS = Set.of(
            Key.parse("pipez:block/pipe_core"),
            Key.parse("pipez:block/pipe_part"),
            Key.parse("pipez:block/pipe_extract"),
            Key.parse("pipez:block/item_pipe_core"),
            Key.parse("pipez:block/item_pipe_part"),
            Key.parse("pipez:block/item_pipe_extract"),
            Key.parse("pipez:block/fluid_pipe_core"),
            Key.parse("pipez:block/fluid_pipe_part"),
            Key.parse("pipez:block/fluid_pipe_extract"),
            Key.parse("pipez:block/energy_pipe_core"),
            Key.parse("pipez:block/energy_pipe_part"),
            Key.parse("pipez:block/energy_pipe_extract"),
            Key.parse("pipez:block/universal_pipe_core"),
            Key.parse("pipez:block/universal_pipe_part"),
            Key.parse("pipez:block/universal_pipe_extract"),
            Key.parse("pipez:block/gas_pipe_core"),
            Key.parse("pipez:block/gas_pipe_part"),
            Key.parse("pipez:block/gas_pipe_extract")
    );
    public static final Set<Key> REQUIRED_TEXTURES = Arrays.stream(PipeType.values())
            .map(PipeType::texture)
            .map(Key::parse)
            .collect(Collectors.toUnmodifiableSet());

    private Pipez1211231Profile() {
    }

    public static boolean acceptsArtifact(long size, String sha256) {
        return size == JAR_SIZE && JAR_SHA256.equals(sha256);
    }
}
