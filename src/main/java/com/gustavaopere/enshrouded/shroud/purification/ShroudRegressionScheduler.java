package com.gustavaopere.enshrouded.shroud.purification;

import com.gustavaopere.enshrouded.shroud.core.CoreLifecycleState;
import com.gustavaopere.enshrouded.shroud.core.ShroudCoreService;
import com.gustavaopere.enshrouded.shroud.expansion.ShroudGridGeometry;
import com.gustavaopere.enshrouded.shroud.state.ShroudCellPos;
import com.gustavaopere.enshrouded.shroud.state.ShroudCellState;
import com.gustavaopere.enshrouded.shroud.state.ShroudCoreState;
import com.gustavaopere.enshrouded.shroud.state.ShroudRegionState;
import com.gustavaopere.enshrouded.shroud.state.ShroudWorldState;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Pure bounded logical retreat for destroyed Shroud cores.
 *
 * <p>Equivalent persisted state, core seed and policy always produce the same next work. The
 * scheduler stores no authoritative runtime queue, so an interrupted purification resumes from the
 * remaining SavedData cells without a schema extension.</p>
 */
public final class ShroudRegressionScheduler {
    private final ShroudGridGeometry geometry;
    private final PurificationPolicy policy;

    public ShroudRegressionScheduler(ShroudGridGeometry geometry, PurificationPolicy policy) {
        this.geometry = Objects.requireNonNull(geometry, "geometry");
        this.policy = Objects.requireNonNull(policy, "policy");
    }

    public TickResult tick(ShroudWorldState state, int globalBudget, int perCoreBudget) {
        Objects.requireNonNull(state, "state");
        if (globalBudget <= 0 || perCoreBudget <= 0) {
            throw new IllegalArgumentException("regression budgets must be > 0");
        }

        ShroudWorldState current = state;
        List<RegressedCell> regressed = new ArrayList<>();
        int remainingGlobal = globalBudget;

        List<UUID> destroyedCoreIds = state.cores().values().stream()
                .filter(core -> core.lifecycleState() == CoreLifecycleState.DESTROYED)
                .map(ShroudCoreState::id)
                .sorted()
                .toList();

        for (UUID coreId : destroyedCoreIds) {
            ShroudCoreState core = current.cores().get(coreId);
            if (core == null || core.lifecycleState() != CoreLifecycleState.DESTROYED) {
                continue;
            }

            ShroudRegionState region = requireOwnedRegion(current, core);
            if (region.cells().isEmpty()) {
                current = ShroudCoreService.markPurified(current, coreId).state();
                continue;
            }
            if (remainingGlobal == 0) {
                continue;
            }

            int work = Math.min(Math.min(perCoreBudget, remainingGlobal), region.cells().size());
            List<ShroudCellPos> ordered = orderedForRegression(core, region);
            LinkedHashMap<ShroudCellPos, ShroudCellState> cells = new LinkedHashMap<>(region.cells());

            for (int index = 0; index < work; index++) {
                ShroudCellPos position = ordered.get(index);
                ShroudCellState previous = cells.get(position);
                PurificationPolicy ignored = policy;
                var next = ignored.regress(previous);
                if (next.isPresent()) {
                    ShroudCellState updated = next.orElseThrow();
                    cells.put(position, updated);
                    regressed.add(new RegressedCell(core.id(), region.id(), position, previous.intensity(), updated.intensity(), false));
                } else {
                    cells.remove(position);
                    regressed.add(new RegressedCell(core.id(), region.id(), position, previous.intensity(), 0.0D, true));
                }
            }

            LinkedHashMap<UUID, ShroudRegionState> regions = new LinkedHashMap<>(current.regions());
            regions.put(region.id(), new ShroudRegionState(region.id(), region.coreId(), cells));
            current = new ShroudWorldState(current.schemaVersion(), current.cores(), regions);
            remainingGlobal -= work;

            if (cells.isEmpty()) {
                current = ShroudCoreService.markPurified(current, coreId).state();
            }
        }

        return new TickResult(current, regressed);
    }

    private static ShroudRegionState requireOwnedRegion(ShroudWorldState state, ShroudCoreState core) {
        ShroudRegionState region = state.regions().get(core.regionId());
        if (region == null || !region.coreId().equals(core.id())) {
            throw new IllegalStateException("destroyed core has no valid owned Shroud region: " + core.id());
        }
        return region;
    }

    private List<ShroudCellPos> orderedForRegression(ShroudCoreState core, ShroudRegionState region) {
        ShroudCellPos center = geometry.cellAt(core.center());
        return region.cells().keySet().stream()
                .sorted((left, right) -> compareCells(core.expansionSeed(), center, left, right))
                .toList();
    }

    private static int compareCells(long seed, ShroudCellPos center, ShroudCellPos left, ShroudCellPos right) {
        int byDistance = Long.compare(distanceSquared(center, right), distanceSquared(center, left));
        if (byDistance != 0) {
            return byDistance;
        }
        int bySeed = Long.compareUnsigned(orderKey(seed, left), orderKey(seed, right));
        if (bySeed != 0) {
            return bySeed;
        }
        return left.compareTo(right);
    }

    private static long distanceSquared(ShroudCellPos center, ShroudCellPos position) {
        long dx = (long) position.x() - center.x();
        long dy = (long) position.y() - center.y();
        long dz = (long) position.z() - center.z();
        return dx * dx + dy * dy + dz * dz;
    }

    private static long orderKey(long seed, ShroudCellPos position) {
        long value = seed;
        value ^= mix64(position.x() * 0x9E3779B97F4A7C15L);
        value ^= mix64(position.y() * 0xC2B2AE3D27D4EB4FL);
        value ^= mix64(position.z() * 0x165667B19E3779F9L);
        return mix64(value);
    }

    private static long mix64(long value) {
        value ^= value >>> 30;
        value *= 0xBF58476D1CE4E5B9L;
        value ^= value >>> 27;
        value *= 0x94D049BB133111EBL;
        return value ^ (value >>> 31);
    }

    public record RegressedCell(
            UUID coreId,
            UUID regionId,
            ShroudCellPos position,
            double previousIntensity,
            double currentIntensity,
            boolean cleared) {
        public RegressedCell {
            Objects.requireNonNull(coreId, "coreId");
            Objects.requireNonNull(regionId, "regionId");
            Objects.requireNonNull(position, "position");
        }
    }

    public record TickResult(ShroudWorldState state, List<RegressedCell> regressedCells) {
        public TickResult {
            Objects.requireNonNull(state, "state");
            regressedCells = List.copyOf(Objects.requireNonNull(regressedCells, "regressedCells"));
        }
    }
}
