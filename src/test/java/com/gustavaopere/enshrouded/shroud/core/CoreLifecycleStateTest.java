package com.gustavaopere.enshrouded.shroud.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class CoreLifecycleStateTest {
    @Test
    void stableIdsRoundTripWithoutOrdinalPersistence() {
        for (CoreLifecycleState state : CoreLifecycleState.values()) {
            assertSame(state, CoreLifecycleState.fromId(state.id()).orElseThrow());
        }
        assertTrue(CoreLifecycleState.fromId("future_state").isEmpty());
        assertTrue(CoreLifecycleState.fromId(null).isEmpty());
    }

    @Test
    void onlyForwardLifecycleTransitionsAreLegal() {
        assertTrue(CoreLifecycleState.DORMANT.canTransitionTo(CoreLifecycleState.ACTIVE));
        assertTrue(CoreLifecycleState.ACTIVE.canTransitionTo(CoreLifecycleState.DESTROYED));
        assertTrue(CoreLifecycleState.DESTROYED.canTransitionTo(CoreLifecycleState.PURIFIED));

        assertFalse(CoreLifecycleState.ACTIVE.canTransitionTo(CoreLifecycleState.DORMANT));
        assertFalse(CoreLifecycleState.DESTROYED.canTransitionTo(CoreLifecycleState.ACTIVE));
        assertFalse(CoreLifecycleState.PURIFIED.canTransitionTo(CoreLifecycleState.DESTROYED));
        assertFalse(CoreLifecycleState.PURIFIED.canTransitionTo(CoreLifecycleState.ACTIVE));
        assertFalse(CoreLifecycleState.ACTIVE.canTransitionTo(CoreLifecycleState.ACTIVE));
    }

    @Test
    void illegalResurrectionAndSkippedTransitionsThrow() {
        assertThrows(IllegalStateException.class,
                () -> CoreLifecycleState.PURIFIED.transitionTo(CoreLifecycleState.ACTIVE));
        assertThrows(IllegalStateException.class,
                () -> CoreLifecycleState.DESTROYED.transitionTo(CoreLifecycleState.ACTIVE));
        assertThrows(IllegalStateException.class,
                () -> CoreLifecycleState.DORMANT.transitionTo(CoreLifecycleState.DESTROYED));
    }

    @Test
    void onlyActiveCoreIsExpansionEligible() {
        assertFalse(CoreLifecycleState.DORMANT.expansionEligible());
        assertTrue(CoreLifecycleState.ACTIVE.expansionEligible());
        assertFalse(CoreLifecycleState.DESTROYED.expansionEligible());
        assertFalse(CoreLifecycleState.PURIFIED.expansionEligible());
    }
}
