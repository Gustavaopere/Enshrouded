package com.gustavaopere.enshrouded.shroud.core;

import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;
import com.gustavaopere.enshrouded.shroud.state.ShroudCellPos;
import com.gustavaopere.enshrouded.shroud.state.ShroudCellState;
import com.gustavaopere.enshrouded.shroud.state.ShroudCoreState;
import com.gustavaopere.enshrouded.shroud.state.ShroudRegionState;
import com.gustavaopere.enshrouded.shroud.state.ShroudWorldState;
import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

final class ShroudCoreServiceTest {
    private static final UUID CORE_ID = UUID.fromString("11111111-aaaa-bbbb-cccc-111111111111");
    private static final UUID REGION_ID = UUID.fromString("22222222-aaaa-bbbb-cccc-222222222222");
    private static final BlockPos CENTER = new BlockPos(32, 70, -16);

    @Test
    void registrationCreatesExactlyOneDormantCoreAndOwnedRegion() {
        CoreMutationResult result = register(ShroudWorldState.empty());

        assertTrue(result.changed());
        assertEquals(1, result.state().cores().size());
        assertEquals(1, result.state().regions().size());

        ShroudCoreState core = result.state().cores().get(CORE_ID);
        assertEquals(CoreLifecycleState.DORMANT, core.lifecycleState());
        assertEquals(REGION_ID, core.regionId());
        assertEquals(CORE_ID, result.state().regions().get(REGION_ID).coreId());
    }

    @Test
    void discardDormantRemovesOnlyUnactivatedCoreAndOwnedEmptyRegion() {
        ShroudWorldState dormant = register(ShroudWorldState.empty()).state();

        CoreMutationResult discarded = ShroudCoreService.discardDormant(dormant, CORE_ID);

        assertTrue(discarded.changed());
        assertFalse(discarded.state().cores().containsKey(CORE_ID));
        assertFalse(discarded.state().regions().containsKey(REGION_ID));
        assertFalse(ShroudCoreService.discardDormant(discarded.state(), CORE_ID).changed());
    }

    @Test
    void discardDormantCannotEraseActiveCoreOrUnexpectedLogicalField() {
        ShroudWorldState dormant = register(ShroudWorldState.empty()).state();
        ShroudWorldState active = ShroudCoreService.activate(dormant, CORE_ID).state();

        CoreMutationResult activeDiscard = ShroudCoreService.discardDormant(active, CORE_ID);
        assertFalse(activeDiscard.changed());
        assertSame(active, activeDiscard.state());

        ShroudCellPos position = new ShroudCellPos(0, 0, 0);
        ShroudCellState cell = new ShroudCellState(position, 0.25D, ShroudSeverity.SHROUD);
        LinkedHashMap<UUID, ShroudRegionState> regions = new LinkedHashMap<>(dormant.regions());
        regions.put(REGION_ID, new ShroudRegionState(REGION_ID, CORE_ID, Map.of(position, cell)));
        ShroudWorldState unexpectedField = new ShroudWorldState(dormant.schemaVersion(), dormant.cores(), regions);

        assertThrows(IllegalStateException.class, () -> ShroudCoreService.discardDormant(unexpectedField, CORE_ID));
    }

    @Test
    void sameRegistrationIsIdempotentEvenAfterLifecycleAdvances() {
        ShroudWorldState registered = register(ShroudWorldState.empty()).state();
        ShroudWorldState active = ShroudCoreService.activate(registered, CORE_ID).state();

        CoreMutationResult duplicate = register(active);

        assertFalse(duplicate.changed());
        assertSame(active, duplicate.state());
        assertEquals(CoreLifecycleState.ACTIVE, duplicate.state().cores().get(CORE_ID).lifecycleState());
    }

