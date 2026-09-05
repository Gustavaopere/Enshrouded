package com.gustavaopere.enshrouded.shroud.expansion;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ShroudExpansionRuntimeBudgetRedTest {

    @Test
    void growthBudgetKeepsGlobalAndPerCoreLimitsIndependent() {
        ShroudWorkBudget budget = ShroudExpansionRuntime.growthBudget(250, 64);

        assertEquals(250, budget.globalPerTick());
        assertEquals(64, budget.perCorePerTick());
    }
}
