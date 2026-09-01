package com.gustavaopere.enshrouded.integration;

import com.gustavaopere.enshrouded.api.progression.ProgressionOwnerResolver;
import net.neoforged.bus.api.IEventBus;

import java.util.Objects;

/** Stage-08 compatibility bootstrap; provider-specific wiring is isolated behind Enshrouded APIs. */
public final class CombatClaimsTeamsIntegrationRuntime {
    private static final ProgressionOwnerResolver OWNER_RESOLVER = ProgressionOwnerResolver.standalone();

    private CombatClaimsTeamsIntegrationRuntime() {
    }

    public static void register(IEventBus modBus) {
        Objects.requireNonNull(modBus, "modBus");
    }

    public static ProgressionOwnerResolver ownerResolver() {
        return OWNER_RESOLVER;
    }
}
