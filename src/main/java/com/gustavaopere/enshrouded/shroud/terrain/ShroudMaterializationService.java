package com.gustavaopere.enshrouded.shroud.terrain;

import com.gustavaopere.enshrouded.api.shroud.MutationAuthority;
import com.gustavaopere.enshrouded.api.shroud.MutationKind;
import com.gustavaopere.enshrouded.api.shroud.ShroudQuery;
import com.gustavaopere.enshrouded.api.shroud.ShroudSample;
import com.gustavaopere.enshrouded.protection.TerrainSafetyTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Objects;

/** Bounded loaded-world projection of the canonical logical Shroud field. */
public final class ShroudMaterializationService {
    private final CorruptionRuleRegistry rules;
    private final MutationAuthority mutationAuthority;
    private final ShroudQuery shroudQuery;
    private final MaterializationWorkQueue queue;

    public ShroudMaterializationService(
            CorruptionRuleRegistry rules,
            MutationAuthority mutationAuthority,
            ShroudQuery shroudQuery,
            int queueCapacity) {
        this.rules = Objects.requireNonNull(rules, "rules");
        this.mutationAuthority = Objects.requireNonNull(mutationAuthority, "mutationAuthority");
        this.shroudQuery = Objects.requireNonNull(shroudQuery, "shroudQuery");
        this.queue = new MaterializationWorkQueue(queueCapacity);
    }

    /**
     * Samples one already-loaded world position and queues the first matching rule.
     * The method never forces a chunk load and never mutates the world directly.
     */
    public boolean schedule(ServerLevel level, BlockPos pos, ShroudSample sample) {
        Objects.requireNonNull(level, "level");
        Objects.requireNonNull(pos, "pos");
        Objects.requireNonNull(sample, "sample");

        BlockPos immutablePos = pos.immutable();
        if (sample.sanctuarySuppressed() || sample.intensity() <= 0.0F || !level.hasChunkAt(immutablePos)) {
            return false;
        }

        BlockState sourceState = level.getBlockState(immutablePos);
        ResourceLocation sourceBlockId = BuiltInRegistries.BLOCK.getKey(sourceState.getBlock());
        for (CorruptionRule rule : rules.all()) {
            if (sample.intensity() < rule.minIntensity()) {
                continue;
            }
            TagKey<Block> sourceTag = TagKey.create(Registries.BLOCK, rule.sourceTag());
            if (!sourceState.is(sourceTag) || !matchesSafetyClass(rule, sourceState)) {
                continue;
            }
            return queue.enqueue(new ShroudMutationJob(immutablePos, rule.id(), sourceBlockId));
        }
        return false;
    }

    /** Applies at most the supplied global/per-chunk budgets and returns successful mutations. */
    public int tick(ServerLevel level, int globalBudget, int perChunkBudget) {
        Objects.requireNonNull(level, "level");
        List<ShroudMutationJob> jobs = queue.pollBudgeted(
                globalBudget,
                perChunkBudget,
                chunkKey -> {
                    ChunkPos chunk = new ChunkPos(chunkKey);
                    return level.hasChunk(chunk.x, chunk.z);
                }
        );

        int mutations = 0;
        for (ShroudMutationJob job : jobs) {
            if (apply(level, job)) {
                mutations++;
            }
        }
        return mutations;
    }

    public int pendingWork() {
        return queue.size();
    }

    private boolean apply(ServerLevel level, ShroudMutationJob job) {
        BlockPos pos = job.pos();
        if (!level.hasChunkAt(pos)) {
            return false;
        }

        CorruptionRule rule = rules.rule(job.ruleId()).orElse(null);
        if (rule == null) {
            return false;
        }

        ShroudSample currentSample;
        try {
            currentSample = shroudQuery.sample(level, pos, null);
        } catch (RuntimeException failure) {
            return false;
        }
        if (currentSample == null
                || currentSample.sanctuarySuppressed()
                || currentSample.intensity() <= 0.0F
                || currentSample.intensity() < rule.minIntensity()) {
            return false;
        }

        BlockState sourceState = level.getBlockState(pos);
        ResourceLocation currentBlockId = BuiltInRegistries.BLOCK.getKey(sourceState.getBlock());
        if (!currentBlockId.equals(job.expectedSourceBlock())) {
            return false;
        }
        if (!sourceState.is(TagKey.create(Registries.BLOCK, rule.sourceTag()))) {
            return false;
        }
        if (!matchesSafetyClass(rule, sourceState)) {
            return false;
        }

        if (!mutationAuthority.canMutate(level, pos, MutationKind.CORRUPTION)) {
            return false;
        }

        Block resultBlock = BuiltInRegistries.BLOCK.get(rule.resultBlock());
        if (!BuiltInRegistries.BLOCK.getKey(resultBlock).equals(rule.resultBlock())) {
            return false;
        }
        if (sourceState.is(resultBlock)) {
            return false;
        }
        return level.setBlock(pos, resultBlock.defaultBlockState(), Block.UPDATE_ALL);
    }

    private static boolean matchesSafetyClass(CorruptionRule rule, BlockState sourceState) {
        return switch (rule.safetyClass()) {
            case SAFE -> sourceState.is(TerrainSafetyTags.CORRUPTIBLE_SAFE);
            case AGGRESSIVE -> sourceState.is(TerrainSafetyTags.CORRUPTIBLE_AGGRESSIVE);
        };
    }
}
