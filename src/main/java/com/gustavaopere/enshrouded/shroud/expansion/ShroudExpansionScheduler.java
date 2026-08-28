package com.gustavaopere.enshrouded.shroud.expansion;

import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;
import com.gustavaopere.enshrouded.shroud.state.ShroudCellPos;
import com.gustavaopere.enshrouded.shroud.state.ShroudCellState;
import com.gustavaopere.enshrouded.shroud.state.ShroudCoreState;
import com.gustavaopere.enshrouded.shroud.state.ShroudRegionState;
import com.gustavaopere.enshrouded.shroud.state.ShroudWorldState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Coordinates bounded logical frontier work without owning or loading Minecraft chunks.
 *
 * <p>Frontier queues are intentionally runtime-only. Persisted {@link ShroudWorldState} remains
 * the canonical logical field; a restart may rebuild frontier work from that state without
 * changing field semantics.</p>
 */
public final class ShroudExpansionScheduler {
    private final ShroudGridGeometry geometry;
    private final ShroudPropagationPolicy policy;
    private final int frontierCapacityPerCore;
    private final Map<UUID, ShroudFrontier> frontiers = new HashMap<>();
    private final Map<UUID, Long> nextSequence = new HashMap<>();
    private UUID nextCoreHint;

    public ShroudExpansionScheduler(
            ShroudGridGeometry geometry,
            ShroudPropagationPolicy policy,
            int frontierCapacityPerCore) {
        this.geometry = Objects.requireNonNull(geometry, "geometry");
        this.policy = Objects.requireNonNull(policy, "policy");
        if (frontierCapacityPerCore <= 0) {
            throw new IllegalArgumentException("frontierCapacityPerCore must be > 0");
        }
        this.frontierCapacityPerCore = frontierCapacityPerCore;
    }

    public boolean enqueue(UUID coreId, ShroudFrontierEntry entry) {
        Objects.requireNonNull(coreId, "coreId");
        Objects.requireNonNull(entry, "entry");
        ShroudFrontier frontier = frontiers.computeIfAbsent(coreId, ignored -> new ShroudFrontier(frontierCapacityPerCore));
        boolean accepted = frontier.offer(entry);
        if (accepted) {
            nextSequence.merge(coreId, Math.addExact(entry.sequence(), 1L), Math::max);
        }
        return accepted;
    }

    public int queuedEntries(UUID coreId) {
        Objects.requireNonNull(coreId, "coreId");
        ShroudFrontier frontier = frontiers.get(coreId);
        return frontier == null ? 0 : frontier.size();
    }

    public TickResult tick(ShroudWorldState state, ShroudWorkBudget budget) {
        Objects.requireNonNull(state, "state");
        Objects.requireNonNull(budget, "budget");

        discardIneligibleFrontiers(state);
        List<UUID> activeCoreIds = activeQueuedCoreIds(state);
        if (activeCoreIds.isEmpty()) {
            nextCoreHint = null;
            return TickResult.idle(state);
        }

        LinkedHashMap<UUID, ShroudRegionState> regions = new LinkedHashMap<>(state.regions());
        LinkedHashMap<UUID, Integer> processedPerCore = new LinkedHashMap<>();
        int processedEntries = 0;
        int appliedCells = 0;
        int index = startingIndex(activeCoreIds);
        int visitsWithoutProgress = 0;

        while (processedEntries < budget.globalPerTick() && visitsWithoutProgress < activeCoreIds.size()) {
            UUID coreId = activeCoreIds.get(index);
            ShroudFrontier frontier = frontiers.get(coreId);
            int coreProcessed = processedPerCore.getOrDefault(coreId, 0);
            boolean canProcess = frontier != null
                    && !frontier.isEmpty()
                    && coreProcessed < budget.perCorePerTick();

            if (canProcess) {
                ShroudFrontierEntry entry = frontier.poll().orElseThrow();
                processedEntries++;
                processedPerCore.put(coreId, coreProcessed + 1);
                visitsWithoutProgress = 0;

                ShroudCoreState core = state.cores().get(coreId);
                if (core != null
                        && core.lifecycleState().expansionEligible()
                        && entry.expansionEpoch() == core.expansionEpoch()) {
                    appliedCells += processEntry(core, entry, regions);
                }
            } else {
                visitsWithoutProgress++;
            }

            index = (index + 1) % activeCoreIds.size();
            nextCoreHint = activeCoreIds.get(index);
        }

        ShroudWorldState nextState = appliedCells == 0
                ? state
                : new ShroudWorldState(state.schemaVersion(), state.cores(), regions);
        return new TickResult(nextState, processedEntries, appliedCells, processedPerCore);
    }

