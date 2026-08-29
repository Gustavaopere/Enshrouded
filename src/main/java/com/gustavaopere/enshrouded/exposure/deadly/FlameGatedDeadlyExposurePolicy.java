package com.gustavaopere.enshrouded.exposure.deadly;

import com.gustavaopere.enshrouded.api.progression.FlamePassageQuery;
import com.gustavaopere.enshrouded.api.progression.ProgressionOwnerResolver;
import com.gustavaopere.enshrouded.exposure.DeadlyExposurePolicy;
import com.gustavaopere.enshrouded.exposure.ShroudExposureAttachment;

import java.util.Objects;
import java.util.UUID;

/**
 * Progression-aware implementation seam for Deadly Shroud passage. The first structural slice
 * intentionally preserves the old fail-closed barrier behavior; progression semantics are driven
 * by the next behavioral TDD slice.
 */
public final class FlameGatedDeadlyExposurePolicy implements DeadlyExposurePolicy {
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
        return fallback.evaluate(playerId, state, elapsedTicks, maxReserveTicks);
    }
}
