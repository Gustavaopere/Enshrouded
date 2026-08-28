package com.gustavaopere.enshrouded.shroud.worldgen;

import com.gustavaopere.enshrouded.registry.ModBlocks;
import com.gustavaopere.enshrouded.shroud.core.ShroudCoreBlockEntity;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.Objects;

public final class ShroudCoreFeature extends Feature<NoneFeatureConfiguration> {
    private final ShroudCoreCandidateField candidates;

    public ShroudCoreFeature(Codec<NoneFeatureConfiguration> codec, ShroudCoreCandidateField candidates) {
        super(codec);
        this.candidates = Objects.requireNonNull(candidates, "candidates");
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        if (!candidates.supportsDimension(level.getLevel().dimension())) {
            return false;
        }

        ChunkPos currentChunk = new ChunkPos(context.origin());
        int cellX = Math.floorDiv(currentChunk.getMinBlockX(), candidates.cellSizeBlocks());
        int cellZ = Math.floorDiv(currentChunk.getMinBlockZ(), candidates.cellSizeBlocks());
        ShroudCoreCandidateField.Candidate candidate = candidates.candidate(level.getSeed(), cellX, cellZ);

        ChunkPos candidateChunk = new ChunkPos(new BlockPos(candidate.blockX(), 0, candidate.blockZ()));
        if (!candidateChunk.equals(currentChunk)) {
            return false;
        }

        int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, candidate.blockX(), candidate.blockZ());
        BlockPos corePos = new BlockPos(candidate.blockX(), surfaceY, candidate.blockZ());
        if (!level.ensureCanWrite(corePos)) {
            return false;
        }
        if (!level.getBlockState(corePos).canBeReplaced()) {
            return false;
        }
        if (!Block.canSupportRigidBlock(level, corePos.below())) {
            return false;
        }

        if (!level.setBlock(corePos, ModBlocks.SHROUD_CORE.get().defaultBlockState(), Block.UPDATE_CLIENTS)) {
            return false;
        }
        if (level.getBlockEntity(corePos) instanceof ShroudCoreBlockEntity coreBlockEntity) {
            coreBlockEntity.requestAutomaticActivation();
        }
        return true;
    }
}