    @Test
    void coreIdPositionAndRegionCollisionsFailClosed() {
        ShroudWorldState registered = register(ShroudWorldState.empty()).state();

        assertThrows(IllegalStateException.class, () -> ShroudCoreService.registerDormant(
                registered, CORE_ID, REGION_ID, CENTER.offset(1, 0, 0), 1, 128, 42L));

        assertThrows(IllegalStateException.class, () -> ShroudCoreService.registerDormant(
                registered,
                UUID.fromString("33333333-aaaa-bbbb-cccc-333333333333"),
                UUID.fromString("44444444-aaaa-bbbb-cccc-444444444444"),
                CENTER,
                1,
                128,
                43L));

        assertThrows(IllegalStateException.class, () -> ShroudCoreService.registerDormant(
                registered,
                UUID.fromString("55555555-aaaa-bbbb-cccc-555555555555"),
                REGION_ID,
                CENTER.offset(8, 0, 0),
                1,
                128,
                44L));
    }

    @Test
    void activateDestroyPurifyIsForwardOnlyAndIdempotentAtRuntimeEdges() {
        ShroudWorldState dormant = register(ShroudWorldState.empty()).state();
        CoreMutationResult activated = ShroudCoreService.activate(dormant, CORE_ID);
        assertTrue(activated.changed());
        assertEquals(CoreLifecycleState.ACTIVE, activated.state().cores().get(CORE_ID).lifecycleState());
        assertFalse(ShroudCoreService.activate(activated.state(), CORE_ID).changed());

        CoreMutationResult destroyed = ShroudCoreService.destroy(activated.state(), CORE_ID);
        assertTrue(destroyed.changed());
        assertEquals(CoreLifecycleState.DESTROYED, destroyed.state().cores().get(CORE_ID).lifecycleState());
        assertFalse(ShroudCoreService.destroy(destroyed.state(), CORE_ID).changed());

        CoreMutationResult purified = ShroudCoreService.markPurified(destroyed.state(), CORE_ID);
        assertTrue(purified.changed());
        assertEquals(CoreLifecycleState.PURIFIED, purified.state().cores().get(CORE_ID).lifecycleState());
        assertFalse(ShroudCoreService.markPurified(purified.state(), CORE_ID).changed());
        assertFalse(ShroudCoreService.destroy(purified.state(), CORE_ID).changed());
        assertThrows(IllegalStateException.class, () -> ShroudCoreService.activate(purified.state(), CORE_ID));
    }

    @Test
    void dormantCoreCannotSkipActivationAndActiveCoreCannotSkipDestruction() {
        ShroudWorldState dormant = register(ShroudWorldState.empty()).state();
        assertThrows(IllegalStateException.class, () -> ShroudCoreService.destroy(dormant, CORE_ID));
        assertThrows(IllegalStateException.class, () -> ShroudCoreService.markPurified(dormant, CORE_ID));

        ShroudWorldState active = ShroudCoreService.activate(dormant, CORE_ID).state();
        assertThrows(IllegalStateException.class, () -> ShroudCoreService.markPurified(active, CORE_ID));
    }

    @Test
    void unknownCoreMutationsFailClosed() {
        UUID missing = UUID.fromString("99999999-aaaa-bbbb-cccc-999999999999");
        ShroudWorldState empty = ShroudWorldState.empty();

        assertThrows(IllegalArgumentException.class, () -> ShroudCoreService.activate(empty, missing));
        assertThrows(IllegalArgumentException.class, () -> ShroudCoreService.destroy(empty, missing));
        assertThrows(IllegalArgumentException.class, () -> ShroudCoreService.markPurified(empty, missing));
        assertFalse(ShroudCoreService.discardDormant(empty, missing).changed());
    }

    @Test
    void destroyedAndPurifiedCoresAreNeverExpansionEligible() {
        ShroudWorldState active = ShroudCoreService.activate(register(ShroudWorldState.empty()).state(), CORE_ID).state();
        ShroudWorldState destroyed = ShroudCoreService.destroy(active, CORE_ID).state();
        ShroudWorldState purified = ShroudCoreService.markPurified(destroyed, CORE_ID).state();

        assertTrue(active.cores().get(CORE_ID).lifecycleState().expansionEligible());
        assertFalse(destroyed.cores().get(CORE_ID).lifecycleState().expansionEligible());
        assertFalse(purified.cores().get(CORE_ID).lifecycleState().expansionEligible());
    }

    private static CoreMutationResult register(ShroudWorldState state) {
        return ShroudCoreService.registerDormant(state, CORE_ID, REGION_ID, CENTER, 1, 128, 42L);
    }
}
