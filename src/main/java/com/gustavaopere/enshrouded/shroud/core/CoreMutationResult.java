package com.gustavaopere.enshrouded.shroud.core;

import com.gustavaopere.enshrouded.shroud.state.ShroudWorldState;

import java.util.Objects;

public record CoreMutationResult(ShroudWorldState state, boolean changed) {
    public CoreMutationResult {
        Objects.requireNonNull(state, "state");
    }

    public static CoreMutationResult unchanged(ShroudWorldState state) {
        return new CoreMutationResult(state, false);
    }

    public static CoreMutationResult changed(ShroudWorldState state) {
        return new CoreMutationResult(state, true);
    }
}
