package com.gustavaopere.enshrouded.client.effects;

import com.gustavaopere.enshrouded.config.EnshroudedClientConfig;
import com.gustavaopere.enshrouded.presentation.AdvancedVfxCue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class AdvancedVfxCueTest {
    @Test void everyCueHasStrictBoundedPresentationBudgets() {
        for (AdvancedVfxCue cue : AdvancedVfxCue.values()) {
            assertTrue(cue.maxParticles() > 0 && cue.maxParticles() <= 24);
            assertTrue(cue.lifetimeTicks() > 0 && cue.lifetimeTicks() <= 40);
            assertTrue(cue.maxDistance() > 0.0D && cue.maxDistance() <= 48.0D);
            assertTrue(cue.cooldownTicks() >= cue.lifetimeTicks());
        }
    }
    @Test void reducedAndMinimalParticleSettingsRemainHardCaps() {
        var reduced = new EnshroudedClientConfig.ParticleSettings(true, 4, 8.0D);
        var minimal = new EnshroudedClientConfig.ParticleSettings(false, 0, 2.0D);
        for (AdvancedVfxCue cue : AdvancedVfxCue.values()) {
            assertTrue(cue.particleBudget(reduced.enabled(), reduced.maxCount()) <= 4);
            assertEquals(0, cue.particleBudget(minimal.enabled(), minimal.maxCount()));
        }
    }
    @Test void onlyDiscreteAuthoritativeWorldEventsRequireServerCues() {
        assertTrue(AdvancedVfxCue.CORE_DESTROYED.serverAuthored());
        assertTrue(AdvancedVfxCue.FLAME_RITUAL_SUCCESS.serverAuthored());
        assertTrue(AdvancedVfxCue.LICH_MANIFESTATION.serverAuthored());
        assertFalse(AdvancedVfxCue.CLEAR_TO_SHROUD.serverAuthored());
        assertFalse(AdvancedVfxCue.SHROUD_TO_DEADLY.serverAuthored());
        assertFalse(AdvancedVfxCue.SANCTUARY_ENTER.serverAuthored());
        assertFalse(AdvancedVfxCue.MADNESS_ESCALATION.serverAuthored());
    }
}
