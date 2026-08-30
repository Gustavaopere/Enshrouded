package com.gustavaopere.enshrouded.flame.state;

import com.gustavaopere.enshrouded.api.progression.ProgressionOwner;
import com.gustavaopere.enshrouded.api.progression.ProgressionOwnerResolver;

import java.util.Objects;
import java.util.UUID;

/** Standalone owner resolution: each player UUID owns an independent progression key. */
public final class DefaultProgressionOwnerResolver implements ProgressionOwnerResolver {
    @Override
    public ProgressionOwner resolve(UUID playerId) {
        return ProgressionOwner.player(Objects.requireNonNull(playerId, "playerId"));
    }
}
