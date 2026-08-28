package com.gustavaopere.enshrouded.shroud.state;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public record ShroudWorldState(
        int schemaVersion,
        Map<UUID, ShroudCoreState> cores,
        Map<UUID, ShroudRegionState> regions) {

    public ShroudWorldState {
        if (schemaVersion < 1) {
            throw new IllegalArgumentException("schemaVersion must be >= 1");
        }
        Objects.requireNonNull(cores, "cores");
        Objects.requireNonNull(regions, "regions");

        LinkedHashMap<UUID, ShroudCoreState> coreCopy = new LinkedHashMap<>();
        cores.forEach((id, state) -> {
            Objects.requireNonNull(id, "core id");
            Objects.requireNonNull(state, "core state");
            if (!id.equals(state.id())) {
                throw new IllegalArgumentException("core map key must match state id");
            }
            if (coreCopy.put(id, state) != null) {
                throw new IllegalArgumentException("duplicate core id: " + id);
            }
        });

        LinkedHashMap<UUID, ShroudRegionState> regionCopy = new LinkedHashMap<>();
        regions.forEach((id, state) -> {
            Objects.requireNonNull(id, "region id");
            Objects.requireNonNull(state, "region state");
            if (!id.equals(state.id())) {
                throw new IllegalArgumentException("region map key must match state id");
            }
            if (regionCopy.put(id, state) != null) {
                throw new IllegalArgumentException("duplicate region id: " + id);
            }
        });

        cores = Map.copyOf(coreCopy);
        regions = Map.copyOf(regionCopy);
    }

    public static ShroudWorldState empty() {
        return new ShroudWorldState(ShroudSchema.CURRENT_VERSION, Map.of(), Map.of());
    }
}
