package com.gustavaopere.enshrouded.integration.ftbteams;

import com.gustavaopere.enshrouded.api.progression.ProgressionOwner;
import com.gustavaopere.enshrouded.api.progression.ProgressionOwnerResolver;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/** Optional team-backed owner resolver. The resolved owner is a value snapshot for one operation. */
public final class FtbTeamsOwnerResolver implements ProgressionOwnerResolver {
    private final TeamLookup teamLookup;

    public FtbTeamsOwnerResolver(TeamLookup teamLookup) {
        this.teamLookup = Objects.requireNonNull(teamLookup, "teamLookup");
    }

    @Override
    public ProgressionOwner resolve(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        final Optional<String> teamId;
        try {
            teamId = Objects.requireNonNull(teamLookup.teamId(playerId), "team lookup returned null");
        } catch (RuntimeException failure) {
            throw new IllegalStateException("FTB Teams owner resolution failed closed", failure);
        }
        return teamId.map(ProgressionOwner::team).orElseGet(() -> ProgressionOwner.player(playerId));
    }

    @FunctionalInterface
    public interface TeamLookup {
        Optional<String> teamId(UUID playerId);
    }
}
