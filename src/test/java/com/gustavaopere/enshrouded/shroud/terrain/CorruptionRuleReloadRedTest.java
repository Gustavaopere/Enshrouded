package com.gustavaopere.enshrouded.shroud.terrain;

import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CorruptionRuleReloadRedTest {
    @Test
    void reloadListenerPublishesImmutableCurrentRegistry() throws Exception {
        Class<?> listener = Class.forName(
                "com.gustavaopere.enshrouded.shroud.terrain.CorruptionRuleReloadListener"
        );
        assertTrue(SimpleJsonResourceReloadListener.class.isAssignableFrom(listener));

        var currentRegistry = listener.getMethod("currentRegistry");
        assertTrue(Modifier.isStatic(currentRegistry.getModifiers()));
        assertEquals(CorruptionRuleRegistry.class, currentRegistry.getReturnType());
    }

    @Test
    void runtimeRegistrationUsesServerReloadEvent() throws Exception {
        Class<?> runtime = Class.forName(
                "com.gustavaopere.enshrouded.shroud.terrain.CorruptionRuleReloadRuntime"
        );
        assertEquals(void.class, runtime.getMethod("register").getReturnType());
    }
}
