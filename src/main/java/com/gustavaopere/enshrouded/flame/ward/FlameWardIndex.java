package com.gustavaopere.enshrouded.flame.ward;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Dimension-local chunk-bucket index for loaded Flame wards.
 *
 * <p>Each ward is indexed into only the chunks intersecting its horizontal radius. Queries inspect
 * the current position's bucket and then apply exact three-dimensional radius semantics, so query
 * cost is independent of the total number of loaded altars.</p>
 */
public final class FlameWardIndex {
    private static final int CHUNK_SIZE = 16;

    private final Map<ResourceKey<Level>, DimensionIndex> dimensions = new HashMap<>();

    public synchronized void activate(ResourceKey<Level> dimension, FlameWardState state) {
        Objects.requireNonNull(dimension, "dimension");
        Objects.requireNonNull(state, "state");
        DimensionIndex index = dimensions.computeIfAbsent(dimension, ignored -> new DimensionIndex());
        FlameWardState previous = index.states.put(state.center(), state);
        if (previous != null) {
            removeBuckets(index, previous);
        }
        addBuckets(index, state);
    }

    public synchronized boolean deactivate(ResourceKey<Level> dimension, BlockPos center) {
        Objects.requireNonNull(dimension, "dimension");
        Objects.requireNonNull(center, "center");
        DimensionIndex index = dimensions.get(dimension);
        if (index == null) {
            return false;
        }
        FlameWardState removed = index.states.remove(center);
        if (removed == null) {
            return false;
        }
        removeBuckets(index, removed);
        if (index.states.isEmpty()) {
            dimensions.remove(dimension);
        }
        return true;
    }

    public synchronized boolean suppresses(ResourceKey<Level> dimension, BlockPos pos) {
        Objects.requireNonNull(dimension, "dimension");
        Objects.requireNonNull(pos, "pos");
        DimensionIndex index = dimensions.get(dimension);
        if (index == null) {
            return false;
        }
        Set<FlameWardState> candidates = index.buckets.get(new ChunkPos(pos).toLong());
        if (candidates == null || candidates.isEmpty()) {
            return false;
        }
        for (FlameWardState state : candidates) {
            if (withinRadius(state, pos)) {
                return true;
            }
        }
        return false;
    }

    public synchronized int activeWardCount(ResourceKey<Level> dimension) {
        DimensionIndex index = dimensions.get(Objects.requireNonNull(dimension, "dimension"));
        return index == null ? 0 : index.states.size();
    }

    public synchronized void clear() {
        dimensions.clear();
    }

    private static boolean withinRadius(FlameWardState state, BlockPos pos) {
        long dx = (long) pos.getX() - state.center().getX();
        long dy = (long) pos.getY() - state.center().getY();
        long dz = (long) pos.getZ() - state.center().getZ();
        long radiusSquared = (long) state.radius() * state.radius();
        return dx * dx + dy * dy + dz * dz <= radiusSquared;
    }

    private static void addBuckets(DimensionIndex index, FlameWardState state) {
        forEachCoveredChunk(state, chunkKey ->
                index.buckets.computeIfAbsent(chunkKey, ignored -> new HashSet<>()).add(state));
    }

    private static void removeBuckets(DimensionIndex index, FlameWardState state) {
        forEachCoveredChunk(state, chunkKey -> {
            Set<FlameWardState> bucket = index.buckets.get(chunkKey);
            if (bucket == null) {
                return;
            }
            bucket.remove(state);
            if (bucket.isEmpty()) {
                index.buckets.remove(chunkKey);
            }
        });
    }

    private static void forEachCoveredChunk(FlameWardState state, java.util.function.LongConsumer consumer) {
        int minChunkX = Math.floorDiv(state.center().getX() - state.radius(), CHUNK_SIZE);
        int maxChunkX = Math.floorDiv(state.center().getX() + state.radius(), CHUNK_SIZE);
        int minChunkZ = Math.floorDiv(state.center().getZ() - state.radius(), CHUNK_SIZE);
        int maxChunkZ = Math.floorDiv(state.center().getZ() + state.radius(), CHUNK_SIZE);
        for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
            for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
                consumer.accept(new ChunkPos(chunkX, chunkZ).toLong());
            }
        }
    }

    private static final class DimensionIndex {
        private final Map<BlockPos, FlameWardState> states = new HashMap<>();
        private final Map<Long, Set<FlameWardState>> buckets = new HashMap<>();
    }
}
