package com.gustavaopere.enshrouded.flame.altar;

import com.gustavaopere.enshrouded.api.shroud.MutationKind;
import com.gustavaopere.enshrouded.protection.ProtectedAreaService;
import com.gustavaopere.enshrouded.protection.ProtectionDecision;
import com.gustavaopere.enshrouded.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;

/**
 * Bounded, read-only validator for the canonical 3x3 Flame Altar complex.
 *
 * <p>The validator never mutates formation state, never activates Sanctuary, and never force-loads
 * chunks. It inspects only the controller plus the eight required shell positions.</p>
 */
public final class FlameAltarStructureValidator {
    public enum Status {
        VALID,
        REQUIRED_CHUNK_UNLOADED,
        MISSING_COMPONENT,
        WRONG_ORIENTATION,
        DUPLICATE_CONTROLLER,
        PROTECTED,
        PROTECTION_INDETERMINATE
    }

    @FunctionalInterface
    public interface ChunkAvailability {
        boolean isLoaded(ServerLevel level, BlockPos pos);
    }

    public record Result(Status status, BlockPos problemPos) {
        public Result {
            Objects.requireNonNull(status, "status");
            Objects.requireNonNull(problemPos, "problemPos");
        }
    }

    private static final CardinalRequirement[] CARDINALS = {
            new CardinalRequirement(0, -1, Direction.SOUTH),
            new CardinalRequirement(1, 0, Direction.WEST),
            new CardinalRequirement(0, 1, Direction.NORTH),
            new CardinalRequirement(-1, 0, Direction.EAST)
    };
    private static final Offset[] CORNERS = {
            new Offset(-1, -1),
            new Offset(1, -1),
            new Offset(-1, 1),
            new Offset(1, 1)
    };
    private static final Offset[] FOOTPRINT = {
            new Offset(0, 0),
            new Offset(0, -1),
            new Offset(1, 0),
            new Offset(0, 1),
            new Offset(-1, 0),
            new Offset(-1, -1),
            new Offset(1, -1),
            new Offset(-1, 1),
            new Offset(1, 1)
    };

    private final ProtectedAreaService protectedAreas;
    private final ChunkAvailability chunks;

    public FlameAltarStructureValidator(ProtectedAreaService protectedAreas) {
        this(
                protectedAreas,
                (level, pos) -> level.getChunkSource().hasChunk(pos.getX() >> 4, pos.getZ() >> 4)
        );
    }

    public FlameAltarStructureValidator(ProtectedAreaService protectedAreas, ChunkAvailability chunks) {
        this.protectedAreas = Objects.requireNonNull(protectedAreas, "protectedAreas");
        this.chunks = Objects.requireNonNull(chunks, "chunks");
    }

    public Result validate(ServerLevel level, BlockPos controllerPos) {
        Objects.requireNonNull(level, "level");
        Objects.requireNonNull(controllerPos, "controllerPos");

        for (Offset offset : FOOTPRINT) {
            BlockPos pos = controllerPos.offset(offset.x(), 0, offset.z());
            if (!chunks.isLoaded(level, pos)) {
                return new Result(Status.REQUIRED_CHUNK_UNLOADED, pos);
            }
        }

        for (Offset offset : FOOTPRINT) {
            BlockPos pos = controllerPos.offset(offset.x(), 0, offset.z());
            ProtectionDecision protection = protectedAreas.protectionAt(level, pos, MutationKind.RITUAL_STRUCTURE);
            if (protection == ProtectionDecision.PROTECTED) {
                return new Result(Status.PROTECTED, pos);
            }
            if (protection == ProtectionDecision.INDETERMINATE) {
                return new Result(Status.PROTECTION_INDETERMINATE, pos);
            }
        }

        BlockState controllerState = level.getBlockState(controllerPos);
        if (!controllerState.is(ModBlocks.FLAME_ALTAR.get())) {
            return new Result(Status.MISSING_COMPONENT, controllerPos);
        }

        for (CardinalRequirement requirement : CARDINALS) {
            BlockPos pos = controllerPos.offset(requirement.x(), 0, requirement.z());
            BlockState state = level.getBlockState(pos);
            if (state.is(ModBlocks.FLAME_ALTAR.get())) {
                return new Result(Status.DUPLICATE_CONTROLLER, pos);
            }
            if (!state.is(ModBlocks.FLAME_ALTAR_BRACE.get())) {
                return new Result(Status.MISSING_COMPONENT, pos);
            }
            if (state.getValue(FlameAltarBraceBlock.FACING) != requirement.facingTowardController()) {
                return new Result(Status.WRONG_ORIENTATION, pos);
            }
        }

        for (Offset offset : CORNERS) {
            BlockPos pos = controllerPos.offset(offset.x(), 0, offset.z());
            BlockState state = level.getBlockState(pos);
            if (state.is(ModBlocks.FLAME_ALTAR.get())) {
                return new Result(Status.DUPLICATE_CONTROLLER, pos);
            }
            if (!state.is(ModBlocks.FLAME_ALTAR_RUNE.get())) {
                return new Result(Status.MISSING_COMPONENT, pos);
            }
        }

        return new Result(Status.VALID, controllerPos);
    }

    private record Offset(int x, int z) {
    }

    private record CardinalRequirement(int x, int z, Direction facingTowardController) {
    }
}
