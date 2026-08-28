package com.gustavaopere.enshrouded.api.progression;

import java.util.Objects;
import java.util.UUID;

/**
 * Resolves an external player identity into the Enshrouded-owned progression owner key.
 * Optional team integrations may replace this resolver without leaking their types into core APIs.
 */
@FunctionalInterface
public interface ProgressionOwnerResolver {
    ProgressionOwner resolve(UUID playerId);

    static ProgressionOwnerResolver standalone() {
        return playerId -> ProgressionOwner.player(Objects.requireNonNull(playerId, "playerId"));
    }
}
