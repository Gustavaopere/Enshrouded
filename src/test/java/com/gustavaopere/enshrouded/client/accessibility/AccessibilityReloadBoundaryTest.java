package com.gustavaopere.enshrouded.client.accessibility;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

final class AccessibilityReloadBoundaryTest {
    private static final Path ROOT = Path.of(System.getProperty("user.dir"));
    private static final Path MAIN = ROOT.resolve("src/main/java/com/gustavaopere/enshrouded");

    @Test
    void sharedClientConfigReloadResetsAllTransientStage07PresentationState() throws IOException {
        Path controller = MAIN.resolve("client/accessibility/AccessibilityPresetController.java");
        assertTrue(Files.isRegularFile(controller), "Stage 07.04 must own preset/reload semantics in one client-only controller");

        String source = Files.readString(controller);
        assertTrue(source.contains("ModConfigEvent.Loading"));
        assertTrue(source.contains("ModConfigEvent.Reloading"));
        assertTrue(source.contains("EnshroudedClientConfig.CLIENT_SPEC"));
        assertTrue(source.contains("ShroudFogController.reset()"));
        assertTrue(source.contains("ShroudAmbientController.reset()"));
        assertTrue(source.contains("ShroudParticleController.reset()"));

        String entrypoint = Files.readString(MAIN.resolve("client/EnshroudedClient.java"));
        assertTrue(entrypoint.contains("AccessibilityPresetController.register(modBus)"),
                "config reload handling must stay on the physical client mod bus");
    }
}
