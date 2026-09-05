package com.gustavaopere.enshrouded.shroud.expansion;

import com.gustavaopere.enshrouded.config.EnshroudedConfig;
import com.gustavaopere.enshrouded.shroud.state.ShroudCoreState;
import com.gustavaopere.enshrouded.shroud.state.ShroudRegionState;
import com.gustavaopere.enshrouded.shroud.state.ShroudSavedData;
import com.gustavaopere.enshrouded.shroud.state.ShroudWorldState;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Server-thread adapter that owns the ephemeral per-dimension frontier schedulers.
 *
 * <p>The persisted logical field remains authoritative in {@link ShroudSavedData}; scheduler
 * queues are deliberately runtime-only and can be reconstructed from persisted region cells after
 * restart. This adapter never loads chunks.</p>
 */
public final class ShroudExpansionRuntime {
    private static final int FRONTIER_CAPACITY_PER_CORE = 4096;
    private static final ShroudGridGeometry GEOMETRY = ShroudGridGeometry.levelOne();
    private static final ShroudPropagationPolicy POLICY = ShroudPropagationPolicy.terrainNeutral();
    private static final ConcurrentMap<ResourceKey<Level>, ShroudExpansionScheduler> SCHEDULERS =
            new ConcurrentHashMap<>();

    private ShroudExpansionRuntime() {
    }

    /**
     * Enqueues the core's center cell only when an active core still owns an empty logical region.
     * Repeated registration requests are idempotent because the frontier deduplicates cell
     * positions and persisted non-empty regions no longer require an initial seed.
     */
    public static boolean enqueueInitialCellIfNeeded(ServerLevel level, ShroudWorldState state, UUID coreId) {
        Objects.requireNonNull(level, "level");
        Objects.requireNonNull(state, "state");
        Objects.requireNonNull(coreId, "coreId");

        ShroudCoreState core = state.cores().get(coreId);
        if (core == null || !core.lifecycleState().expansionEligible()) {
            return false;
        }

        ShroudRegionState region = state.regions().get(core.regionId());
        if (region == null || !region.coreId().equals(core.id())) {
            throw new IllegalStateException("active core has no valid owned Shroud region: " + core.id());
        }
        if (!region.cells().isEmpty()) {
            return false;
        }

        return scheduler(level).enqueue(
                core.id(),
                new ShroudFrontierEntry(GEOMETRY.cellAt(core.center()), core.expansionEpoch(), 0L)
        );
    }

    /**
     * Advances one bounded logical frontier tick for the dimension and persists only changed state.
     */
    public static boolean advance(ServerLevel level) {
        Objects.requireNonNull(level, "level");
        ShroudSavedData savedData = ShroudSavedData.get(level);
        ShroudWorldState current = savedData.state();

        if (current.cores().isEmpty()) {
            SCHEDULERS.remove(level.dimension());
            return false;
        }

        ShroudWorkBudget budget = growthBudget(
                EnshroudedConfig.coreGrowthGlobalWorkPerTick(),
                EnshroudedConfig.coreGrowthWorkPerTick()
        );
        ShroudExpansionScheduler.TickResult result = scheduler(level).tick(current, budget);
        if (result.state().equals(current)) {
            return false;
        }

        savedData.replace(result.state());
        return true;
    }

    static ShroudWorkBudget growthBudget(int globalWorkPerTick, int perCoreWorkPerTick) {
        return new ShroudWorkBudget(globalWorkPerTick, perCoreWorkPerTick);
    }

    public static void clear() {
        SCHEDULERS.clear();
    }

    private static ShroudExpansionScheduler scheduler(ServerLevel level) {
        return SCHEDULERS.computeIfAbsent(
                level.dimension(),
                ignored -> new ShroudExpansionScheduler(GEOMETRY, POLICY, FRONTIER_CAPACITY_PER_CORE)
        );
    }
}
