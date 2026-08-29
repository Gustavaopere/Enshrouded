package com.gustavaopere.enshrouded.shroud.terrain;

import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

import java.util.concurrent.atomic.AtomicBoolean;

/** Registers server datapack reload handling exactly once. */
public final class CorruptionRuleReloadRuntime {
    private static final AtomicBoolean REGISTERED = new AtomicBoolean();

    private CorruptionRuleReloadRuntime() {
    }

    public static void register() {
        if (REGISTERED.compareAndSet(false, true)) {
            NeoForge.EVENT_BUS.addListener(CorruptionRuleReloadRuntime::onReloadListeners);
        }
    }

    private static void onReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new CorruptionRuleReloadListener());
    }
}
