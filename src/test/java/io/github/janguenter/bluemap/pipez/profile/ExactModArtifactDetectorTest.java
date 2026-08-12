/*
 * SPDX-License-Identifier: MIT
 */
package io.github.janguenter.bluemap.pipez.profile;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExactModArtifactDetectorTest {

    @Test
    void exactCiInputActivatesAndNearbyIdentitiesDoNot() {
        String configured = System.getProperty("pipezJar");
        if (configured == null || !Files.isRegularFile(Path.of(configured))) {
            return;
        }
        Path exact = Path.of(configured);
        assertTrue(ExactModArtifactDetector.matches(
                List.of(exact),
                Pipez1211231Profile.JAR_SHA256,
                Pipez1211231Profile.JAR_SIZE
        ));
        assertFalse(ExactModArtifactDetector.matches(
                List.of(exact),
                Pipez1211231Profile.JAR_SHA256,
                Pipez1211231Profile.JAR_SIZE + 1
        ));
        assertThrows(IllegalArgumentException.class, () -> ExactModArtifactDetector.matches(
                List.of(exact),
                "0".repeat(64),
                Pipez1211231Profile.JAR_SIZE
        ));
    }
}
