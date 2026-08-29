package com.gustavaopere.enshrouded.shroud.terrain;

import net.minecraft.world.level.ChunkPos;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.LongPredicate;

/** Bounded FIFO that applies global and per-chunk work budgets without forcing chunk loads. */
public final class MaterializationWorkQueue {
    private final int capacity;
    private final ArrayDeque<ShroudMutationJob> queue = new ArrayDeque<>();
    private final Set<net.minecraft.core.BlockPos> queuedPositions = new HashSet<>();

    public MaterializationWorkQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be positive");
        }
        this.capacity = capacity;
    }

    public boolean enqueue(ShroudMutationJob job) {
        Objects.requireNonNull(job, "job");
        if (queue.size() >= capacity || !queuedPositions.add(job.pos())) {
            return false;
        }
        queue.addLast(job);
        return true;
    }

    public List<ShroudMutationJob> pollBudgeted(int globalBudget, int perChunkBudget, LongPredicate loadedChunk) {
        if (globalBudget < 0 || perChunkBudget < 0) {
            throw new IllegalArgumentException("budgets must be non-negative");
        }
        Objects.requireNonNull(loadedChunk, "loadedChunk");
        if (globalBudget == 0 || perChunkBudget == 0 || queue.isEmpty()) {
            return List.of();
        }

        int candidates = queue.size();
        List<ShroudMutationJob> selected = new ArrayList<>(Math.min(globalBudget, candidates));
        Map<Long, Integer> selectedPerChunk = new HashMap<>();

        for (int i = 0; i < candidates; i++) {
            ShroudMutationJob job = queue.removeFirst();
            long chunkKey = ChunkPos.asLong(job.pos());
            int chunkCount = selectedPerChunk.getOrDefault(chunkKey, 0);

            if (selected.size() < globalBudget && loadedChunk.test(chunkKey) && chunkCount < perChunkBudget) {
                selected.add(job);
                selectedPerChunk.put(chunkKey, chunkCount + 1);
                queuedPositions.remove(job.pos());
            } else {
                queue.addLast(job);
            }
        }

        return List.copyOf(selected);
    }

    public int size() {
        return queue.size();
    }

    public int capacity() {
        return capacity;
    }
}
