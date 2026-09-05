package com.gustavaopere.enshrouded.ecology.state;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class EntityCorruptionTickBudgetRedTest {

    @Test
    void admitsOnlyConfiguredWorkPerServerTickAndResetsOnNextTick() {
        EntityCorruptionTickBudget budget = new EntityCorruptionTickBudget(3);

        assertTrue(budget.tryAcquire(100L));
        assertTrue(budget.tryAcquire(100L));
        assertTrue(budget.tryAcquire(100L));
        assertFalse(budget.tryAcquire(100L));

        assertTrue(budget.tryAcquire(101L));
    }

    @Test
    void samplingHitsExactlyOncePerWindowAfterWarmup() {
        UUID entityId = UUID.fromString("00000000-0000-0000-0000-000000000001");

        for (int window = 1; window <= 3; window++) {
            int hits = 0;
            int start = window * EntityCorruptionRuntime.SAMPLE_INTERVAL_TICKS;
            int end = start + EntityCorruptionRuntime.SAMPLE_INTERVAL_TICKS;
            for (int tick = start; tick < end; tick++) {
                if (EntityCorruptionRuntime.isSampleTick(tick, entityId)) {
                    hits++;
                }
            }
            assertEquals(1, hits, "each post-warmup sampling window must contain exactly one opportunity");
        }
    }

    @Test
    void entityIdsAreDistributedAcrossSamplingPhases() {
        Set<Integer> phases = new HashSet<>();

        for (int index = 1; index <= 16; index++) {
            UUID entityId = new UUID(index, index * 31L);
            for (int tick = EntityCorruptionRuntime.SAMPLE_INTERVAL_TICKS;
                 tick < EntityCorruptionRuntime.SAMPLE_INTERVAL_TICKS * 2;
                 tick++) {
                if (EntityCorruptionRuntime.isSampleTick(tick, entityId)) {
                    phases.add(Math.floorMod(tick, EntityCorruptionRuntime.SAMPLE_INTERVAL_TICKS));
                }
            }
        }

        assertTrue(phases.size() > 1, "UUID phase selection must distribute entities across more than one tick");
    }
}
