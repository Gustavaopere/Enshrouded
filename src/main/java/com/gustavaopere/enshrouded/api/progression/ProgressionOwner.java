package com.gustavaopere.enshrouded.api.progression;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public record ProgressionOwner(Kind kind, String id) {
    public ProgressionOwner {
        Objects.requireNonNull(kind, "kind");
        Objects.requireNonNull(id, "id");
        if (id.isBlank()) {
            throw new IllegalArgumentException("progression owner id must not be blank");
        }
        if (kind == Kind.PLAYER) {
            UUID parsed = UUID.fromString(id);
            String canonical = parsed.toString();
            if (!canonical.equalsIgnoreCase(id)) {
                throw new IllegalArgumentException("player progression owner id must use canonical UUID form");
            }
            id = canonical;
        }
    }

    public static ProgressionOwner player(UUID playerId) {
        return new ProgressionOwner(Kind.PLAYER, Objects.requireNonNull(playerId, "playerId").toString());
    }

    public static ProgressionOwner team(String teamId) {
        return new ProgressionOwner(Kind.TEAM, teamId);
    }

    public static ProgressionOwner world(String worldId) {
        return new ProgressionOwner(Kind.WORLD, worldId);
    }

    public String stableKey() {
        return kind.id + ":" + id;
    }

    public static Optional<ProgressionOwner> parse(String stableKey) {
        if (stableKey == null) {
            return Optional.empty();
        }
        int separator = stableKey.indexOf(':');
        if (separator <= 0 || separator == stableKey.length() - 1) {
            return Optional.empty();
        }
        Optional<Kind> kind = Kind.fromId(stableKey.substring(0, separator));
        if (kind.isEmpty()) {
            return Optional.empty();
        }
        try {
            return Optional.of(new ProgressionOwner(kind.get(), stableKey.substring(separator + 1)));
        } catch (IllegalArgumentException exception) {
            return Optional.empty();
        }
    }

    public enum Kind {
        PLAYER("player"),
        TEAM("team"),
        WORLD("world");

        private final String id;

        Kind(String id) {
            this.id = id;
        }

        public String id() {
            return id;
        }

        public static Optional<Kind> fromId(String id) {
            for (Kind kind : values()) {
                if (kind.id.equals(id)) {
                    return Optional.of(kind);
                }
            }
            return Optional.empty();
        }
    }
}
