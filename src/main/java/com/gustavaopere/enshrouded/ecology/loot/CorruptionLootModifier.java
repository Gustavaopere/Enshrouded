package com.gustavaopere.enshrouded.ecology.loot;

/**
 * Level-1 corruption-loot policy. No Level-1 recipe currently consumes a corruption reagent,
 * therefore the policy is explicitly disabled rather than registering orphan loot.
 */
public final class CorruptionLootModifier {
    private static final CorruptionLootModifier LEVEL_ONE = new CorruptionLootModifier(false, 0);

    private final boolean reagentEnabled;
    private final int maxRollsPerDeath;

    private CorruptionLootModifier(boolean reagentEnabled, int maxRollsPerDeath) {
        this.reagentEnabled = reagentEnabled;
        this.maxRollsPerDeath = maxRollsPerDeath;
    }

    public static CorruptionLootModifier levelOne() {
        return LEVEL_ONE;
    }

    public boolean reagentEnabled() {
        return reagentEnabled;
    }

    public int maxRollsPerDeath() {
        return maxRollsPerDeath;
    }

    public int rollCountForDeath(float corruptionIntensity) {
        return 0;
    }
}
