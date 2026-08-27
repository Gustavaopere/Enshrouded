package com.gustavaopere.enshrouded.api;

import com.gustavaopere.enshrouded.api.progression.ProgressionOwner;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * RED checkpoint for Foundation progression read boundaries.
 *
 * <p>This test deliberately uses reflection so the test source still compiles while the
 * production interfaces do not exist. The expected RED is ClassNotFoundException until
 * ProgressionOwnerResolver and FlamePassageQuery are implemented.</p>
 */
final class ProgressionBoundaryRedTest {
    private static final String RESOLVER_CLASS =
            "com.gustavaopere.enshrouded.api.progression.ProgressionOwnerResolver";
    private static final String PASSAGE_QUERY_CLASS =
            "com.gustavaopere.enshrouded.api.progression.FlamePassageQuery";

    @Test
    void standaloneOwnerResolverMapsUuidToPlayerOwner() throws Exception {
        Class<?> resolverType = Class.forName(RESOLVER_CLASS);
        assertTrue(resolverType.isInterface());
        assertTrue(resolverType.isAnnotationPresent(FunctionalInterface.class));

        Method resolve = resolverType.getDeclaredMethod("resolve", UUID.class);
        assertEquals(ProgressionOwner.class, resolve.getReturnType());

        Method standalone = resolverType.getDeclaredMethod("standalone");
        assertTrue(Modifier.isStatic(standalone.getModifiers()));
        assertEquals(resolverType, standalone.getReturnType());

        Object resolver = standalone.invoke(null);
        UUID playerId = UUID.fromString("c6a9f9cf-21cb-4d35-b1ae-ad37445b0e96");
        assertEquals(ProgressionOwner.player(playerId), resolve.invoke(resolver, playerId));
    }

    @Test
    void levelOnePassageFallbackIsOwnerAgnosticAndStable() throws Exception {
        Class<?> queryType = Class.forName(PASSAGE_QUERY_CLASS);
        assertTrue(queryType.isInterface());
        assertTrue(queryType.isAnnotationPresent(FunctionalInterface.class));

        Method passageLevel = queryType.getDeclaredMethod("passageLevel", ProgressionOwner.class);
        assertEquals(int.class, passageLevel.getReturnType());

        Method levelOneFallback = queryType.getDeclaredMethod("levelOneFallback");
        assertTrue(Modifier.isStatic(levelOneFallback.getModifiers()));
        assertEquals(queryType, levelOneFallback.getReturnType());

        Object query = levelOneFallback.invoke(null);
        assertEquals(1, passageLevel.invoke(query, ProgressionOwner.player(
                UUID.fromString("2926404d-b455-488d-b0c8-5b390cb6e818"))));
        assertEquals(1, passageLevel.invoke(query, ProgressionOwner.team("ftb:builders")));
        assertEquals(1, passageLevel.invoke(query, ProgressionOwner.world("minecraft:overworld")));
    }
}
