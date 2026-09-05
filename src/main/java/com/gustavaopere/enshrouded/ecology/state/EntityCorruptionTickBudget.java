package com.gustavaopere.enshrouded.ecology.state;

/**
 * Allocation-free server-tick admission limiter for entity-corruption updates.
 *
 * <p>The limiter retains no entity or level references. Once the configured allowance is
 * exhausted, additional eligible entities are left untouched until a later sampling opportunity;
 * pathological density therefore increases update latency instead of per-tick query work.</p>
 */
final class EntityCorruptionTickBudget {
    private final int maxPerTick;
    private long currentTick = Long.MIN_VALUE;
    private int used;

    EntityCorruptionTickBudget(int maxPerTick) {
        if (maxPerTick <= 0) {
            throw new IllegalArgumentException("maxPerTick must be > 0");
        }
        this.maxPerTick = maxPerTick;
    }

    boolean tryAcquire(long serverTick) {
        if (serverTick != currentTick) {
            currentTick = serverTick;
            used = 0;
        }
        if (used >= maxPerTick) {
            return false;
        }
        used++;
        return true;
    }

    int maxPerTick() {
        return maxPerTick;
    }
}
