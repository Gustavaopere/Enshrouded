package com.gustavaopere.enshrouded.shroud.core;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

final class CoreSafetyConfigRedTest {
    @Test
    void coreSafetyLimitsClampIndependentExpansionAndPurificationWork() throws Exception {
        Class<?> limits = Class.forName("com.gustavaopere.enshrouded.shroud.core.CoreSafetyLimits");
        Method radius = limits.getMethod("clampMaxInfluenceRadius", int.class);
        Method growth = limits.getMethod("clampGrowthWorkPerTick", int.class);
        Method regression = limits.getMethod("clampRegressionWorkPerTick", int.class);
        Method cleanup = limits.getMethod("clampCleanupWorkPerTick", int.class);

        assertEquals(16, radius.invoke(null, Integer.MIN_VALUE));
        assertEquals(128, radius.invoke(null, 128));
        assertEquals(512, radius.invoke(null, Integer.MAX_VALUE));

        assertEquals(1, growth.invoke(null, Integer.MIN_VALUE));
        assertEquals(32, growth.invoke(null, 32));
        assertEquals(512, growth.invoke(null, Integer.MAX_VALUE));

        assertEquals(1, regression.invoke(null, Integer.MIN_VALUE));
        assertEquals(32, regression.invoke(null, 32));
        assertEquals(512, regression.invoke(null, Integer.MAX_VALUE));

        assertEquals(1, cleanup.invoke(null, Integer.MIN_VALUE));
        assertEquals(64, cleanup.invoke(null, 64));
        assertEquals(512, cleanup.invoke(null, Integer.MAX_VALUE));
    }

    @Test
    void serverConfigExposesBoundedIndependentCoreSafetyValues() throws Exception {
        Class<?> config = Class.forName("com.gustavaopere.enshrouded.config.EnshroudedConfig");
        Field spec = config.getField("SERVER_SPEC");
        assertTrue(Modifier.isStatic(spec.getModifiers()));
        assertEquals("net.neoforged.neoforge.common.ModConfigSpec", spec.getType().getName());

        Method radius = config.getMethod("coreMaxInfluenceRadius");
        Method growth = config.getMethod("coreGrowthWorkPerTick");
        Method regression = config.getMethod("coreRegressionWorkPerTick");
        Method cleanup = config.getMethod("purificationCleanupWorkPerTick");
        assertEquals(int.class, radius.getReturnType());
        assertEquals(int.class, growth.getReturnType());
        assertEquals(int.class, regression.getReturnType());
        assertEquals(int.class, cleanup.getReturnType());
    }
}
