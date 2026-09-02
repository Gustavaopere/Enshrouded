package com.gustavaopere.enshrouded.shroud.discovery;

import com.gustavaopere.enshrouded.api.progression.ProgressionOwner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/** Per-player complete-snapshot deduplicator. Owner changes always force replacement, including empty. */
public final class ShroudDiscoverySyncTracker {
    private final Map<UUID, PlayerState> players = new HashMap<>();

    public Optional<ShroudDiscoveryPayload> update(
            UUID playerId,
            ProgressionOwner owner,
            List<DiscoveredCore> visibleCores) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(owner, "owner");
        List<DiscoveredCore> canonical = ShroudDiscoveryPayload.canonicalize(visibleCores);
        String ownerKey = owner.stableKey();

        PlayerState current = players.get(playerId);
        if (current != null && current.ownerStableKey().equals(ownerKey) && current.cores().equals(canonical)) {
            return Optional.empty();
        }

        long sequence = current == null ? 0L : current.nextSequence();
        ShroudDiscoveryPayload payload = new ShroudDiscoveryPayload(
                ShroudDiscoveryPayload.CURRENT_VERSION,
                sequence,
                ownerKey,
                canonical);
        players.put(playerId, new PlayerState(ownerKey, canonical, sequence + 1L));
        return Optional.of(payload);
    }

    public void forget(UUID playerId) {
        players.remove(Objects.requireNonNull(playerId, "playerId"));
    }

    public int trackedPlayerCount() {
        return players.size();
    }

    private record PlayerState(String ownerStableKey, List<DiscoveredCore> cores, long nextSequence) {
        private PlayerState {
            Objects.requireNonNull(ownerStableKey, "ownerStableKey");
            cores = List.copyOf(Objects.requireNonNull(cores, "cores"));
        }
    }
}
