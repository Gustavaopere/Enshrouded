package com.gustavaopere.enshrouded.shroud.terrain;

import com.gustavaopere.enshrouded.api.shroud.MutationAuthority;
import com.gustavaopere.enshrouded.api.shroud.MutationKind;
import com.gustavaopere.enshrouded.api.shroud.ShroudQuery;
import com.gustavaopere.enshrouded.api.shroud.ShroudSample;
import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;

/** Places decorative Shroud growths without owning spread state or bypassing terrain safety. */
public final class GrowthPlacementService {
    private final MutationAuthority mutationAuthority;
    private final ShroudQuery shroudQuery;

    public GrowthPlacementService(MutationAuthority mutationAuthority, ShroudQuery shroudQuery) {
        this.mutationAuthority = Objects.requireNonNull(mutationAuthority, "mutationAuthority");
        this.shroudQuery = Objects.requireNonNull(shroudQuery, "shroudQuery");
    }

    public boolean tryPlaceOnTop(
            ServerLevel level,
            BlockPos supportPos,
            Block growthBlock,
            ShroudSeverity minimumSeverity,
            float maximumDensity,
            long salt) {
        Objects.requireNonNull(level, "level");
        Objects.requireNonNull(supportPos, "supportPos");
        Objects.requireNonNull(growthBlock, "growthBlock");
        Objects.requireNonNull(minimumSeverity, "minimumSeverity");

        BlockPos targetPos = supportPos.above();
        if (!level.hasChunkAt(supportPos) || !level.hasChunkAt(targetPos)) {
            return false;
        }

        BlockState supportState = level.getBlockState(supportPos);
        BlockState targetState = level.getBlockState(targetPos);
        if (!supportState.isFaceSturdy(level, supportPos, Direction.UP) || !targetState.canBeReplaced()) {
            return false;
        }

        ShroudSample sample;
        try {
            sample = shroudQuery.sample(level, targetPos, null);
        } catch (RuntimeException exception) {
            return false;
        }
        if (sample == null
                || sample.sanctuarySuppressed()
                || sample.intensity() <= 0.0F
                || severityRank(sample.severity()) < severityRank(minimumSeverity)
                || !GrowthCandidateSampler.shouldPlace(
                        level.getSeed(),
                        targetPos.asLong(),
                        sample.intensity(),
                        maximumDensity,
                        salt)) {
            return false;
        }

        try {
            if (!mutationAuthority.canMutate(level, supportPos, MutationKind.CORRUPTION)
                    || !mutationAuthority.canMutate(level, targetPos, MutationKind.GROWTH_PLACEMENT)) {
                return false;
            }
        } catch (RuntimeException exception) {
            return false;
        }

        return level.setBlock(targetPos, growthBlock.defaultBlockState(), Block.UPDATE_CLIENTS);
    }

    MutationAuthority mutationAuthority() {
        return mutationAuthority;
    }

    ShroudQuery shroudQuery() {
        return shroudQuery;
    }

    private static int severityRank(ShroudSeverity severity) {
        return switch (severity) {
            case CLEAR -> 0;
            case SHROUD -> 1;
            case DEADLY -> 2;
        };
    }
}
