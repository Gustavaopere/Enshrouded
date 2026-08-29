package com.gustavaopere.enshrouded.shroud.purification;

import com.gustavaopere.enshrouded.shroud.state.ShroudCellState;

import java.util.Objects;
import java.util.Optional;

/** Pure policy for one bounded logical regression work unit. */
public record PurificationPolicy(double intensityDecayPerStep) {
    public static final double LEVEL_ONE_DECAY_PER_STEP = 0.25D;

    public PurificationPolicy {
        if (!Double.isFinite(intensityDecayPerStep)
                || intensityDecayPerStep <= 0.0D
                || intensityDecayPerStep > 1.0D) {
            throw new IllegalArgumentException("intensityDecayPerStep must be finite and within (0, 1]");
        }
    }

    public static PurificationPolicy levelOne() {
        return new PurificationPolicy(LEVEL_ONE_DECAY_PER_STEP);
    }

    /** Returns an updated cell, or empty once the cell has fully regressed. */
    public Optional<ShroudCellState> regress(ShroudCellState cell) {
        Objects.requireNonNull(cell, "cell");
        double nextIntensity = Math.max(0.0D, cell.intensity() - intensityDecayPerStep);
        if (nextIntensity <= 0.0D) {
            return Optional.empty();
        }
        return Optional.of(new ShroudCellState(cell.position(), nextIntensity, cell.severity()));
    }
}
