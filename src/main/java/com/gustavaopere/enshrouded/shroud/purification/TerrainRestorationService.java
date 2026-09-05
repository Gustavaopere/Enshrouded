package com.gustavaopere.enshrouded.shroud.purification;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.api.shroud.MutationAuthority;
import com.gustavaopere.enshrouded.api.shroud.MutationKind;
import com.gustavaopere.enshrouded.performance.PerformanceCounters;
import com.gustavaopere.enshrouded.shroud.expansion.ShroudGridGeometry;
import com.gustavaopere.enshrouded.shroud.state.ShroudCellPos;
import com.gustavaopere.enshrouded.shroud.terrain.CorruptionRule;
import com.gustavaopere.enshrouded.shroud.terrain.CorruptionRuleRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Bounded best-effort cleanup of visual corruption after a logical cell clears.
 *
 * <p>Cleanup never owns logical Shroud state, never loads chunks, and never overwrites an
 * unrecognized/player-edited block. Every actual world mutation is authorized as
 * {@link MutationKind#PURIFICATION}.</p>
 */
public final class TerrainRestorationService {
    private static final TagKey<Block> SHROUD_GROWTHS = TagKey.create(
            Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath(Enshrouded.MOD_ID, "shroud_growths")
    );

    private final Supplier<CorruptionRuleRegistry> rules;
    private final MutationAuthority mutationAuthority;
    private final ShroudGridGeometry geometry;
    private final int queueCapacity;
    private final ArrayDeque<BlockPos> queue = new ArrayDeque<>();
    private final Set<BlockPos> queuedPositions = new HashSet<>();

    public TerrainRestorationService(
            CorruptionRuleRegistry rules,
            MutationAuthority mutationAuthority,
            ShroudGridGeometry geometry,
            int queueCapacity) {
        this(() -> Objects.requireNonNull(rules, "rules"), mutationAuthority, geometry, queueCapacity);
    }

    public TerrainRestorationService(
            Supplier<CorruptionRuleRegistry> rules,
            MutationAuthority mutationAuthority,
            ShroudGridGeometry geometry,
            int queueCapacity) {
        this.rules = Objects.requireNonNull(rules, "rules");
        this.mutationAuthority = Objects.requireNonNull(mutationAuthority, "mutationAuthority");
        this.geometry = Objects.requireNonNull(geometry, "geometry");
        if (queueCapacity <= 0) {
            throw new IllegalArgumentException("queueCapacity must be > 0");
        }
        this.queueCapacity = queueCapacity;
    }

    /**
     * Captures cleanup candidates from the currently loaded part of a cleared logical cell.
     * Positions that are unloaded or cannot fit in the bounded queue are intentionally left as
     * harmless visual leftovers rather than forcing chunk loads or unbounded memory growth.
     */
    public int scheduleClearedCell(ServerLevel level, ShroudCellPos cell) {
        Objects.requireNonNull(level, "level");
        Objects.requireNonNull(cell, "cell");

        int size = geometry.cellSizeBlocks();
        int minX = Math.multiplyExact(cell.x(), size);
        int minY = Math.multiplyExact(cell.y(), size);
        int minZ = Math.multiplyExact(cell.z(), size);
        int scheduled = 0;
        CorruptionRuleRegistry registry = currentRules();

        for (int dx = 0; dx < size && queue.size() < queueCapacity; dx++) {
            for (int dy = 0; dy < size && queue.size() < queueCapacity; dy++) {
                int y = minY + dy;
                if (y < level.getMinBuildHeight() || y >= level.getMaxBuildHeight()) {
                    continue;
                }
                for (int dz = 0; dz < size && queue.size() < queueCapacity; dz++) {
                    BlockPos pos = new BlockPos(minX + dx, y, minZ + dz);
                    if (!level.hasChunkAt(pos) || !isCleanupCandidate(level.getBlockState(pos), registry)) {
                        continue;
                    }
                    BlockPos immutable = pos.immutable();
                    if (queuedPositions.add(immutable)) {
                        queue.addLast(immutable);
                        scheduled++;
                    }
                }
            }
        }
        return scheduled;
    }

    /** Applies at most the supplied global/per-chunk number of cleanup attempts. */
    public int tick(ServerLevel level, int globalBudget, int perChunkBudget) {
        Objects.requireNonNull(level, "level");
        if (globalBudget < 0 || perChunkBudget < 0) {
            throw new IllegalArgumentException("cleanup budgets must be non-negative");
        }
        if (globalBudget == 0 || perChunkBudget == 0 || queue.isEmpty()) {
            return 0;
        }

        int candidates = queue.size();
        int attempted = 0;
        int mutations = 0;
        Map<Long, Integer> attemptedPerChunk = new HashMap<>();

        for (int index = 0; index < candidates; index++) {
            BlockPos pos = queue.removeFirst();
            long chunkKey = ChunkPos.asLong(pos);
            int chunkCount = attemptedPerChunk.getOrDefault(chunkKey, 0);

            if (attempted < globalBudget && level.hasChunkAt(pos) && chunkCount < perChunkBudget) {
                queuedPositions.remove(pos);
                attempted++;
                attemptedPerChunk.put(chunkKey, chunkCount + 1);
                if (tryRestore(level, pos)) {
                    mutations++;
                }
            } else {
                queue.addLast(pos);
            }
        }
        PerformanceCounters.global().recordRestoration(attempted, mutations);
        return mutations;
    }

    public int pendingWork() {
        return queue.size();
    }

    /**
     * Restores one exact known corrupted block or removes one native Shroud growth. Unknown current
     * states and ambiguous reverse mappings fail closed, which preserves player edits and avoids
     * guessing which original material existed before corruption.
     */
    public boolean tryRestore(ServerLevel level, BlockPos pos) {
        Objects.requireNonNull(level, "level");
        Objects.requireNonNull(pos, "pos");
        if (!level.hasChunkAt(pos)) {
            return false;
        }

        BlockState current = level.getBlockState(pos);
        if (current.is(SHROUD_GROWTHS)) {
            if (!mutationAuthority.canMutate(level, pos, MutationKind.PURIFICATION)) {
                return false;
            }
            return level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        }

        ResourceLocation currentBlockId = BuiltInRegistries.BLOCK.getKey(current.getBlock());
        List<CorruptionRule> matches = currentRules().all().stream()
                .filter(rule -> rule.resultBlock().equals(currentBlockId))
                .toList();
        if (matches.size() != 1) {
            return false;
        }

        CorruptionRule rule = matches.getFirst();
        Block reversal = BuiltInRegistries.BLOCK.get(rule.reversalBlock());
        if (!BuiltInRegistries.BLOCK.getKey(reversal).equals(rule.reversalBlock())) {
            return false;
        }
        if (!mutationAuthority.canMutate(level, pos, MutationKind.PURIFICATION)) {
            return false;
        }
        return level.setBlock(pos, reversal.defaultBlockState(), Block.UPDATE_ALL);
    }

    private static boolean isCleanupCandidate(BlockState state, CorruptionRuleRegistry registry) {
        if (state.is(SHROUD_GROWTHS)) {
            return true;
        }
        ResourceLocation currentBlockId = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        long matches = registry.all().stream().filter(rule -> rule.resultBlock().equals(currentBlockId)).count();
        return matches == 1L;
    }

    private CorruptionRuleRegistry currentRules() {
        return Objects.requireNonNull(rules.get(), "rules supplier result");
    }
}
