package com.gustavaopere.enshrouded.ecology.loot;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

final class CorruptionLootModifierTest {
    @Test
    void levelOneDoesNotInventAnUnusedReagentDrop() {
        CorruptionLootModifier policy = CorruptionLootModifier.levelOne();

        assertFalse(policy.reagentEnabled(),
                "Level 1 currently has no recipe consuming a corruption reagent, so the drop must stay disabled");
        assertEquals(0, policy.maxRollsPerDeath(),
                "disabled Level-1 corruption loot must have a strict zero-roll budget");
        assertEquals(0, policy.rollCountForDeath(1.0F),
                "even a fully corrupted entity must not emit an orphan reagent while the policy is disabled");
    }
}
