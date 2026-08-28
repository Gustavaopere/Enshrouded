package com.gustavaopere.enshrouded.shroud.expansion;

import com.gustavaopere.enshrouded.shroud.state.ShroudCellPos;
import net.minecraft.core.BlockPos;

import java.util.Objects;

/**
 * Deterministic conversion between block coordinates and the sparse logical Shroud grid.
 */
public final class ShroudGridGeometry {
    private final int cellSizeBlocks;

    public ShroudGridGeometry(int cellSizeBlocks) {
        if (cellSizeBlocks <= 0) {
            throw new IllegalArgumentException("cellSizeBlocks must be > 0");
        }
        this.cellSizeBlocks = cellSizeBlocks;
    }

    public int cellSizeBlocks() {
        return cellSizeBlocks;
    }

    public ShroudCellPos cellAt(BlockPos blockPos) {
        Objects.requireNonNull(blockPos, "blockPos");
        return new ShroudCellPos(
                Math.floorDiv(blockPos.getX(), cellSizeBlocks),
                Math.floorDiv(blockPos.getY(), cellSizeBlocks),
                Math.floorDiv(blockPos.getZ(), cellSizeBlocks));
    }

    public BlockPos cellCenter(ShroudCellPos cell) {
        Objects.requireNonNull(cell, "cell");
        int half = cellSizeBlocks / 2;
        return new BlockPos(
                centeredCoordinate(cell.x(), half),
                centeredCoordinate(cell.y(), half),
                centeredCoordinate(cell.z(), half));
    }

    private int centeredCoordinate(int cellCoordinate, int half) {
        return Math.addExact(Math.multiplyExact(cellCoordinate, cellSizeBlocks), half);
    }
}
