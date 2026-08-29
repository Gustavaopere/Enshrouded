package com.gustavaopere.enshrouded.shroud.terrain;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.LongPredicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MaterializationDomainRedTest {
    @Test
    void rulesAreExplicitlyReversibleAndIntensityBounded() throws Exception {
        Class<?> safetyType = Class.forName("com.gustavaopere.enshrouded.shroud.terrain.CorruptionSafetyClass");
        @SuppressWarnings({"rawtypes", "unchecked"})
        Object safe = Enum.valueOf((Class<? extends Enum>) safetyType.asSubclass(Enum.class), "SAFE");

        Class<?> ruleType = Class.forName("com.gustavaopere.enshrouded.shroud.terrain.CorruptionRule");
        Constructor<?> ctor = ruleType.getDeclaredConstructor(
                ResourceLocation.class,
                ResourceLocation.class,
                ResourceLocation.class,
                ResourceLocation.class,
                float.class,
                safetyType
        );
        Object rule = ctor.newInstance(
                ResourceLocation.parse("enshrouded:test_stone"),
                ResourceLocation.parse("enshrouded:corruptible_safe"),
                ResourceLocation.parse("minecraft:deepslate"),
                ResourceLocation.parse("minecraft:stone"),
                0.25f,
                safe
        );

        assertEquals(ResourceLocation.parse("minecraft:stone"), ruleType.getMethod("reversalBlock").invoke(rule));
        assertEquals(0.25f, (float) ruleType.getMethod("minIntensity").invoke(rule), 0.0001f);

        InvocationTargetException invalid = assertThrows(InvocationTargetException.class, () -> ctor.newInstance(
                ResourceLocation.parse("enshrouded:invalid"),
                ResourceLocation.parse("enshrouded:corruptible_safe"),
                ResourceLocation.parse("minecraft:deepslate"),
                ResourceLocation.parse("minecraft:stone"),
                Float.NaN,
                safe
        ));
        assertTrue(invalid.getCause() instanceof IllegalArgumentException);
    }

    @Test
    void workQueueEnforcesGlobalAndPerChunkBudgetsWithoutDroppingDeferredJobs() throws Exception {
        Class<?> jobType = Class.forName("com.gustavaopere.enshrouded.shroud.terrain.ShroudMutationJob");
        Constructor<?> jobCtor = jobType.getDeclaredConstructor(BlockPos.class, ResourceLocation.class, ResourceLocation.class);
        Object a = jobCtor.newInstance(new BlockPos(0, 64, 0), ResourceLocation.parse("enshrouded:a"), ResourceLocation.parse("minecraft:stone"));
        Object b = jobCtor.newInstance(new BlockPos(1, 64, 0), ResourceLocation.parse("enshrouded:b"), ResourceLocation.parse("minecraft:stone"));
        Object c = jobCtor.newInstance(new BlockPos(16, 64, 0), ResourceLocation.parse("enshrouded:c"), ResourceLocation.parse("minecraft:stone"));

        Class<?> queueType = Class.forName("com.gustavaopere.enshrouded.shroud.terrain.MaterializationWorkQueue");
        Object queue = queueType.getDeclaredConstructor(int.class).newInstance(8);
        Method enqueue = queueType.getMethod("enqueue", jobType);
        Method pollBudgeted = queueType.getMethod("pollBudgeted", int.class, int.class, LongPredicate.class);
        Method size = queueType.getMethod("size");

        assertTrue((boolean) enqueue.invoke(queue, a));
        assertTrue((boolean) enqueue.invoke(queue, b));
        assertTrue((boolean) enqueue.invoke(queue, c));

        @SuppressWarnings("unchecked")
        List<Object> first = (List<Object>) pollBudgeted.invoke(queue, 3, 1, (LongPredicate) ignored -> true);
        assertEquals(2, first.size(), "per-chunk budget must prevent two jobs from the same chunk in one drain");
        assertEquals(1, ((Number) size.invoke(queue)).intValue(), "deferred same-chunk work must stay queued");

        @SuppressWarnings("unchecked")
        List<Object> second = (List<Object>) pollBudgeted.invoke(queue, 3, 1, (LongPredicate) ignored -> true);
        assertEquals(1, second.size());
        assertEquals(0, ((Number) size.invoke(queue)).intValue());

        assertFalse((boolean) enqueue.invoke(queueType.getDeclaredConstructor(int.class).newInstance(1), c)
                && (boolean) enqueue.invoke(queueType.getDeclaredConstructor(int.class).newInstance(1), c),
                "capacity must be enforceable");
    }
}
