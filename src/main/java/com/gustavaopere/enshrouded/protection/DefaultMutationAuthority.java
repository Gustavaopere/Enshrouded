package com.gustavaopere.enshrouded.protection;

import com.gustavaopere.enshrouded.api.shroud.FlameWardQuery;
import com.gustavaopere.enshrouded.api.shroud.MutationAuthority;
import com.gustavaopere.enshrouded.api.shroud.MutationKind;
import com.gustavaopere.enshrouded.config.EnshroudedConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;

/** Fail-closed Stage-02 terrain gate consumed by every world mutation sink. */
public final class DefaultMutationAuthority implements MutationAuthority {
    private final MutationSafetyPolicy policy;
    private final FlameWardQuery wardQuery;
    private final ProtectedAreaService protectedAreas;

    public DefaultMutationAuthority(
            MutationSafetyMode mode,
            FlameWardQuery wardQuery,
            ProtectedAreaService protectedAreas,
            boolean allowIndeterminateProtection,
            boolean allowBlockEntityMutation) {
        this.policy = new MutationSafetyPolicy(
                Objects.requireNonNull(mode, "mode"),
                allowIndeterminateProtection,
                allowBlockEntityMutation
        );
        this.wardQuery = Objects.requireNonNull(wardQuery, "wardQuery");
        this.protectedAreas = Objects.requireNonNull(protectedAreas, "protectedAreas");
    }

    public static DefaultMutationAuthority fromConfig(
            FlameWardQuery wardQuery,
            ProtectedAreaService protectedAreas) {
        return new DefaultMutationAuthority(
                EnshroudedConfig.terrainMutationMode(),
                wardQuery,
                protectedAreas,
                EnshroudedConfig.terrainAllowIndeterminateProtection(),
                EnshroudedConfig.terrainAllowBlockEntityMutation()
        );
    }

    @Override
    public boolean canMutate(ServerLevel level, BlockPos pos, MutationKind kind) {
        Objects.requireNonNull(level, "level");
        Objects.requireNonNull(pos, "pos");
        Objects.requireNonNull(kind, "kind");

        BlockState state = level.getBlockState(pos);
        ProtectionDecision protection = Objects.requireNonNull(
                protectedAreas.protectionAt(level, pos, kind),
                "protectedAreas.protectionAt(...)"
        );
        boolean warded = wardQuery.suppresses(level, pos);
        boolean blockEntity = level.getBlockEntity(pos) != null;
        boolean safeTagged = state.is(TerrainSafetyTags.CORRUPTIBLE_SAFE);
        boolean aggressiveTagged = state.is(TerrainSafetyTags.CORRUPTIBLE_AGGRESSIVE);
        boolean replaceable = state.canBeReplaced();

        return policy.allows(
                kind,
                protection,
                warded,
                blockEntity,
                safeTagged,
                aggressiveTagged,
                replaceable
        );
    }
}
