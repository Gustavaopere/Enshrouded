package com.gustavaopere.enshrouded.shroud.worldgen;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;

class ShroudCoreWorldgenBoundaryTest {
    private static final Path WORLDGEN_SOURCE = Path.of(
            "src/main/java/com/gustavaopere/enshrouded/shroud/worldgen");

    @Test
    void worldgenNeverWritesCanonicalShroudStateDirectly() throws IOException {
        try (Stream<Path> files = Files.walk(WORLDGEN_SOURCE)) {
            for (Path path : files.filter(candidate -> candidate.toString().endsWith(".java")).toList()) {
                String source = Files.readString(path);
                assertFalse(source.contains("ShroudSavedData"),
                        () -> path + " must not access ShroudSavedData from worldgen");
                assertFalse(source.contains("ShroudCoreService"),
                        () -> path + " must not call ShroudCoreService from worldgen");
            }
        }
    }
}
