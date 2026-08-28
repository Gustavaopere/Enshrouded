package com.gustavaopere.enshrouded.shroud.expansion;

import com.gustavaopere.enshrouded.shroud.core.CoreLifecycleState;
import com.gustavaopere.enshrouded.shroud.state.ShroudCellPos;
import com.gustavaopere.enshrouded.shroud.state.ShroudCoreState;
import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class ShroudPropagationPolicyRedTest {
    private static final UUID CORE_ID = UUID.fromString("00000000-0000-0000-0000-000000000101");
    private static final UUID REGION_ID = UUID.fromString("00000000-0000-0000-0000-000000000201");

    @Test
    void terrainNeutralPolicyIsDeterministicAndRadiusBounded() {
        ShroudGridGeometry geometry = new ShroudGridGeometry(8);
        ShroudCoreState core = core(32, 0x5EEDL);
        ShroudPropagationPolicy policy = ShroudPropagationPolicy.terrainNeutral();
        ShroudCellPos near = geometry.cellAt(new BlockPos(12, 4, 4));
        ShroudCellPos far = geometry.cellAt(new BlockPos(100, 4, 4));

        double first = policy.intensity(core, geometry, near);
        double second = policy.intensity(core, geometry, near);

        assertEquals(first, second);
        assertTrue(first > 0.0D && first <= 1.0D);
        assertEquals(0.0D, policy.intensity(core, geometry, far));
    }

    @Test
    void terrainNeutralPolicyEnumeratesNeighborsInStableOrder() {
        ShroudPropagationPolicy policy = ShroudPropagationPolicy.terrainNeutral();
        ShroudCellPos origin = new ShroudCellPos(0, 0, 0);

        assertEquals(List.of(
                new ShroudCellPos(1, 0, 0),
                new ShroudCellPos(-1, 0, 0),
                new ShroudCellPos(0, 0, 1),
                new ShroudCellPos(0, 0, -1),
                new ShroudCellPos(0, 1, 0),
                new ShroudCellPos(0, -1, 0)
        ), policy.neighbors(origin));
    }

    private static ShroudCoreState core(int radius, long seed) {
        return new ShroudCoreState(
                CORE_ID,
                new BlockPos(4, 4, 4),
                1,
                CoreLifecycleState.ACTIVE,
                radius,
                seed,
                0L,
                REGION_ID
        );
    }
}
