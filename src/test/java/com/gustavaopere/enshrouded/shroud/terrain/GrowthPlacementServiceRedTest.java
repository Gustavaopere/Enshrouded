package com.gustavaopere.enshrouded.shroud.terrain;

import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GrowthPlacementServiceRedTest {
    @Test
    void serviceExposesSingleSurfacePlacementOperation() throws Exception {
        Method method = GrowthPlacementService.class.getMethod(
                "tryPlaceOnTop",
                ServerLevel.class,
                BlockPos.class,
                Block.class,
                ShroudSeverity.class,
                float.class,
                long.class
        );
        assertEquals(boolean.class, method.getReturnType());
    }
}
