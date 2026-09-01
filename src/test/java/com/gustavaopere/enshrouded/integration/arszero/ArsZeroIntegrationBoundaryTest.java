package com.gustavaopere.enshrouded.integration.arszero;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class ArsZeroIntegrationBoundaryTest {
    @Test
    void optionalAdapterUsesRegistryBoundaryWithoutCompileTimeArsZeroImports() throws Exception {
        String provider = Files.readString(Path.of(
                "src/main/java/com/gustavaopere/enshrouded/integration/arszero/ArsZeroLichProvider.java"
        ));
        String probe = Files.readString(Path.of(
                "src/main/java/com/gustavaopere/enshrouded/integration/arszero/ArsZeroCompatibilityProbe.java"
        ));

        assertFalse(provider.contains("com.github.ars_zero"));
        assertFalse(probe.contains("com.github.ars_zero"));
        assertTrue(probe.contains("ars_zero"));
        assertTrue(probe.contains("lich"));
        assertTrue(probe.contains("MobCategory.MONSTER"));
    }

    @Test
    void bootstrapDefersProbeUntilCommonSetupAfterRegistriesExist() throws Exception {
        String entrypoint = Files.readString(Path.of(
                "src/main/java/com/gustavaopere/enshrouded/Enshrouded.java"
        ));
        String runtime = Files.readString(Path.of(
                "src/main/java/com/gustavaopere/enshrouded/integration/arszero/ArsZeroIntegrationRuntime.java"
        ));

        assertTrue(entrypoint.contains("ArsZeroIntegrationRuntime.register(modBus)"));
        assertTrue(runtime.contains("FMLCommonSetupEvent"));
        assertTrue(runtime.contains("enqueueWork"));
        assertTrue(runtime.contains("LichBossRuntime.registerProvider"));
    }
}
