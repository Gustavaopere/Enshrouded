package com.gustavaopere.enshrouded.exposure.deadly;

import com.gustavaopere.enshrouded.config.EnshroudedConfig;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DeadlyRuntimeIntegrationRedTest {
    @Test
    void configExposesRequiredPassageLevel() throws Exception {
        EnshroudedConfig.class.getMethod("deadlyRequiredPassageLevel");
    }

    @Test
    void exposureRuntimeUsesFoundationProgressionBoundariesThroughFlameGatedPolicy() throws Exception {
        String source = Files.readString(Path.of(
                "src/main/java/com/gustavaopere/enshrouded/exposure/ExposureRuntime.java"));

        assertTrue(source.contains("FlameGatedDeadlyExposurePolicy"),
                "runtime must keep the passage-aware policy as the single Deadly progression gate");
        assertTrue(source.contains("ProgressionRuntimeBindings.ownerResolver()"),
                "runtime must resolve progression through the stable Foundation owner-provider handle");
        assertTrue(source.contains("ProgressionRuntimeBindings.passageQuery()"),
                "runtime must read passage through the stable Foundation provider handle so Stage 05 can supply persistence");
        assertTrue(source.contains("EnshroudedConfig.deadlyRequiredPassageLevel()"),
                "required Deadly passage tier must come from server config rather than a runtime literal");
    }
}
