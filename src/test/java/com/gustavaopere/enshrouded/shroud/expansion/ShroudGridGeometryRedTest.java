package com.gustavaopere.enshrouded.shroud.expansion;

import com.gustavaopere.enshrouded.shroud.state.ShroudCellPos;
import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class ShroudGridGeometryRedTest {
    @Test
    void mapsBlockCoordinatesToCoarseCellsWithFloorDivision() {
        ShroudGridGeometry grid = new ShroudGridGeometry(8);

        assertEquals(new ShroudCellPos(0, 0, 0), grid.cellAt(new BlockPos(0, 0, 0)));
        assertEquals(new ShroudCellPos(0, 0, 0), grid.cellAt(new BlockPos(7, 7, 7)));
        assertEquals(new ShroudCellPos(1, 1, 1), grid.cellAt(new BlockPos(8, 8, 8)));
        assertEquals(new ShroudCellPos(-1, -1, -1), grid.cellAt(new BlockPos(-1, -1, -1)));
        assertEquals(new ShroudCellPos(-2, -2, -2), grid.cellAt(new BlockPos(-9, -9, -9)));
    }

    @Test
    void exposesDeterministicCellCenterAndRejectsInvalidCellSize() {
        ShroudGridGeometry grid = new ShroudGridGeometry(8);

        assertEquals(new BlockPos(4, 4, 4), grid.cellCenter(new ShroudCellPos(0, 0, 0)));
        assertEquals(new BlockPos(-4, -4, -4), grid.cellCenter(new ShroudCellPos(-1, -1, -1)));
        assertThrows(IllegalArgumentException.class, () -> new ShroudGridGeometry(0));
    }
}
