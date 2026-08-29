package com.gustavaopere.enshrouded.client.ecology;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class CorruptionVisualStateTest {
    @Test
    void cleanAndInvalidIntensityProduceNoVisualCue() {
        CorruptionVisualState clean = CorruptionVisualState.fromIntensity(0.0F);
        CorruptionVisualState invalid = CorruptionVisualState.fromIntensity(Float.NaN);

        assertFalse(clean.visible());
        assertEquals(CorruptionVisualState.Cue.NONE, clean.cue());
        assertEquals(0, clean.particleCount());
        assertFalse(invalid.visible());
    }

    @Test
    void taintedAndCorruptedIntensityProduceBoundedReadableCues() {
        CorruptionVisualState tainted = CorruptionVisualState.fromIntensity(0.25F);
        CorruptionVisualState corrupted = CorruptionVisualState.fromIntensity(0.75F);

        assertTrue(tainted.visible());
        assertEquals(CorruptionVisualState.Cue.TAINTED, tainted.cue());
        assertEquals(1, tainted.particleCount());
        assertTrue(tainted.particleIntervalTicks() >= 10,
                "tainted cue must stay sparse enough to avoid per-entity particle spam");

        assertTrue(corrupted.visible());
        assertEquals(CorruptionVisualState.Cue.CORRUPTED, corrupted.cue());
        assertTrue(corrupted.particleCount() >= 1 && corrupted.particleCount() <= 2,
                "corrupted cue must remain bounded per emission");
        assertTrue(corrupted.particleIntervalTicks() > 0
                        && corrupted.particleIntervalTicks() < tainted.particleIntervalTicks(),
                "fully corrupted entities must be more immediately readable than merely tainted ones");
    }
}
