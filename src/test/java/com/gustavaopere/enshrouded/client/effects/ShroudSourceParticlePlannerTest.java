package com.gustavaopere.enshrouded.client.effects;

import com.gustavaopere.enshrouded.config.EnshroudedClientConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class ShroudSourceParticlePlannerTest {
    @Test
    void sourceKindsAreDistanceBoundedAndUseOneSharedPulseCap() {
        var settings = new EnshroudedClientConfig.ParticleSettings(true, 6, 10.0D);

        assertEquals(2, ShroudSourceParticlePlanner.emissionCount(
                ShroudSourceParticlePlanner.SourceKind.CORE, settings, 4.0D));
        assertEquals(1, ShroudSourceParticlePlanner.emissionCount(
                ShroudSourceParticlePlanner.SourceKind.GROWTH, settings, 4.0D));
        assertEquals(3, ShroudSourceParticlePlanner.emissionCount(
                ShroudSourceParticlePlanner.SourceKind.RED_SLUDGE, settings, 4.0D));
        assertEquals(0, ShroudSourceParticlePlanner.emissionCount(
                ShroudSourceParticlePlanner.SourceKind.CORE, settings, 10.1D * 10.1D));
    }

    @Test
    void disabledOrTightlyCappedParticlesCannotExceedClientBudget() {
        var disabled = new EnshroudedClientConfig.ParticleSettings(false, 16, 16.0D);
        assertEquals(0, ShroudSourceParticlePlanner.emissionCount(
                ShroudSourceParticlePlanner.SourceKind.RED_SLUDGE, disabled, 1.0D));

        var capped = new EnshroudedClientConfig.ParticleSettings(true, 1, 16.0D);
        for (var kind : ShroudSourceParticlePlanner.SourceKind.values()) {
            assertTrue(ShroudSourceParticlePlanner.emissionCount(kind, capped, 1.0D) <= 1);
        }
    }

    @Test
    void denseSyntheticSceneCannotEscapeAggregatePulseBudget() {
        var settings = new EnshroudedClientConfig.ParticleSettings(true, 16, 16.0D);
        int remaining = settings.maxCount();
        int emitted = 0;
        for (int index = 0; index < 10_000 && remaining > 0; index++) {
            var kind = ShroudSourceParticlePlanner.SourceKind.values()[index % 3];
            int planned = Math.min(remaining, ShroudSourceParticlePlanner.emissionCount(kind, settings, 4.0D));
            emitted += planned;
            remaining -= planned;
        }
        assertTrue(emitted <= settings.maxCount());
    }
}
