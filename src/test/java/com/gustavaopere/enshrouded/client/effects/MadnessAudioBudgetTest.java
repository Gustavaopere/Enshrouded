package com.gustavaopere.enshrouded.client.effects;

import com.gustavaopere.enshrouded.client.ambient.ShroudAmbientController;
import com.gustavaopere.enshrouded.config.EnshroudedClientConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class MadnessAudioBudgetTest {
    @Test
    void madnessCueHasItsOwnCooldownAndOwnClientSetting() {
        ShroudAmbientController.BudgetState state = new ShroudAmbientController.BudgetState();
        var settings = new EnshroudedClientConfig.MadnessAudioSettings(true, 1.0D);

        assertTrue(ShroudAmbientController.planMadness(MadnessAudioCue.CRITICAL, settings, 100L, state));
        assertFalse(ShroudAmbientController.planMadness(MadnessAudioCue.CRITICAL, settings, 101L, state));
        assertTrue(ShroudAmbientController.planMadness(
                MadnessAudioCue.CRITICAL, settings, 100L + MadnessAudioCue.CRITICAL.cooldownTicks(), state));
    }

    @Test
    void madnessCueCanBeDisabledWithoutDisablingAmbientAudio() {
        ShroudAmbientController.BudgetState state = new ShroudAmbientController.BudgetState();
        assertFalse(ShroudAmbientController.planMadness(
                MadnessAudioCue.FATAL,
                new EnshroudedClientConfig.MadnessAudioSettings(false, 1.0D),
                20L,
                state));
        assertFalse(ShroudAmbientController.planMadness(
                MadnessAudioCue.FATAL,
                new EnshroudedClientConfig.MadnessAudioSettings(true, 0.0D),
                20L,
                state));
    }
}
