package com.gustavaopere.enshrouded.network;

import com.gustavaopere.enshrouded.shroud.discovery.ShroudDiscoveryRuntime;
import com.gustavaopere.enshrouded.shroud.expansion.ShroudGridGeometry;
import com.gustavaopere.enshrouded.shroud.query.DefaultShroudQuery;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Minimal runtime hook for Level-1 local Shroud presentation sync.
 */
public final class ShroudSyncRuntime {
    public static final long MIN_TICKS_BETWEEN_SENDS = 5L;

    private static final ShroudSampleSyncService SERVICE = new ShroudSampleSyncService(
            DefaultShroudQuery.levelOne(ShroudGridGeometry.levelOne()),
            new ShroudPlayerSyncTracker(MIN_TICKS_BETWEEN_SENDS));

    private ShroudSyncRuntime() {
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(ShroudSyncRuntime::onPlayerTickPost);
        NeoForge.EVENT_BUS.addListener(ShroudSyncRuntime::onPlayerLoggedOut);
    }

    static void onPlayerTickPost(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ShroudSampleSyncService.SyncResult result = SERVICE.syncWithSample(player);
            ShroudDiscoveryRuntime.observe(player, result.sample());
        }
    }

    static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            SERVICE.forget(player);
            ShroudDiscoveryRuntime.forget(player);
        }
    }
}