    private int processEntry(
            ShroudCoreState core,
            ShroudFrontierEntry entry,
            Map<UUID, ShroudRegionState> regions) {
        double intensity = policy.intensity(core, geometry, entry.position());
        if (intensity <= 0.0D) {
            return 0;
        }

        ShroudRegionState region = regions.get(core.regionId());
        if (region == null || !region.coreId().equals(core.id())) {
            throw new IllegalStateException("core has no valid owned Shroud region: " + core.id());
        }

        if (region.cells().containsKey(entry.position())) {
            return 0;
        }

        LinkedHashMap<ShroudCellPos, ShroudCellState> cells = new LinkedHashMap<>(region.cells());
        cells.put(entry.position(), new ShroudCellState(entry.position(), intensity, ShroudSeverity.SHROUD));
        ShroudRegionState updatedRegion = new ShroudRegionState(region.id(), region.coreId(), cells);
        regions.put(region.id(), updatedRegion);

        enqueueEligibleNeighbors(core, entry.position(), updatedRegion);
        return 1;
    }

    private void enqueueEligibleNeighbors(
            ShroudCoreState core,
            ShroudCellPos source,
            ShroudRegionState region) {
        for (ShroudCellPos neighbor : policy.neighbors(source)) {
            if (region.cells().containsKey(neighbor) || policy.intensity(core, geometry, neighbor) <= 0.0D) {
                continue;
            }
            long sequence = nextSequence.getOrDefault(core.id(), 0L);
            if (enqueue(core.id(), new ShroudFrontierEntry(neighbor, core.expansionEpoch(), sequence))) {
                nextSequence.put(core.id(), Math.addExact(sequence, 1L));
            }
        }
    }

    private void discardIneligibleFrontiers(ShroudWorldState state) {
        List<UUID> discard = new ArrayList<>();
        frontiers.forEach((coreId, frontier) -> {
            ShroudCoreState core = state.cores().get(coreId);
            if (core == null || !core.lifecycleState().expansionEligible()) {
                discard.add(coreId);
            }
        });
        discard.forEach(coreId -> {
            frontiers.remove(coreId);
            nextSequence.remove(coreId);
            if (coreId.equals(nextCoreHint)) {
                nextCoreHint = null;
            }
        });
    }

    private List<UUID> activeQueuedCoreIds(ShroudWorldState state) {
        return frontiers.entrySet().stream()
                .filter(entry -> !entry.getValue().isEmpty())
                .map(Map.Entry::getKey)
                .filter(coreId -> {
                    ShroudCoreState core = state.cores().get(coreId);
                    return core != null && core.lifecycleState().expansionEligible();
                })
                .sorted()
                .toList();
    }

    private int startingIndex(List<UUID> activeCoreIds) {
        if (nextCoreHint == null) {
            return 0;
        }
        int index = activeCoreIds.indexOf(nextCoreHint);
        return index < 0 ? 0 : index;
    }

    public record TickResult(
            ShroudWorldState state,
            int processedEntries,
            int appliedCells,
            Map<UUID, Integer> processedPerCore) {
        public TickResult {
            Objects.requireNonNull(state, "state");
            Objects.requireNonNull(processedPerCore, "processedPerCore");
            if (processedEntries < 0) {
                throw new IllegalArgumentException("processedEntries must be >= 0");
            }
            if (appliedCells < 0 || appliedCells > processedEntries) {
                throw new IllegalArgumentException("appliedCells must be within [0, processedEntries]");
            }
            processedPerCore = Map.copyOf(processedPerCore);
        }

        private static TickResult idle(ShroudWorldState state) {
            return new TickResult(state, 0, 0, Map.of());
        }
    }
}
