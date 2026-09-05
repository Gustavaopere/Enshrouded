package com.gustavaopere.enshrouded.performance;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class PerformanceCountersRedTest {
    @Test
    void aggregatesLevelOneHotPathWorkAndResetsAtomically() {
        PerformanceCounters counters = new PerformanceCounters();

        counters.recordExpansion(7, 3);
        counters.recordMaterialization(5, 2);
        counters.recordRevertedBlocks(4);
        counters.recordEntityScan(100, 6);
        counters.recordClientPayloads(3);
        counters.recordClientEffects(192, 17);

        PerformanceCounters.Snapshot snapshot = counters.snapshot();
        assertEquals(7L, snapshot.expansionAttempts());
        assertEquals(3L, snapshot.expansionAppliedCells());
        assertEquals(5L, snapshot.materializationAttempts());
        assertEquals(2L, snapshot.successfulMaterializations());
        assertEquals(4L, snapshot.revertedBlocks());
        assertEquals(100L, snapshot.entityScans());
        assertEquals(6L, snapshot.entityConversions());
        assertEquals(3L, snapshot.clientPayloadsSent());
        assertEquals(192L, snapshot.clientEffectSamples());
        assertEquals(17L, snapshot.clientEffectsEmitted());
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
        assertThrows(IllegalArgumentException.class, () -> counters.recordEntityScan(2, 3));
        assertThrows(IllegalArgumentException.class, () -> counters.recordClientEffects(2, 3));
        assertThrows(IllegalArgumentException.class, () -> counters.snapshot().clientPayloadsPerSecondOverTicks(0));
    }
}
