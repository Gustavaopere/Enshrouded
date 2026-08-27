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

    @Test
    void scriptedRandomConsumesValuesDeterministically() {
        TestFixtures.ScriptedRandom random = TestFixtures.randomInts(7, -1, 12);

        assertEquals(2, random.nextInt(5));
        assertEquals(4, random.nextInt(5));
        assertEquals(0, random.nextInt(3));
        assertThrows(IllegalStateException.class, () -> random.nextInt(2));
    }

    @Test
    void scriptedRandomRejectsInvalidBounds() {
        TestFixtures.ScriptedRandom random = TestFixtures.randomInts(0);

        assertThrows(IllegalArgumentException.class, () -> random.nextInt(0));
        assertThrows(IllegalArgumentException.class, () -> random.nextInt(-1));
        assertEquals(0, random.nextInt(1));
    }
}
