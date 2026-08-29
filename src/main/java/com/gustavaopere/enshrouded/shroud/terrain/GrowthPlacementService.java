package com.gustavaopere.enshrouded.shroud.terrain;

import com.gustavaopere.enshrouded.api.shroud.MutationAuthority;
import com.gustavaopere.enshrouded.api.shroud.ShroudQuery;

import java.util.Objects;

/** Stage-02 seam for growth placement; world mutation behavior is added behind canonical safety/query gates. */
public final class GrowthPlacementService {
    private final MutationAuthority mutationAuthority;
    private final ShroudQuery shroudQuery;

    public GrowthPlacementService(MutationAuthority mutationAuthority, ShroudQuery shroudQuery) {
        this.mutationAuthority = Objects.requireNonNull(mutationAuthority, "mutationAuthority");
        this.shroudQuery = Objects.requireNonNull(shroudQuery, "shroudQuery");
    }

    MutationAuthority mutationAuthority() {
        return mutationAuthority;
    }

    ShroudQuery shroudQuery() {
        return shroudQuery;
    }
}
