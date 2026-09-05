package com.gustavaopere.enshrouded.performance;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Passive process-local observability for bounded Level-1 hot paths.
 *
 * <p>Counters never participate in gameplay decisions, persistence, scheduling or authority.
 * Recording is allocation-free and constant-time; callers remain responsible for enforcing their
 * existing canonical budgets.</p>
 */
public final class PerformanceCounters {
    private static final PerformanceCounters GLOBAL = new PerformanceCounters();

    private final AtomicLong expansionAttempts = new AtomicLong();
    private final AtomicLong expansionAppliedCells = new AtomicLong();
    private final AtomicLong regressionWorkUnits = new AtomicLong();
    private final AtomicLong regressionClearedCells = new AtomicLong();
    private final AtomicLong materializationAttempts = new AtomicLong();
    private final AtomicLong successfulMaterializations = new AtomicLong();
    private final AtomicLong restorationAttempts = new AtomicLong();
    private final AtomicLong revertedBlocks = new AtomicLong();
    private final AtomicLong localQueries = new AtomicLong();
    private final AtomicLong entitySamples = new AtomicLong();
    private final AtomicLong entityStateUpdates = new AtomicLong();
    private final AtomicLong clientPayloadsSent = new AtomicLong();
    private final AtomicLong clientEffectSamples = new AtomicLong();
    private final AtomicLong clientEffectsEmitted = new AtomicLong();

    public static PerformanceCounters global() {
        return GLOBAL;
    }

    public void recordExpansion(long attempts, long appliedCells) {
        requireSuccessWithinWork("expansion", attempts, appliedCells);
        expansionAttempts.addAndGet(attempts);
        expansionAppliedCells.addAndGet(appliedCells);
    }

    public void recordRegression(long workUnits, long clearedCells) {
        requireSuccessWithinWork("regression", workUnits, clearedCells);
        regressionWorkUnits.addAndGet(workUnits);
        regressionClearedCells.addAndGet(clearedCells);
    }

    public void recordMaterialization(long attempts, long successful) {
        requireSuccessWithinWork("materialization", attempts, successful);
        materializationAttempts.addAndGet(attempts);
        successfulMaterializations.addAndGet(successful);
    }

    public void recordRestoration(long attempts, long reverted) {
        requireSuccessWithinWork("restoration", attempts, reverted);
        restorationAttempts.addAndGet(attempts);
        revertedBlocks.addAndGet(reverted);
    }

    public void recordLocalQueries(long count) {
        localQueries.addAndGet(requireNonNegative("localQueries", count));
    }

    public void recordEntityUpdate(long samples, long stateUpdates) {
        requireSuccessWithinWork("entityUpdate", samples, stateUpdates);
        entitySamples.addAndGet(samples);
        entityStateUpdates.addAndGet(stateUpdates);
    }

    public void recordClientPayloads(long sent) {
        clientPayloadsSent.addAndGet(requireNonNegative("clientPayloadsSent", sent));
    }

    public void recordClientEffects(long samples, long emitted) {
        requireSuccessWithinWork("clientEffects", samples, emitted);
        clientEffectSamples.addAndGet(samples);
        clientEffectsEmitted.addAndGet(emitted);
    }

    public Snapshot snapshot() {
        return new Snapshot(
                expansionAttempts.get(),
                expansionAppliedCells.get(),
                regressionWorkUnits.get(),
                regressionClearedCells.get(),
                materializationAttempts.get(),
                successfulMaterializations.get(),
                restorationAttempts.get(),
                revertedBlocks.get(),
                localQueries.get(),
                entitySamples.get(),
                entityStateUpdates.get(),
                clientPayloadsSent.get(),
                clientEffectSamples.get(),
                clientEffectsEmitted.get());
    }

    /**
     * Drains every counter using atomic get-and-set operations. Concurrent increments are never
     * lost; an increment racing a drain belongs to either the returned window or the next one.
     */
    public Snapshot snapshotAndReset() {
        return new Snapshot(
                expansionAttempts.getAndSet(0L),
                expansionAppliedCells.getAndSet(0L),
                regressionWorkUnits.getAndSet(0L),
                regressionClearedCells.getAndSet(0L),
                materializationAttempts.getAndSet(0L),
                successfulMaterializations.getAndSet(0L),
                restorationAttempts.getAndSet(0L),
                revertedBlocks.getAndSet(0L),
                localQueries.getAndSet(0L),
                entitySamples.getAndSet(0L),
                entityStateUpdates.getAndSet(0L),
                clientPayloadsSent.getAndSet(0L),
                clientEffectSamples.getAndSet(0L),
                clientEffectsEmitted.getAndSet(0L));
    }

    public void reset() {
        snapshotAndReset();
    }

    private static void requireSuccessWithinWork(String name, long work, long successful) {
        requireNonNegative(name + "Work", work);
        requireNonNegative(name + "Successful", successful);
        if (successful > work) {
            throw new IllegalArgumentException(name + " successful count must not exceed work count");
        }
    }

    private static long requireNonNegative(String name, long value) {
        if (value < 0L) {
            throw new IllegalArgumentException(name + " must be >= 0");
        }
        return value;
    }

    public record Snapshot(
            long expansionAttempts,
            long expansionAppliedCells,
            long regressionWorkUnits,
            long regressionClearedCells,
            long materializationAttempts,
            long successfulMaterializations,
            long restorationAttempts,
            long revertedBlocks,
            long localQueries,
            long entitySamples,
            long entityStateUpdates,
            long clientPayloadsSent,
            long clientEffectSamples,
            long clientEffectsEmitted) {
        public Snapshot {
            requireNonNegative("expansionAttempts", expansionAttempts);
            requireNonNegative("expansionAppliedCells", expansionAppliedCells);
            requireNonNegative("regressionWorkUnits", regressionWorkUnits);
            requireNonNegative("regressionClearedCells", regressionClearedCells);
            requireNonNegative("materializationAttempts", materializationAttempts);
            requireNonNegative("successfulMaterializations", successfulMaterializations);
            requireNonNegative("restorationAttempts", restorationAttempts);
            requireNonNegative("revertedBlocks", revertedBlocks);
            requireNonNegative("localQueries", localQueries);
            requireNonNegative("entitySamples", entitySamples);
            requireNonNegative("entityStateUpdates", entityStateUpdates);
            requireNonNegative("clientPayloadsSent", clientPayloadsSent);
            requireNonNegative("clientEffectSamples", clientEffectSamples);
            requireNonNegative("clientEffectsEmitted", clientEffectsEmitted);
        }

        public static Snapshot empty() {
            return new Snapshot(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L);
        }

        /** Rate for a caller-supplied server-tick observation window; no wall-clock timer is owned here. */
        public double clientPayloadsPerSecondOverTicks(long observedServerTicks) {
            if (observedServerTicks <= 0L) {
                throw new IllegalArgumentException("observedServerTicks must be > 0");
            }
            return (double) clientPayloadsSent * 20.0D / observedServerTicks;
        }
    }
}
