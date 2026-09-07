package com.gustavaopere.enshrouded.client.effects;

import com.gustavaopere.enshrouded.config.EnshroudedClientConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class AdvancedVfxCoreProximityTest {
    @Test
    void coreDensityRampsTowardTheSourceWithoutEscapingThePulseCap() {
        var settings = new EnshroudedClientConfig.ParticleSettings(true, 8, 12.0D);

        assertEquals(4, ShroudSourceParticlePlanner.emissionCount(
                ShroudSourceParticlePlanner.SourceKind.CORE, settings, 2.0D * 2.0D));
        assertEquals(3, ShroudSourceParticlePlanner.emissionCount(
                ShroudSourceParticlePlanner.SourceKind.CORE, settings, 5.0D * 5.0D));
        assertEquals(2, ShroudSourceParticlePlanner.emissionCount(
                ShroudSourceParticlePlanner.SourceKind.CORE, settings, 10.0D * 10.0D));
    }
}
