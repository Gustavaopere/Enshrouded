package com.gustavaopere.enshrouded.client.effects;

import com.gustavaopere.enshrouded.exposure.madness.MadnessStage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class MadnessAudioCueTest {
    @Test
    void onlyServerAuthoredAudioHallucinationStagesProduceCues() {
        assertEquals(MadnessAudioCue.NONE, MadnessAudioCue.forStage(MadnessStage.STABLE));
        assertEquals(MadnessAudioCue.NONE, MadnessAudioCue.forStage(MadnessStage.UNEASY));
        assertEquals(MadnessAudioCue.NONE, MadnessAudioCue.forStage(MadnessStage.DISTORTED));
        assertEquals(MadnessAudioCue.CRITICAL, MadnessAudioCue.forStage(MadnessStage.CRITICAL));
        assertEquals(MadnessAudioCue.FATAL, MadnessAudioCue.forStage(MadnessStage.FATAL));
    }

    @Test
    void moreSevereCueRemainsBoundedAndNoLessUrgent() {
        assertTrue(MadnessAudioCue.CRITICAL.cooldownTicks() >= 40);
        assertTrue(MadnessAudioCue.FATAL.cooldownTicks() >= 20);
        assertTrue(MadnessAudioCue.FATAL.cooldownTicks() <= MadnessAudioCue.CRITICAL.cooldownTicks());
        assertTrue(MadnessAudioCue.FATAL.volumeMultiplier() >= MadnessAudioCue.CRITICAL.volumeMultiplier());
        assertTrue(MadnessAudioCue.FATAL.volumeMultiplier() <= 1.0F);
    }
}
