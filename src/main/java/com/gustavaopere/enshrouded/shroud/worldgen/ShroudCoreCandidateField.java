package com.gustavaopere.enshrouded.shroud.worldgen;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.Objects;

/**
 * Deterministic coarse-grid candidate generator for automatic Level 1 Shroud core sites.
 *
 * <p>The field is intentionally independent from chunk loading and SavedData. It only maps
 * a world seed and coarse candidate-cell coordinate to one stable block-space candidate.
 * A half-spacing inset on every cell edge guarantees candidates in neighboring cells cannot
 * violate the configured minimum spacing.</p>
 */
public final class ShroudCoreCandidateField {
    private static final long X_SALT = 0x9E3779B97F4A7C15L;
    private static final long Z_SALT = 0xD1B54A32D192ED03L;
    private static final long SEED_SALT = 0x94D049BB133111EBL;

    private final int cellSizeBlocks;
    private final int minimumSpacingBlocks;
    private final int marginBlocks;
    private final int candidateSpanBlocks;

    public ShroudCoreCandidateField(int cellSizeBlocks, int minimumSpacingBlocks) {
        if (minimumSpacingBlocks <= 0) {
            throw new IllegalArgumentException("minimumSpacingBlocks must be positive");
        }
        if (cellSizeBlocks <= minimumSpacingBlocks) {
            throw new IllegalArgumentException("cellSizeBlocks must be greater than minimumSpacingBlocks");
        }

        this.cellSizeBlocks = cellSizeBlocks;
        this.minimumSpacingBlocks = minimumSpacingBlocks;
        this.marginBlocks = minimumSpacingBlocks / 2;
        this.candidateSpanBlocks = cellSizeBlocks - (2 * marginBlocks);
        if (candidateSpanBlocks <= 0) {
            throw new IllegalArgumentException("candidate cell has no interior after spacing margin");
        }
    }

    public Candidate candidate(long worldSeed, int cellX, int cellZ) {
        long cellBaseX = Math.multiplyExact((long) cellX, cellSizeBlocks);
        long cellBaseZ = Math.multiplyExact((long) cellZ, cellSizeBlocks);

        long coordinateSeed = worldSeed ^ SEED_SALT;
        coordinateSeed ^= mix64((long) cellX * X_SALT);
        coordinateSeed ^= mix64((long) cellZ * Z_SALT);

        int offsetX = (int) Math.floorMod(mix64(coordinateSeed ^ X_SALT), candidateSpanBlocks);
        int offsetZ = (int) Math.floorMod(mix64(coordinateSeed ^ Z_SALT), candidateSpanBlocks);

        int blockX = Math.toIntExact(cellBaseX + marginBlocks + offsetX);
        int blockZ = Math.toIntExact(cellBaseZ + marginBlocks + offsetZ);
        return new Candidate(blockX, blockZ);
    }

    public boolean supportsDimension(ResourceKey<Level> dimension) {
        return Level.OVERWORLD.equals(Objects.requireNonNull(dimension, "dimension"));
    }

    public int cellSizeBlocks() {
        return cellSizeBlocks;
    }

    public int minimumSpacingBlocks() {
        return minimumSpacingBlocks;
    }

    private static long mix64(long value) {
        value = (value ^ (value >>> 30)) * 0xBF58476D1CE4E5B9L;
        value = (value ^ (value >>> 27)) * 0x94D049BB133111EBL;
        return value ^ (value >>> 31);
    }

    public record Candidate(int blockX, int blockZ) {
    }
}
