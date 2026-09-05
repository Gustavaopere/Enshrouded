package com.gustavaopere.enshrouded.ecology.state;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
}
