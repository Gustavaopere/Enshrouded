package com.gustavaopere.enshrouded.api;

import com.gustavaopere.enshrouded.api.progression.FlamePassageQuery;
import com.gustavaopere.enshrouded.api.progression.ProgressionOwner;
import com.gustavaopere.enshrouded.api.progression.ProgressionOwnerResolver;
import com.gustavaopere.enshrouded.api.progression.ProgressionRuntimeBindings;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class ProgressionRuntimeBindingsTest {
    @AfterEach
    void resetBindings() {
        ProgressionRuntimeBindings.reset();
    }

    @Test
    void stableDelegatingHandlesStartWithFoundationFallbacks() {
        ProgressionOwnerResolver resolver = ProgressionRuntimeBindings.ownerResolver();
        FlamePassageQuery query = ProgressionRuntimeBindings.passageQuery();
        UUID playerId = UUID.fromString("11111111-aaaa-bbbb-cccc-222222222222");
        ProgressionOwner owner = ProgressionOwner.player(playerId);

        assertEquals(owner, resolver.resolve(playerId));
        assertEquals(1, query.passageLevel(owner));
    }

    @Test
    void existingHandlesObserveInstalledProviderAndResetWithoutRecreation() {
        ProgressionOwnerResolver resolverHandle = ProgressionRuntimeBindings.ownerResolver();
        FlamePassageQuery queryHandle = ProgressionRuntimeBindings.passageQuery();
        UUID playerId = UUID.fromString("33333333-aaaa-bbbb-cccc-444444444444");
        ProgressionOwner team = ProgressionOwner.team("ftb:runtime-test");

        ProgressionRuntimeBindings.install(ignored -> team, ignored -> 7);

        assertEquals(team, resolverHandle.resolve(playerId));
        assertEquals(7, queryHandle.passageLevel(team));

        ProgressionRuntimeBindings.reset();
        assertEquals(ProgressionOwner.player(playerId), resolverHandle.resolve(playerId));
        assertEquals(1, queryHandle.passageLevel(team));
    }

    @Test
    void installationRejectsNullProviders() {
        assertThrows(NullPointerException.class, () -> ProgressionRuntimeBindings.install(null, owner -> 1));
        assertThrows(NullPointerException.class, () -> ProgressionRuntimeBindings.install(ProgressionOwnerResolver.standalone(), null));
    }
}
