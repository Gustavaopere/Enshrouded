package com.gustavaopere.enshrouded.client.ambient;

import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class ShroudAmbientProfileTest {
    @Test
    void ordinaryAndDeadlyUseDistinctBoundedSoundProfiles() {
        ShroudSoundProfile clear = ShroudSoundProfile.forState(ShroudSeverity.CLEAR, false);
        ShroudSoundProfile ordinary = ShroudSoundProfile.forState(ShroudSeverity.SHROUD, false);
        ShroudSoundProfile deadly = ShroudSoundProfile.forState(ShroudSeverity.DEADLY, false);

        assertEquals(ShroudSoundProfile.NONE, clear);
        assertEquals(ShroudSoundProfile.NONE, ShroudSoundProfile.forState(ShroudSeverity.SHROUD, true));
        assertEquals(ShroudSoundProfile.ORDINARY, ordinary);
        assertEquals(ShroudSoundProfile.DEADLY, deadly);
        assertTrue(ordinary.cooldownTicks() >= 80 && ordinary.cooldownTicks() <= 400);
        assertTrue(deadly.cooldownTicks() >= 40 && deadly.cooldownTicks() < ordinary.cooldownTicks());
        assertTrue(deadly.baseVolume() > ordinary.baseVolume());
    }

    @Test
    void ordinaryAndDeadlyUseDistinctBoundedParticleProfiles() {
        ShroudParticleProfile clear = ShroudParticleProfile.forState(ShroudSeverity.CLEAR, false);
        ShroudParticleProfile ordinary = ShroudParticleProfile.forState(ShroudSeverity.SHROUD, false);
        ShroudParticleProfile deadly = ShroudParticleProfile.forState(ShroudSeverity.DEADLY, false);

        assertEquals(ShroudParticleProfile.NONE, clear);
        assertEquals(ShroudParticleProfile.NONE, ShroudParticleProfile.forState(ShroudSeverity.DEADLY, true));
        assertEquals(ShroudParticleProfile.ORDINARY, ordinary);
        assertEquals(ShroudParticleProfile.DEADLY, deadly);
        assertTrue(ordinary.intervalTicks() >= 4);
        assertTrue(deadly.intervalTicks() >= 2 && deadly.intervalTicks() <= ordinary.intervalTicks());
        assertTrue(ordinary.baseCount() > 0);
        assertTrue(deadly.baseCount() > ordinary.baseCount());
        assertTrue(deadly.baseCount() <= 12, "Level-1 particle emission must stay tightly bounded per tick");
    }
}
