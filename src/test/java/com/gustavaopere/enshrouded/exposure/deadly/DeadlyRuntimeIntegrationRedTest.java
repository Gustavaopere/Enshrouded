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
                "runtime must replace the Task-01 hard barrier with the Task-03 passage-aware policy");
        assertTrue(source.contains("ProgressionOwnerResolver.standalone()"),
                "standalone runtime must resolve progression through the Foundation owner boundary");
        assertTrue(source.contains("FlamePassageQuery.levelOneFallback()"),
                "standalone runtime must use the Foundation Level-1 passage fallback until Stage 05 provides persistence");
        assertTrue(source.contains("EnshroudedConfig.deadlyRequiredPassageLevel()"),
                "required Deadly passage tier must come from server config rather than a runtime literal");
    }
}
