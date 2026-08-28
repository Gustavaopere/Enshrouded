package com.gustavaopere.enshrouded.shroud.expansion;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

final class FrontierExpansionContractRedTest {
    @Test
    void frontierExpansionContractsExist() {
        assertDoesNotThrow(() -> Class.forName("com.gustavaopere.enshrouded.shroud.expansion.ShroudFrontier"));
        assertDoesNotThrow(() -> Class.forName("com.gustavaopere.enshrouded.shroud.expansion.ShroudFrontierEntry"));
        assertDoesNotThrow(() -> Class.forName("com.gustavaopere.enshrouded.shroud.expansion.ShroudPropagationPolicy"));
        assertDoesNotThrow(() -> Class.forName("com.gustavaopere.enshrouded.shroud.expansion.ShroudWorkBudget"));
        assertDoesNotThrow(() -> Class.forName("com.gustavaopere.enshrouded.shroud.expansion.ShroudExpansionScheduler"));
    }
}
