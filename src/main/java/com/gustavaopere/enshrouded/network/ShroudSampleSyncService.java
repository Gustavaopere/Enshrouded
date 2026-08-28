package com.gustavaopere.enshrouded.network;

import com.gustavaopere.enshrouded.api.shroud.ShroudQuery;
import com.gustavaopere.enshrouded.api.shroud.ShroudSample;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Objects;

/**
 * Server-only bridge from the canonical Shroud query to the minimal client presentation payload.
 */
public final class ShroudSampleSyncService {
    private final ShroudQuery query;
    private final ShroudPlayerSyncTracker tracker;

    public ShroudSampleSyncService(ShroudQuery query, ShroudPlayerSyncTracker tracker) {
        this.query = Objects.requireNonNull(query, "query");
        this.tracker = Objects.requireNonNull(tracker, "tracker");
    }

    public boolean sync(ServerPlayer player) {
        Objects.requireNonNull(player, "player");
        ShroudSample sample = query.sample(player.serverLevel(), player.blockPosition(), player);
        return tracker.update(player.getUUID(), player.serverLevel().getGameTime(), sample)
                .map(payload -> {
                    PacketDistributor.sendToPlayer(player, payload);
                    return true;
                })
                .orElse(false);
    }

    public void forget(ServerPlayer player) {
        Objects.requireNonNull(player, "player");
        tracker.forget(player.getUUID());
    }
}
