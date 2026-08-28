package com.gustavaopere.enshrouded.shroud.core;

import com.gustavaopere.enshrouded.shroud.state.ShroudCoreState;
import com.gustavaopere.enshrouded.shroud.state.ShroudRegionState;
import com.gustavaopere.enshrouded.shroud.state.ShroudWorldState;
import net.minecraft.core.BlockPos;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class ShroudCoreService {
    private ShroudCoreService() {
    }

    public static CoreMutationResult registerDormant(
            ShroudWorldState state,
            UUID coreId,
            UUID regionId,
            BlockPos center,
            int tier,
            int maxInfluenceRadius,
            long expansionSeed) {
        Objects.requireNonNull(state, "state");
        Objects.requireNonNull(coreId, "coreId");
        Objects.requireNonNull(regionId, "regionId");
        Objects.requireNonNull(center, "center");

        ShroudCoreState existing = state.cores().get(coreId);
        if (existing != null) {
            assertSameRegistration(existing, regionId, center, tier, maxInfluenceRadius, expansionSeed);
            return CoreMutationResult.unchanged(state);
        }

        if (state.regions().containsKey(regionId)) {
            throw new IllegalStateException("region id is already owned: " + regionId);
        }
        state.cores().values().stream()
                .filter(core -> core.center().equals(center))
                .findFirst()
                .ifPresent(core -> {
                    throw new IllegalStateException("a Shroud core is already registered at " + center + ": " + core.id());
                });

        ShroudCoreState core = new ShroudCoreState(
                coreId,
                center,
                tier,
                CoreLifecycleState.DORMANT,
                maxInfluenceRadius,
                expansionSeed,
                0L,
                regionId
        );
        ShroudRegionState region = new ShroudRegionState(regionId, coreId, Map.of());

        LinkedHashMap<UUID, ShroudCoreState> cores = new LinkedHashMap<>(state.cores());
        LinkedHashMap<UUID, ShroudRegionState> regions = new LinkedHashMap<>(state.regions());
        cores.put(coreId, core);
        regions.put(regionId, region);

        return CoreMutationResult.changed(new ShroudWorldState(state.schemaVersion(), cores, regions));
    }

    public static CoreMutationResult activate(ShroudWorldState state, UUID coreId) {
        return transition(state, coreId, CoreLifecycleState.ACTIVE, true);
    }

    public static CoreMutationResult destroy(ShroudWorldState state, UUID coreId) {
        Objects.requireNonNull(state, "state");
        ShroudCoreState core = requireCore(state, coreId);
        if (core.lifecycleState() == CoreLifecycleState.DESTROYED || core.lifecycleState() == CoreLifecycleState.PURIFIED) {
            return CoreMutationResult.unchanged(state);
        }
        return transition(state, coreId, CoreLifecycleState.DESTROYED, false);
    }

    public static CoreMutationResult markPurified(ShroudWorldState state, UUID coreId) {
        Objects.requireNonNull(state, "state");
        ShroudCoreState core = requireCore(state, coreId);
        if (core.lifecycleState() == CoreLifecycleState.PURIFIED) {
            return CoreMutationResult.unchanged(state);
        }
        return transition(state, coreId, CoreLifecycleState.PURIFIED, false);
    }

    private static CoreMutationResult transition(
            ShroudWorldState state,
            UUID coreId,
            CoreLifecycleState target,
            boolean targetIsIdempotent) {
        Objects.requireNonNull(state, "state");
        Objects.requireNonNull(target, "target");
        ShroudCoreState core = requireCore(state, coreId);
        if (targetIsIdempotent && core.lifecycleState() == target) {
            return CoreMutationResult.unchanged(state);
        }

        CoreLifecycleState transitioned = core.lifecycleState().transitionTo(target);
        ShroudCoreState updated = withLifecycle(core, transitioned);
        LinkedHashMap<UUID, ShroudCoreState> cores = new LinkedHashMap<>(state.cores());
        cores.put(coreId, updated);
        return CoreMutationResult.changed(new ShroudWorldState(state.schemaVersion(), cores, state.regions()));
    }

    private static ShroudCoreState requireCore(ShroudWorldState state, UUID coreId) {
        Objects.requireNonNull(coreId, "coreId");
        ShroudCoreState core = state.cores().get(coreId);
        if (core == null) {
            throw new IllegalArgumentException("unknown Shroud core: " + coreId);
        }
        return core;
    }

    private static ShroudCoreState withLifecycle(ShroudCoreState core, CoreLifecycleState lifecycle) {
        return new ShroudCoreState(
                core.id(),
                core.center(),
                core.tier(),
                lifecycle,
                core.maxInfluenceRadius(),
                core.expansionSeed(),
                core.expansionEpoch(),
                core.regionId()
        );
    }

    private static void assertSameRegistration(
            ShroudCoreState existing,
            UUID regionId,
            BlockPos center,
            int tier,
            int maxInfluenceRadius,
            long expansionSeed) {
        if (!existing.regionId().equals(regionId)
                || !existing.center().equals(center)
                || existing.tier() != tier
                || existing.maxInfluenceRadius() != maxInfluenceRadius
                || existing.expansionSeed() != expansionSeed) {
            throw new IllegalStateException("core id collision with different registration data: " + existing.id());
        }
    }
}
