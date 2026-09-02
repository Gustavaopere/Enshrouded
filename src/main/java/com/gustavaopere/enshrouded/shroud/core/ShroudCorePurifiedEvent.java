package com.gustavaopere.enshrouded.shroud.core;

import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.Event;

import java.util.Objects;
import java.util.UUID;

/** Published exactly when an authoritative core lifecycle reaches PURIFIED. */
public final class ShroudCorePurifiedEvent extends Event {
    private final ServerLevel level;
    private final UUID coreId;

    public ShroudCorePurifiedEvent(ServerLevel level, UUID coreId) {
        this.level = Objects.requireNonNull(level, "level");
        this.coreId = Objects.requireNonNull(coreId, "coreId");
    }

    public ServerLevel level() {
        return level;
    }

    public UUID coreId() {
        return coreId;
    }
}
