package com.gustavaopere.enshrouded.exposure;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExposureSurfaceRedTest {
    private static final Path ROOT = Path.of("src/main/java/com/gustavaopere/enshrouded/exposure");

    @Test
    void stageThreePlayerExposureSurfaceExistsBeforeRuntimeImplementation() {
        List<String> requiredSources = List.of(
                "ShroudExposureAttachment.java",
                "ExposureSchema.java",
                "ExposureService.java",
                "ExposureSnapshot.java",
                "ExposurePayload.java",
                "DeadlyExposurePolicy.java"
        );

        List<String> missing = requiredSources.stream()
                .filter(file -> Files.notExists(ROOT.resolve(file)))
                .toList();

        assertEquals(List.of(), missing,
                "Stage 03 player exposure contract is incomplete; missing production surfaces: " + missing);
    }
}
