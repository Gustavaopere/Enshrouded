package com.gustavaopere.enshrouded.client.effects;

import com.gustavaopere.enshrouded.config.EnshroudedClientConfig;
import com.gustavaopere.enshrouded.presentation.AdvancedVfxCue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class AdvancedVfxSequenceBudgetTest {
    @Test
    void temporalTargetsAreMonotonicAndReachExactlyTheConfiguredBudget() {
        var settings = new EnshroudedClientConfig.ParticleSettings(true, 16, 16.0D);

        for (AdvancedVfxCue cue : AdvancedVfxCue.values()) {
            int previous = 0;
            for (int elapsed = 0; elapsed <= cue.lifetimeTicks(); elapsed++) {
                int target = AdvancedVfxSequenceBudget.targetEmitted(cue, settings, elapsed);
                assertTrue(target >= previous);
                assertTrue(target <= cue.particleBudget(settings.enabled(), settings.maxCount()));
                previous = target;
            }
            assertEquals(cue.particleBudget(settings.enabled(), settings.maxCount()), previous);
        }
    }

    @Test
    void reducedAndMinimalPresetsRemainHardCapsAcrossTheWholeSequence() {
        var reduced = new EnshroudedClientConfig.ParticleSettings(true, 4, 8.0D);
        var minimal = new EnshroudedClientConfig.ParticleSettings(false, 0, 2.0D);

        for (AdvancedVfxCue cue : AdvancedVfxCue.values()) {
            assertTrue(AdvancedVfxSequenceBudget.targetEmitted(cue, reduced, cue.lifetimeTicks()) <= 4);
            assertEquals(0, AdvancedVfxSequenceBudget.targetEmitted(cue, minimal, cue.lifetimeTicks()));
        }
    }

    @Test
    void invalidOrOutOfRangeElapsedTimeFailsClosedByClamping() {
        var settings = new EnshroudedClientConfig.ParticleSettings(true, 8, 10.0D);
        assertEquals(0, AdvancedVfxSequenceBudget.targetEmitted(AdvancedVfxCue.CLEAR_TO_SHROUD, settings, -10));
        assertEquals(6, AdvancedVfxSequenceBudget.targetEmitted(AdvancedVfxCue.CLEAR_TO_SHROUD, settings, 10_000));
    }
}
