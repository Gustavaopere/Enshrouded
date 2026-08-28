package com.gustavaopere.enshrouded.shroud.core;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

final class CoreLifecycleRedTest {
    @Test
    void lifecycleHasStableIdsAndOnlyForwardTransitions() throws Exception {
        Class<?> lifecycle = Class.forName("com.gustavaopere.enshrouded.shroud.core.CoreLifecycleState");
        assertTrue(lifecycle.isEnum());

        Object dormant = enumConstant(lifecycle, "DORMANT");
        Object active = enumConstant(lifecycle, "ACTIVE");
        Object destroyed = enumConstant(lifecycle, "DESTROYED");
        Object purified = enumConstant(lifecycle, "PURIFIED");

        Method id = lifecycle.getMethod("id");
        assertEquals("dormant", id.invoke(dormant));
        assertEquals("active", id.invoke(active));
        assertEquals("destroyed", id.invoke(destroyed));
        assertEquals("purified", id.invoke(purified));

        Method canTransitionTo = lifecycle.getMethod("canTransitionTo", lifecycle);
        assertEquals(true, canTransitionTo.invoke(dormant, active));
        assertEquals(true, canTransitionTo.invoke(active, destroyed));
        assertEquals(true, canTransitionTo.invoke(destroyed, purified));

        assertEquals(false, canTransitionTo.invoke(active, dormant));
        assertEquals(false, canTransitionTo.invoke(destroyed, active));
        assertEquals(false, canTransitionTo.invoke(purified, destroyed));
        assertEquals(false, canTransitionTo.invoke(purified, active));
        assertEquals(false, canTransitionTo.invoke(active, active));
    }

    @Test
    void stableIdParserRejectsUnknownLifecycle() throws Exception {
        Class<?> lifecycle = Class.forName("com.gustavaopere.enshrouded.shroud.core.CoreLifecycleState");
        Method fromId = lifecycle.getMethod("fromId", String.class);

        assertEquals("ACTIVE", ((Enum<?>) ((Optional<?>) fromId.invoke(null, "active")).orElseThrow()).name());
        assertTrue(((Optional<?>) fromId.invoke(null, "future_state")).isEmpty());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static Object enumConstant(Class<?> enumType, String name) {
        return Enum.valueOf((Class<? extends Enum>) enumType, name);
    }
}
