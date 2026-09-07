package com.gustavaopere.enshrouded.client.effects;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class AdvancedVfxClientContractTest {
    @Test
    void controllerConsumesOnlySynchronizedStateAndEphemeralClientboundCues() throws IOException {
        String controller = Files.readString(Path.of(
                "src/main/java/com/gustavaopere/enshrouded/client/effects/AdvancedVfxController.java"));

        assertTrue(controller.contains("ClientExposureState.INSTANCE.lastSequence()"));
        assertTrue(controller.contains("ClientAdvancedVfxState.INSTANCE.drain()"));
        assertTrue(controller.contains("AdvancedVfxTransitionPlanner.plan"));
        assertTrue(controller.contains("EnshroudedClientConfig.particleSettings()"));
        assertTrue(controller.contains("minecraft.level.hasChunkAt(payload.origin())"));
        assertFalse(controller.contains("PacketDistributor"));
        assertFalse(controller.contains("SavedData"));
        assertFalse(controller.contains("sendToServer"));
        assertFalse(controller.contains("lodestone"));
    }

    @Test
    void physicalClientBootstrapRegistersAndResetsAdvancedVfx() throws IOException {
        String client = Files.readString(Path.of(
                "src/main/java/com/gustavaopere/enshrouded/client/EnshroudedClient.java"));
        String accessibility = Files.readString(Path.of(
                "src/main/java/com/gustavaopere/enshrouded/client/accessibility/AccessibilityPresetController.java"));

        assertTrue(client.contains("AdvancedVfxController.register(NeoForge.EVENT_BUS);"));
        assertTrue(client.contains("AdvancedVfxController.reset();"));
        assertTrue(client.contains("ClientAdvancedVfxState.INSTANCE.reset();"));
        assertTrue(client.contains("ClientExposureState.INSTANCE.reset();"));
        assertTrue(accessibility.contains("AdvancedVfxController.reset();"));
    }
}
