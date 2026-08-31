package com.gustavaopere.enshrouded.client.ambient;

import com.gustavaopere.enshrouded.config.EnshroudedClientConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class ShroudAmbientBudgetTest {
    @Test
    void cooldownsBoundRepeatedAmbientEmission() {
        ShroudAmbientController.BudgetState state = new ShroudAmbientController.BudgetState();
        var audio = new EnshroudedClientConfig.AudioSettings(true, 1.0D);
        var particles = new EnshroudedClientConfig.ParticleSettings(true, 8);

        ShroudAmbientController.EmissionPlan first = ShroudAmbientController.plan(
                ShroudSoundProfile.ORDINARY, ShroudParticleProfile.ORDINARY, audio, particles, 100L, state);
        assertTrue(first.playSound());
        assertEquals(3, first.particleCount());

        ShroudAmbientController.EmissionPlan immediate = ShroudAmbientController.plan(
                ShroudSoundProfile.ORDINARY, ShroudParticleProfile.ORDINARY, audio, particles, 101L, state);
        assertFalse(immediate.playSound());
        assertEquals(0, immediate.particleCount());

        ShroudAmbientController.EmissionPlan particlesReady = ShroudAmbientController.plan(
                ShroudSoundProfile.ORDINARY, ShroudParticleProfile.ORDINARY, audio, particles, 108L, state);
        assertFalse(particlesReady.playSound());
        assertEquals(3, particlesReady.particleCount());

        ShroudAmbientController.EmissionPlan soundReady = ShroudAmbientController.plan(
                ShroudSoundProfile.ORDINARY, ShroudParticleProfile.ORDINARY, audio, particles, 280L, state);
        assertTrue(soundReady.playSound());
    }

    @Test
    void channelsAreIndependentAndParticleCountIsHardCapped() {
        ShroudAmbientController.BudgetState audioOffState = new ShroudAmbientController.BudgetState();
        ShroudAmbientController.EmissionPlan audioOff = ShroudAmbientController.plan(
                ShroudSoundProfile.DEADLY,
                ShroudParticleProfile.DEADLY,
                new EnshroudedClientConfig.AudioSettings(false, 1.0D),
                new EnshroudedClientConfig.ParticleSettings(true, 4),
                20L,
                audioOffState);
        assertFalse(audioOff.playSound());
        assertEquals(4, audioOff.particleCount());

        ShroudAmbientController.BudgetState particlesOffState = new ShroudAmbientController.BudgetState();
        ShroudAmbientController.EmissionPlan particlesOff = ShroudAmbientController.plan(
                ShroudSoundProfile.DEADLY,
                ShroudParticleProfile.DEADLY,
                new EnshroudedClientConfig.AudioSettings(true, 1.0D),
                new EnshroudedClientConfig.ParticleSettings(false, 16),
                20L,
                particlesOffState);
        assertTrue(particlesOff.playSound());
        assertEquals(0, particlesOff.particleCount());
    }

    @Test
    void noHazardCreatesNoAmbientWorkAndResetClearsCooldowns() {
        ShroudAmbientController.BudgetState state = new ShroudAmbientController.BudgetState();
        var audio = EnshroudedClientConfig.AudioSettings.defaults();
        var particles = EnshroudedClientConfig.ParticleSettings.defaults();

        ShroudAmbientController.EmissionPlan none = ShroudAmbientController.plan(
                ShroudSoundProfile.NONE, ShroudParticleProfile.NONE, audio, particles, 5L, state);
        assertFalse(none.playSound());
        assertEquals(0, none.particleCount());

        ShroudAmbientController.plan(
                ShroudSoundProfile.DEADLY, ShroudParticleProfile.DEADLY, audio, particles, 10L, state);
        state.reset();
        ShroudAmbientController.EmissionPlan afterReset = ShroudAmbientController.plan(
                ShroudSoundProfile.DEADLY, ShroudParticleProfile.DEADLY, audio, particles, 11L, state);
        assertTrue(afterReset.playSound());
        assertTrue(afterReset.particleCount() > 0);
    }
}
