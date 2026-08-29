package com.gustavaopere.enshrouded.exposure;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class ExposureAttachmentConfigRedTest {
    @Test
    void exposureAttachmentIsPersistedRegisteredAndFreshOnRespawn() throws Exception {
        try {
            Field attachment = ShroudExposureAttachment.class.getField("PLAYER_EXPOSURE");
            assertTrue(Modifier.isStatic(attachment.getModifiers()));
            Method register = ShroudExposureAttachment.class.getMethod(
                    "register",
                    Class.forName("net.neoforged.bus.api.IEventBus")
            );
            assertTrue(Modifier.isStatic(register.getModifiers()));

            String source = Files.readString(Path.of(
                    "src/main/java/com/gustavaopere/enshrouded/exposure/ShroudExposureAttachment.java"));
            assertTrue(source.contains("NeoForgeRegistries.ATTACHMENT_TYPES"),
                    "player exposure must use the NeoForge attachment registry");
            assertTrue(source.contains(".serialize(MAP_CODEC)"),
                    "attachment persistence must use the validated versioned map codec");
            assertFalse(source.contains(".copyOnDeath("),
                    "exposure reserve must not copy arbitrary pre-death state onto respawn");
        } catch (NoSuchFieldException | NoSuchMethodException exception) {
            fail("NeoForge exposure attachment registration is not implemented yet: " + exception.getMessage());
        }
    }

    @Test
    void serverConfigExposesNormalReserveAndDeadlyEmergencyWindow() throws Exception {
        try {
            Class<?> config = Class.forName("com.gustavaopere.enshrouded.config.EnshroudedConfig");
            Method maxReserve = config.getMethod("exposureMaxReserveTicks");
            Method emergencyWindow = config.getMethod("exposureEmergencyWindowTicks");
            assertEquals(int.class, maxReserve.getReturnType());
            assertEquals(int.class, emergencyWindow.getReturnType());

            assertEquals(300 * 20, ExposureSchema.DEFAULT_MAX_RESERVE_TICKS,
                    "Level-1 normal exposure baseline is 300 seconds");
            assertTrue(ExposureSchema.DEFAULT_EMERGENCY_WINDOW_TICKS < ExposureSchema.DEFAULT_MAX_RESERVE_TICKS,
                    "Deadly fallback window must be materially shorter than ordinary Shroud reserve");
        } catch (NoSuchMethodException exception) {
            fail("Exposure server config contract is not implemented yet: " + exception.getMessage());
        }
    }
}
