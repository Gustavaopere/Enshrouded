package com.gustavaopere.enshrouded.test;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class TestFixturesExecutionTest {
    @Test
    void deterministicTickClockStartsAndAdvancesPredictably() {
        TestFixtures.TickClock clock = TestFixtures.clockAt(40L);

        assertEquals(40L, clock.currentTick());
        clock.advance(2L);
        assertEquals(42L, clock.currentTick());
    }

    @Test
    void deterministicTickClockRejectsBackwardTime() {
        assertThrows(IllegalArgumentException.class, () -> TestFixtures.clockAt(-1L));

        TestFixtures.TickClock clock = TestFixtures.clockAt(5L);
        assertThrows(IllegalArgumentException.class, () -> clock.advance(-1L));
        assertEquals(5L, clock.currentTick());
    }
}
