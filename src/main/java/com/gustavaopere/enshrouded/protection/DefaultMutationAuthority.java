package com.gustavaopere.enshrouded.protection;

import com.gustavaopere.enshrouded.api.shroud.FlameWardQuery;
import com.gustavaopere.enshrouded.api.shroud.MutationAuthority;
import com.gustavaopere.enshrouded.api.shroud.MutationKind;
import com.gustavaopere.enshrouded.config.EnshroudedConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/** Fail-closed Stage-02 terrain gate consumed by every world mutation sink. */
public final class DefaultMutationAuthority implements MutationAuthority {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMutationAuthority.class);

    private final MutationSafetyPolicy policy;
    private final FlameWardQuery wardQuery;
    private final ProtectedAreaService protectedAreas;
    private final AtomicBoolean protectionFailureLogged = new AtomicBoolean();
    private final AtomicBoolean wardFailureLogged = new AtomicBoolean();

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

    public static DefaultMutationAuthority fromConfig(FlameWardQuery wardQuery) {
        return fromConfig(wardQuery, ProtectionRuntimeBindings.protectedAreas());
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
        ProtectionDecision protection = protectionAt(level, pos, kind);
        boolean warded = wardedAt(level, pos);
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

    private ProtectionDecision protectionAt(ServerLevel level, BlockPos pos, MutationKind kind) {
        try {
            ProtectionDecision decision = protectedAreas.protectionAt(level, pos, kind);
            if (decision != null) {
                return decision;
            }
            logProtectionFailureOnce("Protection adapter returned null; treating the result as INDETERMINATE.", null);
        } catch (RuntimeException failure) {
            logProtectionFailureOnce("Protection adapter query failed; treating the result as INDETERMINATE.", failure);
        }
        return ProtectionDecision.INDETERMINATE;
    }

    private boolean wardedAt(ServerLevel level, BlockPos pos) {
        try {
            return wardQuery.suppresses(level, pos);
        } catch (RuntimeException failure) {
            if (wardFailureLogged.compareAndSet(false, true)) {
                LOGGER.warn("Flame ward query failed; threat-introducing mutations will fail closed.", failure);
            }
            return true;
        }
    }

    private void logProtectionFailureOnce(String message, RuntimeException failure) {
        if (!protectionFailureLogged.compareAndSet(false, true)) {
            return;
        }
        if (failure == null) {
            LOGGER.warn(message);
        } else {
            LOGGER.warn(message, failure);
        }
    }
}
