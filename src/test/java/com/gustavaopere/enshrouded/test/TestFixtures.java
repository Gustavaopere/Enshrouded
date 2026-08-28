package com.gustavaopere.enshrouded.test;

import java.util.Arrays;

/** Deterministic test-only fixtures shared by unit tests. */
public final class TestFixtures {
    private TestFixtures() {
    }

    public static TickClock clockAt(long initialTick) {
        return new TickClock(initialTick);
    }

    public static ScriptedRandom randomInts(int... values) {
        if (values == null) {
            throw new NullPointerException("values");
        }
        return new ScriptedRandom(Arrays.copyOf(values, values.length));
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

    /**
     * Script-backed integer source for tests that must exercise random branches without relying on a PRNG algorithm.
     * Each successful call consumes exactly one scripted value and normalizes it into the requested bound.
     */
    public static final class ScriptedRandom {
        private final int[] values;
        private int index;

        private ScriptedRandom(int[] values) {
            this.values = values;
        }

        public int nextInt(int bound) {
            if (bound <= 0) {
                throw new IllegalArgumentException("bound must be positive");
            }
            if (index >= values.length) {
                throw new IllegalStateException("scripted random exhausted");
            }
            return Math.floorMod(values[index++], bound);
        }

        public int remaining() {
            return values.length - index;
        }
    }
}
