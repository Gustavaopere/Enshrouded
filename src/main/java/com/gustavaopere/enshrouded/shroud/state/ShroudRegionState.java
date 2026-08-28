package com.gustavaopere.enshrouded.shroud.state;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public record ShroudRegionState(UUID id, UUID coreId, Map<ShroudCellPos, ShroudCellState> cells) {
    public ShroudRegionState {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(coreId, "coreId");
        Objects.requireNonNull(cells, "cells");

        LinkedHashMap<ShroudCellPos, ShroudCellState> copy = new LinkedHashMap<>();
        cells.forEach((position, state) -> {
            Objects.requireNonNull(position, "cell position");
            Objects.requireNonNull(state, "cell state");
            if (!position.equals(state.position())) {
                throw new IllegalArgumentException("cell map key must match state position");
            }
            if (copy.put(position, state) != null) {
                throw new IllegalArgumentException("duplicate cell position: " + position);
            }
        });
        cells = Map.copyOf(copy);
    }
}
