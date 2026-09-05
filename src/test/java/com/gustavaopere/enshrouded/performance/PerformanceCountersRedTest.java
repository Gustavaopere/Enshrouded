package com.gustavaopere.enshrouded.performance;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class PerformanceCountersRedTest {
    @Test
    void aggregatesLevelOneHotPathWorkAndResetsAtomically() {
        PerformanceCounters counters = new PerformanceCounters();

        counters.recordExpansion(7, 3);
        counters.recordRegression(6, 2);
        counters.recordMaterialization(5, 2);
        counters.recordRestoration(8, 4);
        counters.recordLocalQueries(11);
        counters.recordEntityUpdate(100, 6);
        counters.recordClientPayloads(3);
        counters.recordClientEffects(1, 8);

        PerformanceCounters.Snapshot snapshot = counters.snapshot();
        assertEquals(7L, snapshot.expansionAttempts());
        assertEquals(3L, snapshot.expansionAppliedCells());
        assertEquals(6L, snapshot.regressionWorkUnits());
        assertEquals(2L, snapshot.regressionClearedCells());
        assertEquals(5L, snapshot.materializationAttempts());
        assertEquals(2L, snapshot.successfulMaterializations());
        assertEquals(8L, snapshot.restorationAttempts());
        assertEquals(4L, snapshot.revertedBlocks());
        assertEquals(11L, snapshot.localQueries());
        assertEquals(100L, snapshot.entitySamples());
        assertEquals(6L, snapshot.entityStateUpdates());
        assertEquals(3L, snapshot.clientPayloadsSent());
        assertEquals(1L, snapshot.clientEffectSamples());
        assertEquals(8L, snapshot.clientEffectsEmitted());
        assertEquals(12.0D, snapshot.clientPayloadsPerSecondOverTicks(5), 0.0001D);

        PerformanceCounters.Snapshot drained = counters.snapshotAndReset();
        assertEquals(snapshot, drained);
        assertEquals(PerformanceCounters.Snapshot.empty(), counters.snapshot());
    }

    @Test
    void rejectsImpossibleSuccessCountsAndNegativeWork() {
        PerformanceCounters counters = new PerformanceCounters();

        assertThrows(IllegalArgumentException.class, () -> counters.recordExpansion(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> counters.recordExpansion(1, 2));
        assertThrows(IllegalArgumentException.class, () -> counters.recordMaterialization(1, 2));
        assertThrows(IllegalArgumentException.class, () -> counters.recordRestoration(1, 2));
        assertThrows(IllegalArgumentException.class, () -> counters.recordLocalQueries(-1));
        assertThrows(IllegalArgumentException.class, () -> counters.recordEntityUpdate(2, 3));
        assertThrows(IllegalArgumentException.class, () -> counters.recordClientEffects(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> counters.recordClientEffects(0, -1));
        assertThrows(IllegalArgumentException.class, () -> counters.snapshot().clientPayloadsPerSecondOverTicks(0));
    }
}
