package com.gustavaopere.enshrouded;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BootstrapContractTest {
    @Test
    void mainModClassAndMetadataExist() {
        assertDoesNotThrow(
                () -> Class.forName("com.gustavaopere.enshrouded.Enshrouded"),
                "The NeoForge entrypoint must exist on the test runtime classpath"
        );
        assertTrue(
                Files.isRegularFile(Path.of("src/main/resources/META-INF/neoforge.mods.toml")),
                "NeoForge metadata must exist in src/main/resources"
        );
    }
}
