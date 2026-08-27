package com.gustavaopere.enshrouded.test;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class TestFixturesExecutionTest {
    @Test
    void intentionalRedProvesDeterministicFixtureActuallyExecutes() {
        TestFixtures.TickClock clock = TestFixtures.clockAt(40L);

        // INTENTIONAL RED checkpoint: this must fail with actual=40 before the assertion is corrected.
        assertEquals(41L, clock.currentTick());
    }
}
