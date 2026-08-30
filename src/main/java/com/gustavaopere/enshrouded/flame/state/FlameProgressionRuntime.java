package com.gustavaopere.enshrouded.flame.state;

import com.gustavaopere.enshrouded.api.progression.ProgressionRuntimeBindings;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

/** Installs persistence-backed Flame progression providers for the active server lifecycle. */
public final class FlameProgressionRuntime {
    private FlameProgressionRuntime() {
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(FlameProgressionRuntime::onServerStarted);
        NeoForge.EVENT_BUS.addListener(FlameProgressionRuntime::onServerStopped);
    }

    static void onServerStarted(ServerStartedEvent event) {
        ProgressionRuntimeBindings.install(
                new DefaultProgressionOwnerResolver(),
                FlamePassageService.forServer(event.getServer())
        );
    }

    static void onServerStopped(ServerStoppedEvent event) {
        ProgressionRuntimeBindings.reset();
    }
}
