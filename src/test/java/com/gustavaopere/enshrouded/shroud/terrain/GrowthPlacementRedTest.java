package com.gustavaopere.enshrouded.shroud.terrain;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

class GrowthPlacementRedTest {
    @Test
    void plannedGrowthTypesAndDeterministicSamplerExist() throws Exception {
        Class<?> sampler = Class.forName("com.gustavaopere.enshrouded.shroud.terrain.GrowthCandidateSampler");
        Class.forName("com.gustavaopere.enshrouded.shroud.terrain.GrowthPlacementService");
        Class.forName("com.gustavaopere.enshrouded.content.block.ShroudGrowthBlock");
        Class.forName("com.gustavaopere.enshrouded.content.block.ShroudVeinBlock");
        Class.forName("com.gustavaopere.enshrouded.content.block.WitheredGrowthBlock");

        Method shouldPlace = sampler.getMethod(
                "shouldPlace",
                long.class,
                long.class,
                float.class,
                float.class,
                long.class
        );
        assertTrue(Modifier.isStatic(shouldPlace.getModifiers()));
        assertEquals(boolean.class, shouldPlace.getReturnType());

        Object first = shouldPlace.invoke(null, 991L, 12345L, 0.75F, 0.40F, 17L);
        Object second = shouldPlace.invoke(null, 991L, 12345L, 0.75F, 0.40F, 17L);
        assertEquals(first, second, "same world/position/profile must sample deterministically");
    }

    @Test
    void samplerHonorsZeroFullAndMonotonicDensityBounds() throws Exception {
        Class<?> sampler = Class.forName("com.gustavaopere.enshrouded.shroud.terrain.GrowthCandidateSampler");
        Method shouldPlace = sampler.getMethod(
                "shouldPlace",
                long.class,
                long.class,
                float.class,
                float.class,
                long.class
        );

        int lowDensityAccepted = 0;
        int highDensityAccepted = 0;
        for (long key = 0; key < 1024; key++) {
            assertFalse((boolean) shouldPlace.invoke(null, 42L, key, 1.0F, 0.0F, 3L));
            assertTrue((boolean) shouldPlace.invoke(null, 42L, key, 1.0F, 1.0F, 3L));

            boolean low = (boolean) shouldPlace.invoke(null, 42L, key, 0.60F, 0.20F, 3L);
            boolean high = (boolean) shouldPlace.invoke(null, 42L, key, 0.60F, 0.80F, 3L);
            if (low) {
                lowDensityAccepted++;
                assertTrue(high, "increasing density must not reject a candidate already accepted at lower density");
            }
            if (high) {
                highDensityAccepted++;
            }
        }

        assertTrue(highDensityAccepted >= lowDensityAccepted);
    }
}
