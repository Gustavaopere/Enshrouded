package com.gustavaopere.enshrouded.exposure.deadly;

import com.gustavaopere.enshrouded.api.progression.FlamePassageQuery;
import com.gustavaopere.enshrouded.api.progression.ProgressionOwnerResolver;
import com.gustavaopere.enshrouded.exposure.DeadlyExposurePolicy;
import com.gustavaopere.enshrouded.exposure.ShroudExposureAttachment;

import java.util.Objects;
import java.util.UUID;

/**
 * Progression-aware Deadly Shroud policy. Passage ownership is resolved exclusively through the
 * Foundation progression boundaries; unavailable or inconsistent progression data fails closed.
 */
public final class FlameGatedDeadlyExposurePolicy implements DeadlyExposurePolicy {
    private static final int ALLOWED_DRAIN_TICKS_PER_TICK = 1;

    private final ProgressionOwnerResolver ownerResolver;
    private final FlamePassageQuery passageQuery;
    private final PassageRequirement requirement;
    private final DeadlyExposurePolicy fallback;

    public FlameGatedDeadlyExposurePolicy(
            ProgressionOwnerResolver ownerResolver,
            FlamePassageQuery passageQuery,
            PassageRequirement requirement,
            int emergencyWindowTicks,
            int rapidDrainTicksPerTick) {
        this.ownerResolver = Objects.requireNonNull(ownerResolver, "ownerResolver");
        this.passageQuery = Objects.requireNonNull(passageQuery, "passageQuery");
        this.requirement = Objects.requireNonNull(requirement, "requirement");
        this.fallback = DeadlyExposurePolicy.levelOneBarrier(emergencyWindowTicks, rapidDrainTicksPerTick);
    }

    public ProgressionOwnerResolver ownerResolver() {
        return ownerResolver;
    }

    public FlamePassageQuery passageQuery() {
        return passageQuery;
    }

    public PassageRequirement requirement() {
        return requirement;
    }

    @Override
    public Decision evaluate(
            UUID playerId,
            ShroudExposureAttachment state,
            int elapsedTicks,
            int maxReserveTicks) {
        // The existing barrier owns validation and is also the deterministic fail-closed decision.
        Decision failClosed = fallback.evaluate(playerId, state, elapsedTicks, maxReserveTicks);
        if (playerId == null) {
            return failClosed;
        }

        try {
            var owner = ownerResolver.resolve(playerId);
            if (owner == null || !requirement.isMetBy(passageQuery.passageLevel(owner))) {
                return failClosed;
            }
        } catch (RuntimeException exception) {
            return failClosed;
        }

        int current = Math.min(state.remainingTicks(), maxReserveTicks);
        long candidate = (long) current - (long) ALLOWED_DRAIN_TICKS_PER_TICK * elapsedTicks;
        return new Decision((int) Math.max(candidate, 0L), false);
    }
}
