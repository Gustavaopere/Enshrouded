package com.gustavaopere.enshrouded.shroud.discovery;

import com.gustavaopere.enshrouded.api.progression.ProgressionOwner;
import com.gustavaopere.enshrouded.api.progression.ProgressionRuntimeBindings;
import com.gustavaopere.enshrouded.api.shroud.ShroudSample;
import com.gustavaopere.enshrouded.shroud.core.CoreLifecycleState;
import com.gustavaopere.enshrouded.shroud.core.ShroudCoreDestroyedEvent;
import com.gustavaopere.enshrouded.shroud.core.ShroudCorePurifiedEvent;
import com.gustavaopere.enshrouded.shroud.state.ShroudSavedData;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Objects;

/** Server authority for owner-scoped Shroud core discovery and complete marker snapshots. */
public final class ShroudDiscoveryRuntime {
    private static final ShroudDiscoverySyncTracker SYNC = new ShroudDiscoverySyncTracker();

    private ShroudDiscoveryRuntime() {
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(ShroudDiscoveryRuntime::onCoreDestroyed);
        NeoForge.EVENT_BUS.addListener(ShroudDiscoveryRuntime::onCorePurified);
        NeoForge.EVENT_BUS.addListener(ShroudDiscoveryRuntime::onServerStopped);
    }

    /**
     * Consumes the exact canonical sample already queried for the player's presentation sync.
     * Discovery therefore performs only a direct source-id lookup and never scans undiscovered cores.
     */
    public static void observe(ServerPlayer player, ShroudSample sample) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(sample, "sample");

        ProgressionOwner owner = ProgressionRuntimeBindings.ownerResolver().resolve(player.getUUID());
        ShroudDiscoverySavedData discoveryData = ShroudDiscoverySavedData.get(player.serverLevel());
        ShroudDiscoveryObservation.Result observed = ShroudDiscoveryObservation.observe(
                discoveryData.state(),
                owner,
                player.serverLevel().dimension().location().toString(),
                sample,
                ShroudSavedData.get(player.serverLevel()).state());
        discoveryData.replace(observed.state());

        SYNC.update(player.getUUID(), owner, observed.visibleCores())
                .ifPresent(payload -> PacketDistributor.sendToPlayer(player, payload));
    }

    public static void forget(ServerPlayer player) {
        Objects.requireNonNull(player, "player");
        SYNC.forget(player.getUUID());
    }

    static void onCoreDestroyed(ShroudCoreDestroyedEvent event) {
        updateKnownLifecycle(event.level(), event.coreId(), CoreLifecycleState.DESTROYED);
    }

    static void onCorePurified(ShroudCorePurifiedEvent event) {
        updateKnownLifecycle(event.level(), event.coreId(), CoreLifecycleState.PURIFIED);
    }

    static void onServerStopped(ServerStoppedEvent event) {
        SYNC.clear();
    }

    private static void updateKnownLifecycle(
            net.minecraft.server.level.ServerLevel level,
            java.util.UUID coreId,
            CoreLifecycleState lifecycle) {
        ShroudDiscoverySavedData discoveryData = ShroudDiscoverySavedData.get(level);
        discoveryData.replace(discoveryData.state().updateKnownLifecycleEverywhere(coreId, lifecycle));
    }
}
