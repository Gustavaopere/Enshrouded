package com.gustavaopere.enshrouded.flame.state;

import com.gustavaopere.enshrouded.api.progression.FlamePassageQuery;
import com.gustavaopere.enshrouded.api.progression.ProgressionOwner;
import com.gustavaopere.enshrouded.api.progression.ProgressionOwnerResolver;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class FlameProgressionContractsTest {
    @Test
    void newOwnersReadAsLevelOneWithoutMaterializingState() {
        FlameProgressionState state = FlameProgressionState.empty();
        ProgressionOwner owner = ProgressionOwner.player(UUID.fromString("11111111-2222-3333-4444-555555555555"));

        FlameProgressionState.OwnerProgression progression = state.progression(owner);

        assertEquals(1, progression.flameLevel());
        assertEquals(1, progression.passageLevel());
        assertTrue(progression.completedRituals().isEmpty());
        assertFalse(state.hasOwner(owner));
    }

    @Test
    void ritualCheckpointIsAtomicIdempotentAndOwnerIsolated() {
        ProgressionOwner first = ProgressionOwner.player(UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee"));
        ProgressionOwner second = ProgressionOwner.player(UUID.fromString("01234567-89ab-cdef-0123-456789abcdef"));
        ResourceLocation ritual = ResourceLocation.fromNamespaceAndPath("enshrouded", "synthetic_progression_test");

        FlameProgressionState initial = FlameProgressionState.empty();
        FlameProgressionState once = initial.applyRitualCheckpoint(first, ritual, 2, 2).orElseThrow();

        assertEquals(2, once.progression(first).flameLevel());
        assertEquals(2, once.progression(first).passageLevel());
        assertEquals(Set.of(ritual), once.progression(first).completedRituals());
        assertTrue(once.applyRitualCheckpoint(first, ritual, 3, 3).isEmpty());
        assertEquals(1, once.progression(second).flameLevel());
        assertEquals(1, once.progression(second).passageLevel());
        assertTrue(once.progression(second).completedRituals().isEmpty());
    }

    @Test
    void progressionLevelsAreFormatBoundedButLevelTwoContentIsNotDefinedHere() {
        assertThrows(IllegalArgumentException.class, () -> new FlameProgressionState.OwnerProgression(0, 1, Set.of()));
        assertThrows(IllegalArgumentException.class, () -> new FlameProgressionState.OwnerProgression(1, 0, Set.of()));
        assertThrows(IllegalArgumentException.class, () -> new FlameProgressionState.OwnerProgression(
                FlameProgressionSchema.MAX_LEVEL + 1,
                1,
                Set.of()
        ));
        assertThrows(IllegalArgumentException.class, () -> new FlameProgressionState.OwnerProgression(
                1,
                FlameProgressionSchema.MAX_LEVEL + 1,
                Set.of()
        ));
    }

    @Test
    void standaloneResolverMatchesFoundationSemantics() {
        UUID playerId = UUID.fromString("c6a9f9cf-21cb-4d35-b1ae-ad37445b0e96");
        ProgressionOwnerResolver foundation = ProgressionOwnerResolver.standalone();
        ProgressionOwnerResolver stageFive = new DefaultProgressionOwnerResolver();

        assertEquals(foundation.resolve(playerId), stageFive.resolve(playerId));
        assertThrows(NullPointerException.class, () -> stageFive.resolve(null));
    }

    @Test
    void passageServiceImplementsFoundationReadBoundary() {
        ProgressionOwner owner = ProgressionOwner.team("ftb:builders");
        ResourceLocation ritual = ResourceLocation.fromNamespaceAndPath("enshrouded", "synthetic_passage_test");
        FlameProgressionState state = FlameProgressionState.empty()
                .applyRitualCheckpoint(owner, ritual, 1, 4)
                .orElseThrow();

        FlamePassageQuery query = new FlamePassageService(state::progression);

        assertEquals(4, query.passageLevel(owner));
        assertThrows(NullPointerException.class, () -> query.passageLevel(null));
    }
}
