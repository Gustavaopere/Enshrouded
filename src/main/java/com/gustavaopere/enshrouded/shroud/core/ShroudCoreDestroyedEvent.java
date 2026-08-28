package com.gustavaopere.enshrouded.shroud.core;

import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.Event;

import java.util.Objects;
import java.util.UUID;

public final class ShroudCoreDestroyedEvent extends Event {
    private final ServerLevel level;
    private final UUID coreId;

    public ShroudCoreDestroyedEvent(ServerLevel level, UUID coreId) {
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
