package com.gustavaopere.enshrouded.shroud.terrain;

import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GrowthMaterializationIntegrationRedTest {
    @Test
    void materializationServiceOwnsBoundedGrowthWork() throws Exception {
        Method scheduleGrowth = ShroudMaterializationService.class.getMethod(
                "scheduleGrowth",
                ServerLevel.class,
                BlockPos.class,
                Block.class,
                ShroudSeverity.class,
                float.class,
                long.class
        );
        Method tickGrowths = ShroudMaterializationService.class.getMethod(
                "tickGrowths",
                ServerLevel.class,
                int.class,
                int.class
        );
        Method pendingGrowthWork = ShroudMaterializationService.class.getMethod("pendingGrowthWork");

        assertEquals(boolean.class, scheduleGrowth.getReturnType());
        assertEquals(int.class, tickGrowths.getReturnType());
        assertEquals(int.class, pendingGrowthWork.getReturnType());
    }
}
