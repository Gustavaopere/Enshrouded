package com.gustavaopere.enshrouded.api;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** RED checkpoint for the Foundation-owned Flame ward read boundary. */
final class FlameWardBoundaryRedTest {
    private static final String WARD_QUERY_CLASS =
            "com.gustavaopere.enshrouded.api.shroud.FlameWardQuery";

    @Test
    void wardQueryShapeAndNoWardFallbackExist() throws Exception {
        Class<?> queryType = Class.forName(WARD_QUERY_CLASS);
        assertTrue(queryType.isInterface());
        assertTrue(queryType.isAnnotationPresent(FunctionalInterface.class));

        Method suppresses = queryType.getDeclaredMethod("suppresses", ServerLevel.class, BlockPos.class);
        assertEquals(boolean.class, suppresses.getReturnType());

        Method none = queryType.getDeclaredMethod("none");
        assertTrue(Modifier.isStatic(none.getModifiers()));
        assertEquals(queryType, none.getReturnType());

        Object fallback = none.invoke(null);
        assertFalse((boolean) suppresses.invoke(fallback, null, null),
                "Foundation no-ward fallback must never invent Sanctuary suppression");
    }
}
