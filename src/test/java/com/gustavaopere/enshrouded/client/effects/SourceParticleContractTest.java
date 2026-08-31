package com.gustavaopere.enshrouded.client.effects;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class SourceParticleContractTest {
    private static final Path ROOT = Path.of(System.getProperty("user.dir"));
    private static final Path JAVA = ROOT.resolve("src/main/java/com/gustavaopere/enshrouded");
    private static final Path ASSETS = ROOT.resolve("src/main/resources/assets/enshrouded");

    @Test
    void canonicalParticleRegistryProvidersAndSourceLocalRuntimeExist() throws IOException {
        String registry = Files.readString(JAVA.resolve("registry/ModParticles.java"));
        assertTrue(registry.contains("SHROUD_CORE"));
        assertTrue(registry.contains("SHROUD_GROWTH"));
        assertTrue(registry.contains("RED_SLUDGE"));
        assertTrue(Files.readString(JAVA.resolve("registry/ModRegistries.java")).contains("ModParticles.register(modBus)"));

        String client = Files.readString(JAVA.resolve("client/EnshroudedClient.java"));
        assertTrue(client.contains("ShroudParticleController::registerParticleProviders"));
        assertTrue(client.contains("ShroudParticleController.register(NeoForge.EVENT_BUS)"));

        String controller = Files.readString(JAVA.resolve("client/effects/ShroudParticleController.java"));
        assertTrue(controller.contains("ClientTickEvent.Post"));
        assertTrue(controller.contains("MAX_SAMPLES_PER_PULSE"));
        assertTrue(controller.contains("particles.maxDistance()"));
        assertTrue(controller.contains("minecraft.level.hasChunkAt"));
        assertTrue(controller.contains("ModBlocks.SHROUD_CORE"));
        assertTrue(controller.contains("ModBlocks.SHROUD_GROWTH"));
        assertTrue(controller.contains("ModBlocks.SHROUD_VEIN"));
        assertTrue(controller.contains("ModBlocks.WITHERED_GROWTH"));
        assertTrue(controller.contains("ModBlocks.RED_SLUDGE"));
        assertFalse(controller.contains("getAllEntities"));
    }

    @Test
    void customParticleDescriptionsAndTexturesArePackaged() throws IOException {
        for (String id : new String[]{"shroud_core", "shroud_growth", "red_sludge"}) {
            Path description = ASSETS.resolve("particles/" + id + ".json");
            assertTrue(Files.isRegularFile(description), "missing particle description " + id);
            assertTrue(Files.readString(description).contains("enshrouded:particle/" + id));
            assertPng(ASSETS.resolve("textures/particle/" + id + ".png"));
        }
    }

    private static void assertPng(Path path) throws IOException {
        assertTrue(Files.isRegularFile(path), "missing PNG asset: " + path);
        byte[] bytes = Files.readAllBytes(path);
        assertTrue(bytes.length > 100, "particle PNG is implausibly small: " + path);
        assertArrayEquals(new byte[]{(byte) 137, 80, 78, 71, 13, 10, 26, 10},
                new byte[]{bytes[0], bytes[1], bytes[2], bytes[3], bytes[4], bytes[5], bytes[6], bytes[7]});
    }
}
