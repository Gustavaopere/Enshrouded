package com.gustavaopere.enshrouded.client.ecology;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class CorruptionVisualRuntimeTest {
    @Test
    void cleanStateNeverEmitsParticles() {
        CorruptionVisualState clean = CorruptionVisualState.fromIntensity(0.0F);
        assertFalse(CorruptionVisualRuntime.shouldEmitAtTick(clean, 0));
        assertFalse(CorruptionVisualRuntime.shouldEmitAtTick(clean, 20));
        assertFalse(CorruptionVisualRuntime.shouldEmitAtTick(clean, 200));
    }

    @Test
    void taintedAndCorruptedStatesUseTheirBoundedCadence() {
        CorruptionVisualState tainted = CorruptionVisualState.fromIntensity(0.25F);
        CorruptionVisualState corrupted = CorruptionVisualState.fromIntensity(0.75F);

        assertFalse(CorruptionVisualRuntime.shouldEmitAtTick(tainted, 19));
        assertTrue(CorruptionVisualRuntime.shouldEmitAtTick(tainted, 20));
        assertFalse(CorruptionVisualRuntime.shouldEmitAtTick(corrupted, 7));
        assertTrue(CorruptionVisualRuntime.shouldEmitAtTick(corrupted, 8));
    }
}
