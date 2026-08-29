package com.gustavaopere.enshrouded.shroud.terrain;

import com.gustavaopere.enshrouded.api.shroud.MutationAuthority;
import com.gustavaopere.enshrouded.api.shroud.MutationKind;
import com.gustavaopere.enshrouded.api.shroud.ShroudQuery;
import com.gustavaopere.enshrouded.api.shroud.ShroudSample;
import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;
import com.gustavaopere.enshrouded.protection.MutationSafetyMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/** Bounded loaded-world projection of the canonical logical Shroud field. */
public final class ShroudMaterializationService {
    private final CorruptionRuleRegistry rules;
    private final MutationAuthority mutationAuthority;
    private final ShroudQuery shroudQuery;
    private final MutationSafetyMode safetyMode;
    private final MaterializationWorkQueue queue;
    private final GrowthPlacementService growthPlacementService;
    private final int growthQueueCapacity;
    private final ArrayDeque<GrowthPlacementJob> growthQueue = new ArrayDeque<>();
    private final Set<BlockPos> queuedGrowthSupports = new HashSet<>();

    public ShroudMaterializationService(
            CorruptionRuleRegistry rules,
            MutationAuthority mutationAuthority,
            ShroudQuery shroudQuery,
            MutationSafetyMode safetyMode,
            int queueCapacity) {
        this.rules = Objects.requireNonNull(rules, "rules");
        this.mutationAuthority = Objects.requireNonNull(mutationAuthority, "mutationAuthority");
        this.shroudQuery = Objects.requireNonNull(shroudQuery, "shroudQuery");
        this.safetyMode = Objects.requireNonNull(safetyMode, "safetyMode");
        this.queue = new MaterializationWorkQueue(queueCapacity);
        this.growthPlacementService = new GrowthPlacementService(mutationAuthority, shroudQuery);
        this.growthQueueCapacity = queueCapacity;
    }

    /**
     * Samples one already-loaded world position and queues the first matching enabled rule.
     * The method never forces a chunk load and never mutates the world directly.
     */
    public boolean schedule(ServerLevel level, BlockPos pos, ShroudSample sample) {
        Objects.requireNonNull(level, "level");
        Objects.requireNonNull(pos, "pos");
        Objects.requireNonNull(sample, "sample");

        BlockPos immutablePos = pos.immutable();
        if (!level.hasChunkAt(immutablePos)) {
            return false;
        }

        BlockState sourceState = level.getBlockState(immutablePos);
        ResourceLocation sourceBlockId = BuiltInRegistries.BLOCK.getKey(sourceState.getBlock());
        for (CorruptionRule rule : rules.all()) {
            if (!ruleEnabled(rule) || !rule.appliesTo(sample)) {
                continue;
            }
            TagKey<Block> sourceTag = TagKey.create(Registries.BLOCK, rule.sourceTag());
            if (!sourceState.is(sourceTag)) {
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

    /**
     * Queues a decorative growth request for an already-loaded support position.
     * Logical Shroud state is deliberately not captured here; it is re-sampled at apply time.
     */
    public boolean scheduleGrowth(
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
        if (!Float.isFinite(maximumDensity) || maximumDensity < 0.0F || maximumDensity > 1.0F) {
            throw new IllegalArgumentException("maximumDensity must be finite and within [0, 1]");
        }

        BlockPos immutableSupport = supportPos.immutable();
        BlockPos targetPos = immutableSupport.above();
        if (!level.hasChunkAt(immutableSupport) || !level.hasChunkAt(targetPos)) {
            return false;
        }
        if (growthQueue.size() >= growthQueueCapacity || !queuedGrowthSupports.add(immutableSupport)) {
            return false;
        }

        growthQueue.addLast(new GrowthPlacementJob(
                immutableSupport,
                growthBlock,
                minimumSeverity,
                maximumDensity,
                salt
        ));
        return true;
    }

    /**
     * Applies bounded growth-placement work without forcing chunks. Budgets count attempted jobs,
     * so stale or denied candidates cannot consume unbounded server time by retrying forever.
     */
    public int tickGrowths(ServerLevel level, int globalBudget, int perChunkBudget) {
        Objects.requireNonNull(level, "level");
        if (globalBudget < 0 || perChunkBudget < 0) {
            throw new IllegalArgumentException("budgets must be non-negative");
        }
        if (globalBudget == 0 || perChunkBudget == 0 || growthQueue.isEmpty()) {
            return 0;
        }

        int candidates = growthQueue.size();
        int attempted = 0;
        int placements = 0;
        Map<Long, Integer> attemptedPerChunk = new HashMap<>();

        for (int i = 0; i < candidates; i++) {
            GrowthPlacementJob job = growthQueue.removeFirst();
            long chunkKey = ChunkPos.asLong(job.supportPos());
            int chunkCount = attemptedPerChunk.getOrDefault(chunkKey, 0);
            BlockPos targetPos = job.supportPos().above();
            boolean loaded = level.hasChunkAt(job.supportPos()) && level.hasChunkAt(targetPos);

            if (attempted < globalBudget && loaded && chunkCount < perChunkBudget) {
                queuedGrowthSupports.remove(job.supportPos());
                attempted++;
                attemptedPerChunk.put(chunkKey, chunkCount + 1);
                if (growthPlacementService.tryPlaceOnTop(
                        level,
                        job.supportPos(),
                        job.growthBlock(),
                        job.minimumSeverity(),
                        job.maximumDensity(),
                        job.salt())) {
                    placements++;
                }
            } else {
                growthQueue.addLast(job);
            }
        }

        return placements;
    }

    public int pendingGrowthWork() {
        return growthQueue.size();
    }

    private boolean apply(ServerLevel level, ShroudMutationJob job) {
        BlockPos pos = job.pos();
        if (!level.hasChunkAt(pos)) {
            return false;
        }

        CorruptionRule rule = rules.rule(job.ruleId()).orElse(null);
        if (rule == null || !ruleEnabled(rule)) {
            return false;
        }

        ShroudSample currentSample;
        try {
            currentSample = shroudQuery.sample(level, pos, null);
        } catch (RuntimeException failure) {
            return false;
        }
        if (currentSample == null || !rule.appliesTo(currentSample)) {
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

    private boolean ruleEnabled(CorruptionRule rule) {
        return rule.safetyClass() != CorruptionSafetyClass.AGGRESSIVE
                || safetyMode == MutationSafetyMode.AGGRESSIVE;
    }

    private record GrowthPlacementJob(
            BlockPos supportPos,
            Block growthBlock,
            ShroudSeverity minimumSeverity,
            float maximumDensity,
            long salt) {
    }
}
