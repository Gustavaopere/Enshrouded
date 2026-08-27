package com.gustavaopere.enshrouded.api;

import com.gustavaopere.enshrouded.api.progression.FlamePassageQuery;
import com.gustavaopere.enshrouded.api.progression.ProgressionOwner;
import com.gustavaopere.enshrouded.api.progression.ProgressionOwnerResolver;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class ProgressionBoundaryTest {
    @Test
    void standaloneOwnerResolverMapsUuidToPlayerOwner() {
        assertTrue(ProgressionOwnerResolver.class.isAnnotationPresent(FunctionalInterface.class));

        ProgressionOwnerResolver resolver = ProgressionOwnerResolver.standalone();
        UUID playerId = UUID.fromString("c6a9f9cf-21cb-4d35-b1ae-ad37445b0e96");

        assertEquals(ProgressionOwner.player(playerId), resolver.resolve(playerId));
        assertThrows(NullPointerException.class, () -> resolver.resolve(null));
    }

    @Test
    void levelOnePassageFallbackIsOwnerAgnosticAndStable() {
        assertTrue(FlamePassageQuery.class.isAnnotationPresent(FunctionalInterface.class));

        FlamePassageQuery query = FlamePassageQuery.levelOneFallback();
        assertEquals(1, query.passageLevel(ProgressionOwner.player(
                UUID.fromString("2926404d-b455-488d-b0c8-5b390cb6e818"))));
        assertEquals(1, query.passageLevel(ProgressionOwner.team("ftb:builders")));
        assertEquals(1, query.passageLevel(ProgressionOwner.world("minecraft:overworld")));
        assertThrows(NullPointerException.class, () -> query.passageLevel(null));
    }
}
