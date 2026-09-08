package com.gustavaopere.enshrouded.flame.altar;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Ephemeral subscription index for Flame Altar formation recovery.
 *
 * <p>This is not gameplay authority and is never persisted. It only remembers which already-loaded
 * controller is waiting for which missing chunk so {@code ChunkEvent.Load} can wake exactly the
 * affected altar instead of scanning unrelated chunks.</p>
 */
final class FlameAltarRecoveryIndex {
    private final Map<ChunkPos, Set<BlockPos>> altarsByMissingChunk = new HashMap<>();
    private final Map<BlockPos, ChunkPos> missingChunkByAltar = new HashMap<>();

    void waitFor(BlockPos altarPos, ChunkPos missingChunk) {
        BlockPos immutableAltarPos = altarPos.immutable();
        ChunkPos previous = missingChunkByAltar.put(immutableAltarPos, missingChunk);
        if (previous != null && !previous.equals(missingChunk)) {
            removeFromChunk(previous, immutableAltarPos);
        }
        altarsByMissingChunk
                .computeIfAbsent(missingChunk, ignored -> new HashSet<>())
                .add(immutableAltarPos);
    }

    Set<BlockPos> consume(ChunkPos loadedChunk) {
        Set<BlockPos> waiting = altarsByMissingChunk.remove(loadedChunk);
        if (waiting == null || waiting.isEmpty()) {
            return Set.of();
        }

        Set<BlockPos> snapshot = Set.copyOf(waiting);
        for (BlockPos altarPos : snapshot) {
            missingChunkByAltar.remove(altarPos, loadedChunk);
        }
        return snapshot;
    }

    void remove(BlockPos altarPos) {
        BlockPos immutableAltarPos = altarPos.immutable();
        ChunkPos missingChunk = missingChunkByAltar.remove(immutableAltarPos);
        if (missingChunk != null) {
            removeFromChunk(missingChunk, immutableAltarPos);
        }
    }

    boolean isEmpty() {
        return missingChunkByAltar.isEmpty() && altarsByMissingChunk.isEmpty();
    }

    private void removeFromChunk(ChunkPos chunkPos, BlockPos altarPos) {
        Set<BlockPos> waiting = altarsByMissingChunk.get(chunkPos);
        if (waiting == null) {
            return;
        }
        waiting.remove(altarPos);
        if (waiting.isEmpty()) {
            altarsByMissingChunk.remove(chunkPos);
        }
    }
}
