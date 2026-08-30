package com.gustavaopere.enshrouded.story.manifestation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class LichPhaseDirectorTest {
    @Test
    void derivesProviderNeutralEscalationFromHealthTimeOrEncounterEvents() {
        FirstManifestationDefinition definition = FirstManifestationDefinition.levelOne();
        LichPhaseDirector director = new LichPhaseDirector(definition);

        assertEquals(LichPhaseDirector.Phase.PHASE_ONE, director.phase(1.0F, 0L, 0));
        assertEquals(LichPhaseDirector.Phase.PHASE_TWO,
                director.phase(definition.phaseTwoHealthFraction(), 0L, 0));
        assertEquals(LichPhaseDirector.Phase.PHASE_THREE,
                director.phase(definition.phaseThreeHealthFraction(), 0L, 0));
        assertEquals(LichPhaseDirector.Phase.PHASE_TWO,
                director.phase(1.0F, definition.phaseTwoElapsedTicks(), 0));
        assertEquals(LichPhaseDirector.Phase.PHASE_THREE,
                director.phase(1.0F, definition.phaseThreeElapsedTicks(), 0));
        assertEquals(LichPhaseDirector.Phase.PHASE_TWO,
                director.phase(1.0F, 0L, definition.phaseTwoEventPressure()));
        assertEquals(LichPhaseDirector.Phase.PHASE_THREE,
                director.phase(1.0F, 0L, definition.phaseThreeEventPressure()));
    }

    @Test
    void strongestSatisfiedSignalWinsAndInvalidInputsFailClosed() {
        FirstManifestationDefinition definition = FirstManifestationDefinition.levelOne();
        LichPhaseDirector director = new LichPhaseDirector(definition);

        assertEquals(LichPhaseDirector.Phase.PHASE_THREE,
                director.phase(0.95F, definition.phaseThreeElapsedTicks(), 0));
        assertEquals(LichPhaseDirector.Phase.PHASE_THREE,
                director.phase(definition.phaseThreeHealthFraction(), 0L, definition.phaseTwoEventPressure()));

        assertThrows(IllegalArgumentException.class, () -> director.phase(Float.NaN, 0L, 0));
        assertThrows(IllegalArgumentException.class, () -> director.phase(-0.01F, 0L, 0));
        assertThrows(IllegalArgumentException.class, () -> director.phase(1.01F, 0L, 0));
        assertThrows(IllegalArgumentException.class, () -> director.phase(1.0F, -1L, 0));
        assertThrows(IllegalArgumentException.class, () -> director.phase(1.0F, 0L, -1));
    }
}
