package com.gustavaopere.enshrouded.test;

/** Deterministic test-only fixtures shared by unit tests. */
public final class TestFixtures {
    private TestFixtures() {
    }

    public static TickClock clockAt(long initialTick) {
        return new TickClock(initialTick);
    }

    public static final class TickClock {
        private long tick;

        private TickClock(long initialTick) {
            if (initialTick < 0) {
                throw new IllegalArgumentException("initialTick must be non-negative");
            }
            this.tick = initialTick;
        }

        public long currentTick() {
            return tick;
        }

        public void advance(long ticks) {
            if (ticks < 0) {
                throw new IllegalArgumentException("ticks must be non-negative");
            }
            tick = Math.addExact(tick, ticks);
        }
    }
}
