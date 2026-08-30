package com.gustavaopere.enshrouded.flame.ward;

import com.gustavaopere.enshrouded.api.shroud.FlameWardRuntimeBindings;
import com.gustavaopere.enshrouded.config.EnshroudedConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

import java.util.concurrent.atomic.AtomicBoolean;

/** Owns the single loaded-altar Sanctuary provider for one active server lifecycle. */
public final class FlameWardRuntime {
    private static final AtomicBoolean REGISTERED = new AtomicBoolean();
    private static final FlameWardService SERVICE =
            new FlameWardService(new FlameWardIndex(), EnshroudedConfig::flameWardRadius);

    private FlameWardRuntime() {
    }

    public static void register() {
        if (REGISTERED.compareAndSet(false, true)) {
            NeoForge.EVENT_BUS.addListener(FlameWardRuntime::onServerStarted);
            NeoForge.EVENT_BUS.addListener(FlameWardRuntime::onServerStopped);
        }
    }

    public static void onAltarLoaded(ServerLevel level, BlockPos pos) {
        SERVICE.activate(level, pos);
    }

    public static void onAltarRemoved(ServerLevel level, BlockPos pos) {
        SERVICE.deactivate(level, pos);
    }

    static FlameWardService service() {
        return SERVICE;
    }

    static void onServerStarted(ServerStartedEvent event) {
        FlameWardRuntimeBindings.install(SERVICE);
    }

    static void onServerStopped(ServerStoppedEvent event) {
        FlameWardRuntimeBindings.reset();
        SERVICE.clear();
    }
}
