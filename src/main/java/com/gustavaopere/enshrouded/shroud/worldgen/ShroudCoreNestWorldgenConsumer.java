package com.gustavaopere.enshrouded.shroud.worldgen;

import com.gustavaopere.enshrouded.api.shroud.MutationAuthority;
import com.gustavaopere.enshrouded.api.shroud.MutationKind;
import com.gustavaopere.enshrouded.presentation.setpiece.ShroudCoreNestLayout;
import com.gustavaopere.enshrouded.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.Objects;

/**
 * Bounded production consumer for the Stage 10 Shroud Core Nest composition contract.
 *
 * <p>The authoritative core is placed and registered by {@link ShroudCoreFeature}; this class only
 * projects non-controller set-piece roles into already-writable worldgen positions. It never reads
 * or writes canonical Shroud SavedData, never force-loads chunks, and never turns a decorative
 * failure into a second core lifecycle.</p>
 */
final class ShroudCoreNestWorldgenConsumer {
    private final MutationAuthority mutationAuthority;

    ShroudCoreNestWorldgenConsumer(MutationAuthority mutationAuthority) {
        this.mutationAuthority = Objects.requireNonNull(mutationAuthority, "mutationAuthority");
    }

    int placeOrdinaryNest(WorldGenLevel level, BlockPos corePos) {
        Objects.requireNonNull(level, "level");
        Objects.requireNonNull(corePos, "corePos");

        int placed = 0;
        for (ShroudCoreNestLayout.Part part : ShroudCoreNestLayout.ordinary().parts()) {
            if (part.role() == ShroudCoreNestLayout.Role.CORE_ANCHOR) {
                continue;
            }

            BlockPos target = resolveTarget(level, corePos, part);
            if (!level.ensureCanWrite(target)) {
                continue;
            }
            if (!mutationAuthority.canMutate(level.getLevel(), target, MutationKind.GROWTH_PLACEMENT)) {
                continue;
            }

            BlockState state = stateFor(materialFor(part.role()));
            if (level.setBlock(target, state, Block.UPDATE_CLIENTS)) {
                placed++;
            }
        }
        return placed;
    }

    private static BlockPos resolveTarget(
            WorldGenLevel level,
            BlockPos corePos,
            ShroudCoreNestLayout.Part part) {
        int x = corePos.getX() + part.offset().getX();
        int z = corePos.getZ() + part.offset().getZ();
        int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
        int verticalOffset = resolvedVerticalOffset(part.role(), part.offset().getY());
        return new BlockPos(x, surfaceY + verticalOffset, z);
    }

    static int resolvedVerticalOffset(ShroudCoreNestLayout.Role role, int authoredOffsetY) {
        Objects.requireNonNull(role, "role");
        if (role == ShroudCoreNestLayout.Role.CORE_ANCHOR) {
            return authoredOffsetY;
        }
        if (role == ShroudCoreNestLayout.Role.SLUDGE_BASIN) {
            return 0;
        }
        return Math.max(0, authoredOffsetY);
    }

    static Material materialFor(ShroudCoreNestLayout.Role role) {
        Objects.requireNonNull(role, "role");
        return switch (role) {
            case CORE_ANCHOR -> throw new IllegalArgumentException("core anchor is owned by ShroudCoreFeature");
            case RIB -> Material.ACTIVE_GROWTH;
            case ROOT_VEIN -> Material.VEIN;
            case SLUDGE_BASIN -> Material.RED_SLUDGE;
            case HANGING_GROWTH -> Material.ACTIVE_GROWTH;
            case RUIN -> Material.WITHERED_GROWTH;
        };
    }

    private static BlockState stateFor(Material material) {
        return switch (material) {
            case ACTIVE_GROWTH -> ModBlocks.SHROUD_GROWTH.get().defaultBlockState();
            case VEIN -> ModBlocks.SHROUD_VEIN.get().defaultBlockState();
            case RED_SLUDGE -> ModBlocks.RED_SLUDGE.get().defaultBlockState();
            case WITHERED_GROWTH -> ModBlocks.WITHERED_GROWTH.get().defaultBlockState();
        };
    }

    enum Material {
        ACTIVE_GROWTH,
        VEIN,
        RED_SLUDGE,
        WITHERED_GROWTH
    }
}
