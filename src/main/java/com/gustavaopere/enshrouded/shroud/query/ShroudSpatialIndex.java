package com.gustavaopere.enshrouded.shroud.query;

import com.gustavaopere.enshrouded.api.shroud.ShroudSample;
import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;
import com.gustavaopere.enshrouded.shroud.core.CoreLifecycleState;
import com.gustavaopere.enshrouded.shroud.state.ShroudCellPos;
import com.gustavaopere.enshrouded.shroud.state.ShroudCellState;
import com.gustavaopere.enshrouded.shroud.state.ShroudCoreState;
import com.gustavaopere.enshrouded.shroud.state.ShroudRegionState;
import com.gustavaopere.enshrouded.shroud.state.ShroudWorldState;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Immutable cell-local lookup built from one dimension-local {@link ShroudWorldState} snapshot.
 * Query cost is independent of total core count after the index has been built.
 */
public final class ShroudSpatialIndex {
    private final Map<ShroudCellPos, IndexedCell> cells;

    private ShroudSpatialIndex(Map<ShroudCellPos, IndexedCell> cells) {
        this.cells = Map.copyOf(cells);
    }

    public static ShroudSpatialIndex from(ShroudWorldState state) {
        Objects.requireNonNull(state, "state");
        HashMap<ShroudCellPos, IndexedCell> indexed = new HashMap<>();

        state.regions().values().stream()
                .sorted((left, right) -> left.id().compareTo(right.id()))
                .forEach(region -> addRegion(state, region, indexed));
        return new ShroudSpatialIndex(indexed);
    }

    private static void addRegion(
            ShroudWorldState state,
            ShroudRegionState region,
            Map<ShroudCellPos, IndexedCell> indexed) {
        ShroudCoreState core = state.cores().get(region.coreId());
        if (core == null || core.lifecycleState() == CoreLifecycleState.PURIFIED) {
            return;
        }

        region.cells().values().stream()
                .sorted((left, right) -> left.position().compareTo(right.position()))
                .forEach(cell -> {
                    IndexedCell candidate = new IndexedCell(core.id(), region.id(), cell);
                    indexed.merge(cell.position(), candidate, ShroudSpatialIndex::winner);
                });
    }

    private static IndexedCell winner(IndexedCell left, IndexedCell right) {
        int byIntensity = Double.compare(left.cell().intensity(), right.cell().intensity());
        if (byIntensity != 0) {
            return byIntensity > 0 ? left : right;
        }
        return left.coreId().compareTo(right.coreId()) <= 0 ? left : right;
    }

    public int indexedCellCount() {
        return cells.size();
    }

    public Optional<IndexedCell> lookup(ShroudCellPos position) {
        Objects.requireNonNull(position, "position");
        return Optional.ofNullable(cells.get(position));
    }

    public ShroudSample sample(
            ShroudCellPos position,
            ShroudSeverityThresholds thresholds,
            boolean sanctuarySuppressed) {
        Objects.requireNonNull(position, "position");
        Objects.requireNonNull(thresholds, "thresholds");

        IndexedCell indexedCell = cells.get(position);
        if (indexedCell == null) {
            return sanctuarySuppressed
                    ? new ShroudSample(0.0f, ShroudSeverity.CLEAR, Optional.empty(), true)
                    : ShroudSample.clear();
        }

        ShroudCellState cell = indexedCell.cell();
        float intensity = (float) cell.intensity();
        ShroudSeverity severity = thresholds.classify(cell.intensity(), cell.severity());
        return new ShroudSample(intensity, severity, Optional.of(indexedCell.coreId()), sanctuarySuppressed);
    }

    public record IndexedCell(UUID coreId, UUID regionId, ShroudCellState cell) {
        public IndexedCell {
            Objects.requireNonNull(coreId, "coreId");
            Objects.requireNonNull(regionId, "regionId");
            Objects.requireNonNull(cell, "cell");
        }
    }
}
