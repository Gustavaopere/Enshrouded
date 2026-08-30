package com.gustavaopere.enshrouded.flame.ward;

import com.gustavaopere.enshrouded.api.shroud.FlameWardQuery;
import com.gustavaopere.enshrouded.api.shroud.FlameWardRuntimeBindings;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class FlameWardContractsTest {
    @AfterEach
    void resetBindings() {
        FlameWardRuntimeBindings.reset();
    }

    @Test
    void indexedRadiusIsInclusiveDimensionLocalAndOverlapSafe() {
        FlameWardIndex index = new FlameWardIndex();
        BlockPos firstCenter = new BlockPos(0, 64, 0);
        BlockPos secondCenter = new BlockPos(16, 64, 0);

        index.activate(Level.OVERWORLD, new FlameWardState(firstCenter, 8));
        index.activate(Level.OVERWORLD, new FlameWardState(secondCenter, 8));

        assertTrue(index.suppresses(Level.OVERWORLD, new BlockPos(8, 64, 0)));
        assertFalse(index.suppresses(Level.OVERWORLD, new BlockPos(8, 73, 0)));
        assertFalse(index.suppresses(Level.NETHER, firstCenter));

        assertTrue(index.deactivate(Level.OVERWORLD, firstCenter));
        assertTrue(index.suppresses(Level.OVERWORLD, new BlockPos(8, 64, 0)),
                "overlap must remain suppressed by the second active ward");
        assertTrue(index.deactivate(Level.OVERWORLD, secondCenter));
        assertFalse(index.suppresses(Level.OVERWORLD, new BlockPos(8, 64, 0)));
    }

    @Test
    void wardStateSnapshotsMutableCenter() {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos(4, 70, -2);
        FlameWardState state = new FlameWardState(mutable, 12);

        mutable.set(100, 100, 100);

        assertTrue(state.center().equals(new BlockPos(4, 70, -2)));
    }

    @Test
    void foundationRuntimeHandleRemainsStableAcrossProviderSwap() {
        FlameWardQuery stableHandle = FlameWardRuntimeBindings.query();
        assertFalse(stableHandle.suppresses(null, null));

        FlameWardRuntimeBindings.install((level, pos) -> true);

        assertSame(stableHandle, FlameWardRuntimeBindings.query());
        assertTrue(stableHandle.suppresses(null, null));

        FlameWardRuntimeBindings.reset();
        assertFalse(stableHandle.suppresses(null, null));
    }
}
