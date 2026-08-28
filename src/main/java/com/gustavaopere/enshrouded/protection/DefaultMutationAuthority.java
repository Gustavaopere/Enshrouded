package com.gustavaopere.enshrouded.protection;

import com.gustavaopere.enshrouded.api.shroud.FlameWardQuery;
import com.gustavaopere.enshrouded.api.shroud.MutationAuthority;
import com.gustavaopere.enshrouded.api.shroud.MutationKind;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import java.util.Objects;

/**
 * Fail-closed Stage-02 terrain gate. Behavioral policy is introduced through observed REDs after
 * this boundary exists; the initial implementation deliberately grants no mutation authority.
 */
public final class DefaultMutationAuthority implements MutationAuthority {
    private final MutationSafetyMode mode;
    private final FlameWardQuery wardQuery;
    private final ProtectedAreaService protectedAreas;
    private final boolean allowIndeterminateProtection;
    private final boolean allowBlockEntityMutation;

    public DefaultMutationAuthority(
            MutationSafetyMode mode,
            FlameWardQuery wardQuery,
            ProtectedAreaService protectedAreas,
            boolean allowIndeterminateProtection,
            boolean allowBlockEntityMutation) {
        this.mode = Objects.requireNonNull(mode, "mode");
        this.wardQuery = Objects.requireNonNull(wardQuery, "wardQuery");
        this.protectedAreas = Objects.requireNonNull(protectedAreas, "protectedAreas");
        this.allowIndeterminateProtection = allowIndeterminateProtection;
        this.allowBlockEntityMutation = allowBlockEntityMutation;
    }

    @Override
    public boolean canMutate(ServerLevel level, BlockPos pos, MutationKind kind) {
        Objects.requireNonNull(level, "level");
        Objects.requireNonNull(pos, "pos");
        Objects.requireNonNull(kind, "kind");
        return false;
    }
}
